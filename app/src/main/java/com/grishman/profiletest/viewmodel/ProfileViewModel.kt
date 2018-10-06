package com.grishman.profiletest.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.grishman.profiletest.extensions.*
import com.grishman.profiletest.model.Card
import com.grishman.profiletest.model.CardsResponse
import com.grishman.profiletest.model.ProfileFullData
import com.grishman.profiletest.model.ProfileResponse
import com.grishman.profiletest.network.OpenpayService
import com.grishman.profiletest.network.Outcome
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Profile VM
 */
class ProfileViewModel(private val api: OpenpayService) : ViewModel() {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    var profileOutcome: LiveData<Outcome<ProfileFullData>> = MutableLiveData()
    val postProfileOutcome: PublishSubject<Outcome<ProfileFullData>> =
            PublishSubject.create<Outcome<ProfileFullData>>()

    var fullName: ObservableField<String> = ObservableField()
    var location: ObservableField<String> = ObservableField()
    var imageUrl: ObservableField<String> = ObservableField()
    var cardsData: MutableLiveData<List<Card>>? = MutableLiveData()
    var isRefreshing: ObservableBoolean = ObservableBoolean(false)

    init {
        profileOutcome = postProfileOutcome.toLiveData(compositeDisposable)
    }

    fun initLoading() {
        //Show loading
        postProfileOutcome.loading(true)

        Observable.zip(
                api.getProfileInfo(),
                api.getCards(),
                zipProfileAndCards()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { profileData ->
                            postProfileOutcome.success(profileData)
                            imageUrl.set(profileData.profile?.avatar?.imageUrl!!)
                            fullName.set("${profileData.profile?.firstName} ${profileData.profile?.lastName}")
                            location.set("${profileData.profile?.location?.city},  ${profileData.profile?.location?.country}")
                        },
                        { error -> handleError(error) }
                ).addTo(compositeDisposable)

//        compositeDisposable = Observable.zip(
//                api.getCards(),
//                api.getProfileInfo(),
//                io.reactivex.functions.BiFunction { cardsR: CardsResponse, profileR: ProfileResponse ->
//                    ProfileFullData().apply {
//                        profile = profileR
//                        cards = cardsR.cards
//                    }
//                }
//        ).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { result: ProfileFullData ->
//                    //fixme wtf
//                    run {
//                        cardsData?.value = result.cards
//                        imageUrl.set(result.profile?.avatar?.imageUrl!!)
//
//                        //testText.text = "${result.cards?.size} result found and /\n profile name is ${result.profile?.firstName}"
//                        fullName.set("${result.profile?.firstName} ${result.profile?.lastName}")
//                        location.set("${result.profile?.location?.city},  ${result.profile?.location?.country}")
//                    }
//
//                }
        //handleError(Throwable("something wrong"))
    }

    private fun zipProfileAndCards() =
            BiFunction<ProfileResponse, CardsResponse, ProfileFullData> { profileData, cardsResponse ->
                //saveUsersAndPosts(users, posts)
                ProfileFullData().apply {
                    profile = profileData
                    cards = cardsResponse.cards
                }
            }

    private fun handleError(error: Throwable) {
        postProfileOutcome.failed(error)
    }

    override fun onCleared() {
        super.onCleared()
        //Clear Rx stuff
        compositeDisposable.clear()
    }

}
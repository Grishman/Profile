package com.grishman.profiletest.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.grishman.profiletest.extensions.*
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
    private val postProfileOutcome: PublishSubject<Outcome<ProfileFullData>> =
            PublishSubject.create<Outcome<ProfileFullData>>()

    var fullName: ObservableField<String> = ObservableField()
    var location: ObservableField<String> = ObservableField()
    var imageUrl: ObservableField<String> = ObservableField()
    var isRefreshing: ObservableBoolean = ObservableBoolean(false)

    init {
        profileOutcome = postProfileOutcome.toLiveData(compositeDisposable)
    }

    fun initLoading() {
        //Show loading
        postProfileOutcome.loading(true)
        //Fetch data
        Observable.zip(
                api.getProfileInfo(),
                api.getCards(),
                zipProfileAndCards()
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { profileData ->
                            //Update our View.
                            postProfileOutcome.success(profileData)
                            imageUrl.set(profileData.profile?.avatar?.imageUrl!!)
                            fullName.set("${profileData.profile?.firstName} ${profileData.profile?.lastName}")
                            location.set("${profileData.profile?.location?.city},  ${profileData.profile?.location?.country}")
                        },
                        { error -> handleError(error) }
                ).addTo(compositeDisposable)
    }

    /**
     * Using .zip method to create our end data.
     */
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
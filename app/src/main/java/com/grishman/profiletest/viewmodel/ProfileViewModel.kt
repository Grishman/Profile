package com.grishman.profiletest.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.grishman.profiletest.model.CardsResponse
import com.grishman.profiletest.model.NewType
import com.grishman.profiletest.model.ProfileResponse
import com.grishman.profiletest.network.OpenpayService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Profile VM
 */
class ProfileViewModel(private val api: OpenpayService) : ViewModel() {
    private var disposable: Disposable? = null

    var fullName: ObservableField<String> = ObservableField()
    var location: ObservableField<String> = ObservableField()

    fun initLoading() {
        //fixme maybe Observables.zip()
        disposable = Observable.zip(
                api.getCards(),
                api.getProfileInfo(),
                io.reactivex.functions.BiFunction { cardsR: CardsResponse, profileR: ProfileResponse ->
                    NewType().apply {
                        profile = profileR
                        cards = cardsR.cards
                    }
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result: NewType ->
                    //fixme wtf
                    run {
                        //testText.text = "${result.cards?.size} result found and /\n profile name is ${result.profile?.firstName}"
                        fullName.set("${result.profile?.firstName} ${result.profile?.lastName}")
                        location.set(result.profile?.location?.city + result.profile?.location?.country)
                    }

                }
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}
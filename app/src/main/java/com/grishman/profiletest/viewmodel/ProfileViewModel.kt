package com.grishman.profiletest.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.widget.ImageView
import com.grishman.profiletest.R
import com.grishman.profiletest.model.Card
import com.grishman.profiletest.model.CardsResponse
import com.grishman.profiletest.model.NewType
import com.grishman.profiletest.model.ProfileResponse
import com.grishman.profiletest.network.OpenpayService
import com.squareup.picasso.Picasso
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
    var imageUrl: String = ""

    var cardsData: MutableLiveData<List<Card>>? = MutableLiveData()

    fun initLoading() {
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
                        cardsData?.value = result.cards
                        imageUrl= result.profile?.avatar?.imageUrl!!
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

    companion object {
        //    @BindingAdapter("bind:imageUrl")
        //    fun loadImage(view: ImageView, imageUrl: String) {
        //        Picasso.get()
        //                .load(imageUrl)
        //                .placeholder(R.mipmap.ic_launcher)
        //                .into(view)
        //    }
            @JvmStatic
            @BindingAdapter("bind:imageurl")
            fun loadImage(view: ImageView, imageUrl: String) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(view)
            }
    }
}
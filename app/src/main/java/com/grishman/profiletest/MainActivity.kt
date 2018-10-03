package com.grishman.profiletest

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.grishman.profiletest.model.CardsResponse
import com.grishman.profiletest.model.NewType
import com.grishman.profiletest.model.ProfileResponse
import com.grishman.profiletest.network.OpenpayService
import com.iravul.swipecardview.SwipeCardModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.support.v7.widget.LinearLayoutManager
import com.iravul.swipecardview.SwipeCardAdapter



class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private val apiService by lazy {
        OpenpayService.create()
    }

    private var swipeCardModels: List<SwipeCardModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            //test
            beginSearch("")
        }

        swipeCardModels = ArrayList()

        //dummydata
        for (i in 0..10) {
            val swipeCardModel = SwipeCardModel()
            swipeCardModel.setId("ID-$i")
            swipeCardModel.setTitle("Product-$i")
            swipeCardModel.setDescription("ProductDesc-$i")
            swipeCardModel.setPrice((i * 10).toString() + " Euro")
            swipeCardModel.setPhotoUrl("https://s-media-cache-ak0.pinimg.com/736x/a3/99/24/a39924a3fcb7266ff7360af8a6ba2e98.jpg")
            (swipeCardModels as ArrayList<SwipeCardModel>).add(swipeCardModel)
        }

        val swipeCardAdapter = SwipeCardAdapter(this, swipeCardModels, null)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = cards_recycler
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = swipeCardAdapter
    }

    private fun beginSearch(searchString: String) {
        //fixme maybe Observables.zip()
        disposable = apiService.getCards()
                .zipWith(
                        apiService.getProfileInfo(),
                        BiFunction { list: CardsResponse, info: ProfileResponse ->
                            val resulted = NewType()
                            resulted.profile = info
                            resulted.cards = list.cards
                            return@BiFunction resulted
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            //fixme wtf
                            run {
                                testText.text = "${result.cards?.size} result found and /\n profile name is ${result.profile?.firstName}"
                                profile_full_name.text = "${result.profile?.firstName} ${result.profile?.lastName}"
                                profile_location.text = result.profile?.location?.city + result.profile?.location?.country
                            }
                        },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

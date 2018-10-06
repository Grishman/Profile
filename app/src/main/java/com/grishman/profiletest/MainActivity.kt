package com.grishman.profiletest

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.grishman.profiletest.cards.CardPagerAdapter
import com.grishman.profiletest.databinding.ActivityMainBinding
import com.grishman.profiletest.model.CardsResponse
import com.grishman.profiletest.model.ProfileFullData
import com.grishman.profiletest.model.ProfileResponse
import com.grishman.profiletest.network.OpenpayService
import com.grishman.profiletest.network.Outcome
import com.grishman.profiletest.viewmodel.ProfileViewModel
import com.grishman.profiletest.viewmodel.ProfileViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG = "ProfileActivity"
    private var disposable: Disposable? = null

    private val apiService by lazy {
        OpenpayService.create()
    }

    private lateinit var cardAdapter: CardPagerAdapter

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this, ProfileViewModelFactory(apiService)).get(ProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //fixme replace with DI framework
//        val factory = ProfileViewModelFactory(apiService)
//        val profileViewModel = ViewModelProviders.of(this, factory)
//                .get(ProfileViewModel::class.java)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            //viewmodel = profileViewModel
            setLifecycleOwner(this@MainActivity)
            //content.viewmodel=profileViewModel
            content.viewmodel = viewModel
        }
        //
        initiateDataListener()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            //test
            //beginSearch("")
            //fixme move to onStarts
            viewModel.initLoading()
//            profileViewModel.cardsData?.observe(this, Observer {
//                it.let {
//                    cardAdapter.swapItems(it)
//                    it?.find { it.isDefault == true }.apply { cards_recycler.currentItem = it?.indexOf(this) ?: 0 }
//                }
//            })
        }
//        for (i in 0..10) {
//            val swipeCardModel = SwipeCardModel()
//            swipeCardModel.setId("ID-$i")
//            swipeCardModel.setTitle("Product-$i")
//            swipeCardModel.setDescription("ProductDesc-$i")
//            swipeCardModel.setPrice((i * 10).toString() + " Euro")
//            swipeCardModel.setPhotoUrl("https://s-media-cache-ak0.pinimg.com/736x/a3/99/24/a39924a3fcb7266ff7360af8a6ba2e98.jpg")
//            (swipeCardModels as ArrayList<SwipeCardModel>).add(swipeCardModel)
//        }
//
//        val swipeCardAdapter = SwipeCardAdapter(this, swipeCardModels, null)
//        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = cards_recycler
        cardAdapter = CardPagerAdapter()
//        cardAdapter.addCardItem(CardItem("https://s3-ap-southeast-2.amazonaws.com/openpay-mobile-test/white.png"))
//        cardAdapter.addCardItem(CardItem("https://s3-ap-southeast-2.amazonaws.com/openpay-mobile-test/black.png"))
//        cardAdapter.addCardItem(CardItem("https://s3-ap-southeast-2.amazonaws.com/openpay-mobile-test/white.png"))
//        cardAdapter.addCardItem(CardItem("https://s3-ap-southeast-2.amazonaws.com/openpay-mobile-test/black.png"))
//        mFragmentCardAdapter = CardFragmentPagerAdapter(supportFragmentManager,
//                dpToPixels(2, this))
//
//        mCardShadowTransformer = ShadowTransformer(mViewPager, cardAdapter)
//        mFragmentCardShadowTransformer = ShadowTransformer(mViewPager, mFragmentCardAdapter)

        recyclerView.adapter = cardAdapter
        //recyclerView.setPageTransformer(false, mCardShadowTransformer)
        recyclerView.currentItem = 1
        recyclerView.pageMargin = 45
        recyclerView.offscreenPageLimit = 3
//
//        Picasso.get()
//                .load("https://s3-ap-southeast-2.amazonaws.com/openpay-mobile-test/elon.png")
//                .placeholder(R.mipmap.ic_launcher)
//                .transform(CircleTransform())
//                .into(binding.content.profileImage)
    }

    private fun beginSearch(searchString: String) {
        //fixme maybe Observables.zip()
        disposable = apiService.getCards()
                .zipWith(
                        apiService.getProfileInfo(),
                        BiFunction { list: CardsResponse, info: ProfileResponse ->
                            ProfileFullData().apply {
                                profile = info
                                cards = list.cards
                            }
//                            val resulted = ProfileFullData()
//                            resulted.profile = info
//                            resulted.cards = list.cards
//                            return@BiFunction resulted
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            //fixme wtf
                            run {
                                //testText.text = "${result.cards?.size} result found and /\n profile name is ${result.profile?.firstName}"
                                profile_full_name.text = "${result.profile?.firstName} ${result.profile?.lastName}"
                                profile_location.text = result.profile?.location?.city + result.profile?.location?.country
                            }
                        },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )
    }

    private fun initiateDataListener() {
        //Observe the outcome and update state of the screen  accordingly
        viewModel.profileOutcome.observe(this, Observer<Outcome<ProfileFullData>> { outcome ->
            Log.d(TAG, "initiateDataListener: " + outcome.toString())
            when (outcome) {
            //loading state
                is Outcome.Progress -> viewModel.isRefreshing.set(outcome.loading)

                is Outcome.Success -> {
                    Log.d(TAG, "initiateDataListener: Successfully loaded data")
                    cardAdapter.swapItems(outcome.data.cards)
                }

                is Outcome.Failure -> {

                    if (outcome.e is IOException)
                        Toast.makeText(
                                this,
                                "Need interent",
                                Toast.LENGTH_LONG
                        ).show()
                    else
                        Toast.makeText(
                                this,
                                "Try again",
                                Toast.LENGTH_LONG
                        ).show()
                }

            }
        })
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

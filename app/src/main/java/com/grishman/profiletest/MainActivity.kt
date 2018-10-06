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
import com.grishman.profiletest.model.ProfileFullData
import com.grishman.profiletest.network.OpenpayService
import com.grishman.profiletest.network.Outcome
import com.grishman.profiletest.viewmodel.ProfileViewModel
import com.grishman.profiletest.viewmodel.ProfileViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG = "ProfileActivity"

    //API client
    private val apiService by lazy {
        OpenpayService.create()
    }

    private lateinit var cardAdapter: CardPagerAdapter

    //fixme replace with DI framework
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this, ProfileViewModelFactory(apiService)).get(ProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            setLifecycleOwner(this@MainActivity)
            content.viewmodel = viewModel
        }
        initiateDataListener()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            //fixme move to onStarts
            viewModel.initLoading()
        }
        initCardsViewPager()
    }

    private fun initCardsViewPager() {
        val cardsViewPager = cards_recycler
        cardAdapter = CardPagerAdapter()
        cardsViewPager.adapter = cardAdapter
        cardsViewPager.currentItem = 0
        cardsViewPager.pageMargin = 45
        cardsViewPager.offscreenPageLimit = 3
    }

    //Observe LiveData from ViewModel.
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
                    //todo find default card and set it via ViewPager.currentItem
                }

                is Outcome.Failure -> {

                    if (outcome.e is IOException)
                        Toast.makeText(
                                this,
                                "Need internet",
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

package com.grishman.profiletest

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.grishman.profiletest.cards.CardPagerAdapter
import com.grishman.profiletest.databinding.ActivityMainBinding
import com.grishman.profiletest.model.ProfileFullData
import com.grishman.profiletest.network.OpenpayService
import com.grishman.profiletest.network.Outcome
import com.grishman.profiletest.viewmodel.ProfileViewModel
import com.grishman.profiletest.viewmodel.ProfileViewModelFactory
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException

/**
 * Profile screen
 */
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
        //DataBinding
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            setLifecycleOwner(this@MainActivity)
            content.viewmodel = viewModel
        }
        //Observe
        initiateDataListener()
        //UI
        initCardsViewPager()
        profile_settings.setOnClickListener {
            //open empty screen
            startActivity(Intent(this, EmptyActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        if (isNetworkAvailable()) {
            viewModel.initLoading()
        } else {
            Toast.makeText(
                    this,
                    getString(R.string.error_no_internet),
                    Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Check network connection before make API calls.
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun initCardsViewPager() {
        val cardsViewPager = cards_recycler
        cardAdapter = CardPagerAdapter()
        cardsViewPager.adapter = cardAdapter
        cardsViewPager.currentItem = 0
        cardsViewPager.pageMargin = 55
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
                    //find default card and set it via ViewPager.currentItem
                    cards_recycler.currentItem = outcome.data.cards?.indexOf(
                            outcome.data.cards?.first { it.isDefault == true }
                    ) ?: 0
                    //it?.find { it.isDefault==true }.apply { cards_recycler.currentItem=it?.indexOf(this) ?:0 }}
                }

                is Outcome.Failure -> {

                    if (outcome.e is IOException)
                        Toast.makeText(
                                this,
                                getString(R.string.error_api),
                                Toast.LENGTH_LONG
                        ).show()
                    else
                        Toast.makeText(
                                this,
                                getString(R.string.error_try_again),
                                Toast.LENGTH_LONG
                        ).show()
                }

            }
        })
    }
}

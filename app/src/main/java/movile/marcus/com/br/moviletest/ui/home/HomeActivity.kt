package movile.marcus.com.br.moviletest.ui.home

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_home.*
import movile.marcus.com.br.moviletest.R
import movile.marcus.com.br.moviletest.model.Status
import movile.marcus.com.br.moviletest.model.data.TweetData
import movile.marcus.com.br.moviletest.ui.BaseActivity
import movile.marcus.com.br.moviletest.ui.BaseRecyclerAdapter

class HomeActivity : BaseActivity(), BaseRecyclerAdapter.OnItemClickListener {

    private val homeTweetListAdapter = HomeTweetListAdapter()
    private var user: String? = null

    private val homeViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
    }

    private fun init() {
        setupRecyclerView()
        initObservers()

        if (homeViewModel.tweetResult.value == null) {
            homeViewModel.getTweetByUser("globoesporte")
        }
    }

    private fun setupRecyclerView() {
        activityHomeTweetList?.layoutManager = LinearLayoutManager(
            applicationContext, LinearLayoutManager
                .VERTICAL, false
        )
        homeTweetListAdapter.listener = this
        activityHomeTweetList?.adapter = homeTweetListAdapter
    }

    private fun initObservers() {
        homeViewModel.tweetResult.observeResource(this, onSuccess = {
            completeLayout(it)
        }, onError = {
            when (it.status) {
                Status.INTERNET_ERROR -> {

                }
                else -> {
                    showDefaultErro()
                }
            }
        })

        homeViewModel.googleResult.observeResource(this, onSuccess = {
            Snackbar.make(activityHomeContainer, it.documentSentiment.score.toString(), Snackbar.LENGTH_LONG).show()
        }, onError = {
            when (it.status) {
                Status.INTERNET_ERROR -> {

                }
                else -> {
                    showDefaultErro()
                }
            }
        })
    }

    private fun completeLayout(tweeList: List<TweetData>) {
        homeTweetListAdapter.addToList(tweeList as ArrayList<TweetData>)
    }

    private fun showDefaultErro() {
        val snackbar = Snackbar.make(activityHomeContainer, "Ocorreu um erro!", Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Tentar Novamente") {
            homeViewModel.getTweetByUser("globoesporte")
        }
        snackbar.show()
    }

    override fun onItemClick(view: View, position: Int) {
        val tweetData = homeTweetListAdapter.getItem(position)
        tweetData.text?.let {
            homeViewModel.getTextAnalyzer(it)
        }
    }
}
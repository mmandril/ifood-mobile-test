package movile.marcus.com.br.moviletest.ui.home

import android.arch.lifecycle.MutableLiveData
import movile.marcus.com.br.moviletest.model.Resource
import movile.marcus.com.br.moviletest.model.Status
import movile.marcus.com.br.moviletest.model.data.SentimentResult
import movile.marcus.com.br.moviletest.model.data.TweetData
import movile.marcus.com.br.moviletest.model.repository.GoogleRepository
import movile.marcus.com.br.moviletest.model.repository.TwitterRepository
import movile.marcus.com.br.moviletest.ui.BaseViewModel
import movile.marcus.com.br.moviletest.util.ResourceLiveData
import movile.marcus.com.br.moviletest.util.toHandlerFlowable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val twitterRepository: TwitterRepository,
    private val googleRepository: GoogleRepository
) : BaseViewModel() {

    val tweetResult = ResourceLiveData<List<TweetData>>()
    val googleResult = ResourceLiveData<SentimentResult>()
    val loadingAnalyzerResult = MutableLiveData<Boolean>()

    fun getTweetByUser(user: String) {
        twitterRepository.saveLastSearch(user)
        twitterRepository
            .getTweetByUser(user)
            .doOnSubscribe { loading.postValue(true) }
            .doFinally { loading.postValue(false) }
            .toHandlerFlowable()
            .subscribeLiveData(this, tweetResult)
    }

    fun getTextAnalyzer(text: String) {
        googleRepository
            .getTextAnalyzer(text)
            .doOnSubscribe { loadingAnalyzerResult.postValue(true) }
            .doFinally { loadingAnalyzerResult.postValue(false) }
            .toHandlerFlowable()
            .subscribeLiveData(this, googleResult)
    }

    fun removeGoogleResult() {
        googleResult.postValue(Resource(null, Status.SUCCESS, null))
    }

    fun getLastSearch() {
        val lastSearch = twitterRepository.getLastSearch()
        lastSearch?.let {
            getTweetByUser(lastSearch)
        }
    }
}
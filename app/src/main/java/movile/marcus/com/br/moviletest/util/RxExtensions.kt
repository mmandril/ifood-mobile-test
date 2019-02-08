package movile.marcus.com.br.moviletest.util

import android.arch.lifecycle.MutableLiveData
import movile.marcus.com.br.moviletest.ui.BaseViewModel
import io.reactivex.Flowable
import movile.marcus.com.br.moviletest.model.data.Resource

fun <T> Flowable<T>.toHandlerFlowable() = HandlerFlowable(this)

data class HandlerFlowable<T>(val observable: Flowable<T>) {

    fun subscribeLiveData(viewModel: BaseViewModel, liveData: ResourceLiveData<T>) {
        viewModel.disposables.add(
            observable
                .doOnError { viewModel.loading.postValue(false) }
                .subscribe(
                    { liveData.postValue(Resource.success(it)) },
                    { handleError(liveData, it) })
        )
    }

    private fun handleError(liveData: MutableLiveData<Resource<T>>, it: Throwable) {
        liveData.postValue(Resource.error(it))
    }
}
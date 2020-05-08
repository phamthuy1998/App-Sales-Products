package com.thuypham.ptithcm.mytiki.base


//---add by Anna
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
abstract class BaseViewModel : ViewModel() {

    private var compositeDisposable = CompositeDisposable()

    fun launchDisposable(disposable: Disposable){
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
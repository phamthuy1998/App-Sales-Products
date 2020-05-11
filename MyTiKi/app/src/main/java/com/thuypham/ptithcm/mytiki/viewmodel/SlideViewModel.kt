package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.repository.SlideRepository

class SlideViewModel(private val repository:SlideRepository) :ViewModel(){

    /* get all slide */
    private val responseAllListSlide = MutableLiveData<ResultData<ArrayList<Slide>>>()
    val listSlide = Transformations.switchMap(responseAllListSlide) {
        it.data
    }

    val networkListSlide = Transformations.switchMap(responseAllListSlide) {
        it.networkState
    }

    fun getAllSlideOfCategory(categoryID: String){
        responseAllListSlide.value = repository.getAllSlideOfCategory(categoryID)
    }
    fun getAllSlide(){
        responseAllListSlide.value = repository.getAllSide()
    }

    /* get all slide of category*/
    fun getSlideOfCategory(categoryID: String){
        responseAllListSlide.value = repository.getAllSlideOfCategory(categoryID)
    }

    /* add slide */
    private var responseAddSlide = MutableLiveData<NetworkState>()

    fun addSlide(slide: Slide){
        responseAddSlide = repository.addSlide(slide)
    }

    /* update slide*/
    private var responseUpdateSlide = MutableLiveData<NetworkState>()

    fun updateSlide(slide: Slide){
        responseUpdateSlide = repository.updateSlide(slide)
    }

    /* Del slide*/
    private var responseDelSlide = MutableLiveData<NetworkState>()

    fun delSlide(slideID: String){
        responseDelSlide = repository.delSlide(slideID)
    }
}
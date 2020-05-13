package com.thuypham.ptithcm.mytiki.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.repository.SlideRepository

class SlideViewModel(private val repository:SlideRepository) :ViewModel(){


    var name = MutableLiveData<String>().apply { value = "" }
//    var image = MutableLiveData<String>().apply { value = "" }
    var slide = MutableLiveData<Slide>().apply { value = Slide() }
    var category = MutableLiveData<Category>().apply { value = Category() }

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
    var responseAddSlide = MutableLiveData<ResultData<Slide>>()

    val networkAddSlide = Transformations.switchMap(responseAddSlide) {
        it.networkState
    }

    val slideAdd = Transformations.switchMap(responseAddSlide) {
        it.data
    }

    fun addSlide(slide: Slide, imageUri: Uri?=null){
        responseAddSlide.value = repository.addSlide(slide,imageUri)
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
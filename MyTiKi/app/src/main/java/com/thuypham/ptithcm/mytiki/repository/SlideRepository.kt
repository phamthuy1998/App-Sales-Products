package com.thuypham.ptithcm.mytiki.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Slide

interface SlideRepository {
    fun getAllSide(): ResultData<ArrayList<Slide>>
    fun getAllSlideOfCategory(categoryID: String): ResultData<ArrayList<Slide>>
    fun addSlide(slide: Slide,imageUri: Uri?=null): ResultData<Slide>
    fun updateSlide(slide: Slide):  MutableLiveData<NetworkState>
    fun delSlide(slideID: String):  MutableLiveData<NetworkState>
}
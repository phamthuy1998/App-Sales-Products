package com.thuypham.ptithcm.mytiki.event

interface DrawableListener {
    fun onLogout()
    fun onNavigateOtherActivity(destActivity: Enum<*>)
}
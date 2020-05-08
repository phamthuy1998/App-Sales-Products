package com.thuypham.ptithcm.mytiki.data

import java.io.Serializable

data class User(
    var id: String?= null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var password: String? = null,
    var birthday: String? = null,
    var gender: String? = null,
    var daycreate: String? = null,
    var role: Int? = 1
):Serializable
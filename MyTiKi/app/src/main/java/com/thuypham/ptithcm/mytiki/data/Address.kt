package com.thuypham.ptithcm.mytiki.data

import java.io.Serializable

data class Address(
    var id: String?=null,
    var name: String?=null,
    var phone: String?=null,
    var address: String?=null,
    var default: Boolean?=null
) : Serializable
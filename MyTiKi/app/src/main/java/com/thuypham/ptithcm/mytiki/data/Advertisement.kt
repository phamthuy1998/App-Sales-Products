package com.thuypham.ptithcm.mytiki.data

import java.io.Serializable

data class Advertisement(
    var name: String?=null,
    var id: String?=null,
    var image: String?=null,
    var id_category: String?=null,
    var name_category: String?=null
) : Serializable
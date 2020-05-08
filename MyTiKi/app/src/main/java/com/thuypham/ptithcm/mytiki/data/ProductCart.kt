package com.thuypham.ptithcm.mytiki.data

import java.io.Serializable

data class ProductCart(
    var id: String? = null,
    var number: Long ?= null
) : Serializable
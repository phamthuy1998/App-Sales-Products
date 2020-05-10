package com.thuypham.ptithcm.mytiki.base

import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity

enum class ActivityType {
    ACCOUNT {
        override fun value(): Class<*> = AuthActivity::class.java
    },

    EDIT_PROFILE {
        override fun value(): Class<*> = AuthActivity::class.java
    },

    PRODUCT {
        override fun value(): Class<*> = AuthActivity::class.java
    },

    CATEGORY {
        override fun value(): Class<*> = AuthActivity::class.java
    },

    ORDER {
        override fun value(): Class<*> = AuthActivity::class.java
    },

    REVENUE {
        override fun value(): Class<*> = AuthActivity::class.java
    },

    SLIDE {
        override fun value(): Class<*> = AuthActivity::class.java
    }, ;

    abstract fun value(): Class<*>
}
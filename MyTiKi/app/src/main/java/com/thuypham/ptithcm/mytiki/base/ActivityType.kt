package com.thuypham.ptithcm.mytiki.base

import com.thuypham.ptithcm.mytiki.feature.admin.acc.AccountsActivity
import com.thuypham.ptithcm.mytiki.feature.employee.category.CategoriesActivity
import com.thuypham.ptithcm.mytiki.feature.employee.main.HomeEmployeeActivity
import com.thuypham.ptithcm.mytiki.feature.employee.order.OrdersActivity
import com.thuypham.ptithcm.mytiki.feature.employee.product.ProductsActivity
import com.thuypham.ptithcm.mytiki.feature.employee.profiile.ProfileActivity
import com.thuypham.ptithcm.mytiki.feature.employee.revenue.RevenueActivity
import com.thuypham.ptithcm.mytiki.feature.employee.slide.SlidesActivity

enum class ActivityType {
    ACCOUNT {
        override fun value(): Class<*> = AccountsActivity::class.java
    },

    EMPTY {
        override fun value(): Class<*> = HomeEmployeeActivity::class.java
    },

    HOME {
        override fun value(): Class<*> = HomeEmployeeActivity::class.java
    },

    EDIT_PROFILE {
        override fun value(): Class<*> = ProfileActivity::class.java
    },

    PRODUCT {
        override fun value(): Class<*> = ProductsActivity::class.java
    },

    CATEGORY {
        override fun value(): Class<*> = CategoriesActivity::class.java
    },

    ORDER {
        override fun value(): Class<*> = OrdersActivity::class.java
    },

    REVENUE {
        override fun value(): Class<*> = RevenueActivity::class.java
    },

    SLIDE {
        override fun value(): Class<*> = SlidesActivity::class.java
    }, ;

    abstract fun value(): Class<*>
}
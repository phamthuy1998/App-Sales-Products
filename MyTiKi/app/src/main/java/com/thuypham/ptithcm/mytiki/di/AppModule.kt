package com.thuypham.ptithcm.mytiki.di

import com.thuypham.ptithcm.mytiki.repository.*
import com.thuypham.ptithcm.mytiki.repository.impl.*
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    /*----------------- View Model -----------------*/
    /*---------------- Authentication --------------------------*/
    viewModel {
        UserViewModel(get())
    }

//    viewModel {
//        UserViewModel(get())
//    }


    /*-------------------- Repository --------------------*/
    single<AuthRepository> { AuthRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }
    single<RevenueRepository> { RevenueRepositoryImpl() }
}
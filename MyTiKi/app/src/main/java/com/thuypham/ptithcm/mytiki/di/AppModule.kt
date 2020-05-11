package com.thuypham.ptithcm.mytiki.di

import com.thuypham.ptithcm.mytiki.repository.*
import com.thuypham.ptithcm.mytiki.repository.impl.*
import com.thuypham.ptithcm.mytiki.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    /*----------------- View Model -----------------*/
    viewModel { UserViewModel(get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { SlideViewModel(get()) }
    viewModel { RevenueViewModel(get()) }
    viewModel { AccountViewModel(get()) }

    /*-------------------- Repository --------------------*/
    single<AuthRepository> { AuthRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }
    single<RevenueRepository> { RevenueRepositoryImpl() }
    single<SlideRepository> { SlideRepositoryImpl() }
    single<AccountRepository> { AccountRepositoryImpl() }
}
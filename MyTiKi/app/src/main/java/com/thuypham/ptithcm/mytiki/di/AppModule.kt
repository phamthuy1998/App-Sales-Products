package com.thuypham.ptithcm.mytiki.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.thuypham.ptithcm.mytiki.repository.*
import com.thuypham.ptithcm.mytiki.repository.impl.*
import com.thuypham.ptithcm.mytiki.services.NotificationApi
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }
    single<RevenueRepository> { RevenueRepositoryImpl() }
    single<SlideRepository> { SlideRepositoryImpl() }
    single<AccountRepository> { AccountRepositoryImpl() }

    single {
        retrofit()
    }
//
//    single {
//        get<Retrofit>().create(NotificationApi::class.java)   // 5
//
//    }
}
private fun retrofit(): NotificationApi {
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.BASIC

    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    return Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .client(client)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NotificationApi::class.java)
}

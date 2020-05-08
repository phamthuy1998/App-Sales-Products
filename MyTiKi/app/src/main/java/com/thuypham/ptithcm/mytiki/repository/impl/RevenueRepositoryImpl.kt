package com.thuypham.ptithcm.mytiki.repository.impl

import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.repository.RevenueRepository

class RevenueRepositoryImpl : RevenueRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun databaseRef() = firebaseDatabase?.reference

}
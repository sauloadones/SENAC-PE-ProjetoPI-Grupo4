package com.example.appsenkaspi

import android.app.Application
import androidx.room.Room
import com.example.appsenkaspi.data.local.database.AppDatabase

class App : Application() {

    // Expondo publicamente o database
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "appsenkaspi-db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}

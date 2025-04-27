package com.example.appsenkaspi

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PilarEntity::class],
    version = 1,
    exportSchema = false // (opcional para simplificar sem warnings no build)
)
@TypeConverters(Converters::class) // Habilita salvar Date direto no banco
abstract class AppDatabase : RoomDatabase() {

    abstract fun pilarDao(): PilarDao
}

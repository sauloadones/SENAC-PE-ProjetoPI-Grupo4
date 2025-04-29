package com.example.appsenkaspi

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        PilarEntity::class,
        SubpilarEntity::class,
        AcaoEntity::class,
        AtividadeEntity::class,
        FuncionarioEntity::class,

        AcaoFuncionarioEntity::class,
        AtividadeFuncionarioEntity::class,
    ],
    version = 2, // ou 3 se já atualizou
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pilarDao(): PilarDao
    abstract fun subpilarDao(): SubpilarDao
    abstract fun acaoDao(): AcaoDao
    abstract fun atividadeDao(): AtividadeDao
    abstract fun funcionarioDao(): FuncionarioDao



    abstract fun acaoFuncionarioDao(): AcaoFuncionarioDao    // 🔵 Adicionado
    abstract fun atividadeFuncionarioDao(): AtividadeFuncionarioDao  // 🔵 Adicionado

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()  // Atualiza sem crash se mudar estrutura
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

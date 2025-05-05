package com.example.appsenkaspi

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PilarEntity::class,
        SubpilarEntity::class,
        AcaoEntity::class,
        AtividadeEntity::class,
        FuncionarioEntity::class,
        ChecklistItemEntity:: class,
        AcaoFuncionarioEntity::class,
        AtividadeFuncionarioEntity::class,
        RequisicaoEntity::class,
        NotificacaoEntity::class
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
    abstract fun checklistDao(): ChecklistDao
    abstract fun requisicaoDao(): RequisicaoDao
    abstract fun notificacaoDao(): NotificacaoDao


    abstract fun acaoFuncionarioDao(): AcaoFuncionarioDao    // 🔵 Adicionado
    abstract fun atividadeFuncionarioDao(): AtividadeFuncionarioDao  // 🔵 Adicionado

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appsenkaspi.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Executado apenas na criação do banco
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).funcionarioDao()
                                dao.inserirTodos(listOf(
                                    FuncionarioEntity(
                                        nomeCompleto = "Ana Beatriz Souza",
                                        email = "ana.souza@example.com",
                                        cargo = Cargo.COORDENADOR,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=1",
                                        nomeUsuario = "ana.souza",
                                        senha = "senha123",
                                        idAcesso = 1
                                    ),
                                    FuncionarioEntity(
                                        nomeCompleto = "Fernanda Oliveira",
                                        email = "fernanda.oliveira@example.com",
                                        cargo = Cargo.APOIO,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=3",
                                        nomeUsuario = "fernanda.oliveira",
                                        senha = "senha123",
                                        idAcesso = 3
                                    ),
                                    FuncionarioEntity(
                                        nomeCompleto = "Carlos Eduardo Silva",
                                        email = "carlos.silva@example.com",
                                        cargo = Cargo.GESTOR,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=2",
                                        nomeUsuario = "carlos.silva",
                                        senha = "senha123",
                                        idAcesso = 2
                                    ),
                                    // adicione quantos quiser…
                                )
                                    // outros funcionários...
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}

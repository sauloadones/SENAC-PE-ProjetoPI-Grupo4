package com.example.appsenkaspi.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appsenkaspi.data.local.dao.AtividadeDao
import com.example.appsenkaspi.data.local.dao.AtividadeFuncionarioDao
import com.example.appsenkaspi.data.local.entity.AtividadeFuncionarioEntity
import com.example.appsenkaspi.data.local.enums.Cargo
import com.example.appsenkaspi.data.local.dao.ChecklistDao
import com.example.appsenkaspi.data.local.entity.ChecklistItemEntity
import com.example.appsenkaspi.data.local.converter.Converters
import com.example.appsenkaspi.data.local.dao.FuncionarioDao
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity
import com.example.appsenkaspi.data.local.dao.PilarDao
import com.example.appsenkaspi.data.local.entity.PilarEntity
import com.example.appsenkaspi.data.local.dao.RequisicaoDao
import com.example.appsenkaspi.data.local.entity.RequisicaoEntity
import com.example.appsenkaspi.data.local.dao.SubpilarDao
import com.example.appsenkaspi.data.local.entity.SubpilarEntity
import com.example.appsenkaspi.data.local.dao.AcaoDao
import com.example.appsenkaspi.data.local.dao.AcaoFuncionarioDao
import com.example.appsenkaspi.data.local.entity.AcaoEntity
import com.example.appsenkaspi.data.local.entity.AcaoFuncionarioEntity
import com.example.appsenkaspi.data.local.entity.AtividadeEntity
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
        ChecklistItemEntity::class,
        AcaoFuncionarioEntity::class,
        AtividadeFuncionarioEntity::class,
        RequisicaoEntity::class,

    ],
    version = 3, // ou 3 se já atualizou
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
                    .addCallback(object : Callback() {
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
                                        idAcesso = 1,
                                        numeroTel = "(84)91191-9291",
                                        fotoBanner = ""

                                    ),
                                    FuncionarioEntity(
                                        nomeCompleto = "Usuario Teste",
                                        email = "usuario.teste@example.com",
                                        cargo = Cargo.COORDENADOR,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=1",
                                        nomeUsuario = "teste",
                                        senha = "senha123",
                                        idAcesso = 5,
                                        numeroTel = "(84)91191-9291",
                                        fotoBanner = ""

                                    ),
                                    FuncionarioEntity(
                                        nomeCompleto = "Fernanda Oliveira",
                                        email = "fernanda.oliveira@example.com",
                                        cargo = Cargo.APOIO,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=3",
                                        nomeUsuario = "fernanda.oliveira",
                                        senha = "senha123",
                                        idAcesso = 3,
                                        numeroTel = "(84)91191-9291",
                                        fotoBanner = ""
                                    ),
                                    FuncionarioEntity(
                                        nomeCompleto = "Carlos Eduardo Silva",
                                        email = "carlos.silva@example.com",
                                        cargo = Cargo.GESTOR,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=2",
                                        nomeUsuario = "carlos.silva",
                                        senha = "senha123",
                                        idAcesso = 2,
                                        numeroTel = "(84)91191-9291",
                                        fotoBanner = ""
                                    ),
                                    FuncionarioEntity(
                                        nomeCompleto = "Eu mesmo",
                                        email = "eumesmo.oliveira@example.com",
                                        cargo = Cargo.APOIO,
                                        fotoPerfil = "https://i.pravatar.cc/150?img=3",
                                        nomeUsuario = "fernanda.oliveira",
                                        senha = "senha123",
                                        idAcesso = 4,
                                        numeroTel = "(84)91191-9291",
                                        fotoBanner = ""
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

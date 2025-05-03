package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appsenkaspi.data.AcaoComStatus

@Dao
interface AtividadeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAtividade(atividade: AtividadeEntity)

    @Update
    suspend fun atualizarAtividade(atividade: AtividadeEntity)

    @Delete
    suspend fun deletarAtividade(atividade: AtividadeEntity)

    @Transaction
    @Query("""
        SELECT * FROM atividades 
        WHERE acaoId = :acaoId
    """)
    fun listarAtividadesComFuncionariosPorAcao(acaoId: Int): LiveData<List<AtividadeComFuncionarios>>

    @Query("DELETE FROM atividades WHERE id = :id")
    suspend fun deletarPorId(id: Int)


    @Insert
    suspend fun inserirComRetorno(atividade: AtividadeEntity): Long

    @Query("SELECT * FROM atividades WHERE id = :id")
    suspend fun buscarAtividadePorId(id: Int): AtividadeEntity?

    @Transaction
    @Query("SELECT * FROM atividades WHERE id = :atividadeId")
    fun buscarAtividadeComFuncionarios(atividadeId: Int): LiveData<AtividadeComFuncionarios>


    @Query("SELECT * FROM atividades WHERE acaoId = :acaoId")
    fun listarAtividadesPorAcao(acaoId: Int): LiveData<List<AtividadeEntity>>

    @Transaction
    @Query("SELECT * FROM atividades WHERE id = :id")
    fun getAtividadeComFuncionariosPorId(id: Int): LiveData<AtividadeComFuncionarios>


}


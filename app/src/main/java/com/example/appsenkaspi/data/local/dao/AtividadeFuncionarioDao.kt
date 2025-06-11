package com.example.appsenkaspi.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appsenkaspi.data.local.entity.AtividadeFuncionarioEntity
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity

@Dao
interface AtividadeFuncionarioDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun inserirAtividadeFuncionario(atividadeFuncionario: AtividadeFuncionarioEntity)













  @Delete
    suspend fun deletarAtividadeFuncionario(atividadeFuncionario: AtividadeFuncionarioEntity)

    @Query("SELECT funcionarioId FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    fun listarFuncionariosPorAtividade(atividadeId: Int): LiveData<List<Int>>

    @Query("SELECT atividadeId FROM atividades_funcionarios WHERE funcionarioId = :funcionarioId")
    fun listarAtividadesPorFuncionario(funcionarioId: Int): LiveData<List<Int>>

    @Query("DELETE FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    suspend fun deletarPorAtividade(atividadeId: Int)

    @Query("""
        SELECT f.* FROM funcionarios f
        INNER JOIN atividades_funcionarios af ON af.funcionarioId = f.id
        WHERE af.atividadeId = :atividadeId
    """)
    suspend fun getResponsaveisByAtividadeId(atividadeId: Int): List<FuncionarioEntity>

  @Query("""
    SELECT f.* FROM funcionarios f
    INNER JOIN atividades_funcionarios af ON af.funcionarioId = f.id
    WHERE af.atividadeId = :atividadeId
""")
  suspend fun getResponsaveisDaAtividade(atividadeId: Int): List<FuncionarioEntity>



}

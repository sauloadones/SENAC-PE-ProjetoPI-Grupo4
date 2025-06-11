package com.example.appsenkaspi.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appsenkaspi.data.local.entity.AcaoFuncionarioEntity
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity

@Dao
interface AcaoFuncionarioDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun inserirAcaoFuncionario(acaoFuncionario: AcaoFuncionarioEntity)

    @Delete
    suspend fun deletarAcaoFuncionario(acaoFuncionario: AcaoFuncionarioEntity)

    @Query("SELECT funcionarioId FROM acoes_funcionarios WHERE acaoId = :acaoId")
    fun listarFuncionariosPorAcao(acaoId: Int): LiveData<List<Int>>

    @Query("SELECT acaoId FROM acoes_funcionarios WHERE funcionarioId = :funcionarioId")
    fun listarAcoesPorFuncionario(funcionarioId: Int): LiveData<List<Int>>


        @Query("""
        SELECT f.* FROM funcionarios f
        INNER JOIN acoes_funcionarios af ON af.funcionarioId = f.id
        WHERE af.acaoId = :acaoId
    """)
        suspend fun getResponsaveisByAcaoId(acaoId: Int): List<FuncionarioEntity>

  @Query("DELETE FROM acoes_funcionarios WHERE acaoId = :acaoId")
  suspend fun deletarResponsaveisPorAcao(acaoId: Int)



}

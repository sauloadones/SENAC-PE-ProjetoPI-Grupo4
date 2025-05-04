package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appsenkaspi.Converters.StatusRequisicao
import com.example.appsenkaspi.Converters.TipoRequisicao

@Dao
interface RequisicaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(requisicao: RequisicaoEntity): Long

    @Update
    suspend fun atualizar(requisicao: RequisicaoEntity)

    @Delete
    suspend fun deletar(requisicao: RequisicaoEntity)

    @Query("SELECT * FROM requisicoes ORDER BY dataCriacao DESC")
    fun listarTodas(): LiveData<List<RequisicaoEntity>>

    @Query("SELECT * FROM requisicoes WHERE status = :status")
    fun listarPorStatus(status: StatusRequisicao): LiveData<List<RequisicaoEntity>>

    @Query("SELECT * FROM requisicoes WHERE tipo = :tipo")
    fun listarPorTipo(tipo: TipoRequisicao): LiveData<List<RequisicaoEntity>>

    @Query("SELECT * FROM requisicoes WHERE idSolicitante = :funcionarioId")
    fun listarPorSolicitante(funcionarioId: Int): LiveData<List<RequisicaoEntity>>
}

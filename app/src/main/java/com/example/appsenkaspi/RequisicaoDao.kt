package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*


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

    @Query("SELECT * FROM requisicoes WHERE solicitanteId = :funcionarioId")
    fun listarPorSolicitante(funcionarioId: Int): LiveData<List<RequisicaoEntity>>



    @Query("UPDATE requisicoes SET status = :status WHERE id = :id")
    fun atualizarStatus(id: Int, status: StatusRequisicao)



    @Query("SELECT * FROM requisicoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): RequisicaoEntity?
}

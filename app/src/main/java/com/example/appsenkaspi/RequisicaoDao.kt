package com.example.appsenkaspi


import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RequisicaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(requisicao: RequisicaoEntity)

    @Update
    suspend fun update(requisicao: RequisicaoEntity)

    @Query("SELECT * FROM requisicoes WHERE status = :status")
    fun getRequisicoesPendentes(status: StatusRequisicao = StatusRequisicao.PENDENTE): LiveData<List<RequisicaoEntity>>


    @Query("SELECT * FROM requisicoes WHERE id = :id")
    suspend fun getRequisicaoById(id: Int): RequisicaoEntity?

    @Query("SELECT * FROM requisicoes WHERE solicitanteId = :userId ORDER BY dataSolicitacao DESC")
    fun getRequisicoesDoUsuario(userId: Int): LiveData<List<RequisicaoEntity>>

    @Query("SELECT * FROM requisicoes WHERE solicitanteId = :usuarioId ORDER BY dataSolicitacao DESC")
    fun getNotificacoesDoApoio(usuarioId: Int): LiveData<List<RequisicaoEntity>>

}

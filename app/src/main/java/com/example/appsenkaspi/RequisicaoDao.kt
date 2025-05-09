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

  @Query("""
    SELECT * FROM requisicoes
    WHERE atividadeId = :atividadeId
    AND solicitanteId = :solicitanteId
    AND tipo = :tipo
    AND status = 'PENDENTE'
    LIMIT 1
""")
  suspend fun getRequisicaoPorAtividade(
    atividadeId: Int,
    solicitanteId: Int,
    tipo: TipoRequisicao
  ): RequisicaoEntity?



    @Insert
    suspend fun insert(requisicao: RequisicaoEntity)

  @Query("""
    SELECT * FROM requisicoes
    WHERE solicitanteId = :usuarioId AND foiVista = 0
""")
  fun getNotificacoesNaoVistas(usuarioId: Int): LiveData<List<RequisicaoEntity>>

  @Query("SELECT COUNT(*) FROM requisicoes WHERE solicitanteId = :usuarioId AND foiVista = 0")
  fun getQuantidadeNaoVistas(usuarioId: Int): LiveData<Int>

  @Query("UPDATE requisicoes SET foiVista = 1 WHERE solicitanteId = :usuarioId AND foiVista = 0")
  suspend fun marcarComoVista(usuarioId: Int)

  @Query("SELECT EXISTS(SELECT 1 FROM requisicoes WHERE atividadeId = :atividadeId AND tipo = :tipo AND status = 'PENDENTE')")
  suspend fun existeRequisicaoPendenteParaAtividade(atividadeId: Int, tipo: TipoRequisicao): Boolean

  // Outras funções...




}

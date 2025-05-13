package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RequisicaoDao {

  // ✅ Inserção única com substituição em conflito
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun inserir(requisicao: RequisicaoEntity)



  @Update
  suspend fun update(requisicao: RequisicaoEntity)

  // ✅ Requisições pendentes para coordenador (sem incluir notificações informativas)
  @Query("""
    SELECT * FROM requisicoes
    WHERE status = :status
    AND tipo != 'atividade_para_vencer' -- ← Essa linha já existe, confirme se está ativa
""")
  fun getRequisicoesPendentes(status: StatusRequisicao = StatusRequisicao.PENDENTE): LiveData<List<RequisicaoEntity>>


  @Query("SELECT * FROM requisicoes WHERE id = :id")
  suspend fun getRequisicaoById(id: Int): RequisicaoEntity?

  @Query("""
        SELECT * FROM requisicoes
        WHERE solicitanteId = :userId
        ORDER BY dataSolicitacao DESC
    """)
  fun getRequisicoesDoUsuario(userId: Int): LiveData<List<RequisicaoEntity>>

  @Query("""
        SELECT * FROM requisicoes
        WHERE solicitanteId = :usuarioId
        ORDER BY dataSolicitacao DESC
    """)
  fun getNotificacoesDoApoio(usuarioId: Int): LiveData<List<RequisicaoEntity>>

  // ✅ Verifica se existe uma requisição pendente para um usuário específico
  @Query("""
        SELECT * FROM requisicoes
        WHERE atividadeId = :atividadeId
        AND solicitanteId = :solicitanteId
        AND tipo = :tipo
        AND status = 'pendente'
        LIMIT 1
    """)
  suspend fun getRequisicaoPorAtividade(
    atividadeId: Int,
    solicitanteId: Int,
    tipo: TipoRequisicao
  ): RequisicaoEntity?

  // ✅ Contagem para badge (apenas notificações de ação, se quiser incluir tudo, remova o filtro)
  @Query("""
    SELECT COUNT(*) FROM requisicoes
    WHERE solicitanteId = :userId
    AND foiVista = 0
""")
  fun getQuantidadeNaoVistas(userId: Int): LiveData<Int>



  // ✅ Marca todas como vistas
  @Query("""
        UPDATE requisicoes
        SET foiVista = 1
        WHERE solicitanteId = :usuarioId
        AND foiVista = 0
    """)
  suspend fun marcarComoVista(usuarioId: Int)

  // ✅ Lista não vistas completas
  @Query("""
        SELECT * FROM requisicoes
        WHERE solicitanteId = :usuarioId
        AND foiVista = 0
    """)
  fun getNotificacoesNaoVistas(usuarioId: Int): LiveData<List<RequisicaoEntity>>

  // ✅ Checa existência específica de notificação (por pessoa e tipo)
  @Query("""
        SELECT EXISTS(
            SELECT 1 FROM requisicoes
            WHERE atividadeId = :atividadeId
            AND solicitanteId = :solicitanteId
            AND tipo = :tipo
            AND status = 'pendente'
        )
    """)
  suspend fun existeRequisicaoPendenteParaAtividade(
    atividadeId: Int,
    solicitanteId: Int,
    tipo: TipoRequisicao
  ): Boolean


  @Query("""
    SELECT EXISTS(
        SELECT 1 FROM requisicoes
        WHERE atividadeId = :atividadeId
        AND solicitanteId = :solicitanteId
        AND tipo = :tipo
        AND date(dataSolicitacao / 1000, 'unixepoch') = date('now')
    )
""")
  suspend fun existeRequisicaoHojeParaAtividade(
    atividadeId: Int,
    solicitanteId: Int,
    tipo: TipoRequisicao
  ): Boolean

  @Query("""
    SELECT COUNT(*) FROM requisicoes
    WHERE status = 'pendente'
    AND tipo IN ('criar_atividade', 'editar_atividade', 'criar_acao', 'editar_acao', 'completar_atividade')
""")
  fun getQuantidadePendentesParaCoordenador(): LiveData<Int>





}


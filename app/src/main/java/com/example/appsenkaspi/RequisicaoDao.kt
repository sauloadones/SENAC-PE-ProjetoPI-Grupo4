package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RequisicaoDao {

  // ✅ Inserção única com substituição em conflito
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun inserir(requisicao: RequisicaoEntity): Long



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
        AND date(datetime(dataSolicitacao / 1000, 'unixepoch')) = date('now')
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

  @Query("""
    SELECT COUNT(*) FROM requisicoes
    WHERE solicitanteId = :userId
    AND foiVista = 0
    AND tipo IN ('atividade_para_vencer', 'atividade_vencida', 'prazo_alterado', 'atividade_concluida', 'responsavel_adicionado', 'responsavel_removido')
""")
  fun getQuantidadeNotificacoesPrazoNaoVistas(userId: Int): LiveData<Int>

  @Query("""
    UPDATE requisicoes
    SET foiVista = 1
    WHERE solicitanteId = :usuarioId
    AND foiVista = 0
    AND tipo in ('atividade_para_vencer', 'atividade_vencida', 'prazo_alterado', 'atividade_concluida', 'responsavel_adicionado', 'responsavel_removido')
""")
  suspend fun marcarNotificacoesDePrazoComoVistas(usuarioId: Int)

  @Query("""
  SELECT EXISTS (
    SELECT 1 FROM requisicoes
    WHERE atividadeId = :atividadeId
    AND solicitanteId = :solicitanteId
    AND tipo = 'atividade_vencida'
  )
""")
  suspend fun existeRequisicaoDeVencida(
    atividadeId: Int,
    solicitanteId: Int
  ): Boolean

  @Query("SELECT * FROM requisicoes WHERE atividadeId = :atividadeId AND (tipo = 'atividade_para_vencer' OR tipo = 'atividade_vencidade')")
  suspend fun getRequisicoesDePrazoPorAtividade(atividadeId: Int): List<RequisicaoEntity>
  @Query("""
      UPDATE requisicoes
      SET foiVista = 1
      WHERE id = :requisicaoId
    """)
  suspend fun marcarComoOculta(requisicaoId: Int)

  @Query("UPDATE requisicoes SET resolvida = 1 WHERE id = :requisicaoId")
  suspend fun marcarComoResolvida(requisicaoId: Int)

  @Query("DELETE FROM requisicoes WHERE atividadeId = :atividadeId AND tipo = :tipo")
  suspend fun deletarRequisicoesDePrazoPorAtividade(
    atividadeId: Int,
    tipo: TipoRequisicao = TipoRequisicao.ATIVIDADE_PARA_VENCER
  )
  @Query("DELETE FROM requisicoes WHERE atividadeId = :atividadeId AND tipo = :tipo")
  suspend fun deletarRequisicoesDeTipoPorAtividade(
    atividadeId: Int,
    tipo: TipoRequisicao
  )
  @Query("SELECT * FROM requisicoes WHERE solicitanteId = :id ORDER BY dataSolicitacao DESC")
  fun getNotificacoesDoFuncionario(id: Int): LiveData<List<RequisicaoEntity>>

  @Query("""
  SELECT COUNT(*) > 0 FROM requisicoes
  WHERE atividadeId = :atividadeId
    AND solicitanteId = :responsavelId
    AND tipo = :tipo
    AND mensagemResposta LIKE '%' || :diasRestantes || '%'
""")
  suspend fun existeNotificacaoComDiasRestantes(
    atividadeId: Int,
    responsavelId: Int,
    tipo: TipoRequisicao,
    diasRestantes: Int
  ): Boolean

  @Query("""
  SELECT EXISTS(
    SELECT 1 FROM requisicoes
    WHERE atividadeId = :atividadeId
    AND solicitanteId = :responsavelId
    AND tipo = :tipo
    AND mensagemResposta = :mensagem
  )
""")
  suspend fun existeMensagemExata(
    atividadeId: Int,
    responsavelId: Int,
    tipo: TipoRequisicao,
    mensagem: String
  ): Boolean



  @Query("SELECT * FROM requisicoes ORDER BY dataSolicitacao DESC")
  fun getTodasNotificacoes(): LiveData<List<RequisicaoEntity>>

  @Query("""
  DELETE FROM requisicoes
  WHERE atividadeId = :atividadeId AND tipo = :tipo AND solicitanteId = :responsavelId
""")
  suspend fun deletarRequisicoesDeTipoPorAtividadeEFuncionario(
    atividadeId: Int,
    tipo: TipoRequisicao,
    responsavelId: Int
  )

  @Query("UPDATE requisicoes SET excluida = 1 WHERE id IN (:ids)")
  suspend fun marcarComoExcluidas(ids: List<Int>)


  @Query("SELECT * FROM requisicoes WHERE excluida = 0")
  fun getNaoExcluidas(): LiveData<List<RequisicaoEntity>>


}


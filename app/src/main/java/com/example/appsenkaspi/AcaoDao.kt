package com.example.appsenkaspi


import androidx.room.*
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@Dao
interface AcaoDao {

    /**
     * Insere uma Ação no banco e retorna o ID gerado.
     * Usa REPLACE para conflitos.
     */
    @Insert
    suspend fun inserirAcao(acao: AcaoEntity)

    @Transaction
    @Query("SELECT * FROM acoes WHERE id = :acaoId")
    suspend fun buscarComFuncionarios(acaoId: Int): AcaoComFuncionarios


    /**
     * Atualiza uma Ação existente.
     */
    @Update
    suspend fun atualizarAcao(acao: AcaoEntity)

    /**
     * Remove uma Ação.
     */
    @Delete
    suspend fun deletarAcao(acao: AcaoEntity)

    /**
     * Lista todas as Ações pertencentes a um Pilar.
     */
    @Query("SELECT * FROM acoes WHERE pilarId = :pilarId")
    fun listarAcoesPorPilar(pilarId: Int): LiveData<List<AcaoEntity>>

    @Query("SELECT * FROM acoes WHERE id = :id")
    suspend fun getAcaoByIdNow(id: Int): AcaoEntity?

    /**
     * Busca uma Ação pelo ID.
     */
    @Query("SELECT * FROM acoes WHERE id = :id")
    suspend fun buscarAcaoPorId(id: Int): AcaoEntity?

    @Query("SELECT * FROM acoes WHERE id = :id")
    fun getAcaoById(id: Int): LiveData<AcaoEntity?>

    /**
     * Retorna a Ação junto com os Funcionários relacionados.
     */
    @Transaction

    @Insert
    suspend fun inserirComRetorno(acao: AcaoEntity): Long

    @Query("""
    SELECT
      a.id,
      a.nome,
      a.descricao,
      a.dataInicio,
      a.dataPrazo,
      a.pilarId,
      a.status,
      a.criado_por,
      a.data_criacao  ,
      COUNT(at.id)    AS totalAtividades,
      SUM(
        CASE WHEN at.status = 'concluida' THEN 1
             ELSE 0
        END
      ) AS ativasConcluidas
    FROM acoes AS a
    LEFT JOIN atividades AS at
      ON at.acaoId = a.id
    WHERE a.pilarId = :pilarId
    GROUP BY a.id
""")
    fun listarPorPilar(pilarId: Int): LiveData<List<AcaoComStatus>>
  @Query("""
    SELECT
        a.* ,
      COUNT(at.id)    AS totalAtividades,
      SUM(
        CASE WHEN at.status = 'concluida' THEN 1
             ELSE 0
        END
      ) AS ativasConcluidas
    FROM acoes AS a
    LEFT JOIN atividades AS at
      ON at.acaoId = a.id
    WHERE a.subpilarId = :subpilarId
    GROUP BY a.id
""")
  fun listarPorSubpilar(subpilarId: Int): LiveData<List<AcaoComStatus>>


    @Query("SELECT * FROM acoes WHERE id = :acaoId LIMIT 1")
    suspend fun getAcaoPorIdDireto(acaoId: Int): AcaoEntity?

  @Query("""
    SELECT
        a.id AS acaoId,
        a.nome AS nome,
        COALESCE(SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) * 1.0 / COUNT(at.id), 0) AS progresso,
        COUNT(at.id) AS totalAtividades
    FROM acoes a
    LEFT JOIN atividades at ON at.acaoId = a.id
    WHERE a.pilarId = :pilarId
    GROUP BY a.id
""")
  suspend fun listarProgressoPorPilar(pilarId: Int): List<ProgressoAcao>


  @Query("SELECT * FROM acoes WHERE id = :id")
    fun buscarPorId(id: Int): AcaoEntity?

    @Query("SELECT * FROM acoes WHERE id = :id")
    suspend fun getById(id: Int): AcaoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(acao: AcaoEntity)

    @Update
    suspend fun update(acao: AcaoEntity)

    @Delete
    suspend fun delete(acao: AcaoEntity)

    @Query("SELECT * FROM acoes WHERE id = :id")
    suspend fun getAcaoPorId(id: Int): AcaoEntity?

    @Query("SELECT * FROM acoes")
    fun listarTodas(): LiveData<List<AcaoEntity>>


  @Query("""
    SELECT
        a.id AS acaoId,
        a.nome AS nome,  -- ✅ Essa linha é obrigatória!
        COUNT(at.id) AS totalAtividades,
        COALESCE(SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) * 1.0 / COUNT(at.id), 0) AS progresso
    FROM acoes a
    LEFT JOIN atividades at ON at.acaoId = a.id
    WHERE a.subpilarId = :subpilarId
    GROUP BY a.id
""")
  suspend fun listarProgressoPorSubpilar(subpilarId: Int): List<ProgressoAcao>


  @Query("""
  SELECT
    subpilarId AS subpilarId,
    COUNT(id) AS totalAcoes,
    CASE
      WHEN COUNT(id) = 0 THEN 0.0
      ELSE CAST(SUM(
        CASE WHEN status = 'concluida' THEN 1 ELSE 0 END
      ) AS FLOAT) / COUNT(id)
    END AS progresso
  FROM acoes
  WHERE pilarId = :pilarId AND subpilarId IS NOT NULL
  GROUP BY subpilarId
""")
  suspend fun listarProgressoPorSubpilaresDoPilar(pilarId: Int): List<ProgressoSubpilarDTO>


  @Query("SELECT * FROM acoes WHERE subpilarId = :subpilarId")
  fun getAcoesPorSubpilar(subpilarId: Int): LiveData<List<AcaoEntity>>

  @Insert
  suspend fun inserirRetornandoId(acao: AcaoEntity): Long



    @Query("SELECT * FROM acoes WHERE pilarId = :pilarId")
    suspend fun getAcoesPorPilarDireto(pilarId: Int): List<AcaoEntity>

  @Query("""
  SELECT
    a.*,
    COUNT(at.id) AS totalAtividades,
    SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) AS ativasConcluidas
  FROM acoes a
  LEFT JOIN atividades at ON at.acaoId = a.id
  WHERE a.pilarId = :pilarId
  GROUP BY a.id
""")
  fun listarAcoesComStatusPorPilar(pilarId: Int): LiveData<List<AcaoComStatus>>


  @Query("""
  SELECT
    a.*,
    COUNT(at.id) AS totalAtividades,
    SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) AS ativasConcluidas
  FROM acoes a
  LEFT JOIN atividades at ON at.acaoId = a.id
  GROUP BY a.id
""")
  fun listarTodasAcoesComStatus(): LiveData<List<AcaoComStatus>>

  @Query("""
  SELECT
    a.*,
    COUNT(at.id) AS totalAtividades,
    SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) AS ativasConcluidas
  FROM acoes a
  LEFT JOIN atividades at ON at.acaoId = a.id
  WHERE a.pilarId = :pilarId
  GROUP BY a.id
""")
  suspend fun listarAcoesComStatusPorPilarNow(pilarId: Int): List<AcaoComStatus>

  @Query("""
  SELECT
    a.*,
    COUNT(at.id) AS totalAtividades,
    SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) AS ativasConcluidas
  FROM acoes a
  LEFT JOIN atividades at ON at.acaoId = a.id
  GROUP BY a.id
""")
  suspend fun listarTodasAcoesComStatusNow(): List<AcaoComStatus>

  @Query("""
    SELECT
        a.*,
        COUNT(at.id) AS totalAtividades,
        SUM(CASE WHEN at.status = 'concluida' THEN 1 ELSE 0 END) AS ativasConcluidas
    FROM acoes a
    LEFT JOIN atividades at ON at.acaoId = a.id
    WHERE a.pilarId IN (:pilarIds)
    GROUP BY a.id
""")
  suspend fun listarAcoesComStatusPorPilares(pilarIds: List<Int>): List<AcaoComStatus>

  @Query("SELECT * FROM acoes WHERE subpilarId = :subpilarId")
  suspend fun listarPorSubpilares(subpilarId: Int): List<AcaoEntity>




}









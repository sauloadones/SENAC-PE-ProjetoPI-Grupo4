package com.example.appsenkaspi

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.appsenkaspi.data.AcaoComStatus

@Dao
interface AcaoDao {

    /**
     * Insere uma Ação no banco e retorna o ID gerado.
     * Usa REPLACE para conflitos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAcao(acao: AcaoEntity): Long

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

    /**
     * Busca uma Ação pelo ID.
     */
    @Query("SELECT * FROM acoes WHERE id = :id")
    suspend fun buscarAcaoPorId(id: Int): AcaoEntity?

    /**
     * Retorna a Ação junto com os Funcionários relacionados.
     */
    @Transaction
    @Query("SELECT * FROM acoes WHERE id = :acaoId")
    fun buscarAcaoComFuncionarios(acaoId: Int): LiveData<AcaoComFuncionarios>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRetornandoId(acao: AcaoEntity): Long


    @Query("""
  SELECT
    a.*,
    (SELECT COUNT(*) FROM atividades WHERE acaoId = a.id) AS totalAtividades,
    (SELECT COUNT(*) FROM atividades WHERE acaoId = a.id AND concluida = 1) AS ativasConcluidas
  FROM acoes AS a
  WHERE a.pilarId = :pilarId
""")
    fun listarPorPilar(pilarId: Int): LiveData<List<AcaoComStatus>>




}

  package com.example.appsenkaspi

  import androidx.lifecycle.LiveData
  import androidx.room.*
  import com.example.appsenkaspi.data.AcaoComStatus

  @Dao
  interface AtividadeDao {

      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun inserirAtividade(atividade: AtividadeEntity)

      @Update
      suspend fun atualizarAtividade(atividade: AtividadeEntity)

      @Delete
      suspend fun deletarAtividade(atividade: AtividadeEntity)

      @Transaction
      @Query("""
          SELECT * FROM atividades
          WHERE acaoId = :acaoId
      """)
      fun listarAtividadesComFuncionariosPorAcao(acaoId: Int): LiveData<List<AtividadeComFuncionarios>>

      @Query("DELETE FROM atividades WHERE id = :id")
      suspend fun deletarPorId(id: Int)

      @Insert
      suspend fun inserirComRetorno(atividade: AtividadeEntity): Long

      @Query("SELECT * FROM atividades WHERE id = :atividadeId")
      fun getAtividadeById(atividadeId: Int): LiveData<AtividadeEntity>


      @Transaction
      @Query("SELECT * FROM atividades WHERE id = :atividadeId")
      fun buscarAtividadeComFuncionarios(atividadeId: Int): LiveData<AtividadeComFuncionarios>


      @Query("SELECT * FROM atividades WHERE acaoId = :acaoId")
      fun listarAtividadesPorAcao(acaoId: Int): LiveData<List<AtividadeEntity>>

      @Transaction
      @Query("SELECT * FROM atividades WHERE id = :id")
      fun getAtividadeComFuncionariosPorId(id: Int): LiveData<AtividadeComFuncionarios>

      @Query("SELECT COUNT(*) FROM atividades WHERE acaoId = :acaoId")
      fun contarTotalPorAcao(acaoId: Int): LiveData<Int>

      @Query("SELECT COUNT(*) FROM atividades WHERE acaoId = :acaoId AND status = 'concluida'")
      fun contarConcluidasPorAcao(acaoId: Int): LiveData<Int>

      @Query("SELECT COUNT(*) FROM atividades WHERE acaoId = :acaoId")
      suspend fun contarTotalPorAcaoValor(acaoId: Int): Int

      @Query("SELECT COUNT(*) FROM atividades WHERE acaoId = :acaoId AND status = 'concluida'")
      suspend fun contarConcluidasPorAcaoValor(acaoId: Int): Int

      @Query("""
      SELECT a.* FROM atividades a
      INNER JOIN atividades_funcionarios af ON af.atividadeId = a.id
      WHERE af.funcionarioId = :funcionarioId
  """)
      fun listarAtividadesPorFuncionario(funcionarioId: Int): LiveData<List<AtividadeEntity>>

      @Query("""
      SELECT * FROM atividades
      WHERE id IN (
          SELECT atividadeId FROM atividades_funcionarios
          WHERE funcionarioId = :funcionarioId
      )
  """)
      fun listarAtividadesDoFuncionario(funcionarioId: Int): LiveData<List<AtividadeEntity>>

      @Transaction
      @Query("""
          SELECT * FROM atividades
          WHERE id IN (
              SELECT atividadeId FROM atividades_funcionarios
              WHERE funcionarioId = :funcionarioId
          )
      """)
      fun listarAtividadesComResponsaveis(funcionarioId: Int): LiveData<List<AtividadeComFuncionarios>>


      @Query("SELECT * FROM atividades WHERE id = :id")
      fun buscarPorId(id: Int): AtividadeEntity?

      @Query("SELECT * FROM atividades WHERE id = :id")
      suspend fun getById(id: Int): AtividadeEntity

      @Transaction
      @Query("SELECT * FROM atividades WHERE id = :atividadeId")
      suspend fun buscarComFuncionarios(atividadeId: Int): AtividadeComFuncionarios

      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insert(atividade: AtividadeEntity)

      @Update
      suspend fun update(atividade: AtividadeEntity)

      @Delete
      suspend fun delete(atividade: AtividadeEntity)

      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insertComRetorno(atividade: AtividadeEntity): Long

      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun inserirRelacaoFuncionario(relacao: AtividadeFuncionarioEntity)

    @Query("""
      SELECT a.* FROM atividades a
      INNER JOIN atividades_funcionarios af ON a.id = af.atividadeId
      WHERE af.funcionarioId = :funcionarioId
  """)
    suspend fun getAtividadesComPrazoPorFuncionario(funcionarioId: Int): List<AtividadeEntity>

    @Query("SELECT * FROM atividades WHERE dataPrazo IS NOT NULL")
    suspend fun getTodasAtividadesComDataPrazo(): List<AtividadeEntity>

    @Query("DELETE FROM atividades_funcionarios WHERE atividadeId = :atividadeId")
    suspend fun deletarRelacoesPorAtividade(atividadeId: Int)

    @Query("SELECT * FROM atividades WHERE id = :id")
    suspend fun getAtividadePorIdDireto(id: Int): AtividadeEntity

    @Query("SELECT * FROM atividades WHERE acaoId = :acaoId")
    suspend fun getAtividadesPorAcaoDireto(acaoId: Int): List<AtividadeEntity>

  }


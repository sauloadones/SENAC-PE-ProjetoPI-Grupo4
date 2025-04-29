package com.example.appsenkaspi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.Converters.Cargo
import kotlinx.coroutines.launch


class FuncionarioViewModel(application: Application) : AndroidViewModel(application) {
    // Em qualquer lugar do seu código (por ex no ViewModel ou num objeto utilitário)
    val funcionariosTeste = listOf(
        FuncionarioEntity(
            nomeCompleto = "Ana Beatriz Souza",
            email = "ana.souza@example.com",
            cargo = Cargo.COORDENADOR,
            fotoPerfil = "https://i.pravatar.cc/150?img=1",
            nomeUsuario = "ana.souza",
            senha = "senha123",
            idAcesso = 1
        ),
        FuncionarioEntity(
            nomeCompleto = "Carlos Eduardo Silva",
            email = "carlos.silva@example.com",
            cargo = Cargo.GESTOR,
            fotoPerfil = "https://i.pravatar.cc/150?img=2",
            nomeUsuario = "carlos.silva",
            senha = "senha123",
            idAcesso = 2
        ),
        FuncionarioEntity(
            nomeCompleto = "Fernanda Oliveira",
            email = "fernanda.oliveira@example.com",
            cargo = Cargo.APOIO,
            fotoPerfil = "https://i.pravatar.cc/150?img=3",
            nomeUsuario = "fernanda.oliveira",
            senha = "senha123",
            idAcesso = 3
        ),
        // adicione quantos quiser…
    )



    private val funcionarioDao = AppDatabase.getDatabase(application).funcionarioDao()
    val listaFuncionarios: LiveData<List<FuncionarioEntity>> =
        funcionarioDao.listarTodosFuncionarios()

    init {
        // Na primeira vez que abrir, popula se estiver vazio
        viewModelScope.launch {
            val atuais = listasFuncionarios.value
            if (atuais.isNullOrEmpty()) {
                funcionarioDao.inserirTodos(funcionariosTeste)
            }
        }
    }
    val listasFuncionarios = funcionarioDao.listarTodosFuncionarios()
    fun inserir(funcionario: FuncionarioEntity) = viewModelScope.launch {
        funcionarioDao.inserirFuncionario(funcionario)
    }

    fun atualizar(funcionario: FuncionarioEntity) = viewModelScope.launch {
        funcionarioDao.atualizarFuncionario(funcionario)
    }

    fun deletar(funcionario: FuncionarioEntity) = viewModelScope.launch {
        funcionarioDao.deletarFuncionario(funcionario)
    }
}


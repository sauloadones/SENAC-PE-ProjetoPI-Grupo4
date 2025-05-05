package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.appsenkaspi.databinding.FragmentTelaDetalheRequisicaoBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TelaDetalheRequisicaoFragment : Fragment() {

    private lateinit var binding: FragmentTelaDetalheRequisicaoBinding
    private lateinit var viewModel: RequisicaoViewModel
    private var requisicaoId: Int = -1
    private lateinit var usuarioAtual: FuncionarioEntity
    private lateinit var funcionarioDao: FuncionarioDao
    private lateinit var pilarDao: PilarDao
    private lateinit var atividadeDao: AtividadeDao
    private lateinit var acaoDao: AcaoDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = AppDatabase.getDatabase(requireContext())
        funcionarioDao = db.funcionarioDao()
        pilarDao = db.pilarDao()
        atividadeDao = db.atividadeDao()
        acaoDao = db.acaoDao()

        binding = FragmentTelaDetalheRequisicaoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RequisicaoViewModel::class.java]

        requisicaoId = arguments?.getInt("requisicaoId") ?: -1

        val prefs = requireContext().getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)
        val coordenadorId = prefs.getInt("funcionarioId", -1)

        viewLifecycleOwner.lifecycleScope.launch {
            val funcionario = funcionarioDao.buscarPorId(coordenadorId)

            if (funcionario == null) {
                requireActivity().finish()
                return@launch
            }

            usuarioAtual = funcionario
            carregarRequisicao()
        }

        return binding.root
    }

    private fun carregarRequisicao() {
        viewModel.viewModelScope.launch {
            val requisicao = viewModel.buscarPorId(requisicaoId)

            if (requisicao != null) {
                val gson = Gson()
                binding.textTipo.text = requisicao.tipo.name
                binding.textPilar.text = "Pilar ID: ${requisicao.pilarId}"
                binding.textSolicitante.text = "Solicitante ID: ${requisicao.solicitanteId}"

                if (requisicao.atividadeJson != null) {
                    val atividade = gson.fromJson(requisicao.atividadeJson, AtividadeEntity::class.java)
                    binding.textNome.text = atividade.nome
                    binding.textDescricao.text = atividade.descricao
                    binding.textDataInicio.text = formatarData(atividade.dataCriacao)
                    binding.textDataPrazo.text = formatarData(atividade.dataPrazo)
                    binding.textPrioridade.text = atividade.prioridade.name
                    binding.textStatus.text = atividade.status.name

                    try {
                        val atividadeCompleta = atividadeDao.buscarComFuncionarios(atividade.id)
                        val nomes = atividadeCompleta.funcionarios.map { it.nomeUsuario }
                        binding.textResponsaveis.text = "Responsáveis: ${nomes.joinToString(", ")}"
                    } catch (e: Exception) {
                        binding.textResponsaveis.text = "Responsáveis: Erro ao carregar"
                    }
                }

                if (requisicao.acaoJson != null) {
                    val acao = gson.fromJson(requisicao.acaoJson, AcaoEntity::class.java)
                    binding.textNome.text = acao.nome
                    binding.textDescricao.text = acao.descricao
                    binding.textDataInicio.text = formatarData(acao.dataInicio)
                    binding.textDataPrazo.text = formatarData(acao.dataPrazo)
                    binding.textStatus.text = acao.status.name

                    try {
                        val acaoCompleta = acaoDao.buscarComFuncionarios(acao.id)
                        val nomes = acaoCompleta.funcionarios.map { it.nomeUsuario }
                        binding.textResponsaveis.text = "Responsáveis: ${nomes.joinToString(", ")}"
                    } catch (e: Exception) {
                        binding.textResponsaveis.text = "Responsáveis: Erro ao carregar"
                    }

                    binding.textPrioridade.visibility = View.GONE // Ação não tem prioridade
                }

                // Ações dos botões
                binding.btnAceitar.setOnClickListener {
                    val tipoNome = when (requisicao.tipo) {
                        TipoRequisicao.CRIAR_ATIVIDADE,
                        TipoRequisicao.EDITAR_ATIVIDADE,
                        TipoRequisicao.COMPLETAR_ATIVIDADE -> "atividade"
                        TipoRequisicao.CRIAR_ACAO,
                        TipoRequisicao.EDITAR_ACAO -> "ação"
                        else -> "requisição"
                    }
                    val mensagem = "Sua $tipoNome foi aceita por ${usuarioAtual.nomeUsuario}"
                    viewModel.responderRequisicao(requisicao.id, StatusRequisicao.ACEITA, usuarioAtual.id, mensagem)
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.btnRecusar.setOnClickListener {
                    val tipoNome = when (requisicao.tipo) {
                        TipoRequisicao.CRIAR_ATIVIDADE,
                        TipoRequisicao.EDITAR_ATIVIDADE,
                        TipoRequisicao.COMPLETAR_ATIVIDADE -> "atividade"
                        TipoRequisicao.CRIAR_ACAO,
                        TipoRequisicao.EDITAR_ACAO -> "ação"
                        else -> "requisição"
                    }
                    val mensagem = "Sua $tipoNome foi recusada por ${usuarioAtual.nomeUsuario}"
                    viewModel.responderRequisicao(requisicao.id, StatusRequisicao.RECUSADA, usuarioAtual.id, mensagem)
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun formatarData(data: Date?): String {
        if (data == null) return "Indefinida"
        val formatador = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatador.format(data)
    }
}

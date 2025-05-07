package com.example.appsenkaspi

import ResponsavelAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetalheNotificacaoFragment : Fragment() {

    private val viewModel: NotificacaoViewModel by activityViewModels()
    private lateinit var requisicao: RequisicaoEntity
    private val formatoData = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detalhe_notificacao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requisicaoId = arguments?.getInt("requisicaoId") ?: return

        viewModel.getRequisicaoPorId(requisicaoId).observe(viewLifecycleOwner) { req ->
            requisicao = req ?: return@observe

            val tvTituloTopo = view.findViewById<TextView>(R.id.tvTituloTopo)
            val tvPilar = view.findViewById<TextView>(R.id.tvPilar)
            val tvNome = view.findViewById<TextView>(R.id.tvNome)
            val tvDescricao = view.findViewById<TextView>(R.id.tvDescricao)
            val tvDataInicio = view.findViewById<TextView>(R.id.tvDataInicio)
            val tvDataFinal = view.findViewById<TextView>(R.id.tvDataFinal)
            val tvPrazo = view.findViewById<TextView>(R.id.tvPrazo)
            val tvResponsaveis = view.findViewById<TextView>(R.id.tvResponsaveis)
            val tvPrioridade = view.findViewById<(TextView)>(R.id.tvPrioridade)
            val tvPrioridadeBox = view.findViewById<(TextView)>(R.id.tvPrioridadeBox)
            when (requisicao.tipo) {
                TipoRequisicao.CRIAR_ACAO, TipoRequisicao.EDITAR_ACAO -> {
                    val acao = Gson().fromJson(requisicao.acaoJson, AcaoJson::class.java)
                    tvTituloTopo.text = if (requisicao.tipo == TipoRequisicao.CRIAR_ACAO) "Solicitação de Nova Ação" else "Edição de Ação"
                    tvPilar.text = "1° Pilar: ${acao.nomePilar}"
                    tvNome.text = acao.nome
                    tvDescricao.text = acao.descricao
                    tvPrazo.text = "Prazo da ação: ${formatoData.format(acao.dataPrazo)}"
                    tvDataInicio.visibility = View.GONE
                    tvDataFinal.visibility = View.GONE
                    tvPrazo.visibility = View.VISIBLE

                    // Buscar e exibir nomes
                    lifecycleScope.launch {
                        val nomes = AppDatabase.getDatabase(requireContext())
                            .funcionarioDao()
                            .getFuncionariosPorIds(acao.responsaveis)
                            .joinToString { it.nomeCompleto }
                        tvResponsaveis.text = "Responsáveis: $nomes"
                    }
                }

                TipoRequisicao.CRIAR_ATIVIDADE, TipoRequisicao.EDITAR_ATIVIDADE -> {
                    val atividade = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
                    tvTituloTopo.text = if (requisicao.tipo == TipoRequisicao.CRIAR_ATIVIDADE) "Solicitação de Nova Atividade" else "Edição de Atividade"
                    tvPilar.text = "1° Pilar: ${atividade.nomePilar}"
                    tvNome.text = atividade.nome
                    tvDescricao.text = atividade.descricao
                    tvDataInicio.text = "Data de início: ${formatoData.format(atividade.dataInicio)}"
                    tvDataFinal.text = "Data de Termino: ${formatoData.format(atividade.dataPrazo)}"
                    tvDataInicio.visibility = View.VISIBLE
                    tvDataFinal.visibility = View.VISIBLE
                    tvPrazo.visibility = View.GONE
                    tvPrioridade.text = atividade.prioridade.name.uppercase()
                    tvPrioridade.visibility = View.VISIBLE
                    tvPrioridadeBox.visibility = View.VISIBLE

                    // Buscar e exibir nomes
                    lifecycleScope.launch {
                        val nomes = AppDatabase.getDatabase(requireContext())
                            .funcionarioDao()
                            .getFuncionariosPorIds(atividade.responsaveis)
                            .joinToString { it.nomeCompleto }
                        tvResponsaveis.text = "Responsáveis: $nomes"
                    }
                }

                TipoRequisicao.COMPLETAR_ATIVIDADE -> {
                    val atividade = Gson().fromJson(requisicao.atividadeJson, AtividadeJson::class.java)
                    tvTituloTopo.text = "Conclusão de Atividade"
                    tvPilar.text = "1° Pilar: ${atividade.nomePilar}"
                    tvNome.text = atividade.nome
                    tvDescricao.text = atividade.descricao
                    tvDataInicio.text = "Data de início: ${formatoData.format(atividade.dataInicio)}"
                    tvDataFinal.text = "Finalizada em: ${formatoData.format(atividade.dataPrazo)}"
                    tvPrioridade.text = atividade.prioridade.name.uppercase()
                    tvPrioridadeBox.visibility = View.VISIBLE
                    tvPrioridade.visibility = View.VISIBLE
                    tvDataInicio.visibility = View.VISIBLE
                    tvDataFinal.visibility = View.VISIBLE
                    tvPrazo.visibility = View.GONE

                    // Buscar e exibir nomes
                    lifecycleScope.launch {
                        val nomes = AppDatabase.getDatabase(requireContext())
                            .funcionarioDao()
                            .getFuncionariosPorIds(atividade.responsaveis)
                            .joinToString { it.nomeCompleto }
                        tvResponsaveis.text = "Responsáveis: $nomes"
                    }
                }

                else -> {}
            }

            view.findViewById<Button>(R.id.btnAceitar).setOnClickListener {
                viewModel.responderRequisicao(requireContext(), requisicao, true)
                requireActivity().supportFragmentManager.popBackStack()
            }

            view.findViewById<Button>(R.id.btnRecusar).setOnClickListener {
                viewModel.responderRequisicao(requireContext(), requisicao, false)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
}

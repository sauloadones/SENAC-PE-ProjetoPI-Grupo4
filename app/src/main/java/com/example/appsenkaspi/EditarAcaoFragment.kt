package com.example.appsenkaspi

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.appsenkaspi.databinding.FragmentEditarAcaoBinding
import com.example.appsenkaspi.utils.configurarBotaoVoltar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditarAcaoFragment : Fragment() {

    private var _binding: FragmentEditarAcaoBinding? = null
    private val binding get() = _binding!!

    private val acaoViewModel: AcaoViewModel by activityViewModels()
    private val acaoFuncionarioViewModel: AcaoFuncionarioViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()

    private var dataPrazoSelecionada: Date? = null
    private val calendario = Calendar.getInstance()

    private var acaoId: Int = -1
    private val funcionariosSelecionados = mutableListOf<FuncionarioEntity>()
    private lateinit var adapterSelecionados: FuncionarioSelecionadoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarAcaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)
        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            when (funcionario?.cargo) {
                Cargo.APOIO -> {
                    binding.confirmarButtonWrapperEdicao.visibility = View.GONE
                    binding.cardPedirConfirmacao.visibility = View.VISIBLE
                    binding.iconeMenuEdicao.visibility = View.GONE
                }
                Cargo.COORDENADOR -> {
                    binding.confirmarButtonWrapperEdicao.visibility = View.VISIBLE
                    binding.cardPedirConfirmacao.visibility = View.GONE
                }
                Cargo.GESTOR -> {
                    binding.confirmarButtonWrapperEdicao.visibility = View.GONE
                    binding.cardPedirConfirmacao.visibility = View.GONE
                    binding.iconeMenuEdicao.visibility = View.GONE
                }

                else -> {
                    binding.confirmarButtonWrapperEdicao.visibility = View.GONE
                    binding.cardPedirConfirmacao.visibility = View.GONE
                    binding.iconeMenuEdicao.visibility = View.GONE
                }
            }
        }


        adapterSelecionados = FuncionarioSelecionadoAdapter(funcionariosSelecionados)
        binding.recyclerViewFuncionariosSelecionados.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewFuncionariosSelecionados.adapter = adapterSelecionados

        binding.buttonPickDateEdicao.setOnClickListener { abrirDatePicker() }
        binding.iconSelecionarFuncionario.setOnClickListener { abrirDialogSelecionarFuncionarios() }
        binding.confirmarButtonWrapperEdicao.setOnClickListener { confirmarEdicaoAcao() }
        val menuIcon = view.findViewById<ImageView>(R.id.iconeMenuEdicao)
        menuIcon.setOnClickListener { exibirPopupMenu(it) }

        acaoId = arguments?.getInt("acaoId") ?: -1
        if (acaoId == -1) {
            Toast.makeText(requireContext(), "Erro: Ação inválida!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        acaoViewModel.getAcaoById(acaoId).observe(viewLifecycleOwner) { acao ->
            acao?.let {
                binding.inputNomeEdicao.setText(it.nome)
                binding.inputDescricaoEdicao.setText(it.descricao)
                dataPrazoSelecionada = it.dataPrazo
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.buttonPickDateEdicao.text = fmt.format(it.dataPrazo)
            }
        }

        childFragmentManager.setFragmentResultListener(
            "funcionariosSelecionados",
            viewLifecycleOwner
        ) { _, bundle ->
            val lista = bundle.getParcelableArrayList<FuncionarioEntity>("listaFuncionarios") ?: arrayListOf()
            funcionariosSelecionados.clear()
            funcionariosSelecionados.addAll(lista.filterNotNull())
            adapterSelecionados.notifyDataSetChanged()
        }
    }

    private fun deletarAcao() {
        viewLifecycleOwner.lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(requireContext()).acaoDao()
            val acao = dao.buscarAcaoPorId(acaoId)
            if (acao != null) {
                dao.deletarAcao(acao)
                Toast.makeText(requireContext(), "Ação deletada com sucesso!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Erro ao localizar a ação!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun abrirDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, ano, mes, dia ->
                calendario.set(ano, mes, dia)
                dataPrazoSelecionada = calendario.time
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.buttonPickDateEdicao.text = fmt.format(dataPrazoSelecionada!!)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun abrirDialogSelecionarFuncionarios() {
        SelecionarResponsavelDialogFragment()
            .show(childFragmentManager, "SelecionarFuncionariosDialog")
    }
    private fun exibirDialogoConfirmacao() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja deletar este Ação?")
            .setPositiveButton("Deletar") { _, _ -> deletarAcao() }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun exibirPopupMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.menu_pilar, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_deletar -> {
                    exibirDialogoConfirmacao()
                    true
                }
                else -> false
            }
        }




        popup.show()
    }

    private fun confirmarEdicaoAcao() {
        val nome = binding.inputNomeEdicao.text.toString().trim()
        val descricao = binding.inputDescricaoEdicao.text.toString().trim()

        if (nome.isEmpty()) {
            binding.inputNomeEdicao.error = "Digite o nome da ação"
            return
        }
        if (dataPrazoSelecionada == null) {
            binding.buttonPickDateEdicao.error = "Selecione uma data de prazo"
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val acaoExistente = acaoViewModel.getAcaoByIdNow(acaoId)
            if (acaoExistente != null) {
                val acaoAtualizada = acaoExistente.copy(
                    nome = nome,
                    descricao = descricao,
                    dataPrazo = dataPrazoSelecionada!!
                )
                acaoViewModel.atualizar(acaoAtualizada)

                Toast.makeText(requireContext(), "Ação atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Erro ao atualizar a ação!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

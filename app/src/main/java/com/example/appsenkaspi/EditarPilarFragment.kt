package com.example.appsenkaspi

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditarPilarFragment : Fragment() {

    private lateinit var inputNomeEdicao: EditText
    private lateinit var inputDescricaoEdicao: EditText
    private lateinit var buttonPickDateEdicao: Button
    private lateinit var confirmarEdicaoButtonWrapper: FrameLayout

    private val pilarViewModel: PilarViewModel by activityViewModels()

    private var novaDataSelecionada: Date? = null
    private var pilarId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_editar_pilar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputNomeEdicao = view.findViewById(R.id.inputNomeEdicao)
        inputDescricaoEdicao = view.findViewById(R.id.inputDescricaoEdicao)
        buttonPickDateEdicao = view.findViewById(R.id.buttonPickDateEdicao)
        confirmarEdicaoButtonWrapper = view.findViewById(R.id.confirmarButtonWrapperEdicao)

        buttonPickDateEdicao.setOnClickListener { abrirDatePicker() }
        confirmarEdicaoButtonWrapper.setOnClickListener { confirmarEdicao() }

        // Recebe o ID do Pilar via argumentos
        pilarId = arguments?.getInt("pilarId") ?: -1
        if (pilarId != -1) {
            carregarDadosPilar(pilarId)
        } else {
            Toast.makeText(requireContext(), "Erro ao carregar Pilar!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun carregarDadosPilar(id: Int) {
        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(requireContext()).pilarDao()
            val pilar = dao.buscarPilarPorId(id)
            if (pilar != null) {
                inputNomeEdicao.setText(pilar.nome)
                inputDescricaoEdicao.setText(pilar.descricao)
                novaDataSelecionada = pilar.dataPrazo

                novaDataSelecionada?.let {
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    buttonPickDateEdicao.text = formato.format(it)
                }
            }
        }
    }

    private fun abrirDatePicker() {
        val calendario = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendario.set(year, month, dayOfMonth)
                novaDataSelecionada = calendario.time
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                buttonPickDateEdicao.text = formato.format(novaDataSelecionada!!)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun confirmarEdicao() {
        val novoNome = inputNomeEdicao.text.toString().trim()
        val novaDescricao = inputDescricaoEdicao.text.toString().trim()

        if (novoNome.isEmpty()) {
            inputNomeEdicao.error = "Digite o novo nome do Pilar"
            return
        }

        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(requireContext()).pilarDao()
            val pilarExistente = dao.buscarPilarPorId(pilarId)

            if (pilarExistente != null) {
                val pilarAtualizado = pilarExistente.copy(
                    nome = novoNome,
                    descricao = novaDescricao,
                    dataPrazo = novaDataSelecionada ?: pilarExistente.dataPrazo
                )

                dao.atualizarPilar(pilarAtualizado)

                Toast.makeText(requireContext(), "Pilar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }
}

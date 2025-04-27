package com.example.appsenkaspi

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CriarPilarFragment : Fragment() {

    private lateinit var inputNomePilar: EditText
    private lateinit var inputDescricao: EditText
    private lateinit var buttonPickDate: Button
    private lateinit var buttonAddSubpilar: LinearLayout
    private lateinit var confirmarButtonWrapper: FrameLayout
    private lateinit var recyclerViewSubpilares: RecyclerView

    private val listaSubpilares = mutableListOf<String>()
    private lateinit var subpilarAdapter: SubpilarAdapter

    private var dataSelecionada: Date? = null
    private val calendario = Calendar.getInstance()

    private val homeViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory((requireActivity().application as App).database.pilarDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_criar_pilar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputNomePilar = view.findViewById(R.id.inputNomePilar)
        inputDescricao = view.findViewById(R.id.inputDescricao)
        buttonPickDate = view.findViewById(R.id.buttonPickDate)
        buttonAddSubpilar = view.findViewById(R.id.buttonAddSubpilar)
        confirmarButtonWrapper = view.findViewById(R.id.confirmarButtonWrapper)
        recyclerViewSubpilares = view.findViewById(R.id.recyclerViewSubpilares)

        configurarRecyclerView()

        buttonPickDate.setOnClickListener {
            abrirDatePicker()
        }

        buttonAddSubpilar.setOnClickListener {
            val prazoSelecionado = dataSelecionada
            if (prazoSelecionado != null) {
                val dialog = AdicionarSubpilarDialogFragment(prazoSelecionado)
                dialog.show(parentFragmentManager, "AdicionarSubpilarDialog")
            } else {
                buttonPickDate.error = "Escolha primeiro um prazo para o Pilar"
            }
        }

        confirmarButtonWrapper.setOnClickListener {
            confirmarCriacaoPilar()
        }

        parentFragmentManager.setFragmentResultListener("novoSubpilar", viewLifecycleOwner) { _, bundle ->
            val nomeSubpilar = bundle.getString("nomeSubpilar")
            if (nomeSubpilar != null) {
                listaSubpilares.add(nomeSubpilar)
                subpilarAdapter.notifyItemInserted(listaSubpilares.size - 1)
            }
        }
    }

    private fun configurarRecyclerView() {
        subpilarAdapter = SubpilarAdapter(listaSubpilares)
        recyclerViewSubpilares.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSubpilares.adapter = subpilarAdapter
    }

    private fun abrirDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendario.set(year, month, dayOfMonth)
                dataSelecionada = calendario.time
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                buttonPickDate.text = formato.format(dataSelecionada!!)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun confirmarCriacaoPilar() {
        val nome = inputNomePilar.text.toString().trim()
        val descricao = inputDescricao.text.toString().trim()

        if (nome.isEmpty()) {
            inputNomePilar.error = "Nome obrigatório"
            return
        }

        homeViewModel.adicionarPilar(nome, descricao, dataSelecionada)

        parentFragmentManager.popBackStack()
    }
}

package com.example.appsenkaspi

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.Converters.StatusPilar
import com.example.appsenkaspi.databinding.FragmentCriarPilarBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CriarPilarFragment : Fragment() {

    private var _binding: FragmentCriarPilarBinding? = null
    private val binding get() = _binding!!

    private val pilarViewModel: PilarViewModel        by activityViewModels()
    private val subpilarViewModel: SubpilarViewModel  by activityViewModels()

    private var dataPrazoSelecionada: Date? = null
    private val calendario = Calendar.getInstance()

    private val listaSubpilares = mutableListOf<String>()
    private lateinit var subpilarAdapter: SubpilarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarPilarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // configura RecyclerView de subpilares
        subpilarAdapter = SubpilarAdapter(listaSubpilares)
        binding.recyclerViewSubpilares.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = subpilarAdapter
        }

        // listeners
        binding.buttonPickDate.setOnClickListener { abrirDatePicker() }
        binding.buttonAddSubpilar.setOnClickListener { abrirDialogAdicionarSubpilar() }
        binding.confirmarButtonWrapper.setOnClickListener { confirmarCriacaoPilar() }

        // recebe subpilar criado no diálogo
        childFragmentManager.setFragmentResultListener("novoSubpilar", viewLifecycleOwner) { _, bundle ->
            bundle.getString("nomeSubpilar")?.let { nome ->
                listaSubpilares.add(nome)
                subpilarAdapter.notifyItemInserted(listaSubpilares.size - 1)
            }
        }
    }

    private fun abrirDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendario.set(year, month, day)
                dataPrazoSelecionada = calendario.time
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.buttonPickDate.text = fmt.format(dataPrazoSelecionada!!)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun abrirDialogAdicionarSubpilar() {
        dataPrazoSelecionada?.let { prazo ->
            AdicionarSubpilarDialogFragment.newInstance(-1, prazo)
                .show(childFragmentManager, "AdicionarSubpilarDialog")
        } ?: run {
            binding.buttonPickDate.error = "Escolha primeiro um prazo"
        }
    }

    private fun confirmarCriacaoPilar() {
        val nome      = binding.inputNomePilar.text.toString().trim()
        val descricao = binding.inputDescricao.text.toString().trim()
        val prazo     = dataPrazoSelecionada

        if (nome.isEmpty()) {
            binding.inputNomePilar.error = "Digite o nome do Pilar"
            return
        }
        if (prazo == null) {
            binding.buttonPickDate.error = "Escolha um prazo"
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // insere pilar e obtém ID
            val idLong = pilarViewModel.inserirRetornandoId(
                PilarEntity(
                    nome = nome,
                    descricao = descricao,
                    dataInicio = Date(),
                    dataPrazo = prazo,
                    status = StatusPilar.VENCIDO,
                    dataCriacao = Date(),
                    criadoPor = Int
                )
            )
            val novoId = idLong.toInt()

            // insere subpilares
            listaSubpilares.forEach { subNome ->
                subpilarViewModel.inserir(
                    SubpilarEntity(
                        nome       = subNome,
                        descricao  = null,
                        dataInicio = Date(),
                        dataPrazo  = prazo,
                        pilarId    = novoId
                    )
                )
            }

            // navega para TelaPilarFragment
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    TelaPilarFragment().apply {
                        arguments = Bundle().apply { putInt("pilarId", novoId) }
                    }
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

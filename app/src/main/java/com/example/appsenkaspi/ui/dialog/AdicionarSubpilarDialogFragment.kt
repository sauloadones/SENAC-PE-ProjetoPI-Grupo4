package com.example.appsenkaspi.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.appsenkaspi.viewmodel.NotificacaoViewModel
import com.example.appsenkaspi.R
import com.example.appsenkaspi.viewmodel.SubpilarViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdicionarSubpilarDialogFragment : DialogFragment() {

  private var pilarId: Int = -1
  private var prazoMaximo: Date? = null

  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()
  private val subpilarViewModel: SubpilarViewModel by activityViewModels()

  private lateinit var inputNomeSubpilar: EditText
  private lateinit var inputDescricaoSubpilar: EditText
  private lateinit var buttonPickDateSubpilar: Button
  private lateinit var buttonConfirmarSubpilar: Button

  private var dataSelecionada: Date? = null
  private val calendario = Calendar.getInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let { bundle ->
      pilarId = bundle.getInt(ARG_PILAR_ID)
      prazoMaximo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        bundle.getSerializable(ARG_PRAZO, Date::class.java)
      } else {
        @Suppress("DEPRECATION")
        bundle.getSerializable(ARG_PRAZO) as? Date
      }
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val view = LayoutInflater.from(context).inflate(R.layout.popup_adicionar_subpilar, null)

    inputNomeSubpilar = view.findViewById(R.id.inputNomeSubpilar)
    inputDescricaoSubpilar = view.findViewById(R.id.inputDescricaoSubpilar)
    buttonPickDateSubpilar = view.findViewById(R.id.buttonPickDateSubpilar)
    buttonConfirmarSubpilar = view.findViewById(R.id.buttonConfirmarSubpilar)

    buttonPickDateSubpilar.setOnClickListener { abrirDatePicker() }
    buttonConfirmarSubpilar.setOnClickListener { confirmarSubpilar() }

    return AlertDialog.Builder(requireContext()).setView(view).create()
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.apply {
      setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
  }

  private fun abrirDatePicker() {
    DatePickerDialog(
        requireContext(),
        { _, year, month, dayOfMonth ->
            calendario.set(year, month, dayOfMonth)
            val dataEscolhida = calendario.time
            if (prazoMaximo != null && dataEscolhida.after(prazoMaximo)) {
                buttonPickDateSubpilar.error = "Data deve ser igual ou anterior ao prazo do Pilar"
            } else {
                dataSelecionada = dataEscolhida
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                buttonPickDateSubpilar.text = formato.format(dataEscolhida)
                buttonPickDateSubpilar.error = null
            }
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    ).show()
  }

  private fun confirmarSubpilar() {
    val nome = inputNomeSubpilar.text.toString().trim()
    val descricao = inputDescricaoSubpilar.text.toString().trim()

    if (nome.isEmpty()) {
      inputNomeSubpilar.error = "Nome obrigatório"
      return
    }
    if (dataSelecionada == null) {
      buttonPickDateSubpilar.error = "Selecione uma data válida"
      return
    }

    // Notifica o fragmento pai com os dados preenchidos
    parentFragmentManager.setFragmentResult(
      "novoSubpilar",
        bundleOf(
            "nomeSubpilar" to nome,
            "descricaoSubpilar" to descricao,
            "prazoSubpilar" to dataSelecionada
        )
    )

    // Fecha o diálogo
    dismiss()
  }


  companion object {
    private const val ARG_PILAR_ID = "pilarId"
    private const val ARG_PRAZO = "prazoMaximo"

    fun newInstance(pilarId: Int, prazoMaximo: Date): AdicionarSubpilarDialogFragment {
      return AdicionarSubpilarDialogFragment().apply {
        arguments = Bundle().apply {
          putInt(ARG_PILAR_ID, pilarId)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            putSerializable(ARG_PRAZO, prazoMaximo)
          } else {
            @Suppress("DEPRECATION")
            putSerializable(ARG_PRAZO, prazoMaximo)
          }
        }
      }
    }
  }
}

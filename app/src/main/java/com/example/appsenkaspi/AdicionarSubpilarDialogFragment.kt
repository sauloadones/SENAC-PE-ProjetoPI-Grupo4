package com.example.appsenkaspi

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdicionarSubpilarDialogFragment(private val prazoMaximo: Date?) : DialogFragment() {

    private lateinit var inputNomeSubpilar: EditText
    private lateinit var inputDescricaoSubpilar: EditText
    private lateinit var buttonPickDateSubpilar: Button
    private lateinit var buttonConfirmarSubpilar: Button

    private var dataSelecionada: Date? = null
    private val calendario = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.popup_adicionar_subpilar, null)

        inputNomeSubpilar = view.findViewById(R.id.inputNomeSubpilar)
        inputDescricaoSubpilar = view.findViewById(R.id.inputDescricaoSubpilar)
        buttonPickDateSubpilar = view.findViewById(R.id.buttonPickDateSubpilar)
        buttonConfirmarSubpilar = view.findViewById(R.id.buttonConfirmarSubpilar)

        buttonPickDateSubpilar.setOnClickListener {
            abrirDatePicker()
        }

        buttonConfirmarSubpilar.setOnClickListener {
            confirmarSubpilar()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun abrirDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendario.set(year, month, dayOfMonth)
                val dataEscolhida = calendario.time

                if (prazoMaximo != null && dataEscolhida.after(prazoMaximo)) {
                    buttonPickDateSubpilar.error = "Data deve ser igual ou anterior ao prazo do Pilar"
                } else {
                    dataSelecionada = dataEscolhida
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    buttonPickDateSubpilar.text = formato.format(dataSelecionada!!)

                    buttonPickDateSubpilar.error = null  // 🛠 Limpa o erro se for válido!
                }
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun confirmarSubpilar() {
        val nome = inputNomeSubpilar.text.toString().trim()

        if (nome.isEmpty()) {
            inputNomeSubpilar.error = "Nome obrigatório"
            return
        }
        if (dataSelecionada == null) {
            buttonPickDateSubpilar.error = "Selecione uma data válida"
            return
        }

        val bundle = Bundle().apply {
            putString("nomeSubpilar", nome)
            putString("descricaoSubpilar", inputDescricaoSubpilar.text.toString().trim())
            putSerializable("dataSubpilar", dataSelecionada)
        }

        parentFragmentManager.setFragmentResult("novoSubpilar", bundle)
        dismiss()
    }

}

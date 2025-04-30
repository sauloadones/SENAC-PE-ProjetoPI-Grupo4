package com.example.appsenkaspi

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EsqueciSenhaDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.popup_esqueci_senha, null)

        val campoId = view.findViewById<EditText>(R.id.editTextIdRecuperacao)
        val botaoRecuperar = view.findViewById<Button>(R.id.buttonRecuperar)

        botaoRecuperar.setOnClickListener {
            val idRecuperado = campoId.text.toString()
            dismiss()
        }

        return AlertDialog.Builder(requireContext(), R.style.LoginDialogTheme)
            .setView(view)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val density = resources.displayMetrics.density
        dialog?.window?.setLayout(
            (320 * density).toInt(), // Altere esse valor conforme necessário
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
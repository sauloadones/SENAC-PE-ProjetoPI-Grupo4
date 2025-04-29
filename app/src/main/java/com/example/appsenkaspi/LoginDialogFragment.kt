package com.example.appsenkaspi

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class LoginDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.login_popup, null)

        val editTextId = view.findViewById<EditText>(R.id.editTextId)
        val editTextSenha = view.findViewById<EditText>(R.id.editTextSenha)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBoxLembrar)
        val buttonEntrar = view.findViewById<Button>(R.id.buttonEntrar)
        val textEsqueceu = view.findViewById<TextView>(R.id.textEsqueceuSenha)

        buttonEntrar.setOnClickListener {
            val id = editTextId.text.toString()
            val senha = editTextSenha.text.toString()

            // Inicia a Activity CriarPilares
            val intent = Intent(requireContext(), TelaPrincipalActivity::class.java)
            startActivity(intent)

            dismiss() // fecha o dialogo atual
        }

        textEsqueceu.setOnClickListener {
            EsqueciSenhaDialogFragment().show(parentFragmentManager, "esqueciSenhaPopup")
        }

        return AlertDialog.Builder(requireContext(), R.style.LoginDialogTheme)
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
}
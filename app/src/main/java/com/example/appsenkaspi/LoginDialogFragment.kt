package com.example.appsenkaspi

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.*
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

        // Lógica de mostrar/ocultar senha
        var senhaVisivel = false
        editTextSenha.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX >= (editTextSenha.right - editTextSenha.compoundDrawables[DRAWABLE_END].bounds.width())) {

                senhaVisivel = !senhaVisivel

                if (senhaVisivel) {
                    editTextSenha.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    editTextSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_open_eye, 0)
                } else {
                    editTextSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    editTextSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_closed_eye, 0)
                }

                // Mantém o cursor no final do texto
                editTextSenha.setSelection(editTextSenha.text.length)

                true
            } else {
                false
            }
        }

        buttonEntrar.setOnClickListener {
            val id = editTextId.text.toString()
            val senha = editTextSenha.text.toString()

            // Inicia a TelaPrincipalActivity
            val intent = Intent(requireContext(), TelaPrincipalActivity::class.java)
            startActivity(intent)

            dismiss()
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
            (320 * resources.displayMetrics.density).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}

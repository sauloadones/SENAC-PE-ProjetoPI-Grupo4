package com.example.appsenkaspi

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginDialogFragment : DialogFragment() {

    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.login_popup, null)

        val editTextId = view.findViewById<EditText>(R.id.editTextId)
        val editTextSenha = view.findViewById<EditText>(R.id.editTextSenha)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBoxLembrar)
        val buttonEntrar = view.findViewById<Button>(R.id.buttonEntrar)
        val textEsqueceu = view.findViewById<TextView>(R.id.textEsqueceuSenha)

        // Lógica de mostrar/ocultar senha
        var senhaVisivel = false
        editTextSenha.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            val drawable = editTextSenha.compoundDrawables[DRAWABLE_END]
            if (drawable != null && event.action == MotionEvent.ACTION_UP) {
                val touchAreaStart = editTextSenha.right - drawable.bounds.width() - 20
                if (event.rawX >= touchAreaStart) {
                    senhaVisivel = !senhaVisivel
                    if (senhaVisivel) {
                        editTextSenha.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        editTextSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_open_eye, 0)
                    } else {
                        editTextSenha.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        editTextSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_closed_eye, 0)
                    }
                    editTextSenha.setSelection(editTextSenha.text.length)
                    v.performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }

        buttonEntrar.setOnClickListener {
            val idAcesso = editTextId.text.toString().trim().toIntOrNull()
            val senha = editTextSenha.text.toString().trim()

            if (idAcesso == null || senha.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(requireContext()).funcionarioDao()
                val funcionario = dao.autenticar(idAcesso, senha)

                if (funcionario != null) {
                    // Salva login no SharedPreferences
                    val prefs = requireContext().getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putInt("funcionario_id", funcionario.id).apply()
                    prefs.edit().putBoolean("lembrarLogin", checkBox.isChecked).apply()

                    funcionarioViewModel.logarFuncionario(funcionario)

                    startActivity(Intent(requireContext(), TelaPrincipalActivity::class.java))
                    dismiss()
                } else {
                    Toast.makeText(context, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                }
            }
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

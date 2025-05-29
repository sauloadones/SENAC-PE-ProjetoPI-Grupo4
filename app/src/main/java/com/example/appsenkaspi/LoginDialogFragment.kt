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
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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

        // Setup seguro para armazenamento criptografado
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "login_secure_prefs",
            masterKeyAlias,
            requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // Preencher os campos se "lembrar de mim" estiver ativado
        val lembrarLogin = sharedPreferences.getBoolean("lembrarLogin", false)
        if (lembrarLogin) {
            editTextId.setText(sharedPreferences.getString("idAcesso", ""))
            editTextSenha.setText(sharedPreferences.getString("senha", ""))
            checkBox.isChecked = true
        }

        // Mostrar/ocultar senha
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
                    // Salva o login de forma segura
                    val editor = sharedPreferences.edit()
                    if (checkBox.isChecked) {
                        editor.putString("idAcesso", idAcesso.toString())
                        editor.putString("senha", senha)
                        editor.putBoolean("lembrarLogin", true)
                    } else {
                        editor.clear()
                    }
                    editor.apply()

                    salvarFuncionarioLogado(requireContext(), funcionario.id, funcionario.nomeUsuario)

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

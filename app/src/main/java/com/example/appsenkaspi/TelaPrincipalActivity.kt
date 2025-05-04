package com.example.appsenkaspi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appsenkaspi.databinding.ActivityTelaPrincipalBinding
import kotlinx.coroutines.launch

class TelaPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTelaPrincipalBinding
    private lateinit var funcionarioViewModel: FuncionarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica login via SharedPreferences
        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val funcionarioId = prefs.getInt("funcionarioId", -1)
        if (funcionarioId == -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Inicializa ViewModel
        funcionarioViewModel = ViewModelProvider(this)[FuncionarioViewModel::class.java]

        // Recupera o funcionário do banco e define no ViewModel
        lifecycleScope.launch {
            val funcionario = AppDatabase.getDatabase(this@TelaPrincipalActivity)
                .funcionarioDao()
                .buscarPorId(funcionarioId)

            if (funcionario != null) {
                funcionarioViewModel.logarFuncionario(funcionario)
            } else {
                // Falha ao encontrar o funcionário salvo, volta pro login
                prefs.edit().clear().apply()
                startActivity(Intent(this@TelaPrincipalActivity, MainActivity::class.java))
                finish()
                return@launch
            }

            // Após garantir o login e ViewModel, abre a tela principal
            binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
            setContentView(binding.root)

            if (savedInstanceState == null) {
                supportFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment())
                    .commit()
            }
        }
    }
}

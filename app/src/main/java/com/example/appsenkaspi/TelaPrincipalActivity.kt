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

        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("funcionario_prefs", MODE_PRIVATE)
        val funcionarioId = prefs.getInt("funcionario_id", -1)

        if (funcionarioId == -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        funcionarioViewModel = ViewModelProvider(this)[FuncionarioViewModel::class.java]

        lifecycleScope.launch {
            val funcionario = AppDatabase.getDatabase(this@TelaPrincipalActivity)
                .funcionarioDao()
                .buscarPorId(funcionarioId)

            if (funcionario != null) {
                funcionarioViewModel.logarFuncionario(funcionario)
            } else {
                prefs.edit().clear().apply()
                startActivity(Intent(this@TelaPrincipalActivity, MainActivity::class.java))
                finish()
                return@launch
            }

            // Redirecionamento inicial unificado para todos os cargos
            if (savedInstanceState == null) {
                supportFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment()) // ✅ único destino
                    .commit()
            }
        }
    }


}

package com.example.appsenkaspi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appsenkaspi.Converters.Cargo
import com.example.appsenkaspi.databinding.ActivityTelaPrincipalBinding
import kotlinx.coroutines.launch

class TelaPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTelaPrincipalBinding
    private lateinit var funcionarioViewModel: FuncionarioViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout primeiro
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica login via SharedPreferences
        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val funcionarioId = prefs.getInt("funcionarioId", -1)
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

            if (savedInstanceState == null) {
                supportFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                val fragmentInicial = when (funcionario.cargo) {
                    Cargo.APOIO -> TelaAtividadesApoioFragment()
                    else -> HomeFragment()
                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragmentInicial)
                    .commit()
            }
        }
    }

}

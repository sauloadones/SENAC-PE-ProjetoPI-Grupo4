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
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
      }
    }


    criarCanalDeNotificacao() // ✅ ESSENCIAL


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

      if (savedInstanceState == null) {
        supportFragmentManager.popBackStack(
          null,
          androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager.beginTransaction()
          .replace(R.id.main_container, HomeFragment())
          .commit()
      }

      val requisicaoId = intent.getIntExtra("requisicao_id", -1)
      val abrirNotificacoes = intent.getBooleanExtra("abrir_notificacoes", false)

      if (abrirNotificacoes && requisicaoId != -1) {
        val fragment = DetalheNotificacaoFragment().apply {
          arguments = Bundle().apply {
            putInt("requisicaoId", requisicaoId)
          }
        }

        supportFragmentManager.beginTransaction()
          .replace(R.id.main_container, fragment)
          .addToBackStack(null)
          .commit()
      } else if (abrirNotificacoes) {
        supportFragmentManager.beginTransaction()
          .replace(R.id.main_container, NotificacaoFragment())
          .addToBackStack(null)
          .commit()
      }
    }
  }
  private fun criarCanalDeNotificacao() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      val canal = android.app.NotificationChannel(
        "prazo_channel", // ID usado na notificação
        "Notificações de Prazo", // Nome visível para o usuário
        android.app.NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Alertas de atividades próximas do prazo"
      }

      val manager = getSystemService(android.app.NotificationManager::class.java)
      manager.createNotificationChannel(canal)
    }
  }
}

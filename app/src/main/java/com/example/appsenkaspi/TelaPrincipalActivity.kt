package com.example.appsenkaspi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appsenkaspi.databinding.ActivityTelaPrincipalBinding

class TelaPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTelaPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            // Tela inicial = HomeFragment (ou seja, Home da navegação)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment())  // <- carrega o conteúdo principal aqui
                .commit()

            // O BottomNavFragment já está fixo no layout activity_tela_principal.xml
            // Então não precisa adicionar o BottomNavFragment por código
        }
    }
}

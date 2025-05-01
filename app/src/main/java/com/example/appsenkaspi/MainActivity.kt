package com.example.appsenkaspi

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define a cor da status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.graycont)

        // Deixa os ícones brancos (ideal para fundo escuro)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // Inicializa o botão e define o clique para abrir o popup
        val botaoLogin = findViewById<Button>(R.id.loginButtonPrincipal)
        botaoLogin.setOnClickListener {
            LoginDialogFragment().show(supportFragmentManager, "loginPopup")
        }
    }
}

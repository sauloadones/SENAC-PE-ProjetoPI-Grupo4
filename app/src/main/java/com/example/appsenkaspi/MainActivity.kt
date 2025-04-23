package com.example.appsenkaspi

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa o botão e define o clique para abrir o popup
        val botaoLogin = findViewById<Button>(R.id.loginButtonPrincipal)
        botaoLogin.setOnClickListener {
            LoginDialogFragment().show(supportFragmentManager, "loginPopup")
        }
    }
}

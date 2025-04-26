package com.example.appsenkaspi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appsenkaspi.databinding.ActivityTelaPrincipalBinding

class TelaPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.bottom_nav_container, BottomNavFragment())
                .commit()
        }
    }
}



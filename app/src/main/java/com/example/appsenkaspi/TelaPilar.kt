package com.example.appsenkaspi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TelaPilarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_tela_pilar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TelaPilarFragment())
                .commit()
        }
    }
}

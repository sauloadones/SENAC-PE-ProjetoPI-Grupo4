package com.example.appsenkaspi.ui.pilar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appsenkaspi.R
import com.example.appsenkaspi.ui.pilar.TelaPilarFragment

class TelaPilarActivity : AppCompatActivity() {

    private var funcionarioLogadoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_tela_pilar)

        // Pega o ID do Funcionário que logou
        funcionarioLogadoId = intent.getIntExtra("funcionarioId", -1)

        if (savedInstanceState == null) {
            val fragment = TelaPilarFragment().apply {
                arguments = Bundle().apply {
                    putInt("funcionarioId", funcionarioLogadoId)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}

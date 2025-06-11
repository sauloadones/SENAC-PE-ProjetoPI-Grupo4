package com.example.appsenkaspi.ui.perfil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appsenkaspi.R
import com.example.appsenkaspi.data.local.database.AppDatabase
import kotlinx.coroutines.launch

class DetalhesFragment : Fragment() {

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_detalhes, container, false)

    val prefs = requireContext().getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    val funcionarioId = prefs.getInt("funcionario_id", -1)

    val cargoTextView = view.findViewById<TextView>(R.id.textCargo)
    val emailTextView = view.findViewById<TextView>(R.id.textEmail)
    val telefoneTextView = view.findViewById<TextView>(R.id.textTelefone)

    lifecycleScope.launch {
      val dao = AppDatabase.Companion.getDatabase(requireContext()).funcionarioDao()
      val funcionario = dao.getFuncionarioById(funcionarioId)
      funcionario?.let {
        val nomeCargoFormatado = it.cargo.name.lowercase().replaceFirstChar { ch -> ch.uppercase() }

        cargoTextView.text = "$nomeCargoFormatado(a) na empresa SENAC"
        emailTextView.text = it.email
        telefoneTextView.text = it.numeroTel // Substitua pelo campo de telefone quando disponível
      }
    }

    return view
  }
}

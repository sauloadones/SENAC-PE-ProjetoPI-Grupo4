package com.example.appsenkaspi

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.appsenkaspi.R

fun Fragment.configurarBotaoVoltar(view: View) {
    val botao = view.findViewById<ImageView>(R.id.IconVoltar)
    botao?.setOnClickListener {
        parentFragmentManager.popBackStack()
    }
}
fun configurarNotificacaoBadge(
  rootView: View,
  lifecycleOwner: androidx.lifecycle.LifecycleOwner,
  fragmentManager: FragmentManager,
  funcionarioId: Int,
  cargo: Cargo,
  viewModel: NotificacaoViewModel
) {
  val badgeView = rootView.findViewById<TextView>(R.id.notificationBadge)
  val notificationIcon = rootView.findViewById<ImageView>(R.id.notificationIcon)

  // Atualiza badge com base no tipo de usuário
  if (cargo == Cargo.COORDENADOR) {
    viewModel.getQuantidadePendentesParaCoordenador()
      .observe(lifecycleOwner) { quantidade ->
        Log.d("NOTIF_COORD", "Quantas pendentes: $quantidade")
        badgeView.visibility = if (quantidade > 0) View.VISIBLE else View.GONE
        badgeView.text = if (quantidade > 9) "9+" else quantidade.toString()
      }
  } else {
    viewModel.getQuantidadeNaoVistas(funcionarioId)
      .observe(lifecycleOwner) { quantidade ->
        badgeView.visibility = if (quantidade > 0) View.VISIBLE else View.GONE
        badgeView.text = if (quantidade > 9) "9+" else quantidade.toString()
      }
  }

  // Ação ao clicar no sino
  notificationIcon?.setOnClickListener {
    fragmentManager.beginTransaction()
      .replace(R.id.main_container, NotificacaoFragment())
      .addToBackStack(null)
      .commit()
  }
}


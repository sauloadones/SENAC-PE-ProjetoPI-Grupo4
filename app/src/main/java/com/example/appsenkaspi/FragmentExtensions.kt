package com.example.appsenkaspi

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
fun configurarBotaoSino(view: View, fragmentManager: FragmentManager, funcionarioId: Int, viewModel: NotificacaoViewModel) {
  val sino = view.findViewById<ImageView>(R.id.notificationIcon)
  val badge = view.findViewById<View>(R.id.notificationBadge)

  // Observe a quantidade de notificações não vistas
  viewModel.getQuantidadeNaoVistas(funcionarioId).observeForever { quantidade ->
    badge?.visibility = if (quantidade > 0) View.VISIBLE else View.GONE
  }

  sino?.setOnClickListener {
    fragmentManager.beginTransaction()
      .replace(R.id.main_container, NotificacaoFragment())
      .addToBackStack(null)
      .commit()
  }
}

fun inicializarNotificacaoBadge(
  rootView: View,
  lifecycleOwner: androidx.lifecycle.LifecycleOwner,
  fragmentManager: FragmentManager,
  funcionarioId: Int,
  viewModel: NotificacaoViewModel
) {
  val badgeView = rootView.findViewById<TextView>(R.id.notificationBadge)
  val notificationIcon = rootView.findViewById<ImageView>(R.id.notificationIcon)

  viewModel.getQuantidadeNaoVistas(funcionarioId).observe(lifecycleOwner) { quantidade ->
    if (quantidade > 0) {
      badgeView?.visibility = View.VISIBLE
      badgeView?.text = if (quantidade > 9) "9+" else quantidade.toString()
    } else {
      badgeView?.visibility = View.GONE
    }
  }

  notificationIcon?.setOnClickListener {
    fragmentManager.beginTransaction()
      .replace(R.id.main_container, NotificacaoFragment())
      .addToBackStack(null)
      .commit()
  }
}



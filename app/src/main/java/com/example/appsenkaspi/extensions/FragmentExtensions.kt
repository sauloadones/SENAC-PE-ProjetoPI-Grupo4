package com.example.appsenkaspi.extensions

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.example.appsenkaspi.data.local.enums.Cargo
import com.example.appsenkaspi.ui.notificacao.NotificacaoFragment
import com.example.appsenkaspi.viewmodel.NotificacaoViewModel
import com.example.appsenkaspi.R

fun Fragment.configurarBotaoVoltar(view: View) {
    val botao = view.findViewById<ImageView>(R.id.IconVoltar)
    botao?.setOnClickListener {
        parentFragmentManager.popBackStack()
    }
}
fun configurarNotificacaoBadge(
    rootView: View,
    lifecycleOwner: LifecycleOwner,
    fragmentManager: FragmentManager,
    funcionarioId: Int,
    cargo: Cargo,
    viewModel: NotificacaoViewModel
) {
  val badgeView = rootView.findViewById<TextView>(R.id.notificationBadge)
  val notificationIcon = rootView.findViewById<ImageView>(R.id.notificationIcon)

  if (cargo == Cargo.COORDENADOR) {
    val pendentesLiveData = viewModel.getQuantidadePendentesParaCoordenador()
    val prazoLiveData = viewModel.getQuantidadeNotificacoesPrazoNaoVistas(funcionarioId)

    pendentesLiveData.observe(lifecycleOwner) { pendentes ->
      prazoLiveData.observe(lifecycleOwner) { prazo ->
        val total = pendentes + prazo
        Log.d("NOTIF_BADGE", "Pendentes: $pendentes | Prazo: $prazo | Total: $total")
        badgeView.visibility = if (total > 0) View.VISIBLE else View.GONE
        badgeView.text = if (total > 9) "9+" else total.toString()
      }
    }
  } else {
    viewModel.getQuantidadeNaoVistas(funcionarioId)
      .observe(lifecycleOwner) { quantidade ->
        badgeView.visibility = if (quantidade > 0) View.VISIBLE else View.GONE
        badgeView.text = if (quantidade > 9) "9+" else quantidade.toString()
      }
  }

  notificationIcon?.setOnClickListener {
    fragmentManager.beginTransaction()
      .setCustomAnimations(
        R.anim.pull_fade_in,     // animação de entrada
        R.anim.pull_fade_out,    // animação de saída
        R.anim.pull_fade_in,     // animação de retorno ao voltar
        R.anim.pull_fade_out     // animação de saída ao voltar
      )
      .replace(R.id.main_container, NotificacaoFragment())
      .addToBackStack(null)
      .commit()
  }
}

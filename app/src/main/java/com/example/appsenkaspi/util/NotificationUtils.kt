package com.example.appsenkaspi.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.appsenkaspi.R

object NotificationUtils {

  const val CANAL_SISTEMA = "sistema_notificacoes"
  const val CANAL_PRAZO = "canal_prazo"

  private const val NOME_CANAL_SISTEMA = "Notificações do App Senkaspi"
  private const val NOME_CANAL_PRAZO = "Notificações de Prazo"

  fun criarCanais(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val canalSistema = NotificationChannel(
          CANAL_SISTEMA,
          NOME_CANAL_SISTEMA,
          NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notificações automáticas do sistema (conclusão, alteração, etc.)"
        enableLights(true)
        lightColor = Color.BLUE
        enableVibration(true)
      }

      val canalPrazo = NotificationChannel(
          CANAL_PRAZO,
          NOME_CANAL_PRAZO,
          NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notificações sobre prazos de atividades"
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
      }

      manager.createNotificationChannel(canalSistema)
      manager.createNotificationChannel(canalPrazo)
    }
  }

  fun mostrarNotificacao(
      context: Context,
      titulo: String,
      mensagem: String,
      id: Int,
      canalId: String = CANAL_SISTEMA
  ) {
    // Verifica permissão (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    val icon = when (canalId) {
      CANAL_PRAZO -> R.drawable.ic_calendar
      else -> R.drawable.logo
    }

    val builder = NotificationCompat.Builder(context, canalId)
      .setSmallIcon(icon)
      .setContentTitle(titulo)
      .setContentText(mensagem)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(id, builder.build())
  }
}

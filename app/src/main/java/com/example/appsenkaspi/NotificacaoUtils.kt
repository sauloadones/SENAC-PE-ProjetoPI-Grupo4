package com.example.appsenkaspi

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun enviarNotificacaoDePrazo(context: Context, requisicaoId: Int, titulo: String, mensagem: String) {
  // ✅ Se Android 13+ e não tem permissão, não envia
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
    context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    != PackageManager.PERMISSION_GRANTED) {
    return
  }

  val intent = Intent(context, TelaPrincipalActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    putExtra("abrir_notificacoes", true)
    putExtra("requisicao_id", requisicaoId)
  }

  val pendingIntent = PendingIntent.getActivity(
    context, requisicaoId,
    intent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
  )

  val builder = NotificationCompat.Builder(context, "prazo_channel")
    .setSmallIcon(R.drawable.ic_notification)
    .setContentTitle("Atividade se aproxima do prazo: $titulo")
    .setContentText(mensagem)
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .setContentIntent(pendingIntent)
    .setAutoCancel(true)

  NotificationManagerCompat.from(context).notify(requisicaoId, builder.build())
}


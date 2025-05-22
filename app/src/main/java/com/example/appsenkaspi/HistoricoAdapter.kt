package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.databinding.ItemHistoricoBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoricoAdapter(
  private val onClickPilar: (PilarEntity) -> Unit
) : ListAdapter<PilarEntity, HistoricoAdapter.PilarViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilarViewHolder {
    val binding = ItemHistoricoBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
    return PilarViewHolder(binding)
  }

  override fun onBindViewHolder(holder: PilarViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class PilarViewHolder(
    private val binding: ItemHistoricoBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pilar: PilarEntity) {
      binding.nomePilar.text = pilar.nome

      val context = binding.root.context
      val statusText: String
      val statusColor: Int
      val statusIcon: Int

      when (pilar.status) {
        StatusPilar.CONCLUIDO -> {
          statusText = "Concluído"
          statusColor = ContextCompat.getColor(context, R.color.verde_concluido)
          statusIcon = R.drawable.ic_check_circle
        }
        StatusPilar.VENCIDO -> {
          statusText = "Vencido"
          statusColor = ContextCompat.getColor(context, R.color.vermelho_expirado)
          statusIcon = R.drawable.ic_warning
        }
        StatusPilar.EXCLUIDO -> {
          statusText = "Deletado"
          statusColor = ContextCompat.getColor(context, R.color.cinza_deletado)
          statusIcon = R.drawable.ic_delete
        }
        else -> {
          statusText = "Desc"
          statusColor = ContextCompat.getColor(context, R.color.cinza_claro)
          statusIcon = R.drawable.ic_unknown
        }
      }

      binding.statusPilar.text = statusText
      binding.statusPilar.setTextColor(statusColor)
      binding.iconeStatus.setImageResource(statusIcon)
      binding.iconeStatus.setColorFilter(statusColor)

      // ✅ Exibir data de conclusão, não data de prazo
      // ✅ Exibir data condicional
      val formatador = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
      val dataExibida = when (pilar.status) {
        StatusPilar.CONCLUIDO -> pilar.dataConclusao
        StatusPilar.EXCLUIDO -> pilar.dataExcluido
        else -> pilar.dataPrazo
      }
      val dataTexto = dataExibida?.let { formatador.format(it) } ?: "--"
      binding.dataPilar.text = when (pilar.status) {
        StatusPilar.CONCLUIDO -> "Concluído em: $dataTexto"
        StatusPilar.VENCIDO -> "Prazo: $dataTexto"
        StatusPilar.EXCLUIDO -> "Removido em: $dataTexto"
        else -> "--"
      }



      binding.root.setOnClickListener {
        onClickPilar(pilar)
      }
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PilarEntity>() {
      override fun areItemsTheSame(oldItem: PilarEntity, newItem: PilarEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: PilarEntity, newItem: PilarEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}

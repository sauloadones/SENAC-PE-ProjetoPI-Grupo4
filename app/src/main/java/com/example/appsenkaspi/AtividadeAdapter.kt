package com.example.appsenkaspi

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appsenkaspi.Converters.PrioridadeAtividade
import com.example.appsenkaspi.Converters.StatusAtividade
import java.text.SimpleDateFormat
import java.util.*

class AtividadeAdapter(
    // ✅ Callback de clique no card
    private val onItemClick: (AtividadeComFuncionarios) -> Unit
) : ListAdapter<AtividadeComFuncionarios, AtividadeAdapter.ViewHolder>(AtividadeComFuncionariosDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo = view.findViewById<TextView>(R.id.textTitulo)
        val statusBolinha = view.findViewById<View>(R.id.statusBolinha)
        val prioridadeQuadrado = view.findViewById<View>(R.id.viewPrioridade)
        val containerResponsaveis = view.findViewById<LinearLayout>(R.id.containerResponsaveis)
        val containerData = view.findViewById<LinearLayout>(R.id.containerData)
        val textData = view.findViewById<TextView>(R.id.textData)
        val iconClock = view.findViewById<ImageView>(R.id.iconClock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_atividade, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val atividadeComFuncionarios = getItem(position)
        val atividade = atividadeComFuncionarios.atividade

        holder.titulo.text = atividade.nome

        // 🎨 Cor do quadrado de prioridade
        val corPrioridade = when (atividade.prioridade) {
            PrioridadeAtividade.BAIXA -> Color.parseColor("#2ECC40")
            PrioridadeAtividade.MEDIA -> Color.parseColor("#F1C40F")
            PrioridadeAtividade.ALTA -> Color.parseColor("#E74C3C")
        }
        holder.prioridadeQuadrado.setBackgroundColor(corPrioridade)

        // 🔵 Cor da bolinha de status
        val corStatus = when (atividade.status) {
            StatusAtividade.CONCLUIDA -> Color.parseColor("#2ECC40")
            StatusAtividade.EM_ANDAMENTO -> Color.parseColor("#F1C40F")
            else -> Color.TRANSPARENT
        }
        val drawable = holder.statusBolinha.background as? GradientDrawable
        drawable?.setColor(corStatus)

        // 📅 Datas e cores baseadas no prazo
        val diasRestantes = diasParaPrazo(atividade.dataPrazo)
        val dataFormatada = SimpleDateFormat("dd 'de' MMM, HH:mm", Locale("pt", "BR")).format(atividade.dataPrazo)
        holder.textData.text = dataFormatada

        val corFundoData = when {
            atividade.status == StatusAtividade.CONCLUIDA -> Color.parseColor("#2ECC40")
            diasRestantes <= 3 -> Color.parseColor("#E74C3C")
            diasRestantes <= 7 -> Color.parseColor("#F1C40F")
            else -> Color.parseColor("#CCCCCC")
        }

        val fundoDataDrawable = GradientDrawable().apply {
            cornerRadius = 24f
            setColor(corFundoData)
        }
        holder.containerData.background = fundoDataDrawable
        holder.textData.setTextColor(Color.BLACK)
        holder.iconClock.setColorFilter(Color.BLACK)

        // 👥 Fotos dos responsáveis
        holder.containerResponsaveis.removeAllViews()
        val dimensao = holder.itemView.resources.getDimensionPixelSize(R.dimen.tamanho_foto_responsavel)

        atividadeComFuncionarios.funcionarios.forEach { funcionario ->
            val imageView = de.hdodenhof.circleimageview.CircleImageView(holder.itemView.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(dimensao, dimensao).apply {
                    marginEnd = 12
                }
                borderWidth = 2
                borderColor = Color.WHITE
            }
            Glide.with(imageView.context)
                .load(funcionario.fotoPerfil)
                .placeholder(R.drawable.ic_person)
                .into(imageView)
            holder.containerResponsaveis.addView(imageView)
        }

        // ✅ Clique no card redireciona para o fragmento de detalhes
        holder.itemView.setOnClickListener {
            onItemClick(atividadeComFuncionarios)
        }
    }

    private fun diasParaPrazo(dataPrazo: Date): Int {
        val hoje = Calendar.getInstance().time
        val diff = dataPrazo.time - hoje.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}

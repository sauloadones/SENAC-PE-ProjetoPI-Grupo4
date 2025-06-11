package com.example.appsenkaspi.ui.perfil

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appsenkaspi.data.local.enums.PrioridadeAtividade
import com.example.appsenkaspi.R
import com.example.appsenkaspi.data.local.enums.StatusAtividade
import com.example.appsenkaspi.domain.model.AtividadeComFuncionarios
import com.example.appsenkaspi.ui.atividade.AtividadeComFuncionariosDiffCallback
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AtividadePerfilAdapter(
    private val onItemClick: (AtividadeComFuncionarios) -> Unit
) : ListAdapter<AtividadeComFuncionarios, AtividadePerfilAdapter.ViewHolder>(
    AtividadeComFuncionariosDiffCallback()
) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.textTituloPerfil)
        val statusBolinha: View = view.findViewById(R.id.statusBolinhaPerfil)
        val prioridadeQuadrado: View = view.findViewById(R.id.viewPrioridade)
        val containerResponsaveis: LinearLayout = view.findViewById(R.id.containerResponsaveisPerfil)
        val containerData: LinearLayout = view.findViewById(R.id.containerDataPerfil)
        val textData: TextView = view.findViewById(R.id.textDataPerfil)
        val iconClock: ImageView = view.findViewById(R.id.iconClockPerfil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_atividade_perfil, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val atividadeComFuncionarios = getItem(position)
        val atividade = atividadeComFuncionarios.atividade

        holder.titulo.text = atividade.nome

        val contexto = holder.itemView.context

        // 🎨 Cor da prioridade com drawable em camada
        val corPrioridade = when (atividade.prioridade) {
            PrioridadeAtividade.BAIXA -> Color.parseColor("#2ECC40")
            PrioridadeAtividade.MEDIA -> Color.parseColor("#F1C40F")
            PrioridadeAtividade.ALTA -> Color.parseColor("#E74C3C")
        }

        val layerDrawable = AppCompatResources.getDrawable(contexto, R.drawable.bg_prioridade_layer) as? LayerDrawable
        layerDrawable?.let {
            val fundo = it.findDrawableByLayerId(R.id.fundo)
            val wrappedFundo = DrawableCompat.wrap(fundo.mutate())
            DrawableCompat.setTint(wrappedFundo, corPrioridade)
            holder.prioridadeQuadrado.background = it
        }

        // 🔵 Cor da bolinha de status
        val corStatus = when (atividade.status) {
            StatusAtividade.CONCLUIDA -> Color.parseColor("#2ECC40")
            StatusAtividade.EM_ANDAMENTO -> Color.parseColor("#F1C40F")
            else -> Color.TRANSPARENT
        }
        val drawableStatus = holder.statusBolinha.background as? GradientDrawable
        drawableStatus?.setColor(corStatus)

        // 📅 Data e cor do fundo
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
            val imageView = CircleImageView(contexto).apply {
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

        // ✅ Clique no card
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

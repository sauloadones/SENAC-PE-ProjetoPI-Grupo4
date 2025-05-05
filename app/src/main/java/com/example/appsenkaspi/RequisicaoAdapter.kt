package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.databinding.ItemRequisicaoBinding

class   RequisicaoAdapter(
    private val lista: List<RequisicaoEntity>,
    private val onItemClick: (RequisicaoEntity) -> Unit
) : RecyclerView.Adapter<RequisicaoAdapter.RequisicaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequisicaoViewHolder {
        val binding = ItemRequisicaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequisicaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequisicaoViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    inner class RequisicaoViewHolder(private val binding: ItemRequisicaoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(requisicao: RequisicaoEntity) {
            binding.textTipo.text = requisicao.tipo.name
            binding.textStatus.text = requisicao.status.name
            binding.textData.text = requisicao.dataCriacao.toString()

            // Esses campos podem ser preenchidos dinamicamente com nomes, se necessário
            binding.textPilar.text = "Pilar ID: ${requisicao.pilarId}"
            binding.textSolicitante.text = "Solicitante ID: ${requisicao.solicitanteId}"

            binding.root.setOnClickListener {
                onItemClick(requisicao)
            }
        }
    }
}

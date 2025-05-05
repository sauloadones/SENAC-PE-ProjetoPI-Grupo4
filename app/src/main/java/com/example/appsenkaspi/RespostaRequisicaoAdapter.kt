package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.databinding.ItemRespostaBinding

class RespostaRequisicaoAdapter(
    private val lista: List<RequisicaoEntity>
) : RecyclerView.Adapter<RespostaRequisicaoAdapter.RespostaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RespostaViewHolder {
        val binding = ItemRespostaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RespostaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RespostaViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    inner class RespostaViewHolder(private val binding: ItemRespostaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(requisicao: RequisicaoEntity) {
            binding.textTipo.text = requisicao.tipo.name
            binding.textStatus.text = requisicao.status.name
            binding.textCoordenador.text = "Coordenador ID: ${requisicao.coordenadorId}"
            binding.textDataResposta.text = requisicao.dataResposta?.toString() ?: "Sem data"
        }
    }
}

package com.example.appsenkaspi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appsenkaspi.databinding.ItemResponsavelBinding

class ResponsavelAdapter(
    private val funcionarios: List<FuncionarioEntity>,
    private val selecionados: MutableList<FuncionarioEntity>,
    private val onSelecionadosAtualizados: (List<FuncionarioEntity>) -> Unit
) : RecyclerView.Adapter<ResponsavelAdapter.ResponsavelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponsavelViewHolder {
        val binding = ItemResponsavelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ResponsavelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResponsavelViewHolder, position: Int) {
        holder.bind(funcionarios[position])
    }

    override fun getItemCount(): Int = funcionarios.size

    inner class ResponsavelViewHolder(
        private val binding: ItemResponsavelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(funcionario: FuncionarioEntity) {
            val contexto = binding.root.context

            binding.textNome.text = funcionario.nomeCompleto
            binding.textEmail.text = funcionario.email

            Glide.with(contexto)
                .load(funcionario.fotoPerfil)
                .placeholder(R.drawable.ic_person)
                .into(binding.imagemPerfil)

            // Estilo visual conforme seleção
            val isSelecionado = selecionados.contains(funcionario)
            binding.root.setCardBackgroundColor(
                if (isSelecionado) ContextCompat.getColor(contexto, R.color.selecionado)
                else ContextCompat.getColor(contexto, R.color.nao_selecionado)
            )

            binding.root.setOnClickListener {
                if (isSelecionado) {
                    selecionados.remove(funcionario)
                } else {
                    selecionados.add(funcionario)
                }
                notifyItemChanged(adapterPosition)
                onSelecionadosAtualizados(selecionados)
            }
        }
    }
}

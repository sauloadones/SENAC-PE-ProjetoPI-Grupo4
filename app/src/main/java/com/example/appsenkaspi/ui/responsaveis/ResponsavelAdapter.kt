package com.example.appsenkaspi.ui.responsaveis

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appsenkaspi.R
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity
import com.example.appsenkaspi.databinding.ItemResponsavelBinding

class ResponsavelAdapter(
    funcionarios: List<FuncionarioEntity>,
    private val selecionados: MutableList<FuncionarioEntity>,
    private val onSelecionadosAtualizados: (List<FuncionarioEntity>) -> Unit,
    private val modoLeitura: Boolean = false
) : RecyclerView.Adapter<ResponsavelAdapter.ResponsavelViewHolder>() {

    private var listaFuncionarios: MutableList<FuncionarioEntity> = funcionarios.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponsavelViewHolder {
        val binding = ItemResponsavelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ResponsavelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResponsavelViewHolder, position: Int) {
        holder.bind(listaFuncionarios[position])
    }

    override fun getItemCount(): Int = listaFuncionarios.size

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

            if (!modoLeitura) {
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
                    this@ResponsavelAdapter.notifyItemChanged(adapterPosition)
                    onSelecionadosAtualizados.invoke(selecionados)
                }
            } else {
                binding.root.setCardBackgroundColor(Color.TRANSPARENT)
                binding.root.setOnClickListener(null)
            }
        }
    }

    fun atualizarLista(novaLista: List<FuncionarioEntity>) {
        listaFuncionarios.clear()
        listaFuncionarios.addAll(novaLista)
        notifyDataSetChanged()
    }
}

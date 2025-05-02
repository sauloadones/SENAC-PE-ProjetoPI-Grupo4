package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FuncionarioMultiSelecaoAdapter(
    private val selecionados: MutableList<FuncionarioEntity>
) : ListAdapter<FuncionarioEntity, FuncionarioMultiSelecaoAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkFuncionario)
        private val imagePerfil: ImageView = itemView.findViewById(R.id.imageViewFotoPerfil)

        fun bind(funcionario: FuncionarioEntity) {
            checkBox.text = funcionario.nomeCompleto
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = selecionados.contains(funcionario)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selecionados.add(funcionario)
                else selecionados.remove(funcionario)
            }

            if (!funcionario.fotoPerfil.isNullOrEmpty()) {
                Glide.with(itemView).load(funcionario.fotoPerfil).circleCrop().into(imagePerfil)
                imagePerfil.visibility = View.VISIBLE
            } else {
                imagePerfil.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funcionario_selecao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FuncionarioEntity>() {
        override fun areItemsTheSame(a: FuncionarioEntity, b: FuncionarioEntity) = a.id == b.id
        override fun areContentsTheSame(a: FuncionarioEntity, b: FuncionarioEntity) = a == b
    }
}

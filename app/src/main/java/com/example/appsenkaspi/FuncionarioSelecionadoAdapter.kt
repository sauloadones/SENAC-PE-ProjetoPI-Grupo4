package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FuncionarioSelecionadoAdapter(
    private val listaFuncionarios: List<FuncionarioEntity>
) : RecyclerView.Adapter<FuncionarioSelecionadoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagePerfil: ImageView =
            itemView.findViewById(R.id.imageViewFotoPerfil)
        private val textNome: TextView =
            itemView.findViewById(R.id.textViewNomeFuncionario)

        fun bind(funcionario: FuncionarioEntity) {
            textNome.text = funcionario.nomeCompleto
            // Se você tiver uma URL ou recurso de foto:
            // Glide.with(itemView).load(funcionario.urlFoto).circleCrop().into(imagePerfil)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.box_perfil, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaFuncionarios[position])
    }

    override fun getItemCount(): Int = listaFuncionarios.size
}

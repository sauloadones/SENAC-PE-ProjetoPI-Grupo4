package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FuncionarioAdapter(
    private val listaFuncionarios: List<FuncionarioEntity>
) : RecyclerView.Adapter<FuncionarioAdapter.FuncionarioViewHolder>() {

    inner class FuncionarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeTextView: TextView = view.findViewById(R.id.textViewNomeFuncionario)
        val fotoImageView: ImageView = view.findViewById(R.id.imageViewFotoPerfil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuncionarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.box_perfil, parent, false) // Layout ainda serve, depois pode mudar o nome se quiser
        return FuncionarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuncionarioViewHolder, position: Int) {
        val funcionario = listaFuncionarios[position]
        holder.nomeTextView.text = funcionario.nomeCompleto
        holder.fotoImageView.setImageResource(R.drawable.ic_perfil_exemplo) // 📷 Foto padrão
    }

    override fun getItemCount() = listaFuncionarios.size
}

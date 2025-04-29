package com.example.appsenkaspi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FuncionarioMultiSelecaoAdapter(
    private val selecionados: MutableList<FuncionarioEntity>
) : ListAdapter<FuncionarioEntity, FuncionarioMultiSelecaoAdapter.Holder>(Diff()) {

    inner class Holder(item: View) : RecyclerView.ViewHolder(item) {
        private val chk: CheckBox = item.findViewById(R.id.checkFuncionario)
        fun bind(f: FuncionarioEntity) {
            chk.text = f.nomeCompleto
            chk.isChecked = selecionados.contains(f)
            chk.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selecionados.add(f)
                else selecionados.remove(f)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funcionario_selecao, parent, false)
        return Holder(v)
    }
    override fun onBindViewHolder(holder: Holder, pos: Int) {
        holder.bind(getItem(pos))
    }

    class Diff : DiffUtil.ItemCallback<FuncionarioEntity>() {
        override fun areItemsTheSame(a: FuncionarioEntity, b: FuncionarioEntity) =
            a.id == b.id
        override fun areContentsTheSame(a: FuncionarioEntity, b: FuncionarioEntity) =
            a == b
    }
}

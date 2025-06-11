package com.example.appsenkaspi.ui.checklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.data.local.entity.ChecklistItemEntity
import com.example.appsenkaspi.R

class ChecklistAdapter(
    private var itens: List<ChecklistItemEntity>,
    private val onItemCheckedChanged: (ChecklistItemEntity, Boolean) -> Unit,
    private val onDeleteItem: (ChecklistItemEntity) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    inner class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkboxItem)
        val btnExcluir: ImageView = itemView.findViewById(R.id.btnExcluirItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)

    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = itens[position]

        holder.checkbox.text = item.descricao
        holder.checkbox.isChecked = item.concluido

        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onItemCheckedChanged(item.copy(concluido = isChecked), isChecked)
        }

        holder.btnExcluir.setOnClickListener {
            onDeleteItem(item)
        }
    }

    override fun getItemCount(): Int = itens.size

    fun atualizarLista(novosItens: List<ChecklistItemEntity>) {
        this.itens = novosItens
        notifyDataSetChanged()
    }
}

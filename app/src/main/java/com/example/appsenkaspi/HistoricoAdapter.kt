package com.example.appsenkaspi

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.databinding.DialogDetalhesRelatorioBinding
import android.widget.Toast
import com.example.appsenkaspi.utils.abrirArquivo

class HistoricoAdapter(
    private val lista: List<HistoricoRelatorio>
) : RecyclerView.Adapter<HistoricoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.txtTitulo)
        val data: TextView = view.findViewById(R.id.txtData)
        val setinha: View = view.findViewById(R.id.btnDetalhes) // Supondo que a setinha seja um botão na view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico_relatorio, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.titulo.text = item.titulo
        holder.data.text = item.data

        // Listener para a setinha (ou para o item inteiro, se preferir)
        holder.setinha.setOnClickListener {
            mostrarDialogDetalhes(holder.itemView, item)
        }
    }

    private fun mostrarDialogDetalhes(view: View, item: HistoricoRelatorio) {
        // Usando ViewBinding para o diálogo (crie o layout dialog_detalhes_relatorio.xml para isso)
        val dialogBinding = DialogDetalhesRelatorioBinding.inflate(LayoutInflater.from(view.context))

        dialogBinding.txtTitulo.text = item.titulo
        dialogBinding.txtPilar.text = item.pilarNome ?: "—"
        dialogBinding.txtData.text = item.data

        val dialog = AlertDialog.Builder(view.context)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnBaixar.setOnClickListener {
            // Aqui você pode adicionar a lógica para baixar o relatório novamente
            // Se tiver a path salva, você pode abrir, ou disparar nova geração
            // Por enquanto só um toast:
            Toast.makeText(view.context, "Baixando relatório novamente...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialogBinding.btnAbrir.setOnClickListener {
            if (item.caminhoArquivo != null) {
                abrirArquivo(view.context, item.caminhoArquivo)
            } else {
                Toast.makeText(view.context, "Arquivo não disponível para abrir.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}

package com.example.appsenkaspi

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.databinding.DialogDetalhesRelatorioBinding
import com.example.appsenkaspi.utils.getMimeType
import java.io.File
import android.net.Uri

class HistoricoAdapter(
    private val lista: List<HistoricoRelatorio>
) : RecyclerView.Adapter<HistoricoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.txtTitulo)
        val data: TextView = view.findViewById(R.id.txtData)
        val setinha: View = view.findViewById(R.id.btnDetalhes)
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

        holder.setinha.setOnClickListener {
            mostrarDialogDetalhes(holder.itemView, item)
        }
    }

    private fun mostrarDialogDetalhes(view: View, item: HistoricoRelatorio) {
        val dialogBinding = DialogDetalhesRelatorioBinding.inflate(LayoutInflater.from(view.context))

        dialogBinding.txtTitulo.text = item.titulo
        dialogBinding.txtPilar.text = item.pilarNome ?: "—"
        dialogBinding.txtData.text = item.data

        val dialog = AlertDialog.Builder(view.context)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnFechar.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnBaixar.setOnClickListener {
            Toast.makeText(view.context, "Baixando relatório novamente...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialogBinding.btnAbrir.setOnClickListener {
            val caminho = item.caminhoArquivo

            if (caminho != null) {
                try {
                    val uri = Uri.parse(caminho)

                    val mimeType = view.context.contentResolver.getType(uri) ?: "*/*"

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    view.context.startActivity(Intent.createChooser(intent, "Abrir com"))
                } catch (e: Exception) {
                    Toast.makeText(view.context, "Erro ao abrir o arquivo.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(view.context, "Arquivo não disponível.", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialog.show()
    }
}


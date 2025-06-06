package com.example.appsenkaspi

import com.example.appsenkaspi.utils.baixarArquivo
import android.app.AlertDialog
import android.content.Intent
import android.content.Context
import androidx.lifecycle.lifecycleScope
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.launch
import com.example.appsenkaspi.utils.baixarArquivoComOkHttp
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.databinding.DialogDetalhesRelatorioBinding
import com.example.appsenkaspi.utils.getMimeType
import android.net.Uri
import com.example.appsenkaspi.utils.baixarArquivoComOkHttp

class RelatorioAdapter(
    private val context: Context,
    private val usuario: String,
    private val lista: MutableList<HistoricoRelatorio>,
    private val lifecycleOwner: androidx.lifecycle.LifecycleOwner
) : RecyclerView.Adapter<RelatorioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.txtTitulo)
        val data: TextView = view.findViewById(R.id.txtData)
        val tipoArquivo: TextView = view.findViewById(R.id.txtTipoArquivo)
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

        holder.titulo.text = item.titulo ?: "Título não disponível"
        holder.data.text = item.data ?: "Data não disponível"
        holder.tipoArquivo.text = when (item.tipoArquivo?.lowercase()) {
            "pdf" -> "PDF"
            "xlsx" -> "Excel"
            "docx" -> "Word"
            else -> "Desconhecido"
        }

        holder.setinha.setOnClickListener {
            mostrarDialogDetalhes(holder.itemView, item, position)
        }
    }

    private fun removerItem(position: Int) {
        lista.removeAt(position)
        notifyItemRemoved(position)
        HistoricoStorage.salvar(context, lista, usuario)
    }

    private fun mostrarDialogDetalhes(view: View, item: HistoricoRelatorio, position: Int) {
        val dialogBinding = DialogDetalhesRelatorioBinding.inflate(LayoutInflater.from(view.context))

        dialogBinding.txtTitulo.text = item.titulo ?: "—"
        dialogBinding.txtPilar.text = item.pilarNome ?: "—"
        dialogBinding.txtData.text = item.data ?: "—"
        dialogBinding.txtTipoArquivo.text = when (item.tipoArquivo?.lowercase()) {
            "pdf" -> "PDF"
            "xlsx" -> "Excel"
            "docx" -> "Word"
            else -> "Desconhecido"
        }

        val dialog = AlertDialog.Builder(view.context)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
        )

        dialogBinding.btnFechar.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnBaixar.setOnClickListener {
            val url = item.urlArquivo
            if (!url.isNullOrEmpty()) {
                val extensao = when (item.tipoArquivo?.lowercase()) {
                    "pdf" -> ".pdf"
                    "xlsx" -> ".xlsx"
                    "docx" -> ".docx"
                    else -> ".pdf"
                }
                val nomeArquivo = item.titulo?.replace(" ", "_") + extensao
                val mimeType = getMimeType(nomeArquivo)

                println("DEBUG: URL para reinstalar: $url")

                lifecycleOwner.lifecycleScope.launch {
                    val caminho = baixarArquivoComOkHttp(view.context, url, nomeArquivo, mimeType)
                    if (caminho != null) {
                        Toast.makeText(view.context, "Arquivo reinstalado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(view.context, "Falha ao reinstalar o arquivo.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(view.context, "URL do arquivo indisponível.", Toast.LENGTH_SHORT).show()
            }
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

        dialogBinding.txtApagar.setOnClickListener {
            AlertDialog.Builder(view.context)
                .setTitle("Confirmar exclusão")
                .setMessage("Deseja realmente apagar este item do histórico?")
                .setPositiveButton("Sim") { _, _ ->
                    removerItem(position) // remove e atualiza storage
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

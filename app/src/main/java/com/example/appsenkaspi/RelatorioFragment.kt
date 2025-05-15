package com.example.appsenkaspi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appsenkaspi.databinding.FragmentRelatorioBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class RelatorioFragment : Fragment() {
    private var _binding: FragmentRelatorioBinding? = null
    private val binding get() = _binding!!
    private val pilarViewModel: PilarViewModel by viewModels()
    private val apiService = RetrofitClient.apiService

    private var listaPilares: List<PilarEntity> = emptyList()
    private lateinit var pilarAdapter: ArrayAdapter<String>
    private var isGeral: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelatorioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkStoragePermission()
        carregarPilaresNoSpinner()

        binding.textSelecionarPilar.visibility = View.GONE
        binding.spinnerPilares.visibility = View.GONE

        binding.btnRelatorioGeral.setOnClickListener {
            isGeral = true
            binding.textSelecionarPilar.visibility = View.GONE
            binding.spinnerPilares.visibility = View.GONE
        }

        binding.btnRelatorioPorPilar.setOnClickListener {
            isGeral = false
            binding.textSelecionarPilar.visibility = View.VISIBLE
            binding.spinnerPilares.visibility = View.VISIBLE
        }

        binding.btnPdf.setOnClickListener {
            gerarRelatorio("pdf")
        }

        binding.btnWord.setOnClickListener {
            gerarRelatorio("word")
        }

        binding.btnExcel.setOnClickListener {
            gerarRelatorio("excel")
        }
    }

    private fun carregarPilaresNoSpinner() {
        lifecycleScope.launch {
            val pilares = withContext(Dispatchers.IO) {
                pilarViewModel.getTodosPilares()
            }

            listaPilares = pilares

            val nomes = pilares.map { it.nome }

            pilarAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nomes)
            pilarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerPilares.adapter = pilarAdapter
        }
    }

    private fun gerarRelatorio(tipo: String) {
        lifecycleScope.launch {
            try {
                val pilaresSelecionados: List<PilarEntity> = if (isGeral) {
                    withContext(Dispatchers.IO) { pilarViewModel.getTodosPilares() }
                } else {
                    val posicao = binding.spinnerPilares.selectedItemPosition
                    listOf(listaPilares[posicao])
                }

                val listaDTO = pilaresSelecionados.map {
                    PilarDTO(
                        nome = it.nome,
                        descricao = it.descricao,
                        dataInicio = it.dataInicio.toString(),
                        dataPrazo = it.dataPrazo.toString(),
                        status = it.status.name,
                        criadoPor = it.criadoPor.toString()
                    )
                }

                val request = RelatorioRequest(pilares = listaDTO)

                val response: Response<ResponseBody>? = when (tipo) {
                    "pdf" -> apiService.gerarPdf(request)
                    "word" -> apiService.gerarWord(request)
                    "excel" -> apiService.gerarExcel(request)
                    else -> null
                }

                response?.let {
                    if (it.isSuccessful) {
                        salvarArquivo(it.body(), "relatorio.$tipo")
                    } else {
                        Toast.makeText(requireContext(), "Erro ao gerar relatório", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Erro inesperado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarArquivo(body: ResponseBody?, nomeArquivo: String) {
        if (body == null) return

        val path = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val arquivo = File(path, nomeArquivo)

        try {
            val inputStream: InputStream = body.byteStream()
            val outputStream = FileOutputStream(arquivo)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            Toast.makeText(requireContext(), "Arquivo salvo em ${arquivo.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Erro ao salvar arquivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

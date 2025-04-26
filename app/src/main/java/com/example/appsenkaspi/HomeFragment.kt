package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var pilaresRecyclerView: RecyclerView
    private lateinit var addButtonWrapper: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa o RecyclerView e o botão +
        pilaresRecyclerView = view.findViewById(R.id.pilaresRecyclerView)
        addButtonWrapper = view.findViewById(R.id.addButtonWrapper)

        // Configura o RecyclerView com 2 colunas
        pilaresRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Ação ao clicar no botão de adicionar pilar
        addButtonWrapper.setOnClickListener {
            Toast.makeText(requireContext(), "Adicionar novo pilar", Toast.LENGTH_SHORT).show()
            // Aqui você pode abrir uma tela para criação de pilar
        }
    }

    // Dados de exemplo

}

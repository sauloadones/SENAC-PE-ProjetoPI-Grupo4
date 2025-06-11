package com.example.appsenkaspi.ui.pilar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appsenkaspi.R
import com.example.appsenkaspi.ui.pilar.TelaPilarFragment
import com.example.appsenkaspi.ui.perfil.PilarAdapter
import com.example.appsenkaspi.viewmodel.PilarViewModel

class RecyclerViewPilar : Fragment() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var adapter: PilarAdapter

  private val pilarViewModel: PilarViewModel by activityViewModels()

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_recycler_view_pilar, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    recyclerView = view.findViewById(R.id.recyclerViewPilares)
    adapter = PilarAdapter(
        onClickPilar = { pilar -> abrirTelaDoPilar(pilar.id) },
        verificarSubpilares = { id -> pilarViewModel.temSubpilaresDireto(id) }
    )

    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter

    pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { pilares ->
      adapter.submitList(pilares)
    }
  }

  private fun abrirTelaDoPilar(pilarId: Int) {
    parentFragmentManager.beginTransaction()
      .replace(
        R.id.main_container,
        TelaPilarFragment().apply {
          arguments = Bundle().apply { putInt("pilarId", pilarId) }
        }
      )
      .addToBackStack(null)
      .commit()
  }
}

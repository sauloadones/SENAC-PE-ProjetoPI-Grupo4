package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appsenkaspi.databinding.FragmentBottomNavBinding

class BottomNavFragment : Fragment() {

    private lateinit var binding: FragmentBottomNavBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomNavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()

        // Inicializa com Home selecionado
        updateIndicator(R.id.nav_home)
        navigateTo(HomeFragment())
    }

    private fun setupClickListeners() {
        binding.navRelatorio.setOnClickListener {
            updateIndicator(R.id.nav_relatorio)
            navigateTo(RelatorioFragment())
        }
        binding.navDashboard.setOnClickListener {
            updateIndicator(R.id.nav_dashboard)
            navigateTo(DashboardFragment())
        }
        binding.navHome.setOnClickListener {
            updateIndicator(R.id.nav_home)
            navigateTo(HomeFragment())
        }
        binding.navPerfil.setOnClickListener {
            updateIndicator(R.id.nav_perfil)
            navigateTo(PerfilFragment())
        }
    }

    private fun updateIndicator(selectedId: Int) {
        binding.indicatorRelatorio.visibility = if (selectedId == R.id.nav_relatorio) View.VISIBLE else View.INVISIBLE
        binding.indicatorDashboard.visibility = if (selectedId == R.id.nav_dashboard) View.VISIBLE else View.INVISIBLE
        binding.indicatorHome.visibility = if (selectedId == R.id.nav_home) View.VISIBLE else View.INVISIBLE
        binding.indicatorPerfil.visibility = if (selectedId == R.id.nav_perfil) View.VISIBLE else View.INVISIBLE

        // ATUALIZA SELEÇÃO VISUAL
        binding.iconRelatorio.isSelected = (selectedId == R.id.nav_relatorio)
        binding.iconDashboard.isSelected = (selectedId == R.id.nav_dashboard)
        binding.iconHome.isSelected = (selectedId == R.id.nav_home)
        binding.iconPerfil.isSelected = (selectedId == R.id.nav_perfil)
    }


    private fun navigateTo(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }
}

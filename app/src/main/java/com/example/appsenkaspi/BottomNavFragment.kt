package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.appsenkaspi.databinding.FragmentBottomNavBinding

class BottomNavFragment : Fragment() {

    private lateinit var binding: FragmentBottomNavBinding
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomNavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            funcionario?.let {
                updateIndicator(R.id.nav_home)

                val atual = parentFragmentManager.findFragmentById(R.id.main_container)
                if (atual == null) {
                    navigateTo(HomeFragment()) // ✅ Direciona todos para o mesmo fragmento
                }
            }
        }
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
            navigateTo(HomeFragment()) // ✅ Apoio e demais cargos voltam para o padrão
        }

        binding.navPerfil.setOnClickListener {
            updateIndicator(R.id.nav_perfil)
            navigateTo(PerfilFragment())
        }
    }

    private fun updateIndicator(selectedId: Int) {
        val itemViews = mapOf(
            R.id.nav_relatorio to binding.navRelatorio,
            R.id.nav_dashboard to binding.navDashboard,
            R.id.nav_home to binding.navHome,
            R.id.nav_perfil to binding.navPerfil
        )

        val icons = mapOf(
            R.id.nav_relatorio to binding.iconRelatorio,
            R.id.nav_dashboard to binding.iconDashboard,
            R.id.nav_home to binding.iconHome,
            R.id.nav_perfil to binding.iconPerfil
        )

        val selectedView = itemViews[selectedId] ?: return
        val indicator = binding.indicatorSlider

        selectedView.post {
            val targetX = selectedView.left + selectedView.width / 2 - indicator.width / 2
            indicator.animate()
                .translationX(targetX.toFloat())
                .setDuration(250)
                .start()
        }

        icons.forEach { (id, icon) ->
            icon.isSelected = (id == selectedId)
        }
    }

    private fun navigateTo(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }
}

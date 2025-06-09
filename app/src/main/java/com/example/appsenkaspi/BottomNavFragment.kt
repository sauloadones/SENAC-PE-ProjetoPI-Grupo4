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
    private var currentSelectedId: Int = R.id.nav_home

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
                    navigateTo(HomeFragment(), R.id.nav_home)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.navHome.setOnClickListener {
            updateIndicator(R.id.nav_home)
            navigateTo(HomeFragment(), R.id.nav_home)
        }

        binding.navDashboard.setOnClickListener {
            updateIndicator(R.id.nav_dashboard)
            navigateTo(DashboardFragment(), R.id.nav_dashboard)
        }

        binding.navRelatorio.setOnClickListener {
            updateIndicator(R.id.nav_relatorio)
            navigateTo(RelatorioFragment(), R.id.nav_relatorio)
        }

        binding.navPerfil.setOnClickListener {
            updateIndicator(R.id.nav_perfil)
            navigateTo(PerfilFragment(), R.id.nav_perfil)
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

    private fun navigateTo(fragment: Fragment, selectedId: Int) {
        if (selectedId == currentSelectedId) return

        val orderMap = mapOf(
            R.id.nav_home to 0,
            R.id.nav_dashboard to 1,
            R.id.nav_relatorio to 2,
            R.id.nav_perfil to 3
        )

        val currentIndex = orderMap[currentSelectedId] ?: 0
        val newIndex = orderMap[selectedId] ?: 0

        val (enterAnim, exitAnim, popEnterAnim, popExitAnim) = if (newIndex > currentIndex) {
            listOf(
                R.anim.slide_fade_in_right,
                R.anim.slide_fade_out_left,
                R.anim.slide_fade_in_left,
                R.anim.slide_fade_out_right
            )
        } else {
            listOf(
                R.anim.slide_fade_in_left,
                R.anim.slide_fade_out_right,
                R.anim.slide_fade_in_right,
                R.anim.slide_fade_out_left
            )
        }

        currentSelectedId = selectedId

        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
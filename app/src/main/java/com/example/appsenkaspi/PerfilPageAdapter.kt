package com.example.appsenkaspi

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PerfilPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // 2 abas: Detalhes e Trabalhos

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetalhesFragment()  // Exibe a aba "Detalhes"
            1 -> TrabalhosFragment()  // Exibe a aba "Trabalhos"
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}

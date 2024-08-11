package com.ai.guildmasterapp.ui.hamburgerMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.databinding.FragmentHamburgerBinding

class HamburgerMenuFragment : Fragment() {

    private var _binding: FragmentHamburgerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hamburgerViewModel =
            ViewModelProvider(this).get(HamburgerMenuViewModel::class.java)

        _binding = FragmentHamburgerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHamburger
        hamburgerViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
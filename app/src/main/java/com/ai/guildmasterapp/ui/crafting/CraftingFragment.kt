package com.ai.guildmasterapp.ui.crafting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.databinding.FragmentCraftingBinding
import com.ai.guildmasterapp.databinding.FragmentCraftingBinding.inflate


class CraftingFragment : Fragment() {
    private var _binding: FragmentCraftingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val craftingViewModel =
            ViewModelProvider(this).get(CraftingViewModel::class.java)

        _binding = inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCrafting
        craftingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
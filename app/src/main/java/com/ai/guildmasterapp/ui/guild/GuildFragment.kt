package com.ai.guildmasterapp.ui.guild

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ai.guildmasterapp.GuildInfo
import com.ai.guildmasterapp.R
import com.ai.guildmasterapp.databinding.FragmentGuildBinding


class GuildFragment : Fragment() {
    private var _binding: FragmentGuildBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var guildViewModel: GuildViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using FragmentGuildBinding
        _binding = FragmentGuildBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        guildViewModel = ViewModelProvider(this).get(GuildViewModel::class.java)

        guildViewModel.guildInfo.observe(viewLifecycleOwner, Observer {guildInfo ->
            updateUI(guildInfo)
        })

        guildViewModel.getGuildInfo()
    }


    private fun updateUI(guildInfo: GuildInfo?){

        guildInfo?.let {
            binding.guildName.text = it.name
            binding.guildTag.text = "[${it.tag}]"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
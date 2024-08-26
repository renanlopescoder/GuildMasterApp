package com.ai.guildmasterapp.ui.guild

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ai.guildmasterapp.GuildInfo
import com.ai.guildmasterapp.databinding.FragmentGuildBinding
import coil.load
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.launch
import com.ai.guildmasterapp.api.GuildWars2Api


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
            // binding.guildEmblemView.load("https://api.guildwars2.com/v2/emblem/${it.emblem.foreground_id}/${it.emblem.background_id}")


            // Loads emblem image from API call
            lifecycleScope.launch {
                var background:  List<String>? = GuildWars2Api().getFallbackEmblemLayerBackground().layers
                var foreground: List<String>? = GuildWars2Api().getFallbackEmblemLayerForeground().layers

                /* background = GuildWars2Api().fetchEmblemLayer(it.emblem.background_id, "backgrounds") { layers -> layers }
                foreground = GuildWars2Api().fetchEmblemLayer(it.emblem.foreground_id, "foregrounds") { layers -> layers } */

                val emblemLayers = (background ?: emptyList()) + (foreground ?: emptyList())
                val emblemDrawable = combineEmblemLayers(emblemLayers)

                binding.guildEmblemView.setImageDrawable(emblemDrawable)
            }

        }
    }



    // Uses Coli to load image from the API EmblemLayer fetch and cast the image as a BitmapDrawable
    private suspend fun loadBitmapFromUrl(url: String): Bitmap? {
        val loader = ImageLoader(requireContext()) // Image loader variable from Coli library
        val request = ImageRequest.Builder(requireContext()) // Initializes request from url string parameter
            .data(url)
            .allowHardware(false) // ensures bitmap is not hardware-accelerated
            .build()

        // If the loader is successful, it will return a drawable object.
        val result = (loader.execute(request) as? SuccessResult)?.drawable

        // Returns the drawable object cast as a bitmap
        return (result as? BitmapDrawable)?.bitmap
    }



    // Combines the multiple layers of the emblem into a BitmapDrawable object
    private suspend fun combineEmblemLayers(layers: List<String>): Drawable? {
        if (layers.isEmpty()) return null // If the list of URLs are empty

        // Load the first image layer to initialize the size of the canvas
        val tempBitmap = loadBitmapFromUrl(layers[0])?.copy(Bitmap.Config.ARGB_8888, true) ?: return null
        // Initialize an empty bitmap with the same size as the first bitmap from the emblem
        val bitmap = Bitmap.createBitmap(tempBitmap.width, tempBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap) // Create a canvas based on the new bitmap

        layers.forEach { url ->
            val layerBitmap = loadBitmapFromUrl(url)
            layerBitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, null)
            }
        }

        return BitmapDrawable(resources, bitmap)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}








/* private fun generateEmblemDrawable(emblem: Emblem): Drawable {

    }


    private fun getColor(colorID: Int): Int {


        return ContextCompat.getColor(requireContext(), colorID)
    } */
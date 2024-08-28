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
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.ai.guildmasterapp.GlobalState
import kotlinx.coroutines.launch


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

        super.onViewCreated(view, savedInstanceState) // Calls parent class function

        guildViewModel = ViewModelProvider(this).get(GuildViewModel::class.java)

        guildViewModel.guildInfo.observe(viewLifecycleOwner, Observer {guildInfo ->

            // Creates a coroutine
            lifecycleScope.launch {
                updateGuildInfo(guildInfo)

            }
        })
        // Fetches API data.
        guildViewModel.getGuildInfo()
    }


    private suspend fun updateGuildInfo(guildInfo: GuildInfo?){

        guildInfo?.let {
            // Assigns the fragment_guild.xml fragment Ids
            binding.guildName.text = it.guild_name
            binding.guildTag.text = "[${it.tag}]"

            // Initializes the background & foreground layers
            var background: List<String>? = null
            var foreground: List<String>? = null
            background = guildViewModel.getEmblemLayers(it.emblem.background_id, "backgrounds")
            foreground = guildViewModel.getEmblemLayers(it.emblem.foreground_id, "foregrounds")

            // Loads emblem image from API call
            lifecycleScope.launch {

                val emblemLayers = (background ?: emptyList()) + (foreground ?: emptyList())

                val emblemDrawable = makeEmblem(emblemLayers)

                binding.guildEmblemView.setImageDrawable(emblemDrawable)

                val testIds = guildViewModel.getItemIds()

                val index = testIds.first()

                val testItemDetails = guildViewModel.getItemDetails(index)

                val exampleItemIcon = makeEmblem(listOf(testItemDetails.icon))

                binding.testItemIcon.setImageDrawable(exampleItemIcon)
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
    private suspend fun makeEmblem(layers: List<String>): Drawable? {
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


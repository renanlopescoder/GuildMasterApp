package com.ai.guildmasterapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.api.CharacterDetail

class CharacterSelectionActivity : AppCompatActivity() {

    private lateinit var charactersInLayout: LinearLayout
    private lateinit var selectedCharacterOverlay: RelativeLayout
    private lateinit var selectedCharacterImage: ImageView
    private lateinit var selectedCharacterName: TextView
    private lateinit var selectedCharacterLevel: TextView
    private lateinit var selectedCharacterRace: TextView
    private lateinit var selectedCharacterGender: TextView
    private lateinit var selectedCharacterProfession: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_selection)

        charactersInLayout = findViewById(R.id.characters)
        selectedCharacterOverlay = findViewById(R.id.selected_character_overlay)
        selectedCharacterImage = findViewById(R.id.selected_character_image)
        selectedCharacterName = findViewById(R.id.selected_character_name)
        selectedCharacterLevel = findViewById(R.id.selected_character_level)
        selectedCharacterRace = findViewById(R.id.selected_character_race)
        selectedCharacterGender = findViewById(R.id.selected_character_gender)
        selectedCharacterProfession = findViewById(R.id.selected_character_profession)

        // FETCH API DATA
        val api = GuildWars2Api()
        api.getCharacters { characters ->
            runOnUiThread {
                if (characters != null) {
                    populateCharacters(characters)
                }
            }
        }
    }

    private fun populateCharacters(characters: List<String>) {
        characters.forEach { characterName ->
            val cardView = CardView(this).apply {
                radius = 16f
                layoutParams = LinearLayout.LayoutParams(
                    200,
                    280
                ).apply {
                    setMargins(16, 0, 16, 0) // Space between the card icons
                }
                elevation = 8f
            }

            val characterLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val selectionIcon = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    200, 200
                ).apply {
                    setMargins(0, 8, 0, 0)
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                }
                // Default is Inactive Icon
                setImageResource(R.drawable.select_inactive_icon)
                contentDescription = "Selection icon Inactive"
            }

            val textView = TextView(this).apply {
                text = characterName
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 12f
                setPadding(0, 8, 0, 0)
            }

            characterLayout.addView(selectionIcon)
            characterLayout.addView(textView)

            cardView.addView(characterLayout)

            cardView.setOnClickListener {
                if (selectionIcon.tag == "inactive" || selectionIcon.tag == null) {
                    selectionIcon.setImageResource(R.drawable.select_active_icon)
                    selectionIcon.tag = "active"
                } else {
                    selectionIcon.setImageResource(R.drawable.select_inactive_icon)
                    selectionIcon.tag = "inactive"
                }

                showSelectedCharacter(characterName, R.drawable.select_active_icon)
            }

            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
            cardView.startAnimation(animation)

            charactersInLayout.addView(cardView)
        }
    }


    private fun showSelectedCharacter(characterName: String, characterImageResId: Int) {

        selectedCharacterName.text = characterName
        selectedCharacterOverlay.visibility = View.VISIBLE
        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        selectedCharacterOverlay.startAnimation(fadeInAnimation)

        val api = GuildWars2Api()
        api.getCharacterDetails(characterName) { characterDetails ->
            runOnUiThread {
                if (characterDetails != null) {
                    val detail = characterDetails[0]
                    selectedCharacterLevel.text = "Level: ${detail.level}"
                    selectedCharacterRace.text = "Race: ${detail.race}"
                    selectedCharacterGender.text = "Gender: ${detail.gender}"
                    selectedCharacterProfession.text = "Profession: ${detail.profession}"
                    val resourceName = "character_${detail.race}-${detail.profession}-${detail.gender}".lowercase().replace("-", "_")

                    val resId = resources.getIdentifier(resourceName, "drawable", packageName)

                    // Set the image resource dynamically
                    if (resId != 0) {
                        selectedCharacterImage.setImageResource(resId)
                    } else {
                        selectedCharacterImage.setImageResource(characterImageResId)
                    }
                } else {
                    Toast.makeText(this, "Failed to load character details", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Hide overlay character on click
        selectedCharacterOverlay.setOnClickListener {
            val fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
            selectedCharacterOverlay.startAnimation(fadeOutAnimation)
            selectedCharacterOverlay.visibility = View.GONE
        }
    }
}

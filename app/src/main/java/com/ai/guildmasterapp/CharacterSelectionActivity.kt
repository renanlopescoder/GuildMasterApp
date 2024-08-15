package com.ai.guildmasterapp



import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ai.guildmasterapp.api.GuildWars2Api
import com.ai.guildmasterapp.MainActivity

class CharacterSelectionActivity : AppCompatActivity() {

    private lateinit var charactersInLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_selection)

        charactersInLayout = findViewById(R.id.characters)

        // FETCH API DATA
        val api = GuildWars2Api()
        api.getCharacters { characters ->
            runOnUiThread {
                if (characters != null) {
                    populateCharacters(characters)
                } else {
                    Toast.makeText(this, "Failed to load characters", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun populateCharacters(characters: List<String>) {
        characters.forEach { characterName ->
            val button = Button(this).apply {
                text = characterName
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    Toast.makeText(this@CharacterSelectionActivity, "Selected: $characterName", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CharacterSelectionActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            charactersInLayout.addView(button)
        }
    }
}

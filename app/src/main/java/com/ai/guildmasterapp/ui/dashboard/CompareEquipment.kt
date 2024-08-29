package com.ai.guildmasterapp.ui.dashboard

import android.annotation.SuppressLint
import android.icu.number.Precision
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.ai.guildmasterapp.R

class CompareEquipment : AppCompatActivity() {

    private lateinit var helmImage: ImageView
    private lateinit var shoulderImage: ImageView
    private lateinit var chestImage: ImageView
    private lateinit var handsImage: ImageView
    private lateinit var legsImage: ImageView
    private lateinit var feetImage: ImageView
    // Text Views
    private lateinit var powerAttribute: TextView
    private lateinit var toughnessAttribute: TextView
    private lateinit var precisionAttribute: TextView
    private lateinit var vitalityAttribute: TextView
    private lateinit var concentrationAttribute: TextView
    private lateinit var conditionDamageAttribute: TextView
    private lateinit var expertiseAttribute: TextView
    private lateinit var ferocityAttribute: TextView
    private lateinit var healingPowerAttribute: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_builder)
        supportActionBar?.hide()

        //Back button, when pressed it will close the activity
        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { returnToMain() }


        helmImage = findViewById(R.id.equipment_helm)
        shoulderImage = findViewById(R.id.equipment_shoulders)
        chestImage = findViewById(R.id.equipment_chest)
        handsImage = findViewById(R.id.equipment_hands)
        legsImage = findViewById(R.id.equipment_legs)
        feetImage = findViewById(R.id.equipment_feet)

        powerAttribute = findViewById(R.id.power_attr)
        toughnessAttribute = findViewById(R.id.toughness_attr)
        precisionAttribute = findViewById(R.id.precision_attr)
        vitalityAttribute = findViewById(R.id.vitality_attr)
        concentrationAttribute = findViewById(R.id.concentration_attr)
        conditionDamageAttribute = findViewById(R.id.condition_attr)
        expertiseAttribute = findViewById(R.id.expertise_attr)
        ferocityAttribute = findViewById(R.id.ferocity_attr)
        healingPowerAttribute = findViewById(R.id.healing_power_attr)

        setPower(59)
        setToughness(37)
        setPrecision(41)
        setVitality(37)
        setConcentration(0)
        setConditionDamage(0)
        setExpertise(0)
        setFerocity(0)
        setHealingPower(0)
    }
    private fun returnToMain() {
        finish()
    }

    private fun setPower(newPower: Int){
        val powerString = getString(R.string.power_attribute) + newPower.toString()
        powerAttribute.text = powerString
    }

    private fun setToughness(newToughness: Int){
        val toString = getString(R.string.toughness_attribute) + newToughness.toString()
        toughnessAttribute.text = toString
    }

    private fun setPrecision(newPrecision: Int){
        val precisionString = getString(R.string.precision_attribute) + newPrecision.toString()
        precisionAttribute.text = precisionString
    }

    private fun setVitality(newVitality: Int){
        val vitalityString = getString(R.string.vitality_attribute) + newVitality.toString()
        vitalityAttribute.text = vitalityString
    }

    private fun setConcentration(newConcentration: Int){
        val concentrationString = getString(R.string.concentration_attribute) + newConcentration.toString()
        concentrationAttribute.text = concentrationString
    }

    private fun setConditionDamage(newConditionDamage: Int){
        val conditionString = getString(R.string.condition_attribute) + newConditionDamage.toString()
        conditionDamageAttribute.text = conditionString
    }

    private fun setExpertise(newExpertise: Int){
        val expertiseString = getString(R.string.expertise_attribute) + newExpertise.toString()
        expertiseAttribute.text = expertiseString
    }

    private fun setFerocity(newFerocity: Int){
        val ferocityString = getString(R.string.ferocity_attribute) + newFerocity.toString()
        ferocityAttribute.text = ferocityString
    }

    private fun setHealingPower(newHealingPower: Int){
        val healingPowerString = getString(R.string.healing_power_attribute) + newHealingPower.toString()
        healingPowerAttribute.text = healingPowerString
    }

}
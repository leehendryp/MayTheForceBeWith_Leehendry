package com.leehendryp.maytheforcebewithleehendry.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leehendryp.maytheforcebewithleehendry.databinding.ActivityDetailsBinding
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character.Companion.CHARACTER

class DetailsActivity : AppCompatActivity() {
    private var character: Character? = null

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCharacterFromBundle()
        populateFields()
    }

    private fun getCharacterFromBundle() {
        intent.extras?.let { character = it.getSerializable(CHARACTER) as Character }
    }

    private fun populateFields() {
        character?.let {
            binding.apply {
                textDetailsCharacterName.text = it.name
                textCharacterHeightDetails.text = it.height
                textCharacterMassDetails.text = it.mass
                textCharacterHairColorDetails.text = it.hairColor
                textCharacterSkinColorDetails.text = it.skinColor
                textCharacterEyeColorDetails.text = it.eyeColor
                textCharacterBirthYearDetails.text = it.birthYear
                textCharacterGenderDetails.text = it.gender
            }
        }
    }
}

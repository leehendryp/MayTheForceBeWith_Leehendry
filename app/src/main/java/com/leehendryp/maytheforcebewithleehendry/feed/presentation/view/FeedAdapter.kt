package com.leehendryp.maytheforcebewithleehendry.feed.presentation.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leehendryp.maytheforcebewithleehendry.R
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import kotlinx.android.synthetic.main.item_character.view.item_text_character_name

class FeedAdapter(
    private var characters: MutableSet<Character>
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)

        return ViewHolder(view)
    }

    fun update(characters: Set<Character>) {
        this.characters.addAll(characters)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = characters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(characters.toList()[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.item_text_character_name

        fun bind(character: Character) {
            name.text = character.name
        }
    }
}
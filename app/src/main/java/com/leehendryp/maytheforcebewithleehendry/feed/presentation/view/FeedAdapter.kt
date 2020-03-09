package com.leehendryp.maytheforcebewithleehendry.feed.presentation.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.leehendryp.maytheforcebewithleehendry.R
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import kotlinx.android.synthetic.main.item_feed.view.item_text_character_name

class FeedAdapter(
    private val characters: MutableSet<Character>,
    private val onClick: (Character) -> Unit
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = characters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCharacter = characters.toList()[position]
        holder.bind(currentCharacter)
        holder.itemView.setOnClickListener { onClick(currentCharacter) }
    }

    fun update(characters: Set<Character>) {
        this.characters.addAll(characters)
        notifyDataSetChanged()
    }

    fun clearList() {
        characters.clear()
        notifyItemRangeRemoved(0, characters.size)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.item_text_character_name

        fun bind(character: Character) {
            name.text = character.name
        }
    }
}
package com.example.marekmeyer.lyricquiz_kotlin.activities

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.marekmeyer.lyricquiz_kotlin.R


import com.example.marekmeyer.lyricquiz_kotlin.models.Artist

import kotlinx.android.synthetic.main.fragment_top.view.*

/**
 * [RecyclerView.Adapter] that can display an [Artist] item
 */
class TopArtistsRecyclerViewAdapter(private val values: List<Artist>)
    : RecyclerView.Adapter<TopArtistsRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_top, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = values[position]
        holder.itemNumberView.text = (position + 1).toString()
        holder.itemTextView.text = artist.name

        with(holder.mView) {
            tag = artist
        }
    }

    override fun getItemCount(): Int = values.size

    // A view in this context is each row
    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val itemNumberView: TextView = mView.itemNumber
        val itemTextView: TextView = mView.itemName

        override fun toString(): String {
            return super.toString() + " '" + itemTextView.text + "'"
        }
    }
}

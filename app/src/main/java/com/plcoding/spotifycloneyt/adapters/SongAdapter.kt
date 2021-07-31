package com.plcoding.spotifycloneyt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.data.entities.Song
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.swipe_item.view.*
import kotlinx.android.synthetic.main.swipe_item.view.tvPrimary
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager,

) : BaseSongAdapter(R.layout.list_item) {

    override val differ=AsyncListDiffer(this,diffCallBack)


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song=songs[position]
        holder.itemView.apply {
            tvSecondary.text=song.title
            tvSecondary.text=song.subtittle
            glide.load(song.imageUrl).into(ivItemImage)

            setOnClickListener {
                onItemClickListener?.let {click->
                 click(song)
                }
            }
        }
    }


}
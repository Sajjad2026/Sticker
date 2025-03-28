package com.nova.pack.stickers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nova.pack.stickers.R

class StickerAdapter(
    private val stickerList: List<String?>,
    private val onItemClick: (String?) -> Unit
) : RecyclerView.Adapter<StickerAdapter.StickerViewHolder>() {

    inner class StickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.stickerImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sticker_item_layout, parent, false)
        return StickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        val stickerPath = stickerList.getOrNull(position)
        Glide.with(holder.itemView.context)
            .load(if (stickerPath.isNullOrEmpty()) R.drawable.ic_sticker_holder else "file://$stickerPath")
            .centerCrop()
            .placeholder(R.drawable.ic_sticker_holder)
            .error(R.drawable.ic_sticker_holder)
            .into(holder.imageView)

        holder.itemView.setOnClickListener { onItemClick(stickerPath) }
    }

    override fun getItemCount(): Int = stickerList.size
}

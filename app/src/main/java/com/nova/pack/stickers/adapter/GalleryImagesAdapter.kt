package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nova.pack.stickers.databinding.ItemImageLytBinding
import com.nova.pack.stickers.listener.onGalleryImageClickListener


class GalleryImagesAdapter(private var context: Context, listener: onGalleryImageClickListener): RecyclerView.Adapter<GalleryImagesAdapter.ViewHolder>(){
    private var imageList = ArrayList<String>()
    private var listener: onGalleryImageClickListener = listener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemImageLytBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    suspend fun setList(imageList : List<String>){
        this.imageList.addAll(imageList)
        notifyDataSetChanged()
    }


    class ViewHolder(val binding : ItemImageLytBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            Glide.with(imgPlaceholder).load("file:${imageList[position]}")
                .apply(
                    RequestOptions()
                        .fitCenter()
                )
                .into(imgPlaceholder)

            holder.itemView.setOnClickListener {
               listener.onImageClick("file:${imageList[position]}")
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
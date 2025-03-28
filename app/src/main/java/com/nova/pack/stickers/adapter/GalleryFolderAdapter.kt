package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nova.pack.stickers.databinding.ItemFolderLytBinding
import com.nova.pack.stickers.listener.onGalleryFolderClickListener
import com.nova.pack.stickers.model.ImagesModel

class GalleryFolderAdapter(private var context: Context,listener: onGalleryFolderClickListener): RecyclerView.Adapter<GalleryFolderAdapter.ViewHolder>(){
    private var folderList = ArrayList<ImagesModel>()
    private var listener: onGalleryFolderClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFolderLytBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(folderList : List<ImagesModel>){
        this.folderList = folderList as ArrayList<ImagesModel>
        notifyDataSetChanged()
    }


    class ViewHolder(val binding : ItemFolderLytBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            folderNameTxt.text = folderList[position].folder
            Glide.with(context).load("file:${folderList[position].imagePathList[0]}").into(folderImgHolder)
            imageCountTxt.text = folderList[position].imagePathList.size.toString()
            holder.itemView.setOnClickListener {
                listener.onClick(position)
            }
            }
        }

    override fun getItemCount(): Int {
        return folderList.size
    }

}
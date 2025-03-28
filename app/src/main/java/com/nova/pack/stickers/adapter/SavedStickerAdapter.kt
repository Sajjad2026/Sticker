package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nova.pack.stickers.databinding.HomeStickerItemBinding
import com.nova.pack.stickers.listener.OnMyStickerExportButtonClick
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView

class SavedStickerAdapter (private var context: Context, onStickerClickListener: onStickerPackClickListener,onMyStickerExportButtonClick: OnMyStickerExportButtonClick): RecyclerView.Adapter<SavedStickerAdapter.ViewHolder>(){
    private var stickerList = ArrayList<StickerPackView>()
    private var listener: onStickerPackClickListener = onStickerClickListener
    private var exportBtnListener: OnMyStickerExportButtonClick = onMyStickerExportButtonClick
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeStickerItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(stickerList : List<StickerPackView>){
        this.stickerList.clear()
        this.stickerList = stickerList as ArrayList<StickerPackView>
        notifyDataSetChanged()
    }


    class ViewHolder(val binding : HomeStickerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvPackStickerName.text = stickerList[position].identifier
            ivFirstSticker.visibility = View.VISIBLE
            ivSecondSticker.visibility = View.VISIBLE
            ivThirdSticker.visibility = View.VISIBLE
            ivFourSticker.visibility = View.VISIBLE

            if(stickerList[position].stickers.size == 1)
            {
                Glide.with(context).load(stickerList[position].stickers[0].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivFirstSticker)
                ivSecondSticker.visibility = View.INVISIBLE
                ivThirdSticker.visibility = View.INVISIBLE
                ivFourSticker.visibility = View.INVISIBLE
            }
            else if(stickerList[position].stickers.size == 2)
            {
                Glide.with(context).load(stickerList[position].stickers[0].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivFirstSticker)
                Glide.with(context).load(stickerList[position].stickers[1].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivSecondSticker)
                ivThirdSticker.visibility = View.INVISIBLE
                ivFourSticker.visibility = View.INVISIBLE
            }
            else if(stickerList[position].stickers.size == 3)
            {
                Glide.with(context).load(stickerList[position].stickers[0].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivFirstSticker)
                Glide.with(context).load(stickerList[position].stickers[1].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivSecondSticker)
                Glide.with(context).load(stickerList[position].stickers[2].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivThirdSticker)
                ivFourSticker.visibility = View.INVISIBLE
            }
            else if(stickerList[position].stickers.size >= 4)
            {
                Glide.with(context).load(stickerList[position].stickers[0].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivFirstSticker)
                Glide.with(context).load(stickerList[position].stickers[1].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivSecondSticker)
                Glide.with(context).load(stickerList[position].stickers[2].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivThirdSticker)
                Glide.with(context).load(stickerList[position].stickers[3].imageFile).transition(
                    DrawableTransitionOptions.withCrossFade())
                    .into(ivFourSticker)
            }
            seeAllBtn.setOnClickListener {
                listener.OnStickerClickListener(stickerList[position])
            }

            exportBtn.setOnClickListener {
                exportBtnListener.onClick(stickerList[position].identifier)
            }

            cardBg.setOnClickListener {
                listener.OnStickerClickListener(stickerList[position])
            }
            downloadCountText.visibility = View.INVISIBLE
            favouriteCountText.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return stickerList.size
    }

}
package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.NativeStickers_Detail_Activity_RecyclerView
import com.nova.pack.stickers.ads.InAppClass
import com.nova.pack.stickers.ads.NativeAdManager
import com.nova.pack.stickers.databinding.NativeAdBinding
import com.nova.pack.stickers.databinding.StickerItemLytBinding
import com.nova.pack.stickers.model.StickerView
import com.nova.pack.stickers.utils.SharedPreferenceHelper

class DetailsStickerAdapter(private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var stickerList = ArrayList<Any>() // Holds both StickerView and ads

    companion object {
        const val VIEW_TYPE_STICKER = 0
        const val VIEW_TYPE_AD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 3 &&NativeStickers_Detail_Activity_RecyclerView!=0) VIEW_TYPE_AD else VIEW_TYPE_STICKER
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_AD -> {
                val binding = NativeAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AdViewHolder(binding)
            }
            else -> StickerViewHolder(
                StickerItemLytBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<StickerView>) {
        stickerList.clear()
        stickerList.addAll(list)
        // Insert ad after the first row
        if (NativeStickers_Detail_Activity_RecyclerView!=0 &&  !SharedPreferenceHelper.getSession("mySession", false) && !InAppClass.isPurchase){
            if (list.isNotEmpty() && list.size > 3) {
                stickerList.add(3, "ad_placeholder")
            }
        }
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return stickerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StickerViewHolder -> {
                val sticker = stickerList[position] as StickerView
                holder.binding.apply {
                    Glide.with(context)
                        .load(sticker.imageFile)
                        .placeholder(R.drawable.ic_loading)
                        .into(stickerImage)
                }
                holder.itemView.setOnClickListener {
                    createAlertBox(position)
                }
            }
            is AdViewHolder -> {
                holder.bindNativeAd()
            }
        }
    }

    private fun createAlertBox(position: Int) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.open_sticker_lyt, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!(context as Activity).isFinishing) {
            dialog.show()
        }
        val btnCancel = view.findViewById<ImageView>(R.id.cancel_btn)
        val imageView = view.findViewById<ImageView>(R.id.imageview_sticker)
        val sticker = stickerList[position] as StickerView
        Glide.with(context).load(sticker.imageFile).placeholder(R.drawable.ic_loading).into(imageView)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    class StickerViewHolder(val binding: StickerItemLytBinding) : RecyclerView.ViewHolder(binding.root)

    inner class AdViewHolder(var binding:NativeAdBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindNativeAd() {
            if (!InAppClass.isPurchase) {
                    binding.nativeContainer.visibility = View.VISIBLE
                    if (NativeStickers_Detail_Activity_RecyclerView ==1){
                        NativeAdManager.nativeAdmob(context as AppCompatActivity, binding.nativeContainer, (context as AppCompatActivity).window, 1)
                    }else if (NativeStickers_Detail_Activity_RecyclerView ==2){
                        NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, (context as AppCompatActivity).window, "home_sticker","top", binding.linear)
                    }else if (NativeStickers_Detail_Activity_RecyclerView ==3){
                        NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, (context as AppCompatActivity).window, "home_sticker","bottom",binding.linear)
                    }else if (NativeStickers_Detail_Activity_RecyclerView ==4){
                        binding.nativeContainer.minimumHeight=context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
                        NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, (context as AppCompatActivity).window, "home_sticker","small",binding.linear)
                    }else if (NativeStickers_Detail_Activity_RecyclerView ==5){
                        NativeAdManager.nativeWithoutMedia(context as AppCompatActivity, binding.nativeContainer, (context as AppCompatActivity).window, "home_sticker","random",binding.linear)
                    }else{
                        Log.d("sfkljsf","adapter")
                        binding.nativeContainer.visibility = View.GONE
                    }
            }else{
                binding.nativeContainer.visibility = View.GONE
            }
        }
    }
}

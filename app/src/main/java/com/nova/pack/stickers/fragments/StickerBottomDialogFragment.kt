package com.nova.pack.stickers.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nova.pack.stickers.R

class StickerBottomDialogFragment: BottomSheetDialogFragment()  {
    private var mStickerListener: StickerListener? = null

    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.stickers_bottom_dialog_lyt, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        val rvEmoji: RecyclerView = contentView.findViewById(R.id.rvEmoji)
        val stickerPath1: ImageView = contentView.findViewById(R.id.stickerPack1)
        val stickerPath2: ImageView = contentView.findViewById(R.id.stickerPack2)
        val stickerPath3: ImageView = contentView.findViewById(R.id.stickerPack3)
        val stickerPath4: ImageView = contentView.findViewById(R.id.stickerPack4)
        val closeBtn: ImageView = contentView.findViewById(R.id.close_btn)

        var gridLayoutManager = GridLayoutManager(activity, 3)
        rvEmoji.layoutManager = gridLayoutManager
        val stickerAdapter = StickerAdapter()
        rvEmoji.adapter = stickerAdapter
//        rvEmoji.setHasFixedSize(true)
        stickerAdapter.setList(stickerPathList3)
        stickerPath1.setOnClickListener {
            stickerAdapter.setList(stickerPathList3)
            gridLayoutManager = GridLayoutManager(activity, 3)
            rvEmoji.layoutManager = gridLayoutManager
        }
        stickerPath2.setOnClickListener {
            stickerAdapter.setList(stickerPathList2)
            gridLayoutManager = GridLayoutManager(activity, 7)
            rvEmoji.layoutManager = gridLayoutManager
        }
        stickerPath3.setOnClickListener {
            stickerAdapter.setList(stickerPathList1)
            gridLayoutManager = GridLayoutManager(activity, 3)
            rvEmoji.layoutManager = gridLayoutManager
        }
        stickerPath4.setOnClickListener {
            stickerAdapter.setList(stickerPathList)
            gridLayoutManager = GridLayoutManager(activity, 3)
            rvEmoji.layoutManager = gridLayoutManager
        }
        closeBtn.setOnClickListener {
            dismiss()
        }



    }

    inner class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        private var stickerList = ArrayList<String>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_sticker, parent, false)
            return ViewHolder(view)
        }
        fun setList(stickerList : List<String>){
            this.stickerList = stickerList as ArrayList<String>
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Load sticker image from remote url
            Glide.with(requireContext())
                .asBitmap()
                .load(Uri.parse(stickerList[position]))
                .into(holder.imgSticker)
        }

        override fun getItemCount(): Int {
            return stickerList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgSticker: ImageView = itemView.findViewById(R.id.imgSticker)

            init {
                itemView.setOnClickListener {
                    if (mStickerListener != null) {
                        Glide.with(requireContext())
                            .asBitmap()
                            .load(stickerList[layoutPosition])
                            .into(object : CustomTarget<Bitmap?>(256, 256) {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                                    if (mStickerListener != null && !resource.isRecycled) {
                                        mStickerListener!!.onStickerClick(resource)
                                    }
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })
                    }
                    dismiss()
                }
            }
        }
    }

    companion object {
        private val stickerPathList: List<String> = (1..19).map { index ->
            "file:///android_asset/pack1/$index.png"
        }
        private val stickerPathList1: List<String> = (1..20).map { index ->
            "file:///android_asset/pack2/$index.png"
        }
        private val stickerPathList2: List<String> = (1..30).map { index ->
            "file:///android_asset/pack3/$index.png"
        }
        private val stickerPathList3: List<String> = (1..20).map { index ->
            "file:///android_asset/pack4/$index.png"
        }
    }
}
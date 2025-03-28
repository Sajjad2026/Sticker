package com.nova.pack.stickers.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nova.pack.stickers.R
import com.nova.pack.stickers.databinding.DeleteStickerItemLytBinding
import com.nova.pack.stickers.listener.OnItemDeleteCheckBoxSelectListener
import com.nova.pack.stickers.model.DeleteStickerItem

class DeleteStickerAdapter(val context:Context,onItemDeleteCheckBoxSelectListener: OnItemDeleteCheckBoxSelectListener): RecyclerView.Adapter<DeleteStickerAdapter.ViewHolder>() {
    private var listener: OnItemDeleteCheckBoxSelectListener = onItemDeleteCheckBoxSelectListener
    private var deleteList = ArrayList<DeleteStickerItem>()
    var isCheck=false
    var isCheckAll=false
    var isUnCheckAll=false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DeleteStickerItemLytBinding.inflate(LayoutInflater.from(parent.context)))
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(deleteList : List<DeleteStickerItem>){
        this.deleteList = deleteList as ArrayList<DeleteStickerItem>
        notifyDataSetChanged()
    }
    fun selectAllItems(selectAll: Boolean) {
        this.isCheckAll = selectAll
        notifyDataSetChanged()
    }
    fun uncheckAllItems(selectAll: Boolean) {
        this.isUnCheckAll = selectAll
        notifyDataSetChanged()
    }
    class ViewHolder(val binding : DeleteStickerItemLytBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context).load(deleteList[position].path).placeholder(R.drawable.ic_loading).into(imgPlaceholder)
//            if (deleteList[position].isSelected){
//                checkBox.isChecked = true
//            }
            if (isCheckAll){
                deleteList[position].isSelected=isCheckAll
                checkBox.isChecked = isCheckAll
                listener.onCheckBoxSelect()
            }else if (isUnCheckAll){
                deleteList[position].isSelected=false
                checkBox.isChecked = false
                listener.onCheckBoxSelect()
            }

            if(!deleteList[position].isSelected){
                checkBox.isChecked = false
            }

            imgPlaceholder.setOnClickListener {
                if (!checkBox.isChecked){
                    isCheck=true
                    checkBox.isChecked=true
                    deleteList[position].isSelected = isCheck
                    listener.onCheckBoxSelect()
                }else{
                    checkBox.isChecked=false
                    isCheck=false
                    deleteList[position].isSelected = false
                    listener.onCheckBoxSelect()
                }
            }
            checkBox.setOnClickListener {
                if (checkBox.isChecked){
                    isCheck=true
                    checkBox.isChecked=true
                    deleteList[position].isSelected = isCheck
                    listener.onCheckBoxSelect()
                }else{
                    checkBox.isChecked=false
                    isCheck=false
                    deleteList[position].isSelected = false
                    listener.onCheckBoxSelect()
                }
            }
          }
    }
    override fun getItemCount(): Int {
        return deleteList.size
    }
}
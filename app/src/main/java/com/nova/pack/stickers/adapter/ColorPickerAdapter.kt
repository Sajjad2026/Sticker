package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nova.pack.stickers.databinding.ColorPickerLytBinding
import com.nova.pack.stickers.listener.OnColorPickerListener

class ColorPickerAdapter(private var context: Context,onColorClickListener: OnColorPickerListener): RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {
    private var colorPickerList = ArrayList<Int>()
    private var listener:OnColorPickerListener = onColorClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ColorPickerLytBinding.inflate(LayoutInflater.from(parent.context)))
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(colorList : List<Int>){
        this.colorPickerList = colorList as ArrayList<Int>
        notifyDataSetChanged()
    }

    class ViewHolder(val binding : ColorPickerLytBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.apply {
                root.setOnClickListener {
                    listener.onColorSelected(colorPickerList[position])
                }
                colorPickerView.setBackgroundColor(colorPickerList[position])
            }
    }

    override fun getItemCount(): Int {
        return colorPickerList.size
    }
}
package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nova.pack.stickers.databinding.FontPickerLytBinding
import com.nova.pack.stickers.listener.OnFontPickerClickListener

class FontPickerAdapter(private var context: Context, onFontPickerClicked:  OnFontPickerClickListener): RecyclerView.Adapter<FontPickerAdapter.ViewHolder>() {
    private lateinit var fontPickerList:List<String>
    private var listener: OnFontPickerClickListener = onFontPickerClicked

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FontPickerLytBinding.inflate(LayoutInflater.from(parent.context)))
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(fontList : List<String>){
        this.fontPickerList = fontList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding : FontPickerLytBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            root.setOnClickListener {
                listener.onFontSelected(position)
            }
            val fontFileName = fontPickerList[position]
            val typeface = Typeface.createFromAsset(context.assets, fontFileName)
            fontText.typeface = typeface
        }
    }

    override fun getItemCount(): Int {
        return fontPickerList.size
    }
}
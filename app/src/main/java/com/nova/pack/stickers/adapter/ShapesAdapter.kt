package com.nova.pack.stickers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nova.pack.stickers.databinding.ShapesLytBinding
import com.nova.pack.stickers.listener.OnShapeClickListener

class ShapesAdapter (private var context: Context,onShapeClickListener: OnShapeClickListener): RecyclerView.Adapter<ShapesAdapter.ViewHolder>(){
    private var shapesList = ArrayList<Int>()
    private var listener: OnShapeClickListener= onShapeClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ShapesLytBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(shapesList : List<Int>){
        this.shapesList = shapesList as ArrayList<Int>
        notifyDataSetChanged()
    }


    class ViewHolder(val binding : ShapesLytBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            shapesImageview.setImageResource(shapesList[position])
        }
        holder.itemView.setOnClickListener {
            listener.onShapeClick(position)
        }

    }

    override fun getItemCount(): Int {
        return shapesList.size
    }

}
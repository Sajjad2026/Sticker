package com.nova.pack.stickers.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nova.pack.stickers.R
import com.nova.pack.stickers.databinding.CategoryListItemBinding
import com.nova.pack.stickers.listener.OnCategorySelectedListener
import com.nova.pack.stickers.model.CategoryStickers


class CategoryStickersAdapter(private var context: Context,onCategorySelectedListener: OnCategorySelectedListener): RecyclerView.Adapter<CategoryStickersAdapter.ViewHolder>() {
    private var categoryList = ArrayList<CategoryStickers>()
    private var listener:OnCategorySelectedListener = onCategorySelectedListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CategoryListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(categoryList : List<CategoryStickers>){
        this.categoryList = categoryList as ArrayList<CategoryStickers>
        notifyDataSetChanged()
    }


    class ViewHolder(val binding : CategoryListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colors = listOf(
            context.resources.getColor(R.color.category_1),
            context.resources.getColor(R.color.category_2),
            context.resources.getColor(R.color.category_3),
            context.resources.getColor( R.color.category_4)
        )

        holder.binding.apply {
            categoryTitle.text = categoryList[position].categoryTitle
            Log.d("hjhggf", categoryList[position].categoryImage)

            // Set background color based on position modulo colors size
            categoryCardView.setCardBackgroundColor(colors[position % colors.size])

            Glide.with(context).load(categoryList[position].categoryImage).into(categoryImage)

            categoryItemLyt.setOnClickListener {
                listener.onCategorySelectedListener(categoryTitle.text.toString(), position)
            }
            val bounceAnimator = ObjectAnimator.ofFloat(categoryItemLyt, "translationY", 0f, -10f, 0f)
            bounceAnimator.duration = 1000
            bounceAnimator.repeatCount = ObjectAnimator.INFINITE
            bounceAnimator.interpolator = BounceInterpolator()
            bounceAnimator.start()
        }


    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
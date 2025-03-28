package com.nova.pack.stickers.adapter

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nova.pack.stickers.api.StickerJson
import com.nova.pack.stickers.fragments.CategoryItemFragment


class CategoryPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return StickerJson.stickerData.size
        }

        override fun createFragment(position: Int): CategoryItemFragment {
            return newInstance(StickerJson.stickerData[position])
        }
    private fun newInstance(category: String): CategoryItemFragment {
        val myFragment = CategoryItemFragment()
        val args = Bundle()
        args.putString("category", category)
        myFragment.arguments = args
        return myFragment
    }
}
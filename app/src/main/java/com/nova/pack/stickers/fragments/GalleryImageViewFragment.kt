package com.nova.pack.stickers.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.nova.pack.stickers.activites.EditStickerImageActivity
import com.nova.pack.stickers.adapter.GalleryImagesAdapter
import com.nova.pack.stickers.databinding.FragmentGalleryImageViewBinding
import com.nova.pack.stickers.listener.onGalleryImageClickListener
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GalleryImageViewFragment : Fragment(),onGalleryImageClickListener {
    private lateinit var binding : FragmentGalleryImageViewBinding
    var adapter: GalleryImagesAdapter? = null
    var activityName = ""
    private lateinit var viewModel: GalleryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGalleryImageViewBinding.inflate(inflater, container, false)
        val position = arguments?.getInt("folderPosition",0)!!
        viewModel = ViewModelProvider(this)[GalleryViewModel(requireContext())::class.java]
        adapter = GalleryImagesAdapter(requireContext(),this)

        binding.idImagesRecyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(),3)
        binding.idImagesRecyclerView.layoutManager = layoutManager
        binding.idImagesRecyclerView.addItemDecoration(RecyclerViewItemDecorator(10))
        CoroutineScope(Dispatchers.Main).launch {
            if (Config.isAnim){
                val imgList = viewModel.videos.value?.get(position)?.imagePathList ?: emptyList()
                val chunkedItems = imgList.chunked(50)
                for (chunk in chunkedItems) {
                    adapter?.setList(chunk)
                    delay(1000) // 1000 milliseconds = 1 second
                }
            }else {
                val imgList = viewModel.images.value?.get(position)?.imagePathList ?: emptyList()
                val chunkedItems = imgList.chunked(50)
                for (chunk in chunkedItems) {
                    adapter?.setList(chunk)
                    delay(1000) // 1000 milliseconds = 1 second
                }
            }
        }
        if (Config.isAnim){
            viewModel.videos.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty() && it.size >= position)
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter!!.setList(it[position].imagePathList)
                    }
            }
        }else {
            viewModel.images.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty() && it.size >= position)
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter!!.setList(it[position].imagePathList)
                    }

            }
        }
        return binding.root
    }

    class RecyclerViewItemDecorator(private val spaceInPixels: Int) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = spaceInPixels
            outRect.right = spaceInPixels
            outRect.bottom = spaceInPixels
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = spaceInPixels
            } else {
                outRect.top = 0
            }
        }
    }

    override fun onImageClick(imagePath: String) {
        Log.d("activityName1", activityName)
        activityName = arguments?.getString("activityName").toString()
            val intent = Intent(context,EditStickerImageActivity::class.java)
            intent.putExtra("imagePath",imagePath)
            intent.putExtra("activityName", activityName)
            startActivity(intent)
            activity?.finish()
    }

}
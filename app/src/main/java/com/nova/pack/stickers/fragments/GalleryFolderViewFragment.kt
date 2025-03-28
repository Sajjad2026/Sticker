package com.nova.pack.stickers.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.GalleryFolderAdapter
import com.nova.pack.stickers.databinding.FragmentFolderViewBinding
import com.nova.pack.stickers.listener.onGalleryFolderClickListener
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryFolderViewFragment : Fragment(),onGalleryFolderClickListener {
    private lateinit var binding : FragmentFolderViewBinding
    private lateinit var adapter: GalleryFolderAdapter
    private lateinit var viewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderViewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[GalleryViewModel(requireContext())::class.java]
        adapter = GalleryFolderAdapter(requireContext(),this)
        viewModel.emitFolderDropDownUi(false)
        binding.galleryFoldersRecyclerview.adapter = adapter
        GlobalScope.launch {
            if (Config.isAnim) {
                viewModel.getAllVideosPath()
                Log.d("Sdjkshf","videos")
            }else{
                Log.d("Sdjkshf","images")
                viewModel.getAllImagesPath()
            }
        }
        if (Config.isAnim) {
            viewModel.videos.observe(viewLifecycleOwner) {
                if (it.size == 0) {
                    binding.emptyLyt.visibility = View.VISIBLE
                } else {
                    binding.emptyLyt.visibility = View.INVISIBLE
                }
                adapter.setList(it)
            }
        }else {
            viewModel.images.observe(viewLifecycleOwner) {
                if (it.size == 0) {
                    binding.emptyLyt.visibility = View.VISIBLE
                } else {
                    binding.emptyLyt.visibility = View.INVISIBLE
                }
                adapter.setList(it)
            }
        }
        setObservers()
        return binding.root
    }
    override fun onClick(position: Int) {
        viewModel.emitFolderDropDownUi(true,viewModel.images.value?.get(position)?.folder)
        val photoFragment = GalleryImageViewFragment()
        val bundle = Bundle()
        bundle.putInt("folderPosition", position)
        if (Config.isAnim){
            bundle.putString("activityName", "activityAnim")
        }else{
            bundle.putString("activityName", "activityName")
        }
        photoFragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.gallery_main_frameholder, photoFragment)
        fragmentTransaction.addToBackStack(null) // Optional: Adds the transaction to the back stack
        fragmentTransaction.commit()
    }
    private fun setObservers(){
        viewModel.uiState.observe(viewLifecycleOwner) {
            val dataState = it ?: return@observe
            binding.progressBar.visibility = if (dataState.isLoading) View.VISIBLE else View.GONE
            Log.d("Error:",it.error.toString())
        }
    }
}
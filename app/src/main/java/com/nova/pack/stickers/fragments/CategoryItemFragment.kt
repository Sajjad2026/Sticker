package com.nova.pack.stickers.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.activites.StickerDetailActivity
import com.nova.pack.stickers.adapter.HomeStickerAdapter
import com.nova.pack.stickers.databinding.FragmentCategoryItemBinding
import com.nova.pack.stickers.listener.onStickerPackClickListener
import com.nova.pack.stickers.model.StickerPackView
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryItemFragment : Fragment(), onStickerPackClickListener {
    private lateinit var viewModel: StickersViewModel
    private lateinit var binding: FragmentCategoryItemBinding
    private var mHomeStickerAdapter: HomeStickerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.theme?.applyStyle(R.style.Theme_WhatsappStickerMaker, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryItemBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StickersViewModel::class.java]

        arguments?.getString("category")?.let { category ->
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.setStickerCategory(category)
            }
        }

        populateHomeStickerRecyclerView()
        setObservers()
        return binding.root
    }

    private fun setObservers() {
        viewModel.sticker.observe(viewLifecycleOwner) { stickers ->
            Log.d("Sfdkhsfs", viewModel.sticker.value.toString())
            arguments?.getString("category")?.let { category ->
                val matchingData = stickers.filter { it.category_name == category }
                mHomeStickerAdapter?.setList(matchingData)
                mHomeStickerAdapter?.setFragment(true)
            }
        //    preferences.saveObjectsList("sticker_packs", stickers)
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            val dataState = it ?: return@observe
            binding.progressBar.visibility = if (dataState.isLoading) View.VISIBLE else View.GONE
            Log.d("Error:", it.error.toString())
        }
    }

    private fun populateHomeStickerRecyclerView() {
        binding.homeStickersRecyclerview.layoutManager = LinearLayoutManager(context)
        mHomeStickerAdapter = activity?.let { HomeStickerAdapter(requireActivity(), this) }
        binding.homeStickersRecyclerview.adapter = mHomeStickerAdapter
    }

    override fun OnStickerClickListener(item: StickerPackView) {
        val intent = Intent(context, StickerDetailActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(HomeFragment.EXTRA_STICKERPACK, item)
        intent.putExtras(bundle)
        requireContext().startActivity(intent)
    }
}

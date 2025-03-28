package com.nova.pack.stickers.activites

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.DeleteStickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityDeleteStickerBinding
import com.nova.pack.stickers.listener.OnItemDeleteCheckBoxSelectListener
import com.nova.pack.stickers.model.DeleteStickerItem
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DeleteStickerActivity : AppCompatActivity(),OnItemDeleteCheckBoxSelectListener {
    lateinit var binding: ActivityDeleteStickerBinding
    private lateinit var viewModel: EditImageViewModel
    private var deleteStickerAdapter: DeleteStickerAdapter?= null
    private lateinit var itemDeleteList: ArrayList<DeleteStickerItem>
    private var deleteStickerPack = false
    private var isCheck = false
    private var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteStickerBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EditImageViewModel(this)::class.java]
        itemDeleteList = ArrayList()
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpRecyclerView()
        setObserver()
        loadBottomBanner()
        binding.deleteBtn.setOnClickListener {
            val selectedItems = itemDeleteList.filter { it.isSelected }
            if(selectedItems.isNotEmpty())
            {
                showCustomDeleteDialog()
            }
            else
            {
                Toast.makeText(this@DeleteStickerActivity,"Kindly Select Any Image To Delete First",Toast.LENGTH_SHORT).show()
            }
        }
        binding.selectIconImageview.setOnClickListener {
            if(!isCheck){
                isCheck=true
                deleteStickerAdapter?.selectAllItems(true)
                deleteStickerAdapter?.uncheckAllItems(false)
                binding.selectIconImageview.setImageResource(R.drawable.ic_cancel_select)
            }else{
                isCheck=false
                deleteStickerAdapter?.selectAllItems(false)
                deleteStickerAdapter?.uncheckAllItems(true)
                binding.selectIconImageview.setImageResource(R.drawable.ic_box)
            }
        }
        binding.backBtn.setOnClickListener {
            if (!isBackPressed){
                isBackPressed=true
                loadBackButtonInterstitial()
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressed){
                    isBackPressed=true
                loadBackButtonInterstitial()
            }
            }
        })
    }
    private fun loadBackButtonInterstitial(){
        InterstitialAdManager.showInterstitial(this@DeleteStickerActivity, EditStickerImageActivity(),"","",
            "","", "finish",
            AdsManager.BackInterstitialDeleteStickerActivity
        )
    }
    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer, AdsManager.BannerTopDeleteStickerActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2, AdsManager.BannerBottomDeleteStickerActivity)
    }

    private fun setUpRecyclerView() {
        binding.deleteRecyclerview.layoutManager = GridLayoutManager(this@DeleteStickerActivity,3, RecyclerView.VERTICAL,false)
        deleteStickerAdapter = DeleteStickerAdapter(this,this)
        binding.deleteRecyclerview.adapter = deleteStickerAdapter
    }

    private fun setObserver(){
        viewModel.stickerList.observe(this){
            itemDeleteList.clear()
            for(i in it.indices)
            {
                if(it[i].identifier.toString() == viewModel.currentIdentifier) {
                    for (j in it[i].stickers?.indices!!) {
                        val path = it[i].stickers!![j].imageFile
                        itemDeleteList.add(DeleteStickerItem(path,j,false))
                    }
                }
            }
            deleteStickerAdapter?.setList(itemDeleteList)
        }
        binding.selectIconImageview.setOnClickListener {
            val selectedItems = itemDeleteList.filter { it.isSelected }
            if(selectedItems.isNotEmpty())
            {
                itemDeleteList.forEach { it.isSelected = false }
            }
        }
        binding.backBtn.setOnClickListener {
            val activityName = intent.getStringExtra("activityName").toString()
            Log.d("activityName",activityName.toString())
            val intent = Intent(this@DeleteStickerActivity,CreateStickerActivity::class.java)
            intent.putExtra("activityName",activityName)
            startActivity(intent)
            finish()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val activityName = intent.getStringExtra("activityName").toString()
                val intent = Intent(this@DeleteStickerActivity,CreateStickerActivity::class.java)
                intent.putExtra("activityName",activityName)
                startActivity(intent)
                finish()
            }
        })

    }

    private fun showCustomDeleteDialog(){
        val factory = LayoutInflater.from(this)
        val deleteDialogView: View = factory.inflate(R.layout.confirm_dailog_lyt, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        deleteDialogView.findViewById<AppCompatButton>(R.id.delete_btn)
            .setOnClickListener {
                binding.progressBar.visibility = View.VISIBLE
                val selectedItems = itemDeleteList.filter { it.isSelected }
                kotlin.runCatching {
                    CoroutineScope(Dispatchers.IO).launch{
                        deleteStickerPack = viewModel.deleteStickerToPack(viewModel.currentIdentifier, selectedItems)
                        viewModel.getStickers()
                        runOnUiThread{
                            binding.progressBar.visibility = View.GONE
                            deleteDialog.dismiss()
                            if(deleteStickerPack)
                            {
                                val activityName = intent.getStringExtra("activityName").toString()
                                if(activityName == "mySticker")
                                {
                                    val intent = Intent(this@DeleteStickerActivity,MainActivity::class.java)
                                    intent.putExtra("activityName",activityName)
                                    intent.putExtra("openActivity","mySticker")
                                    startActivity(intent)
                                    finish()
                                }
                               else {
//                                    val intent = Intent(
//                                        this@DeleteStickerActivity,
//                                        CreatedStickerActivity::class.java
//                                    )
//                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                }.onSuccess {


                }.onFailure {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                    }

            }
        deleteDialogView.findViewById<AppCompatButton>(R.id.cancel_btn).setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.setCancelable(false)
        deleteDialog.show()
    }
    override fun onCheckBoxSelect() {
        val selectedItems = itemDeleteList.filter { it.isSelected }
        if(selectedItems.isNotEmpty()){
            binding.selectIconImageview.setImageResource(R.drawable.ic_cancel_select)
        }else{
            binding.selectIconImageview.setImageResource(R.drawable.ic_box)
        }
        val selectedString = getString(R.string.selected)
        binding.selectCountTextview.text = buildString {
        append(selectedString)
        append(" ")
        append(selectedItems.size)
        }
    }
}


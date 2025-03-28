package com.nova.pack.stickers.activites

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.StickerAdapter
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.BackInterstitialCreateStickerActivity
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityCreateStickerBinding
import com.nova.pack.stickers.utils.Config.Companion.isAnim
import com.nova.pack.stickers.utils.SharedPreferenceHelper
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateStickerActivity : AppCompatActivity() {
    private val preferences by lazy { SharedPreferenceHelper() }
    lateinit var binding: ActivityCreateStickerBinding
    private val PERMISSION_REQUEST_CODE = 20
    private lateinit var viewModel: EditImageViewModel
    private val stickerPaths = MutableList(12) { null as String? } // Holds paths for up to 12 stickers
    var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStickerBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EditImageViewModel(this)::class.java]
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadBanner()

        viewModel.stickerList.observe(this){
            val list = it.map {it1->
                it1.toStickersPack()
            }.map { either ->
                either.toStickersPackView()
            }
            preferences.saveObjectsList("sticker_packs", list)
        }

        binding.deleteBtn.setOnClickListener {
            if (viewModel.currentIdentifier.isNotEmpty()) {
                val activityName = intent.getStringExtra("activityName").toString()
                val intent = Intent(this, DeleteStickerActivity::class.java).apply {
                    putExtra("activityName", activityName)
                    putExtra("identifier", viewModel.currentIdentifier)
                }
                startActivity(intent)
                //finish()
            } else {
                Toast.makeText(this, getString(R.string.no_item), Toast.LENGTH_SHORT).show()
            }
        }

        binding.whatsappBtn.setOnClickListener {
            viewModel.addCustomPackToWhatsapp(viewModel.currentIdentifier, this)
        }

        binding.whatsappBusinessBtn.setOnClickListener {
            viewModel.addCustomPackToWhatsappBusiness(viewModel.currentIdentifier, this)
        }

        binding.editIdentifier.setOnClickListener {
            if (viewModel.currentIdentifier.isNotEmpty()) {
                createAlertBox()
            } else {
                Toast.makeText(this, getString(R.string.no_item), Toast.LENGTH_SHORT).show()
            }
        }

        val activityName = intent.getStringExtra("activityName").toString()
        Log.d("activityName8", activityName)
        binding.identifierTextview.text = viewModel.currentIdentifier

        binding.backBtn.setOnClickListener {
            if (!isBackPressed) {
                isBackPressed=true
                isAnim = false
                loadInterstitialAd()
            }
        }

        binding.shareWithFriendsBtn.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, """
                        Let me recommend you this application

                        https://play.google.com/store/apps/details?id=$packageName
                    """.trimIndent())
                }
                startActivity(Intent.createChooser(shareIntent, "Choose one"))
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isBackPressed) {
                    isBackPressed=true
                    isAnim = false
                    loadInterstitialAd()
                }
            }
        })

        kotlin.runCatching {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getStickers()
            }
        }.onSuccess {
            setObserver()
            setupRecyclerView()
        }.onFailure {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3) // 3 columns in grid
        binding.stickerRecyclerView.layoutManager = layoutManager

        val adapter = StickerAdapter(stickerPaths) { stickerPath ->
            requestPermission()
        }
        binding.stickerRecyclerView.adapter = adapter
    }

    private fun setObserver() {
        viewModel.stickerList.observe(this) { stickerPacks ->
            stickerPaths.clear()
            val matchingPack = stickerPacks.find { it.identifier == viewModel.currentIdentifier }
            matchingPack?.stickers?.forEachIndexed { index, sticker ->
                if (index < 12 && !sticker.imageFile.isNullOrEmpty()) {
                    stickerPaths.add(sticker.imageFile)
                }
            }
            // Fill remaining items with null to maintain a fixed size
            while (stickerPaths.size < 12) stickerPaths.add(null)
            (binding.stickerRecyclerView.adapter as StickerAdapter).notifyDataSetChanged()
        }
    }




    private fun createAlertBox() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.enter_identifer_dialog_lyt, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnOk = view.findViewById<AppCompatButton>(R.id.ok_btn)
        val close = view.findViewById<ImageView>(R.id.cross)
        val btnCancel = view.findViewById<AppCompatButton>(R.id.cancel_btn)
        val identifierEditText = view.findViewById<EditText>(R.id.identifier_edit_text)
        identifierEditText.setText(viewModel.currentIdentifier)
        close.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            val newIdentifier = identifierEditText.text.toString().trim()
            if (newIdentifier.isNotEmpty() && newIdentifier.matches("""^[a-zA-Z0-9_\-. ]{1,128}$""".toRegex()) && !newIdentifier.startsWith(".")) {
                kotlin.runCatching {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (viewModel.checkIdentifierAlreadyExist(newIdentifier)) {
                            runOnUiThread {
                                identifierEditText.error = "Identifier Already Exists"
                            }
                        } else {
                            viewModel.changeIdentifier(viewModel.currentIdentifier, newIdentifier)
                            // Trigger data refresh
                            viewModel.refreshStickerList() // Add this method in your ViewModel
                            runOnUiThread {
                                // Update UI
                                viewModel.currentIdentifier = newIdentifier
                                binding.identifierTextview.text = newIdentifier
                                Toast.makeText(this@CreateStickerActivity, "Identifier Changed Successfully", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                        }
                    }
                }.onFailure {
                    runOnUiThread {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            } else {
                identifierEditText.error = "Invalid Identifier Name"
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun requestPermission() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val activityName = intent.getStringExtra("activityAnim") ?: intent.getStringExtra("activityName")
                val intent = Intent(this, GalleryActivity::class.java).apply {
                    putExtra("activityName", activityName)
                }
                startActivity(intent)
                finish()
            } else {
                if (permissions.any { !ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {
                    showPermissionDeniedDialog()
                } else {
                    Toast.makeText(this, "Kindly Grant Access To Use App", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("This app requires storage access to function properly. Please grant the permission in app settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun loadBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainerTop,
            AdsManager.BannerTopCreateStickerActivity
        )
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2,
            AdsManager.BannerBottomCreateStickerActivity
        )
    }

    private fun loadInterstitialAd() {
        if(intent.getStringExtra("activityName").toString() == "mySticker"){
            InterstitialAdManager.showInterstitial(this@CreateStickerActivity, MainActivity(),"openActivity","","mySticker","", "finishes", BackInterstitialCreateStickerActivity)
        }else{
            val intent = Intent(this@CreateStickerActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

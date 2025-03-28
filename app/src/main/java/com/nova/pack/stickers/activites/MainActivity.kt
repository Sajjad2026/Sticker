package com.nova.pack.stickers.activites
import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.BottomNavInterstitialCounter
import com.nova.pack.stickers.ads.AdsManager.InterstitialBottomNavFirstTime
import com.nova.pack.stickers.ads.AdsManager.Is_InApp_Screen_Show_Or_Not
import com.nova.pack.stickers.ads.AdsManager.Is_Show_Non_Functional_Items
import com.nova.pack.stickers.ads.BannerAdManager
import com.nova.pack.stickers.ads.Constants.Companion.checkIfPlay
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityMainBinding
import com.nova.pack.stickers.fragments.HomeFragment
import com.nova.pack.stickers.fragments.MyStickerFragment
import com.nova.pack.stickers.listener.ItemClick
import com.nova.pack.stickers.repository.LocalDatabaseRepository
import com.nova.pack.stickers.room.StickerDatabase
import com.nova.pack.stickers.utils.Config
import com.nova.pack.stickers.utils.Config.Companion.isClickBottom
import com.nova.pack.stickers.utils.Config.Companion.isCreate
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import com.nova.pack.stickers.viewmodel.StickersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),ItemClick {
    private lateinit var viewModel: StickersViewModel
    private lateinit var editViewModel: EditImageViewModel
    private var isOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabRotate: Animation
    private lateinit var fabReRotate: Animation
    private lateinit var fabRotateForward: Animation
    private lateinit var fabRotateBackward: Animation
    lateinit var binding: ActivityMainBinding
    private lateinit var db: LocalDatabaseRepository
    private lateinit var analytics: FirebaseAnalytics
    var isFirst=false
    var counter=0
    private val toBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)
    }
    private val fromBottomBgAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)
    }
    var isBackPressed=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = LocalDatabaseRepository(StickerDatabase(this))
        analytics = Firebase.analytics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1);
            }else {
                // repeat the permission or open app details
            }
        }
        viewModel = ViewModelProvider(this)[StickersViewModel::class.java]
        editViewModel = ViewModelProvider(this)[EditImageViewModel::class.java]
        val openActivity = intent.getStringExtra("openActivity").toString()
        if (intent.extras?.containsKey("return") == true){
            binding.imgHome.alpha=0.5f
            binding.imgSticker.alpha=1f
            Log.d("sdsfcv","1")
            binding.titleText.text=getString(R.string.my_sticker)
            setCurrentFragment(MyStickerFragment())
            binding.lytSearch.visibility=View.GONE
            binding.btnHome.backgroundTintList=resources.getColorStateList(R.color.white)
            binding.btnMySticker.backgroundTintList=resources.getColorStateList(R.color.bg_color)
        }
        val scaleX = ObjectAnimator.ofFloat(binding.premiumBtn, "scaleX", 1f, 1.1f, 1f)
        scaleX.duration = 1000

        val scaleY = ObjectAnimator.ofFloat(binding.premiumBtn, "scaleY", 1f, 1.1f, 1f)
        scaleY.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)

        val repeatAnimator = ValueAnimator.ofInt(0, 1)
        repeatAnimator.duration = 2000
        repeatAnimator.repeatCount = ValueAnimator.INFINITE
        repeatAnimator.addUpdateListener {
            animatorSet.start()
        }
        repeatAnimator.start()
        if (Is_Show_Non_Functional_Items){
            binding.premiumBtn.visibility=View.VISIBLE
            binding.fab2.visibility=View.VISIBLE
        }
        binding.drawerBtn.setOnClickListener {
            if (!checkIfPlay) {
                showDialog()
            } else {
                val intent = Intent(this, SettingActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    this,
                    R.animator.slide_anim,
                    R.animator.slide_anim
                )
                startActivity(intent, options.toBundle())
            }
        }
        binding.premiumBtn.setOnClickListener {
            if (!checkIfPlay) {
                showDialog()
            } else {
                if (Is_InApp_Screen_Show_Or_Not) {
                    val intent = Intent(this, InAppActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this@MainActivity,getString(R.string.comming_soon),Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.lytSearch.setOnClickListener {
            if (!checkIfPlay) {
                showDialog()
            } else {
                val intent = Intent(this, SearchStickersActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.stickersSearchView.setOnClickListener {
            if (!checkIfPlay) {
                showDialog()
            } else {
                val intent = Intent(this, SearchStickersActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        loadBottomBanner()
        if (isCreate){
            binding.imgHome.alpha=0.5f
            binding.imgSticker.alpha=1f
            isCreate=false
            binding.titleText.text=getString(R.string.my_sticker)
            setCurrentFragment(MyStickerFragment())
        }else {
            if (intent.extras?.containsKey("create") == true) {
                binding.imgHome.alpha=0.5f
                binding.imgSticker.alpha=1f
                setCurrentFragment(MyStickerFragment())
                binding.btnHome.backgroundTintList=resources.getColorStateList(R.color.white)
                binding.btnMySticker.backgroundTintList=resources.getColorStateList(R.color.bg_color)
            } else{
                Log.d("sdsfcv","2")
                if (openActivity == "mySticker") {
                    binding.imgHome.alpha=0.5f
                    binding.imgSticker.alpha=1f
                    binding.titleText.text=getString(R.string.my_sticker)
                    setCurrentFragment(MyStickerFragment())
                    binding.lytSearch.visibility=View.GONE
                    binding.btnHome.backgroundTintList=resources.getColorStateList(R.color.white)
                    binding.btnMySticker.backgroundTintList=resources.getColorStateList(R.color.bg_color)
                } else {
                    binding.imgHome.alpha=1f
                    binding.imgSticker.alpha=0.5f
                    setCurrentFragment(HomeFragment())
                }
            }
        }


        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        fabRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_anti_clock_wise)
        fabReRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_clock_wise)
        fabRotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward)
        fabRotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward)
        binding.home.setOnClickListener {
            binding.imgHome.alpha=1f
            binding.imgSticker.alpha=0.5f
                    binding.titleText.text=getString(R.string.home)
                    binding.lytSearch.visibility=View.VISIBLE
                    binding.btnHome.backgroundTintList=resources.getColorStateList(R.color.bg_color)
                    binding.btnMySticker.backgroundTintList=resources.getColorStateList(R.color.white)
                    if (isOpen) {
                        animateFab()
                    }
                    isClickBottom=false
                    Log.d("adahd","click")
                    if (!checkIfPlay){
                        showDialog()
                    }else {
                     //   showDialog()
                        showInterstitialAd(HomeFragment())
                        if (!isFirst) {
                            isFirst = true
                            if (InterstitialBottomNavFirstTime) {
                                InterstitialAdManager.showInterstitial(
                                    this@MainActivity, ExitActivity(), "", "", "", "", "fragment",
                                    AdsManager.InterstitialBottomNavHome
                                )
                            }
                        } else {
                            counter++
                            if (counter == BottomNavInterstitialCounter) {
                                counter = 0
                                InterstitialAdManager.showInterstitial(
                                    this@MainActivity, ExitActivity(), "", "", "", "", "fragment",
                                    AdsManager.InterstitialBottomNavHome
                                )
                            }
                        }
                    }
                    true
                }
               binding.sticker.setOnClickListener{
                   binding.imgHome.alpha=0.5f
                   binding.imgSticker.alpha=1f
                   binding.lytSearch.visibility=View.GONE
                   binding.titleText.text=getString(R.string.my_sticker)
                   binding.btnHome.backgroundTintList=resources.getColorStateList(R.color.white)
                   binding.btnMySticker.backgroundTintList=resources.getColorStateList(R.color.bg_color)
                    if (isOpen) {
                        animateFab()
                    }
                    isClickBottom=false
                    if (!checkIfPlay){
                        showDialog()
                    }else {
                        showInterstitialAd(MyStickerFragment())
                        if (!isFirst) {
                            isFirst = true
                            if (InterstitialBottomNavFirstTime) {
                                InterstitialAdManager.showInterstitial(
                                    this@MainActivity, ExitActivity(), "", "", "", "", "fragment",
                                    AdsManager.InterstitialBottomNavSticker
                                )
                            }
                        } else {
                            counter++
                            if (counter == BottomNavInterstitialCounter) {
                                counter = 0
                                InterstitialAdManager.showInterstitial(
                                    this@MainActivity, ExitActivity(), "", "", "", "", "fragment",
                                    AdsManager.InterstitialBottomNavSticker
                                )
                            }
                        }
                    }
                    true
                }

        binding.btnFab.setOnClickListener {
            animateFab()
        }
        binding.fab1.setOnClickListener {
            if (!checkIfPlay){
                showDialog()
            }else {
                Config.isAnim = false
                CoroutineScope(Dispatchers.IO).launch {
                    if (db.getItemCount() == 0) {
                        runOnUiThread {
                            editViewModel.currentIdentifier = ""
                                val intent =
                                    Intent(this@MainActivity, CreateStickerActivity::class.java)
                                intent.putExtra("activityName", "mySticker")
                                startActivity(intent)

                        }
                    } else {
                        runOnUiThread {
                                val intent =
                                    Intent(this@MainActivity, CreateStickerActivity::class.java)
                                intent.putExtra("activityName", "mySticker")
                                startActivity(intent)
                        }
                    }
                }
                animateFab()
            }
        }

        binding.fab2.setOnClickListener {
            Toast.makeText(this@MainActivity,getString(R.string.comming_soon),Toast.LENGTH_SHORT).show()
            animateFab()
        }

        onBackPressedDispatcher.addCallback {
            if (!isBackPressed) {
                isBackPressed = true
                Log.d("Sfmsnf", "back")
                InterstitialAdManager.showInterstitial(
                    this@MainActivity, ExitActivity(), "", "", "", "", "finishes",
                    AdsManager.BackInterstitialMainActivity
                )
            }
        }
    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            replace(R.id.mainFragment,fragment)
            commit()
        }


    private fun animateFab() {
        if (isOpen) {
            binding.main.isActivated = false
            binding.fab1.startAnimation(fabClose)
            binding.fab2.startAnimation(fabClose)
            binding.fab1.isClickable = false
            binding.fab2.isClickable = false
            binding.btnFab.startAnimation(fabRotate)
            binding.transparentBg.visibility=View.GONE
            isOpen = false
            binding.fab.visibility = View.INVISIBLE
            //  binding.fab.setImageResource(R.drawable.ic_add)
            binding.transparentBg.startAnimation(toBottomBgAnim)
        } else {
            binding.transparentBg.startAnimation(fromBottomBgAnim)
         //   binding.fab.setImageResource(R.drawable.ic_close_fab)
            binding.fab1.startAnimation(fabOpen)
            binding.fab2.startAnimation(fabOpen)
            binding.btnFab.startAnimation(fabReRotate)
            binding.fab.visibility = View.VISIBLE
            binding.fab1.isClickable = true
            binding.transparentBg.visibility=View.VISIBLE
            binding.fab2.isClickable = true
            isOpen = true
        }
    }

    private fun showInterstitialAd(fragment: Fragment){
        setCurrentFragment(fragment)
    }
    private fun loadBottomBanner() {
        BannerAdManager.loadBannerAd(this, binding.bannerLayout,binding.bannerArea,binding.nativeContainer, AdsManager.BannerTopMainActivity)
        BannerAdManager.loadBannerAd(this, binding.bannerLayout2,binding.bannerArea2,binding.nativeContainer2, AdsManager.BannerBottomMainActivity)
    }
    override fun onClickListener(path: String) {
        if (!isClickBottom) {
            isClickBottom=true
            if (path == "sticker") {
                binding.btnHome.backgroundTintList=resources.getColorStateList(R.color.bg_color)
                binding.btnMySticker.backgroundTintList=resources.getColorStateList(R.color.white)
            }
            binding.imgHome.alpha=1f
            binding.imgSticker.alpha=0.5f
            binding.titleText.text=getString(R.string.home)
            binding.lytSearch.visibility=View.VISIBLE
            setCurrentFragment(HomeFragment())
        }
    }
    fun showDialog(){
        var dialog= Dialog(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.suspend_dialog)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var button=dialog.findViewById<AppCompatButton>(R.id.btn_done)
        button.setOnClickListener {
            dialog.dismiss()
            val appPackageName = packageName
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
        dialog.show()
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        if (requestCode==200 && requestCode== RESULT_OK){
            Log.d("pasopo",intent.data.toString())
        }else{
            Log.d("pasopo",intent.data.toString())
        }
    }
}
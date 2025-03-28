package com.nova.pack.stickers.activites

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager
import com.nova.pack.stickers.ads.AdsManager.LifetimeOff
import com.nova.pack.stickers.ads.AdsManager.OneMonthOff
import com.nova.pack.stickers.ads.AdsManager.OneWeekOff
import com.nova.pack.stickers.ads.AdsManager.ThreeMonthOff
import com.nova.pack.stickers.ads.InAppClass
import com.nova.pack.stickers.ads.InterstitialAdManager
import com.nova.pack.stickers.databinding.ActivityPremiumPurchaseActiviryBinding

import kotlin.random.Random

class InAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPremiumPurchaseActiviryBinding
    var plan="weekly"
    var i=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPremiumPurchaseActiviryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainInit()
    }

    private fun mainInit() {
      termsCondition()

        // InAppClass.setupBillingBlient(this)
        val showFirstImage = Random.nextBoolean()
        if (showFirstImage) {
            binding.btnclose.visibility = View.VISIBLE
        } else {
            binding.btnclose2.visibility = View.VISIBLE
        }
        setUpPrices()
        binding.btnGetStarted.setOnClickListener {
            try {
                if (plan=="weekly"){
                    InAppClass.launch_Subscription_billing_flow(this, InAppClass.oneWeekDetail!!)
                }else if (plan=="month"){
                    InAppClass.launch_Subscription_billing_flow(this, InAppClass.oneMonthDetail!!)
                }else if (plan=="3month"){
                    InAppClass.launch_Subscription_billing_flow(this, InAppClass.threeMonthDetail!!)
                }else{
                    InAppClass.launch_One_Time_Purchase_billing_flow(this, InAppClass.lifetimeDetail!!)
                }
            }catch (e:Exception){
                Log.d("sdklujsf",e.message.toString())
                // Toast.makeText(this,getString(R.string.internet_not),Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnclose.setOnClickListener {
            if (!InAppClass.isPurchase){
                InterstitialAdManager.showInterstitial(this@InAppActivity, MainActivity(),"","","","", "finishes", AdsManager.BackInterstitialInAppActivity)
            }else{
                startActivity(Intent(this@InAppActivity,MainActivity::class.java))
            }
        }
        binding.btnclose2.setOnClickListener {
            if (!InAppClass.isPurchase){
                InterstitialAdManager.showInterstitial(this@InAppActivity, MainActivity(),"","","","", "finishes", AdsManager.BackInterstitialInAppActivity)
            }else{
                startActivity(Intent(this@InAppActivity,MainActivity::class.java))
            }
        }
        onBackPressedDispatcher.addCallback {
        }
    }

    private fun termsCondition() {
        val termsConditionText = getString(R.string.terms_condition)

// Find the start and end index of "Terms & Conditions"
        val startIndex = termsConditionText.indexOf("Terms & Conditions")
        val endIndex = startIndex + "Terms & Conditions".length

// Create a SpannableString
        val spannable = SpannableString(termsConditionText)

// Define a ClickableSpan for "Terms & Conditions"
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Launch the URL
                val url = "https://absoluinc.blogspot.com/2024/12/terms-of-services-for-subscriptions.html" // Replace with your URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Optional: Remove underline
                ds.color = ContextCompat.getColor(this@InAppActivity, R.color.bg_color) // Set your text color
            }
        }

// Apply the ClickableSpan to "Terms & Conditions"
        spannable.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Enable links in the TextView
        binding.txtTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.txtTerms.highlightColor = Color.TRANSPARENT // Optional: Removes highlight on click

// Set the styled text to the TextView
        binding.txtTerms.text = spannable
    }

    fun removeTrailingZero(price: String?): String? {
        return price?.replace(".00", "")
    }
    fun setUpPrices(){
        // Helper function to remove ".00" from the string

// Clean up the price values
        var weekly = removeTrailingZero(InAppClass.oneWeekDetail?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice)
        var month = removeTrailingZero(InAppClass.oneMonthDetail?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice)
        var threeMonth = removeTrailingZero(InAppClass.threeMonthDetail?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice)
        var lifetime = removeTrailingZero(InAppClass.lifetimeDetail?.oneTimePurchaseOfferDetails?.formattedPrice)

// Discount prices
        var weeklyDisc = removeTrailingZero(InAppClass.oneWeekDetailCut?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice)
        var monthDisc = removeTrailingZero(InAppClass.oneMonthDetailCut?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice)
        var threeMonthDisc = removeTrailingZero(InAppClass.threeMonthDetailCut?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice)
        var lifetimeDisc = removeTrailingZero(InAppClass.lifetimeDetailCut?.oneTimePurchaseOfferDetails?.formattedPrice)

// Logging for verification
        Log.d("Formatted Weekly Price", weekly.toString())

// Set fallback text
        val weeklyText = weekly ?: resources.getString(R.string.fetching)
        val monthText = month ?: resources.getString(R.string.fetching)
        val threeMonthText = threeMonth ?: resources.getString(R.string.fetching)
        val lifetimeText = lifetime ?: resources.getString(R.string.fetching)

        val weeklyDiscText = weeklyDisc ?: resources.getString(R.string.fetching)
        val monthDiscText = monthDisc ?: resources.getString(R.string.fetching)
        val threeMonthDiscText = threeMonthDisc ?: resources.getString(R.string.fetching)
        val lifetimeDiscText = lifetimeDisc ?: resources.getString(R.string.fetching)

// Update UI elements
        binding.txt1.text = getStyledSpannable(weeklyText, resources.getString(R.string.Week_Offer))
        binding.txt2.text = getStyledSpannable(monthText, resources.getString(R.string.month_Offer))
        binding.txt3.text = getStyledSpannable(threeMonthText, resources.getString(R.string.three_month_Offer))
        binding.txt4.text = getStyledSpannable(lifetimeText, resources.getString(R.string.LIFETIME_offer))

        binding.txtDis1.text = weeklyDiscText
        binding.txtDis2.text = monthDiscText
        binding.txtDis3.text = threeMonthDiscText
        binding.txtDis4.text = lifetimeDiscText

        binding.txtDisDesc1.text = OneWeekOff ?: resources.getString(R.string.fetching)
        binding.txtDisDesc2.text = OneMonthOff ?: resources.getString(R.string.fetching)
        binding.txtDisDesc3.text = ThreeMonthOff ?: resources.getString(R.string.fetching)
        binding.txtDisDesc4.text = LifetimeOff ?: resources.getString(R.string.fetching)


        binding.weekly.setOnClickListener {
            try {
                plan = "weekly"
                binding.radio1.setImageResource(R.drawable.check_icon)
                binding.radio2.setImageResource(R.drawable.unselect_icon)
                binding.radio3.setImageResource(R.drawable.unselect_icon)
                binding.radio4.setImageResource(R.drawable.unselect_icon)
                binding.weekly.background = resources.getDrawable(R.drawable.gradient_inapp)
                binding.oneMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.threeMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.sixMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                InAppClass.launch_Subscription_billing_flow(this, InAppClass.oneWeekDetail!!)
            }catch (e:Exception){
                Log.d("skdjfs",e.message.toString())
                e.printStackTrace()
            }
        }
        binding.oneMonth.setOnClickListener {
            try {
                plan = "month"
                binding.radio1.setImageResource(R.drawable.unselect_icon)
                binding.radio2.setImageResource(R.drawable.check_icon)
                binding.radio3.setImageResource(R.drawable.unselect_icon)
                binding.radio4.setImageResource(R.drawable.unselect_icon)
                binding.weekly.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.oneMonth.background = resources.getDrawable(R.drawable.gradient_inapp)
                binding.threeMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.sixMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                InAppClass.launch_Subscription_billing_flow(this, InAppClass.oneMonthDetail!!)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        binding.threeMonth.setOnClickListener {
            try {
                plan="3month"
                binding.radio1.setImageResource(R.drawable.unselect_icon)
                binding.radio2.setImageResource(R.drawable.unselect_icon)
                binding.radio3.setImageResource(R.drawable.check_icon)
                binding.radio4.setImageResource(R.drawable.unselect_icon)
                binding.weekly.background=resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.oneMonth.background=resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.threeMonth.background=resources.getDrawable(R.drawable.gradient_inapp)
                binding.sixMonth.background=resources.getDrawable(R.drawable.rect_unselected_bg)
                InAppClass.launch_Subscription_billing_flow(this, InAppClass.threeMonthDetail!!)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        binding.sixMonth.setOnClickListener {
            try {
                plan = "lifetime"
                binding.radio1.setImageResource(R.drawable.unselect_icon)
                binding.radio2.setImageResource(R.drawable.unselect_icon)
                binding.radio3.setImageResource(R.drawable.unselect_icon)
                binding.radio4.setImageResource(R.drawable.check_icon)

                binding.weekly.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.oneMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.threeMonth.background = resources.getDrawable(R.drawable.rect_unselected_bg)
                binding.sixMonth.background = resources.getDrawable(R.drawable.gradient_inapp)
                InAppClass.launch_One_Time_Purchase_billing_flow(this, InAppClass.lifetimeDetail!!)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    private fun getStyledSpannable(startingText: String, restOfText: String): SpannableString {
        val spannableString = SpannableString("$startingText / $restOfText")
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            startingText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(1.0f), // Adjust this value to increase/decrease the text size
            startingText.length + 3, // +3 to account for " / "
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }


}
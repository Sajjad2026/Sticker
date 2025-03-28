package com.nova.pack.stickers.ads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import com.nova.pack.stickers.R
import com.nova.pack.stickers.Utils
import com.nova.pack.stickers.activites.IntroActivity
import com.nova.pack.stickers.activites.LanguageSelectActivity
import com.nova.pack.stickers.activites.ReturningHomeScreen

class Constants {
    companion object{
        var name:String?=null
        var ifConsentVisible=false
        var file:String?=null
        var isSplashBannerLoad=false
        var isDataFetch=false
        var isConsentAccept=false
        var ifAdNotFetch=false
        var checkIfPlay = false
        var clickCounter = 0
        var frequencyLimit=false

        fun startButtonTask(context: Context){
            if (Utils.restorePrefData(context)) {
                val mainActivity = Intent(context, ReturningHomeScreen::class.java)
                context.startActivity(mainActivity)
                (context as Activity).finish()
            }
            else{
                val prefss = PreferenceManager.getDefaultSharedPreferences(context)
                val isFirstLaunch = prefss.getBoolean("isFirstLaunchLanguage", true)
                if (isFirstLaunch) {
                    val intent = Intent(context, LanguageSelectActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }else{
                    val intent = Intent(context, IntroActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                }
            }
        }
        fun exportDialog(context: Context, unit : (String)->Unit){
            var isWhatsapp="whatsapp"
           Dialog(context).apply {
               setCanceledOnTouchOutside(false)
               window?.setBackgroundDrawableResource(android.R.color.transparent)
               setContentView(R.layout.export_dailog)
               show()
               findViewById<AppCompatButton>(R.id.cancel_btn).setOnClickListener {
                   dismiss()
               }
               findViewById<ImageView>(R.id.btn_close).setOnClickListener {
                   dismiss()
               }
               findViewById<AppCompatButton>(R.id.btn_export).setOnClickListener {
                   dismiss()
                   unit(isWhatsapp)
               }
               findViewById<RelativeLayout>(R.id.card_1).setOnClickListener {
                   isWhatsapp=("whatsapp")
                   findViewById<ImageView>(R.id.radio_1).setImageResource(R.drawable.check_icon)
                   findViewById<ImageView>(R.id.radio_2).setImageResource(R.drawable.unselect_icon)
               }
               findViewById<RelativeLayout>(R.id.card_2).setOnClickListener {
                   isWhatsapp=("business")
                   findViewById<ImageView>(R.id.radio_1).setImageResource(R.drawable.unselect_icon)
                   findViewById<ImageView>(R.id.radio_2).setImageResource(R.drawable.check_icon)
               }
           }
        }
    }
}
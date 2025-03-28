package com.nova.pack.stickers.utils

import android.os.CountDownTimer
import androidx.lifecycle.LifecycleObserver
import com.nova.pack.stickers.ads.AdsManager.ShowAppopenAfterTime
import com.nova.pack.stickers.ads.AdsManager.ShowInterstitialAfterTime

object CountDownTimer {
    var countDown1: CountDownTimer?=null
    var countDown2: CountDownTimer?=null
    var showInterstitial=true
    var showAppOpen=true
    fun startTimerInterstitial() {
        countDown1 = object : CountDownTimer(ShowInterstitialAfterTime.toLong(), 1000), LifecycleObserver {
            override fun onTick(millisUntilFinished: Long) {
                showInterstitial=false
            }

            override fun onFinish() {
                showInterstitial=true
            }
        }
        countDown1?.start()
    }
    fun startTimerAppOpen() {
        countDown2= object : CountDownTimer(ShowAppopenAfterTime.toLong(), 1000), LifecycleObserver {
            override fun onTick(millisUntilFinished: Long) {
                showAppOpen=false
            }

            override fun onFinish() {
                showAppOpen=true
            }
        }
        countDown2?.start()
    }
}
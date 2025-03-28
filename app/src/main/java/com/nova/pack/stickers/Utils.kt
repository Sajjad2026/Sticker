package com.nova.pack.stickers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class Utils {
    companion object{
        fun savePrefsData(context:Context) {
            val pref = context.getSharedPreferences("appPrefs",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = pref.edit()
            editor.putBoolean("isIntroOpened", true)
            editor.apply()
        }
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }


            fun restorePrefData(context: Context): Boolean {
            val pref = context.getSharedPreferences("appPrefs", AppCompatActivity.MODE_PRIVATE)
            return pref.getBoolean("isIntroOpened", false)
        }

    }
}
package com.nova.pack.stickers.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nova.pack.stickers.model.StickerPackView

class SharedPreferenceHelper {

    fun <T> saveObjectsList(key: String?, objectList: List<T>?) {
        val objectString = Gson().toJson(objectList)
        val editor = mSharedPreferences!!.edit()
        editor.putString(key, objectString)
        editor.apply()
    }


    fun getObjectsStickerPackViewList(key: String): List<StickerPackView> {
        if (isKeyExists(key)) {
            try {
                val objectString = mSharedPreferences!!.getString(key, null)
                if (objectString != null) {
                    val t: ArrayList<StickerPackView> =
                        Gson().fromJson(
                            objectString,
                            object : TypeToken<List<StickerPackView>?>() {}.type
                        )
                    val finalList: MutableList<StickerPackView> = ArrayList()
                    for (i in 0 until t.size) {
                        finalList.add(
                            StickerPackView(
                                t[i].category_name,
                                t[i].icon,
                                t[i].androidPlayStoreLink,
                                t[i].iosAppStoreLink,
                                t[i].publisherEmail,
                                t[i].privacyPolicyWebsite,
                                t[i].licenseAgreementWebsite,
                                t[i].telegram_url,
                                t[i].identifier,
                                t[i].name,
                                t[i].publisher,
                                t[i].publisherWebsite,
                                t[i].animatedStickerPack,
                                t[i].stickers,
                                t[i].trayImageFile
                            )
                        )
                    }
                    return finalList
                }
            }catch (e:Exception){
                e.printStackTrace()
            }catch (e:IllegalStateException){
                e.printStackTrace()
            }
        }
        return emptyList()
    }



    private fun isKeyExists(key: String): Boolean {
        val map = mSharedPreferences!!.all
        return if (map.containsKey(key)) {
            true
        } else {
            Log.e("SharedPreferences", "No element founded in sharedPrefs with the key $key")
            false
        }
    }
    fun getLastSessionDate(): String? {
        return mSharedPreferences?.getString("last_session_date", null)
    }

    fun saveLastSessionDate(date: String) {
        mSharedPreferences?.edit()?.putString("last_session_date", date)?.apply()
    }
    companion object {
        private var mSharedPreferences: SharedPreferences? = null
        fun saveSession(key: String?, value: Boolean) {
            val editor = mSharedPreferences!!.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun saveSessionNumber(key: String?, value: Int) {
            val editor = mSharedPreferences!!.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getSessionNumber(key: String, defaultValue: Int): Int {
            return mSharedPreferences?.getInt(key, defaultValue)!!
        }

        fun getSession(key: String, defaultValue: Boolean): Boolean {
            return mSharedPreferences?.getBoolean(key, defaultValue)!!
        }
        fun init(context: Context?) {
            mSharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        }


        // Define a key for SharedPreferences
        private const val PREFS_NAME = "my_prefs"
        private const val KEY_RANDOM_VALUE = "random_value"

        // Function to get the SharedPreferences instance
         fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        // Function to get the saved random value
         fun getSavedRandomValue(context: Context): String? {
            return getPreferences(context).getString(KEY_RANDOM_VALUE, null)
        }

        // Function to save the random value
         fun saveRandomValue(context: Context, value: String) {
            val editor = getPreferences(context).edit()
            editor.putString(KEY_RANDOM_VALUE, value)
            editor.apply()
        }

    }
}
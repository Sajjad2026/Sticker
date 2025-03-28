package com.nova.pack.stickers.adapter

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nova.pack.stickers.R
import com.nova.pack.stickers.ads.AdsManager.IsLanguageSelectionFirstTime
import com.nova.pack.stickers.listener.CheckLanguage
import com.nova.pack.stickers.model.Languages


class LanguagesAdapter(
    private var context: Context,
    private var arrayList: ArrayList<Languages>,
    private var checkListener: CheckLanguage
) : RecyclerView.Adapter<LanguagesAdapter.MainHolder>() {
    var isEnglish=false
    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var languages: TextView = itemView.findViewById(R.id.txt_languages)
        var radio: ImageView = itemView.findViewById(R.id.radio_language)
        var ccp: ImageView = itemView.findViewById(R.id.img_flag)
        var card: RelativeLayout = itemView.findViewById(R.id.card_languages)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.languages_items, parent, false)
        return MainHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val data = arrayList[position]
        holder.languages.text = data.languageName
        holder.ccp.setImageDrawable(data.drawable)


        // holder.radio.isChecked = data.isChecked
        if (data.isChecked==false){
            val colorBackground: Int = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground)).getColor(0, 0)
            val colorText: Int = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimary)).getColor(0, 0)
            holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.labguage_bg))
           // holder.card.setBackgroundColor(context.getColor(R.color.bg_color))
            holder.languages.setTextColor(colorText)
            holder.radio.setImageResource(R.drawable.unselect_icon)
          //  holder.radio.visibility=View.GONE
        }


        val prefss = PreferenceManager.getDefaultSharedPreferences(context)
        val isFirstLaunch = prefss.getBoolean("isFirstLaunchLanguage", true)
        if (isFirstLaunch) {
            if (data.languageName=="English" && !isEnglish){
                if (IsLanguageSelectionFirstTime) {
                    // holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.labguage_bg))
//                holder.card.backgroundTintList= context.resources.getColorStateList(R.color.bg_color)
                    holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.languange_bg_))
                //    holder.card.setBackgroundColor(context.getColor(R.color.bg_color))
                    holder.languages.setTextColor(context.getColor(R.color.white))
                    holder.radio.visibility=View.VISIBLE
                    holder.radio.setImageResource(R.drawable.language_check)
                    checkListener?.isLanguage(data.isChecked, data.languages, data.languageName)
                }
            }
        }else{
            var sharedPreferences=context.getSharedPreferences("myLanguages",0)
            var lang=sharedPreferences.getString("language","en")
            if (data.languages==lang && !isEnglish){
                Log.d("sdsds",lang)
                holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.languange_bg_))
             //   holder.card.backgroundTintList= context.resources.getColorStateList(R.color.bg_color)
            //    holder.card.setBackgroundColor(context.getColor(R.color.bg_color))
                holder.languages.setTextColor(context.getColor(R.color.white))
                holder.radio.visibility=View.VISIBLE
                holder.radio.setImageResource(R.drawable.language_check)
            }
        }

        holder.ccp.setOnClickListener {
            Log.d("xccppo","click")
        }

        holder.card.setOnClickListener {
            if (!data.isChecked) {
                holder.radio.setImageResource(R.drawable.language_check)
                data.isChecked=true
                isEnglish=true
                arrayList.forEach { it.isChecked = false }
                holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.labguage_bg))
                //holder.card.backgroundTintList= context.resources.getColorStateList(R.color.bg_color)
                holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.languange_bg_))
                holder.languages.setTextColor(context.getColor(R.color.white))
                holder.radio.visibility=View.VISIBLE
                // Check the tapped item
                data.isChecked = true

                // Notify the adapter that the data set has changed
                notifyDataSetChanged()

                // Notify the check listener if available
                checkListener?.isLanguage(data.isChecked, data.languages, data.languageName)
            }
        }
        holder.ccp.setOnClickListener {
            if (!data.isChecked) {
                holder.radio.setImageResource(R.drawable.language_check)
                data.isChecked=true
                isEnglish=true
                arrayList.forEach { it.isChecked = false }
                holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.labguage_bg))
                //holder.card.backgroundTintList= context.resources.getColorStateList(R.color.bg_color)
                holder.card.setBackgroundDrawable(context.resources.getDrawable(R.drawable.languange_bg_))
                holder.languages.setTextColor(context.getColor(R.color.white))
                holder.radio.visibility=View.VISIBLE
                // Check the tapped item
                data.isChecked = true
                // Notify the adapter that the data set has changed
                notifyDataSetChanged()
                // Notify the check listener if available
                checkListener?.isLanguage(data.isChecked, data.languages, data.languageName)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
package com.mkrdeveloper.weatherapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mkrdeveloper.weatherapp.R
import com.mkrdeveloper.weatherapp.forecastModels.Forecast
import com.squareup.picasso.Picasso

class RvAdapter(private val itemList: List<Forecast>) :
    RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTime: TextView = itemView.findViewById(R.id.tv_Item_time)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_item_status)
        val tvTemp: TextView = itemView.findViewById(R.id.tv_item_temp)

        val imgIcon: ImageView = itemView.findViewById(R.id.img_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = itemList[position]

        val iconId = currentItem.weather[0].icon

        val imageUrl = "https://openweathermap.org/img/w/$iconId.png"
        holder.tvTime.text = currentItem.dt_txt
        holder.tvStatus.text = currentItem.weather[0].description
        holder.tvTemp.text = "${currentItem.main.temp.toInt()} °C"

        Picasso.get().load(imageUrl).into(holder.imgIcon)

        //holder.imgIcon.setImageResource(imageUrl)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
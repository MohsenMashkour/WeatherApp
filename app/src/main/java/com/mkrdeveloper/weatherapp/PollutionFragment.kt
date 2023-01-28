package com.mkrdeveloper.weatherapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.blue
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class PollutionFragment : Fragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pollution, container, false)

        val txt = view.findViewById<TextView>(R.id.tv_fr_pollution)


        val data = arguments

        val co: Double? = data?.getDouble("co")
        val nh3: Double? = data?.getDouble("nh3")
        val no: Double? = data?.getDouble("no")
        val no2: Double? = data?.getDouble("no2")
        val o3: Double? = data?.getDouble("o3")
        val pm10: Double? = data?.getDouble("pm10")
        val pm2_5: Double? = data?.getDouble("pm2_5")
        val so2: Double? = data?.getDouble("so2")

        txt.text = " co: $co\n nh3: $nh3\n no: $no\n" +
                " no2: $no2\n o3: $o3\n" +
                " pm10: $pm10\n pm2_5: $pm2_5\n" +
                " so2: $so2\n"

        // Toast.makeText(context, ""+co+nh3+no+no2+o3+pm10+pm2_5+so2,Toast.LENGTH_LONG).show()


        val barChart = view.findViewById<BarChart>(R.id.bar_chart)


        val list: ArrayList<BarEntry> = ArrayList()


        if (co != null) {
            list.add(BarEntry(1f, co.toFloat()))
        }
        if (nh3 != null) {
            list.add(BarEntry(2f, nh3.toFloat()))
        }
        if (no != null) {
            list.add(BarEntry(3f, no.toFloat()))
        }
        if (no2 != null) {
            list.add(BarEntry(4f, no2.toFloat()))
        }
        if (o3 != null) {
            list.add(BarEntry(5f, o3.toFloat()))
        }
        if (pm10 != null) {
            list.add(BarEntry(6f, pm10.toFloat()))
        }
        if (pm2_5 != null) {
            list.add(BarEntry(7f, pm2_5.toFloat()))
        }
        if (so2 != null) {
            list.add(BarEntry(8f, so2.toFloat()))
        }


        val barDataSet = BarDataSet(list, "")

        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS, 255)

        barDataSet.valueTextColor = Color.BLACK

        barDataSet.barBorderColor = Color.BLACK

        /*barDataSet.colors = mutableListOf(
            Color.YELLOW,
            Color.BLUE,
            Color.GREEN,
            Color.CYAN,
            Color.DKGRAY,
            Color.BLACK,
            Color.MAGENTA,
            Color.RED
        )*/
        barDataSet.barBorderWidth = 1f


        val barData = BarData(barDataSet)

        barChart.data = barData

        barChart.description.text = "Air pollution"
        barChart.animateY(2000)

        barChart.setDrawValueAboveBar(false)

        val quarters = arrayOf("", "co", "nh3", "no", "NO2", "PM10", "O3", "PM25", "so2")
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return quarters[value.toInt()]
            }
        }

        val xAxis: XAxis = barChart.xAxis


        xAxis.granularity = 1f

        xAxis.valueFormatter = formatter



        return view
    }


}
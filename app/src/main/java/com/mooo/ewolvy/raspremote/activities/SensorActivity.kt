package com.mooo.ewolvy.raspremote.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mooo.ewolvy.raspremote.CommandManager
import com.mooo.ewolvy.raspremote.R
import com.mooo.ewolvy.raspremote.database.Device
import kotlinx.android.synthetic.main.activity_sensor.*
import org.json.JSONArray


class SensorActivity : AppCompatActivity() {

    val device: Device by lazy { getDevice(intent.extras) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        title = "${this.getString(this.applicationInfo.labelRes)}: ${device.name}"

        val xAxes = ArrayList<String>()
        val yAxesTemps = ArrayList<Entry>()
        val yAxesHumidity = ArrayList<Entry>()

        CommandManager.sendCommand(
            device.getFullAddress(),
            device.username,
            device.password,
            "")
        { this.runOnUiThread{
            val response = JSONArray(it)
            for (index in 0 until (response.length())){
                val dataObject = response.getJSONObject(index)
                yAxesTemps.add(Entry(index.toFloat(), dataObject.getDouble("temp").toFloat()))
                yAxesHumidity.add(Entry(index.toFloat(), dataObject.getDouble("humidity").toFloat()))
                xAxes.add(dataObject.getString("time"))
            }
            val lineDataSets = ArrayList<ILineDataSet>()

            val lineDataSetTemp = LineDataSet(yAxesTemps, "Temperature")
            lineDataSetTemp.setDrawCircles(true)
            lineDataSetTemp.color = Color.BLUE

            val lineDataSetHum = LineDataSet(yAxesHumidity, "Humidity")
            lineDataSetHum.setDrawCircles(true)
            lineDataSetHum.color = Color.RED

            lineDataSets.add(lineDataSetTemp)
            lineDataSets.add(lineDataSetHum)

            linechart_sensor_chart.data = LineData(lineDataSets)
            linechart_sensor_chart.minOffset = 0f
            linechart_sensor_chart.invalidate()
        }
        }

        /*val xAXES: ArrayList<String> = ArrayList()
        val yAXESsin: ArrayList<Entry> = ArrayList()
        val yAXEScos: ArrayList<Entry> = ArrayList()
        var x = 0.0
        for(i in 1..1000){
            val sinFunction = sin(x).toString().toFloat()
            val cosFunction = cos(x).toString().toFloat()
            x += 0.1
            yAXESsin.add(Entry(i.toFloat(), sinFunction))
            yAXEScos.add(Entry(i.toFloat(), cosFunction))
            xAXES.add(x.toString())
        }
        val xaxes = arrayOfNulls<String>(xAXES.size)
        for (i in xAXES.indices) {
            xaxes[i] = xAXES[i]
        }

        val lineDataSets = ArrayList<ILineDataSet>()

        val lineDataSet1 = LineDataSet(yAXEScos, "cos")
        lineDataSet1.setDrawCircles(false)
        lineDataSet1.color = Color.BLUE

        val lineDataSet2 = LineDataSet(yAXESsin, "sin")
        lineDataSet2.setDrawCircles(false)
        lineDataSet2.color = Color.RED

        lineDataSets.add(lineDataSet1)
        lineDataSets.add(lineDataSet2)

        linechart_sensor_chart.data = LineData(lineDataSets)

        linechart_sensor_chart.setVisibleXRangeMaximum(65f)*/
    }

    private fun getDevice (extras: Bundle?): Device{
        return extras?.getParcelable("DEVICE") ?: Device()
    }
}

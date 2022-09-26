package com.kaimiya.gaurav.weather.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kaimiya.gaurav.weather.R
import com.kaimiya.gaurav.weather.model.ThreeHourWeather

private const val STROKE_WIDTH = 7f
private const val PATH_CORNER_RADIUS_IN_DP = 20
private const val CHILD_HEADER_OR_FOOTER_HEIGHT_IN_DP = 50

internal class TemperatureTrendGraphLinearItemDecorator(listTemperature: List<ThreeHourWeather>, context: Context) :
    RecyclerView.ItemDecoration() {

    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = STROKE_WIDTH
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = ContextCompat.getColor(context, R.color.white)
        pathEffect = CornerPathEffect(PATH_CORNER_RADIUS_IN_DP.dpToPx)
    }

    private val normalizedDayTemperatureValues = normalizeDayTemperatureValues(listTemperature)

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        for (childIndex in 0 until parent.childCount) {
            val childView = parent.getChildAt(childIndex)
            val dataIndex = parent.getChildAdapterPosition(childView)
            val childViewHeight = childView.height

            canvas.drawLine(
                childView.left.toFloat(),
                calculateYValue(dataIndex, childViewHeight),
                childView.right.toFloat(),
                calculateYValue(getDataIndexOfNextChild(dataIndex), childViewHeight),
                drawPaint
            )

        }
    }

    private fun getDataIndexOfNextChild(currentChildDataIndex: Int): Int {
        val nextChildDataIndex = currentChildDataIndex + 1
        return if (nextChildDataIndex >= normalizedDayTemperatureValues.size) {
            currentChildDataIndex
        } else {
            nextChildDataIndex
        }
    }

    private fun calculateYValue(dataIndex: Int, childViewHeight: Int): Float {
        val graphHeight = childViewHeight - (CHILD_HEADER_OR_FOOTER_HEIGHT_IN_DP * 3).dpToPx
        val graphStartHeightDelta = (CHILD_HEADER_OR_FOOTER_HEIGHT_IN_DP*2).dpToPx

        return ((1 - normalizedDayTemperatureValues[dataIndex]) * graphHeight + graphStartHeightDelta).toFloat()
    }

    private fun normalizeDayTemperatureValues(listTemperatures: List<ThreeHourWeather>): List<Double> {
        val minTemperature = listTemperatures.minByOrNull { it.main.temp }
        val maxTemperature = listTemperatures.maxByOrNull { it.main.temp }

        if (minTemperature == null || maxTemperature == null) {
            return emptyList()
        }

        if (minTemperature.main.temp >= maxTemperature.main.temp) {
            return listTemperatures.map { 0.5 }
        }

        val range = maxTemperature.main.temp - minTemperature.main.temp
        return listTemperatures.map {
            val relativeValue = it.main.temp - minTemperature.main.temp
            return@map (relativeValue / range)
        }
    }
}

val Int.dpToPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

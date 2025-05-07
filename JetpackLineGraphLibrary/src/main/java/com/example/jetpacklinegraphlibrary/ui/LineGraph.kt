package com.example.jetpacklinegraphlibrary.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacklinegraphlibrary.model.DataPoint

@Composable
fun LineGraph(
    data: List<DataPoint>,
    modifier: Modifier = Modifier,
    maxY: Float = 2500f,
    nationalAverage: Float = 1500f,
    graphColor: Color = Color(0xFF3B82F6),
    averageLineColor: Color = Color(0xFF10B981),
    gridColor: Color = Color.LightGray,
    labelTextColor: Color = Color.Gray,
    labelTextSizeSp: TextUnit = 12.sp,
    strokeWidth: Float = 4f,
    circleRadius: Float = 6f,
    labelStep: Int = 500,
    spacing: Float = 80f,
    cardBackgroundColor: Color = Color.White,
    showCircleOnPoints: Boolean = false,
    showVerticalLine: Boolean = false,
    yLabel: String = "Kg",
    xLabel: String = "National Average",
) {
    val labelTextSizePx = with(LocalDensity.current) { labelTextSizeSp.toPx() }
    val scrollState = rememberScrollState()
    val canvasWidth = (data.size * 62).dp

    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth(),
        ) {
            Text(
                text = yLabel,
                color = labelTextColor,
                fontSize = labelTextSizeSp,
                modifier = Modifier.padding(start = 25.dp, top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(8.dp)
            ) {
                Box(modifier = Modifier.horizontalScroll(scrollState)) {
                    Canvas(
                        modifier = Modifier
                            .width(canvasWidth)
                            .height(300.dp)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        val spacePerData = (width - spacing) / (data.size - 1)

                        val indexedPoints = data.mapIndexedNotNull { index, point ->
                            if (point.value == 0f) null else
                                index to Offset(
                                    x = spacing + index * spacePerData,
                                    y = height - (point.value / maxY) * height
                                )
                        }

                        val points = indexedPoints.map { it.second }

                        val labelPaint = android.graphics.Paint().apply {
                            color = labelTextColor.toArgb()
                            textSize = labelTextSizePx
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }

                        val labelCount = (maxY / labelStep).toInt()

                        for (i in 0..labelCount) {
                            val y = height - (i * labelStep / maxY) * height
                            drawLine(
                                color = gridColor.copy(alpha = 0.4f),
                                start = Offset(spacing, y),
                                end = Offset(width, y),
                                strokeWidth = 4f
                            )
                            drawContext.canvas.nativeCanvas.drawText(
                                (i * labelStep).toString(),
                                spacing - 12f,
                                y + labelTextSizePx / 3,
                                labelPaint
                            )
                        }

                        drawLine(
                            color = gridColor,
                            start = Offset(spacing, height),
                            end = Offset(size.width, height),
                            strokeWidth = 10f
                        )

                        data.forEachIndexed { index, point ->
                            val x = spacing + index * spacePerData
                            drawLine(
                                color = gridColor,
                                start = Offset(x, height),
                                end = Offset(x, height - 20),
                                strokeWidth = 2f
                            )

                            drawContext.canvas.nativeCanvas.drawText(
                                point.month,
                                x,
                                height + 50,
                                android.graphics.Paint().apply {
                                    color = labelTextColor.toArgb()
                                    textSize = labelTextSizePx
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }

                        // If the first point is not at index 0, draw vertical line from bottom to the first point
                        if (showVerticalLine){
                            if (indexedPoints.isNotEmpty() && indexedPoints.size > 1) {
                                val firstVisibleIndex = indexedPoints.first().first
                                if (firstVisibleIndex != 0) {
                                    val firstVisiblePoint = indexedPoints.first().second
                                    drawLine(
                                        color = graphColor,
                                        start = Offset(firstVisiblePoint.x, height),
                                        end = Offset(firstVisiblePoint.x, firstVisiblePoint.y),
                                        strokeWidth = 4f
                                    )
                                }

                                // Draw vertical line at end if the next point is zero
                                val lastVisibleIndex = indexedPoints.last().first
                                if (lastVisibleIndex < data.lastIndex && data[lastVisibleIndex + 1].value == 0f) {
                                    val lastVisiblePoint = indexedPoints.last().second
                                    drawLine(
                                        color = graphColor,
                                        start = Offset(lastVisiblePoint.x, height),
                                        end = Offset(lastVisiblePoint.x, lastVisiblePoint.y),
                                        strokeWidth = 4f
                                    )
                                }
                            }
                        }

                        val zeroIndices = data.mapIndexedNotNull { index, point -> if (point.value == 0f) index else null }

                        if (zeroIndices.size == data.size || indexedPoints.size < 2) {
                            val centerIndex = zeroIndices.average().toFloat()
                            val centerX = spacing + centerIndex * spacePerData
                            val centerY = height / 2
                            drawContext.canvas.nativeCanvas.drawText(
                                "No Data",
                                centerX,
                                centerY,
                                android.graphics.Paint().apply {
                                    color = labelTextColor.toArgb()
                                    textSize = labelTextSizePx + 30
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                            return@Canvas
                        }

                        val zeroRanges = mutableListOf<Pair<Int, Int>>()
                        var start: Int? = null
                        for (i in data.indices) {
                            if (data[i].value == 0f) {
                                if (start == null) start = i
                            } else {
                                if (start != null) {
                                    zeroRanges.add(start to i - 1)
                                    start = null
                                }
                            }
                        }
                        if (start != null) zeroRanges.add(start to data.lastIndex)

                        zeroRanges.forEach { (startIndex, endIndex) ->
                            val centerIndex = (startIndex + endIndex) / 2f
                            val centerX = spacing + centerIndex * spacePerData
                            val centerY = height / 2

                            drawContext.canvas.nativeCanvas.drawText(
                                "No Data",
                                centerX + 100f,
                                centerY + 20f,
                                android.graphics.Paint().apply {
                                    color = labelTextColor.toArgb()
                                    textSize = labelTextSizePx + 20
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }

                        val smoothPath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val p0 = points[i - 1]
                                val p1 = points[i]
                                val controlX = (p0.x + p1.x) / 2
                                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            }
                            lineTo(points.last().x, height)
                            lineTo(points.first().x, height)
                            close()
                        }

                        drawPath(
                            smoothPath,
                            brush = Brush.verticalGradient(
                                listOf(graphColor.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )

                        val linePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val p0 = points[i - 1]
                                val p1 = points[i]
                                val controlX = (p0.x + p1.x) / 2
                                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            }
                        }

                        drawPath(
                            linePath,
                            color = graphColor,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        if (showCircleOnPoints) {
                            points.forEach {
                                drawCircle(color = graphColor, radius = circleRadius, center = it)
                            }
                        }

                        val firstNonZeroPoint = points.firstOrNull { it.y != height }
                        if (firstNonZeroPoint != null) {
                            val avgY = height - (nationalAverage / maxY) * height
                            drawLine(
                                color = averageLineColor,
                                start = Offset(firstNonZeroPoint.x, avgY),
                                end = Offset(points.last().x, avgY),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                                strokeWidth = 4f
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, bottom = 8.dp), // optional padding for spacing from edge
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = xLabel,
                    color = labelTextColor,
                    fontSize = labelTextSizeSp,
                    textAlign = TextAlign.End
                )
            }

        }
    }
}
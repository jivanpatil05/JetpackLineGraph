package com.example.jetpacklinegraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.jetpacklinegraph.ui.theme.JetpackLineGraphTheme
import com.example.jetpacklinegraphlibrary.model.DataPoint
import com.example.jetpacklinegraphlibrary.ui.LineGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackLineGraphTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        val dataPoints = mutableListOf(
                            DataPoint("Jan", 0f),
                            DataPoint("Feb", 0f),
                            DataPoint("Mar", 1200f),
                            DataPoint("Apr", 2000f),
                            DataPoint("May", 1500f),
                            DataPoint("Jun", 1600f),
                        )
                        LineGraph(
                            data = dataPoints,
                            maxY = 2500f,
                            nationalAverage = 1500f,
                            graphColor = Color.Blue,
                            averageLineColor = Color.Green,
                            gridColor = Color.Gray,
                            labelTextColor = Color.Black,
                            labelTextSizeSp = 14.sp,
                            strokeWidth = 5f,
                            circleRadius = 5f,
                            labelStep = 500,
                            spacing = 80f,
                            cardBackgroundColor = Color.White,
                            showCircleOnPoints = true,
                            showVerticalLine = true
                        )
                    }
                }*/
            }
        }
    }
}


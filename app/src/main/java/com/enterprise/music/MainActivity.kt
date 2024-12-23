package com.enterprise.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.enterprise.music.ui.theme.MusicTheme
import com.enterprise.music.ui.theme.Teal200
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            MusicTheme {

                MusicApp(mainViewModel = mainViewModel)

            }
        }
    }
}

@Composable
fun MusicApp(mainViewModel: MainViewModel) {

    val isProgressBarVisible = mainViewModel.isProgressBarVisible

    val audioFrame = mainViewModel.audioFrame

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)){

        Scaffold(modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()) { innerPadding ->

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = Color.White)) {

                if (isProgressBarVisible.value) {

                    CircularProgressIndicator(color = Color.Green)

                } else {

                    Text(text = stringResource(id = R.string.main_activity_music_visualizer_title), color = Color.Blue, fontSize = 25.sp)

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .border(5.dp, color = Teal200)
                            .padding(10.dp)
                    ) {

                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val canvasHalfHeight = canvasHeight / 2

                        drawLine(
                            start = Offset(x = 0.dp.toPx(), canvasHalfHeight),
                            end = Offset(x = canvasWidth, canvasHalfHeight),
                            color = Color.Blue,
                            strokeWidth = 5.dp.toPx() // instead of 5.dp.toPx() , you can also pass 5f
                        )

                        var maxAmplitudeValue = 0f
                        var minAmplittudeValue = 0f
                        var maxOfAbsoluteValueOfAmplitudes = 0f

                        try {

                            //When the max and min of the audio frame is used,
                            //audio frame signal fills the borders of the graph
                            //Also -1 and 1 can be used, see below
                            maxAmplitudeValue = audioFrame.max()
                            minAmplittudeValue = audioFrame.min()

                            //Audio signal is normalized between -1 and 1
                            //maxAmplitudeValue = 1f
                            //minAmplittudeValue = -1f

                            val listOfAbsoluteVAlueOfMaxAndMin =
                                arrayListOf(abs(maxAmplitudeValue), abs(minAmplittudeValue))
                            maxOfAbsoluteValueOfAmplitudes = listOfAbsoluteVAlueOfMaxAndMin.max()

                        } catch (exception: Exception) {


                        }


                        var tempInput = arrayListOf<Float>()
                        tempInput.addAll(audioFrame)

                        var yVAlues: ArrayList<Float> = getYVAlues(
                            maxOfAbsoluteValueOfAmplitudes = maxOfAbsoluteValueOfAmplitudes,
                            canvasHalfHeight = canvasHalfHeight,
                            audioData = tempInput
                        )

                        var xValues: ArrayList<Float> = getXVAlues(
                            canvasWidth = canvasWidth,
                            elementsSize = audioFrame.size
                        )

                        val strokeWidth = 5.dp.toPx()

                        for (i in 0..yVAlues.size - 1) {

                            var xVAlue = xValues[i]
                            var yValue = yVAlues[i]

                            drawLine(
                                start = Offset(xVAlue, y = canvasHalfHeight),
                                end = Offset(xVAlue, y = canvasHalfHeight - yValue),
                                color = Color.Green,
                                strokeWidth = strokeWidth // instead of 5.dp.toPx() , you can also pass 5f
                            )


                        }


                    }
                }
            }

        }
    }
}

fun getXVAlues(canvasWidth: Float, elementsSize: Int): ArrayList<Float> {

    val output = arrayListOf<Float>()

    var dx = canvasWidth / (elementsSize-1)

    for(i in 0..elementsSize-1){

        output.add(dx*i)

    }

    return output

}

fun getYVAlues(
    maxOfAbsoluteValueOfAmplitudes: Float,
    canvasHalfHeight: Float,
    audioData: ArrayList<Float>
): ArrayList<Float> {

    val output = arrayListOf<Float>()

    var ratio = canvasHalfHeight / maxOfAbsoluteValueOfAmplitudes;

    for(amplitude in audioData){

        output.add(amplitude*ratio)

    }

    return output

}



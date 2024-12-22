package com.enterprise.music

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext val context: Context)
    : ViewModel(){

    var durationOfFrameMilliSecond: Long = 20
    val audioFrame = mutableStateListOf<Float>()
    val isProgressBarVisible = mutableStateOf(false)

    init{

        viewModelScope.launch(Dispatchers.IO) {

            viewModelScope.launch(Dispatchers.Main){
                isProgressBarVisible.value = true
            }

            val audioFrameFlow =
                AppAudioDataReader.readAudioFrame(rawID = R.raw.music,
                    context = context,
                    durationOfFrameMilliSecond = durationOfFrameMilliSecond)

            audioFrameFlow.collect{ tempAudioFrame ->

                delay(durationOfFrameMilliSecond)

                viewModelScope.launch(Dispatchers.Main) {

                    isProgressBarVisible.value = false

                    audioFrame.clear()
                    audioFrame.addAll(tempAudioFrame)

                }


            }

        }

    }

}
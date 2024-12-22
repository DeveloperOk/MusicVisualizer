package com.enterprise.music

data class AppAudioData(
    val sampleRate: Long,
    val bitsPerSample: Long,
    val audioSignal:  ArrayList<Float>
){

}
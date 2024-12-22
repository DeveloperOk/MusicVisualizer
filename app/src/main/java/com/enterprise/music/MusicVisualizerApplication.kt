package com.enterprise.music

import android.app.Application
import android.content.Context


class MusicVisualizerApplication : Application(){

    //Context classes should not be put into static fields, it can cause memory leaks,
    //but this for testing purposes
    companion object{
        lateinit var musicVisualizerApplicationContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        musicVisualizerApplicationContext = this

    }

}
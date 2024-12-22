package com.enterprise.music

import android.content.Context
import java.io.IOException
import java.io.InputStream
import kotlin.math.pow

class AppAudioDataReader {

    companion object{

        /** ------------ Load Audio ------------  */
        /** Helper for Reading Audio  */ /* Converting bytes to int */
        fun convert2LittleEndianBytesToLong(buffer: ByteArray): Long {
            var value = (buffer[1].toInt() and 0xFF).toLong()
            value = (value shl 8) + (buffer[0].toInt() and 0xFF)
            return value
        }

        fun convert4LittleEndianBytesToLong(buffer: ByteArray): Long {
            var value = (buffer[3].toInt() and 0xFF).toLong()
            value = (value shl 8) + (buffer[2].toInt() and 0xFF)
            value = (value shl 8) + (buffer[1].toInt() and 0xFF)
            value = (value shl 8) + (buffer[0].toInt() and 0xFF)
            return value
        }

        fun readAudio(rawID: Int, context: Context): AppAudioData {
            val WaveOut = ArrayList<Float>()
            var sampleRate: Long = 0
            var bitsPerSample: Long = 0
            try {
                val inputStream: InputStream = context.getResources().openRawResource(rawID)
                var read: Int

                /** Header Details  */
                /* Get ChunkID */
                val bytes_tmp = ByteArray(44)
                read = inputStream.read(bytes_tmp, 0, bytes_tmp.size)


                //Medium post for wav header bytes order
                //https://medium.com/@rizveeredwan/working-with-wav-files-in-android-52e9500297e
                sampleRate = convert4LittleEndianBytesToLong(bytes_tmp.slice(24..27).toByteArray())
                bitsPerSample = convert2LittleEndianBytesToLong(bytes_tmp.slice(34..35).toByteArray())


                /** Reading Wav file  */
                /* Reading WaveOut */
                //16 bit per sample redds 2 byte per sample and uses convert2LittleEndianBytesToLong function
                //for 32 bit per sample, it may be 4 byte per sample and use convert4LittleEndianBytesToLong function
                val bytes = ByteArray(2)
                var longtmp: Long
                while (read != -1) {
                    read = inputStream.read(bytes, 0, bytes.size)
                    longtmp = convert2LittleEndianBytesToLong(bytes)

                    var normalizedSample: Float = (longtmp.toFloat()/2.0.pow((bitsPerSample-1).toInt())).toFloat()
                    if(normalizedSample > 1){
                        normalizedSample = normalizedSample - 2
                    }

                    WaveOut.add(normalizedSample)

                }

                /** Close  */
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return AppAudioData(sampleRate = sampleRate, bitsPerSample = bitsPerSample, audioSignal = WaveOut)
        }


        /* Reading Audio */
        fun readAudio_ArrayListOld(rawID: Int, context: Context): ArrayList<Float> {
            val WaveOut = ArrayList<Float>()
            try {
                val inputStream: InputStream = context.getResources().openRawResource(rawID)
                var read: Int

                /** Header Details  */
                /* Get ChunkID */
                val bytes_tmp = ByteArray(44)
                read = inputStream.read(bytes_tmp, 0, bytes_tmp.size)

                /** Reading Wav file  */
                /* Reading WaveOut */
                val bytes = ByteArray(2)
                var longtmp: Long
                while (read != -1) {
                    read = inputStream.read(bytes, 0, bytes.size)
                    longtmp = convert2LittleEndianBytesToLong(bytes)
                    WaveOut.add(longtmp.toFloat())
                }

                /** Close  */
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return WaveOut
        }



    }

}
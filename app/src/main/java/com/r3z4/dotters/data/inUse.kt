package com.r3z4.dotters.data

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import java.io.Serializable
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.ObjectOutputStream
import java.io.ObjectInputStream
import java.util.*

fun <T : Serializable> deepCopy(obj: T?): T? {
    if (obj == null) return null
    val baos = ByteArrayOutputStream()
    val oos  = ObjectOutputStream(baos)
    oos.writeObject(obj)
    oos.close()
    val bais = ByteArrayInputStream(baos.toByteArray())
    val ois  = ObjectInputStream(bais)
    @Suppress("unchecked_cast")
    return ois.readObject() as T
}
    fun LottieAnimationView.playSideWithColors(first:Boolean,side:Int){
        Log.d("animation side","first=$first side=$side")
        //this.setBackgroundColor(Color.BLACK)
        val CIR_COLOR=Color.rgb(238,119,127)
        val CR_COLOR=Color.rgb(125,203,223)
        if (side==0)
            if (first) this.layerColorSet("Shape",CIR_COLOR)
            else {
                this.layerColorSet("ShapeInner",CIR_COLOR)
                this.layerColorSet("ShapeOuter",CR_COLOR)
            }
        else
            if (first) this.layerColorSet("Shape",CR_COLOR)
            else {
                this.layerColorSet("ShapeInner",CR_COLOR)
                this.layerColorSet("ShapeOuter",CIR_COLOR)
            }
        this.playAnimation()
    }
    fun LottieAnimationView.playMidWithColors(side:Int){
        //this.setBackgroundColor(Color.BLACK)
        Log.d("animation side","moveNumber=$side")
        val CIR_COLOR=Color.rgb(238,119,127)
        val CR_COLOR=Color.rgb(125,203,223)
        when(side){
            0-> {
                this.layerColorSet("OutLine",CIR_COLOR)
                this.layerColorSet("Inner",CIR_COLOR)
                this.layerColorSet("Stroke",CIR_COLOR)
                this.layerColorSet("InnerOld",CR_COLOR)
            }
            1-> {
                this.layerColorSet("OutLine",CR_COLOR)
                this.layerColorSet("Inner",CR_COLOR)
                this.layerColorSet("Stroke",CR_COLOR)
                this.layerColorSet("InnerOld",CIR_COLOR)
            }
            else-> {
                this.layerColorSet("OutLine",Color.WHITE)
                this.layerColorSet("Inner",Color.WHITE)
                this.layerColorSet("Stroke",Color.WHITE)
            }
        }
        this.playAnimation()
    }
    fun LottieAnimationView.layerColorSet(layer: String,color: Int){
        this.addValueCallback(
            KeyPath(layer, "**"),
            LottieProperty.COLOR_FILTER,
            { PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP) }
        )
    }
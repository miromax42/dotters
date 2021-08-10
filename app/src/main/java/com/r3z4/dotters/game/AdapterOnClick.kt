package com.r3z4.dotters.game

import androidx.lifecycle.LiveData
import com.r3z4.dotters.data.Dotter

interface AdapterOnClick {
    var id_changed:Int
    val side: LiveData<Int>
    fun onClick(item: Dotter)
    //fun getPicForState(item:Dotter)
}
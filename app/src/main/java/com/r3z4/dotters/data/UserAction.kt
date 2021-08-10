package com.r3z4.dotters.data

import kotlin.properties.Delegates

class UserAction (val action_number:Int, val id:Int, val startState:State, val endState:State) {
    fun undo(items:MutableList<Dotter>){
        items[this.id].state=this.startState
    }
    fun redo(items:MutableList<Dotter>){
        items[this.id].state=this.endState
    }
}
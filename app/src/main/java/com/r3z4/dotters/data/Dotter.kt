package com.r3z4.dotters.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.r3z4.dotters.R
import com.r3z4.dotters.game.GameViewModel
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.N)
class Dotter(
    var id: Int,
    val gameViewModel: GameViewModel
    ) {
//    val showSelfChage: (Dotter)->Unit={
//        //gameViewModel.showChange(this.id)
//    }
    //val stateObservers: MutableList<(Dotter) -> Unit> = mutableListOf(showSelfChage)
    val pid:Long=id.toLong()
    var prevState:ActiveState=ActiveState.EMPTY_OFF
    var nowState:ActiveState=ActiveState.EMPTY_OFF
        set(value) {
            prevState=field
            field = value
        }
    var state: State
    by Delegates.observable(State.EMPTY){
        _,old,new, ->
    if (old!=new) {

        nowState=getFullState()
        Log.d("dotter renew","state change id=${id} old=${old} new=${new}")
        gameViewModel.showChange(id)

    }
}


    var neighboursID:MutableList<Int> = mutableListOf()

    var isActive:Pair<Boolean,Boolean> by Delegates.observable(Pair(false,false)){
        _,old,new-> if ((old!=new) and (state!=State.CROSSED_ALIVE)and (state!=State.CIRCLED_ALIVE)){
        nowState=getFullState()
        Log.d("dotter renew","active1 change id=${id} old=${old} new=${new} statenow=${this.nowState} prev=${this.prevState}")
        gameViewModel.showChange(id)
    }
    }


    var isActive00=false
    var isActive11=false





    init {
        setNeighboursByID(this.id)
    }

    fun getTransition():Transition{
        return when (this.getFullState()){
            ActiveState.EMPTY_ON ->
                when(prevState){
                    ActiveState.EMPTY_OFF-> Transition.EMPTY_OFF_2_ON
                    ActiveState.CIRCLED_ALIVE-> Transition.CIR_ALIVE_2_EMPTY_ON
                    ActiveState.CROSSED_ALIVE-> Transition.CR_ALIVE_2_EMPTY_ON
                    ActiveState.EMPTY_ON-> Transition.EMPTY_ON_NT
                    else-> Transition.EMPTY_OFF_2_ON
                }
            ActiveState.EMPTY_OFF ->
                when(prevState){
                    ActiveState.EMPTY_ON-> Transition.EMPTY_ON_2_OFF
                    ActiveState.EMPTY_OFF-> Transition.EMPTY_OFF_NT
                    else->Transition.EMPTY_ON_2_OFF
            }
            ActiveState.CIRCLED_ALIVE ->
                when(prevState) {
                    ActiveState.EMPTY_ON -> Transition.EMPTY_ON_CIR_ALIVE
                    else -> Transition.EMPTY_ON_CIR_ALIVE
                }
            ActiveState.CROSSED_ALIVE ->
                when(prevState) {
                    ActiveState.EMPTY_ON -> Transition.EMPTY_ON_CR_ALIVE
                    else -> Transition.EMPTY_ON_CR_ALIVE
                }
            ActiveState.CIRCLED_DEAD_ON ->
                when(prevState) {
                    ActiveState.CROSSED_ALIVE -> Transition.CR_ALIVE_2_CIR_DEAD_ON
                    ActiveState.CIRCLED_DEAD_OFF-> Transition.CIR_DEAD_OFF_2_DEAD_ON
                    else -> Transition.CR_ALIVE_2_CIR_DEAD_ON
                }
            ActiveState.CIRCLED_DEAD_OFF ->
                when(prevState) {
                    ActiveState.CIRCLED_DEAD_ON -> Transition.CIR_DEAD_ON_2_DEAD_OFF
                    else -> Transition.CIR_DEAD_ON_2_DEAD_OFF
            }
            ActiveState.CROSSED_DEAD_ON ->
                when(prevState) {
                    ActiveState.CIRCLED_ALIVE -> Transition.CIR_ALIVE_2_CR_DEAD_ON
                    ActiveState.CROSSED_DEAD_OFF-> Transition.CR_DEAD_OFF_2_DEAD_ON
                    else -> Transition.CIR_ALIVE_2_CR_DEAD_ON
                }
            ActiveState.CROSSED_DEAD_OFF ->
                when(prevState) {
                    ActiveState.CROSSED_DEAD_ON -> Transition.CR_DEAD_ON_2_DEAD_OFF
                    else -> Transition.CR_DEAD_ON_2_DEAD_OFF
                }
            else->Transition.UNKNOWN

        }

    }
    fun getFullState():ActiveState{
//        val ret=when (this.state) {
//            State.EMPTY ->{
//                if (gameViewModel.side.value==0)
//                    if (this.isActive.first) ActiveState.EMPTY_ON else ActiveState.EMPTY_OFF
//                else
//                    if (this.isActive.second) ActiveState.EMPTY_ON else ActiveState.EMPTY_OFF
//            }
//            State.CIRCLED_ALIVE -> ActiveState.CIRCLED_ALIVE
//            State.CIRCLED_DEAD -> if (this.isActive.first) ActiveState.CIRCLED_DEAD_ON else ActiveState.CIRCLED_DEAD_OFF
//            State.CROSSED_ALIVE -> ActiveState.CROSSED_ALIVE
//            State.CROSSED_DEAD -> if (this.isActive.second) ActiveState.CROSSED_DEAD_ON else ActiveState.CROSSED_DEAD_OFF
//            else -> ActiveState.EMPTY_OFF
//        }
//        Log.d("dotter renew","state change id=${id} side=${gameViewModel.side.value} active=${this.isActive}")
         return when (this.state) {
                    State.EMPTY ->{
                        if (gameViewModel.side.value==0)
                            if (this.isActive.first) ActiveState.EMPTY_ON else ActiveState.EMPTY_OFF
                        else
                            if (this.isActive.second) ActiveState.EMPTY_ON else ActiveState.EMPTY_OFF
//                        when(this.isActive){
//                            Pair(true,true)->ActiveState.EMPTY_ON_BOTH
//                            Pair(true,false)->ActiveState.EMPTY_ON_CIR
//                            Pair(false,true)->ActiveState.EMPTY_ON_CR
//                            Pair(false,false)->ActiveState.EMPTY_OFF
//                            else -> ActiveState.EMPTY_OFF
//                        }
                    }
                    State.CIRCLED_ALIVE -> ActiveState.CIRCLED_ALIVE
                    State.CIRCLED_DEAD -> if (this.isActive.first) ActiveState.CIRCLED_DEAD_ON else ActiveState.CIRCLED_DEAD_OFF
                    State.CROSSED_ALIVE -> ActiveState.CROSSED_ALIVE
                    State.CROSSED_DEAD -> if (this.isActive.second) ActiveState.CROSSED_DEAD_ON else ActiveState.CROSSED_DEAD_OFF
                    else -> ActiveState.EMPTY_OFF
                }
    }
    fun getFullState(state: State):ActiveState{
        return when (this.state) {
            State.EMPTY ->{
                if (gameViewModel.side.value==0)
                    if (this.isActive.first) ActiveState.EMPTY_ON else ActiveState.EMPTY_OFF
                else
                    if (this.isActive.second) ActiveState.EMPTY_ON else ActiveState.EMPTY_OFF
            }
            State.CIRCLED_ALIVE -> ActiveState.CIRCLED_ALIVE
            State.CIRCLED_DEAD -> if (this.isActive.first) ActiveState.CIRCLED_DEAD_ON else ActiveState.CIRCLED_DEAD_OFF
            State.CROSSED_ALIVE -> ActiveState.CROSSED_ALIVE
            State.CROSSED_DEAD -> if (this.isActive.second) ActiveState.CROSSED_DEAD_ON else ActiveState.CROSSED_DEAD_OFF
            else -> ActiveState.EMPTY_OFF
        }
    }



    fun nextState(side: Int) {

        when (this.state) {
            State.EMPTY -> if (side == 0) {
                state = State.CIRCLED_ALIVE
                //activeLevel=0
            } else {
                state = State.CROSSED_ALIVE
                //activeLevel=0
            }
            State.CIRCLED_ALIVE -> if (side == 0) {
                state = state
            } else {
                state = State.CROSSED_DEAD
                //activeLevel=-1
            }
            State.CROSSED_ALIVE -> if (side == 0) {
                state = State.CIRCLED_DEAD
                //activeLevel=-1
            } else {
                state = state
            }
            else -> state=state
        }


    }
    fun nextStateReturn(side: Int) :State{
        return when (this.state) {
            State.EMPTY -> if (side == 0) {
                State.CIRCLED_ALIVE
            } else {
                State.CROSSED_ALIVE
            }
            State.CIRCLED_ALIVE -> if (side == 0) {
                state
            } else {
                State.CROSSED_DEAD
            }
            State.CROSSED_ALIVE -> if (side == 0) {
                State.CIRCLED_DEAD
            } else {
                state
            }
            else -> state
        }
    }
    override fun toString():String{
        return when(this.state){
            State.EMPTY->"EMPTY"
            State.CIRCLED_ALIVE->"CIRCLED_ALIVE"
            State.CROSSED_ALIVE->"CROSSED_ALIVE"
            else->"DEAD"
        }
    }
    fun setNeighboursByID(id: Int,mesh:Int=10){
        var id2add:Int
        id2add=id-1
        if (id%10!=0){
            neighboursID.add(id2add)
        }
        id2add=id+1
        if (id2add%10!=0){
            neighboursID.add(id2add)
        }
        id2add=id+10
        if (id2add<=99){
            neighboursID.add(id2add)
        }
        id2add=id-10
        if (id2add>=0){
            neighboursID.add(id2add)
        }
        id2add=id+10+1
        if ((id2add<=99) and (id2add%10!=0)){
            neighboursID.add(id2add)
        }
        id2add=id+10-1
        if ((id2add<=99) and (id%10!=0)){
            neighboursID.add(id2add)
        }
        id2add=id-10+1
        if ((id2add>=0) and (id2add%10!=0)){
            neighboursID.add(id2add)
        }
        id2add=id-10-1
        if ((id2add>=0) and (id%10!=0)){
            neighboursID.add(id2add)
        }

    }




}

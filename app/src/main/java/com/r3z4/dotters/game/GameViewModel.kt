package com.r3z4.dotters.game
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.r3z4.dotters.R
import com.r3z4.dotters.data.*

@RequiresApi(Build.VERSION_CODES.N)
class GameViewModel : ViewModel() ,AdapterOnClick{


    override var id_changed: Int=0

    val data: MutableLiveData<MutableList<Dotter>> by lazy {
        MutableLiveData<MutableList<Dotter>>()
    }

    private val _side = MutableLiveData<Int>()
    override val side: LiveData<Int>
        get() = _side



//    val currentSideDrawable=Transformations.map(side) { side -> getSideImage(side) }

    val datahistory= MutableLiveData<MutableList<UserAction>>()
    val pointer=MutableLiveData<Int>(0)
    val moveNumber=MutableLiveData<Int>(0)
    val moveCountMax=3

    val moveNumberString=Transformations.map(moveNumber){it.toString()}
    val moveCountMaxString=moveCountMax.toString()
    val pointerString=Transformations.map(pointer){(it+1).toString()}
    val resetButtonVisible=Transformations.map(datahistory){it.isNotEmpty()}
    val undoButtonVisible=Transformations.map(moveNumber){(it>0) and (it<=3)}
    val redoButtonVisible=Transformations.map(pointer){it+1< datahistory.value?.size ?: 0 }
    val changeSideButtonActive=Transformations.map(moveNumber){it>=3}




    init {

        setSide()
        resetData()
        setActiveDotters()
    }

    private fun setSide() {
        _side.value=0
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun resetData(){
        id_changed=-9999
        setStartDotters()
        datahistory.value= mutableListOf()
        pointer.value=-1
        moveNumber.value=0
        setActiveDotters()
    }

    private fun setStartDotters() {
        data.value = MutableList(100) { index ->
            Dotter(index,this)
        }
        data.value!!.first().state=State.CIRCLED_ALIVE
        data.value!!.last().state=State.CROSSED_ALIVE

//        data.value!!.forEach { dotter ->
//            dotter.neighboursID.forEach {
//                //dotter.stateObservers.add(data.value!![it].OnNeighbourChange)
//                //dotter.deadActiveObservers.add(data.value!![it].onDeadActiveChange)
//                //dotter.connectedObservers.add(data.value!![it].onConnectionChange)
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addToHistory(item: Dotter):Boolean{

        val oldState=item.state
        val newState=item.nextStateReturn(side.value!!)
        val changed=oldState!=newState
        if (oldState!=newState){
            datahistory.value?.removeIf { it.action_number > pointer.value!! }
            pointer.value = pointer.value?.plus(1)
            datahistory.value?.add(UserAction(pointer.value!!,item.id,oldState,newState))
            datahistory.value=datahistory.value
        }
        return changed





    }


    fun changeSide(){

        if (_side.value==0){
            _side.value=1
        }
        else{
            _side.value=0
        }

        data.value!!.forEach {
            if (!it.isActive.second) it.nowState=it.getFullState()
            if (!it.isActive.first) it.nowState=it.getFullState()

            if ((it.isActive.first!=it.isActive.second)and (it.state==State.EMPTY)) {
                showChange(it.id)
            }

            //if (it.isActive1!=it.isActive0) showChange(it.id)
        }

        moveNumber.value=0

//        Log.e("DEBUG","SIDE CHANGED to ${side.value}")

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(item: Dotter) {
        val oldState=item.state
        val newState=item.nextStateReturn(side.value!!)
        val changableByRules=newState!=oldState
        val availableToChange=(((side.value==0)and(item.isActive.first))or((side.value==1)and(item.isActive.second)))
        val maxMovesAchived=(moveNumber.value!! >=3)

        if (availableToChange and !maxMovesAchived and changableByRules){
            moveNumber.value= moveNumber.value?.plus(1)

            //id_changed=item.id
            side.value?.let {
                //setActiveDotters(item.id)
                addToHistory(item)
                item.nextState(it)
            }
            setActiveDotters()
        }
        else if (maxMovesAchived){
            Log.d("move counting","max moves achived")
        }
        else {
            Log.d("move counting","unavilible to move")
        }

        //showChange(item.id)
        //data.value=data.value

        //test
        //Log.e("DEBUG","for id=${item.id} n=${item.neighboursID}")
    }

    fun showChange(id:Int){
        //setActiveDotters(id)
        Log.d("turnOn alg","show change called with id=${id}")
        id_changed= id
        data.value=data.value
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun undo(){

        data.value?.let {
            moveNumber.value= moveNumber.value?.minus(1)
            //id_changed= datahistory.value?.get(pointer.value!!)?.id ?: 0
            showChange(datahistory.value?.get(pointer.value!!)?.id ?: 0)
            datahistory.value?.get(pointer.value!!)?.undo(it)
            pointer.value = pointer.value?.minus(1)
            setActiveDotters()
        }

        //data.value=data.value
        datahistory.value=datahistory.value

    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun redo(){
        data.value?.let {
            moveNumber.value= moveNumber.value?.plus(1)
            pointer.value = pointer.value?.plus(1)
            showChange(datahistory.value?.get(pointer.value!!)?.id ?: 0)
            //id_changed= datahistory.value?.get(pointer.value!!)?.id ?: 0
            datahistory.value?.get(pointer.value!!)?.redo(it)
            setActiveDotters()

        }


        //data.value=data.value
        datahistory.value=datahistory.value
    }

    fun getSideImage():Int{
        return when (side.value){
            0 -> R.raw.alive2alive_b2p
            else -> R.raw.alive2alive_p2b
        }
    }



    fun getDotterWithID(id:Int):Dotter{
        return data.value!![id]
    }

    fun checkActiveNeighbours(id: Int,side:Int):Boolean{
        val dotter=getDotterWithID(id)
        //Log.d("turnOn alg","dotter id=${dotter.id} n=${dotter.neighboursID} ")
        //var active0=false
        if(side==0){
        if ((dotter.state==State.CIRCLED_ALIVE) or (dotter.state==State.CIRCLED_DEAD) or (((dotter.state==State.EMPTY)or(dotter.state==State.CROSSED_ALIVE))and(side==0))){
                dotter.neighboursID.forEach {
                    val nDotter=getDotterWithID(it)
                    if ((nDotter.isActive00) and (nDotter.state!=State.EMPTY) and (nDotter.state!=State.CROSSED_ALIVE)) {
                        Log.d("turnOn alg","turn on 0 id=${id} cause=${it} cause active0=${nDotter.isActive00} active1=${nDotter.isActive11}")
                        return true
                    }
                }
            }}
        if (side==1){
        if ((dotter.state==State.CROSSED_ALIVE) or (dotter.state==State.CROSSED_DEAD) or (((dotter.state==State.EMPTY)or(dotter.state==State.CIRCLED_ALIVE))and(side==1))){
            dotter.neighboursID.forEach {
                val nDotter=getDotterWithID(it)
                if (nDotter.isActive11 and (nDotter.state!=State.EMPTY)and (nDotter.state!=State.CIRCLED_ALIVE)) {
                    Log.d("turnOn alg","turn on 1 id=${id} cause=${it} cause active0=${nDotter.isActive00} active1=${nDotter.isActive11}")
                    return true}
            }
        }}
        //Log.d("turnOn alg","checkNeibFinal id=${id}  active0=${dotter.isActive00} active1=${dotter.isActive11}")
        return false
    }

    fun setDotterActivity(turnOnDotters: TurnOnDotters){
        if (turnOnDotters.side==0){
            data.value!![turnOnDotters.id].isActive=Pair(true,false)
//            data.value!![turnOnDotters.id].isActive0=true
//            data.value!![turnOnDotters.id].isActive1=false
            Log.d("turnOnChange","id=${turnOnDotters.id} active0 turnedOn on ")

        }
        if (turnOnDotters.side==1){
            data.value!![turnOnDotters.id].isActive=Pair(false,true)
//            data.value!![turnOnDotters.id].isActive1=true
//            data.value!![turnOnDotters.id].isActive0=false
            Log.d("turnOnChange","id=${turnOnDotters.id} active1 turnedOn ")

        }
        if (turnOnDotters.side==-1){
            data.value!![turnOnDotters.id].isActive=Pair(true,true)
//            data.value!![turnOnDotters.id].isActive1=true
//            data.value!![turnOnDotters.id].isActive0=true
            Log.d("turnOnChange","id=${turnOnDotters.id} active BOTH turnedOn on ")

        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun setActiveDotters(){
        Log.d("turnOn alg","started")
        val data= mutableListOf<Dotter>()
        data.addAll(this.data.value!!)



        val changedIdSetModifiable= mutableSetOf<Int>()
        changedIdSetModifiable.addAll((0..99).toList())
        this.data.value!!.forEach {
            it.isActive00=false
            it.isActive11=false
        }
        val turnOnIndexSet= mutableSetOf<Int>()
        val turnOnDottersSet= mutableListOf<TurnOnDotters>()
        val changedIdRunnable= mutableSetOf<Int>()
        changedIdRunnable.addAll(changedIdSetModifiable)
        changedIdSetModifiable.removeAll(changedIdSetModifiable)
        while (!changedIdRunnable.isEmpty()){
            changedIdRunnable.forEach { id->
                val dotter=getDotterWithID(id)
                val haveActive0Neighbours=checkActiveNeighbours(id,0)
                val haveActive1Neighbours=checkActiveNeighbours(id,1)
                if ((dotter.state==State.EMPTY) and ((!dotter.isActive11)or(!dotter.isActive00)) and (haveActive0Neighbours and haveActive1Neighbours)){
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,-1))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive11=true
                    dotter.isActive00=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter SUPEREMPTYFULL with id=${id} turned on")
                }
                //Log.d("turnOn alg"," DANGERACTIVE BUG alive with id=${id} turned on haveactive1=${checkActiveNeighbours(id,1)}")

                if ((dotter.state==State.CIRCLED_ALIVE) and (!dotter.isActive00) and !haveActive1Neighbours){
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,0))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive00=true
                    //Log.d("dotter renew", "local change copied status=${data[id].isActive0} original status =${this.data.value!![id].isActive0}")
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter alive with id=${id} turned on")
                }
                if ((dotter.state==State.CIRCLED_DEAD) and (!dotter.isActive00) and haveActive0Neighbours){
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,0))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive00=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter dead with id=${id} turned on")
                }
                if ((dotter.state==State.EMPTY) and (!dotter.isActive00) and haveActive0Neighbours and !haveActive1Neighbours){

                    turnOnDottersSet.add(TurnOnDotters(dotter.id,0))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive00=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter empty0 with id=${id} turned on")
                }
                if ((dotter.state==State.CROSSED_ALIVE) and (!dotter.isActive11) and !haveActive0Neighbours){
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,1))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive11=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter alive with id=${id} turned on")
                }
                if ((dotter.state==State.CROSSED_DEAD) and (!dotter.isActive11) and haveActive1Neighbours){
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,1))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive11=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter dead with id=${id} turned on")
                }
                if ((dotter.state==State.EMPTY) and (!dotter.isActive11) and haveActive1Neighbours and !haveActive0Neighbours){
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,1))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive11=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter empty1 with id=${id} turned on")
                }
                if ((dotter.state==State.CIRCLED_ALIVE) and (!dotter.isActive11) and checkActiveNeighbours(id,1)){
                    //Log.d("turnOn alg","dotter before change DANGERACTIVE alive with id=${id} turned on haveactive1=${checkActiveNeighbours(id,1)}")
                    turnOnDottersSet.removeIf { it.id==dotter.id }
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,-1))
                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive11=true
                    dotter.isActive00=true
                    //Log.d("dotter renew", "local change copied status=${data[id].isActive0} original status =${this.data.value!![id].isActive0}")
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter DANGERACTIVE alive with id=${id} turned on haveactive1=${checkActiveNeighbours(id,1)}")
                }
                if ((dotter.state==State.CROSSED_ALIVE) and (!dotter.isActive00) and haveActive0Neighbours){
                    Log.d("turnOn alg","dotter before change DANGERACTIVE alive with id=${id} turned on haveactive0=${haveActive0Neighbours}")
                    turnOnDottersSet.removeIf { it.id==dotter.id }
                    turnOnDottersSet.add(TurnOnDotters(dotter.id,-1))

                    turnOnIndexSet.add(dotter.id)
                    dotter.isActive00=true
                    dotter.isActive11=true
                    changedIdSetModifiable.addAll(dotter.neighboursID)
                    Log.d("turnOn alg","dotter DANGERACTIVE alive with id=${id} turned on haveactive0=${haveActive0Neighbours}")
                }


//                if ((dotter.state==State.CIRCLED_ALIVE) and (dotter.isActive0)){
//                    dotter.isActive0=false
//                    changedIdSetModifiable.addAll(dotter.neighboursID)
//                    Log.d("turnOn alg","dotter not alive with id=${id} turned off")
//                }
            }
            changedIdRunnable.removeAll(changedIdRunnable)
            changedIdRunnable.addAll(changedIdSetModifiable)
            changedIdSetModifiable.removeAll(changedIdSetModifiable)
            Log.d("turnOn alg","next loop starts with objects in runnableset=${changedIdRunnable.count()} in modSet=${changedIdSetModifiable.count()}")
        }

        val brokenInd= mutableSetOf<Int>()
        turnOnDottersSet.forEach { if (it.side==-1) brokenInd.add(it.id) }
        turnOnDottersSet.removeIf { (brokenInd.contains(it.id) and (it.side!=-1)) }

        //Log.d("turnOn alg","results  turnonind=${turnOnIndexSet} in turnOnElem=${turnOnDottersSet.count()}")
        if(turnOnIndexSet.count()!=turnOnDottersSet.count()) {
            Log.e("turnOn alg","results  turnonind=${turnOnIndexSet.count()} in turnOnElem=${turnOnDottersSet.count()}")
            val testSet= mutableSetOf<Int>()
            val errorSet= mutableSetOf<Int>()
            turnOnDottersSet.forEach {
                if (!testSet.contains(it.id)){
                    testSet.add(it.id)
                }
                else{
                    errorSet.add(it.id)
                }
            }
            turnOnDottersSet.forEach {
                if(errorSet.contains(it.id)){
                    Log.e("turnOn alg","double note id=${it.id} side=${it.side}")
                }
            }
        }


        this.data.value!!.forEach {
            if (!turnOnIndexSet.contains(it.id)){
                it.isActive=Pair(false,false)
            }

        }


        turnOnDottersSet.forEach { setDotterActivity(it) }


    }



}
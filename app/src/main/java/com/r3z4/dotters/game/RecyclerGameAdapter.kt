package com.r3z4.dotters.game

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import com.r3z4.dotters.R
import com.r3z4.dotters.data.Dotter
import com.r3z4.dotters.data.Transition

@RequiresApi(Build.VERSION_CODES.N)
class RecyclerGameAdapter(val adapterOnClick: AdapterOnClick): RecyclerView.Adapter<RecyclerGameAdapter.DotterViewHolder>() {
    var items = listOf<Dotter>()
        set(value) {
            field = value
            Log.d("adapter","adapter list change")
            //notifyDataSetChanged()
            if(adapterOnClick.id_changed>=0){
                Log.d("adapter","adapter list change with id=${adapterOnClick.id_changed}")
                notifyItemChanged(adapterOnClick.id_changed)
            }
            else{
                Log.e("adapter","adapter FULL list change")
                notifyDataSetChanged()
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : DotterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_dotter, parent, false)

        return DotterViewHolder(view)
    }


    override fun onBindViewHolder(holder: DotterViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        val item:Dotter=items.get(position)
        return item.pid
    }

    inner class DotterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lottie_animation: LottieAnimationView = itemView.findViewById(R.id.state_image)

        val animationLoadedListener = LottieListener<LottieComposition> { composition ->
            lottie_animation.setComposition(composition)
            lottie_animation.playAnimation()

        }



        fun setLottieAnimation(transition: Transition) {
            lottie_animation.imageAssetsFolder = "lottie/gameanim"
            //lottie_animation.speed=2f
            val task = when (transition) {
                Transition.EMPTY_OFF_2_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.empty_off_empty_on2off)
                Transition.EMPTY_ON_2_OFF -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.empty_on_2_empty_off)
                Transition.EMPTY_ON_CIR_ALIVE -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.red_aliveempty_on2redalive)
                Transition.EMPTY_ON_CR_ALIVE -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.blue_alive_empty_on2bluealive)
                Transition.CIR_DEAD_ON_2_DEAD_OFF -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.red_dead_off_reddeadcctive2passive)
                Transition.CR_DEAD_ON_2_DEAD_OFF -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.blue_dead_off_bluedeadactive2passive)
                Transition.CR_ALIVE_2_CIR_DEAD_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.red_dead_bluealive2dead)
                Transition.CIR_ALIVE_2_CR_DEAD_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.blue_dead_redalive2dead)
                Transition.CR_ALIVE_2_EMPTY_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.cr_alive_2_empty_on)
                Transition.CIR_ALIVE_2_EMPTY_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.cir_alive_2_empty_on)
//                Transition.CR_ALIVE_2_CIR_DEAD_OFF -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.alive2alive_p2b)
//                Transition.CIR_ALIVE_2_CR_DEAD_OFF -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.alive2alive_p2b)
                Transition.CIR_DEAD_OFF_2_DEAD_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.cir_dead_off_2_on)
                Transition.CR_DEAD_OFF_2_DEAD_ON -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.cr_dead_off_2_on)
                Transition.UNKNOWN -> LottieCompositionFactory.fromRawRes(itemView.context, R.raw.alive2alive_p2b)
                Transition.EMPTY_ON_NT->LottieCompositionFactory.fromRawRes(itemView.context, R.raw.empty_on_nt)
                Transition.EMPTY_OFF_NT->LottieCompositionFactory.fromRawRes(itemView.context, R.raw.empty_off_nt)
                else -> {
                    //lottie_animation.setAnimation(R.raw.welcome_2)
                    LottieCompositionFactory.fromRawRes(itemView.context, R.raw.alive2alive_p2b)
                }

                /// more cases
            }


            task.addListener(animationLoadedListener)
            //task.addFailureListener { Timber.e(it) }
        }

        fun bind(item: Dotter) {
            val res = itemView.context.resources
            Log.d("animatiom","dotter renew with id=${item.id}")

            setLottieAnimation(item.getTransition())
//            lottie_animation.setAnimation(R.raw.allinaev2)
//            val (minFrame, maxFrame,speed)=item.getParamsForAnimation()
//            lottie_animation.playPart(minFrame,maxFrame, speed)
            lottie_animation.setOnClickListener {
                adapterOnClick.onClick(item)
            }
        }

    }

//    fun LottieAnimationView.playPart(minFrame:Int,maxFrame:Int,speed:Float){
//        this.speed=speed
//        this.setMinFrame(minFrame)
//        this.setMaxFrame(maxFrame)
//        this.playAnimation()
//    }
}

class DiffCallbackDotters:DiffUtil.ItemCallback<Dotter>(){
    override fun areItemsTheSame(oldItem: Dotter, newItem: Dotter): Boolean {
        return oldItem.state==newItem.state
    }

    override fun areContentsTheSame(oldItem: Dotter, newItem: Dotter): Boolean {
        return (oldItem.state==newItem.state) and (oldItem.id==newItem.id)
    }

}

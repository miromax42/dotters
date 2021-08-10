package com.r3z4.dotters.game

import android.animation.Animator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import com.r3z4.dotters.R
import com.r3z4.dotters.data.playMidWithColors
import com.r3z4.dotters.data.playSideWithColors
import com.r3z4.dotters.databinding.GameFragmentBinding


class GameFragment : Fragment() {

    companion object {
        fun newInstance() = GameFragment()
    }

    private lateinit var viewModel: GameViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Main settings
        val binding: GameFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.game_fragment, container, false)
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        binding.lifecycleOwner = this
        binding.gameViewModel=viewModel

        //Recyclerview
        val manager=GridLayoutManager(activity,10)
        val adapter = RecyclerGameAdapter(viewModel)
        adapter.setHasStableIds(true)
        (binding.recyclerGame.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false

        binding.recyclerGame.adapter = adapter

        binding.recyclerGame.layoutManager=manager

        val lottie_side_animation=binding.imageSide

        val lottie_undo_button=binding.buttonUndo

        lottie_undo_button.setOnClickListener {
            //lottie_undo_button.setBackgroundColor(Color.DKGRAY)
            lottie_undo_button.progress=0f
            lottie_undo_button.speed=2f
            lottie_undo_button.playAnimation()
            viewModel.undo()
        }

        val animationSideLoadedListener = LottieListener<LottieComposition> { composition ->
            lottie_side_animation.setComposition(composition)



            lottie_side_animation.playSideWithColors(viewModel.pointer.value==-1,viewModel.side.value!!)
        }

        fun setLottieAnimationSide(first: Boolean) {
            val task = when (first) {
                true -> {
                    lottie_side_animation.imageAssetsFolder = "lottie"
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.side_first_show)
                }
                false -> {
                    lottie_side_animation.imageAssetsFolder = "lottie"
                    //lottie_animation.setAnimation(R.raw.welcome_2)
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.side_change_show)
                }
                /// more cases
            }
            task.addListener(animationSideLoadedListener)
            //task.addFailureListener { Timber.e(it) }
        }

        val lottie_changeSide_animation=binding.buttonChangeSide

        val animationChangeSideLoadedListener = LottieListener<LottieComposition> { composition ->
            lottie_changeSide_animation.setComposition(composition)
            lottie_changeSide_animation.playMidWithColors(viewModel.side.value?:0)
        }

        fun setLottieAnimationChangeSide(moveNumber:Int) {
            val task = when (moveNumber) {
                0 -> {
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.mid_button_change)
                }
                1 -> {

                    //lottie_animation.setAnimation(R.raw.welcome_2)
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.mid_button_0_1)
                }
                2 -> {

                    //lottie_animation.setAnimation(R.raw.welcome_2)
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.mid_button_1_2)
                }
                3 -> {

                    //lottie_animation.setAnimation(R.raw.welcome_2)
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.mid_button_2_3)
                }
                /// more cases
                else -> {

                    //lottie_animation.setAnimation(R.raw.welcome_2)
                    LottieCompositionFactory.fromRawRes(this.context, R.raw.side_change_show)
                }
            }

            task.addListener(animationChangeSideLoadedListener)
            //task.addFailureListener { Timber.e(it) }
        }
        fun SetWaitingAnimation(){
            lottie_side_animation.imageAssetsFolder = "lottie"
            //lottie_animation.setAnimation(R.raw.welcome_2)
            val task =LottieCompositionFactory.fromRawRes(this.context, R.raw.side_change_show)
            task.addListener(animationChangeSideLoadedListener)
        }





        viewModel.data.observe(viewLifecycleOwner, Observer {
            it?.let {
                //Log.e("DEBUG","observer called")
                adapter.items=it
            }

        })

        //Side showing
        viewModel.side.observe(viewLifecycleOwner, Observer {
            setLottieAnimationSide(viewModel.pointer.value==-1)

        })

        viewModel.moveNumber.observe(viewLifecycleOwner, Observer {
            setLottieAnimationChangeSide(it ?: 0)
        })


        return  binding.root
    }






}
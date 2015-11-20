package hk.ust.cse.hunkim.questionroom.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import hk.ust.cse.hunkim.questionroom.R;

/**
 * Created by Jonathan on 11/16/2015.
 */
public class AnimFx {

    public static void slide_down(Context context, View view){

        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        if(a!= null){
            a.reset();
            if(view != null){
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context context, View view){

        Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        if(a!= null){
            a.reset();
            if(view != null){
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

}

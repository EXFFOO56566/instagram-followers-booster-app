package com.lxquyen.instabooster.widget.swipestack;

/**
 * Created by Furuichi on 08/07/2022
 */

import android.animation.Animator;

public class AnimationUtils {

    public static abstract class AnimationEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
            // Do nothing
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // Do nothing
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // Do nothing
        }
    }
}
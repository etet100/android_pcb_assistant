package bts.pcbassistant.utils;

import android.animation.Animator;

/**
 * Created by a on 2017-08-07.
 */

public abstract class AnimationEndListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public abstract void onAnimationEnd(Animator animation);

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}

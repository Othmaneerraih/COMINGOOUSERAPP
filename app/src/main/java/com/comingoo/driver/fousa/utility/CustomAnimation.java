package com.comingoo.driver.fousa.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class CustomAnimation {


    public static void animate(final Context context, final View constraintLayout, final float reachedHeigth, final float currentHeight, int duration){

        constraintLayout.setVisibility(View.VISIBLE);

        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) reachedHeigth, context.getResources().getDisplayMetrics());
        ValueAnimator anim = ValueAnimator.ofInt(constraintLayout.getMeasuredHeight(), height);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = constraintLayout.getLayoutParams();
                layoutParams.height =  val;
                constraintLayout.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(duration);
        anim.start();

    }
    public static void animateWidth(final Context context, final View constraintLayout, final float reachedHeigth, final float currentHeight, int duration){

        constraintLayout.setVisibility(View.VISIBLE);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) reachedHeigth, context.getResources().getDisplayMetrics());

        ValueAnimator anim = ValueAnimator.ofInt(constraintLayout.getMeasuredHeight(), height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = constraintLayout.getLayoutParams();
                layoutParams.width =  val;
                constraintLayout.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(duration);
        anim.start();

    }
    public static void fadeIn(Context context,final View constraintLayout, final int duration, final int howSmooth){
        constraintLayout.setVisibility(View.VISIBLE);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(constraintLayout, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);

        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn);
        mAnimationSet.start();
    }
    public static void fadeOut(Context context,final View constraintLayout, final int duration, final int howSmooth){
        constraintLayout.setVisibility(View.VISIBLE);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(constraintLayout, "alpha", 1f, 0f);
        fadeIn.setDuration(duration);

        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn);
        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                constraintLayout.setVisibility(View.GONE);
            }
        });
        mAnimationSet.start();
    }
    public static void animateCollapse(final Context context, final View constraintLayout, final float reachedHeigth, final float currentHeight, int duration){

        constraintLayout.setVisibility(View.VISIBLE);

        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) reachedHeigth, context.getResources().getDisplayMetrics());
        ValueAnimator anim = ValueAnimator.ofInt(constraintLayout.getMeasuredHeight(), height);


        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = constraintLayout.getLayoutParams();
                layoutParams.height =  val;
                constraintLayout.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(duration);
        anim.start();
    }
    public static void resideAnimation(final Context context, final View constraintLayout, final View contentBlocker, final int screenWidth, final int screenHeight, final int duration){

        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) screenHeight, context.getResources().getDisplayMetrics());
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) screenWidth, context.getResources().getDisplayMetrics());
        final float xTranslation = 0.95f * width;
        final float yTranslation = 0.25f * height;



        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillAfter(true);
        animSet.setDuration(duration);
        animSet.setInterpolator(new BounceInterpolator());
        TranslateAnimation translate = new TranslateAnimation( 0, xTranslation , 0, yTranslation);
        animSet.addAnimation(translate);
        ScaleAnimation scale = new ScaleAnimation(1f, 0.65f, 1f, 0.65f, ScaleAnimation.RELATIVE_TO_PARENT, 0f, ScaleAnimation.RELATIVE_TO_PARENT, 0f);
        animSet.addAnimation(scale);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contentBlocker.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        constraintLayout.startAnimation(animSet);




        contentBlocker.setScaleY((float) 0.77);
        contentBlocker.setScaleX((float) 0.865);
        contentBlocker.setTranslationX((int) xTranslation);


        contentBlocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentBlocker.setVisibility(View.GONE);
                contentBlocker.setOnClickListener(null);


                Animation anim = new ScaleAnimation(
                        0.65f, 1f, // Start and end values for the X axis scaling
                        0.65f, 1f); // Pivot point of Y scaling
                anim.setFillAfter(true); // Needed to keep the result of the animation
                anim.setDuration(duration);

                Animation newAnim = new TranslateAnimation(
                        0.9f, 0f,
                        0.25f , 0f
                );
                newAnim.setFillAfter(true); // Needed to keep the result of the animation
                newAnim.setDuration(duration);
                constraintLayout.startAnimation(anim);
                constraintLayout.startAnimation(newAnim);

            }
        });

    }
    public static void expandCircleAnimation(final Context context, final View constraintLayout, float maxHeight, float maxWidth){
        double Height = maxHeight + (maxHeight * 0.5) + maxHeight;
        double Width = maxWidth + (maxWidth * 0.5);
        animate(context, constraintLayout, (float) Height, 1, 650);
        animateWidth(context, constraintLayout, (float) Width, 1, 350);
    }

}

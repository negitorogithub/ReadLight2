package unifar.unifar.readlight2;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;

import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

public class MyViews {


    private final FragmentManager mfragmentManeger;
    private final Activity mcontext;
    private final Animation mseekBarAnimation;
    private final TranslateAnimation mivColorPaletteAnimation;
    private int mcurrentColor;
    private ViewGroup mvgContentFragmentContainer;
    private ImageView mivColorPalette;
    private SeekBar mseekBar;
    private Handler mhandler;
    private Runnable mrunnable;


    public FragmentManager getMfragmentManeger() {
        return mfragmentManeger;
    }

    MyViews(int currentColor, ViewGroup vgContentFragmentContainer, ImageView ivColorPalette, Handler handler, FragmentManager fragmentManager, SeekBar seekBar, Activity context) {
        this.mcurrentColor = currentColor;
        this.mvgContentFragmentContainer = vgContentFragmentContainer;
        this.mvgContentFragmentContainer.setBackgroundColor(this.mcurrentColor);
        this.mivColorPalette = ivColorPalette;
        this.mhandler = handler;
        this.mfragmentManeger = fragmentManager;
        this.mseekBar = seekBar;
        this.mcontext = context;
        this.mseekBar.setMax(1000);
        this.mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                WindowManager.LayoutParams layoutParams = mcontext.getWindow().getAttributes();
                layoutParams.screenBrightness = (float)i/1000;
                mcontext.getWindow().setAttributes(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //this.mseekBarAnimation = AnimationUtils.loadAnimation(mcontext,R.anim.myanim);
        this.mivColorPaletteAnimation = new TranslateAnimation(
                Animation.ABSOLUTE,0,
                Animation.RELATIVE_TO_PARENT,0.2f,
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0);
        this.mivColorPaletteAnimation.setDuration(2000);
        this.mivColorPaletteAnimation.setFillAfter(false);
        this.mivColorPaletteAnimation.setInterpolator(new DecelerateInterpolator());
        mivColorPaletteAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mivColorPalette.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.mseekBarAnimation = new TranslateAnimation(
        Animation.ABSOLUTE,0,
        Animation.ABSOLUTE,0,
        Animation.ABSOLUTE,0,
        Animation.RELATIVE_TO_PARENT,0.2f);
        this.mseekBarAnimation.setDuration(2000);
        this.mseekBarAnimation.setFillAfter(false);
        this.mseekBarAnimation.setInterpolator(new DecelerateInterpolator());
        this.mseekBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mseekBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.mrunnable = new Runnable() {
            @Override
            public void run() {
                mseekBar.startAnimation(mseekBarAnimation);
                mivColorPalette.startAnimation(mivColorPaletteAnimation);
            }
        };

        mvgContentFragmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mivColorPalette.setVisibility(View.VISIBLE);
                mseekBar.setVisibility(View.VISIBLE);
                mivColorPaletteAnimation.cancel();
                mseekBarAnimation.cancel();
                setTimeEvent();
            }
        });
    }

    public int getMcurrentColor() {
        return mcurrentColor;
    }

    public void setMcurrentColor(int mcurrentColor) {
        this.mcurrentColor = mcurrentColor;
    }

    public ViewGroup getMvgContentFragmentContainer() {
        return mvgContentFragmentContainer;
    }

    public void setMvgContentFragmentContainer(ViewGroup mvgContentFragmentContainer) {
        this.mvgContentFragmentContainer = mvgContentFragmentContainer;
    }

    public ImageView getIvColorPalette() {
        return mivColorPalette;
    }

    public void setIvColorPalette(ImageView ivColorPalette) {
        this.mivColorPalette = ivColorPalette;
    }

    public Handler getMhandler() {
        return mhandler;
    }

    public void setMhandler(Handler mhandler) {
        this.mhandler = mhandler;
    }
    void setColorPalette(){
        mivColorPalette.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                OnAmbilWarnaListener onAmbilWarnaListener = new OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialogFragment dialogFragment) {
                    }
                    @Override
                    public void onOk(AmbilWarnaDialogFragment dialogFragment, int color) {
                        mcurrentColor = color;
                        mvgContentFragmentContainer.setBackgroundColor(mcurrentColor);
                    }
                };

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    AmbilWarnaDialogFragment mambilWarnaDialogFragment = AmbilWarnaDialogFragment.newInstance(mcurrentColor);
                    mambilWarnaDialogFragment.setOnAmbilWarnaListener(onAmbilWarnaListener);
                    mambilWarnaDialogFragment.show(mfragmentManeger, "colorPickerDialog");
                }
                return true;
            }
        });

    }
    void setTimeEvent(){
        mhandler.removeCallbacks(mrunnable);
        mhandler.postDelayed(mrunnable, 10000);
    }



}

package unifar.unifar.readlight2;


import android.support.v7.app.AppCompatActivity;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import java.util.Calendar;

import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

class MyViews {


    private final FragmentManager mfragmentManeger;
    private final AppCompatActivity mcontext;
    private final Animation mseekBarAnimation;
    private final TranslateAnimation mivColorPaletteAnimation;
    private int mcurrentColor;
    private ViewGroup mvgContentFragmentContainer;
    private ImageView mivColorPalette;
    private SeekBar mseekBar;
    private Handler mhandler;
    private Runnable mrunnable;
    private ImageView mivAddAlarmButton;
    private Animation mivAddAlarmButtonAnimation;

    public FragmentManager getMfragmentManeger() {
        return mfragmentManeger;
    }

    MyViews(int currentColor, ViewGroup vgContentFragmentContainer, ImageView ivColorPalette, Handler handler, FragmentManager fragmentManager, SeekBar seekBar, AppCompatActivity context, ImageView ivAddAlarmButton) {
        this.mcurrentColor = currentColor;
        this.mvgContentFragmentContainer = vgContentFragmentContainer;
        this.mvgContentFragmentContainer.setBackgroundColor(this.mcurrentColor);
        this.mivColorPalette = ivColorPalette;
        this.mhandler = handler;
        this.mfragmentManeger = fragmentManager;
        this.mseekBar = seekBar;
        this.mcontext = context;
        this.mivAddAlarmButton = ivAddAlarmButton;
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
        this.mivAddAlarmButtonAnimation = new TranslateAnimation(
                Animation.ABSOLUTE,0,
                Animation.RELATIVE_TO_PARENT,-0.2f,
                Animation.ABSOLUTE,0,
                Animation.ABSOLUTE,0);
        this.mivAddAlarmButtonAnimation.setDuration(2000);
        this.mivAddAlarmButtonAnimation.setFillAfter(false);
        this.mivAddAlarmButtonAnimation.setInterpolator(new DecelerateInterpolator());
        this.mivAddAlarmButtonAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mivAddAlarmButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


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
                mivAddAlarmButton.startAnimation(mivAddAlarmButtonAnimation);
            }
        };

        mvgContentFragmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mivColorPalette.setVisibility(View.VISIBLE);
                mseekBar.setVisibility(View.VISIBLE);
                mivAddAlarmButton.setVisibility(View.VISIBLE);
                mivColorPaletteAnimation.cancel();
                mseekBarAnimation.cancel();
                mivAddAlarmButtonAnimation.cancel();
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
    void applyAll(){
        setColorPalette();
        setTimeEvent();
        setOnSettingButtonListener();
    }

    private void setColorPalette(){
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
    private void setTimeEvent(){
        mhandler.removeCallbacks(mrunnable);
        mhandler.postDelayed(mrunnable, 10000);
    }

    private void setOnSettingButtonListener(){
        this.mivAddAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mcontext.getSupportFragmentManager().findFragmentByTag("contentFragment") instanceof TimePickerListener) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(mcontext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            ((TimePickerListener) mcontext.getSupportFragmentManager().findFragmentByTag("contentFragment")).OnTimeSet(i,i1);
                        }
                    },
                            hour,
                            minute,
                            true
                    );
                    timePickerDialog.show();

                 }
            }

            });
    }
}
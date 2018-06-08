package unifar.unifar.readlight2;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.Calendar;
import java.util.Locale;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment implements TimePickerListener{

    public static final String BACK_COLOR = "BackColor";
    private MyViews myViews;
    private AmbilWarnaDialogFragment ambilWarnaDialogFragment;
    private ViewGroup mcontainer;
    private TimePickerListener msettingButtonListener;
    private AppCompatActivity mappCompatActivity;
    private SharedPreferences sharedPreferences;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance() {
        Bundle args = new Bundle();
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        setupOnAtacch(context);
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        setupOnAtacch(activity);
    }
    private void setupOnAtacch(Context context){
        mappCompatActivity = (AppCompatActivity) context;
        if (context instanceof TimePickerListener){
            this.msettingButtonListener = (TimePickerListener)context;
        }

    }
    @Override public void onDetach(){
        super.onDetach();
        this.msettingButtonListener = null;

    }

    @Override public void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BACK_COLOR, myViews.getMcurrentColor());
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final ContentFragment thisContext = this;
        this.mcontainer = container;
        int currentColor;
        View viContent;
        ViewGroup vgContentFragmentContainer ;
        ImageView ivColorPalette;
        ImageView ivAddAlarmButton;
        ImageView ivSettingsView;
        Handler handler;
        SeekBar seekBar;
        final FragmentManager fragmentManeger;
        viContent = inflater.inflate(R.layout.fragment_content, container, false);
        handler = new Handler();
        fragmentManeger = getFragmentManager();
        vgContentFragmentContainer = viContent.findViewById(R.id.flContentFragmentContainer);
        ivColorPalette = viContent.findViewById(R.id.ivColorPalette);
        seekBar = viContent.findViewById(R.id.seekBar);
        ivAddAlarmButton = viContent.findViewById(R.id.ivAddAlarmButton);
        ivSettingsView = viContent.findViewById(R.id.ivSettings);

        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            currentColor =sharedPreferences.getInt(BACK_COLOR, Color.LTGRAY);
        } else {
            currentColor = savedInstanceState.getInt("currentColor");
        }
        myViews = new MyViews(currentColor, vgContentFragmentContainer, ivColorPalette, handler, fragmentManeger, seekBar,(AppCompatActivity) getActivity(), ivAddAlarmButton, ivSettingsView);
        myViews.applyAll();

        // create new instance of AmbilWarnaDialogFragment and set OnAmbilWarnaListener listener to it
        // show dialog fragment with some tag value
        return viContent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", myViews.getMcurrentColor());
    }

    @Override
    public void OnTimeSet(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();

        int hourDelta =0;
        if (calendar.get(Calendar.HOUR_OF_DAY)<hour) {
            hourDelta= hour-calendar.get(Calendar.HOUR_OF_DAY);
        } else if (calendar.get(Calendar.HOUR_OF_DAY)==hour){
            hourDelta =0;
        } else if (calendar.get(Calendar.HOUR_OF_DAY)>hour){
            hourDelta= hour+24-calendar.get(Calendar.HOUR_OF_DAY);
        }
        int minuteDelta = 0;
        if (calendar.get(Calendar.MINUTE)<minute) {
            minuteDelta= minute-calendar.get(Calendar.MINUTE);
        } else if (calendar.get(Calendar.MINUTE)==minute){
            minuteDelta =0;
        } else if (calendar.get(Calendar.MINUTE)>minute){
            minuteDelta= minute+60-calendar.get(Calendar.MINUTE);
        }
        calendar.add(Calendar.HOUR_OF_DAY, hourDelta);
        calendar.add(Calendar.MINUTE, minuteDelta);
        long timeNow = System.currentTimeMillis();
        long timeToDelay = Math.abs(calendar.getTimeInMillis() - timeNow);



        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                mappCompatActivity.finish();
            }
        },timeToDelay
        );


        final Snackbar snackbar;
        String sHour = String.valueOf(hour);
        String sMinute = String.valueOf(minute);
        if (minute<10){
            sMinute = "0"+sMinute;
        }
        snackbar = Snackbar.make(this.mcontainer,getString(R.string.setTimeMessage, sHour, sMinute),Snackbar.LENGTH_INDEFINITE);

                 snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                })
                .show();

        Log.d("rl2",String.valueOf(timeToDelay));
    }
}

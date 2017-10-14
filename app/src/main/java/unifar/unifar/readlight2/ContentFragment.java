package unifar.unifar.readlight2;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import java.util.Date;

import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment implements TimePickerListener{

    private MyViews myViews;
    private AmbilWarnaDialogFragment ambilWarnaDialogFragment;
    private ViewGroup mcontainer;
    private TimePickerListener msettingButtonListener;
    private AppCompatActivity mappCompatActivity;
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
        mappCompatActivity = (AppCompatActivity) context;
        if (context instanceof TimePickerListener){
            this.msettingButtonListener = (TimePickerListener)context;
        }
    }
    @Override public void onDetach(){
        super.onDetach();
        this.msettingButtonListener = null;
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
        ImageView ivSettingButton;
        Handler handler;
        SeekBar seekBar;
        final FragmentManager fragmentManeger;
        viContent = inflater.inflate(R.layout.fragment_content, container, false);
        handler = new Handler();
        fragmentManeger = getChildFragmentManager();
        vgContentFragmentContainer = viContent.findViewById(R.id.flContentFragmentContainer);
        ivColorPalette = viContent.findViewById(R.id.ivColorPalette);
        seekBar = viContent.findViewById(R.id.seekBar);
        ivSettingButton = viContent.findViewById(R.id.ivSettingButton);
        if (savedInstanceState == null) {
            currentColor = Color.LTGRAY;
        } else {
            currentColor = savedInstanceState.getInt("currentColor");
        }
        myViews = new MyViews(currentColor, vgContentFragmentContainer, ivColorPalette, handler, fragmentManeger, seekBar,(AppCompatActivity) getActivity(), ivSettingButton);
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
        Snackbar.make(this.mcontainer,getString(R.string.setTimeMessage,String.valueOf(hour),String.valueOf(minute)),Snackbar.LENGTH_INDEFINITE);

        Log.d("rl2",String.valueOf(timeToDelay));
    }
}

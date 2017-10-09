package unifar.unifar.readlight2;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment implements TimePickerListener{

    private MyViews myViews;
    private AmbilWarnaDialogFragment ambilWarnaDialogFragment;
    private ViewGroup mcontainer;
    private TimePickerListener msettingButtonListener;
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

    }
}

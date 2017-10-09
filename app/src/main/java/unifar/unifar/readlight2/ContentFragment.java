package unifar.unifar.readlight2;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;

import static android.R.attr.cropToPadding;
import static android.R.attr.fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment {

    private MyViews myViews;
    private AmbilWarnaDialogFragment ambilWarnaDialogFragment;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int currentColor;
        View viContent;
        ViewGroup vgContentFragmentContainer ;
        ImageView ivColorPalette;
        Handler handler;
        SeekBar seekBar;
        FragmentManager fragmentManeger;
        viContent = inflater.inflate(R.layout.fragment_content, container, false);

            handler = new Handler();
            fragmentManeger = getFragmentManager();
            vgContentFragmentContainer = viContent.findViewById(R.id.flContentFragmentContainer);
            ivColorPalette = viContent.findViewById(R.id.ivColorPalette);
            seekBar = viContent.findViewById(R.id.seekBar);
        if (savedInstanceState == null) {
            currentColor = Color.LTGRAY;
        } else {
            currentColor = savedInstanceState.getInt("currentColor");
        }
        myViews = new MyViews(currentColor, vgContentFragmentContainer, ivColorPalette, handler, fragmentManeger, seekBar, getActivity());
        myViews.setColorPalette();
        myViews.setTimeEvent();
        // create new instance of AmbilWarnaDialogFragment and set OnAmbilWarnaListener listener to it
        // show dialog fragment with some tag value
        return viContent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", myViews.getMcurrentColor());
    }


}

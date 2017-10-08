package unifar.unifar.readlight2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import yuku.ambilwarna.colorpicker.AmbilWarnaDialogFragment;
import yuku.ambilwarna.colorpicker.OnAmbilWarnaListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment {

    private int currentColor ;

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


        // create OnAmbilWarnaListener instance
        // new color can be retrieved in onOk() event
        OnAmbilWarnaListener onAmbilWarnaListener = new OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialogFragment dialogFragment) {
            }

            @Override
            public void onOk(AmbilWarnaDialogFragment dialogFragment, int color) {

                currentColor = color;
            }
        };

        // create new instance of AmbilWarnaDialogFragment and set OnAmbilWarnaListener listener to it
        // show dialog fragment with some tag value
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(currentColor);
        fragment.setOnAmbilWarnaListener(onAmbilWarnaListener);
        fragment.show(getFragmentManager(),"dolorPickerDiaog");
        return inflater.inflate(R.layout.fragment_content, container, false);

    }

}

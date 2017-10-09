package unifar.unifar.readlight2;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerDialogFragment extends DialogFragment {

    public TimerDialogFragment() {
    }
    // TODO: Rename and change types and number of parameters
    public static TimerDialogFragment newInstance() {
        TimerDialogFragment fragment = new TimerDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Inflate the layout for this fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("TestDialogFragment");
        builder.setView(R.layout.fragment_timer_dialog);
        Dialog dialog = builder.create();
        return dialog ;
    }

}

package unifar.unifar.readlight2

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SendNameFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SendNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SendNameFragment : androidx.fragment.app.Fragment() {
    private var isGold: Boolean = false
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isGold = it.getBoolean(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val db = FirebaseFirestore.getInstance()

        val view = inflater.inflate(R.layout.fragment_send_name, container, false)
        val editText = view.findViewById<EditText>(R.id.nameEditText)
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        if (isGold) {
            submitButton.setOnClickListener { button ->
                button.isEnabled = false
                db.collection("goldSupporters")
                        .add(HashMap<String, Any>()
                                .apply {
                                    put("name", editText.text.toString())
                                    put("time", FieldValue.serverTimestamp())
                                })
                        .addOnSuccessListener {
                            Toast.makeText(activity, R.string.success, Toast.LENGTH_SHORT).show()
                            fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, SettingFragment.newInstance())?.commit()
                        }.addOnFailureListener {
                            button.isEnabled = true
                        }
            }
        } else {
            submitButton.setOnClickListener { button ->
                button.isEnabled = false
                db.collection("normalSupporters")
                        .add(HashMap<String, Any>()
                                .apply {
                                    put("name", editText.text.toString())
                                    put("time", FieldValue.serverTimestamp())
                                })
                        .addOnSuccessListener {
                            Toast.makeText(activity, R.string.success, Toast.LENGTH_SHORT).show()
                            fragmentManager?.beginTransaction()?.replace(R.id.mainActivityContainer, SettingFragment.newInstance())?.commit()
                        }.addOnFailureListener {
                            button.isEnabled = true
                        }
            }
        }
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param isGold Parameter 1.
         * @return A new instance of fragment SendNameFragment.
         */
        @JvmStatic
        fun newInstance(isGold: Boolean) =
                SendNameFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_PARAM1, isGold)
                    }
                }
    }
}

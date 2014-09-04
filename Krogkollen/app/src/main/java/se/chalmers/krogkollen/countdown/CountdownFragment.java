package se.chalmers.krogkollen.countdown;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.ParseBackend;
import se.chalmers.krogkollen.pub.PubCrawl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CountdownFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CountdownFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CountdownFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView countdown, description;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CountdownFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CountdownFragment newInstance(String param1, String param2) {
        CountdownFragment fragment = new CountdownFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CountdownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_countdown, container, false);

        countdown = (TextView) view.findViewById(R.id.countdown_number);
        description = (TextView) view.findViewById(R.id.countdown_main_text);
        Time currentDate = new Time();
        currentDate.setToNow();

        PubCrawl pubCrawl = null;
        try {
            pubCrawl = ParseBackend.getNextPubcrawl();
        } catch (NoBackendAccessException ignored) {}

        System.out.println("YEAR: " + currentDate.year + " MONTH: " + currentDate.month + " DAY: " + currentDate.monthDay);
        System.out.println("YEAR: " + (pubCrawl.getDate().getYear() + 1900) + " MONTH: " + pubCrawl.getDate().getMonth() + " DAY: " + pubCrawl.getDate().getDate());

        String desc = pubCrawl.getDescription().replace("\\n", "\n");
        description.setText(desc);

        Date date = pubCrawl.getDate();
        date.setYear(pubCrawl.getDate().getYear() + 1900);

        int a = daysBetween(new Date(currentDate.year, currentDate.month, currentDate.monthDay), date);

        String counter = "" + a;

        countdown.setText(counter);

        return view;
    }

    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().remove(CountdownFragment.this).commit();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onFragmentInteraction(null);
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

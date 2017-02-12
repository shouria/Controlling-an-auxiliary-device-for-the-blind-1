package com.noName.noName;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class MainFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "MainFragment";
    private View mView;
    private Button btn;
    private ImageView bg_img_view;
    private int curr_img_idx;
    private CountDownTimer timer;
//    private final int[] bg_img_arr = {R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4, R.drawable.bg_5};

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BtnListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "Fragment: onCreate");

        curr_img_idx=0;
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DEBUG", "Fragment: onCreateView");

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        bg_img_view = (ImageView) mView.findViewById(R.id.bg_img_main);
        bg_img_view.setImageResource(R.drawable.bg_1);
        btn = (Button) mView.findViewById(R.id.btn_main_menu);
        btn.setOnClickListener(this);

        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();


        /*// Set the timer for switching background imgs
        timer = new CountDownTimer(2000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                try{
                    bg_switcher();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();*/


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    ////////////////////////////////////////////////////////////////////////


    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (view.getId() == btn.getId() ) {
                // Send the event to the main activity
                mListener.onMenuClick();
            }
        }
    }

    /*

    Switch background pictures
    */
    /*private void bg_switcher() {
        bg_img_view.setImageResource(bg_img_arr[curr_img_idx]);
        curr_img_idx = curr_img_idx == bg_img_arr.length-1 ? 0 : curr_img_idx+1;
        timer.start();
    }*/

    ////////////////////////////    interface  ////////////////////////////////

    public interface OnFragmentInteractionListener {
        void onMenuClick();
    }


}

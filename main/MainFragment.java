package com.example.orcam.mymebasicapp.main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.orcam.mymebasicapp.R;

import java.util.Timer;
import java.util.TimerTask;


public class MainFragment extends Fragment{

    public static final String TAG = "MainFragment";
    private View mView;
    private ImageButton btn_bt_connect;
    private Button btn_menu;
    private ViewPager viewPager;
    private LinearLayout sliderDotsPanel;
    private int dotsCount;
    private ImageView[] dots;
    private Timer mTimer;
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
        Log.d(TAG, "Fragment: onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Fragment: onCreateView");

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        // MyEye images slider
        images_Slider();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        // Bluetooth button
        btn_bt_connect = (ImageButton) mView.findViewById(R.id.btn_BT);
        btn_bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onBtConnect();
            }
        });

        // Menu button
        btn_menu = (Button) mView.findViewById(R.id.btn_main_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListener.onBtnMenu();
            }
        });



        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Disable orientation change
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void images_Slider() {
        viewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        sliderDotsPanel = (LinearLayout) mView.findViewById(R.id.sliderDots);
        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getContext());
        viewPager.setAdapter(viewPagerAdapter);

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i=0 ; i < dotsCount ; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotsPanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i=0 ; i < dotsCount ; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    ////////////////////////////    MyTimerTask  ////////////////////////////////

    /*

    Switch pictures auto
    */
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == dotsCount - 1) {
                        viewPager.setCurrentItem(0);
                    }
                    else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
            });
        }
    }

    ////////////////////////////    Interface  ////////////////////////////////

    public interface OnFragmentInteractionListener {
        void onBtConnect();
        void onBtnMenu();
    }


    @Override
    public void onPause() {
        super.onPause();
        // Disable orientation change
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        mTimer.cancel();
        mTimer.purge();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

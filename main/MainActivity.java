package com.example.orcam.mymebasicapp.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bt.Model.MyMeError;
import com.example.interfaces.enums.BTConnectionState;
import com.example.interfaces.enums.Subscription;
import com.example.logic.API.Interfaces.ARMResponseListener;
import com.example.logic.API.MyMe;
import com.example.logic.API.MyMeCallback;
import com.example.orcam.mymebasicapp.R;
import com.example.orcam.mymebasicapp.connectoin.OneClickConnectionDialog;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private ProgressDialog mProgress;
    private Boolean BT_STATE = false; // Run the app with BT connection
    private static String JSON_DEBUG = "{}";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = new ProgressDialog(this);

        // Create toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // If that state restored, no need to re-create the fragment
        if (savedInstanceState != null) {
            return;
        }

        MainFragment mainFragment = new MainFragment();

        // In case this activity started by other application -> intent
        // Pass the Intent's extra to the fragment as arguments
        mainFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' frameLayout
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();

        Log.d("DEBUG", "Main Activity: after onCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ////////////////////////////// init MyEye -> BT  ////////////////////////////////

    @Override
    public void onBtConnect() {
        initMyeye(false);
    }

    private void initMyeye(final Boolean auto_ext_menu) {
        final MyMeCallback myMeCallback = new MyMeCallback(){
            @Override
            public void pairedDeviceStateUpdated(BTConnectionState state) {
                OneClickConnectionDialog frag = (OneClickConnectionDialog) getFragmentManager().findFragmentByTag(OneClickConnectionDialog.class.getSimpleName());
                if (frag != null) {
                    frag.setState(state);
                    // BT connected -> enter to menu and get json tree
                    if(auto_ext_menu && (state.equals(BTConnectionState.DEVICE_SUBSCRIBED) || state.equals(BTConnectionState.DEVICE_SUBSCRIBING)) ) {
                        enterExtMenu();
                        frag.dismiss();
                    }
                }
            }
        };
        MyMe.getInstance().initMyMe(this, myMeCallback, Subscription.CONTROL.getValue());
        goToConnection();
    }


    private void goToConnection() {
        if(MyMe.getInstance().isDeviceConnected() || MyMe.getInstance().isDeviceConnecting()){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.do_you_want_to_disconnect))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyMe.getInstance().disconnect();
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else{
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            android.app.Fragment prev = getFragmentManager().findFragmentByTag(OneClickConnectionDialog.class.getSimpleName());
            if (prev != null) {
                ft.remove(prev);
            }

            // Create and show the dialog.
            OneClickConnectionDialog frag = OneClickConnectionDialog.newInstance();
            frag.show(getFragmentManager(), OneClickConnectionDialog.class.getSimpleName());
        }
    }


    ////////////////////////////    Enter to MyEye Menu   /////////////////////////////////

    @Override
    public void onBtnMenu() {
        if (BT_STATE) {
            getJsonHandler();
        }

        else {
            // Create new fragment and replace
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, MenuFragment.newInstance(JSON_DEBUG, BT_STATE))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void getJsonHandler() {
        // Checking BT connection
        if( !MyMe.getInstance().isDeviceConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.bt_conn_is_req))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initMyeye(true);
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            enterExtMenu();
        }
    }


    private void enterExtMenu() {
        // Progress dialog
        GeneralUtils.setProgressDialog("", getString(R.string.loading), mProgress);

        // Send EnterExtMenuStat request
        MyMe.getInstance().getControlLogic().extMenuEnterExtMenuState(new ARMResponseListener() {
            @Override
            public void onSuccess(Object res) {
                if (GeneralUtils.responseParser(res.toString()).equals("Entered ExtMenu")) {
                    Log.d(TAG, "enterExtMenu, Entered ExtMenu");
                    getSettingsJson();
                }
                else {
                    Log.d(TAG, "enterExtMenu, Unexpected response of EnterExtMenuState: " + res.toString());
                }
            }

            @Override
            public void onError(MyMeError myMeError) {
                System.out.print(myMeError.getErrorMsg());
            }
        });
    }


    ////////////////////////////    Get Json Settings   /////////////////////////////////

    public void getSettingsJson() {
        MyMe.getInstance().getControlLogic().extMenuGetJsonTree(new ARMResponseListener() {
            @Override
            public void onSuccess(Object res) {
                if (GeneralUtils.responseParser(res.toString()).equals("")) {
                    Log.d(TAG, "getSettingsJson, Response of EnterExtMenuState is empty");
                }
                // Get the json tree
                else {
                    // Create new fragment and replace
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, MenuFragment.newInstance(res.toString(), BT_STATE))
                            .addToBackStack(null)
                            .commit();
                }

                mProgress.dismiss();
            }

            @Override
            public void onError(MyMeError myMeError) { System.out.print(myMeError.getErrorMsg()); }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.noName.noName;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";
    private Fragment curr_fragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "Main Activity: after view created");


        // Create toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // If that state restored, no need to re-create the fragment
        if (savedInstanceState != null) {
            return;
        }

        MainFragment mainFragment = new MainFragment();

        // In case this activity started by other application -> intent
        // Pass the Itent's extra to the fragment as arguments
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


    ////////////////////////////    Main Screen   /////////////////////////////////


    public void onMenuClick() {
        // Create new fragment and replace
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, MenuFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    ////////////////////////////    Menu Screen   /////////////////////////////////

    //lifecycle


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DEBUG", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("DEBUG", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG", "onDestroy");

    }
}

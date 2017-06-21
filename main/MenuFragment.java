package com.example.orcam.mymebasicapp.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.bt.Model.MyMeError;
import com.example.logic.API.Interfaces.ARMResponseListener;
import com.example.logic.API.MyMe;
import com.example.orcam.mymebasicapp.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.Settings;

import static android.text.TextUtils.isDigitsOnly;


public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";
    private static final String ARG_JSON_STR = "json_str";
    private static final String ARG_BT_STATE = "bt_state";
    private static String mJsonStr;
    private final String SWITCH = "switch";
    private final String LIST = "list";
    private final String SEEK_BAR = "seek_bar";
    private Boolean is_main_sec = true; // That for decide which style
    private View mView;
    private ProgressDialog mProgress;
    private Boolean BT_STATE;
    private Button btn_upload;
    private Button btn_restore;

    // Json args names
    private final String J_TITLE = "A.name";
    private final String J_CURRENT = "current";
    private final String J_ENTRIES = "entries";
    private final String J_OP = "setEvent";
    private final String J_TYPE = "type";
    private final String J_ARG = "EventArg";
    private final String J_EXT_ARG = "extra Arg";
    private final String J_ACT = "Action";


//    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(String JsonStr, Boolean bt_state) {
        MenuFragment fragment = new MenuFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_JSON_STR, JsonStr);
        bundle.putBoolean(ARG_BT_STATE, bt_state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            BT_STATE = bundle.getBoolean(ARG_BT_STATE);
            if (BT_STATE) {
                mJsonStr = GeneralUtils.responseParser(bundle.getString(ARG_JSON_STR));
            } else {
                mJsonStr = bundle.getString(ARG_JSON_STR);
            }

        }
        mProgress = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_menu, container, false);

        // Cloud buttons
        btn_upload = (Button) mView.findViewById(R.id.btn_upload_to_cloud);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                upload_settings_to_cloud("0", mJsonStr);
            }
        });

        btn_restore = (Button) mView.findViewById(R.id.btn_restore_from_cloud);
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                restore_settings_from_cloud("0");
            }
        });


        Section sectionArr[] = jsonHandler();
        if (sectionArr == null) {
            Log.w(TAG, "onCreateView: No found sections to display, can't create the menu items");
            // TODO: 12/20/16 alert error and return to the main page
            return mView;
        }

        final LinearLayout lm = (LinearLayout) mView.findViewById(R.id.content_menu_layout);
        drawMenu(lm, sectionArr);
//        send_settings_to_server("urias", "will be json!!");
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Disable orientation change
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    ////////////////////////////   JSON  ////////////////////////////////

    /*
    Handle and parse json
    Return: Section[]
    */
    private Section[] jsonHandler() {
        Section sec_arr[] = null;
        try {
            JSONObject jObj = new JSONObject(mJsonStr);
            JSONArray jSecArr = jObj.getJSONArray(J_ENTRIES);
            sec_arr = new Section[jSecArr.length()];

            // Run over the entries
            for (int i=0 ; i < jSecArr.length() ; i++) {
                sec_arr[i] = create_sec_obj(jSecArr.getJSONObject(i));
            }
        } catch (JSONException e) {
            System.err.println("Exception when parsing the menu json");
            e.printStackTrace();
        }

        return sec_arr;
    }

    /*
    Creates section object Parse json
    Return: Section
    */
    private Section create_sec_obj(JSONObject jObj) {
        Section section = null;
        String title;
        int curr_idx_value;
        String op;
        String arg;
        String ext_arg;
        String[] value_arr;
        JSONArray jArr;
        try {
            title = jObj.getString(J_TITLE);

            if (title.equals("ActionGoBackLong")) {return null;}

            curr_idx_value = jObj.getInt(J_CURRENT);
            op = jObj.has(J_OP) ? jObj.getString(J_OP) : null;
            arg = jObj.has(J_ARG) ? jObj.getString(J_ARG) : null;
            ext_arg = jObj.has(J_EXT_ARG) ? jObj.getString(J_EXT_ARG) : null;
            jArr = jObj.has(J_ENTRIES) ? jObj.getJSONArray(J_ENTRIES) : null;

            if (jArr == null || jArr.length() == 0) {
                Log.w(TAG, "create_sec_obj: entries array is empty in: " + title);
                return null;
            }

            /*
                An new section with values
            */
            if (isOnlyValues(jArr)) {
                // Get the names of values
                value_arr = new String[jArr.length()];
                for (int i=0 ; i < jArr.length() ; i++) {
                    String value_type = jArr.getJSONObject(i).getString(J_TYPE);
                    if ( !value_type.equalsIgnoreCase("action") ) {
                        Log.w(TAG, "create_sec_obj: wrong type in value array: " + value_type);
                        value_arr[i] = null;
                        continue;
                    }

                    value_arr[i] = jArr.getJSONObject(i).getString(J_TITLE);
                }

                // Create an new section
                section = new Section(title, value_arr, curr_idx_value, op, arg, ext_arg);
                return section;
            }



            /*
                An new section with sub sections
                Run over the sub sections recursively (Can't be values, values already handled)
            */
            else {
                jArr = jObj.getJSONArray(J_ENTRIES);
                if (jArr == null || jArr.length() == 0) {
                    Log.w(TAG, "create_sec_obj: entries array is empty in: " + title);
                    return null;
                }

                section = new Section(title, jArr.length());
                for(int i = 0 ; i < jArr.length() ; i++) {
                    Section sub_section = create_sec_obj(jArr.getJSONObject(i));    // Recursive
                    if (sub_section != null) {
                        section.appendSubSection(sub_section);
                    }
                }
                return section;
            }

        } catch (JSONException e) {
            System.err.println("Exception when creating section object: " + jObj.toString());
            e.printStackTrace();
        }

        return section;
    }


    /*
    Check if all the children are values -> type = 'action'
    Return: Boolean
    */
    private Boolean isOnlyValues(JSONArray jArr) throws JSONException {
        for (int i=0 ; i < jArr.length() ; i++) {
            if ( !jArr.getJSONObject(i).getString(J_TYPE).equals(J_ACT) ) {
                return false;
            }
        }
        return true;
    }


    ////////////////////////////    Section class  ////////////////////////////////

    private class Section {
        private String title;
        private int curr_idx_value;
        private String view_type;
        private String[] value_arr;
        private String op;
        private String arg;
        private String ext_arg;
        private Section[] sub_sec_arr;

        public Section(String title, String[] value_arr, int curr_idx_value, String op, String arg, String ext_arg) {
            this.title = splitByUppercase(title, 1);
            this.curr_idx_value = curr_idx_value;
            this.op = op;
            this.arg = arg;
            this.ext_arg = ext_arg;

            // Value array
            this.value_arr = new String[value_arr.length];
            for (int i = 0 ; i < value_arr.length ; i++) {
                this.value_arr[i] = splitByUppercase(value_arr[i], 0);
            }

            this.view_type = getViewTypeByValueArr(value_arr);
        }

        // Array of sections
        public Section(String title, int numOfSubSection) {
            this.title = splitByUppercase(title, 1);
            this.sub_sec_arr = new Section[numOfSubSection];

            this.curr_idx_value = -1;
            this.op = null;
            this.arg = null;
            this.ext_arg = null;
            this.value_arr = null;
            this.view_type = null;
        }

        private String splitByUppercase(String value_Str, int start_idx) {
            if (value_Str == null) {return null;}

            String sepStr = "";
            String[] strArr = value_Str.split("(?<=[a-z])(?=[A-Z])");
            for (int i = start_idx ; i < strArr.length ; i++) {
                sepStr = sepStr.concat(strArr[i] + " ");
            }
            sepStr = sepStr.trim();
            return sepStr;
        }

        private String getViewTypeByValueArr(String[] value_arr) {
            String viewType = LIST;

            if (value_arr.length == 0) {
                Log.w("Menu Fragment", "getViewType: The value array is empty");
            }

            // Switch
            else if (value_arr.length == 2) {
                String value_1 = value_arr[0].toLowerCase();
                String value_2 = value_arr[1].toLowerCase();

                // enable/disable or on/off or true/false
                if ( ((value_1.equals("enable") && value_2.equals("disable")) ||
                        (value_1.equals("disable") && value_2.equals("enable"))) ||
                        ((value_1.equals("on") && value_2.equals("off")) ||
                                (value_1.equals("off") && value_2.equals("on"))) ||
                        ((value_1.equals("true") && value_2.equals("false")) ||
                                (value_1.equals("false") && value_2.equals("true")))) {

                    viewType = SWITCH;
                    return viewType;
                }
            }

            // seek bar
            Boolean seek_bar_flag = true;
            for (String value : value_arr) {
                if ( !isDigitsOnly(value) ) { seek_bar_flag = false; }
            }
            if (seek_bar_flag) { viewType = SEEK_BAR; }

            return viewType;
        }

        /*
        Append a new section object
        */
        private void appendSubSection(Section section) {
            if (section != null) {
                for (int i = 0; i < this.sub_sec_arr.length; i++) {
                    if (this.sub_sec_arr[i] == null) {
                        this.sub_sec_arr[i] = section;
                        break;
                    }
                }
            }
        }

        public void setCurrIdxValue(int new_idx_value) { this.curr_idx_value = new_idx_value; }

        public String getTitle() {
            return title;
        }
        public String getOp() {return op; }
        public int getCurrIdxValue() {
            return curr_idx_value;
        }
        public String getViewType() { return view_type; }
        public String[] getValueArr() {
            return value_arr;
        }
        public Section[] getSubSecArr() {
            return sub_sec_arr;
        }
        public String getArg() { return arg; }
        public String getExtArg() { return ext_arg; }
    }




    ////////////////////////////  Draw views  ////////////////////////////////

    /*
    Draw all the Sections (Views)
    */
    private void drawMenu(LinearLayout lm, Section[] sectionArr) {
        int style;
        Section sec;
        try {
            for (int i=0 ; i < sectionArr.length ; i++) {
                sec = sectionArr[i];
                if (sec == null) {
                    Log.d(TAG, "drawMenu: section is null");
                    continue;
                }

                // Values
                if (sec.getSubSecArr() == null && sec.getValueArr() != null) {
                    style = is_main_sec ? R.style.mainMenuStyle : R.style.menuStyle;

                    switch (sec.getViewType()) {

                        // Draw switch
                        case SWITCH:
                            drawSwitchView(lm, sec, style);
                            break;

                        // Draw seek bar
                        case SEEK_BAR:
                            drawSeekBar(lm, sec, style);
                            break;

                        // Draw alert dialog -> List
                        case LIST:
                            drawItem(lm, sec, -1, style);
                            break;

                        default:
                            Log.d(TAG, "drawMenu: unknown view type");
                    }

                    // After each view draw a line
                    if (i != sectionArr.length - 1) {
//                        drawLine(lm, -1);
                    }
                }

                // sub sections
                else if (sec.getSubSecArr() != null) {
                    // Draw section
                    style = is_main_sec ? R.style.menuTitle : R.style.menuStyle;
                    drawSection(lm, sec.getTitle(), sec.getSubSecArr(), style);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        is_main_sec = false;
    }

    private void drawSwitchView(LinearLayout lm, final Section sec, int style) {
        Switch sv = new Switch(new ContextThemeWrapper(mView.getContext(), style));
        sv.setChecked(sec.curr_idx_value == 0 ? true : false);
        sv.setText(sec.title);
        sv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String value_selected = isChecked ? sec.value_arr[0] : sec.value_arr[1];
                if (BT_STATE) {
                    sendOrcamPlease(sec, value_selected, isChecked ? 0 : 1);
                }
                else {
                    sec.setCurrIdxValue(isChecked ? 0 : 1);
                }
            }
        });

        // Append to linear layout
        lm.addView(sv);
    }

    private void drawSeekBar(LinearLayout lm, final Section sec, int style) {
        // Defines max/min values
        int max_val = Integer.parseInt(sec.value_arr[sec.value_arr.length-1]);
        final int min_val = max_val - (sec.value_arr.length - 1);

        // Create SeekBar
        DiscreteSeekBar dsb = new DiscreteSeekBar(mView.getContext());
        dsb.setMin(min_val);
        dsb.setMax(max_val);    // The max value defines bubble size - > important!!!
        dsb.setProgress(sec.curr_idx_value + min_val);

        // Change the color of the seekbar
        dsb.setScrubberColor(0xFF467FDA);           // Left line
        dsb.setThumbColor(0xFF467FDA, 0xFF467FDA);  // Circle and bubble

        DiscreteSeekBar.OnProgressChangeListener dsb_listener = new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                seekBar.setIndicatorFormatter(sec.value_arr[value - min_val]);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                seekBar.setIndicatorFormatter(sec.value_arr[seekBar.getProgress() - min_val]);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                String value_selected = sec.value_arr[seekBar.getProgress() - min_val];
                /*String res_send = "value: " + sec.value_arr[seekBar.getProgress() - min_val] +
                        "\nop: " + sec.op;
                Toast.makeText(mView.getContext(), res_send, Toast.LENGTH_SHORT).show();*/
                if (BT_STATE) {
                    sendOrcamPlease(sec, value_selected, seekBar.getProgress() - min_val);
                }
                else {
                    sec.setCurrIdxValue(seekBar.getProgress() - min_val);
                }
            }
        };

        dsb.setOnProgressChangeListener(dsb_listener);

        // Create the text view
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), style));
        tv.setText(sec.title);

        // Append to linear layout
        lm.addView(tv);
        lm.addView(dsb);
    }

    private void drawSection(final LinearLayout lm, final String title, final Section[] sub_sec_arr, int style) {
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), style));
        tv.setText(title);

        // Define listener
        tv.setOnClickListener(new View.OnClickListener() {
            Boolean touch_flag = true;
            @Override
            public void onClick(View view) {
                int parent_idx = lm.indexOfChild(view);

                // Draw the sub sections
                if (touch_flag) {
                    touch_flag = false;

                    // Create linear layout which contains the views
                    LinearLayout new_ll = new LinearLayout(new ContextThemeWrapper(mView.getContext(),
                                                                            R.style.menuStyle));
                    new_ll.setOrientation(LinearLayout.VERTICAL);
                    addViewToLayout(lm, new_ll, parent_idx);

                    // Draw strong line
                    drawLine(new_ll, -1);

                    // Draw sub section
                    drawMenu(new_ll, sub_sec_arr);
                }

                // Remove the sub sections (the new linearlayout)
                else {
                    touch_flag = true;
                    lm.removeViewAt(parent_idx + 1); // Shift to linearlayout
                }
            }
        });

        // Add to linear layout
        addViewToLayout(lm, tv, -1);
    }

    private void drawItem(LinearLayout lm, final Section sec , int idx, int style) {
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), style));
        tv.setText(sec.title);

        // Define listener
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create AlertDialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                builder.setTitle(sec.title);
                builder.setSingleChoiceItems(sec.value_arr, sec.curr_idx_value, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value_selected = sec.value_arr[i];
                        if (BT_STATE) {
                            sendOrcamPlease(sec, value_selected, i);
                        } else {
                            sec.setCurrIdxValue(i);
                        }

                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // Add to linear layout
        addViewToLayout(lm, tv, idx);
    }

    private void drawLine(LinearLayout lm, int idx) {
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), R.style.line));

        // Add to linear layout
        addViewToLayout(lm, tv, idx);
    }

    /*
        Add view to the linearlayout by index
    */
    private void addViewToLayout(LinearLayout lm, View view, int idx) {
        // Append to the layout
        if (idx == -1) {
            /*View last_view = mView.findViewById(R.id.menu_footer_layout);
            lm.addView(view, lm.indexOfChild(last_view));*/
            lm.addView(view);
        }
        else {
            lm.addView(view ,idx + 1);    // Add after this index
        }
    }


    ////////////////////////////   send op   ////////////////////////////////

    private void sendOrcamPlease(final Section sec, String value_selected, final int new_idx_value) {
//        String name, String op, String arg, String extArg
        // Progress dialog
        GeneralUtils.setProgressDialog("", getString(R.string.loading), mProgress);

        MyMe.getInstance().getControlLogic().extMenuOrcamPlease(value_selected, sec.getOp(), sec.getArg(), sec.getExtArg(), new ARMResponseListener() {
            @Override
            public void onSuccess(Object res) {
                Log.d(TAG, "sendOrcamPlease, response is :" + GeneralUtils.responseParser(res.toString()));
                sec.setCurrIdxValue(new_idx_value);
                mProgress.dismiss();
            }

            @Override
            public void onError(MyMeError myMeError) {
                System.out.print(myMeError.getErrorMsg());

            }
        });
    }


    ////////////////////////////  client Api  ////////////////////////////////

    private void upload_settings_to_cloud(String user_id, String menu_json) {
        DefaultApi apiInstance = new DefaultApi();
        Settings settings = new Settings(); // Settings | The settings to insert.
        settings.setUserId(user_id);
        settings.setSettingJson(menu_json);
        settings.setTimestemp(new Date());

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgress.dismiss();

                if (response != null) {
                    GeneralUtils.setAlertDialo(getString(R.string.uploaded_successfully), mView.getContext());
                }
                else {
                    GeneralUtils.setAlertDialo(getString(R.string.uploading_failed), mView.getContext());
                }
                Log.d(TAG, "upload_settings_to_cloud, Server response: " + response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GeneralUtils.setAlertDialo(getString(R.string.uploading_failed), mView.getContext());
                System.err.println("Response error: " + error);
            }
        };

        GeneralUtils.setProgressDialog("", getString(R.string.uploading), mProgress);

        try {
            apiInstance.settingsPost(settings, responseListener, errorListener);
        } catch (Exception e) {
            mProgress.dismiss();
            GeneralUtils.setAlertDialo(getString(R.string.uploading_failed), mView.getContext());
            System.err.println("Exception when calling DefaultApi#settingsPost: " + e);
            e.printStackTrace();
        }
    }


    private void restore_settings_from_cloud(String user_id) {
        DefaultApi apiInstance = new DefaultApi();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgress.dismiss();

                if (response != null) {
                    GeneralUtils.setAlertDialo(getString(R.string.restored_successfully), mView.getContext());
                }
                else {
                    GeneralUtils.setAlertDialo(getString(R.string.restore_failed), mView.getContext());
                }
                Log.d("DEBUG", "Server response: " + response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GeneralUtils.setAlertDialo(getString(R.string.restore_failed), mView.getContext());
                System.err.println("Response error: " + error);
            }
        };

        GeneralUtils.setProgressDialog("", getString(R.string.loading), mProgress);

        try {
            apiInstance.settingsGet(user_id, responseListener, errorListener);
        } catch (Exception e) {
            mProgress.dismiss();
            GeneralUtils.setAlertDialo(getString(R.string.restore_failed), mView.getContext());
            System.err.println("Exception when calling DefaultApi#settingsGet: " + e);
            e.printStackTrace();
        }
    }


    ////////////////////////////    interface  ////////////////////////////////

    /*
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }*/


    @Override
    public void onPause() {
        super.onPause();
        // Disable orientation change
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        if (BT_STATE) {
            MyMe.getInstance().getControlLogic().extMenuExitExtMenuState(new ARMResponseListener() {
                @Override
                public void onSuccess(Object res) {
                    Log.d(TAG, "onPause, exit from extMenu, response is :" + GeneralUtils.responseParser(res.toString()));
                }

                @Override
                public void onError(MyMeError myMeError) { System.out.print(myMeError.getErrorMsg()); }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current opened section in case of recreate the fragment
        // TODO: 5/29/17
//        outState.putStringArrayList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
package com.noName.noName;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";
    private final String SWITCH = "switch";
    private final String LIST = "list";
    private View mView;

//    private OnFragmentInteractionListener mListener;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String str_json = "{}";
        Section sectionArr[] = jsonHandler(str_json);
        if (sectionArr == null) {
            Log.w(TAG, "onCreateView: No found sections from json, can't create the menu items");
            // TODO: 12/20/16 return to the main page
        }

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_menu, container, false);
        final LinearLayout lm = (LinearLayout) mView.findViewById(R.id.linear_menu);
        drawSections(lm, sectionArr);
        return mView;
    }


    ////////////////////////////    views  ////////////////////////////////

    /*
    Draw all the Sections (Views)
    Recursively
    */
    private void drawSections(LinearLayout lm, Section[] sectionArr) {
        try {
            for (int i = 0 ; i < sectionArr.length ; i++) {
                if (sectionArr[i] == null) {
                    Log.d(TAG, "drawSections: section is null");
                    continue;
                }

                // Only values
                if (sectionArr[i].getSubSecArr() == null && sectionArr[i].getValueArr() != null) {
                    // Switch
                    if (sectionArr[i].getViewType().equals(SWITCH)) {
                        drawSwitchView(lm, sectionArr[i].getTitle(), false);
                    }
                    // Alert dialog
                    else if (sectionArr[i].getViewType().equals(LIST)) {
                        // Draw seek bar
                        if (sectionArr[i].getTitle().contains("Volume")) {
                            drawSeekBar(lm, sectionArr[i].getTitle(), sectionArr[i].getValueArr().length, 3);
                        }
                        // Alert dialog
                        else {
                            drawItem(lm, sectionArr[i].getTitle(), sectionArr[i].valueArr);
                        }

                    }
                }

                // sub sections
                else if (sectionArr[i].getSubSecArr() != null) {
                    // not the first section -> add space to ui
                    if (i != 0) {
                        drawEmptyView(lm);
                    }

                    // Draw title of the current section
                    drawTitle(lm, sectionArr[i].getTitle());
                    // draw strong line
                    drawLine(lm, true);

                    Section[] subSecArr = sectionArr[i].getSubSecArr();

                    // Draw title of the current section
                    drawSections(lm, subSecArr);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void drawSwitchView(LinearLayout lm, String title, boolean checked) {
        Switch sv = new Switch(new ContextThemeWrapper(mView.getContext(), R.style.menuStyle));
        sv.setChecked(checked);
        sv.setText(title);
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch sv = (Switch)view;
                Toast.makeText(mView.getContext(), sv.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // Append to linear lyout
        lm.addView(sv);
    }

    private void drawSeekBar(LinearLayout lm, String title, int numMax, int currNum) {
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), R.style.menuStyle));
        tv.setText(title);

        SeekBar sb = new SeekBar(new ContextThemeWrapper(mView.getContext(),
                                            R.style.menuStyle));
        sb.setMax(numMax);
        sb.setProgress(currNum);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(mView.getContext(), Integer.toString(seekBar.getProgress()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }
        });

        // Append to linear lyout
        lm.addView(tv);
        lm.addView(sb);
    }

    private void drawTitle(LinearLayout lm, String title) {
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), R.style.menuTitle));
        tv.setText(title);

        // Append to linear lyou
        lm.addView(tv);
    }

    private void drawItem(LinearLayout lm, final String title, final String[] valueArr) {
        TextView tv = new TextView(new ContextThemeWrapper(mView.getContext(), R.style.menuStyle));
        tv.setText(title);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                builder.setTitle(title);
                builder.setItems(valueArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(mView.getContext(), valueArr[i], Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // Append to linear lyou
        lm.addView(tv);
    }

    private void drawLine(LinearLayout lm, boolean strongLine) {
        LinearLayout ly = new LinearLayout(new ContextThemeWrapper(mView.getContext(),
                                strongLine ? R.style.strongLine : R.style.softLine));

        // Append to linear lyout
        lm.addView(ly);
    }

    private void drawEmptyView(LinearLayout lm) {
        LinearLayout ly = new LinearLayout(new ContextThemeWrapper(mView.getContext(),
                                                                R.style.emptyView));

        // Append to linear lyout
        lm.addView(ly);
    }



    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (MainFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BtnListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    ////////////////////////////   JSON  ////////////////////////////////

    /*
    Handle and parse json
    Return: Section[]
    */
    private Section[] jsonHandler(String str_json) {
        Section sectionArr[] = null;
        try {
            JSONObject jsonObj = new JSONObject(str_json);
            JSONArray jSecArr = jsonObj.getJSONArray("entries");
            // The '-1' is because 'ActionExit', no needed this section
            sectionArr = new Section[jSecArr.length()-1];

            // Run over the sections
            for(int i = 0 ; i < jSecArr.length()-1 ; i++) {
                sectionArr[i] = jsonParser(jSecArr.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sectionArr;
    }

    /*
    Parse json and create section object
    Return: Section
    */
    private Section jsonParser(JSONObject jSecObj) {
        Section section = null;
        String[] valueArr = null;
        JSONArray jSecArr = null;
        try {
            String title = jSecObj.getString("name");
            if (title.equalsIgnoreCase("ActionGoBackLong")) {
                return null;
            }
            String selectedValue = null;
            if (jSecObj.getString("type").equalsIgnoreCase("menu")) {
                selectedValue = jSecObj.getString("selected");
            }

            if (jSecObj.has("entries")) {
                jSecArr = jSecObj.getJSONArray("entries");
                if (jSecArr.length() == 0) {
                    Log.w(TAG, "jsonParser: section empty," +
                            "\nIn json obj: " + jSecArr.toString(1));
                    return null;
                }
            }
            else {return null;}

            // Section has not sub sections -> it means only values (The edges of json tree)
            if ( !isNewSection(jSecArr) ) {
                valueArr = valueParser(jSecArr);
                section = new Section(title);
                section.setValueArr(valueArr, selectedValue);
                return section;
            }

            // Run over the sub sections recursively (Can't be values, values already handled)
            // The '-1' is because 'ActionGoBackLong', no needed this section
            section = new Section(title, jSecArr.length()-1);
            for(int i = 0 ; i < jSecArr.length() ; i++) {
                Section subSection = jsonParser(jSecArr.getJSONObject(i));    // Recursive
                if (subSection != null) {
                    section.appendSubSection(subSection);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return section;
    }


    /*
    Parse the values
    Return: String[]
    */
    private String[] valueParser(JSONArray jSubSecArr) throws JSONException {
        if (jSubSecArr == null || jSubSecArr.length() == 0) {return null;}

        String[] valueArr = new String[jSubSecArr.length()];
        // Run over the values and append to array
        for(int j = 0 ; j < valueArr.length ; j++) {// TODO: 12/28/16 there are same that type != action, it means this is another section
            JSONObject currValueObj = jSubSecArr.getJSONObject(j);
            if (currValueObj.getString("type").equalsIgnoreCase("action")) {
                valueArr[j] = currValueObj.getString("name");
            }
        }
        return valueArr;
    }

    /*
    Check if this is another section or list of items
    */
    private boolean isNewSection(JSONArray jArr) throws JSONException {
        // Run over the sub sections
        for (int i = 0; i < jArr.length(); i++) {
            if (jArr.getJSONObject(i).has("type") && ! jArr.getJSONObject(i).getString("type").equalsIgnoreCase("action")) {
                return true;
            }
        }
        return false;
    }

    ////////////////////////////    Section class  ////////////////////////////////

    private class Section {
        private String title;
        private String selectedValue;
        private String viewType;
        private String[] valueArr;
        private Section[] subSecArr;

        public Section(String title) {
            this.title = "";
            this.title = splitByUppercase(title, 1);
        }

        private String splitByUppercase(String conStr, int startIdx) {
            if (conStr == null) {return null;}

            String sepStr = "";
            String[] strArr = conStr.split("(?<=[a-z])(?=[A-Z])");
            for (int i = startIdx ; i < strArr.length ; i++) {
                sepStr = sepStr.concat(strArr[i] + " ");
            }
            sepStr = sepStr.trim();
            return sepStr;
        }

        public Section(String title, int numOfSubSection) {
            this(title);
            this.subSecArr = new Section[numOfSubSection];
        }

        private void setValueArr(String[] valueArr, String selectedValue) {
            this.selectedValue = selectedValue;
            if (valueArr != null) {
                this.valueArr = new String[valueArr.length];
                for (int i = 0 ; i < valueArr.length ; i++) {
                    this.valueArr[i] = splitByUppercase(valueArr[i], 0);
                }
            }
            this.viewType = getViewTypeByValueArr(valueArr);
        }

        private String getViewTypeByValueArr(String[] valueArr) {
            String viewType = null;

            if (valueArr.length == 0) {
                Log.w("Menu Fragment", "getViewType: The value array is empty");
            }

            // Switch
            else if (valueArr.length == 2) {
                String value_1 = valueArr[0].toLowerCase();
                String value_2 = valueArr[1].toLowerCase();
                // enable/disable
                if ((value_1.equals("enable") && value_2.equals("disable")) ||
                        (value_1.equals("disable") && value_2.equals("enable"))) {

                    viewType = SWITCH;
                }
                else {
                    viewType = LIST;
                }
            }

            // If there are values and this is not switch view
            else { viewType = LIST;}

            return viewType;
        }

        /*
        Append a new section object
        */
        private void appendSubSection(Section section) {
            if (section != null) {
                for (int i = 0; i < this.subSecArr.length; i++) {
                    if (this.subSecArr[i] == null) {
                        this.subSecArr[i] = section;
                        break;
                    }
                }
            }
        }

        public String getTitle() {
            return title;
        }

        public String getSelectedValue() {
            return selectedValue;
        }

        public String getViewType() {
            return viewType;
        }

        public String[] getValueArr() {
            return valueArr;
        }

        public Section[] getSubSecArr() {
            return subSecArr;
        }
    }

    ////////////////////////////    interface  ////////////////////////////////

    /*
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }*/
}
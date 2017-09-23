package com.codepath.jennifergodinez.nytimessearch.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.jennifergodinez.nytimessearch.R;
import com.codepath.jennifergodinez.nytimessearch.models.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FilterFragment extends DialogFragment implements SelectDateFragment.OnDatePass {
    EditText etBeginDate;
    TextView tvBeginDate, tvSortOrder, tvNewsDeskValues;
    CheckBox cbArts, cbSports, cbFashion;
    Spinner spSortOrder;
    Filter filter = new Filter();

    final static String BEGIN_DATE="BEGIN_DATE";
    final static String SORT_ORDER="SORT_ORDER";
    final static String NEWSDESK_LIST="NEWSDESK_LIST";
    final static String SETDATE = "SETDATE";


    public FilterFragment() {
    }


    public static FilterFragment newInstance(Filter filter) {

        FilterFragment fragment = new FilterFragment();

        Bundle bundle = new Bundle();
        if (filter!= null) {
            bundle.putString(BEGIN_DATE, filter.getDate());
            bundle.putString(SORT_ORDER, filter.getSortOrder());
            bundle.putStringArrayList(NEWSDESK_LIST, filter.getNewsDeskList());
        } else {
            bundle.putString(BEGIN_DATE, "");
            bundle.putString(SORT_ORDER, "");
            bundle.putStringArrayList(NEWSDESK_LIST, new ArrayList<String>());
        }

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filter.setDate(getArguments().getString(BEGIN_DATE));
        filter.setSortOrder(getArguments().getString(SORT_ORDER));
        filter.setNewsDeskList(getArguments().getStringArrayList(NEWSDESK_LIST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        return inflater.inflate(R.layout.fragment_filter, container, false);
    }


    @Override
    public void onDatePass(Calendar date) {
        String str = new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
        etBeginDate.setText(str);

    }


    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();

        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        // Call super onResume after sizing
        super.onResume();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get fields from view
        tvBeginDate = view.findViewById(R.id.tvBeginDate);
        etBeginDate = view.findViewById(R.id.etDate);
        tvSortOrder = view.findViewById(R.id.tvSortOrder);
        spSortOrder = view.findViewById(R.id.spSortOrder);
        tvNewsDeskValues = view.findViewById(R.id.tvNewsDeskValues);
        cbArts = view.findViewById(R.id.cbArts);
        cbSports = view.findViewById(R.id.cbSports);
        cbFashion = view.findViewById(R.id.cbFashion);

        //prepopulate our filters
        if (!"".equals(filter.getDate())) {
            etBeginDate.setText(filter.getDate());
        }

        if (filter.getSortOrder().equals(((String) spSortOrder.getItemAtPosition(1)).toLowerCase())) {
            spSortOrder.setSelection(1);
        } else {
            spSortOrder.setSelection(0);
        }

        ArrayList<String> newsDeskList = filter.getNewsDeskList();
        if (newsDeskList.size() > 0) {
            if (newsDeskList.contains(cbArts.getText())) {
                cbArts.setChecked(true);
            }
            if (newsDeskList.contains(cbFashion.getText())) {
                cbFashion.setChecked(true);
            }
            if (newsDeskList.contains(cbSports.getText())) {
                cbSports.setChecked(true);
            }
        }

        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectDateFragment newFragment = new SelectDateFragment();

                newFragment.setTargetFragment(FilterFragment.this, 300);

                //Create a bundle to pass the date
                Bundle bundle = new Bundle();
                bundle.putString(SETDATE, filter.getDate());
                newFragment.setArguments(bundle);

                newFragment.show(getFragmentManager(), "datePicker");

            }
        });

        Button btn = view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OnFinishFilter onPassFilter = (OnFinishFilter) getActivity();

                String sortValue = spSortOrder.getSelectedItem().toString();

                ArrayList<String> newsDeskList = new ArrayList<>();
                if (cbArts.isChecked()) { newsDeskList.add(cbArts.getText().toString()); }
                if (cbFashion.isChecked()) { newsDeskList.add(cbFashion.getText().toString()); }
                if (cbSports.isChecked()) { newsDeskList.add(cbSports.getText().toString()); }

                onPassFilter.onFinishFilterDialog(new Filter(etBeginDate.getText().toString(),
                        sortValue.toLowerCase(), newsDeskList));

                // Close the dialog and return back to the parent activity
                dismiss();

            }
        });

    }


    public interface OnFinishFilter {
        void onFinishFilterDialog(Filter filter);
    }

}

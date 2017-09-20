package com.codepath.jennifergodinez.nytimessearch.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.jennifergodinez.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FilterFragment extends DialogFragment implements SelectDateFragment.OnDatePass, TextView.OnEditorActionListener {
    EditText etBeginDate;
    TextView tvBeginDate, tvSortOrder, tvNewsDeskValues;
    CheckBox cbArts, cbSports, cbFashion;
    Spinner spSortOrder;

    public FilterFragment() {
    }

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        return view;
    }

    @Override
    public void onDatePass(Calendar date) {
        String str = new SimpleDateFormat("yyyyMMdd").format(date.getTime());
        Log.d("LOG","hello " + str);
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
        tvBeginDate = (TextView) view.findViewById(R.id.tvBeginDate);
        etBeginDate = (EditText) view.findViewById(R.id.etDate);
        tvSortOrder = (TextView) view.findViewById(R.id.tvSortOrder);
        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        tvNewsDeskValues = (TextView)view.findViewById(R.id.tvNewsDeskValues);
        cbArts = (CheckBox)view.findViewById(R.id.cbArts);
        cbSports = (CheckBox)view.findViewById(R.id.cbSports);
        cbFashion = (CheckBox)view.findViewById(R.id.cbFashion);

        //etBeginDate.setOnEditorActionListener(this);

        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDateFragment newFragment = new SelectDateFragment();
                newFragment.setTargetFragment(FilterFragment.this, 300);
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });

        Button btn = (Button)view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogListener listener = (EditNameDialogListener) getActivity();
                String sortValue = spSortOrder.getSelectedItem().toString();

                ArrayList<String> newsDeskList = new ArrayList();
                if (cbArts.isChecked()) { newsDeskList.add(cbArts.getText().toString()); }
                if (cbFashion.isChecked()) { newsDeskList.add(cbFashion.getText().toString()); }
                if (cbSports.isChecked()) { newsDeskList.add(cbSports.getText().toString()); }

                listener.onFinishEditDialog(etBeginDate.getText().toString(),
                        sortValue.toLowerCase(), newsDeskList);

                // Close the dialog and return back to the parent activity
                dismiss();

            }
        });




        // Show soft keyboard automatically and request focus to field
        tvBeginDate.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            EditNameDialogListener listener = (EditNameDialogListener) getActivity();
            listener.onFinishEditDialog(etBeginDate.getText().toString(),
                    etBeginDate.getText().toString(), null);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }


    public interface EditNameDialogListener {
        void onFinishEditDialog(String date, String sortValue, ArrayList<String> newsDeskList);
    }



}

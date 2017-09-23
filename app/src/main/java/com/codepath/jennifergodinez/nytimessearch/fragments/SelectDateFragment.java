package com.codepath.jennifergodinez.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */

public class SelectDateFragment extends DialogFragment  {

    String setDate;

    public interface OnDatePass {
        void onDatePass(Calendar date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle dateBundle = this.getArguments();
        setDate = dateBundle.getString(FilterFragment.SETDATE);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();

        // if date is passed in
        if (!"".equals(setDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(setDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timeInMillis = date.getTime();
            calendar.setTimeInMillis(timeInMillis);
        }

        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // store the values selected into a Calendar instance
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, i);
                c.set(Calendar.MONTH, i1);
                c.set(Calendar.DAY_OF_MONTH, i2);


                FilterFragment parentFrag = (FilterFragment) getTargetFragment();
                parentFrag.onDatePass(c);

            }
        };
        return new DatePickerDialog(getActivity(), listener, yy, mm, dd);
    }


}

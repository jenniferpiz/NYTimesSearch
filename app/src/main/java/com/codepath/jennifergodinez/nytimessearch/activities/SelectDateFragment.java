package com.codepath.jennifergodinez.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 *
 */

public class SelectDateFragment extends DialogFragment  {
    public interface OnDatePass {
        public void onDatePass(Calendar date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
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

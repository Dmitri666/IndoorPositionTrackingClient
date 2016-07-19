package com.lps.lpsapp.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by dle on 30.11.2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    TheListener listener;

    public DatePickerFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (this.getArguments() != null && this.getArguments().containsKey("date")) {
            long time = this.getArguments().getLong("date");
            Date date = new Date();
            date.setTime(time);
            c.setTime(date);
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        listener = (TheListener) getActivity();

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new CustomDatePickerDialog(getActivity(), this, year, month, day);
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (listener != null) {
            listener.returnDate(new GregorianCalendar(year, month, day));

        }
        this.dismiss();
    }


    public interface TheListener {
        void returnDate(GregorianCalendar date);
    }
}

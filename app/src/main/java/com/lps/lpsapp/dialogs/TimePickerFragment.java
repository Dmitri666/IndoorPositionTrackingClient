package com.lps.lpsapp.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by dle on 30.11.2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TheListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (this.getArguments() != null && this.getArguments().containsKey("time")) {
            long time = this.getArguments().getLong("time");
            Date date = new Date();
            date.setTime(time);
            c.setTime(date);
        }

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        listener = (TheListener) getActivity();

        // Create a new instance of DatePickerDialog and return it
        TimePickerDialog dialog = new CustomTimePickerDialog(getActivity(), this, hour, minute, true);
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        if (listener != null) {
            listener.returnTime(new GregorianCalendar(0, 0, 0, hourOfDay, minute));

        }

    }


    public interface TheListener {
        void returnTime(GregorianCalendar date);
    }
}

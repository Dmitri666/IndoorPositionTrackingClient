package com.lps.lpsapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by dle on 30.11.2015.
 */
public class NumberPickerFragment extends DialogFragment {

    TheListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        listener = (TheListener) getActivity();

        // Create a new instance of DatePickerDialog and return it
        CustomNumberPickerDialog dialog = new CustomNumberPickerDialog(getActivity(), true, null);
        return dialog;
    }

    public interface TheListener {
        void returnNumber(int number);
    }


}

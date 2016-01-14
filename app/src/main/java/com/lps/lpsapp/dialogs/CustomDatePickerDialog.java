package com.lps.lpsapp.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

/**
 * Created by dle on 30.11.2015.
 */
public class CustomDatePickerDialog extends DatePickerDialog {
    public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.setButton(DatePickerDialog.BUTTON_POSITIVE, "ok",this);
        this.setButton(DatePickerDialog.BUTTON_NEGATIVE, "cancel",this);  // hide cancel button
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view,year,month,day);
        this.onClick(this,BUTTON_POSITIVE);
    }
}

package com.lps.lpsapp.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.lps.lpsapp.R;

/**
 * Created by dle on 01.12.2015.
 */
public class CustomNumberPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{
    private final NumberPicker mNumberPicker;

    protected CustomNumberPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.number_picker_dialog, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "ok", this);
        setButton(BUTTON_NEGATIVE, "cancel", this);
        //setButtonPanelLayoutHint(AlertDialog..LAYOUT_HINT_SIDE);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.number_picker_dialog);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}

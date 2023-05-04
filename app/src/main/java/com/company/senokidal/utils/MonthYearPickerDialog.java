package com.company.senokidal.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.company.senokidal.R;
import com.company.senokidal.Splash;

public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private DatePickerDialog.OnDateSetListener listener;

    static SharedPreferences sharedpreferences;
    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Date date_sekarang = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");

        sharedpreferences = getActivity().getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        String ta = sharedpreferences.getString("ta", simpleDateformat.format(date_sekarang) );

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Integer.parseInt(ta));

        View dialog = inflater.inflate(R.layout.dialog_monthyear_picker, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(3500);
        yearPicker.setValue(year);

        builder.setView(dialog).setPositiveButton(Html.fromHtml("<font color='#FF4081'>Ok</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
            }
        }).setNegativeButton(Html.fromHtml("<font color='#FF4081'>Cancel</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MonthYearPickerDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}

package com.achase.safespace;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by achas on 2/18/2017.
 */

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DOB = "com.achas.android.safespace.dob";

    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_date_dialog, null);

        mDatePicker = (DatePicker)v.findViewById(R.id.dialog_date_date_picker);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_of_birth)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Integer year = mDatePicker.getYear();
                                Integer month = mDatePicker.getMonth();
                                Integer day = mDatePicker.getDayOfMonth();

                                String monthString = convertMonthToString(month);

                                String date = monthString+" "+day.toString()+", "+year.toString();
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, String date){
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DOB, date);

        getTargetFragment()
            .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private String convertMonthToString(Integer month){
        String monthString;

        switch(month){
            case 0: monthString = "January";
                break;
            case 1: monthString = "February";
                break;
            case 2: monthString = "March";
                break;
            case 3: monthString = "April";
                break;
            case 4: monthString = "May";
                break;
            case 5: monthString = "June";
                break;
            case 6: monthString = "July";
                break;
            case 7: monthString = "August";
                break;
            case 8: monthString = "September";
                break;
            case 9: monthString = "October";
                break;
            case 10: monthString = "November";
                break;
            case 11: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }

        return monthString;
    }
}

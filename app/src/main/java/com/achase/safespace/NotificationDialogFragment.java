package com.achase.safespace;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.content.ContentValues.TAG;

/**
 * Created by Adam Chase on 3/26/2017.
 */

public class NotificationDialogFragment extends DialogFragment {

    public static final String EXTRA_EMERGENCY = "com.achas.android.safespace.emergency";

    private EditText mEmergencyMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_notificaiton_dialog, null);

        mEmergencyMessage = (EditText)v.findViewById(R.id.emergency_message);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("What is your emergency?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, mEmergencyMessage.getText().toString());
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, String emergency){
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_EMERGENCY, emergency);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

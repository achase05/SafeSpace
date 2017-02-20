package com.achase.safespace;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by achas on 2/8/2017.
 */

public class RegistrationDialogFragment extends DialogFragment {
    private Button standardRegButton;
    private Button medicalRegButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_reg_dialog, null);

        standardRegButton = (Button)v.findViewById(R.id.standard_user);
        standardRegButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), StandardRegistrationActivity.class);
                startActivity(intent);
            }
        });

        medicalRegButton = (Button)v.findViewById(R.id.medical_user);
        medicalRegButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), MedicalRegistrationActivity.class);
                startActivity(intent);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Register as")
                .setNegativeButton("Cancel", null)
                .create();
    }

}

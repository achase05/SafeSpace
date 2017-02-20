package com.achase.safespace;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;

/**
 * Created by achas on 2/17/2017.
 */

public class ForgotPassDialogFragment extends DialogFragment {
    private EditText forgotPassEmail;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_forgot_password_dialog, null);

        forgotPassEmail = (EditText)v.findViewById(R.id.forgot_pass_email);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Enter email")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,forgotPassEmail.getText().toString());
                        resetPassword(forgotPassEmail.getText().toString());
                    }
                })
                .create();
    }


    public void resetPassword(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getActivity(), "An email has been sent to you.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.achase.safespace;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * Created by achas on 2/8/2017.
 */

public class LoginFragment extends Fragment {
    private static final String DIALOG_REG = "DialogReg";
    private static final String DIALOG_FORGOT_PASS = "DialogForgotPass";

    private TextView mRegisterLink;
    private TextView mForgotPassLink;
    private Button loginBtn;
    private EditText emailField;
    private EditText passwordField;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    startActivity(intent);
                }
            }
        };

        mRegisterLink = (TextView)v.findViewById(R.id.register_link);
        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                RegistrationDialogFragment dialog = new RegistrationDialogFragment();
                dialog.show(manager, DIALOG_REG);
            }
        });

        mForgotPassLink = (TextView)v.findViewById(R.id.forgot_pass_link);
        mForgotPassLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                ForgotPassDialogFragment dialog = new ForgotPassDialogFragment();
                dialog.show(manager, DIALOG_FORGOT_PASS);
            }
        });

        emailField = (EditText) v.findViewById(R.id.login_email);
        passwordField = (EditText) v.findViewById(R.id.login_password);

        loginBtn = (Button) v.findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startSignIn();
            }
        });

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    public void startSignIn(){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getActivity(), "Some required fields were empty.", Toast.LENGTH_LONG);
        }else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), "There was a problem with the sign in!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}

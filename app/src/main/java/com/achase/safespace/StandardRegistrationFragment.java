package com.achase.safespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

/**
 * Created by achase on 2/10/2017.
 */

public class StandardRegistrationFragment extends Fragment {

    private static final String DIALOG_BIRTHDAY = "DialogBirthday";

    private static final int REQUEST_DOB = 0;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button DOBbtn;
    private Button mRegisterBtn;

    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPassword;
    private String userConfirmPassword;
    private String userDOB;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_standard_reg, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    addUserInfo(userFirstName, userLastName, userDOB);
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    startActivity(intent);
                }else{
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mFirstName = (EditText)v.findViewById(R.id.reg_standFirstName);
        mLastName = (EditText)v.findViewById(R.id.reg_standLastName);
        mEmail = (EditText)v.findViewById(R.id.reg_standEmail);
        mPassword = (EditText)v.findViewById(R.id.reg_standPass);
        mConfirmPassword = (EditText)v.findViewById(R.id.reg_standConfirmPass);

        DOBbtn = (Button)v.findViewById(R.id.stand_reg_DOB);
        DOBbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(StandardRegistrationFragment.this, REQUEST_DOB);
                dialog.show(manager, DIALOG_BIRTHDAY);
            }
        });

        mRegisterBtn = (Button)v.findViewById(R.id.reg_standRegisterBtn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passwordOkay;

                userFirstName = mFirstName.getText().toString();
                userLastName = mLastName.getText().toString();
                userEmail = mEmail.getText().toString();
                userPassword = mPassword.getText().toString();
                userConfirmPassword = mConfirmPassword.getText().toString();
                userDOB = DOBbtn.getText().toString();

                passwordOkay = confirmPassword(userPassword, userConfirmPassword);
                if(passwordOkay == true){
                    createUser(userEmail, userPassword);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DOB){
            String date = data.getStringExtra(DatePickerFragment.EXTRA_DOB);
            DOBbtn.setText(date);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete" + task.isSuccessful());

                        //If sign in fails, display a message to the user. If sign in succeeds
                        //the auth state listener will be notified and logic to handle the
                        //signed in user can be handled in the listener
                        if(!task.isSuccessful()){
                            Toast.makeText(getActivity(), "Failed to create user.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public boolean confirmPassword(String password, String confirmPassword){
        boolean isValid;

        if(!password.equals(confirmPassword)){
            Log.d(TAG, "Password: "+password+". Confirmed: "+confirmPassword);
            Toast.makeText(getActivity(), "There was an error when confirming your password.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }else{
            isValid = true;
        }

        return isValid;
    }

    public void addUserInfo(String firstName, String lastName, String birthDate){
        User user = new User(firstName, lastName, birthDate, "Standard user", null);
        String userID = mAuth.getCurrentUser().getUid().toString();

        mDatabase.child("users").child(userID).setValue(user);
    }
}

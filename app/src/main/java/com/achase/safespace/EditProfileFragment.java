package com.achase.safespace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by achas on 3/7/2017.
 */

public class EditProfileFragment extends Fragment {

    private EditText mNewFirstName;
    private EditText mNewLastName;
    private Button mNewBirthdayBtn;

    private User mUser;

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstancState){
        super.onCreate(savedInstancState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_profile, container, false);

        //setCurrentValues();

        //Accept first name and last name
        //Store into User object

        //Create date fragment
        //Store new birthday into User object

        //Type condition and hit + to add a new condition to linear layout
        //Add each string to String array in User object
        //Store new strings in User object

        //On button update button, set values in firebase and return to profile

        return v;
    }

    public View setCurrentValues(View w){

    }
}

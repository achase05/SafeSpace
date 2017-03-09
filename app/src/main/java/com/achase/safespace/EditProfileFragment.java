package com.achase.safespace;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achas on 3/7/2017.
 */

public class EditProfileFragment extends Fragment {
    private static final String DIALOG_NEW_BIRTHDAY = "DialogNewBirthday";

    private static final int REQUEST_NEW_DOB = 0;

    private EditText mNewFirstName;
    private EditText mNewLastName;
    private Button mNewBirthdayBtn;
    private EditText mNewConditionField;
    private ImageButton mNewConditionBtn;
    private ViewGroup mConditionsList;

    private User mUser;
    private List<String> conditions;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstancState){
        super.onCreate(savedInstancState);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.confirm_update:
                updateInfo(mUser, conditions);
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_profile, container, false);

        conditions = new ArrayList<String>();

        mNewFirstName = (EditText)v.findViewById(R.id.new_first_name);
        mNewLastName = (EditText)v.findViewById(R.id.new_last_name);
        mNewBirthdayBtn = (Button)v.findViewById(R.id.update_birthday_btn);
        mNewConditionField = (EditText)v.findViewById(R.id.new_condition_field);
        mNewConditionBtn = (ImageButton)v.findViewById(R.id.add_condition_btn);
        mConditionsList = (ViewGroup)v.findViewById(R.id.new_conditions_list);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                displayCurrentInfo(mUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mNewBirthdayBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(EditProfileFragment.this, REQUEST_NEW_DOB);
                dialog.show(manager, DIALOG_NEW_BIRTHDAY);
            }
        });

        mNewConditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCondition(mNewConditionField.getText().toString(), mConditionsList);
            }
        });
        //Type condition and hit + to add a new condition to linear layout
        //Add each string to String array in User object
        //Store new strings in User object

        //On button update button, set values in firebase and return to profile

        return v;
    }

    public View setCurrentValues(View w){

        return w;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_NEW_DOB){
            String date = data.getStringExtra(DatePickerFragment.EXTRA_DOB);
            mNewBirthdayBtn.setText(date);
        }
    }

    public void displayCurrentInfo(User user){
        System.out.println(user.getFirstName());
        mNewFirstName.setText(user.getFirstName());
        mNewLastName.setText(user.getLastName());
        mNewBirthdayBtn.setText(user.getBirthDate());
    }

    public void updateInfo(User user, List conditions){
        user.setFirstName(mNewFirstName.getText().toString());
        user.setLastName(mNewLastName.getText().toString());
        user.setBirthDate(mNewBirthdayBtn.getText().toString());

        String userID = mAuth.getCurrentUser().getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userID).setValue(user);
        mDatabase.child("conditions").child(userID).setValue(conditions);

    }

    public void addCondition(String condition, ViewGroup v){
        conditions.add(condition);

        TextView newCondition = new TextView(getActivity());
        newCondition.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newCondition.setTextColor(Color.WHITE);
        newCondition.setText(condition);
        v.addView(newCondition);
    }
}

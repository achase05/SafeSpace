package com.achase.safespace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by achas on 2/12/2017.
 */

public class UserProfileFragment extends Fragment {

    private Button logoutBtn;
    private TextView mUsername;
    private TextView mUserBirthday;
    private TextView mUserType;

    private FirebaseUser user;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        mUsername = (TextView)v.findViewById(R.id.user_name);
        mUserBirthday = (TextView)v.findViewById(R.id.user_birthday);
        mUserType = (TextView)v.findViewById(R.id.user_type);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUsername.setText(user.lastName + ", " + user.firstName);
                mUserBirthday.setText(user.birthDate);
                mUserType.setText(user.userType);

                System.out.println(user.getFirstName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        logoutBtn = (Button)v.findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }
}

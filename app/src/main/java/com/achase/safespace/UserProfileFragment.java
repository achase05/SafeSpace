package com.achase.safespace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

/**
 * Created by achas on 2/12/2017.
 */

public class UserProfileFragment extends Fragment {
    private static final int REQUEST_PHOTO = 1;

    private Button logoutBtn;
    private TextView mUsername;
    private TextView mUserBirthday;
    private TextView mUserType;
    private ImageView mUserPhoto;
    private ImageButton mUserCamera;
    private User mCurrentUser = new User();
    private Context mContext;
    private File mPhotoFile;

    private FirebaseUser user;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mPhotoFile = getPhotoFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        PackageManager packageManager = getActivity().getPackageManager();

        mUsername = (TextView)v.findViewById(R.id.user_name);
        mUserBirthday = (TextView)v.findViewById(R.id.user_birthday);
        mUserType = (TextView)v.findViewById(R.id.user_type);
        mUserPhoto = (ImageView)v.findViewById(R.id.user_photo);
        mUserCamera = (ImageButton)v.findViewById(R.id.user_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mUserCamera.setEnabled(canTakePhoto);

        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mUserCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                mUsername.setText(mCurrentUser.lastName + ", " + mCurrentUser.firstName);
                mUserBirthday.setText(mCurrentUser.birthDate);
                mUserType.setText(mCurrentUser.userType);

                System.out.println(mCurrentUser.getFirstName());
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

        updatePhotoView();

        return v;
    }

    public File getPhotoFile(){
        File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        if(externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, mCurrentUser.getPhotoFilename(user.getUid()));
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mUserPhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mUserPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }
}

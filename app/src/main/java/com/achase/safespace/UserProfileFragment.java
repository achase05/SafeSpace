package com.achase.safespace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private TextView mSkillsCerts;
    private ImageView mUserPhoto;
    private ImageButton mUserCamera;
    private User mCurrentUser = new User();
    private Context mContext;
    private File mPhotoFile;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private StorageReference mPhotosRef;
    private StorageReference mUserPhotoRef;
    private StorageReference mPhotosUserPhotoRef;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mPhotosRef = mStorageRef.child("photos");
        mUserPhotoRef = mStorageRef.child(mCurrentUser.getPhotoFilename(user.getUid()));
        mPhotosUserPhotoRef = mStorageRef.child("photos/" + mCurrentUser.getPhotoFilename(user.getUid()));

        mPhotoFile = getPhotoFile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_logout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        PackageManager packageManager = getActivity().getPackageManager();

        mUsername = (TextView)v.findViewById(R.id.user_name);
        mUserBirthday = (TextView)v.findViewById(R.id.user_birthday);
        mUserType = (TextView)v.findViewById(R.id.user_type);
        mUserPhoto = (ImageView)v.findViewById(R.id.user_photo);
        mSkillsCerts = (TextView)v.findViewById(R.id.skills_certifications);

        //Taking photo and setting it as user profile picture
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

        //Changing values in user profile by setting the text based on the Firebase information
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                mUsername.setText(mCurrentUser.lastName + ", " + mCurrentUser.firstName);
                mUserBirthday.setText(mCurrentUser.birthDate);
                mUserType.setText(mCurrentUser.userType);

                if(mCurrentUser.userType.equals("Medical user")){
                    mSkillsCerts.setText(mCurrentUser.userSkill);
                }

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


    //Methods for taking and setting user profile picture


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
            uploadPhoto(mUserPhoto);
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

    public void uploadPhoto(ImageView imageView){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
       // Bitmap bitmap = imageView.getDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mUserPhotoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload image to database.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
}

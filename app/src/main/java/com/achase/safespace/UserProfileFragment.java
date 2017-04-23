package com.achase.safespace;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by achas on 2/12/2017.
 */

public class UserProfileFragment extends Fragment {
    private static final String DIALOG_EMERGENCY = "DialogEmergency";

    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_EMERGENCY = 2;

    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
    List<String> conditions;

    private Button mEmergencyBtn;
    private TextView mUsername;
    private TextView mUserBirthday;
    private TextView mUserType;
    private TextView mSkillsCerts;
    private ImageView mUserPhoto;
    private ImageButton mUserCamera;
    private User mCurrentUser = new User();
    private File mPhotoFile;
    private ViewGroup mConditionsListView;
    private int notifyID = 0;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserInfoRef;
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

                Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(logoutIntent);
                return true;
            case R.id.menu_item_edit:
                Intent editIntent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(editIntent);
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
        mConditionsListView = (LinearLayout)v.findViewById(R.id.conditions_list);
        mEmergencyBtn = (Button)v.findViewById(R.id.emergency_button);

        mEmergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                NotificationDialogFragment dialog = new NotificationDialogFragment();
                dialog.setTargetFragment(UserProfileFragment.this, REQUEST_EMERGENCY);
                dialog.show(manager, DIALOG_EMERGENCY);
            }
        });

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


        /************* SENDS NOTIFICATION AFTER DATA CHANGED IN FIREBASE **************/

        /*mDatabase.child("receivers").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publishNotification();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //Changing values in user profile by setting the text based on the Firebase information
        //mUserInfoRef.child("users").child(user.getUid());
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
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



        populateConditionsList(mConditionsListView);

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

        if(requestCode == REQUEST_EMERGENCY){
            String emergency = data.getStringExtra(NotificationDialogFragment.EXTRA_EMERGENCY);
            sendNotificaiton(emergency);
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
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public void populateConditionsList(final ViewGroup v){
        //String[] conditions = {"Test1", "Test2"};


        mDatabase = FirebaseDatabase.getInstance().getReference().child("conditions").child(user.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conditions = dataSnapshot.getValue(t);

                if(conditions!=null) {
                    for (int i = 0; i < conditions.size(); i++) {
                        TextView condition = new TextView(getActivity());
                        condition.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        condition.setText(conditions.get(i));
                        v.addView(condition);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendNotificaiton(String emergency){
       //final List<String> receivers = new ArrayList<String>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Notification").child("message");
        mDatabase.setValue(emergency);

        /*mDatabase = FirebaseDatabase.getInstance().getReference().child("receivers");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    //receivers.add(child.getKey().toString());
                    if (!child.getKey().toString().equals(user.getUid().toString())){
                        System.out.println(child.getKey() + " | " + user.getUid());
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("receivers").child(child.getKey().toString());
                        mDatabase.setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    /*public void publishNotification(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("notification");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String emergency = dataSnapshot.getValue().toString();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getActivity())
                                .setSmallIcon(R.drawable.ic_stat_name)
                                .setContentTitle("Emergency In Your Area!")
                                .setContentText(emergency);
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(getActivity(), EditProfileActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(EditProfileActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);

                NotificationManager mNotificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(notifyID, mBuilder.build());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    public void logout(){
        FirebaseAuth.getInstance().signOut();

        Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(logoutIntent);
    }
}

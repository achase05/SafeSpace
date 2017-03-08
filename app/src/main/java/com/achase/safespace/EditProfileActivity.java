package com.achase.safespace;


import android.support.v4.app.Fragment;

/**
 * Created by achas on 3/7/2017.
 */

public class EditProfileActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new EditProfileFragment();
    }
}

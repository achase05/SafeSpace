package com.achase.safespace;

import android.support.v4.app.Fragment;

/**
 * Created by achas on 2/12/2017.
 */

public class UserProfileActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new UserProfileFragment();
    }
}

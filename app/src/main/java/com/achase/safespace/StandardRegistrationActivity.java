package com.achase.safespace;

import android.support.v4.app.Fragment;

/**
 * Created by achas on 2/10/2017.
 */

public class StandardRegistrationActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new StandardRegistrationFragment();
    }
}

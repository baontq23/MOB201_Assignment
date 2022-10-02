package com.baontq.mob201.ui.profile;

import android.net.Uri;
import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> mFullName, mEmail;
    private MutableLiveData<Uri> mAvatarUrl;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private FirebaseUser user;

    public ProfileViewModel() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFullName = new MutableLiveData<>();
        userMutableLiveData = new MutableLiveData<>();
        userMutableLiveData.setValue(user);
        mAvatarUrl = new MutableLiveData<>();
        mEmail = new MutableLiveData<>();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                mAvatarUrl.setValue(user.getPhotoUrl());
            }
            if (user.getDisplayName() != null) {
                mFullName.setValue(user.getDisplayName());
            } else {
                mFullName.setValue("No name");
            }
            mEmail.setValue(user.getEmail());
        } else {
            mEmail.setValue("");
            mFullName.setValue("Guest");
        }

    }

    public MutableLiveData<String> getEmail() {
        return mEmail;
    }

    public MutableLiveData<String> getFullName() {
        return mFullName;
    }

    public MutableLiveData<Uri> getAvatarUrl() {
        return mAvatarUrl;
    }

}
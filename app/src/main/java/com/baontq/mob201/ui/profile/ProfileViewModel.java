package com.baontq.mob201.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
 private MutableLiveData<String> mText, mFullName;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mFullName = new MutableLiveData<>();
    }

    public MutableLiveData<String> getText() {
        return mText;
    }

    public void setFullName(String fullName) {
        mFullName.setValue(fullName);
    }

    public MutableLiveData<String> getFullName() {
        return mFullName;
    }
}
package com.baontq.mob201.until;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baontq.mob201.MainActivity;
import com.baontq.mob201.ui.auth.LoginActivity;
import com.baontq.mob201.ui.permission.RequestPermissionView;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkSelfPermission()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }

        }else {
            startActivity(new Intent(SplashScreen.this, RequestPermissionView.class));
        }
        finish();
    }

    public boolean checkSelfPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}

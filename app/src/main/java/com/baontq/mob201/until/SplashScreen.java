package com.baontq.mob201.until;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baontq.mob201.MainActivity;
import com.baontq.mob201.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }
        finish();
    }
}

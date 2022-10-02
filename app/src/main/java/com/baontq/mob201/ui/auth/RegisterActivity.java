package com.baontq.mob201.ui.auth;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.TextView;

import com.baontq.mob201.R;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtAuthRePass, edtAuthEmail, edtAuthPassword;
    private MaterialButton btnAuthRegister, btnAuthGoogleLogin;
    private TextView tvAuthLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();
        tvAuthLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        btnAuthGoogleLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void findView() {
        edtAuthRePass = findViewById(R.id.edt_auth_repass);
        edtAuthEmail = findViewById(R.id.edt_auth_email);
        edtAuthPassword = findViewById(R.id.edt_auth_password);
        btnAuthRegister = findViewById(R.id.btn_auth_register);
        btnAuthGoogleLogin = findViewById(R.id.btn_auth_google_login);
        tvAuthLogin = findViewById(R.id.tv_auth_login);
    }
}
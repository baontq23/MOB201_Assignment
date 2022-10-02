package com.baontq.mob201.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baontq.mob201.MainActivity;
import com.baontq.mob201.R;
import com.baontq.mob201.service.AuthService;
import com.baontq.mob201.until.ProgressBarDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kongzue.dialogx.dialogs.PopTip;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtAuthRePass, edtAuthEmail, edtAuthPassword;
    private MaterialButton btnAuthRegister, btnAuthGoogleLogin;
    private TextView tvAuthLogin;
    private FirebaseAuth auth;
    private ResponseReceiver receiver = new ResponseReceiver();
    private ProgressBarDialog progressBarDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBarDialog = new ProgressBarDialog(this);
        auth = FirebaseAuth.getInstance();
        findView();
        tvAuthLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        btnAuthGoogleLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        btnAuthRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String email = edtAuthEmail.getText().toString().trim();
        String password = edtAuthPassword.getText().toString().trim();
        String rePassword = edtAuthRePass.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            PopTip.show(getString(R.string.register_empty_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            PopTip.show(getString(R.string.register_empty_password));
            return;
        }
        if (TextUtils.isEmpty(rePassword)) {
            PopTip.show(getString(R.string.register_empty_re_password));
            return;
        }
        if (!password.equalsIgnoreCase(rePassword)) {
            PopTip.show(getString(R.string.register_repass_not_vaild));
            return;
        }
        progressBarDialog.setMessage("Đang đăng ký, vui lòng chờ").show();
        Intent intent = new Intent(this, AuthService.class);
        intent.setAction(AuthService.ACTION_REGISTER_WITH_EMAIL);
        intent.putExtra("user_email", email);
        intent.putExtra("user_password", password);
        startService(intent);
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(AuthService.ACTION_REGISTER_WITH_EMAIL)) {
                progressBarDialog.dismiss();
                int resultCode = intent.getIntExtra(AuthService.PARAM_REGISTER_STATUS, AuthService.RESULT_LOGIN_FAILED);
                if (resultCode == AuthService.RESULT_LOGIN_SUCCESS) {
                    Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    PopTip.show("Đăng ký thất bại! Thông tin không hợp lệ hoặc đã tồn tại.");
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(AuthService.ACTION_REGISTER_WITH_EMAIL));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
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
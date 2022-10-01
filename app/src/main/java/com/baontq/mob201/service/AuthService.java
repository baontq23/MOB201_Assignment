package com.baontq.mob201.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baontq.mob201.ui.auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthService extends IntentService {
    private static final String TAG = "AUTH_SERVICE";
    public static final String PARAM_LOGIN_STATUS = "auth_login";
    public static final String ACTION_LOGIN_WITH_EMAIL = "com.baontq.mob201.service.action.LOGIN_WITH_EMAIL";
    public static final String ACTION_LOGOUT = "com.baontq.mob201.service.action.LOGOUT";
    public static final int RESULT_LOGIN_FAILED = 401;
    public static final int RESULT_LOGIN_SUCCESS = 200;

    public AuthService() {
        super("AuthService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent broadcastIntent = new Intent();
            switch (intent.getAction()) {
                case ACTION_LOGIN_WITH_EMAIL:
                    broadcastIntent.setAction(ACTION_LOGIN_WITH_EMAIL);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String email = intent.getStringExtra("user_email");
                    String password = intent.getStringExtra("user_password");
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        broadcastIntent.putExtra(PARAM_LOGIN_STATUS, RESULT_LOGIN_SUCCESS);
                                        Toast.makeText(AuthService.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                        sendBroadcast(broadcastIntent);
                                    } else {
                                        broadcastIntent.putExtra(PARAM_LOGIN_STATUS, RESULT_LOGIN_FAILED);
                                        Log.i(TAG, "Information not valid");
                                        sendBroadcast(broadcastIntent);
                                    }
                                }
                            });
                    break;
                case ACTION_LOGOUT:
                    FirebaseAuth.getInstance().signOut();
                    broadcastIntent.setAction(AuthService.ACTION_LOGOUT);
                    sendBroadcast(broadcastIntent);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
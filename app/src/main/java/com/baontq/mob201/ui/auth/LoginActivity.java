package com.baontq.mob201.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baontq.mob201.MainActivity;
import com.baontq.mob201.R;
import com.baontq.mob201.databinding.ActivityLoginBinding;
import com.baontq.mob201.service.AuthService;
import com.baontq.mob201.until.ProgressBarDialog;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kongzue.dialogx.dialogs.MessageDialog;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "GOOGLE_SIGN_IN";
    private ActivityLoginBinding binding;
    public static final int RC_GOOGLE_SIGN_IN = 100;
    private TextView tvLabelEmail, tvLabelPassword, tvAuthRegister;
    private EditText edtAuthEmail, edtAuthPassword;
    private CheckBox chkAuthRemember;
    private MaterialButton btnAuthLogin, btnAuthGuestLogin, btnAuthGoogleLogin;
    private FirebaseAuth mAuth;
    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;
    private Intent authServiceIntent;
    private ResponseReceiver receiver = new ResponseReceiver();
    private ProgressBarDialog progressBarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBarDialog = new ProgressBarDialog(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        oneTapClient = Identity.getSignInClient(this);
        mAuth = FirebaseAuth.getInstance();
        findView();
        tvAuthRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        btnAuthGuestLogin.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
        btnAuthLogin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtAuthEmail.getText().toString().trim())) {
                Toast.makeText(this, R.string.login_empty_email, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(edtAuthPassword.getText().toString().trim())) {
                Toast.makeText(this, R.string.login_empty_password, Toast.LENGTH_SHORT).show();
                return;
            }
            progressBarDialog.setMessage("Đang xác thực");
            progressBarDialog.show();
            authServiceIntent = new Intent(this, AuthService.class);
            authServiceIntent.setAction(AuthService.ACTION_LOGIN_WITH_EMAIL);
            authServiceIntent.putExtra("user_email", edtAuthEmail.getText().toString().trim());
            authServiceIntent.putExtra("user_password", edtAuthPassword.getText().toString().trim());
            startService(authServiceIntent);
        });
        btnAuthGoogleLogin.setOnClickListener(v -> {
            handleLoginWithGoogle();
        });
        

    }

    private void findView() {
        tvLabelEmail = findViewById(R.id.tv_label_email);
        edtAuthEmail = findViewById(R.id.edt_auth_email);
        tvLabelPassword = findViewById(R.id.tv_label_password);
        edtAuthPassword = findViewById(R.id.edt_auth_password);
        chkAuthRemember = findViewById(R.id.chk_auth_remember);
        btnAuthLogin = findViewById(R.id.btn_auth_login);
        btnAuthGoogleLogin = findViewById(R.id.btn_auth_google_login);
        btnAuthGuestLogin = findViewById(R.id.btn_auth_guest_login);
        tvAuthRegister = findViewById(R.id.tv_auth_register);
    }

    private void handleLoginWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(result.getPendingIntent().getIntentSender(), RC_GOOGLE_SIGN_IN, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: " + e.getMessage());
                Toast.makeText(this, "Đã huỷ đăng nhập!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AuthService.ACTION_LOGIN_WITH_EMAIL:
                    progressBarDialog.dismiss();
                    int value = intent.getIntExtra(AuthService.PARAM_LOGIN_STATUS, 401);
                    if (value == AuthService.RESULT_LOGIN_SUCCESS) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Thông tin không chính xác!", Toast.LENGTH_SHORT).show();
                        edtAuthPassword.setText("");
                    }
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(AuthService.ACTION_LOGIN_WITH_EMAIL));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
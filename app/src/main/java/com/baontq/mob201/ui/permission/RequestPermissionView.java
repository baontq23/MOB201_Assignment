package com.baontq.mob201.ui.permission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baontq.mob201.BuildConfig;
import com.baontq.mob201.R;
import com.baontq.mob201.ui.auth.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kongzue.dialogx.dialogs.MessageDialog;

public class RequestPermissionView extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefresh;
    private TextView btnGrant;
    private static final int CODE_PERMISSIONS_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_grant_permission);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this::requestPermission);
        btnGrant = findViewById(R.id.btn_allow);
        btnGrant.setOnClickListener(v -> requestPermission());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSIONS_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(RequestPermissionView.this, LoginActivity.class));
                    finish();
                } else {
                    MessageDialog.show(getString(R.string.notification), "Ứng dụng chưa được cấp quyền!");
                }
            } else {
                MessageDialog.show(getString(R.string.notification), "Ứng dụng chưa được cấp quyền, vui lòng cấp!", "Cài đặt", getString(R.string.close)).setOkButton((dialog, v) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                    return false;
                });
            }
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, CODE_PERMISSIONS_WRITE_STORAGE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        CODE_PERMISSIONS_WRITE_STORAGE);
            }
        }
        mSwipeRefresh.setRefreshing(false);
    }
}

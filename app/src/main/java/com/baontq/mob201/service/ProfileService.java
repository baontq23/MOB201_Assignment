package com.baontq.mob201.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baontq.mob201.api.ImgurApi;
import com.baontq.mob201.network.ImgurService;
import com.baontq.mob201.until.ProgressBarDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileService extends IntentService {
    public static final String ACTION_PROFILE_CHANGE_AVATAR = "com.baontq.mob201.service.action.PROFILE_CHANGE_AVATAR";
    public static final String PARAM_IMAGE_FILE = "profile_avatar_path";
    public static final String PARAM_PROFILE_CHANGE_AVATAR_RESULT = "profile_avatar_result";
    public static final int RESULT_PROFILE_UPDATE_SUCCESS = 200;
    public static final int RESULT_PROFILE_UPDATE_FAILED = 400;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Intent broadcastIntent;

    public ProfileService() {
        super("ProfileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        broadcastIntent = new Intent();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROFILE_CHANGE_AVATAR.equals(action)) {
                broadcastIntent.setAction(ACTION_PROFILE_CHANGE_AVATAR);
                final String imagePath = intent.getStringExtra(PARAM_IMAGE_FILE);
                handleChangeAvatar(imagePath);
            }
        }
    }


    private void handleChangeAvatar(String imagePath) {
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        ImgurService service = ImgurApi.getInstance().create(ImgurService.class);
        Call<JsonObject> resultCall = service.uploadImage(body);
        resultCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    if (response.code() == 200) {
                        broadcastIntent.putExtra("progress", 1);
                        sendBroadcast(broadcastIntent);
                        JsonObject object = response.body().getAsJsonObject("data");
                        String imageLink = String.valueOf(object.get("link"));
                        imageLink = imageLink.substring(1, imageLink.length() -1);
                        Toast.makeText(ProfileService.this, "Link: " + imageLink, Toast.LENGTH_SHORT).show();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(imageLink))
                                .build();
                        user.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        broadcastIntent.putExtra("progress", 2);
                                        if (task.isSuccessful()) {
                                            broadcastIntent.putExtra(PARAM_PROFILE_CHANGE_AVATAR_RESULT, RESULT_PROFILE_UPDATE_SUCCESS);
                                            sendBroadcast(broadcastIntent);
                                        } else {
                                            broadcastIntent.putExtra(PARAM_PROFILE_CHANGE_AVATAR_RESULT, RESULT_PROFILE_UPDATE_FAILED);
                                            Toast.makeText(ProfileService.this, "Firebase error!", Toast.LENGTH_SHORT).show();
                                            sendBroadcast(broadcastIntent);
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(ProfileService.this, "Upload ảnh thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                broadcastIntent.putExtra(PARAM_PROFILE_CHANGE_AVATAR_RESULT, RESULT_PROFILE_UPDATE_FAILED);
                Toast.makeText(ProfileService.this, "Error request upload file!", Toast.LENGTH_SHORT).show();
                sendBroadcast(broadcastIntent);
            }
        });

    }

}
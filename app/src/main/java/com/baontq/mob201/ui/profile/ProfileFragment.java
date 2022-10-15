package com.baontq.mob201.ui.profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baontq.mob201.MainActivity;
import com.baontq.mob201.R;
import com.baontq.mob201.api.ImgurApi;
import com.baontq.mob201.databinding.FragmentProfileBinding;
import com.baontq.mob201.network.ImgurService;
import com.baontq.mob201.service.AuthService;
import com.baontq.mob201.service.ProfileService;
import com.baontq.mob201.ui.auth.LoginActivity;
import com.baontq.mob201.until.FileUntil;
import com.baontq.mob201.until.ProgressBarDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.JsonObject;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnIconChangeCallBack;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialogx.util.InputInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFrag";
    private FirebaseUser user;
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ResponseReceiver receiver = new ResponseReceiver();
    private static final int RESULT_IMAGE_CAPTURE = 0;
    private static final int RESULT_IMAGE_MEDIA = 1;
    private String photoPath;
    ProgressBarDialog progressBarDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBarDialog = new ProgressBarDialog(requireContext());
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.btnProfileLogin.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });
        profileViewModel.getAvatarUrl().observe(getViewLifecycleOwner(), uri -> {
            Glide.with(this).load(uri).placeholder(R.drawable.default_avatar).circleCrop().into(binding.ivAvatar);
        });
        profileViewModel.getFullName().observe(getViewLifecycleOwner(), binding.tvProfileFullName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), binding.tvProfileEmail::setText);
        binding.ivAvatar.setOnClickListener(v -> changeProfileAvatar());
        binding.btnProfileLogout.setOnClickListener(v -> handleLogout());
        binding.btnProfileEdit.setOnClickListener(v -> handleEditProfile());
        binding.tvProfileEditName.setOnClickListener(v -> editFullName());
        binding.tvProfileEditPassword.setOnClickListener(v -> changePassword());
        setInformation();
        return binding.getRoot();
    }

    private void editFullName() {
        new InputDialog("Cập nhật thông tin", "Thay đổi họ tên", "Cập nhật", "Huỷ bỏ")
                .setCancelable(false)
                .setInputText(user.getDisplayName())
                .setAutoShowInputKeyboard(false)
                .setOkButton(new OnInputDialogButtonClickListener<InputDialog>() {
                    @Override
                    public boolean onClick(InputDialog dialog, View v, String inputStr) {
                        if (TextUtils.isEmpty(inputStr)) {
                            PopTip.show("Chưa nhập họ tên");
                            return true;
                        }
                        progressBarDialog.setMessage("Loading").show();
                        ProfileService.updateFullName(requireActivity(), inputStr);
                        return false;
                    }
                }).show();
    }

    private void changePassword() {
        MessageDialog.show("Cập nhật thông tin", "Thay đổi mật khẩu", "Cập nhật", "Huỷ bỏ")
                .setCustomView(new OnBindView<MessageDialog>(R.layout.view_change_password) {
                    @Override
                    public void onBind(MessageDialog dialog, View v) {
                    }
                }).setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog dialog, View v) {
                        EditText edtPassword = dialog.getDialogView().findViewById(R.id.edt_password);
                        EditText edtRePassword = dialog.getDialogView().findViewById(R.id.edt_re_password);
                        String password = edtPassword.getText().toString().trim();
                        String rePassword = edtRePassword.getText().toString().trim();
                        if (TextUtils.isEmpty(password)) {
                            PopTip.show("Chưa nhập mật khẩu mới!");
                            return true;
                        }
                        if (password.length() < 8) {
                            PopTip.show("Mật khẩu phải dài hơn 8 ký tự!");
                            return true;
                        }
                        if (TextUtils.isEmpty(rePassword)) {
                            PopTip.show("Chưa nhập lại mật khẩu mới!");
                            return true;
                        }
                        if (!rePassword.equals(password)) {
                            PopTip.show("Mật khẩu không đồng nhất!");
                            return true;
                        }
                        progressBarDialog.setMessage("Re-Authentication").show();
                        ProfileService.updatePassword(requireActivity(), password);
                        return false;
                    }
                });
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    progressBarDialog.setMessage("Đang tải ảnh lên máy chủ!").show();
                    Uri photoUri = result.getData().getData();
//                    File file = new File(FileUntil.getPath(requireContext(), photoUri));
//                    Glide.with(this).load(file).circleCrop().placeholder(R.drawable.default_avatar).into(binding.ivAvatar);
                    Intent intent = new Intent(getContext(), ProfileService.class);
                    intent.setAction(ProfileService.ACTION_PROFILE_CHANGE_AVATAR);
                    intent.putExtra(ProfileService.PARAM_IMAGE_FILE, FileUntil.getPath(requireContext(), photoUri));
                    requireActivity().startService(intent);
                }
            }
    );

    private File createImageFile() throws IOException {
        String imageFileName = "temporary_file";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }


    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    progressBarDialog.setMessage("Đang tải ảnh lên máy chủ!").show();
                    Intent intent = new Intent(getContext(), ProfileService.class);
                    intent.setAction(ProfileService.ACTION_PROFILE_CHANGE_AVATAR);
                    intent.putExtra(ProfileService.PARAM_IMAGE_FILE, photoPath);
                    requireActivity().startService(intent);
                }
            }
    );


    private void changeProfileAvatar() {
        if (user != null) {
            BottomMenu.show(new String[]{"Chụp ảnh", "Thư viện"})
                    .setOnIconChangeCallBack(new OnIconChangeCallBack<BottomMenu>(true) {
                        @Override
                        public int getIcon(BottomMenu dialog, int index, String menuText) {
                            if (index == 0) {
                                return R.drawable.ic_outline_photo_camera_24;
                            }
                            if (index == 1) {
                                return R.drawable.ic_outline_image_24;
                            }
                            return 0;
                        }
                    })
                    .setMessage("Chọn ảnh")
                    .setOnMenuItemClickListener((dialog, text, index) -> {
                        if (index == 0) {
                            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA
                                    }, RESULT_IMAGE_CAPTURE);

                                } else {
                                    ActivityCompat.requestPermissions(requireActivity(),
                                            new String[]{
                                                    Manifest.permission.CAMERA
                                            }, 1);
                                }
                            } else {
                                Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File photo = null;
                                try {
                                    photo = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (photo != null) {
                                    Uri photoUri = FileProvider.getUriForFile(requireActivity(), "com.baontq.mob201.file_provider", photo);
                                    takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                    cameraLauncher.launch(takePic);
                                }
                            }


                        } else {
                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            launcher.launch(gallery);
                        }
                        return false;
                    });
        } else {
            MessageDialog.show(getString(R.string.notification), getString(R.string.require_login), getString(R.string.close));
        }
    }

    private void handleLogout() {
        Intent intent = new Intent(getContext(), AuthService.class);
        intent.setAction(AuthService.ACTION_LOGOUT);
        requireActivity().startService(intent);
    }

    private void setInformation() {
        if (user == null) {
            binding.btnProfileLogin.setVisibility(View.VISIBLE);
            binding.btnProfileEdit.setVisibility(View.GONE);
            binding.btnProfileLogout.setVisibility(View.GONE);
        }
    }

    private void handleEditProfile() {
        if (binding.llInformation.getVisibility() == View.INVISIBLE) {
            binding.llInformation.setVisibility(View.VISIBLE);
            binding.btnProfileEdit.setText(R.string.done);
            binding.btnProfileEdit.setIconResource(R.drawable.ic_baseline_done_24);
        } else {
            binding.llInformation.setVisibility(View.INVISIBLE);
            binding.btnProfileEdit.setText(R.string.profile_fragment_edit_info);
            binding.btnProfileEdit.setIconResource(R.drawable.ic_baseline_edit_24);
        }

    }

    class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AuthService.ACTION_LOGOUT:
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    requireActivity().finish();
                    break;
                case ProfileService.ACTION_PROFILE_CHANGE_AVATAR:
                    int progress = intent.getIntExtra("progress", 0);
                    if (progress == 1) {
                        progressBarDialog.setMessage("Firebase update data");
                    } else if (progress == 2) {
                        progressBarDialog.dismiss();
                        if (intent.getIntExtra(ProfileService.PARAM_PROFILE_CHANGE_AVATAR_RESULT, ProfileService.RESULT_PROFILE_UPDATE_FAILED) == ProfileService.RESULT_PROFILE_UPDATE_SUCCESS) {
                            PopTip.show("Cập nhật thành công!");
                            profileViewModel.getAvatarUrl().setValue(user.getPhotoUrl());
                        } else {
                            PopTip.show("Cập nhật thất bại!");
                        }
                    }
                    break;
                case ProfileService.ACTION_PROFILE_CHANGE_FULL_NAME:
                    progressBarDialog.dismiss();
                    if (intent.getIntExtra(ProfileService.PARAM_PROFILE_CHANGE_FULL_NAME_RESULT, ProfileService.RESULT_PROFILE_UPDATE_FAILED) == ProfileService.RESULT_PROFILE_UPDATE_SUCCESS) {
                        PopTip.show("Cập nhật thành công!");
                        binding.tvProfileFullName.setText(user.getDisplayName());
                    } else {
                        PopTip.show("Cập nhật thất bại!");
                    }
                    break;
                case ProfileService.ACTION_PROFILE_CHANGE_PASSWORD:
                    progressBarDialog.dismiss();
                    if (intent.getIntExtra(ProfileService.PARAM_PROFILE_CHANGE_PASSWORD_RESULT, ProfileService.RESULT_PROFILE_UPDATE_FAILED) == ProfileService.RESULT_PROFILE_UPDATE_SUCCESS) {
                        PopTip.show("Thay đổi mật khẩu thành công!");
                    } else {
                        PopTip.show("Cập nhật thất bại!");
                    }
                    break;
                default:
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AuthService.ACTION_LOGOUT);
        intentFilter.addAction(ProfileService.ACTION_PROFILE_CHANGE_AVATAR);
        intentFilter.addAction(ProfileService.ACTION_PROFILE_CHANGE_FULL_NAME);
        intentFilter.addAction(ProfileService.ACTION_PROFILE_CHANGE_PASSWORD);
        requireActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
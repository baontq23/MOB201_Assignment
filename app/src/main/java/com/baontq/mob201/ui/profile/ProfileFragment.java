package com.baontq.mob201.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baontq.mob201.MainActivity;
import com.baontq.mob201.R;
import com.baontq.mob201.databinding.FragmentProfileBinding;
import com.baontq.mob201.service.AuthService;
import com.baontq.mob201.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ResponseReceiver receiver = new ResponseReceiver();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.btnProfileLogin.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });
        binding.btnProfileLogout.setOnClickListener(v -> handleLogout());
        binding.btnProfileEdit.setOnClickListener(v -> handleEditProfile());
        profileViewModel.getFullName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.tvProfileFullName.setText(s);
            }
        });
        setInformation();
        return binding.getRoot();
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
            binding.tvProfileEmail.setText("");
            binding.tvProfileFullName.setText("Guest");
        } else {
            binding.tvProfileEmail.setText(user.getEmail());
            binding.tvProfileFullName.setText("Bao Nguyen");
        }
    }

    private void handleEditProfile() {
        if (binding.llInformation.getVisibility() == View.INVISIBLE) {
            binding.llInformation.setVisibility(View.VISIBLE);
            binding.btnProfileEdit.setText("Xong");
            binding.btnProfileEdit.setIconResource(R.drawable.ic_baseline_done_24);
        } else {
            binding.llInformation.setVisibility(View.INVISIBLE);
            binding.btnProfileEdit.setText("Sửa thông tin");
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
package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.User;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.FragmentProfileBinding;
import com.btl_ptit.hotelbooking.view.activity.EditProfileActivity;
import com.btl_ptit.hotelbooking.view.activity.LoginActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindUser();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindUser(); // Refresh data when returning from EditProfileActivity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupListeners() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Are You Sure?")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    SessionManager.getInstance().clear();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void bindUser() {
        if (binding == null) return;
        User user = SessionManager.getInstance().getUser();
        if (user == null) return;

        binding.tvFullName.setText(user.getFullName());
        binding.tvUsername.setText("@" + (user.getFullName() != null ? user.getFullName().toLowerCase().replace(" ", "") : "user"));

        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imgAvatar);
        } else {
            binding.imgAvatar.setImageResource(R.mipmap.ic_launcher);
        }
    }
}

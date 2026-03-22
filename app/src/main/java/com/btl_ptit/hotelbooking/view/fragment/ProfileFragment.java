package com.btl_ptit.hotelbooking.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.User;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.FragmentProfileBinding;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        bindUser();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void bindUser() {
        if (binding == null) return;
        Context context = getContext();
        if (context == null) return;

        User user = new SessionManager().getUser();
        if (user == null) {
            binding.tvFullName.setText("Guest");
            binding.imgAvatar.setImageResource(R.mipmap.ic_launcher);
            return;
        }

        String fullName = user.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = "User";
        }
        binding.tvFullName.setText(fullName);

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
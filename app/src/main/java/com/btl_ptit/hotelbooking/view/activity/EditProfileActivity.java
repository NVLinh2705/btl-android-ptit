package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.btl_ptit.hotelbooking.data.model.User;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityEditProfileBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SupabaseRestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restService = SupabaseClient.createService(SupabaseRestService.class);

        initViews();
        setupListeners();
    }

    private void initViews() {
        User user = SessionManager.getInstance().getUser();
        if (user != null) {
            binding.etFullName.setText(user.getFullName());
            binding.etEmail.setText(user.getEmail());
            binding.etPhone.setText(user.getPhone());
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            updateProfile();
        });
    }

    private void updateProfile() {
        String fullName = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        User currentUser = SessionManager.getInstance().getUser();
        if (currentUser == null) return;

        User updatedUser = new User();
        updatedUser.setFullName(fullName);
        updatedUser.setPhone(phone);

        String token = "Bearer " + SessionManager.getInstance().getAccessToken();
        String idEq = "eq." + currentUser.getId();

        restService.updateUser(token, idEq, updatedUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Update local session
                    currentUser.setFullName(fullName);
                    currentUser.setPhone(phone);
                    SessionManager.getInstance().saveSession(
                            SessionManager.getInstance().getAccessToken(),
                            SessionManager.getInstance().getRefreshToken(),
                            currentUser
                    );
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.btl_ptit.hotelbooking.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseAuthService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityChangePasswordBinding;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String token = SessionManager.getInstance().getAccessToken();

        if (token == null) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText("Đang xử lý...");

        SupabaseAuthService authService = SupabaseClient.createService(SupabaseAuthService.class);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("password", newPassword);

        authService.updateUser("Bearer " + token, updates).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                binding.btnSubmit.setEnabled(true);
                binding.btnSubmit.setText("Tiếp tục");
                
                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                binding.btnSubmit.setEnabled(true);
                binding.btnSubmit.setText("Tiếp tục");
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        String current = binding.etCurrentPassword.getText().toString().trim();
        String newPass = binding.etNewPassword.getText().toString().trim();
        String confirm = binding.etConfirmPassword.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPass.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showSuccessDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_success, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        MaterialButton btnContinue = view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}

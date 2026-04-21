package com.btl_ptit.hotelbooking.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.btl_ptit.hotelbooking.data.dto.LoginRequest;
import com.btl_ptit.hotelbooking.data.dto.LoginResponse;
import com.btl_ptit.hotelbooking.data.dto.RegisterRequest;
import com.btl_ptit.hotelbooking.data.dto.RegisterResponse;
import com.btl_ptit.hotelbooking.data.model.User;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseAuthService;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SupabaseAuthService authService;
    private SupabaseRestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authService = SupabaseClient.createService(SupabaseAuthService.class);
        restService = SupabaseClient.createService(SupabaseRestService.class);

        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        String fullName = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // 1. Validation
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            binding.tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Mật khẩu không khớp");
            return;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        // 2. Auth SignUp
        RegisterRequest signUpRequest = new RegisterRequest(fullName,phone, email, password);
        authService.signUp("hotelbooking://auth-callback",signUpRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse authData = response.body();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("message", "Đăng ký thành công! Hãy xác thực email của bạn để đăng nhập");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Auth error: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi hệ thống: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
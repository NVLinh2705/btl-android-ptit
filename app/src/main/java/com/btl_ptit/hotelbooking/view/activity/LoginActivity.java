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
import com.btl_ptit.hotelbooking.data.model.User;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseAuthService;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.SupabaseRestService;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityLoginBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SupabaseRestService restService;
    private SupabaseAuthService authService;
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authService= SupabaseClient.createService(SupabaseAuthService.class);
        restService = SupabaseClient.createService(SupabaseRestService.class);


        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvForgotPassword.setOnClickListener(v ->
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        );

        binding.tvRegisterPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnGoogleLogin.setOnClickListener(v ->
            Toast.makeText(this, "Google Login clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void performLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Email is required");
            isValid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Password is required");
            isValid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        if (!isValid) return;



        LoginRequest request = new LoginRequest(email, password);

        authService.signIn("password", request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(LoginActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(LoginActivity.this, "Login Failed: Check credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponse body = response.body();

                // Fetch profile from public.users using JWT access token + auth user id
                String bearer = "Bearer " + body.getAccessToken();
                String userId = body.getUser() != null ? body.getUser().getId() : null;
                if (userId == null || userId.trim().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Login ok, missing user id", Toast.LENGTH_SHORT).show();
                    // still save tokens only
                    sessionManager.saveSession(body.getAccessToken(), body.getRefreshToken(), null);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    return;
                }

                String idEq = "eq." + userId;
                String select = "email,phone,full_name,avatar_url,created_at,is_active";

                restService.getUserById(bearer, idEq, select)
                    .enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> resp) {
                            User user = null;
                            if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                                user = resp.body().get(0);
                            }

                            sessionManager.saveSession(
                                body.getAccessToken(),
                                body.getRefreshToken(),
                                user
                            );

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            // Still allow entering app even if profile fetch fails
                            sessionManager.saveSession(body.getAccessToken(), body.getRefreshToken(), null);
                            Toast.makeText(LoginActivity.this, "Login ok, profile fetch failed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
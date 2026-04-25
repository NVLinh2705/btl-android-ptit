package com.btl_ptit.hotelbooking.view.activity;

import android.content.Intent;
import android.net.Uri;
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
        if (sessionManager.getAccessToken() != null && (getIntent() == null || getIntent().getData() == null)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
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

        // XỬ LÝ DEEP LINK (Xác thực email)
        // Nếu là deep link, nó sẽ tự chuyển màn và finish inside
        handleDeepLink();

        // SETUP UI & LISTENERS (Chỉ làm nếu không chuyển màn)
        setupListeners();

        // HIỂN THỊ THÔNG BÁO TỪ MÀN ĐĂNG KÝ
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            getIntent().removeExtra("message");
        }
    }

    private void handleDeepLink() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            // Supabase trả về token sau dấu # (Fragment)
            String fragment = uri.getFragment();

            if (fragment != null && fragment.contains("access_token=")) {
                String accessToken = null;
                String refreshToken = null;

                // Tách chuỗi fragment thành các cặp key=value
                String[] pairs = fragment.split("&");
                for (String pair : pairs) {
                    String[] parts = pair.split("=");
                    if (parts.length > 1) {
                        if (parts[0].equals("access_token")) accessToken = parts[1];
                        else if (parts[0].equals("refresh_token")) refreshToken = parts[1];
                    }
                }

                if (accessToken != null) {
                    // Xóa data để tránh xử lý lại khi xoay màn hình
                    intent.setData(null);
                    // Gọi hàm lấy profile và chuyển màn hình
                    fetchProfileAndNavigate(accessToken, refreshToken);
                }
            }
        }
    }

    private void fetchProfileAndNavigate(String at, String rt) {
        String bearer = "Bearer " + at;

        Toast.makeText(this, "Xác thực thành công! Đang đăng nhập...", Toast.LENGTH_SHORT).show();

        // Thực hiện lưu session tạm thời và vào Main (User profile sẽ được cập nhật sau)
        sessionManager.saveSession(at, rt, null);
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
                            user.setId(userId);
                            System.out.println("id= "+user.getId()+" fullname "+user.getFullName());

                            sessionManager.saveSession(
                                body.getAccessToken(),
                                body.getRefreshToken(),
                                user
                            );

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
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink();
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            getIntent().removeExtra("message");
        }
    }
}
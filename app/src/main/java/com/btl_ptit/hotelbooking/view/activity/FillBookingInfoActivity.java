package com.btl_ptit.hotelbooking.view.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.model.User;
import com.btl_ptit.hotelbooking.data.session.RoomSelectionStore;
import com.btl_ptit.hotelbooking.data.session.SessionManager;
import com.btl_ptit.hotelbooking.databinding.ActivityFillBookingInfoBinding;
import com.btl_ptit.hotelbooking.utils.CurrencyUtils;
import com.btl_ptit.hotelbooking.view.adapter.BookingSelectedRoomAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class FillBookingInfoActivity extends AppCompatActivity {

    public static final String EXTRA_HOTEL_ID = "hotel_id";
    public static final String EXTRA_CHECKIN_DATE = "checkin_date";
    public static final String EXTRA_CHECKOUT_DATE = "checkout_date";
    public static final String EXTRA_ADULTS = "adults";
    public static final String EXTRA_GUEST_FULL_NAME = "guest_full_name";
    public static final String EXTRA_GUEST_EMAIL = "guest_email";
    public static final String EXTRA_GUEST_PHONE = "guest_phone";

    private ActivityFillBookingInfoBinding b;
    private final SessionManager sessionManager = SessionManager.getInstance();

    private static final Pattern FULL_NAME_PATTERN = Pattern.compile("^[\\p{L}]+(?:[\\p{L}\\s'\\-]*[\\p{L}])?$");

    private boolean fullNameValid;
    private boolean emailValid;
    private boolean phoneValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFillBookingInfoBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setupToolbar();
        styleRequiredLabels();
        setupSelectedRooms();
        prefillFromSession();
        setupValidation();
        updateTotalPrice();

        b.btnNextStep.setOnClickListener(v -> onNextStepClicked());
    }

    private void setupToolbar() {
        setSupportActionBar(b.toolbar);
        b.toolbar.setNavigationOnClickListener(v -> finish());
        getWindow().setStatusBarColor(getColor(R.color.toolbar_blue));
    }

    private void setupSelectedRooms() {
        b.rvSelectedRooms.setLayoutManager(new LinearLayoutManager(this));
        BookingSelectedRoomAdapter adapter = new BookingSelectedRoomAdapter();
        b.rvSelectedRooms.setAdapter(adapter);
        adapter.submitList(RoomSelectionStore.getSelectionItems());

        b.layoutSelectedRooms.setVisibility(RoomSelectionStore.hasSelection() ? View.VISIBLE : View.GONE);
    }

    private void prefillFromSession() {
        User user = sessionManager.getUser();
        if (user == null) {
            return;
        }
        if (!TextUtils.isEmpty(user.getFullName())) {
            b.etFullName.setText(user.getFullName());
        }
        if (!TextUtils.isEmpty(user.getEmail())) {
            b.etEmail.setText(user.getEmail());
        }
        if (!TextUtils.isEmpty(user.getPhone())) {
            b.etPhone.setText(user.getPhone());
        }
    }

    private void setupValidation() {
        b.etFullName.addTextChangedListener(simpleWatcher(this::validateFullName));
        b.etEmail.addTextChangedListener(simpleWatcher(this::validateEmail));
        b.etPhone.addTextChangedListener(simpleWatcher(this::validatePhone));

        validateFullName();
        validateEmail();
        validatePhone();
    }

    private TextWatcher simpleWatcher(Runnable validator) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validator.run();
            }
        };
    }

    private void validateFullName() {
        String value = safeText(b.etFullName.getText());
        fullNameValid = value.length() >= 2 && FULL_NAME_PATTERN.matcher(value).matches();
        updateInputState(b.tilFullName, fullNameValid, R.string.error_enter_full_name);
        updateCtaState();
    }

    private void validateEmail() {
        String value = safeText(b.etEmail.getText());
        emailValid = Patterns.EMAIL_ADDRESS.matcher(value).matches();
        updateInputState(b.tilEmail, emailValid, R.string.error_enter_email);
        updateCtaState();
    }

    private void validatePhone() {
        String value = safeText(b.etPhone.getText());
        phoneValid = value.matches("^(0|\\+84)\\d{9,10}$");
        updateInputState(b.tilPhone, phoneValid, R.string.error_enter_phone);
        updateCtaState();
    }

    private void updateInputState(TextInputLayout layout, boolean valid, @StringRes int errorMessageRes) {
        layout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        if (valid) {
            layout.setError(null);
            layout.setErrorEnabled(false);
            layout.setEndIconDrawable(R.drawable.ic_check);
            layout.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_700)));
        } else {
            layout.setError(getString(errorMessageRes));
            layout.setEndIconDrawable(R.drawable.ic_error);
            layout.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_700)));
        }
    }

    private boolean isFormValid() {
        return fullNameValid && emailValid && phoneValid;
    }

    private void updateTotalPrice() {
        b.tvTotalPrice.setText(CurrencyUtils.formatVnd(RoomSelectionStore.getTotalPrice()));
    }

    private void styleRequiredLabels() {
        applyRequiredLabelStyle(b.tvFullNameLabel);
        applyRequiredLabelStyle(b.tvEmailLabel);
        applyRequiredLabelStyle(b.tvPhoneLabel);
    }

    private void applyRequiredLabelStyle(TextView label) {
        CharSequence text = label.getText();
        if (text == null) {
            return;
        }
        String value = text.toString();
        int starIndex = value.lastIndexOf('*');
        if (starIndex < 0) {
            return;
        }

        SpannableString ss = new SpannableString(value);
        ss.setSpan(
                new ForegroundColorSpan(getColor(R.color.red_700)),
                starIndex,
                starIndex + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        label.setText(ss);
    }

    private void updateCtaState() {
        boolean formValid = isFormValid();
        b.btnNextStep.setText(formValid ? R.string.next_step : R.string.fill_missing_info);
    }

    private void onNextStepClicked() {
        if (!isFormValid()) {
            Toast.makeText(this, R.string.fill_missing_info, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ConfirmBookingActivity.class);
        intent.putExtra(EXTRA_HOTEL_ID, getIntent().getIntExtra(EXTRA_HOTEL_ID, -1));
        intent.putExtra(EXTRA_CHECKIN_DATE, getIntent().getStringExtra(EXTRA_CHECKIN_DATE));
        intent.putExtra(EXTRA_CHECKOUT_DATE, getIntent().getStringExtra(EXTRA_CHECKOUT_DATE));
        intent.putExtra(EXTRA_ADULTS, getIntent().getIntExtra(EXTRA_ADULTS, 2));
        intent.putExtra(EXTRA_GUEST_FULL_NAME, safeText(b.etFullName.getText()));
        intent.putExtra(EXTRA_GUEST_EMAIL, safeText(b.etEmail.getText()));
        intent.putExtra(EXTRA_GUEST_PHONE, safeText(b.etPhone.getText()));
        startActivity(intent);
    }

    private String safeText(Editable editable) {
        return editable == null ? "" : editable.toString().trim();
    }
}


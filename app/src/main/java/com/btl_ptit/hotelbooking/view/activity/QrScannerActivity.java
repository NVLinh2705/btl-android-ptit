package com.btl_ptit.hotelbooking.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.btl_ptit.hotelbooking.R;
import com.btl_ptit.hotelbooking.data.remote.SupabaseClient;
import com.btl_ptit.hotelbooking.data.remote.api_services.BookingRestService;
import com.btl_ptit.hotelbooking.databinding.ActivityQrScannerBinding;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QrScannerActivity extends AppCompatActivity {



    private ActivityQrScannerBinding binding;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BookingRestService bookingRestService ;
    private static final String TAG = "QrScannerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingRestService = SupabaseClient.createService(BookingRestService.class);

        binding.barcodeScanner.initializeFromIntent(getIntent());
        binding.barcodeScanner.setStatusText("");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            startScanning();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void startScanning() {
        binding.barcodeScanner.decodeContinuous(result -> {
            String qrText = result.getText();

            if (qrText != null) {
                binding.barcodeScanner.pause();
                handleQrResult(qrText);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.barcodeScanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.barcodeScanner.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startScanning();
            binding.barcodeScanner.resume();
        }
    }

    private void handleQrResult(String qrText) {
        String bookingId = qrText;

        Map<String, Object> body = new HashMap<>();
        body.put("status_code", "CHECKED_IN");

        compositeDisposable.add(
                bookingRestService.changeStatusBooking("eq."+bookingId,body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response.isSuccessful()) {
                                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                                alertDialog.setTitle("Thông báo");
                                alertDialog.setMessage("Đã checkin thành công");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
                                    finish();
                                });
                                alertDialog.show();
                            }
                        }, throwable -> {
                            Log.e(TAG, "Cancel error: " + throwable.getMessage());
                        })
        );
    }
}
package com.example.aichatbot.verifyOTP;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aichatbot.MainActivity;
import com.example.aichatbot.R;

public class OTPVerificationDialog extends Dialog {
    private EditText otpET1, otpET2, otpET3, otpET4;
    private TextView resendBtn;
    private Button verifyOTPBTn;

    private final int resendTime = 60;
    private boolean resendEnabled = false, verifyEnabled = false;

    private int selectedETPosition = 0;

    private final String receiverEmail;
    private String sentOTP;
    private OnMyDialogResult mDialogResult;

    public OTPVerificationDialog(@NonNull Context context, String receiverEmail, String sentOTP) {
        super(context);
        this.receiverEmail = receiverEmail;
        this.sentOTP = sentOTP;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.otp_dialog_layout);
        setUIcontainers();
        startCountDownTimer();
        setupClickListeners();
    }

    private void setUIcontainers() {
        TextView emailView = findViewById(R.id.textview_email);

        otpET1 = findViewById(R.id.otpET1);
        otpET2 = findViewById(R.id.otpET2);
        otpET3 = findViewById(R.id.otpET3);
        otpET4 = findViewById(R.id.otpET4);

        resendBtn = findViewById(R.id.button_resend);
        verifyOTPBTn = findViewById(R.id.button_verifyOTP);

        otpET1.addTextChangedListener(textWatcher);
        otpET2.addTextChangedListener(textWatcher);
        otpET3.addTextChangedListener(textWatcher);
        otpET4.addTextChangedListener(textWatcher);

        showKeyboard(otpET1);
        emailView.setText(receiverEmail);
    }

    private void showKeyboard(EditText otpET) {
        otpET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer() {
        new CountDownTimer(resendTime * 1000, 1000) {

            @Override
            public void onTick(long l) {
                resendBtn.setText("Resend Code (" + (l/1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText("Resend Code");
                resendBtn.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));

            }
        }.start();
    }

    private void setupClickListeners() {
        resendBtn.setOnClickListener(view -> {
            if (resendEnabled) {
                startCountDownTimer();
                sentOTP = MainActivity.Companion.generateOTP();
                MainActivity.Companion.sendOTP(sentOTP, receiverEmail);
            }
        });

        verifyOTPBTn.setOnClickListener(view -> {
            if (verifyEnabled) {
                final String getOTP = otpET1.getText().toString() + otpET2.getText().toString() + otpET3.getText().toString() + otpET4.getText().toString();
                if (getOTP.length() == 4) {
                    // handle verification process here
                    boolean result = false;
                    if(sentOTP.matches(getOTP)) {
                        result = true;
                        Toast.makeText(getContext(), "OTP verified successfully!", Toast.LENGTH_LONG).show();
                    } else Toast.makeText(getContext(), "Incorrect OTP", Toast.LENGTH_LONG).show();

                    if( mDialogResult != null ) mDialogResult.finish(result);
                    OTPVerificationDialog.this.dismiss();

                }
            }
        });
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0){

                if (selectedETPosition == 0) {
                    selectedETPosition = 1;
                    showKeyboard(otpET2);
                }

                else if (selectedETPosition == 1) {
                    selectedETPosition = 2;
                    showKeyboard(otpET3);
                }

                else if (selectedETPosition == 2) {
                    selectedETPosition = 3;
                    showKeyboard(otpET4);
                }

                else{
                    verifyEnabled = true;
                    verifyOTPBTn.setBackgroundColor(R.drawable.round_back_red_100);
                }
            }

        }
    };

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {

            if (selectedETPosition == 3) {
                selectedETPosition = 2;
                showKeyboard(otpET3);
            }

            else if (selectedETPosition == 2) {
                selectedETPosition = 1;
                showKeyboard(otpET2);
            }

            else if (selectedETPosition == 1) {
                selectedETPosition = 0;
                showKeyboard(otpET1);
            }

            verifyOTPBTn.setBackgroundColor(R.drawable.round_back_brown_100);
            return true;
        }

        else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public interface OnMyDialogResult{
        void finish(boolean result);
    }
}

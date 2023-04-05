package com.example.travellink.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.example.travellink.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;


public class SignUpFragment extends Fragment {
    LinearLayout otpLayout;
    ConstraintLayout register;
    OtpTextView otpTextView;
    TextInputEditText firstName, lastName, email, phoneNumb, pass, confirmPass;
    Button Signup, Verify;
    String first_name, last_name, userEmail, userPass, userConfirmPass, userPhone, userID, otp, otpId;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    TextView phone, back, resent;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up, container, false);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        progressBar = root.findViewById(R.id.progress);
        firstName = root.findViewById(R.id.registerFirstName);
        lastName = root.findViewById(R.id.registerLastName);
        email = root.findViewById(R.id.registerEmail);
        phoneNumb = root.findViewById(R.id.registerPhonenumb);
        pass = root.findViewById(R.id.registerPassword);
        confirmPass = root.findViewById(R.id.ConfirmPassword);
        otpLayout = root.findViewById(R.id.otpLayout);
        phone = root.findViewById(R.id.phone);
        otpTextView = root.findViewById(R.id.OTPView);
        register = root.findViewById(R.id.registerLayout);
        Verify = root.findViewById(R.id.buttonVerify);
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = otpTextView.getOTP().toString();
                if (otp.isEmpty()) {
                    Toast.makeText(getActivity(), "Please verify the OTP", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    verifyPhoneNumberWithCode(otpId, otp);
                }
            }
        });
        Signup = root.findViewById(R.id.buttonSignup);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpUser();
            }
        });
        back = root.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpLayout.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);
            }
        });
        resent = root.findViewById(R.id.resentOTP);
        resent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpUser();
            }
        });
        return root;
    }

    private Boolean validate_first_name() {
        String val = firstName.getText().toString();

        if (val.isEmpty()) {
            firstName.setError("Field cannot be empty");
            return false;
        } else {
            firstName.setError(null);
            return true;
        }
    }

    private Boolean validate_last_name() {
        String val = lastName.getText().toString();

        if (val.isEmpty()) {
            firstName.setError("Field cannot be empty");
            return false;
        } else {
            firstName.setError(null);
            return true;
        }
    }

    private Boolean validate_email() {
        String val = email.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            email.setError("Invalid email address");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private Boolean validate_phoneNum() {
        String val = phoneNumb.getText().toString();

        if (val.isEmpty()) {
            phoneNumb.setError("Field cannot be empty");
            return false;
        } else {
            phoneNumb.setError(null);
            return true;
        }
    }

    private Boolean validate_password() {
        String val = pass.getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if (val.isEmpty()) {
            pass.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            pass.setError("Password is too weak, at least 6 characters,no white spaces");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    private Boolean validate_confirm_password() {
        String val = confirmPass.getText().toString();
        if (val.isEmpty()) {
            confirmPass.setError("Field cannot be empty");
            return false;
        } else {
            confirmPass.setError(null);
            return true;
        }
    }

    private boolean compare_pass() {
        if (userPass.compareTo(userConfirmPass) != 0) {
            Toast.makeText(getActivity(), "Password does not match !!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void SignUpUser() {
        if (!validate_first_name() || !validate_last_name() || !validate_email() || !validate_phoneNum() || !validate_password() || !validate_confirm_password()) {
            return;
        }
        first_name = firstName.getText().toString();
        last_name = lastName.getText().toString();
        userEmail = email.getText().toString();
        userPhone = phoneNumb.getText().toString();
        userPass = pass.getText().toString();
        userConfirmPass = confirmPass.getText().toString();

        if (compare_pass()) {
            OTPVerify(userPhone);
        }
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        if(code.equals(otp)&&verificationId.equals(otpId)){

                    myAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                userID = myAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fire_store.collection("user").document(userID);
                                UserData userData = new UserData(userID, first_name, last_name, userEmail, userPhone, userPass);
                                documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Register success", Toast.LENGTH_SHORT).show();
                                        userID = myAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fire_store.collection("user").document(userID);
                                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                startActivity(new Intent(getContext(), MainActivity.class));
                                                getActivity().finish();
                                            }
                                        });
                                    }
                                });
                            }else {
                                Toast.makeText(getActivity(), "Fail to register, your entered email might have been registered", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);;
                                task.getException().getMessage();
                            }


                        }
                    });


        }
        else
            Toast.makeText(getContext(),"Please provide correct OTP",Toast.LENGTH_SHORT).show();
    }


    private void OTPVerify(String numb) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(myAuth)
                .setPhoneNumber(numb)
                .setTimeout(100L, TimeUnit.SECONDS)
                .setActivity(getActivity())
                .setCallbacks(mCallBack)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    protected PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otpTextView.setOTP(code);
                }
            }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otpLayout.setVisibility(View.VISIBLE);
            register.setVisibility(View.GONE);
            otpId = s;
            phone.setText(userPhone);
        }
    };

}
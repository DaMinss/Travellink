package com.example.travellink.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    TextInputLayout email, password;
    TextView createNewAccount, textView2, forgotPass;
    Button Login, Google, Personal, personal;
    float v = 0;
    private FirebaseAuth myAuth;
    ProgressDialog progress;
    String userEmail, userPass;
    TextInputEditText Email, Password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);
        createNewAccount = root.findViewById(R.id.createNewAccount);
        email = root.findViewById(R.id.name);
        password = root.findViewById(R.id.arrive);
        textView2 = root.findViewById(R.id.textView2);
        forgotPass = root.findViewById(R.id.forgot_password);
        Login = root.findViewById(R.id.buttonLogin);
        Google = root.findViewById(R.id.googleAuth);
        Personal = root.findViewById(R.id.offline);

        email.setTranslationX(800);
        password.setTranslationX(800);
        forgotPass.setTranslationX(800);
        textView2.setTranslationX(800);
        createNewAccount.setTranslationX(800);
        Login.setTranslationX(800);
        Google.setTranslationX(800);
        Personal.setTranslationX(800);

        email.setAlpha(v);
        password.setAlpha(v);
        forgotPass.setAlpha(v);
        textView2.setAlpha(v);
        createNewAccount.setAlpha(v);
        Login.setAlpha(v);
        Google.setAlpha(v);
        Personal.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        textView2.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        createNewAccount.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        Login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        Google.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        Personal.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        forgotPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();

        personal = root.findViewById(R.id.offline);
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), com.example.travellink.Auth.Personal.class));
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
        Email = root.findViewById(R.id.Name);
        Password = root.findViewById(R.id.pass);
        progress = new ProgressDialog(getContext());
        progress.setCancelable(false);
        progress.setMessage("Login in...");
        myAuth = FirebaseAuth.getInstance();
        forgotPass = root.findViewById(R.id.forgot_password);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View my_view) {
//                startActivity(new Intent(getContext(), ForgotPassword.class));
//                finish();
            }
        });

        Button Login = root.findViewById(R.id.buttonLogin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        return root;
    }
    private void LoginUser(){
        userEmail = Email.getText().toString();
        userPass = Password.getText().toString();
        if(userEmail.isEmpty() || userPass.isEmpty()){
            Toast.makeText(getContext(), "Please enter your user name and password", Toast.LENGTH_SHORT).show();
        }else{
            progress.show();
            myAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        progress.dismiss();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }else {
                        Toast.makeText(getActivity(), "Fail to login, check your credentials", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
            });
        }

    }
}
package com.example.travellink.Auth;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.travellink.Admin.AdminWelcome;
import com.example.travellink.R;
import com.example.travellink.database.CloudRepo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    TextInputLayout email, password;
    TextView createNewAccount, textView2, forgotPass;
    Button Login, Google, Personal, personal;
    float v = 0;
    ProgressDialog progress;
    String userEmail, userPass;
    TextInputEditText Email, Password;
    private FirebaseAuth myAuth;
    private GoogleSignInClient googleSignInClient;
    ProgressBar progressBar;

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
        progressBar = root.findViewById(R.id.progress1);
        Login = root.findViewById(R.id.buttonLogin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

// Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        Google = root.findViewById(R.id.googleAuth);
        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
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
                RecoverPass recoverPass = new RecoverPass();
                recoverPass.show(getActivity().getSupportFragmentManager(), null);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Get the result of the Google Sign In activity
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Sign in to Firebase with the Google account
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        myAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.dismiss();
                            CloudRepo cloudRepo = new CloudRepo(getContext());
                            cloudRepo.ImportData(myAuth.getCurrentUser().getUid());
                            startActivity(new Intent(getContext(), SplashScreen2.class));
                            getActivity().finish();

                        } else {
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void LoginUser() {
        userEmail = Email.getText().toString();
        userPass = Password.getText().toString();
        if (userEmail.isEmpty() || userPass.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your user name and password", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            myAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("user").document(myAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String isAdmin = documentSnapshot.getString("admin");
                                            Log.d(TAG, "DocumentSnapshot exists.");
                                            if (documentSnapshot.contains("isAdmin")) {
                                                isAdmin = documentSnapshot.getString("admin");
                                                Log.d(TAG, "isAdmin value: " + isAdmin);
                                            }

                                            if (isAdmin != null && isAdmin.equals("Yes")) {
                                                startActivity(new Intent(getContext(), AdminWelcome.class));
                                                getActivity().finish();
                                            } else {
                                                CloudRepo cloudRepo = new CloudRepo(getContext());
                                                cloudRepo.ImportData(myAuth.getCurrentUser().getUid());
                                                startActivity(new Intent(getContext(), SplashScreen2.class));
                                                getActivity().finish();

                                            }
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Fail to login, check your credentials or your connectivity", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }
}
package net.bugfixers.e_commerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.constants.AppConstants;
import net.bugfixers.e_commerce.database.SharedPref;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private TextInputLayout layoutUsername;
    private TextInputLayout layoutPassword;
    private EditText editUsername;
    private EditText editPassword;
    private Group groupLogin;
    private SpinKitView spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spin = findViewById(R.id.spin_kit);
        layoutUsername = findViewById(R.id.layout_username);
        layoutPassword = findViewById(R.id.layout_password);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        TextView textRegister = findViewById(R.id.text_register);
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> login());
        textRegister.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
        groupLogin = findViewById(R.id.group_login);

        new Handler().postDelayed(() -> {
            String log = SharedPref.getInstance(this).getData(AppConstants.LOG);
            if (log != null) {
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            } else {
                groupLogin.setVisibility(View.VISIBLE);
            }
        }, 2500);
    }

    private void login() {
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        if (username.isEmpty()) {
            layoutUsername.setError("Username cannot be empty");
        } else if (password.isEmpty()) {
            layoutPassword.setError("Password cannot be empty");
        } else {
            groupLogin.setVisibility(View.GONE);
            spin.setVisibility(View.VISIBLE);
            getUser(username, password);
        }
    }

    private void getUser(String username, String password) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            String name = document.getString(AppConstants.NAME);
                            String phone = document.getString(AppConstants.PHONE);
                            String userPassword = document.getString(AppConstants.PASSWORD);
                            Log.d(TAG,"password: " + userPassword);
                            spin.setVisibility(View.GONE);
                            if (password.equals(userPassword)) {
                                SharedPref sharedPref = SharedPref.getInstance(this);
                                sharedPref.saveData(AppConstants.LOG, AppConstants.LOG);
                                sharedPref.saveData(AppConstants.NAME, name);
                                sharedPref.saveData(AppConstants.PHONE, phone);
                                startActivity(new Intent(this, MainActivity.class));
                                finishAffinity();
                            } else {
                                groupLogin.setVisibility(View.VISIBLE);
                            }
                        } else {
                            showSnack("No user found, please register");
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(this, RegistrationActivity.class));
                                finish();
                            }, 1500);
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        showSnack("Login failed");
                    }
                });
    }

    private void showSnack(String s) {
        Snackbar.make(findViewById(R.id.login_layout), s, Snackbar.LENGTH_LONG).show();
    }
}
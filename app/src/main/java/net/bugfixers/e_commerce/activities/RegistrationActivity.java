package net.bugfixers.e_commerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.constants.AppConstants;
import net.bugfixers.e_commerce.database.SharedPref;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private TextInputLayout layoutName;
    private TextInputLayout layoutPhone;
    private TextInputLayout layoutUsername;
    private TextInputLayout layoutPassword;
    private EditText editName;
    private EditText editPhone;
    private EditText editUsername;
    private EditText editPassword;
    private Group groupRegister;
    private SpinKitView spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        layoutName = findViewById(R.id.layout_name);
        layoutPhone = findViewById(R.id.layout_phone);
        layoutUsername = findViewById(R.id.layout_username);
        layoutPassword = findViewById(R.id.layout_password);
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);

        Button buttonRegister = findViewById(R.id.button_register);
        groupRegister = findViewById(R.id.group_register);
        spin = findViewById(R.id.spin_kit);
        buttonRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = editName.getText().toString();
        String phone = editPhone.getText().toString();
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        if (name.isEmpty()) {
            layoutName.setError("Name cannot be empty");
        } else if (phone.isEmpty()) {
            layoutPhone.setError("Phone cannot be empty");
        } else if (username.isEmpty()) {
            layoutUsername.setError("Username cannot be empty");
        } else if (password.isEmpty()) {
            layoutPassword.setError("Password cannot be empty");
        } else {
            groupRegister.setVisibility(View.GONE);
            spin.setVisibility(View.VISIBLE);
            postUser(name, phone, username, password);
        }
    }

    private void postUser(String name, String phone, String username, String password) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put(AppConstants.NAME, name);
        newUser.put(AppConstants.PHONE, phone);
        newUser.put(AppConstants.USERNAME, username);
        newUser.put(AppConstants.PASSWORD, password);

        FirebaseFirestore.getInstance().collection("users")
                .document(username)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    SharedPref sharedPref = SharedPref.getInstance(this);
                    sharedPref.saveData(AppConstants.LOG, AppConstants.LOG);
                    sharedPref.saveData(AppConstants.NAME, name);
                    sharedPref.saveData(AppConstants.PHONE, phone);
                    startActivity(new Intent(this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                    showSnack("Registration Failed");
                });
    }

    private void showSnack(String s) {
        Snackbar.make(findViewById(R.id.registration_layout), s, Snackbar.LENGTH_LONG).show();
    }
}
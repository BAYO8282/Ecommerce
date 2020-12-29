package net.bugfixers.e_commerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.adapters.CheckoutAdapter;
import net.bugfixers.e_commerce.constants.AppConstants;
import net.bugfixers.e_commerce.database.SharedPref;
import net.bugfixers.e_commerce.models.Product;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "Checkout";
    private CheckoutAdapter adapter;
    private ArrayList<Product> orders;

    private TextInputLayout layoutAddress;
    private TextInputLayout layoutCity;
    private TextInputLayout layoutZipCode;
    private EditText editAddress;
    private EditText editCity;
    private EditText editZipCode;
    private AppCompatCheckBox saveAddress;
    private Group groupCheckout;
    private SpinKitView spin;

    private SharedPref sharedPref;
    private String address;
    private String city;
    private String zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        orders = getIntent().getParcelableArrayListExtra("products");
        Log.d(TAG, "amount: " + orders.size());

        sharedPref = SharedPref.getInstance(this);
        address = sharedPref.getData(AppConstants.ADDRESS);
        city = sharedPref.getData(AppConstants.CITY);
        zipCode = sharedPref.getData(AppConstants.ZIP_CODE);
        groupCheckout = findViewById(R.id.group_checkout);
        spin = findViewById(R.id.spin_kit);

        layoutAddress = findViewById(R.id.layout_address);
        layoutCity = findViewById(R.id.layout_city);
        layoutZipCode = findViewById(R.id.layout_zip);
        editAddress = findViewById(R.id.edit_address);
        editCity = findViewById(R.id.edit_city);
        editZipCode = findViewById(R.id.edit_zip);
        if (address != null) editAddress.setText(address);
        if (city != null) editCity.setText(city);
        if (zipCode != null) editZipCode.setText(zipCode);
        saveAddress = findViewById(R.id.check_save_address);
        Button buttonCheckout = findViewById(R.id.button_checkout);
        buttonCheckout.setOnClickListener(v -> {
            address = editAddress.getText().toString();
            city = editCity.getText().toString();
            zipCode = editZipCode.getText().toString();
            if (address.isEmpty()) {
                layoutAddress.setError("Address cannot be empty");
            } else if (city.isEmpty()) {
                layoutCity.setError("City cannot be empty");
            } else if (zipCode.isEmpty()) {
                layoutZipCode.setError("Zip code cannot be empty");
            } else {
                if (saveAddress.isChecked()) {
                    sharedPref.saveData(AppConstants.ADDRESS, address);
                    sharedPref.saveData(AppConstants.CITY, city);
                    sharedPref.saveData(AppConstants.ZIP_CODE, zipCode);
                }
                postOrder();
            }
        });

        adapter = new CheckoutAdapter(this, orders);
        RecyclerView recyclerView = findViewById(R.id.order_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void postOrder() {
        groupCheckout.setVisibility(View.GONE);
        spin.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put(AppConstants.USERNAME, sharedPref.getData(AppConstants.NAME));
        map.put(AppConstants.ADDRESS, address);
        map.put(AppConstants.CITY, city);
        map.put(AppConstants.ZIP_CODE, zipCode);
        map.put("time", Calendar.getInstance().getTimeInMillis());
        map.put("cart", orders);
        FirebaseFirestore.getInstance().collection("orders")
                .add(map)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    showSnack("Successfully placed order!");
                    new Handler().postDelayed(() -> {
                        spin.setVisibility(View.GONE);
                        startActivity(new Intent(this, MainActivity.class));
                    }, 2500);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    spin.setVisibility(View.GONE);
                    groupCheckout.setVisibility(View.VISIBLE);
                    showSnack("Order failed!");
                });
    }

    private void showSnack(String s) {
        Snackbar.make(findViewById(R.id.layout_checkout), s, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
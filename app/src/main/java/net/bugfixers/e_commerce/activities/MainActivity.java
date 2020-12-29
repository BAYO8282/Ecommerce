package net.bugfixers.e_commerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.adapters.MainAdapter;
import net.bugfixers.e_commerce.adapters.OrderAdapter;
import net.bugfixers.e_commerce.constants.AppConstants;
import net.bugfixers.e_commerce.database.SharedPref;
import net.bugfixers.e_commerce.models.Order;
import net.bugfixers.e_commerce.models.Product;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main";

    private TextView textAll;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    public ArrayList<Product> products;
    private RecyclerView mainRecycler;
    private MainAdapter adapter;

    private ConstraintLayout profileLayout;
    private ConstraintLayout ordersLayout;
    private SharedPref sharedPref;

    private String address;
    private String city;
    private String zipCode;

    private SpinKitView spin;

    private TextInputLayout layoutAddress;
    private TextInputLayout layoutCity;
    private TextInputLayout layoutZipCode;
    private EditText editAddress;
    private EditText editCity;
    private EditText editZipCode;
    private ImageView edit;
    private boolean editMode;
    private String category;

    private TextView textNoOder;
    private Group groupOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = SharedPref.getInstance(this);
        address = sharedPref.getData(AppConstants.ADDRESS);
        city = sharedPref.getData(AppConstants.CITY);
        zipCode = sharedPref.getData(AppConstants.ZIP_CODE);
        spin = findViewById(R.id.spin_kit);
        textAll = findViewById(R.id.text_all);
        TextView checkout = findViewById(R.id.checkout);
        profileLayout = findViewById(R.id.profile_layout);
        ordersLayout = findViewById(R.id.orders_layout);
        Button logout = findViewById(R.id.button_logout);
        TextView profileName = findViewById(R.id.profile_name);
        TextView profilePhone = findViewById(R.id.profile_phone);
        profileName.setText(sharedPref.getData(AppConstants.NAME));
        profilePhone.setText(String.format("Phone: %s", sharedPref.getData(AppConstants.PHONE)));
        logout.setOnClickListener(v -> {
            sharedPref.saveData(AppConstants.LOG, null);
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
        checkout.setOnClickListener(v -> {
                ArrayList<Product> orders = new ArrayList<>();
                for (Product product : products) {
                    if (product.getAmount() > 0) orders.add(product);
                }
                if (orders.size() > 0) {
                    startActivity(new Intent(this, CheckoutActivity.class)
                            .putParcelableArrayListExtra("products", orders)
                    );
                } else {
                    showSnack("Nothing is added to cart");
                }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        changeToolbarStatus(true);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu) {
                ordersLayout.setVisibility(View.GONE);
                drawer.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                category = "All Products";
                changeToolbarStatus(true);
                changeCategory();
            } else if (item.getItemId() == R.id.favorites) {
                ordersLayout.setVisibility(View.GONE);
                drawer.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                category = "Favorites";
                changeToolbarStatus(true);
                changeCategory();
            } else if (item.getItemId() == R.id.orders) {
                ordersLayout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                drawer.setVisibility(View.GONE);
                category = "Orders";
                changeToolbarStatus(false);
            } else if (item.getItemId() == R.id.profile) {
                ordersLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.VISIBLE);
                drawer.setVisibility(View.GONE);
                category = "Profile";
                changeToolbarStatus(false);
            }
            return true;
        });

        textNoOder = findViewById(R.id.no_order);
        groupOrder = findViewById(R.id.group_order);
        layoutAddress = findViewById(R.id.layout_address);
        layoutCity = findViewById(R.id.layout_city);
        layoutZipCode = findViewById(R.id.layout_zip);
        editAddress = findViewById(R.id.edit_address);
        editCity = findViewById(R.id.edit_city);
        editZipCode = findViewById(R.id.edit_zip);
        editAddress.setEnabled(false);
        editCity.setEnabled(false);
        editZipCode.setEnabled(false);
        if (address != null) editAddress.setText(address);
        if (city != null) editCity.setText(city);
        if (zipCode != null) editZipCode.setText(zipCode);
        edit = findViewById(R.id.edit);
        edit.setOnClickListener(v -> {
            if (editMode) {
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
                    sharedPref.saveData(AppConstants.ADDRESS, address);
                    sharedPref.saveData(AppConstants.CITY, city);
                    sharedPref.saveData(AppConstants.ZIP_CODE, zipCode);
                    editAddress.setEnabled(false);
                    editCity.setEnabled(false);
                    editZipCode.setEnabled(false);
                    edit.setImageResource(R.drawable.ic_edit);
                    editMode = false;
                    showSnack("Address updated!");
                }
            } else {
                edit.setImageResource(R.drawable.ic_update);
                editAddress.setEnabled(true);
                editCity.setEnabled(true);
                editZipCode.setEnabled(true);
                editAddress.requestFocus();
                editMode = true;
            }
        });

        products = new ArrayList<>();

        mainRecycler = findViewById(R.id.recyclerview_main);
        mainRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        category = "All Products";
        changeCategory();
        getCategory();
        getProducts();
        getOrders();
    }

    private void getOrders() {
        FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("username", sharedPref.getData(AppConstants.NAME))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Order> orders = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            orders.add(new Order(document.getId(), document.getLong("time"), document.getString("address"), document.getString("city"), document.getString("zipCode")));
                        }
                        if (orders.size() > 0) {
                            textNoOder.setVisibility(View.GONE);
                            groupOrder.setVisibility(View.VISIBLE);
                            RecyclerView recyclerView = findViewById(R.id.order_recycler);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            recyclerView.setAdapter(new OrderAdapter(this, orders));
                        } else {
                            groupOrder.setVisibility(View.GONE);
                            textNoOder.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    spin.setVisibility(View.GONE);
                });
    }

    private void changeToolbarStatus(boolean b) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(b);
            getSupportActionBar().setHomeButtonEnabled(b);
        }
        if (b) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    private void getCategory() {
        Menu menu = navigationView.getMenu();
        FirebaseFirestore.getInstance().collection("category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        menu.add("All Products");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            menu.add(document.getString("name"));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void getProducts() {
        products.clear();
        FirebaseFirestore.getInstance().collection("product")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            products.add(document.toObject(Product.class));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    spin.setVisibility(View.GONE);
                });
    }

    private void changeCategory() {
        textAll.setText(category);
        adapter = new MainAdapter(this, category);
        mainRecycler.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Category: " + category);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else if (!category.equals("All Products")) {
            ordersLayout.setVisibility(View.GONE);
            drawer.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
            category = "All Products";
            changeToolbarStatus(true);
            changeCategory();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> super.onBackPressed())
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        category = item.getTitle().toString();
        changeCategory();
        return true;
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("products", products);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        products = savedInstanceState.getParcelableArrayList("products");
    }

    private void showSnack(String s) {
        Snackbar.make(findViewById(R.id.main_layout), s, Snackbar.LENGTH_LONG).show();
    }
}
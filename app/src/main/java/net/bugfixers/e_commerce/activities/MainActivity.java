package net.bugfixers.e_commerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.adapters.MainAdapter;
import net.bugfixers.e_commerce.constants.AppConstants;
import net.bugfixers.e_commerce.database.SharedPref;
import net.bugfixers.e_commerce.models.Product;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main";

    private TextView textAll;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private ArrayList<Product> products;
    private RecyclerView mainRecycler;
    private MainAdapter adapter;

    private ConstraintLayout profileLayout;
    private ConstraintLayout ordersLayout;
    private SharedPref sharedPref;

    private SpinKitView spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        sharedPref = SharedPref.getInstance(this);
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
        checkout.setOnClickListener(v ->
                startActivity(new Intent(this, CheckoutActivity.class)

                )

        );
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu) {
                ordersLayout.setVisibility(View.GONE);
                drawer.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                changeCategory("All Products");
            } else if (item.getItemId() == R.id.favorites) {
                ordersLayout.setVisibility(View.GONE);
                drawer.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                changeCategory("Favorite");
            } else if (item.getItemId() == R.id.orders) {
                ordersLayout.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.GONE);
                drawer.setVisibility(View.GONE);
            } else if (item.getItemId() == R.id.profile) {
                ordersLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.VISIBLE);
                drawer.setVisibility(View.GONE);
            }
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        products = new ArrayList<>();

        adapter = new MainAdapter(this, products, "All Products");
        mainRecycler = findViewById(R.id.recyclerview_main);
        mainRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mainRecycler.setAdapter(adapter);

        getCategory();
        getProducts();
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

    private void changeCategory(String category) {
        textAll.setText(category);
        adapter = new MainAdapter(this, products, category);
        mainRecycler.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        String title = item.getTitle().toString();
        Log.d(TAG, "Title: " + item.getTitle());
        changeCategory(title);
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
}
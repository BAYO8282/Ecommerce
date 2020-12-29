package net.bugfixers.e_commerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.adapters.CheckoutAdapter;
import net.bugfixers.e_commerce.models.Product;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "Checkout";
    private CheckoutAdapter adapter;
    private ArrayList<Product> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        orders = getIntent().getParcelableArrayListExtra("products");
        Log.d(TAG, "amount: " + orders.size());

        adapter = new CheckoutAdapter(this, orders);
        RecyclerView recyclerView = findViewById(R.id.order_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
package net.bugfixers.e_commerce.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.bugfixers.e_commerce.R;

public class ProductDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        String image = getIntent().getStringExtra("productImage");
        String name = getIntent().getStringExtra("productName");
        int price = getIntent().getIntExtra("productPrice", 0);
        String details = getIntent().getStringExtra("productDetails");
        boolean isFavorite = getIntent().getBooleanExtra("isFavorite", false);

        ImageView imageProduct = findViewById(R.id.image_product);
        TextView productName = findViewById(R.id.text_product_name);
        TextView productPrice = findViewById(R.id.text_product_price);
        TextView productDetails = findViewById(R.id.text_product_details);
        ImageView imageFavorite = findViewById(R.id.favorite);
        Glide.with(this).load(image).placeholder(R.drawable.ic_blank_image).error(R.drawable.ic_blank_image).into(imageProduct);
        productName.setText(name);
        productPrice.setText(String.format("Price: %s Taka", price));
        productDetails.setText(details == null?"No details":Html.fromHtml(details));
        Glide.with(this).load(isFavorite?R.drawable.ic_favorites:R.drawable.ic_favorites_border).into(imageFavorite);
    }
}
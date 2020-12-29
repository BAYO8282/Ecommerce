package net.bugfixers.e_commerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.activities.MainActivity;
import net.bugfixers.e_commerce.models.Product;

import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final Context context;
    private final String category;

    public MainAdapter(Context context, String category) {
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = ((MainActivity)context).products.get(position);

        if (category.equals("All Products")) {
            holder.itemView.setVisibility(View.VISIBLE);
        }else if(category.equals("Favorites") && product.isFavorite()) {
            holder.itemView.setVisibility(View.VISIBLE);
        } else if (category.equals(product.getCategory())) {
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format(Locale.getDefault(),"Price: %d Taka", product.getPrice()));
        Glide.with(context).load(product.getImage()).placeholder(R.drawable.ic_profile_large)
                .error(R.drawable.ic_profile_large).into(holder.productImage);

        holder.plus.setOnClickListener(v -> {
            product.setAmount(product.getAmount() + 1);
            holder.count.setText(String.valueOf(product.getAmount()));
        });
        holder.minus.setOnClickListener(v -> {
            if (product.getAmount() == 1) {
                holder.addToCart.setVisibility(View.VISIBLE);
                holder.cartAmount.setVisibility(View.GONE);
            } else {
                product.setAmount(product.getAmount() - 1);
            }
            holder.count.setText(String.valueOf(product.getAmount()));
        });

        if (product.getAmount() > 0) {
            holder.addToCart.setVisibility(View.GONE);
            holder.cartAmount.setVisibility(View.VISIBLE);
            holder.count.setText(String.valueOf(product.getAmount()));
        } else {
            holder.addToCart.setOnClickListener(v -> {
                product.setAmount(1);
                holder.addToCart.setVisibility(View.GONE);
                holder.cartAmount.setVisibility(View.VISIBLE);
                holder.count.setText(String.valueOf(product.getAmount()));
            });
        }

        holder.favorite.setOnClickListener(v -> {
            product.setFavorite(!product.isFavorite());
            holder.favorite.setImageResource(product.isFavorite()?R.drawable.ic_favorites:R.drawable.ic_favorites_border);
        });

        holder.favorite.setImageResource(product.isFavorite()?R.drawable.ic_favorites:R.drawable.ic_favorites_border);
    }

    @Override
    public int getItemCount() {
        return ((MainActivity)context).products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView productImage;
        TextView productPrice;
        ImageView addToCart;
        ImageView plus;
        ImageView minus;
        TextView count;
        LinearLayout cartAmount;
        ImageView favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.text_product_name);
            productPrice = itemView.findViewById(R.id.text_product_price);
            productImage = itemView.findViewById(R.id.image_product);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            count = itemView.findViewById(R.id.count);
            cartAmount = itemView.findViewById(R.id.cart_amount);
            favorite = itemView.findViewById(R.id.favorite);
        }
    }
}

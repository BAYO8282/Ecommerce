package net.bugfixers.e_commerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.bugfixers.e_commerce.R;
import net.bugfixers.e_commerce.activities.CheckoutActivity;
import net.bugfixers.e_commerce.models.Product;

import java.util.ArrayList;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Product> list;

    public CheckoutAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format(Locale.getDefault(),"%d x %d = %d Taka", product.getPrice(), product.getAmount(), product.getPrice()*product.getAmount()));
        Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.ic_profile_large)
                .error(R.drawable.ic_profile_large).into(holder.productImage);

        holder.count.setText(String.valueOf(product.getAmount()));
        holder.plus.setOnClickListener(v -> {
            product.setAmount(product.getAmount() + 1);
            holder.count.setText(String.valueOf(product.getAmount()));
            holder.productPrice.setText(String.format(Locale.getDefault(),"%d x %d = %d Taka", product.getPrice(), product.getAmount(), product.getPrice()*product.getAmount()));
        });
        holder.minus.setOnClickListener(v -> {
            if (product.getAmount() == 1) {
                list.remove(position);
                notifyDataSetChanged();
            } else {
                product.setAmount(product.getAmount() - 1);
                holder.productPrice.setText(String.format(Locale.getDefault(),"%d x %d = %d Taka", product.getPrice(), product.getAmount(), product.getPrice()*product.getAmount()));
                holder.count.setText(String.valueOf(product.getAmount()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0) {
            return list.size();
        } else {
            ((CheckoutActivity) context).finish();
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView productImage;
        TextView productPrice;
        ImageView plus;
        ImageView minus;
        TextView count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.text_product_name);
            productPrice = itemView.findViewById(R.id.text_product_price);
            productImage = itemView.findViewById(R.id.image_product);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            count = itemView.findViewById(R.id.count);
        }
    }
}

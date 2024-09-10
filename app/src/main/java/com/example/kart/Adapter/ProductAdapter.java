package com.example.kart.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Model.CartModel;
import com.example.kart.Model.Product;
import com.example.kart.ProductDetail;
import com.example.kart.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    String admin;

    public ProductAdapter(Context context, List<Product> productList, String admin) {
        this.context = context;
        this.productList = productList;
        this.admin = admin;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.description.setText(product.getDescription());
        holder.mrp.setText("₹ " +product.getMrp());
        holder.salePrice.setText("₹ " +product.getSale());

        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        // You can use an image loading library like Glide or Picasso to load the image
        // Glide.with(holder.itemView.getContext()).load(product.getImageUrl()).into(holder.imageView);
        Picasso.get().load(product.getImage()).into(holder.image);



        if (admin == "true")
        {
            holder.addToCart.setVisibility(View.GONE);
            holder.delete.setVisibility(View.VISIBLE);
        }
        else {
            holder.addToCart.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = holder.addToCart.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.addToCart.setLayoutParams(params);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                deleteProduct(product.getId(), position);
            }

        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id", product.getId());
                context.startActivity(intent);
            }
        });


        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CartModel cartModel = new CartModel();
                cartModel.setTitle(product.getTitle());
                cartModel.setImage(product.getImage());
                cartModel.setMrp(product.getMrp());
                cartModel.setPrice(product.getSale());
                cartModel.setId(product.getId());
                cartModel.setQty("1");

                SharedPreferences preferences =  context.getSharedPreferences("phone", MODE_PRIVATE);
                String ph = preferences.getString("phone","");



                FirebaseDatabase.getInstance().getReference("AddToCart").child(ph).child(product.getId()).setValue(cartModel);

                Toast.makeText(context, "Added To Cart", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void deleteProduct(String id, int position) {

        FirebaseDatabase.getInstance().getReference("AllProducts").child(id).removeValue();
        productList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, mrp, salePrice, addToCart, delete;
        ShapeableImageView image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            mrp = itemView.findViewById(R.id.mrp);
            salePrice = itemView.findViewById(R.id.sale_price);
            image = itemView.findViewById(R.id.post_image);
            addToCart = itemView.findViewById(R.id.addToCart);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}

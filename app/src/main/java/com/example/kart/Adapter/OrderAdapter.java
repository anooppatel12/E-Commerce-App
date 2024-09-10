package com.example.kart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Model.CartModel;
import com.example.kart.Model.Request;
import com.example.kart.OrderDetail;
import com.example.kart.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ProductViewHolder> {

    private Context context;
    private List<Request> productList;


    public OrderAdapter(Context context, List<Request> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Request request = productList.get(position);

        holder.id.setText("#"+request.getId());
        holder.amount.setText("₹ " + request.getOrderAmount());
        holder.name.setText(request.getName());
        holder.phone.setText(request.getPhone());
        holder.status.setText(request.getStatus());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetail.class);
                intent.putExtra("id", request.getId());
                context.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, phone, status, amount;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            status = itemView.findViewById(R.id.status);
            amount = itemView.findViewById(R.id.amount);

        }
    }
}

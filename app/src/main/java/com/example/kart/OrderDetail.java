package com.example.kart;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Adapter.OrderAdapter;
import com.example.kart.Adapter.ProductOrderAdapter;
import com.example.kart.Model.CartModel;
import com.example.kart.Model.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail extends AppCompatActivity {

    RecyclerView recyclerView;

    ProductOrderAdapter adapter;
    List<CartModel> productList;

    FirebaseDatabase database;
    DatabaseReference reference;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        id = getIntent().getStringExtra("id");

        recyclerView = findViewById(R.id.recycler_orders_detail);

        productList = new ArrayList<>();
        adapter = new ProductOrderAdapter(OrderDetail.this, productList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("CartList");


        loadProduct();

    }

    private void loadProduct() {

        SharedPreferences preferences =  getSharedPreferences("phone", MODE_PRIVATE);
        String ph = preferences.getString("phone","");

        reference.child(ph).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartModel cartModel = dataSnapshot.getValue(CartModel.class);
                    productList.add(cartModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product data", error.toException());
            }
        });

    }
}
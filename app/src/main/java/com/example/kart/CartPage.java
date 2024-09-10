package com.example.kart;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Adapter.CartAdapter;
import com.example.kart.Adapter.ProductAdapter;
import com.example.kart.Model.CartModel;
import com.example.kart.Model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartPage extends AppCompatActivity {

    RecyclerView recyclerView;
    CartAdapter adapter;
    TextView cartCount;
    List<CartModel> productList;

    FirebaseDatabase database;
    DatabaseReference reference;

    TextView total, subtotal, charges, checkout;

    int charge = 0;
    double sum = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_page);

        total = findViewById(R.id.total);
        subtotal = findViewById(R.id.subtotal);
        charges = findViewById(R.id.charges);

        checkout = findViewById(R.id.checkout);






        cartCount = findViewById(R.id.count_cart);
        recyclerView = findViewById(R.id.recycler_cart);

        FirebaseDatabase.getInstance().getReference("Charge").child("charge")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                       if (snapshot.exists()){
                           String cc = snapshot.getValue(String.class);
                           charges.setText("₹"+cc);
                           charge = Integer.parseInt(cc);
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        productList = new ArrayList<>();
        adapter = new CartAdapter(CartPage.this, productList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SharedPreferences preferences =  getSharedPreferences("phone", MODE_PRIVATE);
        String ph = preferences.getString("phone","");


        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AddToCart").child(ph);


        loadProductData();
    }

    private void loadProductData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartModel product = dataSnapshot.getValue(CartModel.class);
                    productList.add(product);

                    Map<String, Objects> map = (Map<String, Objects>) dataSnapshot.getValue();
                    Object pp = map.get("price");
                    Object qty = map.get("qty");

                    Double tt = Double.parseDouble(String.valueOf(pp));

                    Double qq = Double.parseDouble(String.valueOf(qty));

                    double total = tt*qq;

                    sum += total;

                }

                checkout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartPage.this, CheckoutPage.class);
                        intent.putExtra("amount",String.valueOf(sum+charge));
                        startActivity(intent);
                    }
                });


                subtotal.setText(String.valueOf(sum));
                total.setText(String.valueOf(sum+charge));

                String count = String.valueOf(productList.size());
                cartCount.setText("Total Items ("+count+")");

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product data", error.toException());
            }
        });
    }
}
package com.example.kart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kart.Model.CartModel;
import com.example.kart.Model.Product;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDetail extends AppCompatActivity {

    private static final String TAG = "ProductDetail";

    private ShapeableImageView postImage;
    private TextView title, description, mrp, salePrice;
    private Button addToCart;

    private DatabaseReference productReference;

    TextView increase, decrease, countNumber;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        decrease = findViewById(R.id.decrement);
        increase = findViewById(R.id.increment);
        countNumber = findViewById(R.id.count_number);

        postImage = findViewById(R.id.post_image);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        mrp = findViewById(R.id.mrp);
        salePrice = findViewById(R.id.sale_price);
        addToCart = findViewById(R.id.addToCart);

        String productId = getIntent().getStringExtra("id");
        if (productId != null) {
            productReference = FirebaseDatabase.getInstance().getReference("AllProducts").child(productId);
            loadProductDetails();
        } else {
            Log.e(TAG, "Product ID not found in intent extras");
        }

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count > 1) {
                    count--;
                    countNumber.setText(String.valueOf(count));
                }
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                countNumber.setText(String.valueOf(count));
            }
        });

    }

    private void loadProductDetails() {
        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    title.setText(product.getTitle());
                    description.setText(product.getDescription());
                    mrp.setText("₹ " + product.getMrp());
                    salePrice.setText("₹ " + product.getSale());
                    Picasso.get().load(product.getImage()).into(postImage);

                    addToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CartModel cartModel = new CartModel();
                            cartModel.setTitle(product.getTitle());
                            cartModel.setImage(product.getImage());
                            cartModel.setMrp(product.getMrp());
                            cartModel.setPrice(product.getSale());
                            cartModel.setId(product.getId());
                            cartModel.setQty(String.valueOf(count));

                            SharedPreferences preferences =  getSharedPreferences("phone", MODE_PRIVATE);
                            String ph = preferences.getString("phone","");


                            FirebaseDatabase.getInstance().getReference("AddToCart").child(ph).child(product.getId()).setValue(cartModel);

                            Toast.makeText(ProductDetail.this, "Added To Cart", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    Log.e(TAG, "Product not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product details", error.toException());
            }
        });
    }
}

package com.example.kart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Adapter.CategoryAdapter;
import com.example.kart.Adapter.ProductAdapter;
import com.example.kart.Model.Category;
import com.example.kart.Model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainSection extends AppCompatActivity {

    private static final String TAG = "MainSection";

    RecyclerView recyclerView, recycler_category;
    ImageView cart, myOrder;
    ProductAdapter adapter;
    CategoryAdapter categoryAdapter;
    List<Product> myList;
    List<Category> catList;

    FirebaseDatabase database;
    DatabaseReference reference, referenceCategory;

    SearchView searchView;
    VideoView videoView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_section);
        Log.d(TAG, "MainSection Activity Created");

        String myValue = getIntent().getStringExtra("state");
        Toast.makeText(this, "" + myValue, Toast.LENGTH_SHORT).show();

        cart = findViewById(R.id.cart);
        myOrder = findViewById(R.id.my_order);
        searchView = findViewById(R.id.search_view);

        searchView.setQueryHint("Search here...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProduct(newText);
                return false;
            }
        });

        videoView = findViewById(R.id.video_view);

//        videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+
//                R.raw.video));

        videoView.setVideoURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/kart-655dd.appspot.com/o/video.mp4?alt=media&token=a77e3346-9152-4f98-a78e-f63e8965baa7"));

        MediaController mediaController = new MediaController(this);

        videoView.setMediaController(mediaController);

//        videoView.start();

        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSection.this, MyOrder.class));
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSection.this, CartPage.class));
            }
        });

        recyclerView = findViewById(R.id.recycler_product);
        recycler_category = findViewById(R.id.recycler_category);

        myList = new ArrayList<>();
        catList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AllProducts");
        referenceCategory = database.getReference("AllCategory");

        // Set up RecyclerView for products
        adapter = new ProductAdapter(MainSection.this, myList, "false");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up RecyclerView for categories (horizontal)
        categoryAdapter = new CategoryAdapter(MainSection.this, catList, "false");
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_category.setLayoutManager(horizontalLayoutManager);
        recycler_category.setAdapter(categoryAdapter);

        loadProductData();
        loadCategory();
    }

    private void searchProduct(String newText) {

        String ss = newText.toLowerCase();

        Query query = reference.orderByChild("title").startAt(ss).endAt(ss + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product model = dataSnapshot.getValue(Product.class);
                    myList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load products", error.toException());
            }
        });
    }

    private void loadCategory() {
        referenceCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                catList.clear(); // Clear the list to avoid duplication
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category model = dataSnapshot.getValue(Category.class);
                    catList.add(model);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load categories", error.toException());
            }
        });
    }

    private void loadProductData() {
        Log.d(TAG, "Loading product data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myList.clear(); // Clear the list to avoid duplication
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product model = dataSnapshot.getValue(Product.class);
                    myList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load products", error.toException());
            }
        });
    }
}

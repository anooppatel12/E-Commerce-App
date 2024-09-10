package com.example.kart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Adapter.ProductAdapter;
import com.example.kart.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AdminSection extends AppCompatActivity {

    private static final String TAG = "AdminSection";

    EditText title, description, mrp, salePrice, charge;
    ImageView upload, image_view;
    Button publish, update;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference reference;

    LinearLayoutManager layoutManager;

    private String url;
    Uri imageUri;

    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Product> myList;

    String catId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_section);
        Log.d(TAG, "AdminSection Activity Created");

        catId = getIntent().getStringExtra("CatId");

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        mrp = findViewById(R.id.mrp);
        salePrice = findViewById(R.id.sale_price);
        image_view = findViewById(R.id.image_view);
        upload = findViewById(R.id.upload);
        publish = findViewById(R.id.publish);

        charge = findViewById(R.id.charge);
        update = findViewById(R.id.update);

        FirebaseDatabase.getInstance().getReference("Charge").child("charge")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String cc = snapshot.getValue(String.class);
                        charge.setText(cc);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Charge").child("charge").setValue(charge.getText().toString());
                Toast.makeText(AdminSection.this, "Updated!", Toast.LENGTH_SHORT).show();
            }
        });


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AllProducts");

        reference.keepSynced(true);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ms = String.valueOf(System.currentTimeMillis());

                Product model = new Product();

                model.setTitle(title.getText().toString());
                model.setDescription(description.getText().toString());
                model.setMrp(mrp.getText().toString());
                model.setSale(salePrice.getText().toString());
                model.setId(ms);
                model.setImage(url);
                model.setCatId(catId);

                if (title.getText().length() > 5) {
                    if (description.getText().length() > 10) {
                        reference.child(ms).setValue(model);
                        Toast.makeText(AdminSection.this, "Published", Toast.LENGTH_SHORT).show();

                        title.setText("");
                        description.setText("");
                        mrp.setText("");
                        salePrice.setText("");
                        image_view.setImageResource(R.drawable.placeholder__image);
                        loadProductData();
                    } else {
                        description.setError("Can't be Empty");
                    }
                } else {
                    title.setError("Can't be Empty");
                }
            }
        });

        recyclerView = findViewById(R.id.post_recycler); // Initialize RecyclerView
        myList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        adapter = new ProductAdapter(AdminSection.this, myList, "false");

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadProductData();
    }

    private void loadProductData() {
        Query query = reference.orderByChild("catId").equalTo(catId);
        myList.clear();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product model = dataSnapshot.getValue(Product.class);
                    myList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product data", error.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            uploadImage(imageUri);
            image_view.setImageURI(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            String tt = String.valueOf(System.currentTimeMillis());
            StorageReference folder = storageReference.child("PostImages/" + tt);

            folder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            folder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                    Log.d(TAG, "Image URL: " + url);
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            dialog.setMessage("Uploading..." + progress + "%");
                        }
                    });
        }
    }
}

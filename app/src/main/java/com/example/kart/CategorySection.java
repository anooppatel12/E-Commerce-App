package com.example.kart;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kart.Adapter.CategoryAdapter;
import com.example.kart.Model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CategorySection extends AppCompatActivity {

    private static final String TAG = "CategorySection";

    EditText title;
    ImageView upload, image_view;
    Button publish;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference reference;

    LinearLayoutManager layoutManager;

    private String url;
    Uri imageUri;

    RecyclerView recyclerView;
    CategoryAdapter adapter;
    List<Category> myList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_section);
        Log.d(TAG, "CategorySection Activity Created");

        title = findViewById(R.id.title);
        image_view = findViewById(R.id.image_view);
        upload = findViewById(R.id.upload);
        publish = findViewById(R.id.publish);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 21);
            }
        });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AllCategory");

        reference.keepSynced(true);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ms = String.valueOf(System.currentTimeMillis());

                Category model = new Category();
                model.setTitle(title.getText().toString());
                model.setId(ms);
                model.setImage(url);

                if (title.getText().length() > 0) {
                    reference.child(ms).setValue(model);
                    Toast.makeText(CategorySection.this, "Published", Toast.LENGTH_SHORT).show();

                    title.setText("");
                    image_view.setImageResource(R.drawable.placeholder__image);
                    loadCategoryData();
                } else {
                    title.setError("Can't be Empty");
                }
            }
        });

        recyclerView = findViewById(R.id.category_recycler);
        myList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        layoutManager = new LinearLayoutManager(this);
        adapter = new CategoryAdapter(CategorySection.this, myList, "true");

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadCategoryData();
    }

    private void loadCategoryData() {
        myList.clear();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category model = dataSnapshot.getValue(Category.class);
                    myList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load category data", error.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 21 && resultCode == RESULT_OK && data != null) {
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
            StorageReference folder = storageReference.child("CategoryImages/" + tt);

            folder.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Log.e(TAG, "Image upload failed", e);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            dialog.setMessage("Uploading... " + (int) progress + "%");
                        }
                    });
        }
    }
}

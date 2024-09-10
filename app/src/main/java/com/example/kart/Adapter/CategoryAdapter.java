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

import com.example.kart.AdminSection;
import com.example.kart.Model.Category;
import com.example.kart.ProductList;
import com.example.kart.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    String admin;

    public CategoryAdapter(Context context, List<Category> categoryList , String admin) {
        this.context = context;
        this.categoryList = categoryList;
        this.admin = admin;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.title.setText(category.getTitle());

        Picasso.get().load(category.getImage()).into(holder.image);


        if (admin == "true")
        {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else
        {
            holder.deleteButton.setVisibility(View.GONE);
        }


        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(category.getId(), position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (admin =="true")
                {
                    Intent intent = new Intent(context, AdminSection.class);
                    intent.putExtra("CatId", category.getId());
                    context.startActivity(intent);
                }else
                {
                    Intent intent = new Intent(context, ProductList.class);
                    intent.putExtra("CatId", category.getId());
                    context.startActivity(intent);
                }

            }
        });
    }

    private void deleteCategory(String id, int position) {
        FirebaseDatabase.getInstance().getReference("AllCategory").child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                    categoryList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete category", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ShapeableImageView image;
        ImageView deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.post_image);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}

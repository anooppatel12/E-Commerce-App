package com.example.kart.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kart.Adapter.OrderAdapter;
import com.example.kart.Model.Request;
import com.example.kart.MyOrder;
import com.example.kart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TwoFragment extends Fragment {

    RecyclerView recyclerView;

    OrderAdapter adapter;
    List<Request> productList;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);

        productList = new ArrayList<>();
        adapter = new OrderAdapter(getActivity(), productList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Orders");


        loadOrders();

        return view;
    }

    private void loadOrders() {
        SharedPreferences preferences =  getActivity().getSharedPreferences("phone", MODE_PRIVATE);
        String ph = preferences.getString("phone","");

        reference.child(ph).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request request = dataSnapshot.getValue(Request.class);
                    productList.add(request);
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
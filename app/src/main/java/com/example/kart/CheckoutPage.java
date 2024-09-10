package com.example.kart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kart.Model.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CheckoutPage extends AppCompatActivity {

    String amount;

    TextView finalAmount;
    Button orderPlace;
    EditText address, city, pinCode;

    FirebaseDatabase database;
    DatabaseReference from, to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout_page);

        amount = getIntent().getStringExtra("amount");

        SharedPreferences preferences =  getSharedPreferences("phone", MODE_PRIVATE);
        String ph = preferences.getString("phone","");



        database =FirebaseDatabase.getInstance();
        from = database.getReference("AddToCart").child(ph);

        to = database.getReference("CartList").child(ph);


        orderPlace = findViewById(R.id.order_place);
        finalAmount = findViewById(R.id.final_amount);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        pinCode = findViewById(R.id.pin_code);

        finalAmount.setText("₹"+amount);

        orderPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences =  getSharedPreferences("phone", MODE_PRIVATE);
                String ph = preferences.getString("phone","");


                String id = String.valueOf(System.currentTimeMillis());

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

                String date= df.format(calendar.getTime());

                Request request =  new Request();

                request.setAddress(address.getText().toString());
                request.setCity(city.getText().toString());
                request.setDate(date);
                request.setId(id);
                request.setName("anoop");
                request.setOrderAmount(amount);
                request.setPhone(ph);
                request.setPinCode(pinCode.getText().toString());
                request.setStatus("Processing");

                FirebaseDatabase.getInstance().getReference("Orders")
                        .child(ph).child(id).setValue(request);

                Toast.makeText(CheckoutPage.this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();

                moveCart(id);
            }
        });
    }

    private void moveCart(String id) {

        from.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                to.child(id).setValue(snapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            from.removeValue();
                            finish();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
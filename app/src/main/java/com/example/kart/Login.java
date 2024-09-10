package com.example.kart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText phone, password;
    TextView createAccount;
    Button login;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        createAccount = findViewById(R.id.create_account);
        login = findViewById(R.id.login);

        createAccount.setPaintFlags(createAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);



        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ph = phone.getText().toString();
                String pass = password.getText().toString();
                reference.child(ph).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {

                            String password = snapshot.child("password").getValue(String.class);

                            if (pass.equals(password))
                            {

                                SharedPreferences preferences = getSharedPreferences("phone", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("phone", phone.getText().toString());
                                editor.apply();

                                startActivity(new Intent(Login.this, MainActivity.class));
                            }
                            else
                            {
                                Toast.makeText(Login.this, "Wrong Password!", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else
                        {
                            Toast.makeText(Login.this, "User not Exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        checkUser();

    }

    private void checkUser() {

        SharedPreferences preferences =  getSharedPreferences("phone", MODE_PRIVATE);
        String ph = preferences.getString("phone","");

        if (ph.length()>9)
        {
            startActivity(new Intent(Login.this, MainActivity.class));
        }


    }
}
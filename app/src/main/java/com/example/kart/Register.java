package com.example.kart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    Button register;
    EditText name, phone, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.register);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();

                map.put("name", name.getText().toString());
                map.put("phone", phone.getText().toString());
                map.put("password", password.getText().toString());

                String ph = phone.getText().toString();
                String pass = password.getText().toString();
                String nm = name.getText().toString();

                if (ph.length()>9 && pass.length()>=3 && nm.length()>=3)
                {

                    SharedPreferences preferences = getSharedPreferences("phone", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("phone", ph);
                    editor.apply();

                    FirebaseDatabase.getInstance().getReference("Users").child(ph)
                            .setValue(map);
                    Toast.makeText(Register.this, "Register Successfully!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Register.this, MainActivity.class));

                }
                else
                {
                    Toast.makeText(Register.this, "Please Fill ALl Fields", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}
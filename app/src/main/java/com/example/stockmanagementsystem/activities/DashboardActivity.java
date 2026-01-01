package com.example.stockmanagementsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    Button btnProducts, btnCategories, btnOrders,
            btnSuppliers, btnUsers, btnDeleteAll, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnProducts = findViewById(R.id.btnProducts);
        btnCategories = findViewById(R.id.btnCategories);
        btnOrders = findViewById(R.id.btnOrders);
        btnSuppliers = findViewById(R.id.btnSuppliers);
        btnUsers = findViewById(R.id.btnUsers);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnLogout = findViewById(R.id.btnLogout);

        btnProducts.setOnClickListener(v ->
                startActivity(new Intent(this, ProductsActivity.class)));

        btnCategories.setOnClickListener(v ->
                startActivity(new Intent(this, CategoriesActivity.class)));

        btnOrders.setOnClickListener(v ->
                startActivity(new Intent(this, OrdersActivity.class)));

        btnSuppliers.setOnClickListener(v ->
                startActivity(new Intent(this, SuppliersActivity.class)));

        btnUsers.setOnClickListener(v ->
                startActivity(new Intent(this, UsersActivity.class)));

        btnLogout.setOnClickListener(v -> logout());

        btnDeleteAll.setOnClickListener(v -> confirmDeleteAll());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void confirmDeleteAll() {
        new AlertDialog.Builder(this)
                .setTitle("Danger")
                .setMessage("Delete ALL data? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> deleteAll())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAll() {
        FirebaseDatabase.getInstance().getReference().removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show());
    }
}

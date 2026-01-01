package com.example.stockmanagementsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    Button btnProducts, btnCategories, btnOrders,
            btnSuppliers, btnUsers, btnDeleteAll, btnLogout;

    private TextView txtTotalProducts, txtTotalStock, txtLowStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtTotalProducts = findViewById(R.id.txtTotalProducts);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        txtLowStock = findViewById(R.id.txtLowStock);

        btnProducts = findViewById(R.id.btnProducts);
        btnCategories = findViewById(R.id.btnCategories);
        btnOrders = findViewById(R.id.btnOrders);
        btnSuppliers = findViewById(R.id.btnSuppliers);
        btnUsers = findViewById(R.id.btnUsers);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnLogout = findViewById(R.id.btnLogout);

        loadDashboardData();

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
    private void loadDashboardData() {

        DatabaseReference productsRef =
                FirebaseDatabase.getInstance().getReference("products");

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalProducts = 0;
                int totalStock = 0;
                int lowStock = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    totalProducts++;

                    Integer qty = ds.child("quantity").getValue(Integer.class);
                    if (qty != null) {
                        totalStock += qty;
                        if (qty < 5) {
                            lowStock++;
                        }
                    }
                }

                txtTotalProducts.setText("Total Products: " + totalProducts);
                txtTotalStock.setText("Total Stock: " + totalStock);
                txtLowStock.setText("Low Stock Items: " + lowStock);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                        Toast.makeText(this,
                                "All data deleted", Toast.LENGTH_SHORT).show());
    }
}

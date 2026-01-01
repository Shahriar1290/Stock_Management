package com.example.stockmanagementsystem.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.adapters.OrderAdapter;
import com.example.stockmanagementsystem.models.Order;
import com.example.stockmanagementsystem.models.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    DatabaseReference ordersRef, productsRef;
    List<Order> orders = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        productsRef = FirebaseDatabase.getInstance().getReference("products");

        RecyclerView rv = findViewById(R.id.ordersRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(orders, ordersRef);
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddOrder).setOnClickListener(v -> showAddDialog());

        ordersRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot s) {
                orders.clear();
                for (com.google.firebase.database.DataSnapshot c : s.getChildren()) {
                    Order o = c.getValue(Order.class);
                    if (o != null) {
                        orders.add(new Order(c.getKey(), o.getProductId(), o.getProductName(), o.getQuantity(), o.getTimestamp()));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull com.google.firebase.database.DatabaseError e) {}
        });

        productsRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot s) {
                products.clear();
                for (com.google.firebase.database.DataSnapshot c : s.getChildren()) {
                    Product p = c.getValue(Product.class);
                    if (p != null) products.add(new Product(
                            c.getKey(),
                            p.getName(),
                            p.getPrice(),
                            p.getQuantity(),
                            p.getCategoryId()   // ✅ ADD THIS
                    ));

                }
            }
            @Override public void onCancelled(@NonNull com.google.firebase.database.DatabaseError e) {}
        });
    }

    private void showAddDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_order, null);
        Spinner sp = v.findViewById(R.id.spProducts);
        EditText etQty = v.findViewById(R.id.etQty);

        List<String> names = new ArrayList<>();
        for (Product p : products) names.add(p.getName());
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));

        new AlertDialog.Builder(this)
                .setTitle("Add Order")
                .setView(v)
                .setPositiveButton("Add", (d, w) -> {
                    int idx = sp.getSelectedItemPosition();
                    Product p = products.get(idx);
                    int q = Integer.parseInt(etQty.getText().toString());

                    String id = ordersRef.push().getKey();
                    ordersRef.child(id).setValue(
                            new Order(id, p.getId(), p.getName(), q, System.currentTimeMillis())
                    );

                    // reduce stock
                    productsRef.child(p.getId())
                            .child("quantity")
                            .setValue(p.getQuantity() - q);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

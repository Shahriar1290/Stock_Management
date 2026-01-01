package com.example.stockmanagementsystem.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddOrderActivity extends AppCompatActivity {

    private Spinner spinnerProduct;
    private EditText edtQuantity;
    private Button btnStockIn, btnStockOut;

    private String productId;
    private int orderQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        spinnerProduct = findViewById(R.id.spinnerProduct);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnStockIn = findViewById(R.id.btnStockIn);
        btnStockOut = findViewById(R.id.btnStockOut);

        loadProducts();

        btnStockIn.setOnClickListener(v -> processStock(true));
        btnStockOut.setOnClickListener(v -> processStock(false));
    }

    private void loadProducts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(AddOrderActivity.this,
                                android.R.layout.simple_spinner_item);

                for (DataSnapshot s : snapshot.getChildren()) {
                    String name = s.child("name").getValue(String.class);
                    if (name != null) adapter.add(name);
                }

                adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item);
                spinnerProduct.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //  Main stock logic
    private void processStock(boolean isStockIn) {

        String qtyStr = edtQuantity.getText().toString().trim();

        if (qtyStr.isEmpty()) {
            Toast.makeText(this, "Enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        orderQty = Integer.parseInt(qtyStr);

        if (orderQty <= 0) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String productName = spinnerProduct.getSelectedItem().toString();

        DatabaseReference productsRef =
                FirebaseDatabase.getInstance().getReference("products");

        productsRef.orderByChild("name")
                .equalTo(productName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot s : snapshot.getChildren()) {
                            productId = s.getKey();
                            Integer currentQty =
                                    s.child("quantity").getValue(Integer.class);

                            if (currentQty == null) currentQty = 0;

                            if (!isStockIn && currentQty == 0) {
                                Toast.makeText(AddOrderActivity.this,
                                        "Stock is empty. Cannot stock out.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            updateProductQuantity(
                                    productsRef,
                                    productId,
                                    currentQty,
                                    isStockIn
                            );
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    //  Update product quantity
    private void updateProductQuantity(DatabaseReference productsRef,
                                       String productId,
                                       int currentQty,
                                       boolean isStockIn) {

        int newQty = isStockIn
                ? currentQty + orderQty
                : currentQty - orderQty;

        if (newQty < 0) {
            Toast.makeText(this,
                    "Not enough stock",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        productsRef.child(productId)
                .child("quantity")
                .setValue(newQty)
                .addOnSuccessListener(unused -> {
                    saveOrder(isStockIn ? "IN" : "OUT");
                    finish();
                });
    }

    //  Save order
    private void saveOrder(String type) {

        DatabaseReference ordersRef =
                FirebaseDatabase.getInstance().getReference("orders");

        HashMap<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("productName",
                spinnerProduct.getSelectedItem().toString());
        map.put("quantity", orderQty);
        map.put("type", type);
        map.put("timestamp", System.currentTimeMillis());
        map.put("userId",
                FirebaseAuth.getInstance().getUid());

        ordersRef.push().setValue(map);
    }
}

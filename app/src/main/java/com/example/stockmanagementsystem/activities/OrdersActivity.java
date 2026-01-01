package com.example.stockmanagementsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.adapters.OrderAdapter;
import com.example.stockmanagementsystem.models.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private DatabaseReference ordersRef;
    private List<Order> orderList;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        RecyclerView recyclerView = findViewById(R.id.ordersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList, ordersRef);
        recyclerView.setAdapter(adapter);

        Button btnAddOrder = findViewById(R.id.btnAddOrder);
        btnAddOrder.setOnClickListener(v ->
                startActivity(new Intent(OrdersActivity.this, AddOrderActivity.class)));

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    Order o = s.getValue(Order.class);
                    if (o != null) {
                        orderList.add(new Order(
                                s.getKey(),          // orderId
                                o.getProductId(),
                                o.getProductName(),
                                o.getQuantity(),
                                o.getTimestamp()
                        ));
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

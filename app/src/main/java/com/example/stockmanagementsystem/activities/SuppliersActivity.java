package com.example.stockmanagementsystem.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.adapters.SupplierAdapter;
import com.example.stockmanagementsystem.models.Supplier;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SuppliersActivity extends AppCompatActivity {

    DatabaseReference suppliersRef;
    List<Supplier> supplierList;
    SupplierAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);

        suppliersRef = FirebaseDatabase.getInstance().getReference("suppliers");

        RecyclerView recycler = findViewById(R.id.suppliersRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        supplierList = new ArrayList<>();
        adapter = new SupplierAdapter(supplierList, suppliersRef);
        recycler.setAdapter(adapter);

        findViewById(R.id.btnAddSupplier).setOnClickListener(v -> addSupplier());

        suppliersRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                supplierList.clear();
                for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                    Supplier sup = s.getValue(Supplier.class);
                    if (sup != null) {
                        supplierList.add(new Supplier(
                                s.getKey(),
                                sup.getName(),
                                sup.getPhone(),
                                sup.getEmail()
                        ));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
        });
    }

    private void addSupplier() {
        EditText etName = new EditText(this);
        etName.setHint("Supplier Name");

        EditText etPhone = new EditText(this);
        etPhone.setHint("Phone");

        EditText etEmail = new EditText(this);
        etEmail.setHint("Email");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(etName);
        layout.addView(etPhone);
        layout.addView(etEmail);

        new AlertDialog.Builder(this)
                .setTitle("Add Supplier")
                .setView(layout)
                .setPositiveButton("Add", (d, w) -> {
                    String id = suppliersRef.push().getKey();
                    suppliersRef.child(id).setValue(
                            new Supplier(
                                    id,
                                    etName.getText().toString(),
                                    etPhone.getText().toString(),
                                    etEmail.getText().toString()
                            )
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

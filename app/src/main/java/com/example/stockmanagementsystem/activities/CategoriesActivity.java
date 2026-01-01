package com.example.stockmanagementsystem.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.adapters.CategoryAdapter;
import com.example.stockmanagementsystem.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private DatabaseReference categoriesRef;
    private List<Category> categoryList;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);

        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        RecyclerView recyclerView = findViewById(R.id.categoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton btnAdd = findViewById(R.id.btnAddCategory);

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, categoriesRef);
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> addCategory());

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Category c = s.getValue(Category.class);
                    if (c != null) {
                        categoryList.add(new Category(s.getKey(), c.getName()));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoriesActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory() {
        EditText et = new EditText(this);
        et.setHint("Category name");

        new AlertDialog.Builder(this)
                .setTitle("Add Category")
                .setView(et)
                .setPositiveButton("Add", (d, w) -> {
                    String id = categoriesRef.push().getKey();
                    if (id != null) {
                        categoriesRef.child(id)
                                .setValue(new Category(id, et.getText().toString()));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

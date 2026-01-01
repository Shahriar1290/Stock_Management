package com.example.stockmanagementsystem.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.adapters.CategoryAdapter;
import com.example.stockmanagementsystem.models.Category;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    DatabaseReference categoriesRef;
    List<Category> categoryList;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        RecyclerView recycler = findViewById(R.id.categoriesRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, categoriesRef);
        recycler.setAdapter(adapter);

        findViewById(R.id.btnAddCategory).setOnClickListener(v -> addCategory());

        categoriesRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                categoryList.clear();
                for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                    Category c = s.getValue(Category.class);
                    if (c != null) {
                        categoryList.add(new Category(s.getKey(), c.getName()));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
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
                    categoriesRef.child(id).setValue(
                            new Category(id, et.getText().toString())
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

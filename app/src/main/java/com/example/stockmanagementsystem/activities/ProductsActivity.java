package com.example.stockmanagementsystem.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.adapters.ProductAdapter;
import com.example.stockmanagementsystem.models.Category;
import com.example.stockmanagementsystem.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private DatabaseReference productsRef;
    private DatabaseReference categoriesRef;

    private RecyclerView recyclerView;
    private EditText etSearch;

    private final List<Product> productList = new ArrayList<>();
    private final List<Product> filteredList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        productsRef = FirebaseDatabase.getInstance().getReference("products");
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        recyclerView = findViewById(R.id.productsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(filteredList, productsRef, categoriesRef);
        recyclerView.setAdapter(adapter);

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
        });

        loadCategories();
        loadProducts();

        findViewById(R.id.btnAddProduct).setOnClickListener(v -> showAddDialog());
    }

    private void loadCategories() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsActivity.this,
                        "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Product p = s.getValue(Product.class);
                    if (p != null) {
                        productList.add(new Product(
                                s.getKey(),
                                p.getName(),
                                p.getPrice(),
                                p.getQuantity(),
                                p.getCategoryId()
                        ));
                    }
                }
                filterProducts(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsActivity.this,
                        "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            for (Product p : productList) {
                if (p.getName().toLowerCase()
                        .contains(query.toLowerCase())) {
                    filteredList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    //ADD PRODUCT

    private void showAddDialog() {
        if (categoryList.isEmpty()) {
            Toast.makeText(this,
                    "Please add categories first", Toast.LENGTH_SHORT).show();
            return;
        }

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_product, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPrice = view.findViewById(R.id.etPrice);
        EditText etQty = view.findViewById(R.id.etQty);
        Spinner spCategory = view.findViewById(R.id.spCategory);

        List<String> categoryNames = new ArrayList<>();
        for (Category c : categoryList) {
            categoryNames.add(c.getName());
        }

        spCategory.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categoryNames
        ));

        new AlertDialog.Builder(this)
                .setTitle("Add Product")
                .setView(view)
                .setPositiveButton("Add", (d, w) -> {

                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String qtyStr = etQty.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
                        Toast.makeText(this,
                                "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int catIndex = spCategory.getSelectedItemPosition();
                    String categoryId = categoryList.get(catIndex).getId();

                    String id = productsRef.push().getKey();

                    Product product = new Product(
                            id,
                            name,
                            Double.parseDouble(priceStr),
                            Integer.parseInt(qtyStr),
                            categoryId
                    );

                    productsRef.child(id).setValue(product);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

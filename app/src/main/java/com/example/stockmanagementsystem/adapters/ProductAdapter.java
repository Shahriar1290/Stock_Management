package com.example.stockmanagementsystem.adapters;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.models.Category;
import com.example.stockmanagementsystem.models.Product;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private static final int LOW_STOCK_THRESHOLD = 3;

    private final List<Product> productList;
    private final DatabaseReference productsRef;
    private final DatabaseReference categoriesRef;

    public ProductAdapter(List<Product> productList,
                          DatabaseReference productsRef,
                          DatabaseReference categoriesRef) {
        this.productList = productList;
        this.productsRef = productsRef;
        this.categoriesRef = categoriesRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Product p = productList.get(position);

        h.txtName.setText(p.getName());
        h.txtPrice.setText("Price: " + p.getPrice());

        if (p.getQuantity() == 0) {
            h.txtQty.setText("Qty: 0");
            h.txtQty.setTextColor(Color.RED);
            h.txtOutOfStock.setVisibility(View.VISIBLE);
            h.itemView.setBackgroundColor(Color.parseColor("#FFEBEE")); // light red
        } else if (p.getQuantity() <= LOW_STOCK_THRESHOLD) {
            h.txtQty.setText("Qty: " + p.getQuantity() + "  ⚠ LOW STOCK");
            h.txtQty.setTextColor(Color.RED);
            h.txtOutOfStock.setVisibility(View.GONE);
            h.itemView.setBackgroundColor(Color.parseColor("#FFF3E0")); // light orange
        } else {
            h.txtQty.setText("Qty: " + p.getQuantity());
            h.txtQty.setTextColor(Color.BLACK);
            h.txtOutOfStock.setVisibility(View.GONE);
            h.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (p.getCategoryId() != null) {
            categoriesRef.child(p.getCategoryId())
                    .child("name")
                    .get()
                    .addOnSuccessListener(s ->
                            h.txtCategory.setText("Category: " + s.getValue(String.class))
                    );
        } else {
            h.txtCategory.setText("Category: N/A");
        }

        h.btnDelete.setOnClickListener(v ->
                productsRef.child(p.getId()).removeValue()
        );

        h.btnEdit.setOnClickListener(v ->
                showEditDialog(h.itemView, p)
        );
    }

    private void showEditDialog(View parent, Product p) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_add_product, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPrice = view.findViewById(R.id.etPrice);
        EditText etQty = view.findViewById(R.id.etQty);
        Spinner spCategory = view.findViewById(R.id.spCategory);

        etName.setText(p.getName());
        etPrice.setText(String.valueOf(p.getPrice()));
        etQty.setText(String.valueOf(p.getQuantity()));

        List<Category> categories = new ArrayList<>();
        List<String> categoryNames = new ArrayList<>();

        categoriesRef.get().addOnSuccessListener(snapshot -> {
            int selectedIndex = 0;
            int index = 0;

            for (var s : snapshot.getChildren()) {
                Category c = s.getValue(Category.class);
                if (c != null) {
                    categories.add(new Category(s.getKey(), c.getName()));
                    categoryNames.add(c.getName());

                    if (s.getKey().equals(p.getCategoryId())) {
                        selectedIndex = index;
                    }
                    index++;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    parent.getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryNames
            );
            spCategory.setAdapter(adapter);
            spCategory.setSelection(selectedIndex);
        });

        new AlertDialog.Builder(parent.getContext())
                .setTitle("Edit Product")
                .setView(view)
                .setPositiveButton("Update", (d, w) -> {
                    p.setName(etName.getText().toString());
                    p.setPrice(Double.parseDouble(etPrice.getText().toString()));
                    p.setQuantity(Integer.parseInt(etQty.getText().toString()));

                    int catIndex = spCategory.getSelectedItemPosition();
                    if (!categories.isEmpty() && catIndex >= 0) {
                        p.setCategoryId(categories.get(catIndex).getId());
                    }

                    productsRef.child(p.getId()).setValue(p);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtPrice, txtQty, txtCategory, txtOutOfStock;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtOutOfStock = itemView.findViewById(R.id.txtOutOfStock);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

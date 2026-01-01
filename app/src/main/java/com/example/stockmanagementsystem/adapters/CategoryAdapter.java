package com.example.stockmanagementsystem.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.models.Category;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private DatabaseReference categoriesRef;

    public CategoryAdapter(List<Category> categoryList, DatabaseReference categoriesRef) {
        this.categoryList = categoryList;
        this.categoriesRef = categoriesRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category c = categoryList.get(position);
        holder.txtName.setText(c.getName());

        holder.btnDelete.setOnClickListener(v ->
                categoriesRef.child(c.getId()).removeValue()
        );

        holder.btnEdit.setOnClickListener(v ->
                showEditDialog(holder.itemView, c)
        );
    }

    private void showEditDialog(View view, Category c) {
        EditText et = new EditText(view.getContext());
        et.setText(c.getName());

        new AlertDialog.Builder(view.getContext())
                .setTitle("Edit Category")
                .setView(et)
                .setPositiveButton("Update", (d, w) -> {
                    c.setName(et.getText().toString());
                    categoriesRef.child(c.getId()).setValue(c);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

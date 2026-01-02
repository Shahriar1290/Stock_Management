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
import com.example.stockmanagementsystem.models.Supplier;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    private final List<Supplier> suppliers;
    private final DatabaseReference suppliersRef;

    public SupplierAdapter(List<Supplier> suppliers, DatabaseReference suppliersRef) {
        this.suppliers = suppliers;
        this.suppliersRef = suppliersRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supplier, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Supplier s = suppliers.get(pos);

        h.txtName.setText(s.getName());
        h.txtPhone.setText("Phone: " + s.getPhone());
        h.txtEmail.setText("Email: " + s.getEmail());

        h.btnDelete.setOnClickListener(v ->
                suppliersRef.child(s.getId()).removeValue()
        );

        h.btnEdit.setOnClickListener(v ->
                showEditDialog(h.itemView, s)
        );
    }

    private void showEditDialog(View parent, Supplier s) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_add_supplier, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etEmail = view.findViewById(R.id.etEmail);

        etName.setText(s.getName());
        etPhone.setText(s.getPhone());
        etEmail.setText(s.getEmail());

        new AlertDialog.Builder(parent.getContext())
                .setTitle("Edit Supplier")
                .setView(view)
                .setPositiveButton("Update", (d, w) -> {
                    s.setName(etName.getText().toString());
                    s.setPhone(etPhone.getText().toString());
                    s.setEmail(etEmail.getText().toString());
                    suppliersRef.child(s.getId()).setValue(s);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return suppliers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPhone, txtEmail;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

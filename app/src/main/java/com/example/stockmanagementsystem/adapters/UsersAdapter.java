package com.example.stockmanagementsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanagementsystem.R;
import com.example.stockmanagementsystem.models.User;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Collections;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    List<User> list;
    Context context;

    public UsersAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.row_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        User u = list.get(position);

        h.name.setText(u.name);
        h.email.setText(u.email);
        h.role.setText("Role: " + u.role);

        h.delete.setOnClickListener(v -> deleteUser(u));
    }

    private void deleteUser(User user) {

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.uid)
                .removeValue();

        FirebaseFunctions.getInstance()
                .getHttpsCallable("deleteUserByUid")
                .call(Collections.singletonMap("uid", user.uid));

        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, role;
        Button delete;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.txtName);
            email = v.findViewById(R.id.txtEmail);
            role = v.findViewById(R.id.txtRole);
            delete = v.findViewById(R.id.btnDelete);
        }
    }
}

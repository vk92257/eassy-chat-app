package com.eassychat.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eassychat.Chatmodule.ChatActivity;
import com.eassychat.R;
import com.eassychat.response.AllUsers;
import com.eassychat.retorfit.APIConstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUsersViewHolder> {

    ArrayList<AllUsers> allUsers;
    Context ctx;

    public AllUserAdapter(ArrayList<AllUsers> allUsers, Context ctx) {
        this.allUsers = allUsers;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_all_user, parent, false);
        return new AllUsersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersViewHolder holder, int position) {
        AllUsers allUser = allUsers.get(position);
        holder.name.setText(allUser.getName());
        Picasso.get()
                .load(allUser.getProfilePic())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(holder.profilePic);
        holder.rootLayout.setOnClickListener(view -> {
            Intent intent = new Intent(ctx, ChatActivity.class).putExtra(APIConstance.PAPER_ID, allUser.getId())
                    .putExtra(APIConstance.NAME, allUser.getName()).
                    putExtra(APIConstance.PROFILE_PIC, allUser.getProfilePic());
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return allUsers.size();
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePic;
        private TextView name;
        private LinearLayout rootLayout;

        public AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
            rootLayout = itemView.findViewById(R.id.userRootLayout);
        }
    }
}

package com.rezerve_sepeti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PostHolder> {

    private ArrayList<String> businessTableList;

    public RecyclerAdapter(ArrayList<String> businessTableList) {
        this.businessTableList = businessTableList;
    }

    @NonNull
    @NotNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_user_dashboard,parent,false);


        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerAdapter.PostHolder holder, int position) {
        holder.tab1.setText(businessTableList.get(position));
        holder.tab2.setText(businessTableList.get(position));
    }

    @Override
    public int getItemCount() {

        return businessTableList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        TextView tab1;
        TextView tab2;

        public PostHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tab1= itemView.findViewById(R.id.tab1);
            tab2 = itemView.findViewById(R.id.tab2);


        }
    }
}

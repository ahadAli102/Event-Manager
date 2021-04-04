package com.example.softwareprojectv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.softwareprojectv.R;
import com.example.softwareprojectv.model.ShowActivitiesModel;

import java.util.List;

public class ShowActivitiesAdapter extends RecyclerView.Adapter<ShowActivitiesAdapter.ShowActivitiesViewHolder>{
    private List<ShowActivitiesModel> modelList;
    private Context context;

    public ShowActivitiesAdapter(Context context) {
        this.context = context;
    }

    public void setModelList(List<ShowActivitiesModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ShowActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_activities_list,parent,false);
        return new ShowActivitiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowActivitiesViewHolder holder, int position) {
        holder.textView.setText(modelList.get(position).getName());
        Glide.with(context).load(modelList.get(position).getUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ShowActivitiesViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        public ShowActivitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_activities_list_text);
            imageView = itemView.findViewById(R.id.single_activities_list_image);
        }
    }

}

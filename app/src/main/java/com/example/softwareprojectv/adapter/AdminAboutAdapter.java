package com.example.softwareprojectv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.softwareprojectv.R;
import com.example.softwareprojectv.model.AdminAbout;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdminAboutAdapter extends RecyclerView.Adapter<AdminAboutAdapter.AboutHolder>{
    private List<AdminAbout> aboutList;
    private Context context;
    private final ClickInterface clickInterface;
    public AdminAboutAdapter(Context context, ClickInterface clickInterface) {
        this.context = context;
        this.clickInterface = clickInterface;
    }
    public void setAboutList(List<AdminAbout> aboutList) {
        this.aboutList = aboutList;
    }
    public AboutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_admin_about_layout,parent,false);
        return new AboutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutHolder holder, int position) {
        holder.nameText.setText(aboutList.get(position).getName());
        holder.emailText.setText(aboutList.get(position).getEmail());
        holder.designationText.setText(aboutList.get(position).getDesignation());
        Glide.with(context).load(aboutList.get(position).getImageUrl()).centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return aboutList.size();
    }

    public class AboutHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView nameText,designationText,emailText;
        CircleImageView imageView;
        public AboutHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.adminAboutSingleNameId);
            designationText = itemView.findViewById(R.id.adminAboutSingleDesignationId);
            emailText = itemView.findViewById(R.id.adminAboutSingleEmailId);
            imageView = itemView.findViewById(R.id.adminAboutSingleImageId);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            clickInterface.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            clickInterface.onLongItemClick(getAdapterPosition());
            return false;
        }
    }

    public interface ClickInterface {
        // for on Click....
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

}
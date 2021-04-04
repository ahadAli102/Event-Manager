package com.example.softwareprojectv.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.model.Image;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{
    private java.util.List<Image> imageList;

    public ImageAdapter(java.util.List<Image> imageList) {
        this.imageList = imageList;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(imageList.get(position).getImageName());
        holder.singleImage.setImageURI(imageList.get(position).getUri());
        holder.deleteImage.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                imageList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView singleImage,deleteImage;
        private android.widget.TextView textView;

        public MyViewHolder(@androidx.annotation.NonNull android.view.View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.singleImageTextId);
            singleImage= itemView.findViewById(R.id.singleImageId);
            deleteImage= itemView.findViewById(R.id.singleDeleteButtonId);
        }
    }
}

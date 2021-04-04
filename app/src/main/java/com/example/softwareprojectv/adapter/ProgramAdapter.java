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
import com.example.softwareprojectv.model.Program;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.MyProgramViewHolder>{
    private List<Program> programList;
    private Context context;
    private final ClickInterface clickInterface;

    public ProgramAdapter(Context context, ClickInterface clickInterface) {
        this.context = context;
        this.clickInterface = clickInterface;
    }
    public void setProgramList(List<Program> programList) {
        this.programList = programList;
    }

    @NonNull
    @Override
    public MyProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_program_layout,parent,false);
        return new MyProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProgramViewHolder holder, int position) {
        holder.titleText.setText(programList.get(position).getTitle());
        String startDate ="Starts from "+ programList.get(position).getStartDate()+" at "+programList.get(position).getStartTime();
        String endDate ="Ends at "+ programList.get(position).getEndDate()+" at "+programList.get(position).getEndTime();
        holder.startDate.setText(startDate);
        holder.endDate.setText(endDate);
        Glide.with(context).load(programList.get(position).getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public class MyProgramViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView titleText;
        private TextView startDate;
        private TextView endDate;
        private CircleImageView imageView;
        public MyProgramViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.single_programTitleTextViewId);
            startDate = itemView.findViewById(R.id.single_programStartTextViewId);
            endDate = itemView.findViewById(R.id.single_programEndTextViewId);
            imageView = itemView.findViewById(R.id.single_programImageViewId);
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
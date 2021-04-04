package com.example.softwareprojectv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.model.ProgramApplicants;

import java.util.List;

public class ProgramApplicantsAdapter extends RecyclerView.Adapter<ProgramApplicantsAdapter.MyProgramApplicantViewHolder>{
    List<ProgramApplicants> applicantsList;

    public void setApplicantsList(List<ProgramApplicants> applicantsList) {
        this.applicantsList = applicantsList;
    }

    @NonNull
    @Override
    public MyProgramApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_program_applicants_layout,parent,false);
        return new MyProgramApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProgramApplicantViewHolder holder, int position) {
        holder.nameText.setText(applicantsList.get(position).getName());
        holder.emailText.setText(applicantsList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return applicantsList.size();
    }

    class MyProgramApplicantViewHolder extends RecyclerView.ViewHolder{
        private TextView nameText,emailText;
        public MyProgramApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.single_program_applicant_name);
            emailText = itemView.findViewById(R.id.single_program_applicant_email);
        }
    }

}

package com.example.softwareprojectv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.model.Activities;
import com.example.softwareprojectv.model.ShowActivitiesModel;

import java.util.ArrayList;
import java.util.List;

public class UserActivitiesAdapter extends RecyclerView.Adapter<UserActivitiesAdapter.UserViewHolder> {
    private List<Activities> userList;
    private final ClickInterface clickInterface;
    private Context context;

    public UserActivitiesAdapter(ClickInterface clickInterface,Context context) {
        this.clickInterface = clickInterface;
        this.context=context;
    }

    public void getUserList(List<Activities> userList){
        this.userList= userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.single_activities,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.name.setText("Name: "+userList.get(position).getActivityName());
        holder.description.setText("Description: "+userList.get(position).getActivityDescription());
        holder.setChildRecycler(holder.childRecyclerView,userList,position);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView name,description;
        private RecyclerView childRecyclerView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.singleActivitiesNameId);
            description= itemView.findViewById(R.id.singleActivitiesDescriptionId);
            childRecyclerView = itemView.findViewById(R.id.single_activities_recyclerView);
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
        public void setChildRecycler(RecyclerView childRecyclerView, List<Activities> userList,int position) {
            List<ShowActivitiesModel> modelList;
            ShowActivitiesAdapter adapter;
            Activities model = userList.get(position);
            modelList = new ArrayList();
            for (int i = 0; i < model.getImageNameList().size(); i++) {
                ShowActivitiesModel showActivitiesModel = new ShowActivitiesModel(model.getImageNameList().get(i)
                        ,model.getImageLinkList().get(i));
                modelList.add(showActivitiesModel);
            }
            adapter = new ShowActivitiesAdapter(context);
            adapter.setModelList(modelList);
            childRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            childRecyclerView.setHasFixedSize(true);
            childRecyclerView.setAdapter(adapter);
        }
    }

    public interface ClickInterface {
        // for on Click....
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

}
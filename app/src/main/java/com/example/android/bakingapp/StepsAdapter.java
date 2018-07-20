package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Khare on 08-Apr-18.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsTextViewHolder> {

    private ArrayList<String> shortDescription;

    private StepsItemClickListener mClickListener;

    public void setStepsList(ArrayList<String> description) {
        shortDescription = description;
    }

    @NonNull
    @Override
    public StepsTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.steps_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        StepsTextViewHolder textViewHolder = new StepsTextViewHolder(view);

        return textViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StepsTextViewHolder holder, int position) {
        if(shortDescription != null) {
            String des = shortDescription.get(position);
            holder.stepDescriptionTextView.setText(des);
        }
    }


    @Override
    public int getItemCount() {
        if (shortDescription == null) {
            return 0;
        }
        return shortDescription.size();
    }

    public class StepsTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView stepDescriptionTextView;

        public StepsTextViewHolder(View itemView) {
            super(itemView);
            stepDescriptionTextView = itemView.findViewById(R.id.tv_steps_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
    public void setmClickListener(StepsAdapter.StepsItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface StepsItemClickListener {
        void onItemClick(View view, int position);
    }
}

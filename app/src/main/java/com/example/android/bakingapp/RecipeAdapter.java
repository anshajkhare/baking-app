package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Khare on 08-Apr-18.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.TextViewHolder> {

    private String[] recipeNamesList;
    private int[] servingList;

    private RecipeClickListener mClickListener;

    RecipeAdapter() {

    }

    public void setRecipeList(String[] recipe, int[] serving) {
        recipeNamesList = recipe;
        servingList = serving;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeAdapter.TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        TextViewHolder textViewHolder = new TextViewHolder(view);

        return textViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.TextViewHolder holder, int position) {
        if (recipeNamesList != null) {
            String recipe = recipeNamesList[position];
            int serving = servingList[position];
            holder.recipeNameView.setText(recipe);
            holder.servingView.setText("Servings: "+ String.valueOf(serving));
        }
    }

    @Override
    public int getItemCount() {
        if (recipeNamesList == null) return 0;
        return recipeNamesList.length;
    }

    class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView recipeNameView;
        TextView servingView;

        public TextViewHolder(View itemView) {
            super(itemView);

            recipeNameView = itemView.findViewById(R.id.tv_recipe_name);
            servingView = itemView.findViewById(R.id.tv_serving);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) {
                mClickListener.onRecipeClick(v, getAdapterPosition());
            }
        }
    }

    public void setmClickListener(RecipeClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface RecipeClickListener {
        void onRecipeClick(View view, int position);
    }
}

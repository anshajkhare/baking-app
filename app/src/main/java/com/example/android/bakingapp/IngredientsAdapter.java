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

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsTextViewHolder> {

    private int[] mQuantity;
    private String[] mMeasure;
    private String[] mIngredient;

    @NonNull
    @Override
    public IngredientsTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.ingredient_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        IngredientsTextViewHolder textViewHolder = new IngredientsTextViewHolder(view);

        return textViewHolder;
    }

    public void setIngredientList(int[] quantity, String[] measure, String[] ingredient) {
        mQuantity = quantity;
        mMeasure = measure;
        mIngredient = ingredient;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsTextViewHolder holder, int position) {
        if (mIngredient != null) {
            String ingredient = mIngredient[position];
            int quantity = mQuantity[position];
            String measure = mMeasure[position];
            String textSet = quantity + " " + measure + " of " + ingredient;
            holder.ingredientsView.setText(textSet);
        }
    }

    @Override
    public int getItemCount() {
        if(mIngredient == null) {
            return 0;
        }
        return mIngredient.length;
    }

    public class IngredientsTextViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientsView;

        public IngredientsTextViewHolder(View itemView) {
            super(itemView);

            ingredientsView = itemView.findViewById(R.id.tv_ingredients_name);
        }
    }
}

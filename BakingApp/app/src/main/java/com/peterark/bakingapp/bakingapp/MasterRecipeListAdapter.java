package com.peterark.bakingapp.bakingapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peterark.bakingapp.bakingapp.databinding.ListItemMasterRecipeListBinding;

import java.util.List;

/**
 * Created by PETER on 29/10/2017.
 */

public class MasterRecipeListAdapter extends RecyclerView.Adapter<MasterRecipeListAdapter.RecipeListViewHolder>{


    private List<RecipeItem> mItemList;
    private final OnRecipeClickHandler mHandler;


    public MasterRecipeListAdapter(List<RecipeItem> itemList, OnRecipeClickHandler handler){
        this.mItemList = itemList;
        this.mHandler  = handler;
    }

    public void setItemList(List<RecipeItem> itemList){
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public RecipeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context         = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId  = R.layout.list_item_master_recipe_list;
        ListItemMasterRecipeListBinding binding = DataBindingUtil.inflate(inflater,layoutId,parent,false);

        return new RecipeListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecipeListViewHolder holder, int position) {
        RecipeItem item = mItemList.get(position);

        holder.mBinding.recipeNameTextview.setText(item.recipeName);
        holder.mBinding.recipeServingsTextview.setText(String.valueOf(item.recipeServings));
    }

    @Override
    public int getItemCount() {
        return mItemList != null ? mItemList.size() : 0;
    }

    public interface OnRecipeClickHandler{
        void onRecipeClick(RecipeItem recipeId);
    }

    public class RecipeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ListItemMasterRecipeListBinding mBinding;

        private RecipeListViewHolder(ListItemMasterRecipeListBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            RecipeItem item = mItemList.get(adapterPosition);
            mHandler.onRecipeClick(item);
        }
    }
}

package com.peterark.bakingapp.bakingapp.panels.recipeDetail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.databinding.ListItemRecipeDetailStepBinding;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeStep;

import java.util.List;

/**
 * Created by PETER on 7/11/2017.
 */

public class RecipeDetailFragmentStepAdapter extends RecyclerView.Adapter<RecipeDetailFragmentStepAdapter.RecipeStepListViewHolder>{


    private List<RecipeStep> mItemList;
    private final OnRecipeStepClickHandler mHandler;


    public RecipeDetailFragmentStepAdapter(List<RecipeStep> itemList, RecipeDetailFragmentStepAdapter.OnRecipeStepClickHandler handler){
        this.mItemList = itemList;
        this.mHandler  = handler;
    }

    public void setItemList(List<RecipeStep> itemList){
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public RecipeDetailFragmentStepAdapter.RecipeStepListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context         = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId  = R.layout.list_item_recipe_detail_step;
        ListItemRecipeDetailStepBinding binding = DataBindingUtil.inflate(inflater,layoutId,parent,false);

        return new RecipeDetailFragmentStepAdapter.RecipeStepListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecipeDetailFragmentStepAdapter.RecipeStepListViewHolder holder, int position) {
        RecipeStep item = mItemList.get(position);

        holder.mBinding.stepNumberTextview.setText(position == 0 ? "I" : String.valueOf(position)); // Assuming that the one in the position zero is the Introduction "I".
        holder.mBinding.stepShortDescriptionTextview.setText(item.stepShortDescription);
    }

    @Override
    public int getItemCount() {
        return mItemList != null ? mItemList.size() : 0;
    }

    public interface OnRecipeStepClickHandler{
        void onRecipeStepClick(int recipeStep);
    }

    public class RecipeStepListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ListItemRecipeDetailStepBinding mBinding;


        private RecipeStepListViewHolder(ListItemRecipeDetailStepBinding binding){
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            RecipeStep item = mItemList.get(adapterPosition);
            mHandler.onRecipeStepClick(item.stepId);
        }
    }
}

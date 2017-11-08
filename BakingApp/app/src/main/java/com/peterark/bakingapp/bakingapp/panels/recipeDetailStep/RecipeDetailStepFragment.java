package com.peterark.bakingapp.bakingapp.panels.recipeDetailStep;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.databinding.FragmentRecipeDetailStepBinding;

/**
 * Created by PETER on 7/11/2017.
 */

public class RecipeDetailStepFragment extends Fragment {
    FragmentRecipeDetailStepBinding mBinding;

    // Empty Constructor
    public RecipeDetailStepFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail_step, container, false);
        return mBinding.getRoot();
    }
}

package com.backing.backingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.RecipeDto;
import com.backing.backingapp.adapters.RecipesAdapter;

import java.util.ArrayList;
import java.util.List;

public class MasterRecipesFragment extends Fragment {
    private static final String RECIPES_KEY = "RECIPES_KEY";
    private List<RecipeDto> recipes;
    private RecipesAdapter adapter = new RecipesAdapter();


    public MasterRecipesFragment() {
    }

    public void setRecipes(List<RecipeDto> recipes) {
        this.recipes = recipes;
        adapter.swapAdapter(recipes);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(RECIPES_KEY, new ArrayList<>(recipes));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            OnRecipeClickListener mListener = (OnRecipeClickListener) context;
            if (adapter != null) {
                adapter.setListener(mListener);
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " need to implement OnRecipeClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_recipes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mManager);
        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPES_KEY)) {
            recipes = (ArrayList<RecipeDto>) savedInstanceState.getSerializable(RECIPES_KEY);
        }

        if (recipes != null){
            adapter.swapAdapter(recipes);
        }
        return view;
    }

    public interface OnRecipeClickListener {
        void onRecipeSelected(int id);
    }
}

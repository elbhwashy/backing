package com.backing.backingapp.fragments;

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
import com.backing.backingapp.adapters.IngredientsAdapter;

public class IngredientsFragment extends Fragment {
    public static final String INGREDIENTS_KEY = "INGREDIENTS_KEY";
    private IngredientsAdapter adapter = new IngredientsAdapter();
    private RecipeDto recipeRealm;

    public IngredientsFragment() {
    }

    public void setIngredients(RecipeDto realm) {
        this.recipeRealm = realm;
        adapter.swapAdapter(recipeRealm.getIngredients());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(INGREDIENTS_KEY, recipeRealm);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_ingredients);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mManager);
        if (savedInstanceState != null && savedInstanceState.containsKey(INGREDIENTS_KEY)) {
            recipeRealm = (RecipeDto) savedInstanceState.getSerializable(INGREDIENTS_KEY);
        }
        if (recipeRealm != null){
            adapter.swapAdapter(recipeRealm.getIngredients());
        }
        return view;
    }
}

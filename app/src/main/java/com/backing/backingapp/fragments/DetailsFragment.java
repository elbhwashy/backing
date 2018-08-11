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
import android.widget.TextView;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.DetailDto;
import com.backing.backingapp.data.dto.DetailDto.DetailType;
import com.backing.backingapp.data.dto.RecipeDto;
import com.backing.backingapp.data.dto.StepDto;
import com.backing.backingapp.adapters.DetailsAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {
    public static final String RECIPE_KEY = "RECIPE";
    private DetailsAdapter adapter = new DetailsAdapter();
    private RecipeDto recipeRealm;

    public DetailsFragment() {
    }

    public void setDetailsAndSteps(RecipeDto realm) {
        recipeRealm = realm;
        adapter.swapAdapter(transferRecipeToList(recipeRealm));
    }

    private List<DetailDto> transferRecipeToList(RecipeDto recipeRealm) {
        List<DetailDto> details = new ArrayList<>();
        String textIngredients = "Ingredients on " + recipeRealm.getServings() + " portions";
        details.add(new DetailDto.Builder().setType(DetailType.INGREDIENTS).setText(textIngredients).build());
        int i = 1;

        for (StepDto step : recipeRealm.getSteps()) {
            DetailDto.Builder builder = new DetailDto.Builder();
            builder.setId(step.getId())
                    .setText("Step " + i + ": " + step.getShortDescription())
                    .setType(DetailType.STEP)
                    .setIsVideo(step.getVideoURL() != null && !step.getVideoURL().isEmpty());
            details.add(builder.build());
            i++;
        }
        return details;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(RECIPE_KEY, recipeRealm);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            OnClickListener listener = (OnClickListener) context;
            if (adapter != null) {
                adapter.setListener(listener);
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "need to implement OnDetailOrStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_details, container, false);

        LinearLayoutManager mManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_details);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mManager);
        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_KEY)) {
            recipeRealm = ((RecipeDto) savedInstanceState.getSerializable(RECIPE_KEY));
        }

        if (recipeRealm != null) {
            adapter.swapAdapter(transferRecipeToList(recipeRealm));
        }
        return view;
    }

    public interface OnClickListener {
        void onSelected(DetailDto.DetailType type, int stepId, boolean haveVideo);
    }
}

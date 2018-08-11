package com.backing.backingapp.data;

import com.backing.backingapp.data.RecipeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestService {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<RecipeResponse>> getRecipes();
}

package com.backing.backingapp.data.dto;

import com.backing.backingapp.data.RecipeResponse;
import com.backing.backingapp.data.realm.IngredientRealm;

import java.io.Serializable;

public class IngredientDto implements Serializable {
    private int id;
    private double ingredientQuantity;
    private String ingredientMeasure;
    private String ingredient;

    public IngredientDto(double ingredientQuantity, String ingredientMeasure, String ingredient) {
        this.ingredientQuantity = ingredientQuantity;
        this.ingredientMeasure = ingredientMeasure;
        this.ingredient = ingredient;
    }

    IngredientDto(RecipeResponse.Ingredient ingredient) {
        this.ingredientQuantity = ingredient.getIngredientQuantity();
        this.ingredientMeasure = ingredient.getIngredientMeasure();
        this.ingredient = ingredient.getIngredient();
    }

    IngredientDto(IngredientRealm ingredient) {
        this.id = ingredient.getId();
        this.ingredientQuantity = ingredient.getIngredientQuantity();
        this.ingredientMeasure = ingredient.getIngredientMeasure();
        this.ingredient = ingredient.getIngredient();
    }

    public int getId() {
        return id;
    }

    public double getIngredientQuantity() {
        return ingredientQuantity;
    }

    public String getIngredientMeasure() {
        return ingredientMeasure;
    }

    public String getIngredient() {
        return ingredient;
    }
}

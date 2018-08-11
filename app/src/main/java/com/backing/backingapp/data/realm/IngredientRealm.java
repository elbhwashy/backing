package com.backing.backingapp.data.realm;

import com.backing.backingapp.data.RecipeResponse.Ingredient;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class IngredientRealm extends RealmObject implements Serializable{
    @PrimaryKey
    private int id;
    private double quantity;
    private String measure;
    private String ingredient;

    public IngredientRealm() {
    }

    public IngredientRealm(Ingredient ingredient) {
        this.quantity = ingredient.getIngredientQuantity();
        this.measure = ingredient.getIngredientMeasure();
        this.ingredient = ingredient.getIngredient();
        this.id = ((int) quantity) + (measure + ingredient).hashCode();
    }

    public double getIngredientQuantity() {
        return quantity;
    }

    public String getIngredientMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getId() {
        return id;
    }
}

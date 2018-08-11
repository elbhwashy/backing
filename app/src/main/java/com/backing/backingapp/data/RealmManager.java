package com.backing.backingapp.data;

import com.backing.backingapp.data.realm.IngredientRealm;
import com.backing.backingapp.data.realm.RecipeRealm;
import com.backing.backingapp.data.realm.StepRealm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmManager {
    private Realm realmInstance;

    private Realm getRealmInstance() {
        if (realmInstance == null || realmInstance.isClosed()) {
            realmInstance = Realm.getDefaultInstance();
        }
        return realmInstance;
    }

    public void deleteFromRealm(Class<? extends RealmObject> entityRealmClass, String id) {
        Realm realm = getRealmInstance();
        RealmObject entity = realm.where(entityRealmClass).equalTo("id", id).findFirst();
        if (entity != null) {
            realm.executeTransaction(realm1 -> entity.deleteFromRealm());
        }
        realm.close();
    }

    public void saveRecipesFromNet(List<RecipeResponse> recipes) {
        Realm realm = getRealmInstance();
        for (RecipeResponse recipe : recipes) {
            realm.executeTransaction(realm1 -> realm1.insertOrUpdate(new RecipeRealm(recipe)));
        }
        realm.close();
    }

    public RealmResults<RecipeRealm> getRecipes
            (RealmChangeListener<RealmResults<RecipeRealm>> callback) {
        RealmResults<RecipeRealm> results = getRealmInstance().where(RecipeRealm.class).findAllAsync();
        results.addChangeListener(callback);
        return results;
    }

    public RealmResults<RecipeRealm> getRecipes() {
        return getRealmInstance().where(RecipeRealm.class).findAllAsync();
    }

    public RealmResults<StepRealm> getSteps() {
        return getRealmInstance().where(StepRealm.class).findAllAsync();
    }

    public RecipeRealm getRecipeById(int id, RealmChangeListener<RecipeRealm> realmChangeListener) {
        RecipeRealm recipeRealm = getRealmInstance().where(RecipeRealm.class).equalTo("id", id).findFirstAsync();
        recipeRealm.addChangeListener(realmChangeListener);
        return recipeRealm;
    }

    public IngredientRealm getIngredientById(int id) {
        return getRealmInstance().where(IngredientRealm.class)
                .equalTo("id", id).findFirstAsync();
    }

    public StepRealm getStepById(int id) {
        return getRealmInstance()
                .where(StepRealm.class).equalTo("id", id).findFirst();
    }

}

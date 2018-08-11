package com.backing.backingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.backing.backingapp.R;
import com.backing.backingapp.data.dto.WidgetDto;
import com.backing.backingapp.data.realm.IngredientRealm;
import com.backing.backingapp.data.realm.RecipeRealm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.backing.backingapp.widget.Widget.updateAppWidget;

public class IngredientsConfigureActivity extends Activity {

    private static final String NAME = "com.backing.backingapp.widget.Ingredients";
    private static final String PREFIX_KEY = "appwidget_";
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private RecyclerView recyclerView;

    public IngredientsConfigureActivity() {
        super();
    }

    static void saveTitlePref(Context context, int appWidgetId, Set<String> text) {
        SharedPreferences.Editor preference = context.getSharedPreferences(NAME, 0).edit();
        preference.putStringSet(PREFIX_KEY + appWidgetId, text);
        preference.apply();
    }

    static Set<String> loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences preference = context.getSharedPreferences(NAME, 0);
        Set<String> titleValue = preference.getStringSet(PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return Collections.emptySet();
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor preference = context.getSharedPreferences(NAME, 0).edit();
        preference.remove(PREFIX_KEY + appWidgetId);
        preference.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Realm.init(this);
        setResult(RESULT_CANCELED);
        RealmResults<RecipeRealm> all = Realm.getDefaultInstance().where(RecipeRealm.class).findAll();
        List<WidgetDto> list = new ArrayList<>();
        for (RecipeRealm recipeRealm : all) {
            list.add(new WidgetDto(recipeRealm.getId(), recipeRealm.getName()));
        }
        setContentView(R.layout.ingredients_configure);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_widget_config);
        WidgetAdapter widgetAdapter = new WidgetAdapter(list, new Widgetable() {
            @Override
            public void clickEvent(int id) {
                final Context context = IngredientsConfigureActivity.this;
                RecipeRealm recipe = Realm.getDefaultInstance().where(RecipeRealm.class).equalTo("id", id).findFirst();
                Set<String> strings = new HashSet<>();
                for (IngredientRealm ingredient : recipe.getIngredients()) {
                    strings.add(ingredient.getIngredient() + " " + ingredient.getIngredientQuantity() + " " + ingredient.getIngredientMeasure());
                }
                saveTitlePref(context, appWidgetId, strings);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                updateAppWidget(context, appWidgetManager, appWidgetId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
        recyclerView.setAdapter(widgetAdapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    public interface Widgetable {
        void clickEvent(int id);
    }
}


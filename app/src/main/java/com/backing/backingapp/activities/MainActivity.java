package com.backing.backingapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.backing.backingapp.R;
import com.backing.backingapp.data.RealmManager;
import com.backing.backingapp.data.RestService;
import com.backing.backingapp.data.ServiceGenerator;
import com.backing.backingapp.data.RecipeResponse;
import com.backing.backingapp.data.dto.DetailDto;
import com.backing.backingapp.data.dto.RecipeDto;
import com.backing.backingapp.data.dto.StepDto;
import com.backing.backingapp.data.realm.RecipeRealm;
import com.backing.backingapp.fragments.DetailsFragment;
import com.backing.backingapp.fragments.DetailsFragment.OnClickListener;
import com.backing.backingapp.fragments.IngredientsFragment;
import com.backing.backingapp.fragments.MasterRecipesFragment;
import com.backing.backingapp.fragments.MasterRecipesFragment.OnRecipeClickListener;
import com.backing.backingapp.fragments.StepFragment;
import com.backing.backingapp.utils.Playable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnRecipeClickListener, OnClickListener, Playable {

    private static final String STATE_KEY = "STATE_FRAGMENT";
    private static final String ID_RECIPE_KEY = "ID_RECIPE";
    private static final String ID_STEP_KEY = "ID_STEP";
    public static final String TAG_RECIPE = "FRAGMENT_RECIPE";
    public static final String TAG_STEP = "FRAGMENT_STEP";
    public static final String TAG_INGREDIENTS = "FRAGMENT_INGREDIENTS";
    public static final String TAG_DETAILS = "FRAGMENT_DETAILS";
    private static final int STATE_RECIPE = 1;
    private static final int STATE_DETAILS = 2;
    private static final int STATE_STEP = 3;
    private static final int STATE_INGREDIENTS = 4;

    private int recipeId;
    private int stepId;
    private int stateFragment;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    private RestService service;
    private RealmResults<RecipeRealm> recipes;
    private RealmManager realmManager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private FragmentManager manager;


    private void initCurrentFragment(Bundle state) {
        if (state != null) {
            recipeId = state.getInt(ID_RECIPE_KEY, -1);
            stepId = state.getInt(ID_STEP_KEY, -1);
            stateFragment = state.getInt(STATE_KEY, STATE_RECIPE);
            updateFragments(false);
        } else {
            recipeId = -1;
            stepId = -1;
            stateFragment = STATE_RECIPE;
            updateFragments(true);
        }
    }

    private void updateFragments(boolean isStart) {

        executeNetQuery();
        switch (stateFragment) {
            case STATE_INGREDIENTS:
                if (isStart) {
                    IngredientsFragment fragment = new IngredientsFragment();
                    realmManager.getRecipeById(recipeId, new RealmChangeListener<RecipeRealm>() {
                        @Override
                        public void onChange(RecipeRealm recipeRealm) {
                            fragment.setIngredients(new RecipeDto(recipeRealm));
                        }
                    });
                    if (findViewById(R.id.second_container) != null) {
                        manager.beginTransaction().replace(R.id.second_container, fragment, TAG_INGREDIENTS).commit();
                    } else {
                        manager.beginTransaction().replace(R.id.first_container, fragment, TAG_INGREDIENTS).commit();
                    }
                }
                break;
            case STATE_STEP:
                if (isStart) {
                    StepFragment fragment = new StepFragment();
                    fragment.setStep(new StepDto(realmManager.getStepById(stepId)));
                    ;
                    if (findViewById(R.id.second_container) != null) {
                        manager.beginTransaction().replace(R.id.second_container, fragment, TAG_STEP).commit();
                    } else {
                        manager.beginTransaction().replace(R.id.first_container, fragment, TAG_STEP).commit();
                    }
                }
                break;
            case STATE_DETAILS:
                if (isStart) {
                    DetailsFragment fragment = new DetailsFragment();
                    realmManager.getRecipeById(recipeId, new RealmChangeListener<RecipeRealm>() {
                        @Override
                        public void onChange(RecipeRealm recipeRealm) {
                            fragment.setDetailsAndSteps(new RecipeDto(recipeRealm));
                        }
                    });
                    manager.beginTransaction().replace(R.id.first_container, fragment, TAG_DETAILS).commit();
                }
                break;
            case STATE_RECIPE:
                if (isStart) {
                    MasterRecipesFragment fragment = new MasterRecipesFragment();
                    updateRecipes(fragment);
                }
                break;
            default:
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY, stateFragment);
        outState.putInt(ID_RECIPE_KEY, recipeId);
        outState.putInt(ID_STEP_KEY, stepId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initMedia();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        manager = getSupportFragmentManager();
        realmManager = new RealmManager();
        realmManager.getRecipes(recipeRealms -> recipes = recipeRealms);
        setSupportActionBar(toolbar);
        initCurrentFragment(savedInstanceState);
    }

    private void initMedia() {
        mediaSession = new MediaSessionCompat(this, MainActivity.class.getName());
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSession.setMediaButtonReceiver(null);
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setActive(true);
    }

    private void executeNetQuery() {
        if (service == null) {
            service = ServiceGenerator.createService(RestService.class);
        }
        service.getRecipes().enqueue(new Callback<List<RecipeResponse>>() {
            @Override
            public void onResponse(Call<List<RecipeResponse>> call, Response<List<RecipeResponse>> response) {
                if (response.code() == 200 && response.body() != null) {
                    RealmManager realmManager = new RealmManager();
                    realmManager.saveRecipesFromNet(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<RecipeResponse>> call, Throwable t) {
                Log.e(MainActivity.class.getName(), t.getMessage(), t);
            }
        });
    }

    @Override
    public void onRecipeSelected(int id) {
        recipeId = id;
        stateFragment = STATE_DETAILS;
        DetailsFragment fragment = new DetailsFragment();
        realmManager.getRecipeById(recipeId, new RealmChangeListener<RecipeRealm>() {
            @Override
            public void onChange(RecipeRealm recipeRealm) {
                fragment.setDetailsAndSteps(new RecipeDto(recipeRealm));
            }
        });
        manager.beginTransaction().replace(R.id.first_container, fragment, TAG_DETAILS).commit();
    }

    @Override
    public void onSelected(DetailDto.DetailType type, int Id, boolean haveVideo) {
        Fragment fragment = null;
        String tag = null;
        switch (type) {
            case STEP:
                stateFragment = STATE_STEP;
                stepId = Id;
                fragment = new StepFragment();
                ((StepFragment) fragment).setStep(new StepDto(realmManager.getStepById(stepId)));
                tag = TAG_STEP;
                break;
            case INGREDIENTS:
                stateFragment = STATE_INGREDIENTS;
                stepId = -1;
                fragment = new IngredientsFragment();
                Fragment finalFragment = fragment;
                realmManager.getRecipeById(recipeId, recipeRealm ->
                        ((IngredientsFragment) finalFragment).setIngredients(new RecipeDto(recipeRealm)));
                tag = TAG_INGREDIENTS;
                break;
        }

        if (findViewById(R.id.second_container) != null) {
            manager.beginTransaction().replace(R.id.second_container, fragment, tag).commit();
        } else {
            manager.beginTransaction().replace(R.id.first_container, fragment, tag).commit();
        }

    }

    @Override
    public void onBackPressed() {
        switch (stateFragment) {
            case STATE_INGREDIENTS:
            case STATE_STEP:
                stateFragment = STATE_DETAILS;
                stepId = -1;
                if (findViewById(R.id.second_container) != null) {
                    manager.beginTransaction().replace(R.id.second_container, new Fragment()).commit();
                } else {
                    DetailsFragment detailsFragment = new DetailsFragment();
                    realmManager.getRecipeById(recipeId, recipeRealm -> detailsFragment.setDetailsAndSteps(new RecipeDto(recipeRealm)));
                    manager.beginTransaction().replace(R.id.first_container, detailsFragment, TAG_DETAILS).commit();
                }
                break;
            case STATE_DETAILS:
                stateFragment = STATE_RECIPE;
                recipeId = -1;
                MasterRecipesFragment recipesFragment = new MasterRecipesFragment();
                updateRecipes(recipesFragment);
                break;
            case STATE_RECIPE:
            default:
                super.onBackPressed();
                break;
        }
    }

    private void updateRecipes(MasterRecipesFragment recipesFragment) {
        realmManager.getRecipes(recipeRealms -> {
            recipes = recipeRealms;
            if (recipesFragment != null) {
                List<RecipeDto> recipeDtos = new ArrayList<>();
                for (RecipeRealm recipeRealm : recipeRealms) {
                    recipeDtos.add(new RecipeDto(recipeRealm));
                }
                recipesFragment.setRecipes(recipeDtos);
            }
        });
        manager.beginTransaction().replace(R.id.first_container, recipesFragment, TAG_RECIPE).commit();
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    @Override
    public PlaybackStateCompat.Builder getStatePlayback() {
        return stateBuilder;
    }

    @Override
    protected void onStop() {
        if (recipes != null) {
            recipes.removeAllChangeListeners();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.peterark.bakingapp.bakingapp.panels.recipeDetailStep;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;
import com.peterark.bakingapp.bakingapp.databinding.FragmentRecipeDetailStepBinding;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by PETER on 7/11/2017.
 */

public class RecipeDetailStepFragment extends Fragment implements LoaderManager.LoaderCallbacks<RecipeDetailStepFragment.TaskResponse>  {

    public static final String RECIPE_ID = "RECIPE_ID";
    public static final String RECIPE_STEP_ID = "RECIPE_STEP_ID";
    private int mRecipeId;
    private int mRecipeStepId;

    private FragmentRecipeDetailStepBinding mBinding;

    private SimpleExoPlayer mExoPlayer;

    private TaskResponse mTaskResponse;

    private boolean isTablet;
    private boolean isInLandscapeMode;

    private PaginationHandler paginationHandler;

    // Empty Constructor
    public RecipeDetailStepFragment(){
    }


    // Fragment "Instance constructor".
    // As recomended in StackOverflow post: https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
    public static RecipeDetailStepFragment newInstance(int recipeId,int recipeStepId){

        RecipeDetailStepFragment fragment = new RecipeDetailStepFragment();

        Bundle args = new Bundle();
        args.putInt(RECIPE_ID,recipeId);
        args.putInt(RECIPE_STEP_ID,recipeStepId);
        fragment.setArguments(args);

        return fragment;
    }

    public interface PaginationHandler{
        void goToPreviousRecipe(int previousStepId);
        void goToNextRecipe(int nextRecipeId);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            paginationHandler = (PaginationHandler) context;
        } catch (ClassCastException e) {
            Timber.d("PaginationHandler is not set. Must be a tablet then.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail_step, container, false);

        // Get the recipe id and recipe step id from the arguments.
        mRecipeId = 0;
        mRecipeStepId = 0;
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(RECIPE_ID)) mRecipeId = bundle.getInt(RECIPE_ID);
            if (bundle.containsKey(RECIPE_STEP_ID)) mRecipeStepId = bundle.getInt(RECIPE_STEP_ID);
        }

        // Get some device current configuration values.
        Context context = getActivity();
        isTablet            = context.getResources().getBoolean(R.bool.isTablet);
        isInLandscapeMode   = context.getResources().getBoolean(R.bool.isInLandscapeMode);

        if(isTablet || isInLandscapeMode) // In any of both cases we hide the pagination buttons.
            mBinding.paginationButtonContainer.setVisibility(View.GONE);
        else {
            mBinding.paginationButtonContainer.setVisibility(View.VISIBLE);
            mBinding.actionGoToPreviousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(paginationHandler!=null)paginationHandler.goToPreviousRecipe(mTaskResponse.previousRecipeStepId);
                }
            });
            mBinding.actionGoToNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(paginationHandler!=null)paginationHandler.goToNextRecipe(mTaskResponse.nextRecipeStepId);
                }
            });
        }

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader<RecipeDetailStepFragment.TaskResponse> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<TaskResponse>(getActivity()) {

            RecipeDetailStepFragment.TaskResponse cachedResponse;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if(cachedResponse!=null)
                    deliverResult(cachedResponse);
                else
                    forceLoad();
            }

            @Override
            public TaskResponse loadInBackground() {

                Context context = getContext();

                if(context == null)
                    return null;

                // Initiating some values.
                int stepId = 0;
                String stepShortDescription         = "";
                String stepDescription              = "";
                String videoUrl                     = "";
                String thumbnailUrl                 = "";
                int previousStepId                  = -1;
                int nextStepId                      = -1;


                // -------------------------------------------------------
                // FIRST Get Info about the current selected recipe.
                // -------------------------------------------------------
                Cursor onRecipeStepCursor = context.getContentResolver().query(RecipeStepContract.RecipeStepEntry.CONTENT_URI,
                                                                    null,
                                                                    RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_ID + "= ? AND " + RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID + "= ?",
                                                                    new String[]{String.valueOf(mRecipeId),String.valueOf(mRecipeStepId)},
                                                                    null
                                                                    );
                if(onRecipeStepCursor!=null) {
                    if(onRecipeStepCursor.moveToNext()) {
                        stepId = onRecipeStepCursor.getInt(onRecipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID));
                        stepShortDescription = onRecipeStepCursor.getString(onRecipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_SHORT_DESCRIPTION));
                        stepDescription = onRecipeStepCursor.getString(onRecipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_DESCRIPTION));
                        videoUrl = onRecipeStepCursor.getString(onRecipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_VIDEO_URL));
                        thumbnailUrl = onRecipeStepCursor.getString(onRecipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_THUMBNAIL_URL));
                    }
                    if(!onRecipeStepCursor.isClosed())onRecipeStepCursor.close();
                }

                // --------------------------------------------------------------
                // Now check the previous and next recipe step id.
                // --------------------------------------------------------------

                // First get all the recipe steps of the current recipe (ordered by RecipeStepId)
                Cursor allRecipeStepsCursor
                        = context.getContentResolver().query(RecipeStepContract.RecipeStepEntry.CONTENT_URI,
                                                            null,
                                                            RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_ID + "= ?",
                                                            new String[]{String.valueOf(mRecipeId)},
                                                            RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID);

                List<Integer> stepIdList = new ArrayList<>();
                if(allRecipeStepsCursor!=null){
                    while (allRecipeStepsCursor.moveToNext()) {
                        int recipeStepId = allRecipeStepsCursor.getInt(allRecipeStepsCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID));
                        stepIdList.add(recipeStepId);
                    }
                    if(!allRecipeStepsCursor.isClosed()) allRecipeStepsCursor.close();
                }

                // Now with the previous list of recipe steps ids we check the previous and next.
                int currentStepIndex = stepIdList.indexOf(mRecipeStepId);
                if(currentStepIndex>=0){
                    int previousIndex   = currentStepIndex-1;
                    int nextIndex       = currentStepIndex+1;

                    // Set the Previous Step (if the previous list index is valid)
                    if(previousIndex>=0)
                        previousStepId  = stepIdList.get(previousIndex);

                    // Set the Next Step (if the next list index is valid)
                    if(nextIndex<=stepIdList.size()-1)
                        nextStepId      = stepIdList.get(nextIndex);
                }


                return new TaskResponse(stepId,stepShortDescription,stepDescription,videoUrl,thumbnailUrl,previousStepId,nextStepId);
            }


            @Override
            public void deliverResult(TaskResponse response) {
                cachedResponse = response;
                super.deliverResult(response);
            }
        };


    }

    @Override
    public void onLoadFinished(Loader<TaskResponse> loader, TaskResponse response) {

        mTaskResponse = response;

        if(mTaskResponse==null)
            return;

        // Now show the elements in the UI
        mBinding.recipeStepNumberTextview.setText(response.stepId == 0 ? "I" : String.valueOf(response.stepId));
        mBinding.recipeShortDescriptionTextview.setText(response.stepShortDescription);
        mBinding.recipeLargeDescriptionTextview.setText(response.stepDescription);

        // Check if there is valid video url.
        if (response.videoUrl.length()>0 || response.thumbnailUrl.length() > 0){
            mBinding.noMediaAvailableTextview.setVisibility(View.GONE);
            mBinding.recipeVideoPlayerview.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(response.videoUrl.length()>0 ? response.videoUrl : response.thumbnailUrl));
        }else {
            mBinding.recipeVideoPlayerview.setVisibility(View.GONE);
            mBinding.noMediaAvailableTextview.setVisibility(View.VISIBLE);
        }

        // Show/Hide Pagination buttons.
        if(response.previousRecipeStepId==-1) mBinding.actionGoToPreviousStep.setVisibility(View.GONE);
        if(response.nextRecipeStepId==-1) mBinding.actionGoToNextStep.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<RecipeDetailStepFragment.TaskResponse> loader) {

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            Context context = getActivity();
            if (context == null) return;


            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mBinding.recipeVideoPlayerview.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    class TaskResponse{
        final int stepId;
        final String stepShortDescription;
        final String stepDescription;
        final String videoUrl;
        final String thumbnailUrl;
        final int previousRecipeStepId;
        final int nextRecipeStepId;

        TaskResponse(int stepId, String stepShortDescription, String stepDescription, String videoUrl, String thumbnailUrl, int previousRecipeStepId, int nextRecipeStepId) {
            this.stepId                 = stepId;
            this.stepShortDescription   = stepShortDescription;
            this.stepDescription        = stepDescription;
            this.videoUrl               = videoUrl;
            this.thumbnailUrl           = thumbnailUrl;
            this.previousRecipeStepId   = previousRecipeStepId;
            this.nextRecipeStepId       = nextRecipeStepId;
        }
    }
}

package com.peterark.bakingapp.bakingapp.panels.recipeDetailStep;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;
import com.peterark.bakingapp.bakingapp.databinding.FragmentRecipeDetailStepBinding;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeStep;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Created by PETER on 7/11/2017.
 */

public class RecipeDetailStepFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    public static String RECIPE_ID = "RECIPE_ID";
    public static String RECIPE_STEP_ID = "RECIPE_STEP_ID";
    private int mRecipeId;
    private int mRecipeStepId;

    FragmentRecipeDetailStepBinding mBinding;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private Cursor mCursor;

    // Empty Constructor
    public RecipeDetailStepFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail_step, container, false);
        //mPlayerView = (SimpleExoPlayerView) mBinding.getRoot().findViewById(R.id.recipe_video_playerview);

        // Get the recipe id and recipe step id from the arguments.
        mRecipeId = 0;
        mRecipeStepId = 0;
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(RECIPE_ID)) mRecipeId = bundle.getInt(RECIPE_ID);
            if (bundle.containsKey(RECIPE_STEP_ID)) mRecipeStepId = bundle.getInt(RECIPE_STEP_ID);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                                RecipeStepContract.RecipeStepEntry.CONTENT_URI,
                                null,
                                RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_ID + "= ? AND " + RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID + "= ?",
                                new String[]{String.valueOf(mRecipeId),String.valueOf(mRecipeStepId)},
                                null
                                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;

        if(mCursor != null){
            if (mCursor.moveToNext()) {
                int stepId                  = cursor.getInt(cursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID));
                String stepShortDescription = cursor.getString(cursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_SHORT_DESCRIPTION));
                String stepDescription      = cursor.getString(cursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_DESCRIPTION));
                String videoUrl             = cursor.getString(cursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_VIDEO_URL));
                String thumbnailUrl         = cursor.getString(cursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_THUMBNAIL_URL));


                // Now show the elements in the UI
                mBinding.recipeStepNumberTextview.setText(stepId == 0 ? "I" : String.valueOf(stepId));
                mBinding.recipeShortDescriptionTextview.setText(stepShortDescription);
                mBinding.recipeLargeDescriptionTextview.setText(stepDescription);

                // Check if there is valid video url.
                if (videoUrl.length()>0){
                    // Hide the Image THumbnail
                    mBinding.recipeMediaImageContainer.setVisibility(View.GONE);

                    // Show the Video Container and initialize the player
                    mBinding.recipeMediaVideoContainer.setVisibility(View.VISIBLE);
                    initializePlayer(Uri.parse(videoUrl));
                }
                else if (thumbnailUrl.length()>0) // If we dont have a video Url, but we have a valid image url. then load it with Picasso.
                {
                    // Hide the Video Container.
                    mBinding.recipeMediaVideoContainer.setVisibility(View.GONE);

                    // Show the Image Container and load it with Picasso.
                    mBinding.recipeMediaVideoContainer.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(thumbnailUrl).into(mBinding.recipeMediaImageContainer);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
}
package com.peterark.bakingapp.bakingapp.helperStructures;

/**
 * Created by PETER on 30/10/2017.
 */

public class RecipeStep {
    public int stepId;
    public String stepShortDescription;
    public String stepDescription;
    public String stepVideoUrl;
    public String stepThumbnailUrl;

    public RecipeStep(int stepId, String stepShortDescription, String stepDescription, String stepVideoUrl, String stepThumbnailUrl) {
        this.stepId                 = stepId;
        this.stepShortDescription   = stepShortDescription;
        this.stepDescription        = stepDescription;
        this.stepVideoUrl           = stepVideoUrl;
        this.stepThumbnailUrl       = stepThumbnailUrl;
    }

    // For the RecipeDetail step list adapter.
    public RecipeStep(String stepShortDescription){
        this.stepShortDescription   = stepShortDescription;
    }
}

package com.pkasemer.malai.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class InferenceModel {

    @SerializedName("original_image")
    @Expose
    private String originalImage;
    @SerializedName("rotated_image")
    @Expose
    private String rotatedImage;
    @SerializedName("time_taken")
    @Expose
    private Double timeTaken;

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public String getRotatedImage() {
        return rotatedImage;
    }

    public void setRotatedImage(String rotatedImage) {
        this.rotatedImage = rotatedImage;
    }

    public Double getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Double timeTaken) {
        this.timeTaken = timeTaken;
    }

}
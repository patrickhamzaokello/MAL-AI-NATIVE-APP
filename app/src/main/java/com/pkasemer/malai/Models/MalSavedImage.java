package com.pkasemer.malai.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class MalSavedImage {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("gender")
    @Expose
    private String gender;


    @SerializedName("slide_id")
    @Expose
    private String slide_id;

    @SerializedName("age")
    @Expose
    private Integer age;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("image_path")
    @Expose
    private String image_path;

    @SerializedName("site_name")
    @Expose
    private String site_name;

    public MalSavedImage(Integer id, String gender, String slide_id, Integer age, String timestamp, String image_path, String site_name) {
        this.id = id;
        this.gender = gender;
        this.slide_id = slide_id;
        this.age = age;
        this.timestamp = timestamp;
        this.image_path = image_path;
        this.site_name = site_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSlide_id() {
        return slide_id;
    }

    public void setSlide_id(String slide_id) {
        this.slide_id = slide_id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }
}

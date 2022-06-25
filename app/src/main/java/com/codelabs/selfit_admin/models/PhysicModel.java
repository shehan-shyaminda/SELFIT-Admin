package com.codelabs.selfit_admin.models;

public class PhysicModel {
    String imageUrl, uploadedDate;

    public PhysicModel(String imageUrl, String uploadedDate) {
        this.imageUrl = imageUrl;
        this.uploadedDate = uploadedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    @Override
    public String toString() {
        return uploadedDate;
    }
}

package com.codelabs.selfit_admin.models;

public class MealsModel {
    String mealName, mealID;

    public MealsModel( String mealID, String mealName) {
        this.mealID = mealID;
        this.mealName = mealName;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealID() {
        return mealID;
    }

    public void setMealID(String mealID) {
        this.mealID = mealID;
    }

    @Override
    public String toString() {
        return mealName;
    }
}

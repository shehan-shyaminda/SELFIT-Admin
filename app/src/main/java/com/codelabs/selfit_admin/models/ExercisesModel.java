package com.codelabs.selfit_admin.models;

public class ExercisesModel {
    String exerciseID, exerciseName;

    public ExercisesModel(String exerciseID, String exerciseName) {
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
    }

    public String getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(String exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    @Override
    public String toString() {
        return exerciseName;
    }
}

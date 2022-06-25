package com.codelabs.selfit_admin.models;

public class UsersModel {
    String usersName, usersRegDate;

    public UsersModel(String usersName, String usersRegDate) {
        this.usersName = usersName;
        this.usersRegDate = usersRegDate;
    }

    public String getUsersName() {
        return usersName;
    }

    public void setUsersName(String usersName) {
        this.usersName = usersName;
    }

    public String getUsersRegDate() {
        return usersRegDate;
    }

    public void setUsersRegDate(String usersRegDate) {
        this.usersRegDate = usersRegDate;
    }
}

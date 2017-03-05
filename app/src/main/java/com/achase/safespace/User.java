package com.achase.safespace;

/**
 * Created by achas on 2/18/2017.
 */

public class User {
    public String firstName;
    public String lastName;
    public String birthDate;
    public String userType;
    public String userSkill;

    public User(){}

    public User(String firstName, String lastName, String birthDate, String userType, String userSkill){
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.userType = userType;
        this.userSkill = userSkill;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserSkill() {
        return userSkill;
    }

    public void setUserSkill(String userSkill) {
        this.userSkill = userSkill;
    }

    public String getPhotoFilename(String userID){
        return "IMG_" + userID + ".jpg";
    }
}

package com.achase.safespace;

/**
 * Created by achas on 2/18/2017.
 */

public class MedicalUser {
    String firstName;
    String lastName;
    String birthDate;
    String skill;
    String trainingCenterId;

    public MedicalUser(){

    }

    public MedicalUser(String firstName, String lastName, String birthDate, String skill, String trainingCenterId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.skill = skill;
        this.trainingCenterId = trainingCenterId;
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

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getTrainingCenterId() {
        return trainingCenterId;
    }

    public void setTrainingCenterId(String trainingCenterId) {
        this.trainingCenterId = trainingCenterId;
    }
}

package com.example.uniconnect;

// Assuming ProfileInfo is a parent class with common fields
public class StudentInfo extends ProfileInfo {

    private String firstname;
    private String lastname;
    private String email;
    private String contactNo;
    private String sdept; // Assuming "tdept" refers to the teacher's department
    private String roll;    // Teacher's unique identifier
private String institution;
    // Default constructor required for Firebase
    public StudentInfo() {
    }

    // Parameterized constructor
    public StudentInfo(String firstname, String lastname, String email, String contactNo,String institution, String sdept, String roll) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contactNo = contactNo;
        this.institution=institution;
        this.sdept = sdept;
        this.roll = roll;
    }

    // Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getname() {
        return firstname+" "+lastname;
    }
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getSdept() {
        return sdept;
    }

    public void setSdept(String sdept) {
        this.sdept = sdept;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", sdept='" + sdept + '\'' +
                ", roll='" + roll + '\'' +
                '}';
    }
}

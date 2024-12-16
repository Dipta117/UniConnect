package com.example.uniconnect;

// Assuming ProfileInfo is a parent class with common fields
public class TeacherInfo extends ProfileInfo {

    private String firstname;
    private String lastname;
    private String email;
    private String contactNo;
    private String tdept; // Assuming "tdept" refers to the teacher's department
    private String id;
    private String institution;// Teacher's unique identifier

    // Default constructor required for Firebase
    public TeacherInfo() {
    }

    // Parameterized constructor
    public TeacherInfo(String firstname, String lastname, String email, String contactNo,String institution, String tdept, String id) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contactNo = contactNo;
        this.institution=institution;
        this.tdept = tdept;
        this.id = id;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    // Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
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
    public String getname() {
        return firstname+" "+lastname;
    }
    public String getTdept() {
        return tdept;
    }

    public void setTdept(String tdept) {
        this.tdept = tdept;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TeacherInfo{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", tdept='" + tdept + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

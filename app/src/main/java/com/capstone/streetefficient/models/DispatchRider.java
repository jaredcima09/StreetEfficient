package com.capstone.streetefficient.models;

import java.util.Date;

public class DispatchRider {
    private String actualAssessment;
    private String address;
    private Date birthdate;
    private String branch;
    private String cR;
    private String contactNumber;
    private String courier_id;
    private String designateBarangay;
    private String email;
    private String emerg_number;
    private String encoded_by;
    private String fname;
    private String gender;
    private String id;
    private String license;
    private String lname;
    private String mname;
    private String oR;
    private String password;
    private String status;
    private String vehicle_type;
    private String writtenExam;

    public DispatchRider() {
    }

    public DispatchRider(String actualAssessment, String address, Date birthdate, String branch, String cR, String contactNumber, String courier_id, String designateBarangay, String email,
                         String emerg_number, String encoded_by, String fname, String gender, String id, String license, String lname, String mname, String oR, String password, String status, String vehicle_type, String writtenExam) {
        this.actualAssessment = actualAssessment;
        this.address = address;
        this.birthdate = birthdate;
        this.branch = branch;
        this.cR = cR;
        this.contactNumber = contactNumber;
        this.courier_id = courier_id;
        this.designateBarangay = designateBarangay;
        this.email = email;
        this.emerg_number = emerg_number;
        this.encoded_by = encoded_by;
        this.fname = fname;
        this.gender = gender;
        this.id = id;
        this.license = license;
        this.lname = lname;
        this.mname = mname;
        this.oR = oR;
        this.password = password;
        this.status = status;
        this.vehicle_type = vehicle_type;
        this.writtenExam = writtenExam;
    }

    public String getActualAssessment() {
        return actualAssessment;
    }

    public void setActualAssessment(String actualAssessment) {
        this.actualAssessment = actualAssessment;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getcR() {
        return cR;
    }

    public void setcR(String cR) {
        this.cR = cR;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }

    public String getDesignateBarangay() {
        return designateBarangay;
    }

    public void setDesignateBarangay(String designateBarangay) {
        this.designateBarangay = designateBarangay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmerg_number() {
        return emerg_number;
    }

    public void setEmerg_number(String emerg_number) {
        this.emerg_number = emerg_number;
    }

    public String getEncoded_by() {
        return encoded_by;
    }

    public void setEncoded_by(String encoded_by) {
        this.encoded_by = encoded_by;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getoR() {
        return oR;
    }

    public void setoR(String oR) {
        this.oR = oR;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getWrittenExam() {
        return writtenExam;
    }

    public void setWrittenExam(String writtenExam) {
        this.writtenExam = writtenExam;
    }

    @Override
    public String toString() {
        return "DispatchRider{" +
                "actualAssessment='" + actualAssessment + '\'' +
                ", address='" + address + '\'' +
                ", birthdate=" + birthdate +
                ", branch='" + branch + '\'' +
                ", cR='" + cR + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", courier_id='" + courier_id + '\'' +
                ", designateBarangay='" + designateBarangay + '\'' +
                ", email='" + email + '\'' +
                ", emerg_number='" + emerg_number + '\'' +
                ", encoded_by='" + encoded_by + '\'' +
                ", fname='" + fname + '\'' +
                ", gender='" + gender + '\'' +
                ", id='" + id + '\'' +
                ", license='" + license + '\'' +
                ", lname='" + lname + '\'' +
                ", mname='" + mname + '\'' +
                ", oR='" + oR + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", vehicle_type='" + vehicle_type + '\'' +
                ", writtenExam='" + writtenExam + '\'' +
                '}';
    }
}

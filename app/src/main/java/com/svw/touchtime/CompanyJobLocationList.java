package com.svw.touchtime;

/**
 * Created by djlee on 3/9/15.
 */
public class CompanyJobLocationList {

    String Name;
    String Street;
    String City;
    String State;
    String ZipCode;
    String Country;
    String Phone;
    String Contact;
    String Email;
    String Job;
    String Location;

    // constructors
    public CompanyJobLocationList() {
        this.Name = null;
        this.Street = null;
        this.City = null;
        this.State = null;
        this.ZipCode = null;
        this.Country = null;
        this.Phone = null;
        this.Contact = null;
        this.Email = null;
        this.Job = null;
        this.Location = null;
    }

    public CompanyJobLocationList(CompanyJobLocationList Company) {
        this.Name = Company.Name;
        this.Street = Company.Street;
        this.City = Company.City;
        this.State = Company.State;
        this.ZipCode = Company.ZipCode;
        this.Country = Company.Country;
        this.Phone = Company.Phone;
        this.Contact = Company.Contact;
        this.Email = Company.Email;
        this.Job = Company.Job;
        this.Location = Company.Location;
    }

    // setters
    public void setName(String Name) {
        if (Name != null) this.Name = Name;
    }
    public void setStreet(String Street) {
        if (Street != null) this.Street = Street;
    }
    public void setCity(String City) {
        if (City != null) this.City = City;
    }
    public void setState(String State) {
        if (State != null) this.State = State;
    }
    public void setZipCode(String ZipCode) {
        if (ZipCode != null) this.ZipCode = ZipCode;
    }
    public void setCountry(String Country) {
        if (Country != null) this.Country = Country;
    }
    public void setPhone(String Phone) {
        if (Phone != null) this.Phone = Phone;
    }
    public void setContact(String Contact) {
        if (Contact != null) this.Contact = Contact;
    }
    public void setEmail(String Email) {
        if (Email != null) this.Email = Email;
    }
    public void setJob(String Job) {
        if (Job != null) this.Job = Job;
    }
    public void setLocation(String Location) {
        if (Location != null) this.Location = Location;
    }

    // getters
    public String getName() { return this.Name; }
    public String getStreet() { return this.Street; }
    public String getCity() { return this.City; }
    public String getState() { return this.State; }
    public String getZipCode() { return this.ZipCode; }
    public String getCountry() { return this.Country; }
    public String getPhone() { return this.Phone; }
    public String getContact() { return this.Contact; }
    public String getEmail() { return this.Email; }
    public String getJob() { return this.Job; }
    public String getLocation() { return this.Location; }
}


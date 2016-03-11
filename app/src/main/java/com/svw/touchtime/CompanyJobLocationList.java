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
        this.Name = "";
        this.Street = "";
        this.City = "";
        this.State = "";
        this.ZipCode = "";
        this.Country = "";
        this.Phone = "";
        this.Contact = "";
        this.Email = "";
        this.Job = "";
        this.Location = "";
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
        if (!Name.isEmpty()) this.Name = Name;
    }
    public void setStreet(String Street) {
        if (!Street.isEmpty()) this.Street = Street;
    }
    public void setCity(String City) {
        if (!City.isEmpty()) this.City = City;
    }
    public void setState(String State) {
        if (!State.isEmpty()) this.State = State;
    }
    public void setZipCode(String ZipCode) {
        if (!ZipCode.isEmpty()) this.ZipCode = ZipCode;
    }
    public void setCountry(String Country) {
        if (!Country.isEmpty()) this.Country = Country;
    }
    public void setPhone(String Phone) {
        if (!Phone.isEmpty()) this.Phone = Phone;
    }
    public void setContact(String Contact) {
        if (!Contact.isEmpty()) this.Contact = Contact;
    }
    public void setEmail(String Email) {
        if (!Email.isEmpty()) this.Email = Email;
    }
    public void setJob(String Job) {
        if (!Job.isEmpty()) this.Job = Job;
    }
    public void setLocation(String Location) {
        if (!Location.isEmpty()) this.Location = Location;
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


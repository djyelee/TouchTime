package com.svw.touchtime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by djlee on 3/13/15.
 */
public class EmployeeProfileList {
    int EmployeeID;
    String LastName;
    String FirstName;
    String Street;
    String City;
    String State;
    String ZipCode;
    String Country;
    String Phone;
    String Email;
    double HourlyRate;
    double PieceRate;
    String SSNumber;
    String DoB;
    String DoH;
    int Active;
    String DocExp;
    int Current;
    String Comments;
    int Group;
    String Company;
    String Location;
    String Job;
    int Status;
    Bitmap Photo;


    // constructors
    public EmployeeProfileList() {
        this.EmployeeID = 0;            // starts from 0
        this.LastName ="No Name";
        this.FirstName ="No Name";
        this.Street ="";
        this.City ="";
        this.State ="";
        this.ZipCode ="";
        this.Country ="";
        this.Phone ="";
        this.Email ="";
        this.HourlyRate = 10.0;
        this.PieceRate = 10.0;
        this.SSNumber ="";
        this.DoB ="";
        this.DoH ="";
        this.Active = 0;
        this.DocExp ="";
        this.Current = 0;
        this.Comments ="";
        this.Group = 0;
        this.Company ="";
        this.Location ="";
        this.Job ="";
        this.Status = 0;
        this.Photo = null;
    }

    public EmployeeProfileList(EmployeeProfileList Employee) {
        this.EmployeeID = Employee.EmployeeID;
        this.LastName = Employee.LastName;
        this.FirstName = Employee.FirstName;
        this.Street = Employee.Street;
        this.City = Employee.City;
        this.State = Employee.State;
        this.ZipCode = Employee.ZipCode;
        this.Country = Employee.Country;
        this.Phone = Employee.Phone;
        this.Email = Employee.Email;
        this.HourlyRate = Employee.HourlyRate;
        this.PieceRate = Employee.PieceRate;
        this.SSNumber = Employee.SSNumber;
        this.DoB = Employee.DoB;
        this.DoH = Employee.DoH;
        this.Active = Employee.Active;
        this.DocExp = Employee.DocExp;
        this.Current = Employee.Current;
        this.Comments = Employee.Comments;
        this.Group = Employee.Group;
        this.Company = Employee.Company;
        this.Location = Employee.Location;
        this.Job = Employee.Job;
        this.Status = Employee.Status;
        this.Photo = Employee.Photo;
    }

    // setters
    public void setEmployeeID(int EmployeeID) {
        if (EmployeeID != 0) this.EmployeeID = EmployeeID;
    }       // must be at least 1
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }
    public void setFirstName(String FirstName) { this.FirstName = FirstName; }
    public void setStreet(String Street) {
        this.Street = Street;
    }
    public void setCity(String City) {
        this.City = City;
    }
    public void setState(String State) {
        this.State = State;
    }
    public void setZipCode(String ZipCode) {
        this.ZipCode = ZipCode;
    }
    public void setCountry(String Country) {
        this.Country = Country;
    }
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }
    public void setEmail(String Email) {
        this.Email = Email;
    }
    public void setHourlyRate(double HourlyRate) { if (HourlyRate >= 0.0) this.HourlyRate = HourlyRate; }
    public void setPieceRate(double PieceRate) {
        if (PieceRate >= 0.0) this.PieceRate = PieceRate;
    }
    public void setSSNumber(String SSNumber) {
        this.SSNumber = SSNumber;
    }
    public void setDoB(String DoB) {
        this.DoB = DoB;
    }
    public void setDoH(String DoH) {
        this.DoH = DoH;
    }
    public void setActive(int Active) {
        this.Active = Active;
    }
    public void setDocExp(String DocExp) {
        this.DocExp = DocExp;
    }
    public void setCurrent(int Current) {
        this.Current = Current;
    }
    public void setComments(String Comments) {
        this.Comments = Comments;
    }
    public void setGroup(int Group) {
        this.Group = Group;
    }
    public void setCompany(String Company) { this.Company = Company; }
    public void setLocation(String Location) { this.Location = Location; }
    public void setJob(String Job) { this.Job = Job; }
    public void setStatus(int Status) {
        this.Status = Status;
    }
    public void setPhoto(Bitmap Photo) {
        this.Photo = (Photo == null) ? null : Photo;
    }
    // getters
    public int getEmployeeID() { return this.EmployeeID; }
    public String getLastName() { return this.LastName; }
    public String getFirstName() { return this.FirstName; }
    public String getStreet() { return this.Street; }
    public String getCity() { return this.City; }
    public String getState() { return this.State; }
    public String getZipCode() { return this.ZipCode; }
    public String getCountry() { return this.Country; }
    public String getPhone() { return this.Phone; }
    public String getEmail() { return this.Email; }
    public double getHourlyRate() { return this.HourlyRate; }
    public double getPieceRate() { return this.PieceRate; }
    public String getSSNumber() { return this.SSNumber; }
    public String getDoB() { return this.DoB; }
    public String getDoH() { return this.DoH; }
    public int getActive() { return this.Active; }
    public String getDocExp() { return this.DocExp; }
    public int getCurrent() { return this.Current; }
    public String getComments() { return this.Comments; }
    public int getGroup() { return this.Group; }
    public String getCompany() { return this.Company; }
    public String getLocation() { return this.Location; }
    public String getJob() { return this.Job; }
    public int getStatus() { return this.Status; }
    public Bitmap getPhoto() { return this.Photo; }

    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap.getByteCount() > 0) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    public static Bitmap getBitmap(byte[] image) {
        if (image == null) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }
}
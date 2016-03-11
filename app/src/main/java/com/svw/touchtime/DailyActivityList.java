package com.svw.touchtime;

/**
 * Created by djlee on 3/29/15.
 */
public class DailyActivityList {
    int EmployeeID;
    String LastName;
    String FirstName;
    String WorkGroup;               // Group number Group name
    String Company;
    String Location;
    String Job;
    String Date;
    String TimeIn;
    String TimeOut;
    long Lunch;
    long Hours;
    String Supervisor;
    String Comments;

    // constructors
    public DailyActivityList() {
        this.EmployeeID = 0;
        this.LastName = "";
        this.FirstName = "";
        this.WorkGroup = "";
        this.Company = "";
        this.Location = "";
        this.Job = "";
        this.Date = "";
        this.TimeIn = "";
        this.TimeOut = "";
        this.Lunch = 0;
        this.Hours = 0;
        this.Supervisor = "";
        this.Comments = "";
    }

    public DailyActivityList(DailyActivityList DailyActivity) {
        this.EmployeeID = DailyActivity.EmployeeID;
        this.LastName = DailyActivity.LastName;
        this.FirstName = DailyActivity.FirstName;
        this.WorkGroup = DailyActivity.WorkGroup;
        this.Company = DailyActivity.Company;
        this.Location = DailyActivity.Location;
        this.Job = DailyActivity.Job;
        this.Date = DailyActivity.Date;
        this.TimeIn = DailyActivity.TimeIn;
        this.TimeOut = DailyActivity.TimeOut;
        this.Lunch = DailyActivity.Lunch;
        this.Hours = DailyActivity.Hours;
        this.Supervisor = DailyActivity.Supervisor;
        this.Comments = DailyActivity.Comments;
    }

    // setters
    public void setEmployeeID(int EmployeeID) {
        if (EmployeeID != 0) this.EmployeeID = EmployeeID;
    }       // must be at least 1

    public void setLastName(String LastName) {
        if (!LastName.isEmpty()) this.LastName = LastName;
    }

    public void setFirstName(String FirstName) {
        if (!FirstName.isEmpty()) this.FirstName = FirstName;
    }

    public void setWorkGroup(String WorkGroup) {
        if (!WorkGroup.isEmpty()) this.WorkGroup = WorkGroup;
    }

    public void setCompany(String Company) {
        if (!Company.isEmpty()) this.Company = Company;
    }

    public void setLocation(String Location) {
        if (!Location.isEmpty()) this.Location = Location;
    }

    public void setJob(String Job) {
        if (!Job.isEmpty()) this.Job = Job;
    }
    public void setDate(String Date) {
        if (!Date.isEmpty()) this.Date = Date;
    }

    public void setTimeIn(String TimeIn) {
        if (!TimeIn.isEmpty()) this.TimeIn = TimeIn;
    }

    public void setTimeOut(String TimeOut) {
        if (!TimeOut.isEmpty()) this.TimeOut = TimeOut;
    }

    public void setLunch(long Lunch) {
        if (Lunch >= 0) this.Lunch = Lunch;
    }

    public void setHours(long Hours) {
        if (Hours >= 0) this.Hours = Hours;
    }

    public void setSupervisor(String Supervisor) {
        if (!Supervisor.isEmpty()) this.Supervisor = Supervisor;
    }

    public void setComments(String Comments) {
        if (!Comments.isEmpty()) this.Comments = Comments;
    }

    // getters
    public int getEmployeeID() {
        return this.EmployeeID;
    }

    public String getLastName() {
        return this.LastName;
    }

    public String getFirstName() {
        return this.FirstName;
    }

    public String getWorkGroup() {
        return this.WorkGroup;
    }

    public String getCompany() {
        return this.Company;
    }

    public String getLocation() {
        return this.Location;
    }

    public String getJob() {
        return this.Job;
    }

    public String getDate() {
        return this.Date;
    }

    public String getTimeIn() {
        return this.TimeIn;
    }

    public String getTimeOut() {
        return this.TimeOut;
    }

    public long getLunch() {
        return this.Lunch;
    }

    public long getHours() {
        return this.Hours;
    }

    public String getSupervisor() {
        return this.Supervisor;
    }

    public String getComments() {
        return this.Comments;
    }
}

package com.svw.touchtime;

/**
 * Created by djlee on 3/23/15.
 */
public class WorkGroupList {
    int GroupID;
    String GroupName;
    String Supervisor;
    String ShiftName;
    double HourlyRate;
    double PieceRate;
    String Company;
    String Location;
    String Job;
    int Status;
    String Employees;

    // constructors
    public WorkGroupList() {
        this.GroupID = 0;
        this.GroupName = null;
        this.Supervisor = null;
        this.ShiftName = null;
        this.HourlyRate = 1.0;
        this.PieceRate = 1.0;
        this.Company = null;
        this.Location = null;
        this.Job =null;
        this.Status = 0;
        this.Employees = null;
    }

    public WorkGroupList(WorkGroupList WorkGroup) {
        this.GroupID = WorkGroup.GroupID;
        this.GroupName = WorkGroup.GroupName;
        this.Supervisor = WorkGroup.Supervisor;
        this.ShiftName = WorkGroup.ShiftName;
        this.HourlyRate = WorkGroup.HourlyRate;
        this.PieceRate = WorkGroup.PieceRate;
        this.Company = WorkGroup.Company;
        this.Location = WorkGroup.Location;
        this.Job = WorkGroup.Job;
        this.Status = WorkGroup.Status;
        this.Employees = WorkGroup.Employees;
    }

    // setters
    public void setGroupID(int GroupID) {
        if (GroupID != 0) this.GroupID = GroupID;
    }       // must be at least 1
    public void setGroupName(String GroupName) {
        if (GroupName != null) this.GroupName = GroupName;
    }
    public void setSupervisor(String Supervisor) {
        if (Supervisor != null) this.Supervisor = Supervisor;
    }
    public void setShiftName(String ShiftName) {
        if (ShiftName != null) this.ShiftName = ShiftName;
    }
    public void setHourlyRate(double HourlyRate) {
        if (HourlyRate > 0.0) this.HourlyRate = HourlyRate;
    }
    public void setPieceRate(double PieceRate) {
        if (PieceRate > 0.0) this.PieceRate = PieceRate;
    }
    public void setCompany(String Company) {
        if (Company != null) this.Company = Company;
    }
    public void setLocation(String Location) {
        if (Location != null) this.Location = Location;
    }
    public void setJob(String Job) {
        if (Job != null) this.Job = Job;
    }
    public void setStatus(int Status) {
       this.Status = Status;
    }
    public void setEmployees(String Employees) {
        if (Employees != null) this.Employees = Employees;
    }

     // getters
    public int getGroupID() { return this.GroupID; }
    public String getGroupName() { return this.GroupName; }
    public String getSupervisor() { return this.Supervisor; }
    public String getShiftName() { return this.ShiftName; }
    public double getHourlyRate() { return this.HourlyRate; }
    public double getPieceRate() { return this.PieceRate; }
    public String getCompany() { return this.Company; }
    public String getLocation() { return this.Location; }
    public String getJob() { return this.Job; }
    public int getStatus() { return this.Status; }
    public String getEmployees() { return this.Employees; }
}
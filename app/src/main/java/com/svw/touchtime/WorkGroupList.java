package com.svw.touchtime;

/**
 * Created by djlee on 3/23/15.
 */
public class WorkGroupList {
    int GroupID;
    String GroupName;
    String Supervisor;
    String ShiftName;
    String Company;
    String Location;
    String Job;
    int Status;
    String Employees;

    // constructors
    public WorkGroupList() {
        this.GroupID = 0;
        this.GroupName = "";
        this.Supervisor = "";
        this.ShiftName = "";
        this.Company = "";
        this.Location = "";
        this.Job ="";
        this.Status = 0;
        this.Employees = "";
    }

    public WorkGroupList(WorkGroupList WorkGroup) {
        this.GroupID = WorkGroup.GroupID;
        this.GroupName = WorkGroup.GroupName;
        this.Supervisor = WorkGroup.Supervisor;
        this.ShiftName = WorkGroup.ShiftName;
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
        if (!GroupName.isEmpty()) this.GroupName = GroupName;
    }
    public void setSupervisor(String Supervisor) {
        if (!Supervisor.isEmpty()) this.Supervisor = Supervisor;
    }
    public void setShiftName(String ShiftName) {
        if (!ShiftName.isEmpty()) this.ShiftName = ShiftName;
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
    public void setStatus(int Status) {
       this.Status = Status;
    }
    public void setEmployees(String Employees) {
        if (!Employees.isEmpty()) this.Employees = Employees;
    }

     // getters
    public int getGroupID() { return this.GroupID; }
    public String getGroupName() { return this.GroupName; }
    public String getSupervisor() { return this.Supervisor; }
    public String getShiftName() { return this.ShiftName; }
    public String getCompany() { return this.Company; }
    public String getLocation() { return this.Location; }
    public String getJob() { return this.Job; }
    public int getStatus() { return this.Status; }
    public String getEmployees() { return this.Employees; }
}
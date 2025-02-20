package com.example.csv_loader.model;

public class EmployeePairs {
    private long employeeId1;
    private long employeeId2;
    private long projectId;
    private long days;

    public EmployeePairs(long employeeId1, long employeeId2, long projectId, long days) {
        this.employeeId1 = employeeId1;
        this.employeeId2 = employeeId2;
        this.projectId = projectId;
        this.days = days;
    }

    public long getEmployeeId1() {
        return employeeId1;
    }

    public void setEmployeeId1(long employeeId1) {
        this.employeeId1 = employeeId1;
    }

    public long getEmployeeId2() {
        return employeeId2;
    }

    public void setEmployeeId2(long employeeId2) {
        this.employeeId2 = employeeId2;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}

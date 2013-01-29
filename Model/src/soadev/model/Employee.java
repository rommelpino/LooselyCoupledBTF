package soadev.model;

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "Employee.findAll", query = "select o from Employee o")
})
@Table(name = "EMPLOYEES")
public class Employee implements Serializable {
    @Column(name="COMMISSION_PCT")
    private Double commissionPct;
    @Column(nullable = false, unique = true, length = 25)
    private String email;
    @Id
    @Column(name="EMPLOYEE_ID", nullable = false)
    private Long employeeId;
    @Column(name="FIRST_NAME", length = 20)
    private String firstName;
    @Column(name="HIRE_DATE", nullable = false)
    private Timestamp hireDate;
    @Column(name="LAST_NAME", nullable = false, length = 25)
    private String lastName;
   
    @Column(name="PHONE_NUMBER", length = 20)
    private String phoneNumber;
    private Double salary;
    @Column(length = 1)
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "JOB_ID")
    private Job job;
    @ManyToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    private Employee manager;
   
    public Employee() {
    }


    public Double getCommissionPct() {
        return commissionPct;
    }

    public void setCommissionPct(Double commissionPct) {
        this.commissionPct = commissionPct;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Timestamp getHireDate() {
        return hireDate;
    }

    public void setHireDate(Timestamp hireDate) {
        this.hireDate = hireDate;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

   

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee employee) {
        this.manager = employee;
        if (employee != null) {
            this.employeeId = employee.getEmployeeId();
        }
    }
   
}

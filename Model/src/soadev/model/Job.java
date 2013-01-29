package soadev.model;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "findAllJobs", query = "select o from Job o")
})
@Table(name = "JOBS")
public class Job implements Serializable {
    @Id
    @Column(name="JOB_ID", nullable = false, length = 10)
    private String jobId;
    @Column(name="JOB_TITLE", nullable = false, length = 35)
    private String jobTitle;
    @Column(name="MAX_SALARY")
    private Long maxSalary;
    @Column(name="MIN_SALARY")
    private Long minSalary;
    @OneToMany(mappedBy = "job")
    private List<Employee> employeeList;

    public Job() {
    }

    public Job(String jobId, String jobTitle, Long maxSalary, Long minSalary) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.maxSalary = maxSalary;
        this.minSalary = minSalary;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public Employee addEmployee(Employee employee) {
        getEmployeeList().add(employee);
        employee.setJob(this);
        return employee;
    }

    public Employee removeEmployee(Employee employee) {
        getEmployeeList().remove(employee);
        employee.setJob(null);
        return employee;
    }
}

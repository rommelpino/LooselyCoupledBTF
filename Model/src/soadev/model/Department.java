package soadev.model;

import java.io.Serializable;

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
  @NamedQuery(name = "Department.findAll", query = "select o from Department o")
})
@Table(name = "DEPARTMENTS")
public class Department implements Serializable {
    @Id
    @Column(name="DEPARTMENT_ID", nullable = false)
    private Long departmentId;
    @Column(name="DEPARTMENT_NAME", nullable = false, length = 30)
    private String departmentName;
    @Column(name="LOCATION_ID")
    private Long locationId;
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    private Employee manager;
    @OneToMany(mappedBy = "department")
    private List<Employee> employeeList;

    public Department() {
    }

    public Department(Long departmentId, String departmentName,
                      Long locationId, Employee employee) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.locationId = locationId;
        this.manager = employee;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }


    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee employee) {
        this.manager = employee;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public Employee addEmployee(Employee employee) {
        getEmployeeList().add(employee);
        employee.setDepartment(this);
        return employee;
    }

    public Employee removeEmployee(Employee employee) {
        getEmployeeList().remove(employee);
        employee.setDepartment(null);
        return employee;
    }
}

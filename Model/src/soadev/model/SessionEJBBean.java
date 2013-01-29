package soadev.model;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "SessionEJB", mappedName = "LooselyCoupledBoundedTaskFlows-Model-SessionEJB")
@Remote
@Local
public class SessionEJBBean implements SessionEJB, SessionEJBLocal {
    @PersistenceContext(unitName="Model")
    private EntityManager em;

    public SessionEJBBean() {
    }

    public Object queryByRange(String jpqlStmt, int firstResult,
                               int maxResults) {
        Query query = em.createQuery(jpqlStmt);
        if (firstResult > 0) {
            query = query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query = query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public Job persistJob(Job job) {
        em.persist(job);
        return job;
    }

    public Job mergeJob(Job job) {
        return em.merge(job);
    }

    public void removeJob(Job job) {
        job = em.find(Job.class, job.getJobId());
        em.remove(job);
    }

    public Job findJobById(String id) {
        if (id == "EMPTY"){
            return new Job();
        }
        return em.find(Job.class, id);
    }


    public Employee persistEmployee(Employee employee) {
        em.persist(employee);
        return employee;
    }

    public Employee mergeEmployee(Employee employee) {
        return em.merge(employee);
    }

    public void removeEmployee(Employee employee) {
        employee = em.find(Employee.class, employee.getEmployeeId());
        em.remove(employee);
    }

    /** <code>select o from Employee o</code> */
    public List<Employee> getEmployeeFindAll() {
        return em.createNamedQuery("Employee.findAll").getResultList();
    }

    public Department persistDepartment(Department department) {
        em.persist(department);
        return department;
    }

    public Department mergeDepartment(Department department) {
        return em.merge(department);
    }

    public void removeDepartment(Department department) {
        department = em.find(Department.class, department.getDepartmentId());
        em.remove(department);
    }

    /** <code>select o from Department o</code> */
    public List<Department> getDepartmentFindAll() {
        return em.createNamedQuery("Department.findAll").getResultList();
    }

    /** <code>select o from Job o</code> */
    public List<Job> findAllJobs() {
        return em.createNamedQuery("findAllJobs").getResultList();
    }
}

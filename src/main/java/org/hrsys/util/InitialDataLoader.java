package org.hrsys.util;


import jakarta.annotation.PostConstruct;
import org.hrsys.model.*;
import org.hrsys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.LocalDate;

@Component
public class InitialDataLoader {

    @Autowired
    private DirectorateService directorateService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private DepartmentService departmentService;



    @PostConstruct
    public void loadData() {
        Directorate directorate1 = new Directorate("Directorate 1");
        Directorate directorate2 = new Directorate("Directorate 2");
        // Directories
        try {
            directorateService.createDirectorate(directorate1);
            directorateService.createDirectorate(directorate2);
        } catch (Exception e) {
            // handle exception
        }

        Department department1 = new Department("Department 1", directorate1);
        Department department2 = new Department("Department 2", directorate1);
        Department department3 = new Department("Department 3", directorate2);

        // Departments
        try {
            departmentService.createDepartment(department1);
            departmentService.createDepartment(department2);
            departmentService.createDepartment(department3);
        } catch (Exception e) {
            // handle exception
        }


        Role role1 = new Role("ROLE_MANAGER");
        Role role2 = new Role("ROLE_EMPLOYEE");

        // Roles
        try {
            roleService.createRole(role1);
            roleService.createRole(role2);
        } catch (Exception e) {
            // handle exception
        }



        Employee employee1 = new Employee("Employee 1", "emp1@hr.com", LocalDate.now(), department1, null, role1, "password");
        employee1.setManager(null);
        // Employees
        try {
            // Save the employees
            employeeService.createEmployee(employee1);
            managerService.upgradeToManager(employee1.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}

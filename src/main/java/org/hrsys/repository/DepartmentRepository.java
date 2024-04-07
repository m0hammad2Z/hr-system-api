package org.hrsys.repository;

import org.hrsys.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByDirectorateId(Long directorateId);
    Department findByName(String name);
}

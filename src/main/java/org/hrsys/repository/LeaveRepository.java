package org.hrsys.repository;

import org.hrsys.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployeeIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(Long employeeId, LocalDate from, LocalDate to);

    void deleteByEmployeeId(Long id);
}

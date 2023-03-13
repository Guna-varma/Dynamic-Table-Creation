package com.example.exampledemo.Repository;

import com.example.exampledemo.model.Employee;
import com.example.exampledemo.model.RelationTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long> {
}

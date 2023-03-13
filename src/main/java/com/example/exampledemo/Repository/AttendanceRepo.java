package com.example.exampledemo.Repository;

import com.example.exampledemo.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance,Long> {
}

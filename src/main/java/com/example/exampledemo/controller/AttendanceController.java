package com.example.exampledemo.controller;

import com.example.exampledemo.Repository.AttendanceRepo;
import com.example.exampledemo.model.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class AttendanceController {
    @Autowired
    private AttendanceRepo attendanceRepo;

    @PostMapping("/postAttendance")
    public Attendance postAttendance(@RequestBody Attendance attendance) {
        return attendanceRepo.save(attendance);
    }
}
package com.example.exampledemo.Repository;

import com.example.exampledemo.model.ManyToManyRelationTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManyToManyRepo extends JpaRepository<ManyToManyRelationTable, Long> {
}

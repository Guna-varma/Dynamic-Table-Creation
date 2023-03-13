package com.example.exampledemo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelationTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstTable;

    private String relationType;


    private String secondTable;

    public RelationTable(String firstTable, String relationType, String secondTable) {
        this.firstTable = firstTable;
        this.relationType = relationType;
        this.secondTable = secondTable;
    }
}

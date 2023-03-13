package com.example.exampledemo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManyToManyRelationTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstTable;
    private String primaryKeyT1;
    private String foreignKeyT1;

    private String secondTable;
    private String primaryKeyT2;
    private String foreignKeyT2;

    public ManyToManyRelationTable(String firstTable, String primaryKeyT1, String foreignKeyT1, String secondTable, String primaryKeyT2, String foreignKeyT2) {
        this.firstTable = firstTable;
        this.primaryKeyT1 = primaryKeyT1;
        this.foreignKeyT1 = foreignKeyT1;
        this.secondTable = secondTable;
        this.primaryKeyT2 = primaryKeyT2;
        this.foreignKeyT2 = foreignKeyT2;
    }
}
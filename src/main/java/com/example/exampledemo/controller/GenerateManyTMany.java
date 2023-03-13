package com.example.exampledemo.controller;

import com.example.exampledemo.Repository.ManyToManyRepo;
import com.example.exampledemo.model.ManyToManyRelationTable;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class GenerateManyTMany {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager em;

    @Autowired
    private ManyToManyRepo repo;

    @PostMapping("/joinTable")
    public void GenerateJoinTable(@RequestBody ManyToManyRelationTable many) {
        String FirstTable = many.getFirstTable();
        String SecondTable = many.getSecondTable();
        String JoinTable = FirstTable+"_"+SecondTable;

        String PrimaryKeyT1 = many.getPrimaryKeyT1();
        String PrimaryKeyT2 = many.getPrimaryKeyT2();
        String ForeignKeyT1 = many.getForeignKeyT1();
        String ForeignKeyT2 = many.getForeignKeyT2();

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE "+ JoinTable).append(" (");
        query.append(ForeignKeyT1+ " BIGINT, ");
        query.append(ForeignKeyT2+" BIGINT, ");
        query.append("FOREIGN KEY " + "(" +ForeignKeyT1+ ") " +"REFERENCES "+ FirstTable+ "("+PrimaryKeyT1+"), ");
        query.append("FOREIGN KEY " + "(" +ForeignKeyT2+ ") " +"REFERENCES "+ SecondTable+ "(" +PrimaryKeyT2+"), ");
        query.append("PRIMARY KEY "+ "("+ ForeignKeyT1 +", "+ForeignKeyT2 + ")");
        query.append(");");
        System.out.println(query.toString());
        jdbcTemplate.execute(query.toString());
        joinTable(FirstTable,SecondTable, PrimaryKeyT1, PrimaryKeyT2, ForeignKeyT1, ForeignKeyT2);
    }

    private void joinTable(String firstTable,String secondTable, String  primaryKeyT1, String  primaryKeyT2, String  foreignKeyT1, String  foreignKeyT2) {
        repo.save(new ManyToManyRelationTable(firstTable,primaryKeyT1,foreignKeyT1,secondTable,primaryKeyT2,foreignKeyT2));
    }

    @GetMapping("/findData")
    public List<HashMap<String , Object>> findData(@RequestParam Long id,
                                                   @RequestParam String tableA,
                                                   @RequestParam String tableB,
                                                   @RequestParam String tableAB,
                                                   @RequestParam String tableA_PK,
                                                   @RequestParam String tableB_PK,
                                                   @RequestParam String tableA_FK,
                                                   @RequestParam String tableB_FK) {
        String  ConditionIdForTable1 =""+id;
        return findDataUsingJoin(tableA,tableB,tableAB,tableA_PK,tableB_PK,tableA_FK,tableB_FK,ConditionIdForTable1);
    }

/*

    select * from employee
    JOIN employee_project on employee.id=employee_project.employee_id
    Join project on employee_project.project_id=project.id
    where employee.id=1;

 */
    private List<HashMap<String,Object>> findDataUsingJoin(String tableA,String tableB,String tableAB,String tableA_PK,
                                                           String tableB_PK,String tableA_FK,String tableB_FK ,String ConditionIdForTable1){

        ArrayList<String> tableBColumnNameArrayList = new ArrayList<>();
        ArrayList<String> tableColumnNameArrayListNew = new ArrayList<>();
        StringBuilder tableBColumnList = new StringBuilder();

        //run query to get metadata to get column name
        String queryForTableBColumnName = String.format("SELECT * FROM %s LIMIT 1", tableB);

        tableBColumnNameArrayList = getColumnNames(queryForTableBColumnName);
        tableBColumnNameArrayList.forEach(str -> {
                    tableBColumnList.append(" ").append(tableB).append(".").append(str).append(",");
                    tableColumnNameArrayListNew.add(tableB + "." + str);
                }
        );

        ArrayList<String> tableAColumnNameArrayList = new ArrayList<>();
        StringBuilder tableAColumnList = new StringBuilder();
        String queryForTableAColumnName = String.format("SELECT * FROM %s LIMIT 1", tableA);

        tableAColumnNameArrayList = getColumnNames(queryForTableAColumnName);
        tableAColumnNameArrayList.forEach(str -> {
            tableAColumnList.append(" ").append(tableA).append(".").append(str).append(",");
            tableColumnNameArrayListNew.add(tableA + "." + str);
        });

        tableAColumnList.deleteCharAt(tableAColumnList.length() - 1).append(" ");
        String query = String.format("select" + tableBColumnList.toString() +""+tableAColumnList.toString() + "from %s " +
                        "JOIN %s on %s.%s=%s.%s " +
                        "join %s on %s.%s=%s.%s " +
                        "where %s.%s=%s;",
                tableA, tableAB, tableA, tableA_PK, tableAB, tableA_FK, tableB, tableAB,tableB_FK,tableB,tableB_PK,tableA,tableA_PK, ConditionIdForTable1);
        return produceResult(query, tableColumnNameArrayListNew);

    }

    private ArrayList<String> getColumnNames(String rawQuery) {
        ArrayList<String> columnList = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost/bugpoc", "root", "qwerty");

            // create a statement object
            stmt = conn.createStatement();

            // execute the query to retrieve the column names
            rs = stmt.executeQuery(rawQuery);

            // get the metadata for the result set
            ResultSetMetaData rsmd = rs.getMetaData();

            // get the number of columns in the result set
            int numCols = rsmd.getColumnCount();

            // iterate over the columns and print their names
            for (int i = 1; i <= numCols; i++) {
                String colName = rsmd.getColumnName(i);
                columnList.add(colName);
                System.out.println("Column " + i + ": " + colName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // close the resources
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return columnList;
    }

    private List<HashMap<String, Object>> produceResult(String joinQuery, ArrayList<String> tableColumnNameArrayListNew) {
        return jdbcTemplate.query(joinQuery, new RowMapper<HashMap<String, Object>>() {
            @Override
            public HashMap<String, Object> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                HashMap<String, Object> map = new HashMap<>();

                for (int i = 0; i < tableColumnNameArrayListNew.size(); i++) {
                    System.out.println("The Field is: " + tableColumnNameArrayListNew.get(i).toString());
                    map.put(tableColumnNameArrayListNew.get(i), resultSet.getObject(i + 1));
                }
                return map;
            }
        });
    }

}

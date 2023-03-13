package com.example.exampledemo.controller;

import com.example.exampledemo.Repository.EmployeeRepo;
import com.example.exampledemo.Repository.RelationTableRepo;
import com.example.exampledemo.model.Attendance;
import com.example.exampledemo.model.Employee;
import com.example.exampledemo.model.RelationTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class GenerateForeignKey {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RelationTableRepo relationTableRepo;
    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    protected EntityManager em;

    @PostMapping("/addEmployee")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeRepo.save(employee);
    }

    @PostMapping("/addGenerateForeignKey")
    public void addGenerateForeignKey(@RequestBody RelationTable body) {
        String OneTName = body.getFirstTable();
        String FKeyName = body.getRelationType();
        String ManyTName = body.getSecondTable();

        //add a query = ALTER TABLE ManyTName ADD FKeyName LONG;
        StringBuilder tableCreationQuery = new StringBuilder();
        tableCreationQuery.append("ALTER TABLE ");
        tableCreationQuery.append(ManyTName + " ");
        tableCreationQuery.append("ADD ").append(FKeyName).append(" ").append("LONG").append(";");
        System.out.println(tableCreationQuery.toString());
        jdbcTemplate.execute(tableCreationQuery.toString());
        entryInRelationTable(OneTName, ManyTName, FKeyName);
    }

    private void entryInRelationTable(String oneTName, String manyTName, String fKeyName) {
        relationTableRepo.save(new RelationTable(oneTName, fKeyName, manyTName));
    }

    @GetMapping("/findDataUsingInnerJoin")
    public List<HashMap<String, Object>> findDataUsingInnerJoin(@RequestParam Long id,
                                                                @RequestParam String table1,
                                                                @RequestParam String tableColumn1,
                                                                @RequestParam String table2,
                                                                @RequestParam String tableColumn2) throws SQLException {
        String conditionIdForTable1 = "" + id;
        return findDataUsingInnerJoin(table1, tableColumn1, table2, tableColumn2, conditionIdForTable1);
    }

    private List<HashMap<String, Object>> findDataUsingInnerJoin(String table1, String tableColumn1, String table2,
                                                                 String tableColumn2, String conditionIdForTable1) {

        ArrayList<String> table2ColumnNameArrayList = new ArrayList<>();
        ArrayList<String> tableColumnNameArrayListNew = new ArrayList<>();
        StringBuilder table2ColumnList = new StringBuilder();

        //run query to get metadata to get column name
        String queryForTable2ColumnName = String.format("SELECT * FROM %s LIMIT 1", table2);

        table2ColumnNameArrayList = getColumnNames(queryForTable2ColumnName);
        table2ColumnNameArrayList.forEach(str -> {
                    table2ColumnList.append(" ").append(table2).append(".").append(str).append(",");
                    tableColumnNameArrayListNew.add(table2 + "." + str);
                }
        );

        ArrayList<String> table1ColumnNameArrayList = new ArrayList<>();
        StringBuilder table1ColumnList = new StringBuilder();
        String queryForTable1ColumnName = String.format("SELECT * FROM %s LIMIT 1", table1);

        table1ColumnNameArrayList = getColumnNames(queryForTable1ColumnName);
        table1ColumnNameArrayList.forEach(str -> {
            table1ColumnList.append(" ").append(table1).append(".").append(str).append(",");
            tableColumnNameArrayListNew.add(table1 + "." + str);
        });

        table1ColumnList.deleteCharAt(table1ColumnList.length() - 1).append(" ");

        String query = String.format("SELECT " +
                        table2ColumnList.toString() + " " + table1ColumnList.toString() +
//                        "attendance.id, attendance.working_date, attendance.attendance_marked, attendance.eid" +
                        " FROM %s " +
                        "JOIN %s " +
                        "ON %s.%s = %s.%s " +
                        "WHERE %s.%s = %s",
                table1, table2, table1, tableColumn1, table2, tableColumn2, table1, tableColumn1, conditionIdForTable1);
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

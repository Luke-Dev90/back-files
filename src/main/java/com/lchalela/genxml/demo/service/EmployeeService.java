package com.lchalela.genxml.demo.service;

import com.lchalela.genxml.demo.entity.Employee;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAll();
    Employee getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Employee employee, Long id);
    String getReport();
    public void getReportPDF(List<Employee> employeeList, HttpServletResponse response) throws IOException;
    void deleteEmployeeById(Long id);
}

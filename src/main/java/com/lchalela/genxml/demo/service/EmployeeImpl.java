package com.lchalela.genxml.demo.service;

import com.lchalela.genxml.demo.entity.Employee;
import com.lchalela.genxml.demo.repository.EmployeeRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.util.Formatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EmployeeImpl implements EmployeeService{

    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeImpl (EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAll() {
        return this.employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(
                ()->{ throw new NoSuchElementException("Not found employee");}
        );
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        this.employeeRepository.save(employee);
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee, Long id) {
        Employee employee1 = this.employeeRepository.findById(id).orElseThrow( () ->{
            throw new NoSuchElementException("Not found employee");
        });

        if(employee1 != null){
            employee1.setName( employee.getName());
            employee1.setLastName( employee.getLastName());
            employee1.setEnabledEmployee(employee.getEnabledEmployee());
            this.employeeRepository.save(employee1);
        }

        return employee1;
    }

    @Override
    public String getReport() {
        List<Employee> employeeList = this.employeeRepository.findAll();

        String dataApp = System.getProperty("user.home");
        String filePath = dataApp.concat("\\Documents\\reportes\\");

        try{
            File file = new File(filePath);

            if(!file.exists()){
                file.mkdir();
            }

            Formatter formatter = new Formatter(filePath.concat("report.txt"));

            employeeList.stream().filter( e -> e.getEnabledEmployee().equals(true))
                    .collect(Collectors.toList())
                    .forEach( p -> formatter.format("%s %s %s %s %n",
                            p.getId(),
                            p.getName(),
                            p.getLastName(),
                            p.getEnabledEmployee()));

            formatter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath.concat("report.txt");
    }

    @Override
    public void getReportPDF(List<Employee> employeeList, HttpServletResponse response) throws IOException {

        List<Employee> employees = employeeList;

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.BLACK);

            Paragraph p1 = new Paragraph("List Employee", font);
            p1.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p1);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);

            writeEmployeeHeader(table);
            writeEmployeeData(table,employees);

            document.add(table);
            document.close();
    }

    public void writeEmployeeHeader(PdfPTable table){

        //#FA6400
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(250, 100, 0));
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.white);

        cell.setPhrase(new Phrase("ID",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("lastName",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled",font));
        table.addCell(cell);
    }

    public void writeEmployeeData(PdfPTable table,List<Employee> employeeList){
        employeeList.forEach(e -> {
            table.addCell(String.valueOf(e.getId()));
            table.addCell(e.getName());
            table.addCell(e.getLastName());
            table.addCell(String.valueOf(e.getEnabledEmployee()));
        });
    }

    @Override
    public void deleteEmployeeById(Long id) {
        Employee employee = this.employeeRepository.findById(id).orElseThrow( ()-> {
            throw new NoSuchElementException("Employee not found");
        });

        if(employee != null){
            this.employeeRepository.delete(employee);
        }
    }
}

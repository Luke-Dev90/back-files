package com.lchalela.genxml.demo.controller;

import com.lchalela.genxml.demo.entity.Employee;
import com.lchalela.genxml.demo.service.EmployeeService;
import com.lchalela.genxml.demo.util.GeneratorXml;
import static com.lchalela.genxml.demo.util.GeneratorXml.generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private Map<String,Object> response = new HashMap<>();

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("/report")
    public ResponseEntity<FileSystemResource> getReportXml() throws IOException {

        String pathFileGenerate = this.employeeService.getReport();
        Path file = Paths.get(pathFileGenerate);
        FileSystemResource fileResource = new FileSystemResource(file.toFile());

        if(!Files.exists(file)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition","attachtment; filename="+file.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileResource);
    }

    @GetMapping("/report/pdf")
    public void generatePdf(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        DateFormat format = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = format.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_"  + currentDateTime + ".pdf";

        response.setHeader(headerKey,headerValue);
        List<Employee> employeeList = this.employeeService.getAll();

        this.employeeService.getReportPDF(employeeList,response);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        response.clear();
        response.put("Employees", this.employeeService.getAll());
        response.put("timestamp", LocalDate.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeId(@PathVariable Long id){
        response.clear();
        response.put("Employee",this.employeeService.getEmployeeById(id));
        response.put("timestamp", LocalDate.now());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> saveEmployee(@RequestBody Employee employee){
        response.clear();
        response.put("new employee", this.employeeService.saveEmployee(employee));
        response.put("timestamp", LocalDate.now());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,@RequestBody Employee employee){
        response.clear();
        response.put("updated employee", this.employeeService.updateEmployee(employee,id));
        response.put("timestamp",LocalDate.now());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id){
        response.clear();
        this.employeeService.deleteEmployeeById(id);
        response.put("Deleted Employee"," successfully") ;
        response.put("timestamp",LocalDate.now());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}

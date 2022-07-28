package com.lchalela.genxml.demo.util;

import com.lchalela.genxml.demo.entity.Employee;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GeneratorXml {

    public static Transformer generator(List<Employee> employeeList) throws IOException {

        Result result = null;
        Transformer transformer = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            DOMImplementation implementation = builder.getDOMImplementation();

            Document document = implementation.createDocument(null,"business",null);
            document.setXmlVersion("1.0");

            Element employees = document.createElement("employees");

            for(int i = 0; i < employeeList.size(); i++){
                Element employee = document.createElement("employee");

                Element name = document.createElement("name");
                Text textName = document.createTextNode(employeeList.get(i).getName());
                name.appendChild(textName);
                employee.appendChild(name);

                Element lastName = document.createElement("lastName");
                Text textLastName = document.createTextNode(employeeList.get(i).getLastName());
                lastName.appendChild(textLastName);
                employee.appendChild(lastName);
                employees.appendChild(employee);
            }

            document.getDocumentElement().appendChild(employees);
            Source source = new DOMSource(document);
            result = new StreamResult(new File("concesionario.xml"));

            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source,result);

        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return transformer;
    }
}

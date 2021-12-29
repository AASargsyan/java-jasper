package com.hmkcode;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.export.*;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class App
{
    // name and destination of output file e.g. "report.pdf"
    private static String destFileName = "report.pdf";
    public static void main( String[] args ) throws IOException, JRException, FontFormatException {
        System.out.println( "generating jasper report..." );

        // 1. compile template ".jrxml" file
        JasperReport jasperReport1 = getJasperReport("C:\\Users\\asargsyan\\Downloads\\Java-master\\java-jasper\\src\\main\\resources\\Blank_A4.jrxml");
        JasperReport jasperReport2 = getJasperReport("C:\\Users\\asargsyan\\Downloads\\Java-master\\java-jasper\\src\\main\\resources\\Blank_A4_2.jrxml");

        // 2. parameters "empty"
        Map<String, Object> parameters = getParameters();
        Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream("C:\\Users\\asargsyan\\Downloads\\Java-master\\java-jasper\\src\\main\\resources\\MountingConnectionsJournal.xsd.xml"));

        // 3. datasource "java object"
        JasperPrint jasperPrint1 = JasperFillManager.fillReport(jasperReport1, parameters,  new JRXmlDataSource(document, "/mountingConnectionsJournal"));
        JasperPrint jasperPrint2 = JasperFillManager.fillReport(jasperReport2, parameters,  new JRXmlDataSource(document, "/"));

        List<JasperPrint> jasperPrintList = new ArrayList<>();
        jasperPrintList.add(jasperPrint1);
        jasperPrintList.add(jasperPrint2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
        exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
        exporter.exportReport();
        FileOutputStream fos = new FileOutputStream(destFileName);
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();

        final String extension = "jpg";
        final float zoom = 1f;
        String fileName = "report";
//one image for every page in my report
        int pages = jasperPrint1.getPages().size();
        for (int i = 0; i < pages; i++) {
            try(OutputStream out = new FileOutputStream(fileName + "_p" + (i+1) +  "." + extension)){
                BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint1, i,zoom);
                ImageIO.write(image, extension, out); //write image to file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static JasperReport getJasperReport(String name) throws FileNotFoundException, JRException {
        return JasperCompileManager.compileReport(name);
    }
    private static Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "hmkcode");
        return parameters;
    }

}

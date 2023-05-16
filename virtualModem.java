//
//      ΔΗΜΗΤΡΙΟΣ ΦΩΛΑΣ ΔΕΜΙΡΗΣ
//
//      ΑΕΜ: 9415
//
//      ΔΙΚΤΥΑ Ι - ΤΜΗΜΑ ΗΛΕΚΤΟΛΟΓΩΝ ΜΗΧΑΝΙΚΩΝ & ΜΗΧΑΝΙΚΩΝ ΥΠΟΛΟΓΙΣΤΩΝ - 2020
//

import ithakimodem.Modem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;


public class virtualModem {

        public static void main(String[] param) {

                //Κώδικας Μετρήσεων

                //4 Minutes of ECHO response measurements

                List<Integer> echoResponseTime = new ArrayList<Integer>();                                    // List with the Response time of all echos

                System.out.println("5 Minute Echo Measurement started");

                long startUpTime = System.currentTimeMillis();

                while ((System.currentTimeMillis() - startUpTime) < 5*1000*60)                          // 4 Minute continuous measurements of ARQ Application

                        echoResponseTime.add((int)echo.echoResponse("E5313"));

                }

                System.out.println("5 Minute Echo Measurement: Time Elapsed");

                //4 Minutes of ARQ Measurements, Response time & Bit Error Rate (BER)

                List<Integer> nackArr = new ArrayList<Integer>();                                             // List with the amount of NACK requests that were sent until no errors
                List<Integer> arqResponseTimeArr = new ArrayList<Integer>();                                  // List with the Response time for each ARQ
                int ackCounter = 0;                                                                     // Amount of ACK request codes sent
                int totalNackCounter = 0;                                                               // Total amount of NACK request codes sent

                int tempResponseTime = 0;

                System.out.println("5 Minute ARQ Measurement started");

                startUpTime = System.currentTimeMillis();                                               // Time monitor for 4 Minute measurement

                while ((System.currentTimeMillis() - startUpTime) < 5*1000*60) {                        // 4 Minute continuous measurements of ARQ Application

                        long startTimeArq = System.currentTimeMillis();                                 // Variable to Calculate the Response time for each ARQ Packet, until no errors

                        nackArr.add(arq.arqApp("Q3825","R9902"));                   // ARQ Application

                        tempResponseTime = (int)(System.currentTimeMillis() - startTimeArq);            // Response Time Calculator;

                        if (nackArr.get(ackCounter) != -1) {                                            // Connection Error Detection
                                totalNackCounter += nackArr.get(ackCounter);                            //      The application will not take into consideration any data
                                ackCounter++;                                                           //              if there was a Connection Problem with the Modem.
                                arqResponseTimeArr.add(tempResponseTime);                               //
                        }
                        tempResponseTime = 0;
                }

                System.out.println("5 Minute ARQ Measurement: Time Elapsed");

                //      Microsoft Excel File & Spreadsheet creation, with the results from the measurements

                try{
                        XSSFWorkbook Results = new XSSFWorkbook();
                        FileOutputStream out = new FileOutputStream(new File("networksResultsEcho.xlsx"));
                        XSSFSheet echoSheet = Results.createSheet("Echo Response Times");

                        XSSFRow responseTimeRow = echoSheet.createRow(0);
                        Cell titleCell = responseTimeRow.createCell(0);
                        titleCell.setCellValue("Response Time: ");

                        for (int i = 0; i < echoResponseTime.size(); i++) {
                                responseTimeRow = echoSheet.createRow(i+1);
                                Cell RespCell = responseTimeRow.createCell(0);
                                RespCell.setCellValue(echoResponseTime.get(i));
                        }

                        XSSFWorkbook ResultsArq = new XSSFWorkbook();
                        XSSFSheet arqSheet = Results.createSheet("ARQ Results");

                        XSSFRow arqTimeRow = arqSheet.createRow(0);
                        Cell cell0 = arqTimeRow.createCell(0);
                        Cell cell1 = arqTimeRow.createCell(1);
                        Cell cell2 = arqTimeRow.createCell(2);
                        cell0.setCellValue("ARQ Response Time: ");
                        cell1.setCellValue("Amount of NACKs");
                        cell2.setCellValue("Amount of Positive ACKs");



                        for (int i = 0; i < arqResponseTimeArr.size(); i++) {
                                arqTimeRow = arqSheet.createRow(i+1);
                                Cell arqCell = arqTimeRow.createCell(0);
                                arqCell.setCellValue(arqResponseTimeArr.get(i));

                                Cell nackcell = arqTimeRow.createCell(1);
                                nackcell.setCellValue(nackArr.get(i));

                                if (i == 1){
                                        Cell ackCell = arqTimeRow.createCell(2);
                                        ackCell.setCellValue(ackCounter);
                                }
                        }

                        Results.write(out);
                        out.close();
                }
                catch (Exception e){
                        System.out.println(e);
                }

                System.out.println("Excel file created");
        }

}

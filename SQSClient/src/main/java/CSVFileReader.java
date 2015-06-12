
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * @author Joker
 */
public class CSVFileReader {

    private static final String[] FILE_HEADERS = {"X_EIDM_GUID", "AGREE_NUM", "ECA_ACCOUNT_CSN", "CUSTOMER_ACCOUNT_NAME__C"};
    private static final String GUID = "X_EIDM_GUID";
    private static final String AGREE_NUMBER = "AGREE_NUM";
    private static final String ACCOUNT_CSN = "ECA_ACCOUNT_CSN";
    private static final String CUSTOMER_ACCOUNT_NAME = "CUSTOMER_ACCOUNT_NAME__C";


    public static List<String> readCsvFile(String fileName) {
        FileReader reader = null;
        CSVParser csvFileParser = null;
        List<String> SqsMessages = new ArrayList<String>();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADERS);
        ObjectMapper mapper = new ObjectMapper();
        try {

            reader = new FileReader(fileName);
            csvFileParser = new CSVParser(reader, csvFormat);
            List csvRecords = csvFileParser.getRecords();
            System.out.println("The size of csvRecords is " + csvRecords.size());
            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = (CSVRecord) csvRecords.get(i);
                GuidMessage mssg = new GuidMessage();
                mssg.setGuid(record.get(GUID));
                mssg.setAggreementNumber(record.get(AGREE_NUMBER));
                mssg.setAccountCsn(record.get(ACCOUNT_CSN));
                mssg.setCustomerAccName(record.get(CUSTOMER_ACCOUNT_NAME));
                try {
                    String str = mapper.writeValueAsString(mssg);
                    SqsMessages.add(str);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            System.out.println("the size of the list is " + SqsMessages.size());
            for (String mssg : SqsMessages) {

                System.out.println(mssg);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                reader.close();
                csvFileParser.close();
            } catch (JsonMappingException jme) {
                jme.getMessage();
            } catch (JsonGenerationException je) {
                je.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return SqsMessages;
    }

    public static void main(String args[]) {
        //TODO move the file to resources folder
        String fileName = "/Users/Joker/guids.csv";
        CSVFileReader.readCsvFile(fileName);
    }
}

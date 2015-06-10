
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;



/**
 * @author Joker
 *
 */
public class CSVFileReader {

	private static final String[] FILE_HEADERS = {"id","name","abbreviation","country"};
	private static final String STATE_ID ="id";
	private static final String STATE_NAME="name";
	private static final String STATE_ABBR="abbreviation";
	private static final String COUNTRY="country";
	
	
	public static List<QueueMessage> readCsvFile(String fileName){
		FileReader reader = null;
		CSVParser csvFileParser = null;
		List<QueueMessage> mssgs = new ArrayList<QueueMessage>();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADERS);
		try{
			
			reader = new FileReader(fileName);
			csvFileParser = new CSVParser(reader,csvFormat);
			List csvRecords = csvFileParser.getRecords();
			System.out.println("The size of csvRecords is "+csvRecords.size());
			for(int i=1;i< csvRecords.size();i++){
				CSVRecord record = (CSVRecord) csvRecords.get(i);
				QueueMessage mssg = new QueueMessage();
				mssg.setId(Integer.parseInt(record.get(STATE_ID)));
				//System.out.println(record.getId());
				mssg.setName(record.get(STATE_NAME));
				mssg.setCountry(record.get(COUNTRY));
				mssg.setAbbreviation(record.get(STATE_ABBR));
				mssgs.add(mssg);
			}
			
			System.out.println("the size of the list is "+mssgs.size());
			for(QueueMessage mssg:mssgs){
				
				System.out.println(mssg.toString());
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			try{
				reader.close();
				csvFileParser.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
		return mssgs;
	}
	
	public static void main(String args[]){
		String fileName= "/Users/Joker/state_table.csv";
		//File file = new File("state_table.csv");
		//System.out.println(System.getProperty("user.home"));
		
		CSVFileReader.readCsvFile(fileName);
	}
}

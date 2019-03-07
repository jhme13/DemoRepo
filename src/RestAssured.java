import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;



public class RestAssured {

	public static String request;
	
	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
		
		 System.out.println("Latest Branch");

		try {
			
			String filename="AdServer_API.xlsx";
			  String path=System.getProperty("user.dir")+"\\Reports";
			   File file = new File(path+"\\"+filename);
			   FileInputStream inputstream = new FileInputStream(file);
			   XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
			   XSSFSheet sheet= workbook.getSheet("AdServerAPI");
			   int rows=sheet.getLastRowNum()-sheet.getFirstRowNum();
			   for (int i=1; i<rows+1; i++)
			   {
				   Row row=sheet.getRow(i);
				   
				  
				for (int j=1; j < row.getLastCellNum(); j++)
					   
					
				    request=row.getCell(j).getStringCellValue();
				   
			   }
			
            HashMap<String, Object> APIParam= new HashMap();
            APIParam.put("ENFORCE_VPAID", true);
            
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed"
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			String a=conn.getResponseMessage();

			String output;
		
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				
				JSONObject xmlJSONObj = XML.toJSONObject(output);
	            int PRETTY_PRINT_INDENT_FACTOR=4;
				String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	            System.out.println(jsonPrettyPrintString);
				
				
				
				// file write
				Writer writer = null;

				try {
				    writer = new BufferedWriter(new OutputStreamWriter(
				          new FileOutputStream("filename.txt"), "utf-8"));
				    writer.write("Something");
				} catch (IOException ex) {
				    // Report
				} finally {
				   try {writer.close();} catch (Exception ex) {/*ignore*/}
				}
				
				
				   
		
			}

			conn.disconnect();

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  }

		}
}
		 
		
		


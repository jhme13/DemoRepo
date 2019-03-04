import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;

import com.sendgrid.smtpapi.SMTPAPI;
import com.sendgrid.Attachments;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

public class GenarateExcelUsingDatabase {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, SendGridException, MessagingException {
	

		
	Class.forName("org.postgresql.Driver");

		Connection connection = null;

		try {
		
			connection = DriverManager.getConnection("jdbc:postgresql://openstream-database-staging.cmg2oeoqlpk8.us-east-1.rds.amazonaws.com/", "openstream","Clearstream1");

		} catch (SQLException e) {

			System.out.println("Connection Failed");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("database connection Open");
		} else {
			System.out.println("Failed to databaseconnection");
		}
	

	PreparedStatement ps=null;
	ResultSet rs=null;
	//String query = "select campaigns.id,campaign_metadata.placement_id, flight_assets.asset_id, flight_assets.flight_id from campaigns INNER JOIN campaign_metadata ON campaigns.id=campaign_metadata.campaign_id INNER JOIN flight_assets ON campaign_metadata.flight_id=flight_assets.flight_idINNER JOIN ads ON flight_assets.flight_id=ads.flight_id where ads.flight_id in (select f.id from placements p, placement_groups pg, flights f, campaigns c, flight_assets fa where p.id = f.placement_id and p.placement_group_id = pg.id and pg.campaign_id = c.id and f.id = fa.flight_id  and c.campaign_type_id = 1) and config like '%"AD_SPEC":"VPAID"%' and campaigns.state_id=2 group by flight_assets.flight_id,flight_assets.asset_id,campaigns.id,campaign_metadata.placement_id";
	//String query="select campaigns.start_date,campaigns.id,campaigns.state_id,assets.id,assets.name, assets.third_party_ad_tag, assets.third_party_url from campaigns INNER JOIN assets ON campaigns.id=assets.campaign_id where campaigns.state_id=2 and ad_tag_type_id=1and assets.third_party_url is not nulland assets.third_party_url != ''AND assets.third_party_url NOT LIKE ('%clearstream-vast-production%')and assets.third_party_url like ('http%')";
    ps=connection.prepareStatement(query);
    rs= ps.executeQuery();
    
    XSSFWorkbook workbook = new XSSFWorkbook();
    
    Date currentDate = Calendar.getInstance().getTime();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
    String finaldatetime = dateFormat.format(currentDate);
    
    
   
    XSSFSheet sheet= workbook.createSheet("3rd_party_adTags_"+finaldatetime+"");
    XSSFRow Row= sheet.createRow(0);
    XSSFCell cell;
    
    cell=Row.createCell(0);
    cell.setCellValue("Asset ID");
    cell=Row.createCell(1);
    cell.setCellValue("Associated Flight ID");
    cell=Row.createCell(2);
    cell.setCellValue("Campaign ID");
    cell=Row.createCell(3);
    cell.setCellValue("Placement ID");
    int i = 1;
    
    
    
    while(rs.next())
    {
    Row=sheet.createRow(i);
    cell=Row.createCell(0);
   
    String id=rs.getString("asset_id");
    Integer number=Integer.parseInt(id);
    cell.setCellValue(number);
    cell=Row.createCell(1);
    String flight_id=rs.getString("flight_id");
    Integer number1=Integer.parseInt(flight_id);
    cell.setCellValue(number1);
    cell=Row.createCell(2);
    
    String campaign_id=rs.getString("id");
    Integer number2=Integer.parseInt(campaign_id);
    cell.setCellValue(number2);
    cell=Row.createCell(3);
    String placement_id=rs.getString("placement_id");
    Integer number3=Integer.parseInt(placement_id);
    cell.setCellValue(number3);
    i++;
   // String asset_id=rs.getString("id");
    //String asset_name=rs.getString("name");
    //String third_party_url=rs.getString("third_party_url");
    	
    }
    String path=System.getProperty("user.dir")+"\\Reports";
   File file = new File(path);
   
    FileOutputStream strem =new FileOutputStream(new File (file,"activeCampaigns_3rd_party_adTags_"+finaldatetime+"_IST.xlsx"));
    workbook.write(strem);
    strem.close();
    System.out.println("Excel sucessfully generated");
    
    
    
  SendGrid sendgrid = new SendGrid("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");

  SendGrid.Email email = new SendGrid.Email();
  
 email.addTo("jaym@cybage.com");
email.addTo("gauravkali@cybage.com");
email.addCc("supradipd@cybage.com");
email.addCc("narottamc@cybage.com");
// email.addTo("jaym@cybage.com");
email.setFrom("info@clearstream.tv");
  
//Local file name and path.
//String attachmentName = "activeCampaigns_3rd_party_adTags_"+finaldatetime+"_IST.xlsx";
//String path1=System.getProperty("user.dir")+"\\Reports";
//MimeBodyPart attachmentPart = new MimeBodyPart();
//Specify the local file to attach.
//DataSource source = new FileDataSource(path1 + attachmentName);
//attachmentPart.setDataHandler(new DataHandler(source));
//This example uses the local file name as the attachment name.
//They could be different if you prefer.
//attachmentPart.setFileName(attachmentName);
//MimeMultipart.addBodyPart(attachmentPart);


  Attachments attachments2 = new Attachments();
String name="activeCampaigns_3rd_party_adTags_"+finaldatetime+"_IST.xlsx";
//attachments2.setFilename(name);
try
{
email.addAttachment(name, file);
}
catch(Exception e)
{
	
}
  
   
   Date currentDate1 = Calendar.getInstance().getTime();

   SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yyyy");
   String emailformat = dateFormat1.format(currentDate);
   
  email.setSubject("Active Campaigns: 3rd Party Ad tags For "+emailformat+"");
  // email.setHtml("Hi,"+'\n'+"Please find the attachment of 3rd party ad tags  for "+emailformat+" IST."+'\n'+"I have also attached same in CSOS-412 as well."+'\n'+"Please let us know if you need any further information.");
  email.setText("Hi Jay / Gaurav,"+'\n'+'\n'+"Please find the attachment of 3rd party ad tags  for "+emailformat+" IST."+'\n'+'\n'+"I have also attached same in CSOS-412 as well."+'\n'+'\n'+"Please let us know if you need any further information."+'\n'+'\n'+'\n'+"Regards,"+'\n'+"Jay Mevada, Sr. QA Engineer"+'\n'+"Cybage Software Pvt. Ltd. (An ISO 27001 Company)"+'\n'+"Gandhinagar, India"+'\n'+"Phone (O):91-79-66737000, Ext:5327"+'\n'+"Fax: 91-79-66737001");
   System.out.println("Hi Jay / Gaurav,"+'\n'+'\n'+"Please find the attachment of 3rd party ad tags  for "+emailformat+" IST."+'\n'+'\n'+"I have also attached same in CSOS-412 as well."+'\n'+'\n'+"Please let us know if you need any further information."+'\n'+'\n'+'\n'+"Regards,"+'\n'+"Jay Mevada, Sr. QA Engineer"+'\n'+"Cybage Software Pvt. Ltd. (An ISO 27001 Company)"+'\n'+"Gandhinagar, India"+'\n'+"Phone (O):91-79-66737000, Ext:5327"+'\n'+"Fax: 91-79-66737001");
  //SendGrid.Response response = sendgrid.send(email);
   System.out.println("Mail successfully Sent");
    
    //ExcelUtils excel= new ExcelUtils();
    //String[] dataToWrite = {"asset_id","asset_name","third_party_url"};
 //excel.writeExcel("D:\\", "Test11111.xlsx", "3rd_party_adTags", dataToWrite);
   
        try{
            if(rs != null) rs.close();
            if(ps != null) ps.close();
            if(connection != null) connection.close();
        } catch(Exception ex){}
    }
	}




	


	





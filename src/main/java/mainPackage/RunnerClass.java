package mainPackage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RunnerClass {
	public static String messageBody = "";
	 public static String mailSubject = "";
	 public static String currentDate = "";

	public static void main(String[] args) throws IOException, GeneralSecurityException{
		
		 LocalDate dateObj = LocalDate.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
         currentDate = dateObj.format(formatter);
		
		if(SheetsQuickstart.addingDatatoSheets() == false) {
			mailSubject = "Data Table summary for "+ currentDate + " is Failed";
			messageBody = "Hi All,\n Data adding to the sheet is Failed.\n\n Regards,\n HomeRiver Group.";
			System.out.println("Error while adding data to the sheet");
		}
		else {
			mailSubject = "Data Table summary for "+ currentDate + " is Failed";
			messageBody = "Hi All,\n Data added to the sheet successfully.\n\n Regards,\n HomeRiver Group.";
			System.out.println("Adding data to the sheet is successful");
		}
		
		//Send Mail
		//CommonMethods.sendFileToMail();

	}

}

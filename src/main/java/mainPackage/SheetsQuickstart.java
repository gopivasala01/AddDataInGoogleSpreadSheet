package mainPackage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class SheetsQuickstart {

	public static String currentTime="";
	public static String updatedDate="";
	public static String[][] DateFromDatabase;
	public static String connectionUrl = "jdbc:sqlserver://azrsrv001.database.windows.net;databaseName=HomeRiverDB;user=service_sql02;password=xzqcoK7T;encrypt=true;trustServerCertificate=true;";
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens/path";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/google-credentials.json";
    private static final String SPREADSHEET_ID = "1EMaAmoOqalGaY9Xj8XzQ_5cC97VpKsH8qkBxHhPNt98";
    private static final String RANGE = "Sheet3!A3:D";
    private static final String DATE = "Sheet3!B1";

    public static boolean addingDatatoSheets() throws IOException, GeneralSecurityException {
        // Connect to MS SQL Server
        try (Connection connection = DriverManager.getConnection("jdbc:sqlserver://azrsrv001.database.windows.net;databaseName=HomeRiverDB;", "service_sql02", "xzqcoK7T")) {
            // Query MS SQL Server
            String sqlQuery = "select [Table Name],[Loads Type],[MAX AsOfDate],[PW Timing Filter] from DBO.HomeRiverTableLastUpdateTimeStamp";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                // Build Google Sheets API service
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                Sheets sheetsService =
                        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                // Clear existing data in Google Sheets
                try {
                	//clearing date
                	 sheetsService.spreadsheets().values()
                     .clear(SPREADSHEET_ID, DATE, new ClearValuesRequest())
                     .execute();
                	 
                	//clearing remaining data
                	 sheetsService.spreadsheets().values()
                     .clear(SPREADSHEET_ID, RANGE, new ClearValuesRequest())
                     .execute();
                	 
                	 System.out.println("Cleared existing data in the sheet");
                }
               catch(Exception e) {
            	   System.out.println("Error while clearing existing data in the sheet");
            	return false;
               }

                // Add data to Google Sheets
                List<List<Object>> values = new java.util.ArrayList<>();
                while (resultSet.next()) {
                    // Assuming column1, column2, column3 are of type String
                    String col1 = resultSet.getString("Table Name");
                    String col2 = resultSet.getString("Loads Type");
                    String col3 = resultSet.getString("MAX AsOfDate");
                    String col4 = resultSet.getString("PW Timing Filter");

                    values.add(Arrays.asList(col1, col2, col3,col4));
                }
                
                try {
                	 ValueRange body = new ValueRange().setValues(values);
                     sheetsService.spreadsheets().values()
                             .update(SPREADSHEET_ID, RANGE, body)
                             .setValueInputOption("RAW")
                             .execute();
                     
                     
                  // Create a ValueRange object with the date value
                     getCurrentDateTime();
                     ValueRange valueRange = new ValueRange().setValues(Arrays.asList(Arrays.asList(updatedDate)));

                     // Update the cell with the date value
                     sheetsService.spreadsheets().values()
                             .update(SPREADSHEET_ID, DATE, valueRange)
                             .setValueInputOption("RAW")
                             .execute();
                 	
                }
                catch(Exception e) {
              	   
              	   	return false;
                }
               
            }
        } catch (SQLException e) {
            e.printStackTrace();
        	return false;
        }
		return true;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
    	      throws IOException {
    	    // Load client secrets.
    	    InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    	    if (in == null) {
    	      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    	    }
    	    GoogleClientSecrets clientSecrets =
    	        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    	    // Build flow and trigger user authorization request.
    	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    	        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    	        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
    	        .setAccessType("offline")
    	        .build();
    	    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    	    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    	  }
    
    public static boolean getCurrentDateTime()
    {
    	String sqlQuery = "select top 1 [LastUpdated] from DBO.HomeRiverTableLastUpdateTimeStamp";
    	try
    	{
    	        Connection con = null;
    	        Statement stmt = null;
    	        ResultSet rs = null;
    	            //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	            con = DriverManager.getConnection(connectionUrl);
    	            String SQL = sqlQuery;
    	            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    	           // stmt = con.createStatement();
    	            stmt.setQueryTimeout(60);
    	            rs = stmt.executeQuery(SQL);
    	            int rows =0;
    	            if (rs.last()) 
    	            {
    	            	rows = rs.getRow();
    	            	// Move to beginning
    	            	rs.beforeFirst();
    	            }
    	            if(rows>1 || rows == 0) {
    	            	return false;
    	            	
    	            }
    	            System.out.println("No of Rows = "+rows);
    	            DateFromDatabase = new String[rows][1];
    	            int  i=0;
    	            while(rs.next())
    	            {
    	  
    	            	String 	ID = rs.getObject(1).toString();
    	            	
    	              //stateCode
    	                try 
    	                {
    	                	if(ID==null)
    	                		DateFromDatabase[i][0] = "";
    	                	else
    	                	{
    	                		DateFromDatabase[i][0] = ID;
    	                		updatedDate = DateFromDatabase[i][0];
    	                	}
    	                }
    	                catch(Exception e)
    	                {
    	                	DateFromDatabase[i][0] = "";
    	                }
    	            }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
}
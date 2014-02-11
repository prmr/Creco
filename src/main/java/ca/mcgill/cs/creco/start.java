package ca.mcgill.cs.creco;

import java.lang.StringBuilder;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.boot.SpringApplication;

import ca.mcgill.cs.creco.util.DataPath;

public class start {

    public static void main(String[] args) throws IOException {
       System.out.println("Hey there");
       URL url;
       String path = DataPath.get();
       System.out.println(path);
       String[] dataset = new String[]{"food","health","money","babiesKids","homeGarden","appliances","cars","electronicsComputers"};

       try {
    	   for(int i=0;i<dataset.length;i++)
    	   {


String inputLine;

	//save to this filename
StringBuilder main_path = new StringBuilder();
main_path.append(path);
main_path.append(dataset[i]);
String fileName = main_path.toString();
File file = new File(fileName);

if (!file.exists()) {
	file.createNewFile();
System.out.println("Created a file");
}

{
	Date date1 = new Date();
	System.out.println(date1);
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	String temporary = sdf.format(file.lastModified());
	Date date2 =null;
	date2 = sdf.parse(temporary);
	long diff = date1.getTime() - date2.getTime();
	long diffSeconds = diff / 1000 % 60;
	long diffMinutes = diff / (60 * 1000) % 60;
	long diffHours = diff / (60 * 60 * 1000) % 24;
	long diffDays = diff / (24 * 60 * 60 * 1000);

	System.out.print(diffDays + " days, ");
	System.out.print(diffHours + " hours, ");
	System.out.print(diffMinutes + " minutes, ");
	System.out.print(diffSeconds + " seconds.");
	if(diffDays>0)
	{
		   Thread.sleep(2000);
		   StringBuilder abc = new StringBuilder();
		   abc.append("http://api.consumerreports.org/v0.1/products.(category.name=");
		   abc.append(dataset[i]);
		   abc.append(").json?api_key=nafbaswcjqtpzfsqmka3hann");
		   String use_this = abc.toString();
		url = new URL (use_this);   
//url = new URL("http://api.consumerreports.org/v0.1/categories.json?api_key=nafbaswcjqtpzfsqmka3hann");
	URLConnection conn = url.openConnection();
	BufferedReader br = new BufferedReader(
         new InputStreamReader(conn.getInputStream()));

	//use FileWriter to write file
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);

	while ((inputLine = br.readLine()) != null) {
		bw.write(inputLine);
	}

	bw.close();
	br.close();
	}
}

    	   }
System.out.println("Done");
    }
       catch (MalformedURLException e) {
			e.printStackTrace();
    }
       catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

    
    
    
 }


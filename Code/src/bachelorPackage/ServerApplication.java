package bachelorPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;


public class ServerApplication {

	public static void main(String[] args) throws SQLException, InterruptedException, IOException {
		String FILENAME = "/home/mouad/Desktop/logs.txt";
		BufferedReader br = null;
		FileReader fr = null;
		while(!Login.connexionState) {
			Login.main(null);
		}
		SqlFiles.main(null);

		try {
			//Setting up the connexion
			Connection con = null;
			String conUrl = "jdbc:sqlserver://"+Login.Host+"; databaseName="+Login.databaseName+"; user="+Login.user+"; password="+Login.password+";";;
			con = DriverManager.getConnection(conUrl);
			con.setAutoCommit(false);

			//Statement stmt = con.createStatement( );
			CallableStatement pstmt ;

			//setting the buffer to read from the file 
			br = new BufferedReader(new FileReader(FILENAME));
			String sCurrentLine;
			String output="";

			//Id is going to be the primary key of my table
			int id =0;

			//going throw the file line by line
			while ((sCurrentLine = br.readLine()) != null) {
				// split the line of my file according to ]
				// some lines contains more strings then others 
				// so I'm doing different cases 
				if(sCurrentLine.split("]").length==5){

					//this sleep function will let the insertion wait for some ammount of time
					try {
						Thread.sleep(100);              //1000 milliseconds is one second.
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//this brachetCnt is going to be iterate of my strings splitted with the bracket ]
					//to eliminate the strings I don't need and select my attributes 
					int bracketCnt=0;
					for(int i=0;i<sCurrentLine.length()-3;i++){
						if(sCurrentLine.charAt(i)=='['){
							bracketCnt++;
							while(sCurrentLine.charAt(i)!=']'){
								if(bracketCnt==2){
									if(sCurrentLine.charAt(i)=='T'){
										output=output + "]";
										i++;
										output=output + " ";
										output=output + "[";
									}
								}
								if(bracketCnt==2 || bracketCnt==4 ){
									output=output + sCurrentLine.charAt(i);							}
								i++;
							}
							if(sCurrentLine.charAt(i)==']'&& (bracketCnt==2 || bracketCnt==4 )){
								output=output + sCurrentLine.charAt(i);
								output=output + " ";;
							}	
						}
						if(bracketCnt==4){
							if(sCurrentLine.charAt(i)==']'){
								i=i+2;
								output=output + "[";
							}
							if(sCurrentLine.charAt(i+1)=='t'&&sCurrentLine.charAt(i+2)=='o'&&sCurrentLine.charAt(i+3)=='o'&&sCurrentLine.charAt(i+4)=='k'){
								output=output + "]";
								output=output + " ";
								i=i+6;
								output=output + "[";
							}
							output=output + sCurrentLine.charAt(i);
						}
					}
					output=output + "]";
					bracketCnt=0;

					//selecting the attributes 
					String[] parts = output.split("]");
					parts[0]=parts[0].substring(1);
					parts[1]=parts[1].substring(2);
					parts[2]=parts[2].substring(2);
					parts[3]=parts[3].substring(2);
					parts[4]=parts[4].substring(2);
					parts[4]=parts[4].replace(',', '.');

					//when cleaning the data we found that some latencies contains double dots so we 
					//eliminated them
					if((count( parts[4],'.')>1)){
						parts[4]="0";
						System.out.println("parts inside" + parts[4]);

					}

					//preparing for the call of the procedure
					pstmt = con.prepareCall("{call insertionPr(?,?,?,?,?,?)}");	
					pstmt.setString(1, id+"");
					pstmt.setString(2, parts[0]);	
					pstmt.setString(3, parts[1]);
					pstmt.setString(4, parts[2]);	
					pstmt.setString(5, parts[3]);	
					pstmt.setString(6, parts[4]);	

					//incrementing the id for the next record
					id++;
					pstmt.execute();

					//set my output to empty string again
					output="";
				}

				//second case if I have 4 stings seperated by ]
				else if (sCurrentLine.split("]").length==4){

					//this is the bracket counter whcih will iterate over my splitted strings 
					// and select my attributes
					int bracketCnt=0;
					for(int i=0;i<sCurrentLine.length()-3;i++){
						if(sCurrentLine.charAt(i)=='['){
							bracketCnt++;
							while(sCurrentLine.charAt(i)!=']'){
								if(bracketCnt==2){
									if(sCurrentLine.charAt(i)=='T'){
										output=output + "]";
										i++;
										output=output + " ";
										output=output + "[";
									}
								}
								if(bracketCnt==2 || bracketCnt==3 ){
									output=output + sCurrentLine.charAt(i);							}
								i++;
							}
							if(sCurrentLine.charAt(i)==']'&& (bracketCnt==2 || bracketCnt==3 )){
								output=output + sCurrentLine.charAt(i);
								output=output + " ";;
							}	
						}
						if(bracketCnt==3){
							if(sCurrentLine.charAt(i)==']'){
								i=i+2;
								output=output + "[";
							}
							if(sCurrentLine.charAt(i+1)=='t'&&sCurrentLine.charAt(i+2)=='o'&&sCurrentLine.charAt(i+3)=='o'&&sCurrentLine.charAt(i+4)=='k'){
								output=output + "]";
								output=output + " ";
								i=i+6;
								output=output + "[";
							}
							output=output + sCurrentLine.charAt(i);
						}
					}
					output=output + "]";
					bracketCnt=0;

					//selecting the attributes 
					String[] parts = output.split("]");
					parts[0]=parts[0].substring(1);
					parts[1]=parts[1].substring(2);
					parts[2]=parts[2].substring(2);
					parts[3]=parts[3].substring(2);
					parts[4]=parts[4].substring(2);
					parts[4]=parts[4].replace(',', '.');

					//when cleaning the data we found that some latencies contains double dots so we 
					//eliminated them	
					if((count( parts[4],'.')>1)){
						parts[4]="0";
						System.out.println("parts inside" + parts[4]);
					}

					//preparing the call of the procedure 
					pstmt = con.prepareCall("{call insertionPr(?,?,?,?,?,?)}");	
					pstmt.setString(1, id+"");
					pstmt.setString(2, parts[0]);	
					pstmt.setString(3, parts[1]);
					pstmt.setString(4, parts[2]);	
					pstmt.setString(5, parts[3]);	
					pstmt.setString(6, parts[4]);	
					id++;
					pstmt.execute();
					output="";
				}
				else {
					//this case does not exist normally!!  we did some tests on the text file to see 
					//what are different cases of strings length we only found length 5 and 4 but 
					//we keep this else in case there is a ligne in the file we couldn't see
					System.err.println();
				}
				con.commit();
			}
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	//this method is used to count the number of dots in a string
	public static int count( final String s, final char c ) {
		final char[] chars = s.toCharArray();
		int count = 0;
		for(int i=0; i<chars.length; i++) {
			if (chars[i] == c) {
				count++;
			}
		}
		return count;
	}

}
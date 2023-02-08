package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
     @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        JsonObject result = new JsonObject();

        try {
            
            // Create JSON arrays that will hold our data          
            JsonArray prodNums = new JsonArray();
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();
            
            // Initializes the reader and csvReader
            Reader reader = new StringReader(csvString);
            CSVReader csvReader = new CSVReader(reader);
            
            // Gets the first row and stores all the headings
            String[] row = csvReader.readNext();
            
            for ( String headings : row ) {
                colHeadings.add(headings);
            }
                         
            // Gets next row
            row = csvReader.readNext();
               
            // If row does not exist, stop the while loop
            while(row != null) {
               
               // Store the first column in the prodNums
               prodNums.add(row[0]);
               
               // Create the JSON array to store the innerData
               JsonArray innerData = new JsonArray();
               
               // Looping through all other rows
               for(int j = 1; j < row.length; j++ ) {
                    final String x = row[j];

                    if ( colHeadings.toArray()[j].equals("Episode") || colHeadings.toArray()[j].equals("Season")) {
                        final int num = Integer.parseInt(x);
                        innerData.add(num);
                    } else {
                        innerData.add(x);
                    }
                    
                }
               
                // Adding innerData to the data json array
                data.add(innerData);
                
                // Adding all the json arrays to the json object
                result.put("ProdNums", prodNums);
                result.put("ColHeadings", colHeadings);
                result.put("Data", data);
               
                // Getting the next row
                row = csvReader.readNext();
            }
        }
        catch (Exception e) {
        }
        
        // Returns the json as a string
        return Jsoner.serialize(result);
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = "";
        
        try {
            
            // Create a json object from the jsonString
            JsonObject json = Jsoner.deserialize(jsonString, new JsonObject());
            
            // Initializes the writer and csvWriter
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            
            // Read the json array from the json object 
            JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray prodNum = (JsonArray) json.get("ProdNums");
            JsonArray data = (JsonArray) json.get("Data");

            // Convert the JsonArray to a String array
            // Thank you @waxwing
            // https://stackoverflow.com/a/1018798
            String[] headings = Arrays.copyOf(colHeadings.toArray(), colHeadings.toArray().length, String[].class);

            // Writing the headings to the csvWriter
            csvWriter.writeNext(headings);
            
            // Looping through the data jsonArray
            for(int i = 0; i < data.size(); i++) {
                // Creating a String array with the size of colHeadings
                String[] arr = new String[colHeadings.size()];
                
                // Storing the prodNum as the first element in the array
                arr[0] = (String) prodNum.get(i);
                
                // Getting the innerData
                JsonArray innerData = ((JsonArray) data.get(i));
                
                // Looping through all the innerData and storing that information into the String array
                for(int j = 1; j <= innerData.size(); j++) {
                    // Getting the column from the data at index i and the column from index j-1
                    String str = ((JsonArray) data.get(i)).get(j-1).toString();
                    
                    // If we're at the heading "Episode" in the array, format the number to have a leading 0. If not, just save the string into the array
                    if(headings[j].equals("Episode")) {
                        arr[j] = String.format("%02d", Integer.parseInt(str));
                    } else {
                        arr[j] = str;
                    }
                }
                // Write the array out to the csvWriter
                csvWriter.writeNext(arr);
            }
            
            // Save the writer string value to the result
            result = writer.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        // Return the result
        return result.trim();
        
    }
    
}
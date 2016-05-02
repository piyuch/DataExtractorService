package task1.data.extractor;

import java.io.File;
import java.io.IOException;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class extracts data from an extraction.booking.html file.
 * The extracted data contains the following information:
 * nameOfHotel, addressOfHotel, hotelRating, scoreOfHotel, reviewsOfHotel, descriptionOfHotel
 * roomCategories, hotelAlternatives 
 * 
 * The results can be seen on the console and also written to a json file.
 * 
 * After running the code the Result Output should look like:
 * 
 * {
 * "Hotel Rating" : "5 Star",
 * "Hotel Name" : "Kempinski Hotel Bristol Berlin",
 * "Room Categories" : [ "Suite with Balcony", "Classic Double or Twin Room", "Superior Double or Twin Room", "Deluxe Double Room", "Deluxe Business Suite", "Junior Suite", "Family Room" ],
 * "Alternative Hotels" : [ "Hotel Adlon Kempinski Berlin", "Grand Hyatt Berlin", "Sofitel Berlin Kurfürstendamm", "Hilton Berlin" ],
 * "Hotel Address" : "Kurfürstendamm 27, Charlottenburg-Wilmersdorf, 10719 Berlin, Germany",
 * "Review Points" : "8.3",
 * "Hotel Reviews" : "1401 reviews",
 * "Hotel Description" : "This 5-star hotel on Berlin Kurfürstendamm shopping street offers elegant rooms, an indoor pool and great public transport links. It is 600 metres from the Gedächtniskirche Church and Berlin Zoo.Kempinski Hotel Bristol Berlin offers air-conditioned rooms with large windows, modern bathrooms and international TV channels. Bathrobes are provided. Free WiFi is available in all areas and high-speed WiFi access can be booked at an additional cost.Gourmet cuisine is served in the popular Kempinski Grill. Reinhard's brasserie offer light cuisine and a terrace overlooking Kurfürstendamm. Guests can enjoy drinks in the Gobelin Halle lounge or in the Bristol Bar.Kempinski Bristol Berlin spa includes a sauna, steam room and gym. Massages and beauty treatments can also be booked here. The hotel's business centre can be used free of charge.Uhlandstraße Underground Station is just outside the Kempinski front door. The KaDeWe shopping mall is 2 stops away.We speak your language!This property has been on Booking.com since 17 May 2010. Hotel Rooms: 301, Hotel Chain: Kempinski"
 * }
 *
 * 
 * @author piyush
 *
 */

public class dataExtractor {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		    ClassLoader classLoader = dataExtractor.class.getClassLoader();
		    File input = new File(classLoader.getResource("extraction.booking.html").getFile());
		    File output = new File(classLoader.getResource("extract.json").getFile());
			Document doc = Jsoup.parse(input, "UTF-8");
			
			String nameOfHotel = extractHotelName(doc);
			String addressOfHotel = extractHotelAddress(doc);
	
		    String hotelRating =extractHotelRating(doc);
			
		    String scoreOfHotel = extractHotelScore(doc);
			String reviewOfHotel = extractNumberOfReviews(doc);
			String description = extractHotelDescription(doc);	
			
			JSONArray roomCategories = roomCategories(doc);			
			JSONArray alternatives = alternativeHotels(doc);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("Hotel Name", nameOfHotel);
			jsonObj.put("Hotel Address", addressOfHotel);
			jsonObj.put("Review Points", scoreOfHotel);
			jsonObj.put("Hotel Reviews", reviewOfHotel);
			jsonObj.put("Hotel Description", description);
	        jsonObj.put("Hotel Rating", hotelRating);
			jsonObj.put("Room Categories", roomCategories);
			jsonObj.put("Alternative Hotels", alternatives);
			
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj));
			
			try {
				ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
				writer.writeValue(output,jsonObj);
			} catch (IOException e) {
				e.printStackTrace();
			}					
	}

	@SuppressWarnings("unchecked")
	private static JSONArray alternativeHotels(Document doc) {
		Element column1 = doc.getElementById("althotelCol_1");
		Elements e = column1.select("a");
		String alernativeHotel1 = e.get(0).text().toString();
	
		Element column2 = doc.getElementById("althotelCol_2");
		Elements e2 = column2.select("a");
		String alernativeHotel2 = e2.get(0).text().toString();
		
		Element column3 = doc.getElementById("althotelCol_3");
		Elements e3 = column3.select("a");
		String alernativeHotel3 = e3.get(0).text().toString();
			
		Element column4 = doc.getElementById("althotelCol_4");
		Elements e4 = column4.select("a");
		String alernativeHotel4 = e4.get(0).text().toString();
		
		JSONArray list = new JSONArray();
		list.add(alernativeHotel1);
		list.add(alernativeHotel2);
		list.add(alernativeHotel3);
		list.add(alernativeHotel4);
		
		return list;
	}

	@SuppressWarnings("unchecked")
	private static JSONArray roomCategories(Document doc) {
		Elements table = doc.getElementsByClass("ftd");
		Elements elementTable = table.select("td");		   
		JSONArray list = new JSONArray();
		for (Element tr : elementTable) {
			  list.add(tr.text());
	       }		  
		return list;
	}

	private static String extractHotelDescription(Document doc) {
		Elements hotelDescription = doc.getElementsByClass("hotel_description_wrapper_exp");
		Elements elementd = hotelDescription.select("p");	
		
		String description = "";
		for (Element x: elementd) {
			description+=x.text(); 
		}
		return description.toString().replace("\u2019s","");
	}

	private static String extractNumberOfReviews(Document doc) {
		Elements hotelReviews = doc.getElementsByClass("score_from_number_of_reviews");
		Elements elementReviews = hotelReviews.select("span");	
		String reviewOfHotel = elementReviews.get(0).text().toString();
		return reviewOfHotel.replaceAll("Score from ","");
	}

	private static String extractHotelScore(Document doc) {
		Elements hotelScore = doc.getElementsByClass("js--hp-scorecard-scoreval");
		Elements elements = hotelScore.select("span");	
		String scoreOfHotel = elements.get(0).text().toString();
		return scoreOfHotel;
	}

	private static String extractHotelRating(Document doc) {
		Elements hotelclassification = doc.getElementsByClass("hp__hotel_ratings");
		String elementclassification = hotelclassification.select("i").attr("class");    
		String ratingStar = elementclassification.replace("b-sprite stars ratings_stars_", "").replace("  star_track","");
	    return ratingStar + " Star";
	
	}

	private static String extractHotelAddress(Document doc) {
		Element hotelAddress = doc.getElementById("hp_address_subtitle");
		Elements elementAddress = hotelAddress.select("span");	
		String addressOfHotel = elementAddress.get(0).text().toString();
		return addressOfHotel;
	}

	private static String extractHotelName(Document doc) {
		Element hotelName = doc.getElementById("hp_hotel_name");
		Elements elementName = hotelName.select("span");	
		String nameOfHotel = elementName.get(0).text().toString();
		return nameOfHotel;
	}
}

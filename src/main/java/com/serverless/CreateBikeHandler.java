package com.serverless;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.serverless.DataAccessLayer.Bike;
import com.serverless.LocationServices.OpenStreetMapUtils;
import com.serverless.LocationServices.GeoHash;

public class CreateBikeHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");
		
      try {

			
    	  LOG.info("Going to map body: " + input.get("body"));
          // get the 'body' from input
          JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

          // create the Bike object for post
          // need to put some validation/error handling in here in the event a value isn't 
          // posted - getting java null pointer exception 
          LOG.info("Creating new bike");
          Bike bike = new Bike();
          bike.setDescription(body.get("description").asText());
          bike.setGender(body.get("gender").asText());
          bike.setFrameSize(body.get("frameSize").asText());
          bike.setCondition(body.get("condition").asText());
          bike.setAddress(body.get("address").asText());
          
          LOG.info("Going for lat/long");
          Map<String, Double> coords;
          coords = OpenStreetMapUtils.getInstance().getCoordinates(body.get("address").asText());
          LOG.info("latitude :" + coords.get("lat"));
          LOG.info("longitude:" + coords.get("lon"));
          
          bike.setLongitude(coords.get("lon"));
          bike.setLatitude(coords.get("lat"));
          
          GeoHash.getInstance();
          bike.setLocation(GeoHash.toHashString(GeoHash.geohash(coords.get("lat"), coords.get("lon"), 60)));
          
          LOG.info("Going for bike save");

          bike.save(bike);
          
          LOG.info("Bike saved");

          // send the response back
	  		return ApiGatewayResponse.builder()
	  				.setStatusCode(200)
	  				.setObjectBody(bike)
	  				.setHeaders()
	  				.build();

      } catch (Exception ex) {
    	  LOG.error("Error in saving bike: " + ex);

          // send the error response back
    			Response responseBody = new Response("Error saving bike: ", input);

    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody(responseBody)
    					.setHeaders()
    					.build();
      }
	}
}
package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.serverless.DataAccessLayer.Bike;
import com.serverless.LocationServices.GeoHash;
import com.serverless.LocationServices.OpenStreetMapUtils;

public class ListBikesNearbyHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		// This entire section needs to be redone. This is using a substring of the
		// geohash to get "nearby".
		// We really need to calculate the 8 boxes around the point of interest.
		// https://acloudguru.com/blog/engineering/location-based-search-results-with-dynamodb-and-geohash
		// https://www.mapzen.com/blog/geohashes-and-you/
		// https://stackoverflow.com/questions/45091236/geohash-java-search-nearby-latlongs
		// https://github.com/kungfoo/geohash-java
		// https://github.com/amazon-archives/dynamodb-geo
		try {

			LOG.debug("Trying....");
			// get the 'pathParameters' from input
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			LOG.debug(pathParameters + " " + input);
			String address = pathParameters.get("address");
			LOG.debug("Address:" + address);

			Map<String, String> queryStringParameters = (Map<String, String>) input.get("queryStringParameters");

			String range = queryStringParameters.get("range");
			
			if (range == null) {
				range = "0";
			}

			Map<String, Double> coords;
			coords = OpenStreetMapUtils.getInstance().getCoordinates(address);
			LOG.debug("latitude :" + coords.get("lat"));
			LOG.debug("longitude:" + coords.get("lon"));
			LOG.debug("range: " + range);

			GeoHash.getInstance();
			String geoHash = GeoHash.toHashString(GeoHash.geohash(coords.get("lat"), coords.get("lon"), 60));

			LOG.debug("geohash: " + geoHash);
			LOG.debug("geohash substring: " + geoHash.substring(0, Integer.parseInt(range) * 5));
			// get all bikes
			List<Bike> bikes = new Bike().listNearby(geoHash.substring(0, Integer.parseInt(range) * 5));

			// send the response back
			return ApiGatewayResponse.builder().
					setStatusCode(200)
					.setObjectBody(bikes)
					.setHeaders()
					.build();
			
		} catch (Exception ex) {
			LOG.error("Error in listing bikes nearby: " + ex);

			// send the error response back
			Response responseBody = new Response("Error listing bikes nearby: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders()
					.build();
		}
	}
}
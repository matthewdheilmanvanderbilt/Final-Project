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

public class ListBikesHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		
		
		try {
			// get all bikes
			List<Bike> bikes = new Bike().list();

			// send the response back
			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(bikes)
					.setHeaders().build();
		} catch (Exception ex) {
			LOG.error("Error getting all bikes: " + ex);

			// send the error response back
			Response responseBody = new Response("Error getting all bikes: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders()
					.build();
		}
	}
}
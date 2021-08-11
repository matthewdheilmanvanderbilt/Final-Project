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

import com.serverless.DataAccessLayer.Bike;

public class GetBikeHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		

    try {
        LOG.info("Trying....");
        // get the 'pathParameters' from input
        Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
        LOG.info("1");
        LOG.info(pathParameters);
        String bikeId = pathParameters.get("id");
        LOG.info(" 2");
       
        // get the Bike by id
        Bike bike = new Bike().get(bikeId);
        LOG.info(" Didn't get here");

        // send the response back
        if (bike != null) {
          return ApiGatewayResponse.builder()
      				.setStatusCode(200)
      				.setObjectBody(bike)
      				.setHeaders()
      				.build();
        } else {
          return ApiGatewayResponse.builder()
      				.setStatusCode(404)
  					.setObjectBody("Bike with id: '" + bikeId + "' not found.")
      				.setHeaders()
      				.build();
        }
    } catch (Exception ex) {
        LOG.error("Error getting bike: " + ex);

        // send the error response back
  			Response responseBody = new Response("Error getting bike: ", input);
  			return ApiGatewayResponse.builder()
  					.setStatusCode(500)
  					.setObjectBody(responseBody)
  					.setHeaders()
  					.build();
    }
	}
}
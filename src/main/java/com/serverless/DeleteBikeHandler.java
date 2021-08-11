
package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Collections;
import java.util.Map;

import com.serverless.DataAccessLayer.Bike;

public class DeleteBikeHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

    try {
        // get the 'pathParameters' from input
        Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
        String bikeId = pathParameters.get("id");

        // get the Product by id
        Boolean success = new Bike().delete(bikeId);

        // send the response back
        if (success) {
          return ApiGatewayResponse.builder()
      				.setStatusCode(204)
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
        LOG.error("Error in deleting bike: " + ex);

        // send the error response back
  			Response responseBody = new Response("Error deleting bike: ", input);
  			return ApiGatewayResponse.builder()
  					.setStatusCode(500)
  					.setObjectBody(responseBody)
  					.setHeaders()
  					.build();
    }
	}
}
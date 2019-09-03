package com.pikachugo.RestManager.connector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikachugo.RestManager.util.ConstructDataAsliRI;
import com.pikachugo.RestManager.util.ConstructDataDUKCAPIL;
import com.pikachugo.RestManager.util.ConstructDataNegativeList;

public class RestConnector {
	private static Logger log = LogManager.getLogger(RestConnector.class);
	
	public static ResponseEntity sendRequest(Map dataMap, String uri, String authorization, HttpMethod requestMethod) {
		HttpHeaders requestHeaders = new HttpHeaders();
		HttpEntity<String>  requestEntity=null;
		ResponseEntity<Map> responseEntity = null;
		String requestBody=null;
		Map requestMap = new HashMap();
		
		//configure rest
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		String timeoutString = "60";
		//Timeout parameter just for development
		log.debug("timeoutString :"+timeoutString);
		if (timeoutString== null || "".equalsIgnoreCase(timeoutString)) {
		    requestFactory.setReadTimeout(60);
		    requestFactory.setConnectTimeout(60);
		}else {
			int timeoutPurchaseParameterInt=Integer.valueOf(timeoutString);
			log.debug("timeoutInt :"+timeoutPurchaseParameterInt);
		    requestFactory.setReadTimeout(timeoutPurchaseParameterInt);
		    requestFactory.setConnectTimeout(timeoutPurchaseParameterInt);
		}	    
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
		
		try {
			//set header
			requestHeaders = checkUriForHeaders(uri);
			
		    //set body (Remapping request dataMap here)
			requestMap = checkUri(uri, dataMap);
			ObjectMapper mapper = new ObjectMapper();
			try {
				requestBody = mapper.writeValueAsString(requestMap);
			} catch (JsonGenerationException e) {
				log.error("FAIL-CONNECTOR with error message : " + e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error("FAIL-CONNECTOR with error message : " + e.getMessage(), e);
			} catch (IOException e) {
				log.error("FAIL-CONNECTOR with error message : " + e.getMessage(), e);
			}
		    
			//set request
			requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
			
			//execute
		    try {
		    		log.debug("Request data : " + requestBody);
		    		responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
	    			log.debug("Response data : "+responseEntity.getBody());
		    } catch (HttpStatusCodeException responseException) {
		    		log.debug("[Response data Exception : "+responseException.getStatusCode()+"] - "+ responseException.getResponseBodyAsString());
		    		if (responseException.getStatusCode()==HttpStatus.UNAUTHORIZED) {
					try {
						String renewToken="new token";
						requestHeaders.set("Authorization", renewToken);
						requestEntity = new HttpEntity<String> (requestBody, requestHeaders);
					    try {
					    		log.debug("Request data : " + requestBody);
					    		responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
					    		log.debug("Response data : "+responseEntity.getBody());
					    } catch (HttpStatusCodeException responseExceptionRetry) {
					    		log.debug("[RETRY] - Response data Exception : ["+responseExceptionRetry.getStatusCode()+"] - "+ responseExceptionRetry.getResponseBodyAsString());
					    }
						
					}catch(Exception ex) {
						log.error("[RETRY] - FAIL-CONNECTOR with error message : " + ex.getMessage(), ex);
					}
				}
		    }
		} catch (Exception e) {
			log.error("FAIL-CONNECTOR TADAPurchaseEgift with error message : " + e.getMessage(), e);
		}		
		return responseEntity;
	}
	
	public static Map checkUri(String uri, Map dataMap) {
		Map returnMap = new HashMap();
		
		if("http://10.32.1.17/PegaAPI/api/ws/NegativeList".equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestDataNegativeList(dataMap);
		}else if("https://api.smma.co.id/thirdparty/1/verify-selfie/verifySelfie".equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestDataDUKCAPIL(dataMap);
		}else if("http://10.22.11.37:8080/umg/getNIK".equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestDataAsliRI(dataMap);
		}
		return returnMap;
	}
	
	public static HttpHeaders checkUriForHeaders(String uri) {
		HttpHeaders returnMap = new HttpHeaders();
		
		if("http://10.32.1.17/PegaAPI/api/ws/NegativeList".equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestHeaderNegativeList(uri);
		}else if("https://api.smma.co.id/thirdparty/1/verify-selfie/verifySelfie".equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestHeaderDUKCAPIL(uri);
		}else if("http://10.22.11.37:8080/umg/getNIK".equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestHeaderAsliRI(uri);
		}
		return returnMap;
	}
	
}

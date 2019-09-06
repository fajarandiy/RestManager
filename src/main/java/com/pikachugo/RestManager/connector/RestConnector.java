package com.pikachugo.RestManager.connector;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikachugo.RestManager.util.ConstructDataAsliRI;
import com.pikachugo.RestManager.util.ConstructDataDUKCAPIL;
import com.pikachugo.RestManager.util.ConstructDataNegativeList;
import com.pikachugo.RestManager.util.MySSLSocketFactory;

public class RestConnector {
	private static Logger log = Logger.getLogger(RestConnector.class);
	public static final String URI_NEGATIVE_LIST = "http://10.32.1.17/PegaAPI/api/ws/NegativeList";
	public static final String URI_VERIFY_SELFIE = "https://10.32.1.202/labs/sb/kyc-service/asliri-selfie-verification";
	public static final String URI_DUKCAPIL_GET_NIK = "http://10.22.11.37:8080/umg/getNIK";
	
	public static ResponseEntity sendRequest(Map dataMap, String uri, HttpMethod requestMethod) throws Exception {
		HttpHeaders requestHeaders = new HttpHeaders();
		HttpEntity<String> requestEntity=null;
		ResponseEntity<Map> responseEntity = null;
		String requestBody=null;
		Map requestMap = new HashMap();
		
		//configure rest template
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		String timeoutString = "30000"; //Hard code 30000 for development testing only
		requestFactory.setHttpClient(MySSLSocketFactory.getNewHttpClient());
		log.debug("timeoutString :"+timeoutString);
		if (timeoutString== null || "".equalsIgnoreCase(timeoutString)) {
		    requestFactory.setReadTimeout(30000);
		    requestFactory.setConnectTimeout(30000);
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
			
		    //set body (Re-mapping request dataMap here)
			requestMap = checkUri(uri, dataMap);
			ObjectMapper mapper = new ObjectMapper();
			try {
				requestBody = mapper.writeValueAsString(requestMap);
			} catch (Exception e) {
				log.error("Fail construct request body with error message : " + e.getMessage(), e);
				throw new Exception(e.getMessage());
			}
		    
			//set request
			requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
			
			//execute
		    try {
		    	log.debug("Request data : " + requestBody);
		    	responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
		    	log.debug("Response data : "+responseEntity.getBody());
		    } catch (Exception e) {
		    	log.error("Response data Exception : "+e.getMessage(), e);
		    	throw new Exception(e.getMessage());
			}
//		    ---- Assumption if throw exception, then failed. There's no retry.
		    
//		    catch (HttpStatusCodeException responseException) {
//		    	log.debug("[Response data Exception : "+responseException.getStatusCode()+"] - "+ responseException.getResponseBodyAsString());
//		    	if (responseException.getStatusCode()==HttpStatus.UNAUTHORIZED) {
//		    		try {
//						String renewToken="new token";
//						requestHeaders.set("Authorization", renewToken);
//						requestEntity = new HttpEntity<String> (requestBody, requestHeaders);
//					    try {
//					    		log.debug("Request data : " + requestBody);
//					    		responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
//					    		log.debug("Response data : "+responseEntity.getBody());
//					    } catch (HttpStatusCodeException responseExceptionRetry) {
//					    		log.debug("[RETRY] - Response data Exception : ["+responseExceptionRetry.getStatusCode()+"] - "+ responseExceptionRetry.getResponseBodyAsString());
//					    }
//						
//					}catch(Exception ex) {
//						log.debug("[RETRY] - FAIL-CONNECTOR with error message : " + ex.getMessage(), ex);
//					}
//				}
//		    }
		} catch (Exception e) {
			log.error("Fail send request with error message : " + e.getMessage(), e);
			throw new Exception(e.getMessage());
		}		
		return responseEntity;
	}
	
	public static Map checkUri(String uri, Map dataMap) {
		Map returnMap = new HashMap();
		
		if(URI_NEGATIVE_LIST.equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestDataNegativeList(dataMap);
		}else if(URI_DUKCAPIL_GET_NIK.equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestDataDUKCAPIL(dataMap);
		}else if(URI_VERIFY_SELFIE.equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestDataAsliRI(dataMap);
		}
		return returnMap;
	}
	
	public static HttpHeaders checkUriForHeaders(String uri) {
		HttpHeaders returnMap = new HttpHeaders();
		
		if(URI_NEGATIVE_LIST.equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestHeaderNegativeList(uri);
		}else if(URI_DUKCAPIL_GET_NIK.equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestHeaderDUKCAPIL(uri);
		}else if(URI_VERIFY_SELFIE.equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestHeaderAsliRI(uri);
		}
		return returnMap;
	}
	
	public static void testAPIAsliRI() {
		Map dataMap = new HashMap();
		dataMap.put("nik", "");
		dataMap.put("selfie_photo", "");
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, URI_VERIFY_SELFIE, HttpMethod.POST);
			log.debug("responseEntity : "+responseEntity.getBody());
		} catch (Exception e) {
			log.debug("Fail verify selfie with error message : "+e.getMessage());
		}
	}
	
	public static void testAPINegativeList() {
		Map dataMap = new HashMap();
		dataMap.put("NIK", "3320015510720001");
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, URI_NEGATIVE_LIST, HttpMethod.POST);
			log.debug("responseEntity : "+responseEntity.getBody());
		} catch (Exception e) {
			log.debug("Fail verify selfie with error message : "+e.getMessage());
		}
	}
	
	public static void main(String [] args) {
//		testAPINegativeList();
		testAPIAsliRI();
	}
}

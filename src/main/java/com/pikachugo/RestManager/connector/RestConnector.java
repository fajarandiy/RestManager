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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikachugo.RestManager.util.ConstructDataAsliRI;
import com.pikachugo.RestManager.util.ConstructDataDUKCAPIL;
import com.pikachugo.RestManager.util.ConstructDataLiveCheck;
import com.pikachugo.RestManager.util.ConstructDataNegativeList;
import com.pikachugo.RestManager.util.MySSLSocketFactory;

public class RestConnector {
	private static Logger log = Logger.getLogger(RestConnector.class);
	public static final String URI_NEGATIVE_LIST = "http://10.32.1.17/PegaAPI/api/ws/NegativeList";
	public static final String URI_VERIFY_SELFIE = "https://10.32.1.202/labs/sb/kyc-service/asliri-selfie-verification";
	public static final String URI_DUKCAPIL_GET_NIK = "http://10.22.11.37:8080/umg/getNIK";
	public static final String URI_LIVENESS_CHECK = "https://img.smma.co.id:8002/detect";
	
	public static ResponseEntity sendRequest(Map dataMap, String uri, HttpMethod requestMethod) throws Exception {
//		Set default http type 1 (application-json)	
		String httpType = "1";
		
		HttpEntity requestEntity = null;
		ResponseEntity<Map> responseEntity = null;
		String requestBody=null;
		Map requestMap = new HashMap();
		
		//Configure rest template
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
		    //Set body (Re-mapping request dataMap here)
			requestMap = constructRequestMap(uri, dataMap);
			log.debug("requestMap : "+requestMap);
			
			//Set entity type either write as a application-json or not
			httpType=checkEntityType(uri, httpType);
			log.debug("httpType : "+httpType);
			
			//Set http entity
			requestEntity = setEntityRequest(requestMap,httpType,uri);
		    
		    //Execute
		    try {
		    	log.debug("Request data : " + requestBody);
		    	responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
		    	log.debug("Response data : "+responseEntity.getBody());
			} catch (Exception e) {
		    	log.error("Response data Exception : "+e.getMessage(), e);
		    	throw new Exception(e.getMessage());
			}
		} catch (Exception e) {
			log.error("Fail send request with error message : " + e.getMessage(), e);
			throw new Exception(e.getMessage());
		}		
		log.debug("berhasil sampe return");
		return responseEntity;
	}
	
	public static Map constructRequestMap(String uri, Map dataMap) {
		Map returnMap = new HashMap();
		if(URI_NEGATIVE_LIST.equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestDataNegativeList(dataMap);
		}else if(URI_DUKCAPIL_GET_NIK.equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestDataDUKCAPIL(dataMap);
		}else if(URI_VERIFY_SELFIE.equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestDataAsliRI(dataMap);
		}else if(URI_LIVENESS_CHECK.equals(uri)) {
			returnMap = ConstructDataLiveCheck.constructRequestDataLiveCheck(dataMap);
		}
		return returnMap;
	}
	
	/**
	 * 
	 * @param uri Third party API URI
	 * @param httpType (1,2)
	 * @see <b>httpType</b> documentation :
	 * <br>1: Application-json 
	 * <br>2: Multipart form data
	 * @return httpType value as a String
	 */
	public static String checkEntityType(String uri, String httpType) {
		if(URI_LIVENESS_CHECK.equals(uri)) {
			httpType = "2";
			return httpType;
		}else {
			return httpType;
		}
	}
	
	public static HttpHeaders checkUriForHeaders(String uri) {
		HttpHeaders returnMap = new HttpHeaders();
		if(URI_NEGATIVE_LIST.equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestHeaderNegativeList(uri);
		}else if(URI_DUKCAPIL_GET_NIK.equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestHeaderDUKCAPIL(uri);
		}else if(URI_VERIFY_SELFIE.equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestHeaderAsliRI(uri);
		}else if(URI_LIVENESS_CHECK.equals(uri)) {
			returnMap = ConstructDataLiveCheck.constructRequestHeaderLiveCheck(uri);
		}
		return returnMap;
	}
	
	public static HttpEntity setEntityRequest(Map dataMap, String httpType , String uri) {
		HttpEntity requestEntity = null;
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders = checkUriForHeaders(uri);
		if("1".equals(httpType)) {
			String jsonString = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonString = mapper.writeValueAsString(dataMap);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return requestEntity = new HttpEntity(jsonString, requestHeaders);
		}else {
			return requestEntity = new HttpEntity(dataMap, requestHeaders); 
		}
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
	
	public static void testAPILivenessCheck() {
		Map dataMap = new HashMap();
		String photo ="";
		dataMap.put("img", photo);
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, URI_LIVENESS_CHECK, HttpMethod.POST);
			log.debug("responseEntity : "+responseEntity.getBody());
		} catch (Exception e) {
			log.debug("Fail verify selfie with error message : "+e.getMessage());
		}
	}
	
	public static void main(String [] args) {
//		testAPINegativeList();
		//testAPIAsliRI();
		testAPILivenessCheck();
	}
}

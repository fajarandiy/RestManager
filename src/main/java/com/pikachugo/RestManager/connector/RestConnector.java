package com.pikachugo.RestManager.connector;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikachugo.RestManager.util.ConstructDataAsliRI;
import com.pikachugo.RestManager.util.ConstructDataDUKCAPIL;
import com.pikachugo.RestManager.util.ConstructDataLiveCheck;
import com.pikachugo.RestManager.util.ConstructDataNegativeList;

public class RestConnector {
	private static Logger log = Logger.getLogger(RestConnector.class);
	public static final String URI_NEGATIVE_LIST = "http://10.32.1.17/PegaAPI/api/ws/NegativeList";
	public static final String URI_VERIFY_SELFIE = "https://sandbox.banksinarmas.com/labs/sb/kyc-service/asliri-selfie-verification";
	public static final String URI_DUKCAPIL_GET_NIK = "http://10.22.11.37:8080/umg/getNIK";
	public static final String URI_LIVENESS_CHECK = "http://10.22.100.18:8002/detect";
	public static final String URI_SMS = "https://10.32.1.202/labs/sb/service-application/sendSms";
	public static final String URI_IZI_OCR = "";
    
	public static ResponseEntity sendRequest(Map dataMap, String uri, HttpMethod requestMethod, String credentials) throws Exception {
		String httpType = "1";
		HttpEntity requestEntity = null;
		ResponseEntity<Map> responseEntity = null;
		String requestBody = null;
		Map requestMap = new HashMap();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		String timeoutString = "60000";
		ignoreSSLConnection(requestFactory);
		log.debug("timeoutString :" + timeoutString);
		if (timeoutString != null && !"".equalsIgnoreCase(timeoutString)) {
			int timeoutPurchaseParameterInt = Integer.valueOf(timeoutString);
			log.debug("timeoutInt :" + timeoutPurchaseParameterInt);
		}
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		try {
			requestMap = constructRequestMap(uri, dataMap);
			log.debug("requestMap : " + requestMap);
			httpType = checkEntityType(uri, httpType);
			log.debug("httpType : " + httpType);
			requestEntity = setEntityRequest(requestMap, httpType, uri, credentials);
			try {
				log.debug("Request data : " + requestBody);
				responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
				log.debug("Response data : " + responseEntity.getBody());
			} catch (Exception e) {
				log.error("Response data Exception : " + e.getMessage(), e);
				throw new Exception(e.getMessage());
			}
		} catch (Exception e) {
			log.error("Fail send request with error message : " + e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		log.debug("berhasil sampe return");
		return responseEntity;
	}
	
	public static ResponseEntity sendRequestNew(Map dataMap, String uri, HttpMethod requestMethod, String type, String credentials) throws Exception {
		String httpType = "1";
		HttpEntity requestEntity = null;
		ResponseEntity<Map> responseEntity = null;
		String requestBody = null;
		Map requestMap = new HashMap();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		String timeoutString = "60000";
		System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
		System.setProperty("https.protocols", "TLSv1.2");
		ignoreSSLConnection(requestFactory);
		log.debug("timeoutString :" + timeoutString);
		if (timeoutString != null && !"".equalsIgnoreCase(timeoutString)) {
			int timeoutPurchaseParameterInt = Integer.valueOf(timeoutString);
			log.debug("timeoutInt :" + timeoutPurchaseParameterInt);
		}
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		try {
			requestMap = constructRequestMapNew(uri, dataMap, type);
			log.debug("requestMap : " + requestMap);
			httpType = checkEntityType(uri, httpType);
			if ("livenessCheck".equalsIgnoreCase(type)) {
				httpType = "2";
			} else if ("OCR".equalsIgnoreCase(type)) {
				httpType = "2";
			}
			log.debug("httpType : " + httpType);
			requestEntity = setEntityRequestNew(requestMap, httpType, uri, type, credentials);
			try {
				log.debug("Request data : " + requestEntity);
				responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, Map.class);
				log.debug("Response data : " + responseEntity.getBody());
			} catch (Exception e) {
				log.error("Response data Exception : " + e.getMessage(), e);
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
		if ("http://10.32.1.17/PegaAPI/api/ws/NegativeList".equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestDataNegativeList(dataMap);
		} else if ("http://10.22.11.37:8080/umg/getNIK".equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestDataDUKCAPIL(dataMap);
		} else if ("https://sandbox.banksinarmas.com/labs/sb/kyc-service/asliri-selfie-verification".equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestDataAsliRI(dataMap);
		} else if ("http://10.22.100.18:8002/detect".equals(uri)) {
			returnMap = ConstructDataLiveCheck.constructRequestDataLiveCheck(dataMap);
		} else if ("https://10.32.1.202/labs/sb/service-application/sendSms".equals(uri)) {
			String referenceId = (String) dataMap.get("referenceId");
			String recipientNo = (String) dataMap.get("recipientNo");
			String message = (String) dataMap.get("message");
			returnMap.put("referenceId", referenceId);
			returnMap.put("recipientNo", recipientNo);
			returnMap.put("message", message);
		} else if ("".equals(uri)) {
			returnMap = ConstructDataLiveCheck.constructRequestDataLiveCheck(dataMap);
		} return returnMap;
	}
	
	public static Map constructRequestMapNew(String uri, Map dataMap, String type) {
		Map returnMap = new HashMap();
		if ("http://10.32.1.17/PegaAPI/api/ws/NegativeList".equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestDataNegativeList(dataMap);
		} else if ("http://10.22.11.37:8080/umg/getNIK".equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestDataDUKCAPIL(dataMap);
		} else if ("https://sandbox.banksinarmas.com/labs/sb/kyc-service/asliri-selfie-verification".equals(uri) || "verifySelfie".equalsIgnoreCase(type)) {
			returnMap = ConstructDataAsliRI.constructRequestDataAsliRI(dataMap);
		} else if ("http://10.22.100.18:8002/detect".equals(uri) || "livenessCheck".equalsIgnoreCase(type)) {
			returnMap = ConstructDataLiveCheck.constructRequestDataLiveCheck(dataMap);
		} else if ("https://10.32.1.202/labs/sb/service-application/sendSms".equals(uri)) {
			String referenceId = (String) dataMap.get("referenceId");
			String recipientNo = (String) dataMap.get("recipientNo");
			String message = (String) dataMap.get("message");
			returnMap.put("referenceId", referenceId);
			returnMap.put("recipientNo", recipientNo);
			returnMap.put("message", message);
		} else if ("".equals(uri) || "OCR".equalsIgnoreCase(type)) {
			returnMap = ConstructDataLiveCheck.constructRequestDataLiveCheck(dataMap);
		}
		return returnMap;
	}
	
	public static String checkEntityType(String uri, String httpType) {
		if ("http://10.22.100.18:8002/detect".equals(uri)) {
			httpType = "2";
			return httpType;
		}
		if ("".equals(uri)) {
			httpType = "2";
			return httpType;
		}
		return httpType;
	}
	
	public static HttpHeaders checkUriForHeaders(String uri, String credentials) {
		HttpHeaders returnMap = new HttpHeaders();
		if ("http://10.32.1.17/PegaAPI/api/ws/NegativeList".equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestHeaderNegativeList(uri, credentials);
		} else if ("http://10.22.11.37:8080/umg/getNIK".equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestHeaderDUKCAPIL(uri);
		} else if ("https://sandbox.banksinarmas.com/labs/sb/kyc-service/asliri-selfie-verification".equals(uri)) {
			returnMap = ConstructDataAsliRI.constructRequestHeaderAsliRI(uri, credentials);
		} else if ("http://10.22.100.18:8002/detect".equals(uri)) {
			returnMap = ConstructDataLiveCheck.constructRequestHeaderLiveCheck(uri);
		} else if ("https://10.32.1.202/labs/sb/service-application/sendSms".equals(uri)) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("x-ibm-client-id", "f54329c4-2422-46fb-9bd0-5132364d3ae9");
			requestHeaders.add("Content-Type", "application/json;charset=utf-8");
			returnMap = requestHeaders;
		} else if ("".equals(uri)) {
			returnMap = ConstructDataLiveCheck.constructRequestHeaderLiveCheck(uri);
		} 
		return returnMap;
	}
	
	public static HttpHeaders checkUriForHeadersNew(String uri, String type, String credentials) {
		HttpHeaders returnMap = new HttpHeaders();
		if ("http://10.32.1.17/PegaAPI/api/ws/NegativeList".equals(uri)) {
			returnMap = ConstructDataNegativeList.constructRequestHeaderNegativeList(uri, credentials);
		} else if ("http://10.22.11.37:8080/umg/getNIK".equals(uri)) {
			returnMap = ConstructDataDUKCAPIL.constructRequestHeaderDUKCAPIL(uri);
		} else if ("https://sandbox.banksinarmas.com/labs/sb/kyc-service/asliri-selfie-verification".equals(uri) || "verifySelfie".equalsIgnoreCase(type)) {
			returnMap = ConstructDataAsliRI.constructRequestHeaderAsliRI(uri, credentials);
		} else if ("http://10.22.100.18:8002/detect".equals(uri) || "livenessCheck".equalsIgnoreCase(type)) {
			returnMap = ConstructDataLiveCheck.constructRequestHeaderLiveCheck(uri);
		} else if ("https://10.32.1.202/labs/sb/service-application/sendSms".equals(uri)) {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("x-ibm-client-id", credentials);
			requestHeaders.add("Content-Type", "application/json;charset=utf-8");
			returnMap = requestHeaders;
		} else if ("".equals(uri) || "OCR".equalsIgnoreCase(type)) {
			returnMap = ConstructDataLiveCheck.constructRequestHeaderLiveCheck(uri);
		}
		return returnMap;
	}
	
	public static HttpEntity setEntityRequest(Map dataMap, String httpType, String uri, String credentials) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders = checkUriForHeaders(uri, credentials);
		if ("1".equals(httpType)) {
			String jsonString = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonString = mapper.writeValueAsString((Object)dataMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return new HttpEntity(jsonString, (MultiValueMap)requestHeaders);
		}
		return new HttpEntity(dataMap, (MultiValueMap)requestHeaders);
	}
	
	public static HttpEntity setEntityRequestNew(Map dataMap, String httpType, String uri, String type, String credentials) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders = checkUriForHeadersNew(uri, type, credentials);
		if ("1".equals(httpType)) {
			String jsonString = "";
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonString = mapper.writeValueAsString(dataMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return new HttpEntity(jsonString, (MultiValueMap)requestHeaders);
		}
		return new HttpEntity(dataMap, (MultiValueMap)requestHeaders);
	}
	
	public static void testAPIAsliRI() {
		Map dataMap = new HashMap();
		dataMap.put("nik", "");
		dataMap.put("selfie_photo", "");
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, "https://sandbox.banksinarmas.com/labs/sb/kyc-service/asliri-selfie-verification", HttpMethod.POST, null);
			log.debug("responseEntity : " + responseEntity.getBody());
		} catch (Exception e) {
			log.error("Fail verify selfie with error message : " + e.getMessage());
		}
	}
	
	public static void testAPINegativeList() {
		Map dataMap = new HashMap();
		dataMap.put("NIK", "3320015510720001");
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, "http://10.32.1.17/PegaAPI/api/ws/NegativeList", HttpMethod.POST, null);
			log.debug("responseEntity : " + responseEntity.getBody());
		} catch (Exception e) {
			log.error("Fail verify selfie with error message : " + e.getMessage());
		}
	}
	
	public static void testAPILivenessCheck() {
		Map dataMap = new HashMap();
		String photo = "";
		dataMap.put("img", photo);
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, "http://10.22.100.18:8002/detect", HttpMethod.POST, null);
			log.debug("responseEntity : " + responseEntity.getBody());
		} catch (Exception e) {
			log.error("Fail verify selfie with error message : " + e.getMessage());
		}
	}
	
	public static void testAPISMSCheck() {
		Map dataMap = new HashMap();
		dataMap.put("referenceId", "bintang1234");
		dataMap.put("recipientNo", "085218322");
		dataMap.put("message", "Pesan Singkat");
		try {
			ResponseEntity responseEntity = sendRequest(dataMap, "https://10.32.1.202/labs/sb/service-application/sendSms", HttpMethod.POST, null);
			log.debug("responseEntity : " + responseEntity.getBody());
		} catch (Exception e) {
			log.error("Fail verify selfie with error message : " + e.getMessage());
		}
	}
	
	public static void ignoreSSLConnection(HttpComponentsClientHttpRequestFactory requestFactory) {
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
			public boolean isTrusted(final X509Certificate[] x509Certificates, String s) throws CertificateException {
				return true;
			}
		};
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (TrustStrategy)acceptingTrustStrategy).build();
			SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, (HostnameVerifier)new NoopHostnameVerifier());
			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory((LayeredConnectionSocketFactory)connectionSocketFactory).build();
			requestFactory.setHttpClient((HttpClient)httpClient);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(final String[] args) {
		testAPIAsliRI();
	}
}

package com.pikachugo.RestManager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pikachugo.RestManager.connector.RestConnector;
import com.pikachugo.RestManager.util.BaseUtil;

@RestController
@RequestMapping("/RestManager")
public class RestManagerController extends BaseUtil {
	private static Logger log = Logger.getLogger(RestManagerController.class);

	@Autowired
	RestConnector restConnector;
	
	@RequestMapping(value="/checkingDataPhoto", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Map> checkingData(@RequestBody final Map dataMap) {
		Map responseMap = new HashMap();
		try {
			log.debug("dataMap : "+dataMap);
			Map responseEntityLivenessCheckMap = null;
			Map responseEntityVerifySelfie = null;
			
			try {
				responseEntityLivenessCheckMap.putAll((Map) RestConnector.sendRequest(dataMap, restConnector.URI_LIVENESS_CHECK, HttpMethod.POST).getBody());
			} catch (Exception e) {
				log.error("Fail liveness check : "+e.getMessage(), e);
				throw new Exception(e.getMessage());
			}
			
			int resultLivenessCheck = 0;
			if (responseEntityLivenessCheckMap != null) {
				try {
					resultLivenessCheck = (Integer) responseEntityLivenessCheckMap.get("result");
				} catch (Exception e) {
					log.error("Fail get liveness check result value : "+e.getMessage());
				}
				if(resultLivenessCheck == 1) {
					try {
						responseEntityVerifySelfie.putAll((Map) RestConnector.sendRequest(dataMap, restConnector.URI_VERIFY_SELFIE, HttpMethod.POST).getBody());
					} catch (Exception e) {
						log.error("Fail verify selfie : "+e.getMessage(), e);
						throw new Exception(e.getMessage());
					}
				} else {
					throw new Exception("Liveness check result invalid");
				}
			} else {
				throw new Exception("Liveness check result is null");
			}
			responseMap.put("responseCode", "00");
			responseMap.put("responseMessage", "Success");
		} catch (Exception e) {
			log.error("Fail checkingDataPhoto : "+e.getMessage());
			responseMap.put("responseCode", "01");
			responseMap.put("errorMessage", e.getMessage());
		}
		return reply(responseMap);
	}
}

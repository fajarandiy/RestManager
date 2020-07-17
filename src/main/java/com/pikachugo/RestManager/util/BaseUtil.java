package com.pikachugo.RestManager.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public class BaseUtil {
	public static boolean nvl(final String s) {
		return s == null || s.trim().length() == 0;
	}
	
	public static boolean nvl(final Object o) {
		return o == null || (o instanceof String && ((String)o).trim().length() == 0);
	}
	
	public static ResponseEntity<Map> reply(Map response) {
		if (!nvl(response.get("errorMessage"))) {
			if (response.get("errorMessage").toString().contains("error.forbidden"))
				return new ResponseEntity(response, HttpStatus.FORBIDDEN); 
			if (response.get("errorMessage").toString().contains("error.session.invalid"))
				return new ResponseEntity(response, HttpStatus.UNAUTHORIZED); 
			return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
		} 
		return new ResponseEntity(response, HttpStatus.OK);
	}
}

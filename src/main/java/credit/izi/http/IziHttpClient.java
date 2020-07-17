package credit.izi.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;



public class IziHttpClient {
    static {
        System.setProperty("sun.net.client.defaultConnectTimeout", "60000");
        System.setProperty("sun.net.client.defaultReadTimeout", "60000");
    }

    /**
     * post方式请求服务器(https协议)
     *
     * @param request 请求内容
     * @return IziResponse
     */
    public static IziResponse post(IziRequest request) {
//		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//		                return null;
//		            }
//		            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//		            }
//		            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//		            }
//		        }
//		    };
        String url;
        String charset = request.getContentEncoding();
        String content = request.getBodyStr();
        HashMap<String, String> header = request.getHeaders();
        IziResponse response = new IziResponse();

        DataOutputStream out = null;
        InputStream is = null;
        try {
            if (request.getParams().isEmpty()) {
                url = request.getUri().toString();
            }
            else {
                url = String.format("%s?%s", request.getUri().toString(), request.getParamStr());
            }

            URL console = new URL(url);
            Proxy proxy = request.getConfig() == null ? Proxy.NO_PROXY : request.getConfig().getProxy();
            HttpURLConnection conn = (HttpURLConnection) console.openConnection(proxy);
            // set timeout
            
            if (request.getConfig() != null) {
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
//                try {
//                	 // Install the all-trusting trust manager
//                    SSLContext sc = SSLContext.getInstance("SSL");
//                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
//                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//             
//                    // Create all-trusting host name verifier
//                    HostnameVerifier allHostsValid = new HostnameVerifier() {
//                        public boolean verify(String hostname, SSLSession session) {
//                            return true;
//                        }
//                    };
//                    conn.setDefaultHostnameVerifier(allHostsValid);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
            }
            conn.setDoOutput(true);
            // 添加header
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.connect();
            out = new DataOutputStream(conn.getOutputStream());
            out.write(content.getBytes(charset));
            out.flush();
            int statusCode = conn.getResponseCode();
            response.setHeader(conn.getHeaderFields());
            response.setStatus(statusCode);
            response.setCharset(charset);
            if (statusCode != 200) {
                return response;
            }

            is = conn.getInputStream();
            if (is != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                response.setBody(outStream.toByteArray());
                System.out.println("bodyy = "+outStream.toByteArray());
            }
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    
    
    

}

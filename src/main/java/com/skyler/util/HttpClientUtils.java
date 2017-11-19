package com.skyler.util;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public abstract class HttpClientUtils {

    private static final Logger logger = Logs.geterrorLogger();

    private static final int connectionTimeoutMillis = 30000;
    private static final int socketTimeoutMillis = 30000;
    private static final int maxTotalConnections = 2000;
    private static final int maxConnectionsPerRoute = 500;
    private static final int connManagerTimeout = 1000;

    public static final String DEFAULT_RESPONSE = "{}";

    private static HttpParams params;


    static {
        params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeoutMillis);
        logger.debug("ThreadSafeClient connectionTimeoutMillis:{"+connectionTimeoutMillis+"}");
        HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);
        logger.debug("ThreadSafeClient socketTimeoutMillis:{"+socketTimeoutMillis+"}");
        ConnManagerParams.setTimeout(params, connManagerTimeout);
        logger.debug("ThreadSafeClient connManagerTimeout:{"+connManagerTimeout+"}");
    }

    public static HttpParams getDefaultHttpParams() {
        return params.copy();
    }

	public static HttpClient getHttpClient() {
        logger.debug("Begin getHttpClient");
        long s = System.currentTimeMillis();
        try {
            return getHttpClient(params);
        } finally {
            logger.debug("End getHttpClient spend:{"+(System.currentTimeMillis() - s)+"}");
        }
    }

    public static HttpClient getHttpClient(HttpParams params) {
        return new DefaultHttpClient(params);
    }

    public static String getJsonByPost(String url, Map<String, String> params) {
        return getJsonByPost(url, params, "UTF-8");
    }

    public static String getJsonByPost(String url, Map<String, String> params, String charset) {
        return getJsonByPost(getHttpClient(), url, params, charset);
    }

    public static String getJsonByPost(HttpClient httpClient, String url, Map<String, String> params, String charset) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient is null.");
        }
        logger.info("Begin HttpClientUtils.getJsonByPost url:{"+url+"} params:{"+params+"} charset:{"+charset+"}");
        String response = DEFAULT_RESPONSE;
        List<NameValuePair> paramList = null;
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            paramList = new ArrayList<NameValuePair>();
            for (Iterator<Map.Entry<String, String>> it = entrySet.iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    NameValuePair nvp = new BasicNameValuePair(key, value);
                    paramList.add(nvp);
                }
            }
        }

        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            if (paramList != null)
                httpPost.setEntity(new UrlEncodedFormEntity(paramList, charset));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.warn("Unable to fetch page {"+httpPost.getURI()+"}, status code: {"+statusCode+"}");
            } else {
                response = EntityUtils.toString(httpEntity, charset);
                response = (response != null ? response.trim() : DEFAULT_RESPONSE);
                logger.debug("Fetch page {"+ httpPost.getURI()+"}, status code: {"+statusCode+"}");
            }
            if (httpEntity != null) {
                httpEntity.consumeContent();
            }
        } catch (Exception e) {
            logger.error("", e);
            if (httpPost != null) {
                httpPost.abort();
            }
        } finally {
            if (httpClient != null && httpClient.getConnectionManager() instanceof SingleClientConnManager) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        //logger.debug("Begin HttpClientUtils.getJsonByPost response:{"+response+"}");
        return response;
    }

    public static String getJsonByPost(HttpClient httpClient, String url, HttpEntity entity, String charset) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient is null.");
        }
        logger.info("Begin HttpClientUtils.getJsonByPost url:{"+url+"} params:{"+params+"} charset:{"+charset+"}");
        String response = DEFAULT_RESPONSE;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            if (entity != null)
                httpPost.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.warn("Unable to fetch page {"+httpPost.getURI()+"}, status code: {"+statusCode+"}");
            } else {
                response = EntityUtils.toString(httpEntity, charset);
                response = (response != null ? response.trim() : DEFAULT_RESPONSE);
                logger.debug("Fetch page {"+ httpPost.getURI()+"}, status code: {"+statusCode+"}");
            }
            if (httpEntity != null) {
                httpEntity.consumeContent();
            }
        } catch (Exception e) {
            logger.error("", e);
            if (httpPost != null) {
                httpPost.abort();
            }
        } finally {
            if (httpClient != null && httpClient.getConnectionManager() instanceof SingleClientConnManager) {
                httpClient.getConnectionManager().shutdown();
            }
        }
        //logger.debug("Begin HttpClientUtils.getJsonByPost response:{"+response+"}");
        return response;
    }

    public static String getStringByGet(String url){
    	return httpGet(url,"UTF-8");
    }

    public static String httpGet(String url, String charset) {
        return httpGet(getHttpClient(), url, charset,null);
    }

    public static String httpGet(HttpClient httpClient, String url, String charset,Map<String,String> headerMap ) {
        String response = "";
        if (httpClient == null) {
            logger.error("httpClient is null.");
            return response;
        }
        	
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(url);
            if(headerMap!=null&&headerMap.size()>0){
            	Iterator<String> iterator=headerMap.keySet().iterator();
            	while (iterator.hasNext()) {
            		String key=iterator.next();
            		httpGet.setHeader(key, headerMap.get(key));
				}
            }
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.warn("Unable to fetch page {"+httpGet.getURI()+"}, status code: {"+statusCode+"}");
            } else {
                response = EntityUtils.toString(httpEntity, charset);
                response = (response != null ? response.trim() : DEFAULT_RESPONSE);
                logger.debug("Fetch page {"+httpGet.getURI()+"}, status code: {"+statusCode+"}");
            }
            if (httpEntity != null) {
                httpEntity.consumeContent();
            }
        } catch (Exception e) {
            logger.error("", e);
            if (httpGet != null) {
                httpGet.abort();
            }
        } finally {
            if (httpClient != null && httpClient.getConnectionManager() instanceof SingleClientConnManager) {
                httpClient.getConnectionManager().shutdown();
            }
        }

        return response;
    }

    private static class SpaceConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        private long timeout;

        SpaceConnectionKeepAliveStrategy(long timeout) {
            this.timeout = timeout;
        }

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            return timeout * 1000;
        }
    }



    public static String get(String url){
        HttpRequestBase request = new HttpGet(url);
        return execute(request,null);
    }
    public static String post(String url){
        HttpRequestBase request = new HttpPost(url);
        return execute(request,null);
    }

    public static String post(String url,Map<String,String> params,Map<String,String> headerMap){

        return post(url, params, headerMap,"UTF-8");
    }

    public static String post(String url,Map<String,String> params,Map<String,String> headerMap,String charset){
        HttpPost request = new HttpPost(url);
        if(params!=null&&params.size()>0){
            List<NameValuePair> paramList = null;
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            paramList = new ArrayList<NameValuePair>();
            for (Iterator<Map.Entry<String, String>> it = entrySet.iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    NameValuePair nvp = new BasicNameValuePair(key, value);
                    paramList.add(nvp);
                }
            }
            try {
                if(StringUtil.isEmpty(charset)) {
                    request.setEntity(new UrlEncodedFormEntity(paramList));
                }else{
                    request.setEntity(new UrlEncodedFormEntity(paramList, Charset.forName(charset)));
                }
            } catch (Exception e) {
                Logs.geterrorLogger().error("HttpClientUtils post",e);
                return null;
            }
        }
        return execute(request,headerMap);
    }

    public static String post(String url, HttpEntity entity, Map<String,String> headerMap){
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);
        return execute(request,headerMap);
    }
    public static String get(String url,Map<String,String> headerMap){
        HttpRequestBase request = new HttpGet(url);
        return execute(request,headerMap);
    }
    public static String getIgnoreStatusCode(String url,Map<String,String> headerMap){
        HttpRequestBase request = new HttpGet(url);
        return execute(null, request, headerMap, true);
    }
    public static String post(String url,Map<String,String> headerMap){
        HttpRequestBase request = new HttpPost(url);
        return execute(request,headerMap);
    }
    private static String execute(HttpRequestBase request,Map<String,String> headerMap){
    	return execute(null, request, headerMap, false);
    }
    private static String execute(CloseableHttpClient httpclient,HttpRequestBase request,Map<String,String> headerMap, boolean ignoreStatusCode){
        StringBuffer log = new StringBuffer("HttpClientUtils execute  method:"+request.getMethod()+" url:"+request.getURI());

        boolean isClose = false;
    	if(httpclient == null){
    		httpclient = HttpClients.createDefault();
    		isClose = true;
    	}
    	boolean isLog = false;
        InputStream resStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        String result = DEFAULT_RESPONSE;

        if(headerMap!=null&&headerMap.size()>0){
            Iterator<String> iterator=headerMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key=iterator.next();
                request.setHeader(key, headerMap.get(key));
            }
        }

        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
            request.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(request);

            try{
                if(!ignoreStatusCode && response.getStatusLine().getStatusCode() != 200){
                    request.abort();
                    if(isClose){
						httpclient.close();
					}
                    return result;
                }

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    resStream = entity.getContent();
                    try {
                        inputStreamReader = new InputStreamReader(resStream, "UTF-8");
                        br = new BufferedReader(inputStreamReader);
                        StringBuffer resBuffer = new StringBuffer();
                        String resTemp = "";
                        while((resTemp = br.readLine()) != null){
                            resBuffer.append(resTemp);
                        }
                        result = resBuffer.toString();
                    }catch (Exception e){
                        isLog = true;
                        Logs.geterrorLogger().error(log.toString(),e);
                    }finally{
                        if(br !=null){
                            br.close();
                        }
                        if(inputStreamReader != null ){
                            inputStreamReader.close();
                        }
                        if(resStream !=null ){
                            resStream.close();
                        }
                    }
                }
            }catch (Exception e){
                isLog = true;
                Logs.geterrorLogger().error(log.toString(),e);
            }finally{
                response.close();
            }
        }catch (Exception e) {
            request.abort();
            Logs.geterrorLogger().error(log.toString(),e);
            isLog = true;
        }finally{
            request.abort();
            if(!isClose){
            	return result;
            }
            try {
				httpclient.close();
				if(isLog)
					Logs.geterrorLogger().error(log.toString()+" shutdown ");
            } catch (IOException e) {
                Logs.geterrorLogger().error(log.toString(),e);
            }
        }
        return result;
    }

    public static CloseableHttpResponse execute(CloseableHttpClient httpclient, HttpRequestBase request) {
        StringBuffer log = new StringBuffer("HttpClientUtils execute  method:" + request.getMethod() + " url:" + request.getURI());

        boolean isClose = false;
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
            isClose = true;
        }

        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
            request.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(request);
            return response;

        } catch (Exception e) {
            request.abort();
            Logs.geterrorLogger().error(log.toString(), e);
        } finally {
            request.abort();
            try {
                if (isClose) {
                    httpclient.close();
                }
            } catch (IOException e) {
                Logs.geterrorLogger().error(log.toString(), e);
            }
        }
        return null;
    }
}

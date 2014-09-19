package org.springframework.social.salesforce.api.impl;

import org.codehaus.jackson.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.salesforce.api.SObjectDetail;
import org.springframework.social.salesforce.api.SObjectOperations;
import org.springframework.social.salesforce.api.SObjectSummary;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of SObjectOperations.
 *
 * @author Umut Utkan
 */
public class SObjectsTemplate extends AbstractSalesForceOperations<Salesforce> implements SObjectOperations {

    private RestTemplate restTemplate;

    public SObjectsTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }


    @Override
    public List<Map> getSObjects() {
        requireAuthorization();
        JsonNode dataNode = restTemplate.getForObject(api.getBaseUrl() + "/sobjects", JsonNode.class);
        return api.readList(dataNode.get("sobjects"), Map.class);
    }

    @Override
    public SObjectSummary getSObject(String name) {
        requireAuthorization();
        JsonNode node = restTemplate.getForObject(api.getBaseUrl() + "/sobjects/{name}", JsonNode.class, name);
        return api.readObject(node.get("objectDescribe"), SObjectSummary.class);
    }

    @Override
    public SObjectDetail describeSObject(String name) {
        requireAuthorization();
        return restTemplate.getForObject(api.getBaseUrl() + "/sobjects/{name}/describe", SObjectDetail.class, name);
    }
    
    public Map<?, ?> getRow(String url, Set<String> keySet)
    {
    	requireAuthorization();
        URIBuilder builder = URIBuilder.fromUri(api.getServerInstanceUrl() + url);
        if (keySet.size() > 0) {
            builder.queryParam("fields", StringUtils.arrayToCommaDelimitedString(keySet.toArray()));
        }
        return restTemplate.getForObject(builder.build(), Map.class);
    }

    @Override
    public Map getRow(String name, String id, String... fields) {
        requireAuthorization();
        URIBuilder builder = URIBuilder.fromUri(api.getBaseUrl() + "/sobjects/" + name + "/" + id);
        if (fields.length > 0) {
            builder.queryParam("fields", StringUtils.arrayToCommaDelimitedString(fields));
        }
        return restTemplate.getForObject(builder.build(), Map.class);
    }

    @Override
    public InputStream getBlob(String name, String id, String field) {
        requireAuthorization();
        return restTemplate.execute(api.getBaseUrl() + "/sobjects/{name}/{id}/{field}",
                HttpMethod.GET, null, new ResponseExtractor<InputStream>() {
            @Override
            public InputStream extractData(ClientHttpResponse response) throws IOException {
                return response.getBody();
            }
        }, name, id, field);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<?, ?> create(String name, Map<String, Object> fields) {
        requireAuthorization();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> entity = new HttpEntity<Map>(fields, headers);
        return restTemplate.postForObject(api.getBaseUrl() + "/sobjects/{name}", entity, Map.class, name);
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Map<String, Object> update(String sObjectName, String sObjectId, Map<String, Object> fields) {
        return updateOpperation(api.getBaseUrl() + "/sobjects/"+sObjectName+"/"+sObjectId+"?_HttpMethod=PATCH", fields);
    }
        
        @SuppressWarnings("unchecked")
		@Override
        public Map<String, Object> update(String objectUrl, Map<String, Object> fields) {
            return updateOpperation(api.getInstanceUrl()+objectUrl+"?_HttpMethod=PATCH", fields);       
    }
        
        private Map<String, Object> updateOpperation(String objectUrl, Map<String, Object> fields)
        {
        	requireAuthorization();
        	HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String,Object>>(fields, headers);
            Map<String, Object> result =  restTemplate.postForObject(objectUrl, 
                    entity, Map.class);
            // SF returns an empty body on success, so mimic the same update you'd get from a create success for consistency
            if (result == null) {
                result = new HashMap<String, Object>();
                result.put("id", objectUrl.substring(objectUrl.lastIndexOf("/")+1, objectUrl.length()));
                result.put("success", true);
                result.put("errors", new ArrayList<String>());
            }
            return result;
        }

}

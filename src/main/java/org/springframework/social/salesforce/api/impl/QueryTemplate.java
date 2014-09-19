package org.springframework.social.salesforce.api.impl;

import org.springframework.social.salesforce.api.QueryOperations;
import org.springframework.social.salesforce.api.QueryResult;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * Default implementation of QueryOperations.
 *
 * @author Umut Utkan
 */
public class QueryTemplate extends AbstractSalesForceOperations<Salesforce> implements QueryOperations {

    private RestTemplate restTemplate;


    public QueryTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public QueryResult query(String query) {
        requireAuthorization();
        URI uri = URIBuilder.fromUri(api.getBaseUrl() + "/query").queryParam("q", query).build();
        return restTemplate.getForObject(uri, QueryResult.class);
    }

    @Override
    public QueryResult nextPage(String pathOrToken) {
        requireAuthorization();
        if (pathOrToken.contains("/")) {
            return restTemplate.getForObject(api.getBaseUrl() + pathOrToken, QueryResult.class);
        } else {
            return restTemplate.getForObject(api.getBaseUrl() + "/query/{token}", QueryResult.class, pathOrToken);
        }
    }
    
    public String simpleQueryBuilder(String type, Map<String, Object> fields, String SOQLOpperator)
    {
    	if (fields.isEmpty())
    		return null;
    	if (SOQLOpperator == null)
    		SOQLOpperator= "AND";
    	
    	String query = "Select CreatedDate, ";
    	for (String value : fields.keySet())
    	{
    		if (fields.get(value) !=null)
    			query = query + value+", ";
    	}
    	query = (String) query.subSequence(0, query.length()-2);
    	query = query + " from " + type + " where ";
    	
    	for (String value : fields.keySet())
    	{
    		if (fields.get(value) !=null)
    			query = query + value +" = '" + (String) fields.get(value) + "' "+ SOQLOpperator+" ";
    	}
    	
    	query = query.substring(0, query.length()-4);
    	query = query + " order by CreatedDate DESC";
    	return query;
    }

}

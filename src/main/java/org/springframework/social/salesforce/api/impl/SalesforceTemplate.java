package org.springframework.social.salesforce.api.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.OAuth2Version;
import org.springframework.social.salesforce.api.AnalyticsOperations;
import org.springframework.social.salesforce.api.ApiOperations;
import org.springframework.social.salesforce.api.ChatterOperations;
import org.springframework.social.salesforce.api.QueryOperations;
import org.springframework.social.salesforce.api.RecentOperations;
import org.springframework.social.salesforce.api.SObjectOperations;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.salesforce.api.SearchOperations;
import org.springframework.social.salesforce.api.impl.json.SalesforceModule;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Default implementation of Salesforce. This is the main entry point
 * for all the operations that can be performed on Salesforce.
 *
 * @author Umut Utkan
 */
public class SalesforceTemplate extends AbstractOAuth2ApiBinding implements Salesforce {

    private static final String INSTANCE_URL = "https://na1.salesforce.com";

    private String instanceUrl;
    private String version = "23.0";

    private ObjectMapper objectMapper;

    private ApiOperations apiOperations;

    private ChatterOperations chatterOperations;

    private QueryOperations queryOperations;

    private RecentOperations recentOperations;

    private SearchOperations searchOperations;

    private SObjectOperations sObjectsOperations;

    private AnalyticsOperations analyticsOperations;


    public SalesforceTemplate() {
        initialize();
    }

    public SalesforceTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }


    @Override
    protected OAuth2Version getOAuth2Version() {
        return OAuth2Version.DRAFT_10;
    }

    @Override
    public ApiOperations apiOperations() {
        return apiOperations;
    }

    @Override
    public ChatterOperations chatterOperations() {
        return chatterOperations;
    }

    @Override
    public QueryOperations queryOperations() {
        return queryOperations;
    }

    @Override
    public RecentOperations recentOperations() {
        return recentOperations;
    }

    @Override
    public SearchOperations searchOperations() {
        return searchOperations;
    }

    @Override
    public SObjectOperations sObjectsOperations() {
        return sObjectsOperations;
    }

    @Override
    public AnalyticsOperations analyticsOperations() {
        return analyticsOperations;
    }

    private void initialize() {
        apiOperations = new ApiTemplate(this, getRestTemplate());
        chatterOperations = new ChatterTemplate(this, getRestTemplate());
        queryOperations = new QueryTemplate(this, getRestTemplate());
        recentOperations = new RecentTemplate(this, getRestTemplate());
        searchOperations = new SearchTemplate(this, getRestTemplate());
        sObjectsOperations = new SObjectsTemplate(this, getRestTemplate());
        analyticsOperations = new AnalyticsTemplate(this, getRestTemplate());
    }

    @Override
    protected MappingJackson2HttpMessageConverter getJsonMessageConverter() {
    	MappingJackson2HttpMessageConverter converter = super.getJsonMessageConverter();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SalesforceModule());
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Override
    protected void configureRestTemplate(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(new SalesforceErrorHandler());
    }

    @Override
    public <T> List<T> readList(JsonNode jsonNode, Class<T> type) {
        try {
            CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, type);
            return (List<T>) objectMapper.reader(listType).readValue(jsonNode);
        } catch (IOException e) {
            throw new UncategorizedApiException("salesforce", "Error deserializing data from Salesforce: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T readObject(JsonNode jsonNode, Class<T> type) {
        try {
            return (T) objectMapper.reader(type).readValue(jsonNode);
        } catch (IOException e) {
            throw new UncategorizedApiException("salesforce", "Error deserializing data from Salesforce: " + e.getMessage(), e);
        }
    }

    @Override
    public String getBaseUrl() {
        return (this.instanceUrl == null ? INSTANCE_URL : this.instanceUrl) + "/services/data/v" + version;
    }
    
    public String getServerInstanceUrl() {
        return (this.instanceUrl == null ? INSTANCE_URL : this.instanceUrl);
    }

    @Override
    public String getInstanceUrl() {
        return this.instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }
    
    @Override
	public void setVersion(String version) {
		this.version = version;
	}
    
    

}

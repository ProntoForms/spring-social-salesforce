package org.springframework.social.salesforce.api.impl;

import org.codehaus.jackson.JsonNode;
import org.springframework.social.salesforce.api.AnalyticsOperations;
import org.springframework.social.salesforce.api.Salesforce;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Default implementation of AnalyticsTemplate.
 *
 * @author Alexandru Luchian
 */
public class AnalyticsTemplate extends AbstractSalesForceOperations<Salesforce> implements AnalyticsOperations {

    private RestTemplate restTemplate;

    public AnalyticsTemplate(Salesforce api, RestTemplate restTemplate) {
        super(api);
        this.restTemplate = restTemplate;
    }

    @Override
    public String reportSummaryWithDetail(String reportId) {
        requireAuthorization();
        URI uri = URIBuilder.fromUri(api.getBaseUrl() + "/analytics/reports/"+reportId).queryParam("includeDetails", "true").build();
        JsonNode result = restTemplate.getForObject(uri, JsonNode.class);

        return result.toString();

    }
}


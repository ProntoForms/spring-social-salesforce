package org.springframework.social.salesforce.api;

import java.util.Map;

/**
 * Defines operations for executing SOQL queries.
 *
 * @author Umut Utkan
 */
public interface QueryOperations {

    QueryResult query(String query);

    QueryResult nextPage(String urlOrToken);
    
    String simpleQueryBuilder(String type, Map<String, Object> query, String SOQLOpperator);


}

package org.springframework.social.salesforce.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Defines operations for interacting with the sObjects API.
 *
 * @author Umut Utkan
 */
public interface SObjectOperations {

    public List<Map> getSObjects();

    public SObjectSummary getSObject(String name);

    public SObjectDetail describeSObject(String name);

    public Map getRow(String name, String id, String... fields);
    
    public Map<?, ?> getRow(String url, Set<String> keySet);

    public InputStream getBlob(String name, String id, String field);

    public Map<?, ?> create(String name, Map<String, Object> fields);
    
    public Map<String, Object> update(String sObjectName, String sObjectId,
			Map<String, Object> fields);

	public Map<String, Object> update(String objectUrl, Map<String, Object> fields);

}

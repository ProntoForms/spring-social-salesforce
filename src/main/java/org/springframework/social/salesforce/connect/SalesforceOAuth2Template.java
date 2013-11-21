package com.truecontext.prontoforms.pes.app.impl.mediators.salesforce.connect;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Map;

/**
 * Salesforce OAuth2Template implementation.
 * <p/>
 * The reason to extend is to extract non-standard instance_url from Salesforce's oauth token response.
 *
 * @author Umut Utkan
 */
public class SalesforceOAuth2Template extends OAuth2Template {

	private final String clientId;
	
	private final String clientSecret;
	
    private String instanceUrl;
    
    private String accessTokenUrl;
    
    private String refreshToken;

    public String getClientId() {
		return clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}

    public SalesforceOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        this(clientId, clientSecret, authorizeUrl, null, accessTokenUrl);
    }

    public SalesforceOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, authenticateUrl, accessTokenUrl);
        setUseParametersForClientAuthentication(true);
        this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.accessTokenUrl = accessTokenUrl;
    }


    @Override
    protected AccessGrant createAccessGrant(String accessToken, String scope, String refreshToken, Integer expiresIn, Map<String, Object> response) {
        this.instanceUrl = (String) response.get("instance_url");

        return super.createAccessGrant(accessToken, scope, refreshToken, expiresIn, response);
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }
    
    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters ) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		
		params.set("grant_type", "authorization_code");
		params.set("client_id", this.clientId);
		params.set("client_secret", this.clientSecret);
		params.set("code", authorizationCode);
		params.set("redirect_uri", redirectUri);

		return postForAccessGrant(accessTokenUrl, params);
	}
    
    
	@Override
	public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {		
		this.refreshToken = refreshToken;
		return (super.refreshAccess(refreshToken, additionalParameters));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		return extractAccessGrant(getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class));
	}
    
	
	private AccessGrant extractAccessGrant(Map<String, Object> result) {
		return createAccessGrant((String) result.get("access_token"), (String) result.get("scope"), this.refreshToken, null, result);
	}
    
}

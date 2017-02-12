package com.jagan.utilitylibrary;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;




/**
 * Cookie helper functions.
 *
 */
public class CookieUtil {
        
    private Map<String, Map<String,Map<String,String>>> cookie_cache = new HashMap<String, Map<String,Map<String,String>>>();

    private static final String COOKIE_SET_INDICATOR = "Set-Cookie";
    private static final String COOKIE_SET_DELIMITER="; ";
    private static final String NAME_VALUE_DELIMITER = ";";
    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final String COOKIE_INDICATOR = "Cookie";

    private static final String COOKIE_PATH = "path";
    private static final String COOKIE_EXPIRATION = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    
    private DateFormat dateFormat  = new SimpleDateFormat(DATE_FORMAT);


    public Map<String, Map<String,Map<String,String>>> getCache() {
		return cookie_cache;
	}



	public void setCache(Map<String, Map<String,Map<String,String>>> store) {
		this.cookie_cache = store;
	}

	// Helper functions
    protected boolean checkCookieExpiration(String cookieExpires) throws ParseException {
    	if (cookieExpires == null) return true;
    	Date now = new Date();
		return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
    }

    protected String extractDomainFromHostString(String host) {
    	String dot = ".";
		if (host.indexOf(dot) != host.lastIndexOf(dot)) {
		    return host.substring(host.indexOf(dot) + 1);
		} else {
		    return host;
		}
    }

    
    protected boolean compareCookiePaths(String cookiePath, String targetPath) {
    	if (cookiePath == null) {
    		return true;
    	} else if (cookiePath.equals("/")) {
    		return true;
    	} else if (targetPath.regionMatches(0, cookiePath, 0, cookiePath.length())) {
    		return true;
    	} else {
    		return false;
    	}
    }


	/**
     * Fetches cookies from the given open connection and stores them in the cache.
     */
    public void fetchCookiesIntoCache(HttpURLConnection conn) throws IOException {
		String cookieDomain = extractDomainFromHostString(conn.getURL().getHost());
		Map<String, Map<String, String>> domainCookieCache; // Cookies in the current domain
		
		if (cookie_cache.containsKey(cookieDomain)) {
			// If we already have an item in the cache for this domain, get it.
			domainCookieCache = cookie_cache.get(cookieDomain);
		} else {
		    // Create a new entry in the cache for this domain.
		    domainCookieCache = new HashMap<String, Map<String, String>>();
		    cookie_cache.put(cookieDomain, domainCookieCache);    
		}
		
		// Fetch the cookies from the connection and put into the cache.
		String hdrName=null;
		for (int i=1; (hdrName = conn.getHeaderFieldKey(i)) != null; i++) {
		    if (COOKIE_SET_INDICATOR.equalsIgnoreCase(hdrName)) {
				Map<String, String> currentCookie = new HashMap<String, String>();
				StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), NAME_VALUE_DELIMITER);

				// Get cookie name first.
				if (st.hasMoreTokens()) {
				    String completeCookie  = st.nextToken();
				    String cookieName = completeCookie.substring(0, completeCookie.indexOf(NAME_VALUE_SEPARATOR));
					String cookieValue = completeCookie.substring(completeCookie.indexOf(NAME_VALUE_SEPARATOR) + 1, completeCookie.length());
				    domainCookieCache.put(cookieName, currentCookie);
				    currentCookie.put(cookieName, cookieValue);
				}
		    
				// Get all other cookies
				while (st.hasMoreTokens()) {
				    String token  = st.nextToken();
				    // Check for empty values.
				    if (token.contains(""+NAME_VALUE_SEPARATOR))
				    	currentCookie.put(token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(), token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1, token.length()));
				    else
				    	currentCookie.put(token, null);
				    
				}
		    }
		}
    }
    
    /**
     * Set cookies on an unopened URLConnection.
     */
    public void fetchCookiesFromCache(URLConnection conn) throws IOException, ParseException {

		URL url = conn.getURL();
		String cookieDomain = extractDomainFromHostString(url.getHost());
		String cookiePath = url.getPath();
		
		Map<String, Map<String,String>> domainStore = cookie_cache.get(cookieDomain);
		// If no cookies for this domain, return null
		if (domainStore == null)
			return;
		
		StringBuffer cookieStr = new StringBuffer();
		
		Iterator<String> cookieNames = domainStore.keySet().iterator();
		while(cookieNames.hasNext()) {
		    String cookieName = cookieNames.next();
		    Map<String, String> cookie = (Map<String, String>)domainStore.get(cookieName);

		    if (compareCookiePaths(cookie.get(COOKIE_PATH), cookiePath) && checkCookieExpiration(cookie.get(COOKIE_EXPIRATION))) {
			cookieStr.append(cookieName);
			cookieStr.append("=");
			cookieStr.append(cookie.get(cookieName));
			if (cookieNames.hasNext())
				cookieStr.append(COOKIE_SET_DELIMITER);
		    }
		}
	    conn.setRequestProperty(COOKIE_INDICATOR, cookieStr.toString());
    }
    
    
    public void setCookie(String domain, String cookieName, String cookieValue) {
		Map<String, String> lookupStore = new HashMap<String, String>();
		
		lookupStore.put(cookieName, cookieValue);
		Map<String, Map<String, String>> domainStore = (Map<String, Map<String, String>>)this.cookie_cache.get(domain);
		if (domainStore == null)
			domainStore = new HashMap<String, Map<String,String>>();
		domainStore.put(cookieName, lookupStore);
		this.cookie_cache.put(domain, domainStore);

    }


    
    /**
     * Returns a string representation of stored cookies organized by domain.
     */

    public String toString() {
    	return cookie_cache.toString();
    }
    
    
}
	
	
	
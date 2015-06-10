package org.avaje.resteasy;

import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Extends the Resteasy filter to support excluding configured urls
 * for serving static content extra with built-in default container servlets.
 *
 * <pre>{@code
 *
 *   <filter>
 *     <description>Extended Resteasy filter</description
 *     <filter-name>RestFilter</filter-name>
 *     <filter-class>org.avaje.resteasy.RestFilter</filter-class>
 *     <init-param>
 *       <param-name>ignore</param-name>
 *       <param-value>(/favicon.ico|/(assets|images|fonts|css|js|res)/.*)</param-value>
 *     </init-param>
 *   </filter>
 *
 *   <filter-mapping>
 *     <filter-name>RestFilter</filter-name>
 *     <url-pattern>/*</url-pattern>
 *   </filter-mapping>
 *
 * }</pre>
 */
public class RestFilter extends FilterDispatcher {

  protected final Logger log = LoggerFactory.getLogger(RestFilter.class);

  protected Pattern ignorePattern;

  @Override
  public void init(FilterConfig config) throws ServletException {
    log.debug("init");
    super.init(config);

    String regex = config.getInitParameter("ignore");
    if (regex != null && regex.length() > 0) {
      try {
        ignorePattern = Pattern.compile(regex);
      } catch (PatternSyntaxException ex) {
        throw new ServletException("Invalid regular expression [" + regex + "] for parameter ignore", ex);
      }
    }

  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

    if (!(req instanceof HttpServletRequest)) {
      chain.doFilter(req, res);

    } else {
      HttpServletRequest hreq = (HttpServletRequest) req;
      HttpServletResponse hres = (HttpServletResponse) res;
      String servletPath = hreq.getServletPath();
      if (ignorePattern != null && ignorePattern.matcher(servletPath).matches()) {
        // ignore for css, javascript and images etc
        chain.doFilter(req, res);
        return;
      }

      preHttpRequest(hreq, hres);
      super.doFilter(req, res, chain);
      postHttpRequest(hreq, hres);
    }

  }

  /**
   * To override as desired.
   */
  protected void preHttpRequest(HttpServletRequest hreq, HttpServletResponse hres) {
    // do nothing
  }

  /**
   * To override as desired.
   */
  protected void postHttpRequest(HttpServletRequest hreq, HttpServletResponse hres) {
    // Clear the MDC Context
    MDC.clear();
  }

}

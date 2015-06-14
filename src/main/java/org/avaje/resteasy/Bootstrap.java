package org.avaje.resteasy;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import org.avaje.resteasy.websocket.BasicWebSocketConfigurator;
import org.avaje.resteasy.websocket.BasicWebSocketEndpointConfig;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

/**
 * Extends the Resteasy Guice servlet context listener with the detection of
 * eager singleton WebSocket server endpoints and registers them with the
 * servlet container.
 */
public class Bootstrap extends GuiceResteasyBootstrapServletContextListener {

  protected static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

  protected ServletContextEvent event;

  protected ServerContainer serverContainer;

  /**
   * On contextInitialized additional obtain the WebSocket ServerContainer.
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    this.event = event;
    this.serverContainer = getServerContainer(event);
    super.contextInitialized(event);
  }

  /**
   * Return the WebSocket ServerContainer from the servletContext.
   */
  protected ServerContainer getServerContainer(ServletContextEvent servletContextEvent) {

    ServletContext context = servletContextEvent.getServletContext();
    return (ServerContainer) context.getAttribute("javax.websocket.server.ServerContainer");
  }

  /**
   * Invoked internally via contextInitialized() this finds and registers WebSocket
   * endpoints that Guice is injecting (that are eager singletons).
   */
  @Override
  protected void withInjector(Injector injector) {

    if (serverContainer != null) {
      registerWebSockets(injector);
    }
  }

  /**
   * Find any Guice eager singletons that are WebSocket endpoints
   * and register them with the servlet container.
   */
  private void registerWebSockets(Injector injector) {

    Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();

    // loop all the Guice bindings looking for @ServerEndpoint singletons
    for (Map.Entry<Key<?>, Binding<?>> entry : allBindings.entrySet()) {

      final Binding<?> binding = entry.getValue();
      binding.acceptScopingVisitor(new DefaultBindingScopingVisitor() {

        @Override
        public Object visitEagerSingleton() {
          // also a eager singleton so register it
          registerWebSocketEndpoint(binding);
          return null;
        }
      });
    }
  }

  /**
   * Check if the binding is a WebSocket endpoint.  If it is then register the webSocket
   * server endpoint with the servlet container.
   */
  protected void registerWebSocketEndpoint(Binding<?> binding) {//}, ServerEndpoint serverEndpoint) {

    Object instance = binding.getProvider().get();
    Class<?> instanceClass = instance.getClass();
    ServerEndpoint serverEndpoint = instanceClass.getAnnotation(ServerEndpoint.class);
    if (serverEndpoint != null) {
      try {
        // register with the servlet container such that the Guice created singleton instance
        // is the instance that is used (and not an instance created per request etc)
        log.debug("registering ServerEndpoint [{}] with servlet container", instanceClass);
        BasicWebSocketConfigurator configurator = new BasicWebSocketConfigurator(instance);
        serverContainer.addEndpoint(new BasicWebSocketEndpointConfig(instanceClass, serverEndpoint, configurator));

      } catch (Exception e) {
        log.error("Error registering WebSocket Singleton " + instance + " with the servlet container", e);
      }
    }
  }

}

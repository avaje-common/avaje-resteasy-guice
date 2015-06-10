package org.avaje.resteasy.websocket;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Implementation of Configurator that takes an already instantiated instance
 * of the WebSocket server endpoint (typically created by Guice) and always
 * returns that.
 * <p>
 *   Used to register a singleton WebSocket server endpoint with the servlet container.
 * </p>
 */
public class BasicWebSocketConfigurator extends ServerEndpointConfig.Configurator {

  final Object instance;

  public BasicWebSocketConfigurator(Object instance) {
    this.instance = instance;
  }

  @Override
  public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
    return (T)instance;
  }

}

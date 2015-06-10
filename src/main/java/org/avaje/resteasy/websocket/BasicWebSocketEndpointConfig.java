package org.avaje.resteasy.websocket;

import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.util.*;

/**
 * Basic implementation of ServerEndpointConfig.
 * <p>
 *   This is
 * </p>
 */
public class BasicWebSocketEndpointConfig implements ServerEndpointConfig {

  protected final Class<?> endpointClass;
  protected final String path;
  protected final List<Class<? extends Decoder>> decoders;
  protected final List<Class<? extends Encoder>> encoders;
  protected final Configurator configurator;
  protected final List<String> subprotocols;

  protected Map<String, Object> userProperties;

  protected List<Extension> extensions;

  public BasicWebSocketEndpointConfig(Class<?> endpointClass, ServerEndpoint serverEndpoint, Configurator configurator) throws DeploymentException {

    this.configurator = configurator;
    this.decoders = Collections.unmodifiableList(Arrays.asList(serverEndpoint.decoders()));
    this.encoders = Collections.unmodifiableList(Arrays.asList(serverEndpoint.encoders()));
    this.subprotocols = Collections.unmodifiableList(Arrays.asList(serverEndpoint.subprotocols()));
    this.path = serverEndpoint.value();

    // supplied by init lifecycle
    this.extensions = new ArrayList<>();
    this.endpointClass = endpointClass;
    this.userProperties = new HashMap<>();
  }

  @Override
  public Configurator getConfigurator() {
    return configurator;
  }

  @Override
  public List<Class<? extends Decoder>> getDecoders() {
    return decoders;
  }

  @Override
  public List<Class<? extends Encoder>> getEncoders() {
    return encoders;
  }

  @Override
  public Class<?> getEndpointClass() {
    return endpointClass;
  }

  @Override
  public List<Extension> getExtensions() {
    return extensions;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public List<String> getSubprotocols() {
    return subprotocols;
  }

  @Override
  public Map<String, Object> getUserProperties() {
    return userProperties;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[endpointClass=");
    sb.append(endpointClass);
    sb.append(",path=");
    sb.append(path);
    sb.append(",decoders=");
    sb.append(decoders);
    sb.append(",encoders=");
    sb.append(encoders);
    sb.append(",subprotocols=");
    sb.append(subprotocols);
    sb.append(",extensions=");
    sb.append(extensions);
    sb.append("]");
    return sb.toString();
  }
}


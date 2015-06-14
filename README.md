# avaje-resteasy-guice
Extensions for using Resteasy with Guice and WebSockets 

### Maven 
```xml
<dependency>
  <groupId>org.avaje.resteasy</groupId>
  <artifactId>avaje-resteasy-guice</artifactId>
  <version>1.1.1</version>
</dependency>
```


## Bootstrap
Provides an extension to GuiceResteasyBootstrapServletContextListener that will detect eager singleton WebSocket server endpoints and registers them with the servlet container.

```xml

<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">

  <!-- Specify the modules to use as per normal Resteasy/Guice -->
  <context-param>
    <param-name>resteasy.guice.modules</param-name>
    <param-value>org.example.myapp.web.module.WebModule</param-value>
  </context-param>

  <!-- Bootstrap - registers Guice WebSocket singletons with the servlet container for you -->
  <listener>
    <listener-class>
      org.avaje.resteasy.Bootstrap
    </listener-class>
  </listener>

```


## RestFilter
Extends the FilterDispatcher providing a configuration parameter that you can 'punch holes' through the filter for using default servlets to serve static content etc. You can specify a regex pattern of resource urls to exclude from processing.

```xml

    <filter>
      <description>Extended Resteasy filter</description
      <filter-name>RestFilter</filter-name>
     <filter-class>org.avaje.resteasy.RestFilter</filter-class>
      <init-param>
        <!-- Specify regex pattern of resource urls to ignore -->
        <param-name>ignore</param-name>
        <param-value>(/favicon.ico|/(assets|images|fonts|css|js|res)/.*)</param-value>
      </init-param>
    </filter>
 
    <filter-mapping>
      <filter-name>RestFilter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>

```

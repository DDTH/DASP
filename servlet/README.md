DASP bootstrap & system services

## History ##

### v0.2.0 (2013-06-26) ###

- *ddth.dasp.common.hazelcastex*: [Hazelcast](http://www.hazelcast.com/) client factory and utilities.
- *ddth.dasp.common.hazelcast*: is now deprecated.

### v0.1.1 (2013-06-20) ###

- *ddth.dasp.common.redis*: [Redis](http://redis.io) client factory and utilities.

### v0.1.0 (too old to remember) ###

## OSGi remote shell configurations ##
The remote shell provides a simple telnet interface to manage OSGi bundles. The IP and Port to bind can be configured via `servlet/common/web.xml`:

    <context-param>
		<description>IP address to bind, used by OSGI remote shell</description>
		<param-name>osgiRemoteShellListenIp</param-name>
		<param-value>127.0.0.1</param-value>
	</context-param>
	<context-param>
		<description>Port to bind, used by OSGI remote shell. Multiple ports are separated by commas, the first available port will be chosen</description>
		<param-name>osgiRemoteShellListenPort</param-name>
		<param-value>16666,16667,16668,16669</param-value>
	</context-param>

## Thrift-based APIs ##
APIs can be called via Apache Thrift protocol. Thrift is enabled and configured via `servlet/common/web.xml`:

    <servlet>
		<description>DASP Thrift API Servlet</description>
		<servlet-name>daspThriftApiServlet</servlet-name>
		<servlet-class>ddth.dasp.servlet.thrift.ThriftApiBootstrapServlet</servlet-class>
		<init-param>
			<description>Set to true to start non-blocking Thrift server, false to start threaded server</description>
			<param-name>nonblockingServer</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<description>Multiple ports are separated by commas, the first available port will be chosen</description>
			<param-name>port</param-name>
			<param-value>9090,9091,9092,9093</param-value>
		</init-param>
		<load-on-startup>30</load-on-startup>
	</servlet>

## REST-based APIs ##
APIs can call via HTTP/HTTPS (REST-like interface). There are two of them:

* [Netty](https://netty.io/) HTTP interface.
* Servlet HTTP interface

There are a few differences between those two:

* URL & Port:
  - Netty: `http://domain:port/api/moduleName/apiName/authKey`. Netty port is configured via `servlet/common/web.xml`
  - Servlet: accessed as a normal servlet as `http://domain:port/<servlet context>/api/moduleName/apiName/authKey`.
* Others:
  - Netty: SSL is not supported yet
  - Servlet: SSL via HTTPS if the serlvet container supports

*The Netty REST interface is recommended over the servlet.*

Netty REST interface configurations (`servlet/common/web.xml`):

    <servlet>
		<description>DASP Netty JSON API Servlet</description>
		<servlet-name>daspNettyJsonApiServlet</servlet-name>
		<servlet-class>ddth.dasp.servlet.netty.api.JsonApiBootstrapServlet</servlet-class>
		<init-param>
            <description>Multiple ports are separated by commas, the first available port will be chosen</description>
			<param-name>port</param-name>
			<param-value>8082,8083,8084,8085</param-value>
		</init-param>
		<init-param>
			<param-name>numWorkers</param-name>
			<param-value>1024</param-value>
		</init-param>
		<init-param>
			<param-name>readTimeout</param-name>
			<param-value>5</param-value>
		</init-param>
		<init-param>
			<param-name>writeTimeout</param-name>
			<param-value>5</param-value>
		</init-param>
		<load-on-startup>30</load-on-startup>
	</servlet>

Servlet REST interface configurations (`servlet/common/web.xml`):

    <servlet>
		<description>DASP JSON API Servlet</description>
		<servlet-name>daspJsonApiServlet</servlet-name>
		<servlet-class>ddth.dasp.servlet.DaspJsonApiServlet</servlet-class>
		<load-on-startup>10</load-on-startup>
	</servlet>

    <servlet-mapping>
		<servlet-name>daspJsonApiServlet</servlet-name>
		<url-pattern>/api/*</url-pattern>
	</servlet-mapping>

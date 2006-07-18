package com.sun.xml.ws.test.container.cargo;

import com.sun.xml.ws.test.container.ApplicationContainer;
import com.sun.xml.ws.test.tool.WsTool;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.RuntimeConfiguration;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;

import java.net.URL;

/**
 * {@link ApplicationContainer} that talks to a server that's already running
 * (IOW launched outside this harness.)
 *
 * <p>
 * This implementation requires that the container be launched externally first.
 * Then the harness will simply deploy/undeloy by using this running container.
 * Useful for repeatedly debugging a test with a remote container.
 *
 * @author Kohsuke Kawaguchi
 */
public class RemoteCargoApplicationContainer extends AbstractCargoContainer<RemoteContainer> {

    private final URL serverUrl;

    /**
     *
     * @param containerId
     *      The ID that represents the container. "tomcat5x" for Tomcat.
     * @param userName
     *      The user name of the admin. Necessary to deploy a war remotely
     * @param password
     *      The password of the admin. Necessary to deploy a war remotely
     * @param server
     *      The URL of the server.
     */
    public RemoteCargoApplicationContainer(WsTool wsimport, WsTool wsgen, boolean debug, String containerId, URL server, String userName, String password) throws Exception {
        super(createContainer(containerId, userName, password, server), wsimport,wsgen,debug);

        this.serverUrl = server;
    }

    private static RemoteContainer createContainer(String containerId, String userName, String password, URL server) throws Exception {
        ConfigurationFactory configurationFactory =
            new DefaultConfigurationFactory();
        RuntimeConfiguration configuration =
            (RuntimeConfiguration) configurationFactory.createConfiguration(
                containerId, ConfigurationType.RUNTIME);

        configuration.setProperty(RemotePropertySet.USERNAME, userName);
        configuration.setProperty(RemotePropertySet.PASSWORD, password);
        if(containerId.startsWith("tomcat"))
            configuration.setProperty(TomcatPropertySet.MANAGER_URL,
                new URL(server,"/manager").toExternalForm());

        // TODO: we should provide a mode to launch the container with debugger

        return (RemoteContainer) new DefaultContainerFactory().createContainer(
            containerId, ContainerType.REMOTE, configuration);
    }

    protected URL getServiceUrl(String contextPath) throws Exception {
        return new URL(serverUrl,"/"+contextPath+"/");
    }

    public void start() throws Exception {
        // the container is assumed to be started
        // noop
    }

    public void shutdown() throws Exception {
        // noop.
    }

    public String toString() {
        return "CargoRemoteContainer:"+container.getId();
    }
}

package com.sun.xml.ws.test.container;

import com.sun.xml.ws.test.tool.WsTool;

/**
 * Base implementation of {@link ApplicationContainer}.
 *
 * This implementation provides code for common tasks, such as assembling files
 * into a war, etc.
 *
 * @author Kohsuke Kawaguchi
 * @author Ken Hofsass
 */
public abstract class AbstractApplicationContainer implements ApplicationContainer {
    private final WsTool wsimport;
    private final WsTool wsgen;

    /**
     * Produce output for debugging the harness.
     */
    private final boolean debug;

    protected AbstractApplicationContainer(WsTool wsimport, WsTool wsgen, boolean debug) {
        this.wsimport = wsimport;
        this.wsgen = wsgen;
        this.debug = debug;
    }

    /**
     * Prepares an exploded war file image for this service.
     */
    protected final WAR assembleWar(DeployedService service) throws Exception {
        WAR war = new WAR(service,debug);

        boolean fromJava = (service.service.wsdl==null);

        if(!fromJava)
            war.compileWSDL(wsimport);
        war.compileJavac();
        if(fromJava)
            war.generateWSDL(wsgen);

        war.generateSunJaxWsXml();
        war.generateWebXml();

        return war;
    }
}
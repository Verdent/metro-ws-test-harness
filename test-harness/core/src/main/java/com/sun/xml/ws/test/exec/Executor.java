/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.test.exec;

import com.sun.xml.ws.test.container.DeploymentContext;
import junit.framework.TestCase;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes a part of a test.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class Executor extends TestCase {
    /**
     * Every {@link Executor} works for one {@link DeploymentContext}.
     */
    public final DeploymentContext context;

    /**
     * Map to store changed system properties - for future restore
     */
    private Map<String, String> systemProperties;

    @Override
    protected void setUp() throws Exception {
        setSystemProperties(context.descriptor.systemProperties);
    }

    @Override
    protected void tearDown() throws Exception {
        rollBackSystemProperties();
    }

    protected Executor(String name, DeploymentContext context) {
        super(context.descriptor.name+"."+name);
        this.context = context;
    }

    /**
     * Executes something.
     *
     * Error happened during this will be recorded as a test failure.
     */
    @Override
    public abstract void runTest() throws Throwable;

    protected final File makeWorkDir(String dirName) {
        File gensrcDir = new File(context.workDir, dirName);
        gensrcDir.mkdirs();
        return gensrcDir;
    }

    /**
     * Setting up required for test system properties
     * @param propertiesList list of properties
     */
    final void setSystemProperties(Collection<String> propertiesList) {
        systemProperties = new HashMap<String, String>();

        for (String property : propertiesList) {
            String[] parts = property.split("=");
            // **** validation ********
            if (parts.length != 2)
                throw new IllegalArgumentException("Bad system property: " + property);
            String name = parts[0];
            String newValue = parts[1];
            if (name == null || name.isEmpty() || newValue == null || newValue.isEmpty())
                throw new IllegalArgumentException("Bad system property: " + property);
            // ************

            String currentValue = System.getProperty(name);
            systemProperties.put(name, currentValue);
            System.setProperty(name, newValue);
        }
    }

    /**
     * Restoring original system properties
     */
    final void rollBackSystemProperties() {
        for (String name : systemProperties.keySet()) {

            String value = systemProperties.get(name);
            if (value != null)
                System.setProperty(name, value);
            else
                System.clearProperty(name);
        }
    }
}

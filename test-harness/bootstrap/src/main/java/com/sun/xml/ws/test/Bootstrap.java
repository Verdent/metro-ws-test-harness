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

package com.sun.xml.ws.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Entry point for setting up the classworlds.
 *
 * <p>
 * To maximize the isolation and avoid test interference, test harness
 * could should not be put into the system classloader. The bootstrap module
 * is the small code that's loaded into the system classloader.
 *
 * <p>
 * It's only job is to find all the jars that consistute the harness,
 * and create a {@link URLClassLoader}, then call into it.
 *
 * @author Kohsuke Kawaguchi
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {
        File home = getHomeDirectory();
        logger.fine("test harness home is "+home);

        // system properties are ugly but easy way to communicate values to the harness main code
        // setting a value other than String makes Ant upset
        System.getProperties().put("HARNESS_HOME",home.getPath());

        // create the harness realm and put everything in there
        List<URL> harness = new ArrayList<URL>();
        // extension hook to add more libraries
        File extLib = new File(home,"lib");
        if(extLib.exists()) {
            for (File jar : extLib.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            })) {
                logger.info("Adding "+jar+" to the harness realm");
                harness.add(jar.toURL());
            }
        }

        // add harness-lib.jar. Do this at the end so that overrides can take precedence.
        File libJar = new File(home,"harness-lib.jar");
        harness.add(libJar.toURL());

        // use the system classloader as the parent, so that the harness
        // and the test code can share the same JUnit
        ClassLoader cl = new URLClassLoader(harness.toArray(new URL[0]),
            ClassLoader.getSystemClassLoader());

        // call into the main method
        Class main = cl.loadClass("com.sun.xml.ws.test.Main");
        Method mainMethod = main.getMethod("main", String[].class);
        Thread.currentThread().setContextClassLoader(cl);
        mainMethod.invoke(null,new Object[]{args});
    }

    /**
     * Determines the 'home' directory of the test harness.
     * This is used to determine where to load other files.
     */
    private static File getHomeDirectory() throws IOException {
        String res = Bootstrap.class.getClassLoader().getResource("com/sun/xml/ws/test/Bootstrap.class").toExternalForm();
        if(res.startsWith("jar:")) {
            res = res.substring(4,res.lastIndexOf('!'));
            // different classloader behaves differently when it comes to space
            return new File(new URL(res).getFile().replace("%20"," ")).getParentFile();
        }
        throw new IllegalStateException("I can't figure out where the harness is loaded from: "+res);
    }

    private static final Logger logger = Logger.getLogger(Bootstrap.class.getName());
}

/*
 * Copyright 2012 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.glassfish;

import org.kohsuke.MetaInfServices;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestContainerFactory;
import org.ops4j.pax.swissbox.framework.FrameworkFactoryFinder;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * A {@link TestContainerFactory} for GlassFish test containers. This factory is published in
 * META-INF/services.
 * 
 * @author Harald Wellmann
 * @since 3.0.0
 */
@MetaInfServices
public class GlassFishTestContainerFactory implements TestContainerFactory {

    /**
     * Creates a GlassFish test container for the given exam system.
     * 
     * @return array with one element
     */
    public TestContainer[] create(ExamSystem system) {
        FrameworkFactory frameworkFactory = FrameworkFactoryFinder.loadSingleFrameworkFactory();
        GlassFishTestContainer container = new GlassFishTestContainer(system, frameworkFactory);
        return new TestContainer[] { container };
    }
}

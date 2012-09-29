/*
 * Copyright 2010 Toni Menzel.
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
package org.ops4j.pax.exam.spi.reactors;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.spi.StagedExamReactor;
import org.ops4j.pax.exam.spi.intern.DefaultTestAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One target only reactor implementation (simpliest and fastest)
 *
 * @author tonit
 */
public class EagerSingleStagedReactor implements StagedExamReactor {

    private static Logger LOG = LoggerFactory.getLogger( EagerSingleStagedReactor.class );

    final private List<TestContainer> m_targetContainer;
    final private List<TestProbeBuilder> m_probes;
    final private Map<TestAddress, TestContainer> m_map;

    /**
     * @param containers to be used
     * @param mProbes    to be installed on all probes
     */
    public EagerSingleStagedReactor( List<TestContainer> containers, List<TestProbeBuilder> mProbes )
    {
        m_map = new LinkedHashMap<TestAddress, TestContainer>();
        m_targetContainer = containers;
        m_probes = mProbes;

        int index = 0;
        for( TestContainer container : containers ) {
            String caption = buildCaption(containers, container, index);
            for( TestProbeBuilder builder : mProbes ) {
                // each probe has addresses.
                for( TestAddress a : builder.getTests() ) {
                    // we need to create a new, because "a" exists for each test container
                    // this new address makes the test (reachable via getTargets() ) reachable directly.
                    m_map.put( new DefaultTestAddress( a, caption ), container );
                }
            }
            index++;
        }
    }
    
    private String buildCaption( List<TestContainer> containers, TestContainer container, int index )
    {
        if (containers.size() == 1) {
            return container.toString();
        }
        else {
            return String.format("%s[%d]", container.toString(), index);
        }
    }

    
    
    public void setUp()
    {
        for( TestContainer container : m_targetContainer ) {
            container.start( );

            for( TestProbeBuilder builder : m_probes ) {
                LOG.debug( "installing probe " + builder );

                try {
                    container.install( builder.build().getStream() );
                } catch( IOException e ) {
                    throw new TestContainerException( "Unable to build the probe.", e );
                }
            }
        }        
    }

    public void invoke( TestAddress address )
        throws Exception
    {
        assert ( address != null ) : "TestAddress must not be null.";

        TestContainer testContainer = m_map.get( address );
        if( testContainer == null ) {
            throw new IllegalArgumentException( "TestAddress " + address + " not from this reactor? Got it from getTargets() really?" );
        }
        testContainer.call( address );
    }

    public Set<TestAddress> getTargets()
    {
        return m_map.keySet();
    }

    public void tearDown()
    {
        for( TestContainer container : m_targetContainer ) {
            container.stop();
        }
    }

    public void afterSuite()
    {
    }

    public void afterTest()
    {
    }

    public void beforeTest()
    {
    }

    public void afterClass()
    {
        tearDown();
    }

    public void beforeClass()
    {
        setUp();
    }

    public void beforeSuite()
    {
    }
}

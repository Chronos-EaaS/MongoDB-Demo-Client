/*
The MIT License (MIT)

Copyright (c) 2018-2021 The Chronos Project (chronos-eaas.org)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package ch.unibas.dmi.dbis.chronos.demoagent;


import ch.unibas.dmi.dbis.chronos.agent.AbstractChronosAgent;
import ch.unibas.dmi.dbis.chronos.agent.ChronosJob;
import ch.unibas.dmi.dbis.chronos.agent.ExecutionException;
import java.io.File;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class ChronosAgent extends AbstractChronosAgent {

    private static final Logger LOG = Logger.getLogger( ChronosAgent.class.getName() );

    private final String systemId;


    ChronosAgent( InetAddress address, int port, boolean secure, boolean useHostname, String environment, String systemId ) {
        super( address, port, secure, useHostname, environment );
        this.systemId = systemId;
    }


    @Override
    protected String[] getSupportedSystemNames() {
        return new String[]{ systemId };
    }


    @Override
    protected Object prepare( ChronosJob job, File inputDirectory, File outputDirectory, Properties results, Object prePhaseData ) throws ExecutionException {
        Map<String, String> settings = job.getParsedCdl();

        updateProgress( job, 1 );

        // system parameters
        final boolean journalingEnabled = settings.get( "journaling" ).equals( "true" );
        final String storageEngine = settings.get( "engine" );

        final Configuration configuration = new Configuration();
        configuration.setJournalEnabled( journalingEnabled );
        configuration.setStorageEngine( storageEngine );

        // YCSB parameters
        final String workload = settings.get( "workload" );
        final int numberOfThreads = Integer.parseInt( settings.get( "threads" ) );
        final int numberOfRecords = Integer.parseInt( settings.get( "records" ) );
        final int numberOfOperations = Integer.parseInt( settings.get( "operations" ) );

        // Stop MongoDB
        MongoDbWrapper.stop();
        updateProgress( job, 4 );

        // Wait a second to allow mongodb to complete its shutdown sequence
        try {
            TimeUnit.SECONDS.sleep( 1 );
        } catch ( InterruptedException e ) {
            // ignore
        }

        // Drop old MongoDB data
        MongoDbWrapper.drop();
        updateProgress( job, 5 );

        // Write MongoDB configuration
        File configFile = new File( outputDirectory, "mongodb.conf" );
        MongoDbWrapper.setConfiguration( configuration, configFile );
        updateProgress( job, 6 );

        // Start MongoDB process
        MongoDbWrapper.start();
        updateProgress( job, 8 );

        // Wait a second to allow mongodb to complete its startup sequence
        try {
            TimeUnit.SECONDS.sleep( 1 );
        } catch ( InterruptedException e ) {
            // ignore
        }

        // Download and prepare YCSB
        YcsbWrapper.install();
        updateProgress( job, 15 );

        // Prepare workload properties
        Properties properties = YcsbWrapper.readWorkloadProperties( workload );
        properties.setProperty( "recordcount", numberOfRecords + "" );
        properties.setProperty( "operationcount", numberOfOperations + "" );
        properties.setProperty( "threadcount", numberOfThreads + "" );
        YcsbWrapper.writeWorkloadProperties( properties );

        final File loadDataLog = new File( outputDirectory, "loadData.log" );
        YcsbWrapper.loadData( loadDataLog );
        updateProgress( job, 30 );

        return prePhaseData == null ? new Object() : prePhaseData;
    }


    @Override
    protected Object warmUp( ChronosJob job, File inputDirectory, File outputDirectory, Properties results, Object prePhaseData ) throws ExecutionException {
        return prePhaseData == null ? new Object() : prePhaseData;
    }


    @Override
    protected Object execute( ChronosJob job, File inputDirectory, File outputDirectory, Properties results, Object prePhaseData ) throws ExecutionException {
        updateProgress( job, 35 );
        final File runLog = new File( outputDirectory, "run.log" );
        YcsbWrapper.run( runLog );
        updateProgress( job, 90 );
        return runLog;
    }


    @Override
    protected Object analyze( ChronosJob job, File inputDirectory, File outputDirectory, Properties results, Object prePhaseData ) throws ExecutionException {
        final File runLog = (File) prePhaseData;
        results.putAll( YcsbWrapper.parseResult( runLog ) );
        return prePhaseData == null ? new Object() : prePhaseData;
    }


    @Override
    protected Object clean( ChronosJob job, File inputDirectory, File outputDirectory, Properties results, Object prePhaseData ) throws ExecutionException {
        updateProgress( job, 95 );
        MongoDbWrapper.stop();
        updateProgress( job, 100 );
        return prePhaseData == null ? new Object() : prePhaseData;
    }


    @Override
    protected void aborted( ChronosJob abortedJob ) {
        // TODO: abort running benchmark
    }


    @Override
    protected void failed( ChronosJob failedJob ) {
        // no cleanup required
    }


    public void updateProgress( ChronosJob job, int progress ) {
        setProgress( job, (byte) progress );
    }


}

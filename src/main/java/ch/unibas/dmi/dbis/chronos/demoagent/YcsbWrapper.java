/*
The MIT License (MIT)

Copyright (c) 2018 Databases and Information Systems Research Group, University of Basel, Switzerland

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


import ch.unibas.dmi.dbis.chronos.agent.ExecutionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;


public class YcsbWrapper {

    public static final String YCSB_VERSION = "0.17.0";

    private static final Logger LOG = Logger.getLogger( MongoDbWrapper.class.getName() );


    public static void install() throws ExecutionException {
        LOG.log( Level.INFO, "Install YCSB..." );
        try {
            // check if ycsb folder already exists
            if ( !new File( App.WORKING_DIR, "ycsb-" + YCSB_VERSION ).exists() || !new File( App.WORKING_DIR, "ycsb-" + YCSB_VERSION ).isDirectory() ) {
                // download ycsb
                ProcessBuilder processBuilder = new ProcessBuilder( "curl", "--silent", "--show-error", "--remote-name", "--location", "https://github.com/brianfrankcooper/YCSB/releases/download/" + YCSB_VERSION + "/ycsb-" + YCSB_VERSION + ".tar.gz" );
                processBuilder.redirectErrorStream( true );
                processBuilder.directory( App.WORKING_DIR );
                Process pwd = processBuilder.start();
                BufferedReader outputReader = new BufferedReader( new InputStreamReader( pwd.getInputStream() ) );
                pwd.waitFor();
                // If everything is fine, there should be no output
                String output;
                if ( (output = outputReader.readLine()) != null ) {
                    LOG.log( Level.WARNING, output );
                    while ( (output = outputReader.readLine()) != null ) {
                        LOG.log( Level.WARNING, output );
                    }
                    throw new ExecutionException( "Something went weong while downloading YCSB! See log for details." );
                }

                // extract
                Runtime.getRuntime().exec( "tar xfz ycsb-" + YCSB_VERSION + ".tar.gz" );
                processBuilder = new ProcessBuilder( "tar", "xfz", "ycsb-" + YCSB_VERSION + ".tar.gz" );
                processBuilder.redirectErrorStream( true );
                processBuilder.directory( App.WORKING_DIR );
                pwd = processBuilder.start();
                outputReader = new BufferedReader( new InputStreamReader( pwd.getInputStream() ) );
                pwd.waitFor();
                // If everything is fine, there should be no output
                if ( (output = outputReader.readLine()) != null ) {
                    LOG.log( Level.WARNING, output );
                    while ( (output = outputReader.readLine()) != null ) {
                        LOG.log( Level.WARNING, output );
                    }
                    throw new ExecutionException( "Something went weong while extracting YCSB! See log for details." );
                }
            }
        } catch ( InterruptedException | IOException e ) {
            throw new ExecutionException( "Something went wrong while installing YCSB!", e );
        }
    }


    public static void loadData( final File logFile ) throws ExecutionException {
        try {
            LOG.log( Level.INFO, "Load YCSB Data..." );
            ProcessBuilder processBuilder = new ProcessBuilder( "./ycsb-" + YCSB_VERSION + "/bin/ycsb", "load", "mongodb", "-P", new File( App.WORKING_DIR, "workload" ).getAbsolutePath() );
            processBuilder.redirectOutput( logFile );
            processBuilder.directory( App.WORKING_DIR );
            Process pwd = processBuilder.start();
            BufferedReader outputReader = new BufferedReader( new InputStreamReader( pwd.getErrorStream() ) );
            pwd.waitFor();
            String outputLine;
            StringBuilder output = new StringBuilder();
            while ( (outputLine = outputReader.readLine()) != null ) {
                output.append( outputLine ).append( "\n" );
            }
            LOG.log( Level.WARNING, output.toString() );
        } catch ( InterruptedException | IOException e ) {
            throw new ExecutionException( "Something went wrong while loading YCSB data into the data store!", e );
        }
    }


    public static void run( final File logFile ) throws ExecutionException {
        LOG.log( Level.INFO, "Running YCSB Benchmark..." );
        try {
            ProcessBuilder processBuilder = new ProcessBuilder( "./ycsb-" + YCSB_VERSION + "/bin/ycsb", "run", "mongodb", "-P", new File( App.WORKING_DIR, "workload" ).getAbsolutePath() );
            processBuilder.redirectOutput( logFile );
            processBuilder.directory( App.WORKING_DIR );
            Process pwd = processBuilder.start();
            BufferedReader outputReader = new BufferedReader( new InputStreamReader( pwd.getErrorStream() ) );
            pwd.waitFor();
            String outputLine;
            StringBuilder output = new StringBuilder();
            while ( (outputLine = outputReader.readLine()) != null ) {
                output.append( outputLine ).append( "\n" );
            }
            LOG.log( Level.WARNING, output.toString() );
        } catch ( InterruptedException | IOException e ) {
            throw new ExecutionException( "Something went wrong while executing YCSB benchmark!", e );
        }
    }


    public static Properties readWorkloadProperties( final String workloadName ) throws ExecutionException {
        try {
            File f = new File( App.WORKING_DIR, "ycsb-" + YCSB_VERSION + "/workloads/" + workloadName );
            InputStream is = new FileInputStream( f );
            Properties properties = new Properties();
            properties.load( is );
            return properties;
        } catch ( Exception e ) {
            throw new ExecutionException( e );
        }
    }


    public static void writeWorkloadProperties( Properties properties ) throws ExecutionException {
        try {
            File f = new File( App.WORKING_DIR, "workload" );
            OutputStream out = new FileOutputStream( f );
            properties.store( out, "YCSB workload properties" );
        } catch ( Exception e ) {
            throw new ExecutionException( e );
        }
    }


    public static Properties parseResult( File file ) throws ExecutionException {
        Properties results = new Properties();
        try {
            List<String> lines = FileUtils.readLines( file, "UTF-8" );
            for ( String line : lines ) {
                String[] parts = line.split( "\\s*,\\s*" );
                String group = null;
                switch ( parts[0] ) {
                    case "[OVERALL]":
                        group = "";
                        break;
                    case "[READ]":
                        group = "read.";
                        break;
                    case "[UPDATE]":
                        group = "update.";
                        break;
                    case "[INSERT]":
                        group = "insert.";
                        break;
                    case "[SCAN]":
                        group = "scan.";
                        break;
                    case "[CLEANUP]":
                        group = "cleanup.";
                        break;
                }
                if ( group != null ) {
                    switch ( parts[1] ) {
                        case "RunTime(ms)":
                            results.setProperty( "runtime", parts[2] );
                            break;
                        case "Throughput(ops/sec)":
                            results.setProperty( "throughput", parts[2] );
                            break;
                        case "Operations":
                            results.setProperty( group + "operations", parts[2] );
                            break;
                        case "AverageLatency(us)":
                            results.setProperty( group + "latency", parts[2] );
                            break;
                        case "MinLatency(us)":
                            results.setProperty( group + "minlatency", parts[2] );
                            break;
                        case "MaxLatency(us)":
                            results.setProperty( group + "maxlatency", parts[2] );
                            break;
                        case "95thPercentileLatency(us)":
                            results.setProperty( group + "95latency", parts[2] );
                            break;
                        case "99thPercentileLatency(us)":
                            results.setProperty( group + "99latency", parts[2] );
                            break;
                        case "Return=OK":
                            results.setProperty( group + "ok", parts[2] );
                            break;
                    }
                }
            }
        } catch ( IOException e ) {
            throw new ExecutionException( e );
        }
        return results;
    }

}

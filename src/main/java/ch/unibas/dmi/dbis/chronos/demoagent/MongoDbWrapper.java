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


import ch.unibas.dmi.dbis.chronos.agent.ExecutionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MongoDbWrapper {

    private static final Logger LOG = Logger.getLogger( MongoDbWrapper.class.getName() );


    public static void start() throws ExecutionException {
        LOG.log( Level.INFO, "Start MongoDB..." );
        try {
            ProcessBuilder processBuilder = new ProcessBuilder( "/usr/bin/sudo", "service", "mongodb", "start" );
            processBuilder.redirectErrorStream( true );
            processBuilder.directory( App.WORKING_DIR );
            Process pwd = processBuilder.start();
            BufferedReader outputReader = new BufferedReader( new InputStreamReader( pwd.getInputStream() ) );
            pwd.waitFor();
            // If everything is fine, there should be no output
            String output;
            if ( (output = outputReader.readLine()) != null ) {
                StringBuilder outputBuilder = new StringBuilder();
                outputBuilder.append( output ).append( "\n" );
                while ( (output = outputReader.readLine()) != null ) {
                    outputBuilder.append( output ).append( "\n" );
                }
                LOG.log( Level.WARNING, outputBuilder.toString() );
                throw new ExecutionException( "Error while starting MongoDB! See log for details." );
            }
        } catch ( InterruptedException | IOException e ) {
            throw new ExecutionException( "Error while starting MongoDB!", e );
        }
    }


    public static void stop() throws ExecutionException {
        LOG.log( Level.INFO, "Stop MongoDB..." );
        try {
            ProcessBuilder processBuilder = new ProcessBuilder( "/usr/bin/sudo", "service", "mongodb", "force-stop" );
            processBuilder.redirectErrorStream( true );
            processBuilder.directory( App.WORKING_DIR );
            Process pwd = processBuilder.start();
            BufferedReader outputReader = new BufferedReader( new InputStreamReader( pwd.getInputStream() ) );
            pwd.waitFor();
            // If everything is fine, there should be no output
            String output;
            if ( (output = outputReader.readLine()) != null ) {
                StringBuilder outputBuilder = new StringBuilder();
                outputBuilder.append( output ).append( "\n" );
                while ( (output = outputReader.readLine()) != null ) {
                    outputBuilder.append( output ).append( "\n" );
                }
                LOG.log( Level.WARNING, outputBuilder.toString() );
                throw new ExecutionException( "Error while stopping MongoDB! See log for details." );
            }
        } catch ( InterruptedException | IOException e ) {
            throw new ExecutionException( "Error while stopping MongoDB!", e );
        }
    }


    public static void drop() throws ExecutionException {
        LOG.log( Level.INFO, "Drop data..." );
        try {
            ProcessBuilder processBuilder = new ProcessBuilder( "/bin/bash", "-c", "/usr/bin/sudo rm -rf /var/lib/mongodb/*" );
            processBuilder.redirectErrorStream( true );
            processBuilder.directory( App.WORKING_DIR );
            Process pwd = processBuilder.start();
            BufferedReader outputReader = new BufferedReader( new InputStreamReader( pwd.getInputStream() ) );
            pwd.waitFor();
            // If everything is fine, there should be no output
            String output;
            if ( (output = outputReader.readLine()) != null ) {
                StringBuilder outputBuilder = new StringBuilder();
                outputBuilder.append( output ).append( "\n" );
                while ( (output = outputReader.readLine()) != null ) {
                    outputBuilder.append( output ).append( "\n" );
                }
                LOG.log( Level.WARNING, outputBuilder.toString() );
                throw new ExecutionException( "Error while dropping data of MongoDB! See log for details." );
            }
        } catch ( InterruptedException | IOException e ) {
            throw new ExecutionException( "Error while dropping data of MongoDB!", e );
        }
    }


    public static void setConfiguration( final Configuration configuration, File configFile ) throws ExecutionException {
        try (
                PrintWriter etc = new PrintWriter( "/etc/mongodb.conf" );
                PrintWriter archive = new PrintWriter( configFile )
        ) {
            etc.println( configuration.toString() );
            archive.println( configuration.toString() );
        } catch ( FileNotFoundException e ) {
            LOG.log( Level.WARNING, "Exception while writing config file", e );
            throw new ExecutionException( "Error while writing MongoDB configuration! See log for details." );
        }
    }

}

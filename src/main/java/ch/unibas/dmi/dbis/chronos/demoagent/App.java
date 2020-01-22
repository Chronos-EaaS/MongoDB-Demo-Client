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


import ch.unibas.dmi.dbis.chronos.agent.AbstractChronosAgent;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;


@Command(name = "chronos-demo-agent", description = "Demo Chonos Agent to show the features of Chronos.")
public class App implements Runnable {

    public static final File WORKING_DIR = new File( new File( System.getProperty( "user.dir" ) ), "chronos-mongodb" );

    @Required
    @Option(name = { "-h", "--host" }, description = "Hostname or IP-Address of the Chronos server.")
    private String hostname = "";

    @Option(name = { "-p", "--port" }, description = "Port of the REST API of the Chronos server.")
    private int port = 443;

    @Option(name = { "-e", "--environment" }, description = "Identifier of the evaluation environment this client runs in.")
    private String environment = "";


    public static void main( String[] args ) {
        SingleCommand<App> parser = SingleCommand.singleCommand( App.class );
        App cmd = parser.parse( args );
        cmd.run();
    }


    @Override
    public void run() {
        InetAddress address = null;
        try {
            address = InetAddress.getByName( hostname );
        } catch ( UnknownHostException e ) {
            System.err.println( "The given host '" + hostname + "' cannot be resolved." );
            System.exit( 1 );
        }

        if ( !WORKING_DIR.exists() ) {
            WORKING_DIR.mkdir();
        }

        AbstractChronosAgent aca = new ChronosAgent( address, port, true, true, environment );
        aca.setDaemon( false );
        aca.start();
    }
}

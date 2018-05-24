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


import lombok.Data;


@Data
public class Configuration {

    // storage
    private String dbPath = "/var/lib/mongodb";
    private boolean journalEnabled = true;
    private String storageEngine = "wiredTiger";

    // systemLog
    private String logDestination = "file";
    private boolean logAppend = true;
    private String logPath = "/var/log/mongodb/mongod.log";

    // network
    private int port = 27017;
    private String bindIp = "127.0.0.1";

    // processManagement
    private String timeZoneInfo = "/usr/share/zoneinfo";


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append( "storage:\n" );
        builder.append( "    dbPath: " ).append( dbPath ).append( "\n" );
        builder.append( "    journal:\n" ).append( "        enabled: " );
        if ( journalEnabled ) {
            builder.append( "true\n" );
        } else {
            builder.append( "false\n" );
        }
        builder.append( "    engine: " ).append( storageEngine ).append( "\n" );
        builder.append( "\n" );

        builder.append( "systemLog:\n" );
        builder.append( "    destination: " ).append( logDestination ).append( "\n" );
        if ( logAppend ) {
            builder.append( "    logAppend: true\n" );
        } else {
            builder.append( "    logAppend: false\n" );
        }
        builder.append( "    path: " ).append( logPath ).append( "\n" );
        builder.append( "\n" );

        builder.append( "net:\n" );
        builder.append( "    port: " ).append( port ).append( "\n" );
        builder.append( "    bindIp: " ).append( bindIp ).append( "\n" );
        builder.append( "\n" );

        builder.append( "processManagement:\n" );
        builder.append( "    timeZoneInfo: " ).append( timeZoneInfo ).append( "\n" );

        return builder.toString();
    }


}

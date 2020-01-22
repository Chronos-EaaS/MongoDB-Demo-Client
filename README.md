# MongoDB Demo Client
A Chronos Client created for demonstration purposes which allows to benchmark MongoDB using the YCSB benchmark.

## Setup
In the following, it is assumed that the steps are executed on a freshly installed Ubuntu 18.04. For other versions and operating systems, the following steps might have to be adapted.

Install Java 8
> sudo apt install openjdk-8-jre-headless

Install MongoDB
> sudo apt install mongodb

Import the provided system settings on Chronos. You can find them in the chronos folder in the root directory of this repository. To import the settings:
1. Create a new system in Chronos (e.g., name: _MongoDB_, description: _MongoDB demo evaluation_).
2. Import the three files by clicking on the "Import" button on the settings page of the newly created system.
3. Get the _SYSTEM_IDENTIFIER_ by clicking on the button "System ID".

Get `mongodb-demo-client.jar`
> wget https://github.com/Chronos-EaaS/MongoDB-Demo-Client/releases/latest/download/mongodb-demo-client.jar

Start the demo client 
> sudo java -jar mongodb-demo-client.jar -h _CHRONOS_SERVER_ -e lab -s _SYSTEM_IDENTIFIER_

That’s all! You can now create projects and experiments and run evaluations.
  

## License
The MIT License (MIT)
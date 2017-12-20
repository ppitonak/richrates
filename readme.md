# RichRates [![Build Status](https://travis-ci.org/ppitonak/richrates.png?branch=master)](https://travis-ci.org/ppitonak/richrates/builds)

RichRates is a sample LGPL-licenced web application that demonstrates usage of Java EE 6 platform technologies
such as JSF 2, RichFaces, CDI and Bean Validation. The application consists of three pages. The first page contains a calculator with which it 
is possible to convert Euros to about 30 foreign currencies and vice versa. The second page contains a table 
with all available exchange rates for selected day. The last page displays a chart for one selected currency. 
It is possible to select time range for which this chart will be displayed. All data are provided by European 
Central Bank. Since it is a demo application, there are only data for last 90 days, excluding weekends and 
state holidays.

The application demonstrates lot of new JSF features, such as bean annotations, new scopes, implicit
navigation, View Declaration Language and JSF2's Ajax tag.

## Requirements

* Apache Maven 3.3 or newer
* OpenJDK 8 or Sun JDK 8 or newer
* WildFly 8.2.0.Final or newer

## Start WildFly with the web profile

1. Open a command line and navigate to the root of the WildFly server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   WILDFLY_HOME/bin/standalone.sh
        For Windows: WILDFLY_HOME\bin\standalone.bat

## Build and deploy

1. Make sure you have started the WildFly as described above.
2. Open a command line and navigate to the root directory of application.
3. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

4. This will deploy `target/richrates.war` to the running instance of the server.

## Access the application 

The application will be running at the following URL: [http://localhost:8080/richrates](http://localhost:8080/richrates).

## Undeploy the Archive

1. Make sure you have started the WildFly as described above.
2. Open a command line and navigate to the root directory of application.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy

## Run the tests

There are three types of tests featured:

1. TestNG unit tests;
2. Arquillian functional tests; and
3. Selenium functional tests.

To execute unit tests, run 

    mvn clean package

To execute only Arquillian functional tests, run 

    mvn clean verify -P wildfly-managed,ftest

To execute both unit tests and functional tests, run

    mvn clean verify -P wildfly-managed,all-tests

All functional tests run in real Java EE container so the commands above will download WildFly 8 from a Maven repository, start the container, deploy the application, start Firefox, and execute the tests.

## Run the tests with different browsers

All functional tests run by default with Firefox which is on PATH. To select a different browser either edit arquillian.xml property "browser" or set an environment property. Current supported values are:

* android
* chrome
* firefox (set in arquillian.xml)
* phantomjs
* internetExplorer
* iphone
* opera

For instance, to run tests with Opera, run

    mvn clean verify -P wildfly-managed,all-tests -D arq.extension.webdriver.browser=opera

You can use PhantomJS to run tests in a headless environment, run
    
    mvn clean verify -P wildfly-managed,all-tests -D arq.extension.webdriver.browser=phantomjs

## Credits

The application uses data provided by [European Central Bank](http://www.ecb.europa.eu).
The images of flags provided by [RedpixArt Design](http://flags.redpixart.com).

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/ppitonak/richrates/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

Direct Method Invocation (DMI) DataSource
-----------------------------------------
This example illustrates setting up a DataSource that accesses your servlet
controller, using Hibernate to process the requests. This example uses a
Spring MVC Controller class.


Using sample projects
---------------------
Instructions are provided below for importing the sample project with Eclipse or
building it from the command line with Ant.

If you instead want to add Smart GWT Power Edition to an existing project, see these
instructions:

   http://www.smartclient.com/smartgwtee/javadoc/com/smartgwt/client/docs/SgwtEESetup.html

For instructions for launching tools such as the Developer Console, Visual
Builder or DataSource Wizards, see the Smart GWT FAQ:

   http://forums.smartclient.com/showthread.php?t=8159


Build Prerequisites
-------------------
- Google Web Toolkit, 1.5.3 or later (2.0 or later recommended)
  https://developers.google.com/web-toolkit/download

  Ensure that the GWT_HOME environment variable is set to the location of your
  Google Web Toolkit SDK directory.

- If building with ant:
  Apache Ant, 1.6.5 or later (1.7.1 or later recommended)
  http://ant.apache.org/

  A copy of Ant is included in the Smart GWT distribution under the
  'apache-ant-1.7.1' directory.

  Ensure that the ANT_HOME environment variable is set to the location of your
  Apache Ant directory, and the 'ant' command is in your PATH.

- If building with Eclipse:
  Google Eclipse Plugin (GEP)
  https://developers.google.com/eclipse/docs/download

  The GEP is highly recommended if using Eclipse. Its use is assumed in the
  instructions below.


Build and deployment using ant
------------------------------
- 'ant hosted'
  Run in GWT Hosted Mode.


- 'ant'
  Compile for deployment.


- 'ant war'
  Compile for deployment and bundle into HibernateDS.war file.

  If you have a web container operating on localhost:8080, you can deploy
  HibernateDS.war into the web container and access it at:

    http://localhost:8080/HibernateDS/HibernateDS.html


Super Development Mode
----------------------
Hosted Mode is being being phased out by GWT and replaced with Super Dev Mode.
You can find an overview of SDM at:

    http://www.gwtproject.org/articles/superdevmode.html

For more details about how to set up or use SDM, see our online Smart GWT SDM
Troubleshooting help topic at:

    http://www.smartclient.com/smartgwt/javadoc/com/smartgwt/client/docs/SuperDevModeTroubleshooting.html


Eclipse Configuration
---------------------
Eclipse and GEP are configured to open this sample as follows:

- Set the Eclipse Classpath variable SGWTPOWER_HOME to point to the root directory
  of the Smart GWT Power Edition distribution. This is configured through
    Windows: Window  -> Preferences -> Java -> Build Path -> Classpath Variables
    MacOS X: Eclipse -> Preferences -> Java -> Build Path -> Classpath Variables
  The included Eclipse project files load JARs from SGWTPOWER_HOME/lib.

- Follow the instructions for  "Working with Existing Projects" with the GEP:
  https://developers.google.com/eclipse/docs/existingprojects

  As of this writing, the instructions are slightly out of date for Eclipse 4.  After
  selecting "New -> Java Project" there won't be an option to "Create project from 
  existing source".  Instead, uncheck "Use default location" below "Project name" and
  browse to the root directory of the desired sample in your installation.

- Create a launch configuration and launch the app as described here:
  https://developers.google.com/eclipse/docs/running_and_debugging_2_0

- (Optional) Attach the server Javadocs for the Server Framework JARs:
  - In Eclipse, right click on the project in Package Explorer.
  - On the left, select "Java Build Path".
  - Click on the Libraries tab.
  - For each of the entries SGWTPOWER_HOME/lib/isomorphic_contentexport.jar, SGWTPOWER_HOME/lib/isomorphic_core_rpc.jar,
    SGWTPOWER_HOME/lib/isomorphic_spring.jar, SGWTPOWER_HOME/lib/isomorphic_sql.jar, and SGWTPOWER_HOME/lib/isomorphic_tools.jar:
    - Click the plus icon (Windows) or arrow (Mac) next to the entry.
    - Select "Javadoc location:".
    - Click the "Edit..." button.
    - Click the "Javadoc URL" radio and browse to the doc/server/javadoc/ folder from the unpacked Smart GWT Power Edition distribution.


Database Configuration
----------------------
This sample connects to an HSQL database that contains various tables with
sample data. The database name is "isomorphic" and is located under
war/WEB-INF/db/hsqldb.

For ease of setup of this sample, a servlet listener HSQLServletContextListener
is configured in war/WEB-INF/web.xml to start and stop HSQL automatically as the
webapp is started/stopped. The contents of the database may be viewed
directly with the sample located at samples/db/run-dbm: select
'HSQL Database Engine Server' and point to

    jdbc:hsqldb:hsql://localhost/isomorphic

to browse the contents of the HSQL database.


Debugging
---------
See our debugging overview at:

    http://www.smartclient.com/smartgwt/javadoc/com/smartgwt/client/docs/Debugging.html

Note: Due to a bug in GWT, the very first time you launch hosted mode, tools such
as the Admin Console will not work. Restart to correct this problem. Compiled mode
is not affected.

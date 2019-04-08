// StateModGUI - main program file for StateMod GUI
//
/* NoticeStart

StateMod GUI
StateMod GUI is a part of Colorado's Decision Support Systems (CDSS)
Copyright (C) 1997-2019 Colorado Department of Natural Resources

StateMod GUI is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

StateMod GUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

You should have received a copy of the GNU General Public License
    along with StateMod GUI.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

// ---------------------------------------------------------------------------
// StateModGUI - main program file for StateMod GUI
// ---------------------------------------------------------------------------
// History:
//
// 01 Apr 2001	Steven A. Malers, RTi	Remove import *.
// 04 May 2001	SAM, RTi		Derive from Applet so a preview on the
//					web can be enabled.
// 23 Sep 2001	SAM, RTi		Update to 05.06.01 to go with StateMod
//					9.96.
// 2001-10-19	SAM, RTi		Update to version 05.07.00.  Use the
//					new low-level mapping code that is also
//					used in StateView/CWRAT 02.06.00.
// 2001-11-21	SAM, RTi		Update to version 05.08.00.  Define a
//					public static string for the program
//					version to be used in the help about
//					window.
// 2002-06-20	SAM, RTi		Update to version 05.09.00.  This
//					includes better handling of wells.
//					Support updated delplt to work with new
//					parameter names.  Enable time series
//					products with -test.
// 2002-07-03	SAM, RTi		Update to version 05.10.00.  Put the
//					login name in the log file.  Fix
//					problems running on a server.
// 2002-07-15	SAM, RTi		Update to version 05.10.01.  Fix some
//					ProcessManager code that was causing
//					the delplt exit status to not be
//					detected correctly.
// 2002-08-22	SAM, RTi		Update to version 05.10.02.  Make new
//					menu structure final.  Use GeoView if
//					a .gvp file is detected in the response
//					file.  Make baseflow data type available
//					in the graphing tool.  Make the default
//					image cover the whole initial screen.
// 2002-10-20	SAM, RTi		Update to use the new ProcessManager,
//					which results in MUCH better overall
//					performance.  Update to version
//					05.10.03.
// 2002-10-29	SAM, RTi		Update to 05.10.04 to reflect minor
//					changes in the process manager.
// 2003-01-31	SAM, RTi		Update to 05.11.00 - hopefully the final
//					JRE 1.1.8 release.  Change the default
//					location of documentation to
//					HOME\doc\SMGUI\UserManual.  Because we
//					are relying on a PDF for documentation,
//					stub out the on-line HTML help.
// ---------------------------------------------------------------------------
// 2003-06-03	J. Thomas Sapienza, RTi	Initial Swing version (06.00.00)
// 2003-07-30	SAM, RTi		* Change StateModGUI_Main_JFrame to
//					  StateMod_JFrame.
//					* Remove -user command line option -
//					  this was apparently brought over from
//					  CWRAT but is not used here.  The user
//					  is determined from the operating
//					  system.
//					* Remove the home and user parameter
//					  being passed to the GUI class - not
//					  needed.
//					* Add the initialize() method from
//					  StateDMI().
//					* Javadoc methods.
//					* Change to use Swing JApplet instead
//					  of Applet.
//					* Add quitProgram().
// 2003-09-06	SAM, RTi		* Split initialize() into initialize1()
//					  and initialize2() to handle setup
//					  before and after arguments are
//					  parsed.  Previously the home directory
//					  was not being detected correctly.
//					* Enable -h and -v command line options.
// 2003-09-18	JTS, RTi		* Now sets up the image icon to load.
// 2003-10-02	SAM, RTi		* Change version to 06.01.00 Beta.
// 2003-10-12	SAM, RTi		* Change version to 06.01.01 2003-10-10
//					  Beta - release to Ray Bennett.
// 2003-10-19	SAM, RTi		* Change version to 06.01.02 2003-10-16
//					  Beta - release to Ray Bennett.
// 2003-10-22	SAM, RTi		* Change version to 06.01.03 2003-10-22
//					  Beta - release to Ray and Ray.
// 2003-10-28	SAM, RTi		* Change version to 06.01.04 2003-10-28
//					  Beta - release to Ray Bennett.
// 2003-10-30	SAM, RTi		* Change version to 06.01.05 2003-10-30
//					  Beta - release to Ray Alvarado and
//					  Ray Bennett.
// 2003-11-06	SAM, RTi		* Change version to 06.01.06 2003-11-06
//					  Beta - release to Ray Alvarado and
//					  Ray Bennett.
// 2003-11-07	SAM, RTi		* Change version to 06.01.07 2003-11-07
//					  Beta - release to Ray Alvarado and
//					  Ray Bennett.  Has demand/supply
//					  summary.
// 2003-12-01	SAM, RTi		* Change version to 06.01.08 2003-12-01
//					  Beta - relaease to Ray Alvarado and
//					  Ray Bennett.  Has binary file
//					  graphing.
// 2004-08-04	JTS, RTi		Added -release flag.
// 2004-08-25	JTS, RTi		Update to version 06.02.00 2004-08-25.	
// 2004-10-14	SAM, RTi		Update to version 06.03.00 for final
//					review/release.
//					* Include recent table model updates to
//					  be consistent with StateDMI 01.17.00.
//					* Fix problem in graphing tool.
// 2005-06-08	JTS, RTi		Added call to setApplicationHome().
// 2006-01-20	JTS, RTi		Updated to version 06.03.01 2006-01-20.
//					The following changes were made:
//					* Corrected a bug in the main Diversions
//					  screen that would corrupt data when
//					  the user sorted the diversion list,
//					  went to the return flow or water right
//					  sub-form, closed the screen, and saved
//					  changes.
// 					* All the main forms that consist of a
//					  table on the left side now implement
//					  JWorksheet sort listeners.  These make
//					  sure that once the table is sorted,
//					  the same record is selected after the
//					  sort.  
//					* The "Graph" button was not visible on
//					  the Delay Table screen, so the GUI
//					  was widened.
// 2006-01-20	SAM, RTi		* Change the startup message levels to
//					  minimize terminal output.
//					* Change Help About to indicate that
//					  StateMod version 11.06 is used with
//					  development and test the new binary
//					  file.
// 2006-02-28	SAM, RTi		Updated to version 06.03.02.
//					* Add a graph for reservoir curves,
//					  using the JFreeChart package.
// 2006-08-16	SAM, RTi		Updated to version 06.04.00.
//					* Begin adding support for plan stations
//					  and call time series.
//					* Test against Statement 11.27 for
//					  South Platte work.
// 2006-08-22	SAM, RTi		Update to version 06.05.00.
//					* Enable plan stations and call time
//					  series.
// 2006-11-08	KAT, RTi		Changed how the -home working is set
// 					  by using getCanonicalPath() to resolve relative paths
// 2007-02-18	SAM, RTi		Update to version 7.00.00
// 2007-03-03	SAM, RTi		Set the release date as 03.02.00 to be
//					consistent with other software releases.
// ---------------------------------------------------------------------------
// EndHeader

package DWR.StateModGUI;

import java.io.File;
import java.io.IOException;

import javax.swing.JApplet;

import RTi.Util.GUI.JGUIUtil;
import RTi.Util.IO.DataDimension;
import RTi.Util.IO.DataUnits;
import RTi.Util.IO.IOUtil;
import RTi.Util.IO.PropList;
import RTi.Util.Message.Message;

import DWR.StateMod.StateMod_OperationalRight_Metadata;
import DWR.StateMod.StateMod_Util;

/**
The StateModGUI class is the entry point into the StateMod GUI application.  The
program can run either as an applet or a stand-alone application.  The main
program performs basic initialization and then starts up the graphical user interface.
*/
@SuppressWarnings("serial")
public class StateModGUI extends JApplet
{

public static String PROGRAM_NAME = "StateModGUI";
public static String PROGRAM_VERSION = "07.04.00 (2013-04-17)";
private static String __home;

/**
StateMod GUI configuration properties read from the StateModGUI.cfg file at software startup.
*/
private static PropList __configProps = new PropList("");

/**
Return a value from the StateMod GUI configuration properties.
*/
public static String getConfigurationPropValue ( String propertyName )
{
	return __configProps.getValue ( propertyName );
}

/**
Instantiates the application instance from an applet.
*/
public void init()
{	IOUtil.setApplet ( this );
	IOUtil.setProgramData ( PROGRAM_NAME, PROGRAM_VERSION, null );
	try {
		parseArgs(this);
	}
	catch (Exception e) {
		Message.printWarning(2, "StateModGUI",
		"Problem parsing command line arguments.  Using default behavior" );
	}	

	JGUIUtil.setSystemLookAndFeel(true);

	try {
		// Instantiate Main GUI
		JGUIUtil.setSystemLookAndFeel(true);
		new StateModGUI_JFrame( __home );
	}
	catch (Exception e) {
		Message.printWarning(2, "init", "Problem starting " + PROGRAM_NAME + "... exiting gracefully.");
		Message.printWarning(2, "StateDMI.init", e);
		quitProgram ( 1 );
	}
}

/**
Initialize default message levels.  See initialize2() for initialization after
command line arguments have been parsed.
*/
private static void initialize1 ()
{	// Initialize message levels...

	Message.setDebugLevel( Message.TERM_OUTPUT, 0 );
	Message.setDebugLevel( Message.LOG_OUTPUT, 0 );
	Message.setStatusLevel( Message.TERM_OUTPUT, 0 );
	Message.setStatusLevel( Message.LOG_OUTPUT, 2 );
	Message.setWarningLevel( Message.TERM_OUTPUT, 0 );
	Message.setWarningLevel( Message.LOG_OUTPUT, 3 );
}

/**
Initialize private data after arguments are processed (mainly to know the home directory).
The data units and operating rules data are read.
*/
private static void initialize2 ()
{	String routine = "StateModGUI.initialize2";

	// Check the home directory...

	if ( !IOUtil.isApplet() && (__home == null) ) {
		__home = "\\CDSS";
		Message.printWarning ( 1, routine,
		"-home was not specified on the command line.  Assuming \"" + __home + "\"" );
	}

	// Set up the units conversion data.  For now read from the DATAUNIT
	// file but in the future may read from HydroBase...

	String units_file = __home + File.separator + "system" + File.separator + "DATAUNIT";

	try {
		DataUnits.readUnitsFile( units_file );
	}
	catch ( Exception e ) {
		Message.printWarning ( 2, routine,
		"Error reading units file \"" + units_file + "\" (" + e + ")\n" +
		"Using default units.  Some conversions will not be supported.\n" +
		"Default output precisions may not be appropriate." );
		try {
			DataDimension dim = DataDimension.lookupDimension ( "L" );
			String dim_abbrev = dim.getAbbreviation();
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 1, "MM", "Millimeter", 1, 1, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "CM", "Centimeter", 2, 10, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "M", "Meter", 2, 1000, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "KM", "Kilometer", 1, 1000000, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "IN", "Inch", 2, 25.4, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "FT", "Feet", 2, 304.8, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "MI", "Mile", 1, 1609344, 0 ) );
		
			dim = DataDimension.lookupDimension ( "L3" );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 1, "ACFT", "Acre-feet", 1, 1, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev, 0, "AF", "Acre-feet", 1, 1, 0 ) );
			DataUnits.addUnits( new DataUnits ( dim_abbrev,	0, "AF/M", "AF/Month", 1, 1, 0 ) );
		}
		catch ( Exception e2 ) {
			// Trouble configuring units.
			Message.printWarning ( 2, routine, "Error setting default units conversions (" + e2 + ")." );
			Message.printWarning ( 2, routine, e );
		}
	}
	
	// Read the operational rights configuration file
	readOperatingRulesFile ();	
}

/**
Instantiates the main application instance when run stand-alone.
@param args Command line arguments.
*/
public static void main (String args[])
{	String routine = "StateModGUI.main";

	try {
		// Main try...

		// Initial set...
	
		IOUtil.setProgramData ( PROGRAM_NAME, PROGRAM_VERSION, args );
		// Use StateMod here (not StateModGUI) because users associate more with the
		// former and expect everything to have a GUI anyhow...
		JGUIUtil.setAppNameForWindows("StateMod");
	
		// Note that messages will not be printed to the log file until the log file is opened below...
	
		initialize1();
	
		try {
			parseArgs(args);
		}
		catch (Exception e) {
			Message.printWarning(1, routine, "Error parsing command line arguments.  Using default behavior." );
			Message.printWarning(1, "StateModGUI", e );
		}
		
		// Open the log file...
		openLogFile();
	
		initialize2();
		
		// Read configuration properties.  This will also take action such as setting the StateMod executable
		// location in StateMod_Util.
		__configProps = readConfigurationFile ();
		
		setIcon();
		
	    Message.printStatus(1, routine, "Running stand-alone version.");
	
		JGUIUtil.setSystemLookAndFeel(true);
		new StateModGUI_JFrame( __home );
	}
	catch (Exception e) {
		Message.printWarning(2, routine, "Error starting " + PROGRAM_NAME + "." );
		Message.printWarning(2, routine, e);
		System.out.println ( "Error starting " + PROGRAM_NAME + ", home=\"" + __home + "\"." );
		e.printStackTrace();
		quitProgram ( 1 );
	}
}

/**
Open the log file.
*/
private static void openLogFile ()
{	String routine = "StateModGUI.openLogFile";
	String logfile = "";
	String user = IOUtil.getProgramUser();

	if ( (__home == null) || (__home.length() == 0) || (__home.charAt(0) == '.')) {
		Message.printWarning ( 2, routine, "Home directory is not defined.  Not opening log file.");
	}
	else {
		if ( (user == null) || user.trim().equals("")) {
			logfile = __home + File.separator + "logs" + File.separator + "StateModGUI.log";
		}
		else {
			logfile = __home + File.separator + "logs" + File.separator + "StateModGUI_" + user + ".log";
		}
		Message.printStatus ( 1, routine, "Log file name: " + logfile );
		try {
			Message.openLogFile ( logfile );
		}
		catch (Exception e) {
			Message.printWarning ( 1, routine, "Error opening log file \"" + logfile + "\"");
		}
	}
}

/**
Parse command line arguments.
@param args Command line arguments.
*/
public static void parseArgs(String[] args)
throws Exception
{	
    // Allow setting of -home via system property "statemodgui.home". This
    // can be supplied by passing the -Dstatemodgui.home=HOME option to the java vm.
    // this little block of code copies the passed values into the front of the args array to
	// make sure that the install home can be considered by following parameters.
    if (System.getProperty("statemodgui.home") != null) {
        String[] extArgs = new String[args.length + 2];
        System.arraycopy(args, 0, extArgs, 2, args.length);
        extArgs[0] = "-home";
        extArgs[1] = System.getProperty("statemodgui.home");
        args = extArgs;
    }
    for (int i = 0; i < args.length; i++) {
		// List alphabetically...
		if ( args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("-help") ||
			args[i].equalsIgnoreCase("/h") || args[i].equals("/?") ) {
			printUsage ();
		}
		else if ( args[i].equalsIgnoreCase("-home")) {
			if ((i + 1)== args.length) {
				throw new Exception("No argument provided to '-home'");
			}
			i++;
			//__home = args[i];
			__home = (new File(args[i])).getCanonicalPath().toString();
			
			IOUtil.setProgramWorkingDir(__home);
			IOUtil.setApplicationHomeDir(__home);
			JGUIUtil.setLastFileDialogDirectory(__home);
		}
		else if (args[i].equals("-test")) {
			IOUtil.testing(true);
		}
		else if (args[i].equalsIgnoreCase("-v") || args[i].equalsIgnoreCase("--version")) {
			printVersion ();
		}
	}
}

/**
Parse the command-line arguments for the applet, determined from the applet data.
@param a JApplet for this application.
*/
public static void parseArgs(JApplet a)
throws Exception {
	String home = a.getParameter("-home");
	String test = a.getParameter("-test");

	if (home != null) {
		
		//__home = home;
		__home = (new File(home)).getCanonicalPath().toString();
		IOUtil.setProgramWorkingDir(__home);		
		IOUtil.setApplicationHomeDir(__home);	
		JGUIUtil.setLastFileDialogDirectory(__home);
	}

	if (test != null) {
		IOUtil.testing(true);
	}
}

/**
Print the program usage to the log file.
*/
public static void printUsage ( )
{	String nl = System.getProperty ( "line.separator" );
	String routine = "StateModGUI.printUsage";
	String usage =  nl +
"Usage:  " + PROGRAM_NAME + " [options]" + nl + nl +
"StateModGUI is the graphical user interface for the StateMod model." + nl+
"The following command line options are recognized:" + nl + nl +
"-home Dir            Specify the home directory for CDSS (e.g., \\CDSS)." +nl+
"                     The default is \"\\CDSS\"." + nl +
//"-d#[,#]            Sets the debug level.  The first number applies to screen"+nl+
//"                   output.  The second to the log file.  If only one value"+nl+
//"                   is given, it will be applied to screen and file output."+nl+
//"-help              Prints the program usage to the screen and log file." +nl+
//"-v                 Prints the program version."+nl+
//"-w#[,#]            Sets the warning level.  The first number applies to" +nl+
//"                   screen output.  The second to the log file.  If only one"+nl+
//"                   value is given, it will be applied to screen and file" +nl+
//"                   output."
		nl;
	System.out.println ( usage );
	Message.printStatus ( 1, routine, usage );
	quitProgram(0);
}

/**
Print the program version and exit the program.
*/
public static void printVersion ( )
{	String nl = System.getProperty ( "line.separator" );
	System.out.println (  nl + PROGRAM_NAME + " version: " + PROGRAM_VERSION + nl + nl +
	"StateMod GUI is a part of Colorado's Decision Support Systems (CDSS)\n" +
	"Copyright (C) 1997-2019 Colorado Department of Natural Resources\n" +
	"\n" +
	"StateMod GUI is free software:  you can redistribute it and/or modify\n" +
	"    it under the terms of the GNU General Public License as published by\n" +
	"    the Free Software Foundation, either version 3 of the License, or\n" +
	"    (at your option) any later version.\n" +
	"\n" +
	"StateMod GUI is distributed in the hope that it will be useful,\n" +
	"    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
	"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
	"    GNU General Public License for more details.\n" +
	"\n" +
	"You should have received a copy of the GNU General Public License\n" +
	"    along with StateMod GUI.  If not, see <https://www.gnu.org/licenses/>.\n" );
	quitProgram (0);
}

/**
Clean up and quit the program.
@param status Exit status.
*/
static void quitProgram ( int status )
{	String	routine = "StateModGUI.quitProgram";

	Message.printStatus ( 1, routine, "Exiting with status " + status + "." );

	System.out.print( "STOP " + status + "\n" );
	Message.closeLogFile();
	System.exit ( status ); 
}

/**
Read the configuration file, if available.
Code should call the getConfigurationPropValue() method to get a value from the property list.
*/
private static PropList readConfigurationFile ()
{	String routine = "StateModGUI.readConfigurationFile";
	
	// TODO SAM 2008-03-10 Change this to be user-specific when user resources are added.
	// Read the configuration file if available.
	String configfile = __home + File.separator + "system" + File.separator + "StateModGUI.cfg";
	PropList props = new PropList ( "StateModGUI" );
	if ( IOUtil.fileExists(configfile)) {
		Message.printStatus(2, routine, "Reading StateMod GUI configuration file \"" + configfile + "\"" );
		props.setPersistentName(configfile);
		try {
			props.readPersistent( true, // OK to append
				false ); // Don't read literals - don't want warnings about comments
		}
		catch ( Exception e ) {
			Message.printWarning(2, routine, "Error reading StateMod GUI configuration file \"" + configfile +
				"\".  Software features may be limited." );
			Message.printWarning(2, routine, e);
		}
		finally {
			// Set some properties in other objects...
			// Path to the StateMod executable
			String stateModPath = props.getValue ( "StateModExecutablePath" );
			if ( (stateModPath != null) && (stateModPath.length() > 0) ) {
				StateMod_Util.setStateModExecutable ( stateModPath );
				Message.printStatus(2,routine,"From configuration file, StateModExecutablePath=\"" +
					stateModPath + "\", expanded StateModExecutablePath=\"" +
					StateMod_Util.getStateModExecutable(IOUtil.getApplicationHomeDir(),null) );
			}
			else {
				Message.printStatus(2,routine,"No StateModExecutablePath set in configuration file, will run StateMod using \"" +
					StateMod_Util.getStateModExecutable(IOUtil.getApplicationHomeDir(),null) + "\"");
			}
			// Path the SmDelta executable
			String smdeltaPath = props.getValue ( "SmDeltaExecutablePath" );
			if ( (smdeltaPath != null) && (smdeltaPath.length() > 0) ) {
				StateMod_Util.setSmDeltaExecutable ( smdeltaPath );
				Message.printStatus(2,routine,"From configuration file, SmDeltaExecutablePath=\"" +
						smdeltaPath + "\", expanded SmDeltaExecutablePath=\"" + StateMod_Util.getSmDeltaExecutable() );
			}
			else {
				Message.printStatus(2,routine,"No SmDeltaExecutablePath set in configuration file, will run SmDelta using \"" +
					StateMod_Util.getSmDeltaExecutable() + "\"");
			}
		}
	}
	else {
		Message.printStatus(2, routine, "StateMod GUI configuration file does not exist:  \"" + configfile + "\"." +
			"\".  Software features may be limited." );
	}
	return props;
}

/**
Read the operating rules file, if available, which overrides whether operating rule types are explicitly
handled or are handled with a text editor.  This changes values in the 
*/
private static void readOperatingRulesFile ()
{	String routine = "StateModGUI.readConfigurationFile";
	
	// TODO SAM 2008-03-10 Change this to be user-specific when user resources are added.
	// Read the configuration file if available.
	String oprfile = __home + File.separator + "system" + File.separator + "OperatingRules.csv";
	if ( IOUtil.fileExists(oprfile)) {
		Message.printStatus(2, routine, "Reading operating rules configuration file \"" + oprfile + "\"" );
		try {
			StateMod_OperationalRight_Metadata.readGlobalData(oprfile);
		}
		catch ( IOException e ) {
			Message.printWarning(2, routine, "Error reading operating rules configuration file \"" + oprfile + "\"" );
			Message.printWarning(3, routine, e);
		}
		finally {
		}
	}
	else {
		Message.printStatus(2, routine, "Operating rules configuration file does not exist:  \"" + oprfile +
			"\" - using internal defaults for which rules are supported for editing." );
	}
}

/**
Set the icon used with the program.
*/
private static void setIcon ()
{	String routine = "StateModGUI.setIcon";
	// Set the icon used for the title bars
	
	String iconpath = "DWR/StateModGUI/StateModIcon32.gif";
	try {	
		JGUIUtil.setIconImage(iconpath);
	}
	catch ( Exception e ) {
		Message.printWarning(2, routine, "Icon could not be loaded from \"" + iconpath + "\"");
		Message.printWarning(2, routine, e);
	}
}

}

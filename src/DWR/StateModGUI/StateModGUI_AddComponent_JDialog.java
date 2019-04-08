//------------------------------------------------------------------------------
// StateMod_AddComponent_JDialog.java - dialog to add an object to a data set
//				component
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 
// 10 Mar 1998	Catherine E.		Created initial version of class.
//		Nutting-Lane, RTi
// 11 Mar 1998	CEN, RTi		Added baseflow add capabilities.
// 					Added oprits add capabilities.
// 01 Apr 2001	Steven A. Malers, RTi	Change GUI to GUIUtil.  Add finalize().
//					Remove import *.
// 2003-06-26	SAM, RTi		* Change Close button to Cancel.
//					* Emphasize river node.
//					* Add a check to make sure the new
//					  river node does not match an existing
//					  river node.
// ---------------------------------
// 2003-08-24	SAM, RTi		* Port from the 1.1.8 version from
//					  SMaddNodeWindow.
//					* Handle all data set components in the
//					  design.
// 2003-09-19	SAM, RTi		* Final push.  Use this to prototype
//					  add/delete also for use in the old
//					  GUI.  Enable in the following order:
//						Evaporation TS (Monthly)
//						Precipitation TS (Monthly)
//						Diversion
// 2003-10-10	SAM, RTi		React to comments after 2003-10-07
//					progress meeting:
//					* Default water right has .01 on the
//					  ID.
//					* Mix the instructions for adding with
//					  the components.
//					* Comment out the help button - use tool
//					  tips if necessary in the short term.
//					* Add delay table for diversions.
//					* Fix bug - returns should default to
//					  the next downstream node.
//					* Comment out the __INSERT_BEFORE -
//					  Ray Bennett said it is unnecessary.
//					* Enable more data components:
//						Reservoir
//						Stream Gage
//						Other node
//						Stream Estimate Station
//						Delay tables
//						Wells
//						Instream flow
// 2003-10-20	SAM, RTi		Corrections based on Ray Bennett
//					feedback.
//					* Add default reservoir account
//					  "Account1".
//					* Add default area/capacity for new
//					  reservoir.
//					* Set diversion owner name to diversion
//					  name.
//					* Add a default climate station
//					  assignment for a new reservoir.
//					* Support adding data to secondary
//					  time series files.
// 2003-10-22	SAM, RTi		* Support adding a new data set by
//					  focusing on the response file.
// 2004-06-29	J. Thomas Sapienza, RTi	Tied in add code for stations to
//					the network and the component data.
// 2007-02-18	SAM, RTi		Clean up code based on Eclipse feedback.
//------------------------------------------------------------------------------
// EndHeader

package DWR.StateModGUI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//import DWR.DMI.HydroBaseDMI.HydrologyNode;
import cdss.domain.hydrology.network.HydrologyNode;

import DWR.StateMod.StateMod_Data;
import DWR.StateMod.StateMod_DataSet;
import DWR.StateMod.StateMod_DataSet_JTree;
import DWR.StateMod.StateMod_DataSet_WindowManager;
import DWR.StateMod.StateMod_DelayTable;
import DWR.StateMod.StateMod_DelayTable_JFrame;
import DWR.StateMod.StateMod_Diversion;
import DWR.StateMod.StateMod_DiversionRight;
import DWR.StateMod.StateMod_Diversion_JFrame;
import DWR.StateMod.StateMod_GUIUtil;
import DWR.StateMod.StateMod_InstreamFlow;
import DWR.StateMod.StateMod_InstreamFlowRight;
import DWR.StateMod.StateMod_InstreamFlow_JFrame;
import DWR.StateMod.StateMod_Network_JFrame;
import DWR.StateMod.StateMod_OperationalRight;
import DWR.StateMod.StateMod_OperationalRight_JFrame;
import DWR.StateMod.StateMod_OperationalRight_Metadata;
import DWR.StateMod.StateMod_Reservoir;
import DWR.StateMod.StateMod_ReservoirAccount;
import DWR.StateMod.StateMod_ReservoirAreaCap;
import DWR.StateMod.StateMod_ReservoirClimate;
import DWR.StateMod.StateMod_ReservoirRight;
import DWR.StateMod.StateMod_Reservoir_JFrame;
import DWR.StateMod.StateMod_Response_JFrame;
import DWR.StateMod.StateMod_ReturnFlow;
import DWR.StateMod.StateMod_RiverNetworkNode;
import DWR.StateMod.StateMod_RiverNetworkNode_JFrame;
import DWR.StateMod.StateMod_StreamEstimate;
import DWR.StateMod.StateMod_StreamEstimate_Coefficients;
import DWR.StateMod.StateMod_StreamEstimate_JFrame;
import DWR.StateMod.StateMod_StreamGage;
import DWR.StateMod.StateMod_StreamGage_JFrame;
import DWR.StateMod.StateMod_Util;
import DWR.StateMod.StateMod_Well;
import DWR.StateMod.StateMod_WellRight;
import DWR.StateMod.StateMod_Well_JFrame;
import RTi.TS.DayTS;
import RTi.TS.MonthTS;
import RTi.TS.TS;
import RTi.TS.TSUtil;
import RTi.Util.GUI.JFileChooserFactory;
import RTi.Util.GUI.JGUIUtil;
import RTi.Util.GUI.ResponseJDialog;
import RTi.Util.GUI.SimpleJButton;
import RTi.Util.GUI.SimpleJComboBox;
import RTi.Util.IO.DataSetComponent;
import RTi.Util.IO.IOUtil;
import RTi.Util.Message.Message;
import RTi.Util.String.StringUtil;
import RTi.Util.Time.DateTime;
import RTi.Util.Time.TimeUtil;
import RTi.Util.Time.YearType;

/**
This class adds objects to data set components.
*/
public class StateModGUI_AddComponent_JDialog extends JDialog 
implements ActionListener, ItemListener
{

/**
Indicates new stream in network
*/
private final String __NONE = "None";

/**
Indicates that a well structure does not have a relationship to a diversion.
*/
private final String __WELL_ONLY = "WELL ONLY";

/**
Calling application - needed so that a new data set can be set.
*/
private StateModGUI_JFrame __parent;
/**
Data set component type to add.
*/
private int __comp_type;
/**
Used with stream estimate stations to indicate whether a new stream estimate node should be created
(true) or an existing node converted to a stream estimate node (false).
*/
private boolean __newnode = true;
/**
Data set that is being edited.
*/
private StateMod_DataSet __dataset = null;
/**
Data set window manager.
*/
private StateMod_DataSet_WindowManager __dataset_wm = null;
/**
Data set tree, to refresh when data objects are added/removed.
*/
private StateMod_DataSet_JTree __dataset_JTree = null;

//private JTextField __new_rnode_id_JTextField;
					// River node - OK from Ray Bennett
					// 2003-08-25 to internally assign the
					// same as the other ID.

/**
New data set directory, used when creating a new data set.
*/
private JTextField __dataset_dir_JTextField;
/**
Name for new node/object.
*/
private JTextField __new_name_JTextField;
/**
ID for new node/object
*/
private JTextField __new_id_JTextField;
/**
Initial value used with a time series.
*/
private JTextField __initial_tsvalue1_JTextField[];
/**
Initial value used with a second time series.
*/
private JTextField __initial_tsvalue2_JTextField[];
/**
Initial value used with a third time series.
*/
private JTextField __initial_tsvalue3_JTextField[];
/**
Initial value for a free-format parameter.
*/
private JTextField __initial_value1_JTextField;
/**
Initial value for a free-format parameter.
*/
private JTextField __initial_value2_JTextField;
/**
Delay tables for diversions, wells.
*/
private SimpleJComboBox __delay_table1_JComboBox;
/**
Delay tables for well depletions.
*/
private SimpleJComboBox __delay_table2_JComboBox;
/**
Used as needed (e.g., time series).
*/
private SimpleJComboBox __units_JComboBox;
/**
Used when creating a new data set.
*/
private SimpleJComboBox __year_type_JComboBox;
/**
Used to list primary data objects so that secondary data (e.g., time series) can be defined.
*/
private SimpleJComboBox __primary_data_JComboBox;
/**
Used when defining time series.
*/
private JTextField __year1_JTextField = null;
/**
Used when defining time series.
*/
private JTextField __year2_JTextField = null;

// Operational rights type choices.
//private TextField _newComments;	// OK from Ray Bennett on 2003-08-25 to ignore.

private SimpleJComboBox __new_oprights_type_JComboBox;
/**
List of nodes downstream of the new node.  Also used for selecting a node
for conversion to a stream estimate station.
*/
private SimpleJComboBox __downstream_nodes_JComboBox;
/**
List of nodes upstream of the new node.
*/
private SimpleJComboBox __upstream_nodes_JComboBox = null;
/**
Position to insert the data.
*/
private SimpleJComboBox __insertpos_JComboBox;

private JButton __add_JButton;
private JButton __help_JButton;
private JButton __cancel_JButton;
/**
Used when creating a new data set.
*/
private JButton __browse_JButton;

private final String __INSERT_FIRST = "First in List";
private final String __INSERT_LAST = "Last in List";
private final String __INSERT_ALPHABETICAL_ID = "Alphabetically by ID";
private final String __INSERT_BEFORE = "Before";

/**
Constructor.
@param parent Parent JFrame, used to manage the dialog window position.
@param comp_type the StateMod data set component to add.  The component should
be specified as the primary component in a group, not the group itself.
@param dataset The StateMod data set to edit.
@param dataset_wm The window manager for the data set.
@param dataset_JTree The data set JTree.  If non-null, its contents will be
refreshed when data objects are added.
*/
public StateModGUI_AddComponent_JDialog ( StateModGUI_JFrame parent, int comp_type,
	StateMod_DataSet dataset, StateMod_DataSet_WindowManager dataset_wm,
	StateMod_DataSet_JTree dataset_JTree )
{	this ( parent, comp_type, dataset, dataset_wm, dataset_JTree, true );
}

public StateModGUI_AddComponent_JDialog ( StateModGUI_JFrame parent, int comp_type,
	StateMod_DataSet dataset, StateMod_DataSet_WindowManager dataset_wm,
	StateMod_DataSet_JTree dataset_JTree, StateMod_Network_JFrame network)
{	this ( parent, comp_type, dataset, dataset_wm, dataset_JTree, true, network);
}

public StateModGUI_AddComponent_JDialog ( StateModGUI_JFrame parent, int comp_type,
	StateMod_DataSet dataset, StateMod_DataSet_WindowManager dataset_wm,
    StateMod_DataSet_JTree dataset_JTree, boolean newnode )
{
	this (parent, comp_type, dataset, dataset_wm, dataset_JTree, newnode, null);
}
/**
Constructor.
@param parent Parent JFrame, used to manage the dialog window position.
@param comp_type the StateMod data set component to add.  The components
typically correspond to the primary component in a group, not the group itself.
@param dataset The StateMod data set to edit.
@param dataset_wm The window manager for the data set.
@param dataset_JTree The data set JTree.  If non-null, its contents will be
refreshed when data objects are added.
@param newnode Used with stream estimate stations to indicate when a new node
should be created (true) or existing nodes should be converted (false).
*/
public StateModGUI_AddComponent_JDialog ( StateModGUI_JFrame parent, int comp_type,
	StateMod_DataSet dataset, StateMod_DataSet_WindowManager dataset_wm,
	StateMod_DataSet_JTree dataset_JTree, boolean newnode, StateMod_Network_JFrame network)
{	super ( parent, true ); // Modal
	String routine = "StateMod_AddComponent_JDialog";
	__parent = parent;
	__comp_type = comp_type;
	__dataset = dataset;
	__dataset_wm = dataset_wm;
	__dataset_JTree = dataset_JTree;
	__newnode = newnode;
	__network = network;

	try {
	GridBagConstraints gbc = new GridBagConstraints();
	GridBagLayout gb = new GridBagLayout();
	JPanel main_JPanel = new JPanel();
	main_JPanel.setLayout ( gb );

	int y = 0;

	// List the GUI layout in the order of data set components in the main
	// GUI.  When adding a node that will be in the river network, the
	// network node choices are displayed.

	if ( comp_type == StateMod_DataSet.COMP_RESPONSE ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "New StateMod Data Set", null );
		String [] notes1 = {
			"A StateMod data set is defined by a response file, which contains a list of files in the data set.",
			"A control file is also required and contains important parameters for the data set.",
			"Control data indicates whether files in the response file are needed for a StateMod run.",
			"To define a new StateMod data set:",
			"1. Enter a base name for the data set, which will be used for initial file names.",
			"   See the StateMod documentation for guidelines on names." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		y = setupInitialValue ( main_JPanel, y, gbc, 1, "", "Base name:" );
		String [] notes2 = {
			"2. Select/create the directory for the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDataSetDirectory ( main_JPanel, y, gbc, "Data set directory:" );
		String [] notes3 = {
			"3. Indicate the year type and period for the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupYearType ( main_JPanel, y, gbc );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		String [] notes4 = {
			"4. Press OK below to confirm creating the new data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_STREAMGAGE_STATIONS ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Stream Gage Station", null );
		String [] notes1 = {
			"Stream gage stations are on-channel point features " +
			"with historical flows and are defined as follows:",
			"1. Determine from the map or other reference the " +
			"location of the new node and its position relative to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"stream gage (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node downstream from new stream gage:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new stream gage.",
			"   If the stream gage is on a new branch, select " + __NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc, "River node upstream from new stream gage:" );
		String [] notes4 = {
			"4. Enter a stream gage identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   The river node identifier and name in the " +
			"network will be set to the same as the stream gage." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New stream gage station identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New stream gage station name:" );
		String [] notes5 = {
			"5. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Historical flow (ACFT, Jan to Dec):" );
		String [] notes6 = {
			"6. Indicate where to add the stream gage in data " +
			"files (the network file is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes6 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes7 = {
			"7. Press Add below to confirm adding the stream gage to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes7 );
	}
	else if((comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Stream Gage Historical Time Series (Monthly)", null );
		}
		else {	StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Stream Gage Historical Time Series (Daily)", null );
		}
		String [] notes1 = {
			"Stream gage historical time series are used to " +
			"estimate base (naturalized) flows and for model calibration.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the stream gage station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the stream gage that will use the time " +
			"series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Stream gage:",
			StateMod_DataSet.COMP_STREAMGAGE_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1,
			"Historical flow (ACFT, Jan to Dec):" );
		}
		else {	String [] notes3 = {
			"3. Enter default daily time series data (values will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Historical flow (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the stream gage "+
			"historical flow time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Monthly Delay Table", null );
		String [] notes1 = {
			"Delay tables are used by diversions and wells to " +
			"indicate return flows, and well depletions.",
			"A delay table may be used by multiple diversions/wells in a data set.",
			"Default delay tables may be defined (e.g., return 100% of flow in current month).",
			"External software (e.g., GIS) is also often used to define delay tables.",
			"To define a delay table:",
			"1. Enter an integer delay table identifier.",
			"   The identifier should be unique in the list of delay tables and have up to 8 digits.",
			"   In general, if daily modeling is also occurring, " +
			"the monthly and daily delay table identifiers should be consistent." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		y = setupID ( main_JPanel, y, gbc, "New delay table identifier:" );
		String [] notes2 = {
			"2. Enter the number of months for the delay table." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Number of months:" );
		String [] notes3 = {
			"3. Press Add below to confirm adding the monthly delay table to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_DELAY_TABLES_DAILY ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Daily Delay Table", null );
		String [] notes1 = {
			"Delay tables are used by diversions and wells to " +
			"indicate return flows, and well depletions.",
			"A delay table may be used by multiple diversions/wells in a data set.",
			"Default delay tables may be defined (e.g., return 100% of flow in current day).",
			"External software (e.g., GIS) is also often used to define delay tables.",
			"To define a delay table:",
			"1. Enter an integer delay table identifier.",
			"   The identifier should be unique in the list of delay tables and have up to 8 digits.",
			"   In general, the daily and monthly delay table identifiers should be consistent." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		y = setupID ( main_JPanel, y, gbc, "New delay table identifier:" );
		String [] notes2 = {
			"2. Enter the number of days for the delay table." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Number of days:" );
		String [] notes3 = {
			"3. Press Add below to confirm adding the daily delay table to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_DIVERSION_STATIONS ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Diversion Station", null );
		String [] notes1 = {
			"Diversion stations are on-channel point features and are defined as follows:",
			"1. Determine from the map or other reference the " +
			"location of the new node and its position relative to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"diversion (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node downstream from new diversion:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new diversion.",
			"   If the diversion is on a new branch, select " +__NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc, "River node upstream from new diversion:" );
		String [] notes4 = {
			"4. Enter a diversion identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   The river node identifier and name in the " +
			"network will be set to the same as the diversion." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New diversion station identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New diversion station name:" );
		String [] notes5 = {
			"5. Enter default monthly time series data (values will be repeated and can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Historical diversions (ACFT, Jan to Dec):" );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 2, "Demands (ACFT, Jan to Dec):" );
		String [] notes6 = {
			"6. Select a default delay table for return flows.  Returns will go to the downstream node." };
		y = setupNotes ( main_JPanel, y, gbc, notes6 );
		y = setupDelayTable ( main_JPanel, y, gbc, 1, "Delay Table:" );
		String [] notes7 = {
			"7. Indicate where to add the diversion in data " +
			"files (the network file is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes7 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes8 = {
			"8. Press Add below to confirm adding the diversion to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes8 );
	}
	else if((comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_DIVERSION_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion Historical Time Series (Monthly)", null);
		}
		else {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion Historical Time Series (Daily)", null );
		}
		String [] notes1 = {
			"Diversion historical time series are used to " +
			"estimate base (naturalized) flows and for model calibration.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the diversion station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the diversion that will use the time series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Diversion:",
			StateMod_DataSet.COMP_DIVERSION_STATIONS,
			StateMod_DataSet.COMP_UNKNOWN );
		if ( comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1,
			"Historical diversion (ACFT, Jan to Dec):" );
		}
		else {
			String [] notes3 = {
			"3. Enter default daily time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1",
			"Historical diversion (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the diversion "+
			"historical time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if((comp_type == StateMod_DataSet.COMP_DEMAND_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_DEMAND_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion Demand Time Series (Monthly)", null);
		}
		else if(comp_type == StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion Demand Override Time Series (Monthly)", null);
		}
		else if(comp_type == StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion Demand Time Series (Average Monthly)", null);
		}
		else {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion Demand Time Series (Daily)", null );
		}
		String [] notes1 = {
			"Diversion demand time series are used when simulating.",
			"Demand values are at the headgate and should " +
			"therefore reflect conveyance/system losses that will occur.",
			"See the consumptive water requirement time series for requirements on the farm/site.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the diversion station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the diversion that will use the time series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Diversion:",
			StateMod_DataSet.COMP_DIVERSION_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( (comp_type == StateMod_DataSet.COMP_DEMAND_TS_MONTHLY ) ||
			(comp_type == StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY) ||
			(comp_type == StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY) ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			if ( comp_type != StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY ) {
				y = setupYear1Year2 ( main_JPanel, y, gbc );
			}
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Demand (ACFT, Jan to Dec):" );
		}
		else {	String [] notes3 = {
			"3. Enter default daily time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Demand (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the diversion demand time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if((comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion/Well Consumptive Water Requirement Time Series (Monthly)", null);
		}
		else if(comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Diversion/Well Consumptive Water Requirement Time Series (Daily)",
			null);
		}
		String [] notes1 = {
			"Consumptive water requirement time series are used " +
			"when simulating using variable efficiencies.",
			"Requirement values are at the farm/site and therefore"+
			" do not reflect conveyance/system losses that will occur.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the diversion/well station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the diversion/well that will use the time series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Diversion or Well:",
			StateMod_DataSet.COMP_DIVERSION_STATIONS, StateMod_DataSet.COMP_WELL_STATIONS );
		if ( (comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY) ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Requirement (ACFT, Jan to Dec):" );
		}
		else {	String [] notes3 = {
			"3. Enter default daily time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Requirement (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the consumptive "+
			"water requirement time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY ){
		StateMod_GUIUtil.setTitle ( this, __dataset,
		"Add Precipitation Time Series (Monthly)", null );
		String [] notes = {
			"Precipitation time series (monthly) are referenced by reservoirs.",
			"Each time series should have a location identifier (i.e., station identifier).",
			"Evaporation time series should be pan evaporation " +
			"when used with precipitation time series",
			"or should contain net evaporation when used without " +
			"a corresponding precipitation time series.",
			"Precipitation time series may be used by more than one reservoir.",
			"After adding the precipitation time series, edit the data in the time series viewer." };
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "New precipitation station ID:" );
		y = setupName ( main_JPanel, y, gbc, "New precipitation station name:" );
		String [] notes2 = {
			"Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupDataUnits ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Initial values (Jan to Dec):" );
		y = setupAddPosition ( main_JPanel, y, gbc );
	}
	else if ( comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Evaporation Time Series (Monthly)", null );
		String [] notes = {
			"Evaporation time series (monthly) are referenced by reservoirs.",
			"Each time series should have a location identifier (i.e., station identifier).",
			"Evaporation time series should be pan evaporation when used with precipitation time series",
			"or should contain net evaporation when used without a corresponding precipitation time series.",
			"Evaporation time series may be used by more than one reservoir.",
			"After adding the evaporation time series, edit the data in the time series viewer." };
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "New evaporation station ID:" );
		y = setupName ( main_JPanel, y, gbc, "New evaporation station name:" );
		String [] notes2 = {
			"Enter default monthly time series data (values will be repeated and can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupDataUnits ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Initial values (Jan to Dec):" );
		y = setupAddPosition ( main_JPanel, y, gbc );
	}
	else if ( comp_type == StateMod_DataSet.COMP_RESERVOIR_STATIONS ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Reservoir Station", null );
		String [] notes1 = {
			"Reservoir stations are point features and are defined as follows:",
			"1. Determine from the map or other reference the location of the new node and its position relative " +
			"to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"reservoir (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node downstream from new reservoir:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new reservoir.",
			"   If the reservoir is on a new branch, select " +__NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc, "River node upstream from new reservoir:" );
		String [] notes4 = {
			"4. Enter a reservoir identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   The river node identifier and name in the network will be set to the same as the reservoir." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New reservoir station identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New reservoir station name:" );
		String [] notes6 = {
			"5. Enter the reservoir capacity." };
		y = setupNotes ( main_JPanel, y, gbc, notes6 );
		y = setupInitialValue ( main_JPanel, y, gbc, 1, "0", "Reservoir capacity (ACFT):" );
		String [] notes5 = {
			"6. Enter default monthly time series data (values will be repeated and can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "End of month content (ACFT, Jan to Dec):" );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 3, "Target minimum (ACFT, Jan to Dec):" );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 2, "Target maximum (ACFT, Jan to Dec):" );
		String [] notes7 = {
			"7. Indicate where to add the reservoir in data " +
			"files (the network file is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes7 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes8 = {
			"8. Press Add below to confirm adding the reservoir to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes8 );
	}
	else if((comp_type ==StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY) ||
		(comp_type ==StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset, "Add Reservoir Content Time Series Series (Monthly)",
			null);
		}
		else if(comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Reservoir Content Time Series (Daily)", null);
		}
		String [] notes1 = {
			"Reservoir historical content time series are used to "+
			"estimate base (naturalized) flows and for model calibration.",
			"Content values are specified at the end of the data interval.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the reservoir station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the reservoir that will use the time series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Reservoir:",
			StateMod_DataSet.COMP_RESERVOIR_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "End of month content (ACFT, Jan to Dec):" );
		}
		else {	String [] notes3 = {
			"3. Enter default daily time series data (values will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "End of day content (ACFT):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the reservoir content time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if((comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset, "Add Reservoir Target Time Series Series (Monthly)",
			null);
		}
		else if(comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Reservoir Target Time Series (Daily)", null);
		}
		String [] notes1 = {
			"Reservoir target time series are used when simulating.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the reservoir station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the reservoir that will use the time " +
			"series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Reservoir:",
			StateMod_DataSet.COMP_RESERVOIR_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1,
			"Target, Minimum (ACFT, Jan to Dec):" );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 2,
			"Target, Maximum (ACFT, Jan to Dec):" );
		}
		else {
			String [] notes3 = {
			"3. Enter default daily time series data (values will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Target, Minimum (ACFT):" );
			y = setupInitialValue ( main_JPanel, y, gbc, 2, "1", "Target, Maximum (ACFT):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the reservoir target time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_INSTREAM_STATIONS ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Instream Flow Station", null );
		String [] notes1 = {
			"Instream flow stations are on-channel point features"+
			" consistent with StateMod's node-based network.",
			"However, StateMod can model instream flow reaches " +
			"by using upstream and downstream terminii nodes.",
			"Typically this is done by adding an \"other node\" as"+
			" the downstream node and defining a upstream node",
			"as an instream flow station with water rights, etc.",
			"To define an instream flow station:",
			"1. Determine from the map or other reference the " +
			"location of the new node and its position relative to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"instream flow station (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc,
			"River node downstream from new instream flow station:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new instream flow station.",
			"   If the instream flow station is on a new branch, select " +__NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc,
			"River node upstream from new instream flow station:" );
		String [] notes4 = {
			"4. Enter an instream flow station identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   The river node identifier and name in the " +
			"network will be set to the same as the instream flow station." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New instream flow station identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New instream flow station name:" );
		String [] notes5 = {
			"5. Enter monthly time series data (values can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1,
			"Average monthly demands (ACFT, Jan to Dec):" );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 2,
			"Monthly demands (ACFT, Jan to Dec):" );
		String [] notes6 = {
			"6. Indicate where to add the instream flow station " +
			"in data files (the network file is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes6 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes7 = {
			"7. Press Add below to confirm adding the instream flow station to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes7 );
	}
	else if((comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Instream Flow Demand Time Series (Monthly)", null);
		}
		else if(comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Instream Flow Demand Time Series (Average Monthly)", null);
		}
		else {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Instream Flow Demand Time Series (Daily)", null );
		}
		String [] notes1 = {
			"Instream, non-consumptive, demand time series are used when simulating.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the instream flow station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the instream flow station that will use " +
			"the time series (typically the upstream terminus)." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Instream flow:",
			StateMod_DataSet.COMP_INSTREAM_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( (comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY ) ||
			(comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY) ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			if ( comp_type != StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY ) {
				y = setupYear1Year2 ( main_JPanel, y, gbc );
			}
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Demand (ACFT, Jan to Dec):" );
		}
		else {
			String [] notes3 = {
			"3. Enter default daily time series data (values will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Demand (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the instream flow demand time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_WELL_STATIONS ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Well Station", null );
		String [] notes1 = {
			"Well stations are point features that may or may not be connected to the main channel.",
			"A well can be \"well only\" (WEL) if it is the only " +
			"supply for land or D&W if a well supplies to lands ",
			"that also have a diversion (surface water) supply.",
			"Wells are defined as follows:",
			"1. Determine from the map or other reference the " +
			"location of the new node and its position relative to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"well (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node downstream from new well:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new well.",
			"   If the well is on a new branch, select " +__NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc, "River node upstream from new well:" );
		String [] notes4 = {
			"4. Enter a well identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   The river node identifier and name in the " +
			"network will be set to the same as the well." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New well station identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New well station name:" );
		String [] notes5 = {
			"5. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
		y = setupYear1Year2 ( main_JPanel, y, gbc );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Historical pumping (ACFT, Jan to Dec):" );
		y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 2, "Demands (ACFT, Jan to Dec):" );
		String [] notes6 = {
			"6. Select a default delay table for return flows.  Returns will go to the downstream node." };
		y = setupNotes ( main_JPanel, y, gbc, notes6 );
		y = setupDelayTable ( main_JPanel, y, gbc, 1, "Delay Table (returns):" );
		String [] notes7 = {
			"7. Select a default delay table for river depletions."+
			"  Depletions will reduce the flow at the downstream node." };
		y = setupNotes ( main_JPanel, y, gbc, notes7 );
		y = setupDelayTable ( main_JPanel, y, gbc, 2, "Delay Table (depletions):" );
		String [] notes8 = {
			"8. Indicate where to add the well in data " +
			"files (the network file is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes8 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes9 = {
			"9. Press Add below to confirm adding the well to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes9 );
	}
	else if((comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Well Historical Pumping Time Series (Monthly)",
			null);
		}
		else {
			StateMod_GUIUtil.setTitle ( this, __dataset,
			"Add Well Historical Pumping Time Series (Daily)",null);
		}
		String [] notes1 = {
			"Well historical pumping time series are used to " +
			"estimate base (naturalized) flows and for model calibration.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the well station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the well that will use the time series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Well:",
			StateMod_DataSet.COMP_WELL_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1,
			"Historical well pumping (ACFT, Jan to Dec):" );
		}
		else {
			String [] notes3 = {
			"3. Enter default daily time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Historical well pumping (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the well "+
			"historical pumping time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if((comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY) ) {
		if ( comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY ) {
			StateMod_GUIUtil.setTitle ( this, __dataset, "Add Well Demand Time Series (Monthly)", null);
		}
		else {
			StateMod_GUIUtil.setTitle ( this, __dataset, "Add Well Demand Time Series (Daily)", null );
		}
		String [] notes1 = {
			"Well demand time series are used when simulating.",
			"Demand values are at the well and should " +
			"therefore reflect conveyance/system losses that will occur.",
			"See the consumptive water requirement time series for requirements on the farm/site.",
			"The time series should not contain missing data.",
			"Default values can be assigned here and edited later.",
			"To define the time series:",
			"1. Make sure the well station is already defined." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the well that will use the time series." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupPrimaryDataList ( main_JPanel, y, gbc, "Well:",
			StateMod_DataSet.COMP_WELL_STATIONS, StateMod_DataSet.COMP_UNKNOWN );
		if ( (comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY ) ) {
			String [] notes3 = {
			"3. Enter default monthly time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialTSValues ( main_JPanel, y, gb, gbc, 1, "Demand (ACFT, Jan to Dec):" );
		}
		else {
			String [] notes3 = {
			"3. Enter default daily time series data (values "+
			"will be repeated and can be edited later)." };
			y = setupNotes ( main_JPanel, y, gbc, notes3 );
			y = setupYear1Year2 ( main_JPanel, y, gbc );
			y = setupInitialValue ( main_JPanel, y, gbc, 1, "1", "Demand (CFS):" );
		}
		String [] notes4 = {
			"4. Indicate where to add the time series in the time series file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"5. Press Add below to confirm adding the well "+
			"demand time series to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if ( (comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS)&& (__newnode == true) ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Stream Estimate Station", null );
		String [] notes1 = {
			"Stream estimate stations are on-channel point " +
			"features with estimated base (naturalized) flows.",
			"Although other nodes can be converted to stream " +
			"estimate stations, you have chosen to create a new node.",
			"The new node will be listed in the network and the stream estimate files only.",
			"Flows are estimated by distributing the flows from " +
			"stream gages with historical flow data.",
			"To define a new stream estimate node:",
			"1. Determine from the map or other reference the " +
			"location of the new node and its position relative to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"stream gage (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node downstream from new stream gage:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new stream estimate station.",
			"   If the stream gage is on a new branch, select " +__NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc,
			"River node upstream from new stream estimate station:" );
		String [] notes4 = {
			"4. Enter a stream estimate station identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   The river node identifier and name in the " +
			"network will be set to the same as the stream estimate station." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New stream estimate station identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New stream estimate station name:" );
		String [] notes5 = {
			"5. Indicate where to add the stream estimate station "+
			"in data files (the network file is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes6 = {
			"6. Press Add below to confirm adding the stream estimate station to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes6 );
	}
	else if ( (comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS)&&(__newnode == false) ) {
		// Convert an existing node to a stream estimate node...
		StateMod_GUIUtil.setTitle ( this, __dataset, "Convert Node to Stream Estimate Station", null );
		String [] notes1 = {
			"Stream estimate stations are on-channel point " +
			"features with estimated base (naturalized) flows.",
			"You have chosen to convert an existing node to a " +
			"stream estimate node, which will only add ",
			"stream estimate coefficient data for the node (" +
			"other existing data will remain unchanged).",
			"Flows are estimated by distributing the flows from " +
			"stream gages with historical flow data.",
			"To convert an existing node to a stream estimate node:",
			"1. Determine from the map or other reference the " +
			"location of the node to be converted to a stream estimate node." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node to convert to a stream " +
			"estimate node (see below, listed top to bottom of network)." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node to convert to stream estimate node:" );
		String [] notes3 = {
			"3. Indicate where to add the stream estimate station "+
			"in the coefficients file (the network file",
			"   is always upstream to downstream)." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes4 = {
			"4. Press Convert below to confirm converting the " +
			"selected node to a stream estimate station." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_OTHER_NODE ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Other Node", null );
		String [] notes1 = {
			"Other nodes are point features, with no additional " +
			"input data other than the network node.",
			"These nodes should not be confused with streamflow estimate stations.",
			"For example, a node may be inserted to represent the "+
			"confluence of a river, where a gage does not exist.",
			"Another use is to represent a possible future diversion or reservoir.",
			"Another example is to represent the downstream " +
			"terminus for an instream flow reach - ",
			"in this case, the downstream terminus is defined " +
			"first and is referenced when defining the upstream "+
			"terminus as an instream flow station.",
			"Define a new node as follows:",
			"1. Determine from the map or other reference the " +
			"location of the new node and its position relative to other nodes." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Select the river node downstream from the new " +
			"node (see below, listed top to bottom of network).",
			"   This will allow the upstream nodes to be " +
			"determined.  Select " + __NONE + " for a new separate stream reach." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupDownstreamNodes ( main_JPanel, y, gbc, "River node downstream from new stream gage:" );
		String [] notes3 = {
			"3. Select the river node upstream from the new node.",
			"   If the node is on a new branch, select " +__NONE + "." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupUpstreamNodes ( main_JPanel, y, gbc,
			"River node upstream from new stream gage:" );
		String [] notes4 = {
			"4. Enter the identifier and name.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   The name should be <= 24 characters.\n",
			"   This information appears only in the river network file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupID ( main_JPanel, y, gbc, "New node identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New node name:" );
		String [] notes5 = {
			"5. Press Add below to confirm adding the node to the data set." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}
	else if ( comp_type == StateMod_DataSet.COMP_OPERATION_RIGHTS ) {
		StateMod_GUIUtil.setTitle ( this, __dataset, "Add Operational Right", null );
		String [] notes1 = {
			"Operational rights define operational relationships between data set features.",
			"For example, operational rights define how water is " +
			"released from a reservoir to supply a diversion.",
			"Operational rights should be defined with a good " +
			"understanding of how a system is operated.",
			"Various operational right types describe different " +
			"behavior - the controlling data are defined with each right.",
			"Define a new operational right as follows:",
			"1. Determine how the actual system is operated and" +
			" identify real-world features in the StateMod data set.",
			"  In particular, identify how the structures/stations interact." };
		y = setupNotes ( main_JPanel, y, gbc, notes1 );
		String [] notes2 = {
			"2. Enter the operational right identifier.",
			"   The identifier should be unique in the data " +
			"set, <= 12 characters and not contain space or dash characters.",
			"   Typically, the first part of the identifier " +
			"matches the water source identifier,",
			"   followed by a \".NN\" counter, similar to other water rights." };
		y = setupNotes ( main_JPanel, y, gbc, notes2 );
		y = setupID ( main_JPanel, y, gbc, "New operational right identifier:" );
		y = setupName ( main_JPanel, y, gbc, "New operational right name:" );
		String [] notes3 = {
			"3. Select the operational right type." };
		y = setupNotes ( main_JPanel, y, gbc, notes3 );
		y = setupOperationalRights ( main_JPanel, y, gbc, "Operational right type:" );
		String [] notes4 = {
			"3. Indicate where to add the operational right in the operational rights file." };
		y = setupNotes ( main_JPanel, y, gbc, notes4 );
		y = setupAddPosition ( main_JPanel, y, gbc );
		String [] notes5 = {
			"4. Press Add below to confirm adding the selected operational right." };
		y = setupNotes ( main_JPanel, y, gbc, notes5 );
	}

	getContentPane().add ( "Center", main_JPanel );

	// Add buttons...

	JPanel button_JPanel = new JPanel();
	button_JPanel.setLayout ( new FlowLayout ( FlowLayout.CENTER ));

	if ( comp_type == StateMod_DataSet.COMP_RESPONSE ) {
		__add_JButton = new SimpleJButton("OK", this);
	}
	else if((comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS)&& (__newnode == false) ) {
		// The one case where the button should say "Convert" and not "Add"...
		__add_JButton = new SimpleJButton("Convert", this);
	}
	else {
		__add_JButton = new SimpleJButton("Add", this);
	}
	button_JPanel.add ( __add_JButton );
	__help_JButton = new SimpleJButton("Help",this);
	__help_JButton.setEnabled ( false );
	// REVISIT - add later.
	//button_JPanel.add ( __help_JButton );
	__cancel_JButton = new SimpleJButton("Cancel",this);
	button_JPanel.add ( __cancel_JButton );

	getContentPane().add( "South", button_JPanel );

	} catch ( Exception e ) {
		Message.printWarning ( 3, routine, e );
	}
	// Make sure the upstream node list agrees with the selected downstream node...
	refreshUpstreamNodes();
	pack();
	JGUIUtil.center(this);	
	setVisible(true);
}

/**
Handle action events.
@param e ActionEvent to handle.
*/
public void actionPerformed ( ActionEvent e )
{	Object o = e.getSource();

	if ( o == __add_JButton ) {
		// List in the order of data set components in the main GUI...
		if ( __comp_type == StateMod_DataSet.COMP_RESPONSE ) {
			newDataSet();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_STREAMGAGE_STATIONS ){
			addStreamGage();
		}
		else if ((__comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY) ) {
			addTimeSeries ();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY ) {
			addDelayTable ( true );
		}
		else if ( __comp_type == StateMod_DataSet.COMP_DELAY_TABLES_DAILY ) {
			addDelayTable ( false );
		}
		else if ( __comp_type == StateMod_DataSet.COMP_DIVERSION_STATIONS ) {
			addDiversion();
		}
		else if ((__comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_DIVERSION_TS_DAILY) ||
			(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY)||
			(__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY) ) {
			addTimeSeries ();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY ) {
			addPrecipitationTS();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY ) {
			addEvaporationTS();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_RESERVOIR_STATIONS ) {
			addReservoir();
		}
		else if((__comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY) ||
			(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
			addTimeSeries ();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_INSTREAM_STATIONS ) {
			addInstreamFlow();
		}
		else if((__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY) ) {
			addTimeSeries ();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_WELL_STATIONS ) {
			addWell();
		}
		else if((__comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY) ||
			(__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY) ) {
			addTimeSeries ();
		}
		// Well irrigation practice and consumptive water requirement
		// time series are handled under diversions above.
		else if ( __comp_type == StateMod_DataSet.COMP_OTHER_NODE ) {
			addOtherNode();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS ) {
			if ( __newnode ) {
				addStreamEstimate();
			}
			else {
				convertStreamEstimate();
			}
		}
		else if(__comp_type == StateMod_DataSet.COMP_OPERATION_RIGHTS) {
			addOperationalRight();
		}
	}
	else if ( o == __browse_JButton ) {
		browse ();
	}
	else if ( o == __cancel_JButton ) {
		dispose ();
	}
}

/**
Add a delay table data set.
@param monthly_data If true, then a monthly delay table will be added.  If
false, a daily delay table will be added.
*/
private void addDelayTable ( boolean monthly_data )
{	String routine = "StateModGUI_AddComponent_JDialog.addDelayTable";
	String new_id = __new_id_JTextField.getText().trim();
	String value1 = __initial_value1_JTextField.getText().trim();

	// Verify data...
	String warning = "";
	if ( new_id.length() == 0 ) {
		warning +="\nThe new delay table identifier must be specified.";
	}
	if ( new_id.length() > 8 ) {
		warning += "\nThe new delay table identifier is > 8 digits.";
	}
	if ( !StringUtil.isInteger(new_id) ) {
		warning += "\nThe delay table identifier must be an integer.";
	}
	if ( !StringUtil.isInteger(value1) ) {
		if ( monthly_data ) {
			warning += "\nThe number of months must be an integer.";
		}
		else {
			warning += "\nThe number of days must be an integer.";
		}
	}
	else if ( StringUtil.atoi(value1) < 0 ) {
		if ( monthly_data ) {
			warning += "\nThe number of months must be >= 0.";
		}
		else {
			warning += "\nThe number of days must be >= 0.";
		}
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe delay table identifier is already in use in the delay tables.";
	}

	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a delay table...

	StateMod_DelayTable delay = new StateMod_DelayTable ( monthly_data );
	delay.setID ( new_id );
	int ndelays = StringUtil.atoi ( value1 );
	if ( ndelays > 0 ) {
		double value = 100.0/StringUtil.atod(value1);
		for ( int i = 0; i < ndelays; i++ ) {
			delay.addRet_val ( value );
		}
	}

	DataSetComponent comp = null;
	if ( monthly_data ) {
		comp = __dataset.getComponentForComponentType (	StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY );
	}
	else {
		comp = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_DELAY_TABLES_DAILY );
	}

	List delaydata_Vector = (List)comp.getData();
	// For now hard-code the following since there is not really a reason
	// for users to want other than sorted order...
	String insertpos = __INSERT_ALPHABETICAL_ID;
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( delaydata_Vector.size() == 0 ) {
			delaydata_Vector.add ( delay );
		}
		else {
			delaydata_Vector.add ( 0, delay );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		delaydata_Vector.add ( delay );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = delaydata_Vector.size();
		StateMod_DelayTable delay2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			delay2 = (StateMod_DelayTable)delaydata_Vector.get(i);
			if ( new_id.compareTo(delay2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				delaydata_Vector.add(i,delay);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			delaydata_Vector.add(delay);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		delaydata_Vector.add(StateMod_Util.indexOf(delaydata_Vector,before), delay );
	}
	comp.setDirty ( true );

	// Close this dialog...

	dispose();

	// Refresh the monthly delay table window so the user can check the new data...

	StateMod_DelayTable_JFrame delay_JFrame = null;
	if ( monthly_data ) {
		delay_JFrame = (StateMod_DelayTable_JFrame)__dataset_wm.refreshWindow (
		StateMod_DataSet_WindowManager.WINDOW_DELAY_TABLE_MONTHLY,true);
	}
	else {
		delay_JFrame = (StateMod_DelayTable_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_DELAY_TABLE_DAILY,true);
	}

	// Select the specific identifier...

	delay_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	if ( monthly_data ) {
		StringBuffer info = new StringBuffer (
		"The monthly delay table " + new_id + " has been added.\n" +
		"The following defaults have been set and can be changed:\n" +
		"  * Number of months specified by user.\n" +
		"  * Constant percentage assigned to all months.\n" +
		"\n" +
		"You should use the Delay Tables window to modify the defaults.\n" );
		new ResponseJDialog(delay_JFrame, "New Monthly Delay Table", info.toString(), ResponseJDialog.OK);
	}
	else {
		StringBuffer info = new StringBuffer (
		"The daily delay table " + new_id + " has been added.\n" +
		"The following defaults have been set and can be changed:\n" +
		"  * Number of days specified by user.\n" +
		"  * Constant percentage assigned to all days.\n" +
		"\n" +
		"You should use the Delay Tables window to modify the defaults.\n" );
		new ResponseJDialog(delay_JFrame, "New Daily Delay Table", info.toString(), ResponseJDialog.OK);
	}

	// Refresh the JTree delay table data...

	if ( monthly_data ) {
		__dataset_JTree.refresh (StateMod_DataSet.COMP_DELAY_TABLE_MONTHLY_GROUP );
		__dataset_JTree.refresh (StateMod_DataSet.COMP_DELAY_TABLE_DAILY_GROUP );
	}
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( delay_JFrame != null ) {
		delay_JFrame.toFront();
	}
}

/**
Add a new diversion to the network.
*/
private void addDiversion()
{	String routine = "StateModGUI_AddComponent_JDialog.addDiversion";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String value1[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value1[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}
	String value2[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value2[i] = __initial_tsvalue2_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning += "\nThe new diversion identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new diversion identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new diversion name is > 24 characters.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_DIVERSION_STATIONS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe diversion identifier is already in use as a diversion.";
	}
	data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe diversion identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning += "\nThe diversion identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe diversion identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nA diversion name must be specified.";
	}
	double [] dvalue1 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value1[i]) ) {
			warning += "\nDiversion value (" + value1[i] + ") must be a number.";
		}
		else {
			dvalue1[i] = StringUtil.atod(value1[i]);
		}
	}
	DataSetComponent comp_divts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp_divts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nDiversion time series identifier (" + new_id + ") is already in use.";
	}
	double [] dvalue2 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value2[i]) ) {
			warning += "\nDemand value (" + value2[i] + ") must be a number.";
		}
		else {
			dvalue2[i] = StringUtil.atod(value2[i]);
		}
	}
	DataSetComponent comp_demts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_DEMAND_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp_demts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nDemand time series identifier (" + new_id + ") is already in use.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a river node...

	helperAddRiverNode ( new_id, new_name );

	// Create a new diversion...

	StateMod_Diversion div = new StateMod_Diversion();
	div.setID ( new_id );
	div.setName ( new_name );
	div.setUsername ( new_name );
	div.setCgoto ( new_id );	// River node ID assumed to be the same as the common ID
	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_STATIONS );
	List divdata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( divdata_Vector.size() == 0 ) {
			divdata_Vector.add ( div );
		}
		else {
			divdata_Vector.add ( 0, div );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		divdata_Vector.add ( div );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = divdata_Vector.size();
		StateMod_Diversion div2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			div2 = (StateMod_Diversion)divdata_Vector.get(i);
			if ( new_id.compareTo(div2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				divdata_Vector.add(i,div);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			divdata_Vector.add(div);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		divdata_Vector.add(StateMod_Util.indexOf(divdata_Vector,before), div );
	}
	comp.setDirty ( true );

	// Assign a default water right...

	StateMod_DiversionRight new_right = new StateMod_DiversionRight();
	new_right.setID ( new_id + ".01" ); // Same as the diversion + ".01"
	new_right.setName ( new_name );	// Same as the diversion
	new_right.setCgoto ( new_id );	// Same as the diversion

	// Always insert alphabetically.  This should hopefully get the right near the diversion...
	comp =	__dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_RIGHTS );
	List divrights_Vector = (List)comp.getData();
	int size = divrights_Vector.size();
	StateMod_DiversionRight divright;
	boolean found = false;
	for ( int i = 0; i < size; i++ ) {
		divright = (StateMod_DiversionRight)divrights_Vector.get(i);
		if ( new_id.compareTo(divright.getID()) < 0 ) {
			// Current ID is less so add before the item being compared.
			divrights_Vector.add(i,new_right);
			found = true;
			break;
		}
	}
	if ( !found ) {
		// Current item was not later than any in list so add as last item...
		divrights_Vector.add(new_right);
	}
	comp.setDirty ( true );
	// Connect the rights...
	div.connectRights ( divrights_Vector );

	// Assign a default return flow pattern (single return to downstream
	// node).  Note that the return is specified for the river node.

	String delay_table = __delay_table1_JComboBox.getSelected();
	String downstream_node = StringUtil.getToken(__downstream_nodes_JComboBox.getSelected(), " -", 0, 0 );
	if ( !delay_table.equals(__NONE) &&!downstream_node.equals(__NONE) ) {
		StateMod_ReturnFlow new_return = new StateMod_ReturnFlow ( StateMod_DataSet.COMP_DIVERSION_STATIONS);
		new_return.setCrtnid ( downstream_node );
		div.addReturnFlow ( new_return );
	}

	// Define a monthly historical diversion time series.

	TS ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (	StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY),
		"ACFT", year1, year2, dvalue1 );
	div.setDiversionMonthTS ( (MonthTS)ts );

	// Demand time series (monthly)...

	ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_DEMAND_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (StateMod_DataSet.COMP_DEMAND_TS_MONTHLY),
		"ACFT", year1, year2, dvalue2 );
	div.setDemandMonthTS ( (MonthTS)ts );

	// Close this dialog...

	dispose();

	// Refresh the diversion window so the user can check the new data...

	StateMod_Diversion_JFrame div_JFrame = (StateMod_Diversion_JFrame)
		__dataset_wm.refreshWindow (
		StateMod_DataSet_WindowManager.WINDOW_DIVERSION, true );

	// Select the specific identifier...

	div_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The diversion " + StateMod_Util.createDataLabel ( div, true ) + " has been added.\n" +
		"The following important defaults have been set and can be changed:\n" +
		"  * Capacity of 0.0 CFS.\n" +
		"  * Historical diversion time series (monthly) specified by user.\n" +
		"  * Demand time series (monthly) values specified by user.\n" +
		"  * Efficiencies set to 60% and monthly efficiencies will be used.\n" +
		"  * Estimate daily data from monthly time series.\n" );
	if ( delay_table.equals (__NONE) ) {
		info.append ( "  * No return flow assignment.\n" );
	}
	else {
		info.append ( "  * One monthly return flow assignment (100% to immediate downstream node).\n" );
	}
	info.append (
		"  * Added one diversion right with zero decree and 99999 administration number.\n" +
		"\n" +
		"You should use the Diversions window to modify other defaults, in particular:\n" +
		"  * Verify the main data.\n" +
		"  * Verify the water rights.\n" +
		"  * Verify the return flow locations.\n" );
	new ResponseJDialog(div_JFrame, "New Diversion", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree diversion data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_DIVERSION_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( div_JFrame != null ) {
		div_JFrame.toFront();
	}

	if (__network != null) {
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_DIV,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}	
}

/**
Add a monthly evaporation time series to the data set.
*/
private void addEvaporationTS ( )
{	String routine = "StateModGUI_AddComponent_JDialog.addEvaporationTS";
	String id = __new_id_JTextField.getText().trim();
	String name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String units = __units_JComboBox.getSelected();
	String value[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( id.equals("") ) {
		warning += "\nID must be specified.";
	}
	if ( name.equals("") ) {
		warning += "\nName must be specified.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	double [] dvalue = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value[i]) ) {
			warning += "\nData value (" + value[i] + ") must be a number.";
		}
		else {
			dvalue[i] = StringUtil.atod(value[i]);
		}
	}
	DataSetComponent comp = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp.getData(),id,"Location",1) >= 0 ) {
		warning += "\nIdentifier (" + id + ") is already in use.";
	}

	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	addTimeSeriesToComponent (	comp, id, name, "Evap", units, year1, year2, dvalue );

	// Refresh the evaporation time series display...

	JFrame evap_JFrame = __dataset_wm.refreshWindow (
		StateMod_DataSet_WindowManager.WINDOW_EVAPORATION, true );

	// Refresh the JTree evaporation data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_EVAPORATION_GROUP );

	// Select the new time series...
	// REVISIT - need a new method.

	// Close this window...
	dispose();

	// Push the evaporation window to the front...

	// This does not seem to be working...
	if ( evap_JFrame != null ) {
		evap_JFrame.toFront();
	}
}

/**
Add a new instream node/reach to the network.
*/
private void addInstreamFlow()
{	String routine = "StateModGUI_AddComponent_JDialog.addInstreamFlow";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String value1[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value1[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}
	String value2[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value2[i] = __initial_tsvalue2_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning += "\nThe new instream flow station identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new instream flow station identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new instream flow station name is > 24 characters.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_INSTREAM_STATIONS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe instream flow station identifier is already in use as an instream flow station.";
	}
	data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe instream flow station identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning += "\nThe instream flow station identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe instream flow station identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nAn instream flow station name must be specified.";
	}
	double [] dvalue1 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value1[i]) ) {
			warning += "\nAverage monthly demand value (" + value1[i] + ") is not a number.";
		}
		else {
			dvalue1[i] = StringUtil.atod(value1[i]);
		}
	}
	DataSetComponent comp_isfts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY );
	if ( TSUtil.indexOf((List)comp_isfts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nInstream average monthly flow time series " +
			"identifier (" + new_id + ") is already in use.";
	}
	double [] dvalue2 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value2[i]) ) {
			warning += "\nDemand value (" + value2[i] + ") is not a number.";
		}
		else {
			dvalue2[i] = StringUtil.atod(value2[i]);
		}
	}
	DataSetComponent comp_demts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_DEMAND_TS_MONTHLY );
	if ( TSUtil.indexOf( (List)comp_demts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nInstream flow monthly time series identifier (" + new_id + ") is already in use.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a river node...

	helperAddRiverNode ( new_id, new_name );

	// Create a new instream flow...

	StateMod_InstreamFlow isf = new StateMod_InstreamFlow();
	isf.setID ( new_id );
	isf.setName ( new_name );
	isf.setCgoto ( new_id ); // River node ID assumed to be the same as the common ID
	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_INSTREAM_STATIONS );
	List isfdata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( isfdata_Vector.size() == 0 ) {
			isfdata_Vector.add ( isf );
		}
		else {
			isfdata_Vector.add ( 0, isf );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		isfdata_Vector.add ( isf );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = isfdata_Vector.size();
		StateMod_InstreamFlow isf2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			isf2 = (StateMod_InstreamFlow)isfdata_Vector.get(i);
			if ( new_id.compareTo(isf2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				isfdata_Vector.add(i,isf);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			isfdata_Vector.add(isf);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ",StringUtil.DELIM_ALLOW_STRINGS, 1 );
		isfdata_Vector.add(	StateMod_Util.indexOf(isfdata_Vector,before), isf );
	}
	comp.setDirty ( true );

	// Assign a default water right...

	StateMod_InstreamFlowRight new_right = new StateMod_InstreamFlowRight();
	new_right.setID ( new_id + ".01" ); // Same as the instream flow + ".01"
	new_right.setName ( new_name );	// Same as the instream flow
	new_right.setCgoto ( new_id );	// Same as the instream flow

	// Always insert alphabetically.  This should hopefully get the right near the instream flow...
	comp =	__dataset.getComponentForComponentType (StateMod_DataSet.COMP_INSTREAM_RIGHTS );
	List isfrights_Vector = (List)comp.getData();
	int size = isfrights_Vector.size();
	StateMod_InstreamFlowRight isfright;
	boolean found = false;
	for ( int i = 0; i < size; i++ ) {
		isfright = (StateMod_InstreamFlowRight)isfrights_Vector.get(i);
		if ( new_id.compareTo(isfright.getID()) < 0 ) {
			// Current ID is less so add before the item being compared.
			isfrights_Vector.add(i,new_right);
			found = true;
			break;
		}
	}
	if ( !found ) {
		// Current item was not later than any in list so add as last item...
		isfrights_Vector.add(new_right);
	}
	comp.setDirty ( true );
	// Connect the rights...
	isf.connectRights ( isfrights_Vector );

	// Define an average monthly demand time series.

	TS ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (
		StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY),
		"ACFT", year1, year2, dvalue1 );
	isf.setDemandAverageMonthTS ( (MonthTS)ts );

	// Demand time series (monthly)...

	ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY),
		"ACFT", year1, year2, dvalue2 );
	isf.setDemandMonthTS ( (MonthTS)ts );

	// Close this dialog...

	dispose();

	// Refresh the instream flow window so the user can check the new data...

	StateMod_InstreamFlow_JFrame isf_JFrame = (StateMod_InstreamFlow_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_INSTREAM, true );

	// Select the specific identifier...

	isf_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The instream flow station " + StateMod_Util.createDataLabel ( isf, true ) + " has been added.\n" +
		"The following important defaults have been set and can be changed:\n" +
		"  * Average monthly demand time series specified by user.\n" +
		"  * Demand time series (monthly) values specified by user.\n" +
		"  * Estimate daily data from monthly time series.\n" +
		"  * Added one instream flow right with zero decree and 99999 administration number.\n" +
		"\n" +
		"You should use the Instream Flows window to modify other defaults, in particular:\n" +
		"  * Verify the main data.\n" +
		"  * Verify the water rights.\n" +
		"  * Verify the demand time series.\n" );
	new ResponseJDialog(isf_JFrame, "New Instream Flow Station", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree instream flow data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_INSTREAM_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( isf_JFrame != null ) {
		isf_JFrame.toFront();
	}

	if (__network != null) {
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_ISF,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}		
}

/**
Add an operational right to the data set.
*/
private void addOperationalRight ()
{	String routine = "StateModGUI_AddComponent_JDialog.addOperationalRight";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();
	String type = StringUtil.getToken( __new_oprights_type_JComboBox.getSelected(), " -",0,0);

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning += "\nThe new operational right identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new operational right identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new operational right name is > 24 characters.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_OPERATION_RIGHTS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe operational right identifier is already in use in the operational rights.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning += "\nThe operational right identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe operatinoal right identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nAn operational right name must be specified.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add the operational right.

	StateMod_OperationalRight op = new StateMod_OperationalRight ();
	op.setID ( new_id );
	op.setName ( new_name );
	op.setItyopr ( type );
	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_OPERATION_RIGHTS );
	List opdata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( opdata_Vector.size() == 0 ) {
			opdata_Vector.add ( op );
		}
		else {
			opdata_Vector.add ( 0, op );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		opdata_Vector.add ( op );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = opdata_Vector.size();
		StateMod_OperationalRight op2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			op2 = (StateMod_OperationalRight)opdata_Vector.get(i);
			if ( new_id.compareTo(op2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				opdata_Vector.add(i,op);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			opdata_Vector.add(op);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		opdata_Vector.add(StateMod_Util.indexOf(opdata_Vector,before), op );
	}
	comp.setDirty ( true );

	// Close this dialog...

	dispose();

	// Refresh the operational right so the user can check the new data...

	StateMod_OperationalRight_JFrame op_JFrame = (StateMod_OperationalRight_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_OPERATIONAL_RIGHT, true );

	// Select the specific identifier...

	op_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The new operational right " + StateMod_Util.createDataLabel ( op, true ) + " has been added.\n" +
		"\n" +
		"You should use the Operational Rights window to define the details about the right.\n" );
	new ResponseJDialog(op_JFrame, "New Operational Right",
		info.toString(), ResponseJDialog.OK);

	// Refresh the JTree river operational rights data...

	__dataset_JTree.refresh(StateMod_DataSet.COMP_OPERATION_GROUP);
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( op_JFrame != null ) {
		op_JFrame.toFront();
	}
}

/**
Add a new "other" node to the network.
*/
private void addOtherNode()
{	String routine = "StateModGUI_AddComponent_JDialog.addOtherNode";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning +="\nThe new node identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new node identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new node name is > 24 characters.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe node identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning +="\nThe node identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe node identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nA node name must be specified.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a river node...

	helperAddRiverNode ( new_id, new_name );

	// Close this dialog...

	dispose();

	// Refresh the network window so the user can check the new data...

	StateMod_RiverNetworkNode_JFrame net_JFrame = (StateMod_RiverNetworkNode_JFrame)
		__dataset_wm.refreshWindow ( StateMod_DataSet_WindowManager.WINDOW_RIVER_NETWORK, true );

	// Select the specific identifier...

	net_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The node " + new_id + " (" + new_name + ") has been added.\n" +
		"\n" +
		"You can use the River Network window to modify the node name, if necessary.\n" );
	new ResponseJDialog(net_JFrame, "New Node", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree river network data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_RIVER_NETWORK_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( net_JFrame != null ) {
		net_JFrame.toFront();
	}

	if (__network != null) {
		Message.printStatus(1, "", "Adding new node to network ....");
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_OTHER,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}
}

/**
Add a monthly precipitation time series to the data set.
*/
private void addPrecipitationTS ( )
{	String routine = "StateModGUI_AddComponent_JDialog.addPrecipitationTS";
	String id = __new_id_JTextField.getText().trim();
	String name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String units = __units_JComboBox.getSelected();
	String value[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( id.equals("") ) {
		warning += "\nID must be specified.";
	}
	if ( name.equals("") ) {
		warning += "\nName must be specified.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	double [] dvalue = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value[i]) ) {
			warning += "\nData value (" + value[i] + ") is not a number.";
		}
		else {
			dvalue[i] = StringUtil.atod(value[i]);
		}
	}
	DataSetComponent comp = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp.getData(),id,"Location",1) >= 0 ) {
		warning += "\nIdentifier (" + id + ") is already in use.";
	}

	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Create new time series...

	addTimeSeriesToComponent ( comp, id, name, "Precip", units, year1, year2, dvalue );

	// Refresh the precipitation time series display...

	JFrame precip_JFrame = __dataset_wm.refreshWindow (
		StateMod_DataSet_WindowManager.WINDOW_PRECIPITATION, true );

	// Refresh the JTree precipitation data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_PRECIPITATION_GROUP );

	// Select the new time series...
	// REVISIT - need a new method.

	// Close this window...
	dispose();

	// Push the precipitation window to the front...

	// This does not seem to be working...
	if ( precip_JFrame != null ) {
		precip_JFrame.toFront();
	}
}

/**
Add a new reservoir to the network.
*/
private void addReservoir()
{	String routine = "StateModGUI_AddComponent_JDialog.addReservoir";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String value1 = __initial_value1_JTextField.getText().trim();
	String tsvalue1[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		tsvalue1[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}
	String tsvalue2[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		tsvalue2[i] = __initial_tsvalue2_JTextField[i].getText().trim();
	}
	String tsvalue3[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		tsvalue3[i] = __initial_tsvalue2_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning += "\nThe new reservoir identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new reservoir identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new reservoir name is > 24 characters.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RESERVOIR_STATIONS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe reservoir identifier is already in use as a reservoir.";
	}
	data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe reservoir identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning += "\nThe reservoir identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe reservoir identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nA reservoir name must be specified.";
	}
	double [] d_tsvalue1 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(tsvalue1[i]) ) {
			warning += "\nEnd of month content value (" +
			tsvalue1[i] + ") is not a number.";
		}
		else {
			d_tsvalue1[i] = StringUtil.atod(tsvalue1[i]);
		}
	}
	DataSetComponent comp_eomts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY );
	if ( TSUtil.indexOf( (List)comp_eomts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nReservoir time series identifier (" + new_id + ") is already in use.";
	}
	double [] d_tsvalue2 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(tsvalue2[i]) ) {
			warning += "\nTarget minimum value (" + tsvalue2[i] + ") is not a number.";
		}
		else {
			d_tsvalue2[i] = StringUtil.atod(tsvalue2[i]);
		}
	}
	DataSetComponent comp_targetts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY );
	if ( TSUtil.indexOf( (List)comp_targetts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nTarget time series identifier (" + new_id + ") is already in use.";
	}
	double [] d_tsvalue3 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(tsvalue3[i]) ) {
			warning += "\nTarget minimum value (" + tsvalue3[i] +
			") is not a number.";
		}
		else {
			d_tsvalue3[i] = StringUtil.atod(tsvalue3[i]);
		}
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	helperAddRiverNode ( new_id, new_name );

	// Create a new reservoir...

	StateMod_Reservoir res = new StateMod_Reservoir();
	res.setID ( new_id );
	res.setName ( new_name );
	res.setCgoto ( new_id );	// River node ID assumed to be the same as the common ID
	res.setVolmax ( value1 );	// User-specified.
	DataSetComponent comp = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_RESERVOIR_STATIONS );
	List resdata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( resdata_Vector.size() == 0 ) {
			resdata_Vector.add ( res );
		}
		else {
			resdata_Vector.add ( 0, res );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		resdata_Vector.add ( res );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = resdata_Vector.size();
		StateMod_Reservoir res2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			res2 = (StateMod_Reservoir)resdata_Vector.get(i);
			if ( new_id.compareTo(res2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				resdata_Vector.add(i,res);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			resdata_Vector.add(res);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		resdata_Vector.add(StateMod_Util.indexOf(resdata_Vector,before), res );
	}
	comp.setDirty ( true );

	// Assign a default water right...

	StateMod_ReservoirRight new_right = new StateMod_ReservoirRight();
	new_right.setID ( new_id + ".01" ); // Same as the reservoir + ".01"
	new_right.setName ( new_name );	// Same as the reservoir
	new_right.setCgoto ( new_id );	// Same as the reservoir

	// Always insert alphabetically.  This should hopefully get the right near the reservoir...
	comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_RIGHTS );
	List resrights_Vector = (List)comp.getData();
	int size = resrights_Vector.size();
	StateMod_ReservoirRight resright;
	boolean found = false;
	for ( int i = 0; i < size; i++ ) {
		resright = (StateMod_ReservoirRight)resrights_Vector.get(i);
		if ( new_id.compareTo(resright.getID()) < 0 ) {
			// Current ID is less so add before the item being compared.
			resrights_Vector.add(i,new_right);
			found = true;
			break;
		}
	}
	if ( !found ) {
		// Current item was not later than any in list so add as last item...
		resrights_Vector.add(new_right);
	}
	// Connect the reservoir rights...
	res.connectRights ( resrights_Vector );
	comp.setDirty ( true );

	// Add one reservoir account...

	StateMod_ReservoirAccount resaccount = new StateMod_ReservoirAccount();
	resaccount.setID ( "1" );
	resaccount.setName ( "Account1" );
	resaccount.setOwnmax ( value1 );
	res.addAccount ( resaccount );

	// Add default area capacity with two points zero to max...

	StateMod_ReservoirAreaCap cap = new StateMod_ReservoirAreaCap ();
	res.addAreaCap ( cap );
	cap = new StateMod_ReservoirAreaCap ();
	cap.setConten ( value1 );
	cap.setSurarea ( StringUtil.atod(value1)/100.0 );
	res.addAreaCap ( cap );

	// Add a default climate station assignment, as follows:
	//
	// 1. If no evaporation or precipitation time series are available, make no assignment.
	// 2. If only evap is available, use the first evaporation station and assign a 100% weight.
	// 3. If only precip is available, use the first precipitation station and assign a 100% weight.
	// 4. If both are available, assign 100% to the first time series in each list.

	String climate_note =
		"  * No evaporation/precipitation stations assigned.";
	DataSetComponent comp_evap = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY );
	DataSetComponent comp_precip = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY );
	if ( comp_evap.hasData() && comp_precip.hasData() ) {
		StateMod_ReservoirClimate climate = new StateMod_ReservoirClimate ();
		TS ts = (TS)((List)comp_precip.getData()).get(0);
		climate.setID ( ts.getLocation() );
		climate.setType ( StateMod_ReservoirClimate.CLIMATE_PTPX );
		climate.setWeight ( 100.0 );
		res.addClimate ( climate );
		climate = new StateMod_ReservoirClimate ();
		ts = (TS)((List)comp_evap.getData()).get(0);
		climate.setID ( ts.getLocation() );
		climate.setType ( StateMod_ReservoirClimate.CLIMATE_EVAP );
		climate.setWeight ( 100.0 );
		res.addClimate ( climate );
		climate_note = "  * Added one precipitation and evaporation station.";
	}
	else if ( comp_evap.hasData() ) {
		StateMod_ReservoirClimate climate = new StateMod_ReservoirClimate ();
		TS ts = (TS)((List)comp_evap.getData()).get(0);
		climate.setID ( ts.getLocation() );
		climate.setType ( StateMod_ReservoirClimate.CLIMATE_EVAP );
		climate.setWeight ( 100.0 );
		res.addClimate ( climate );
		climate_note = "  * Added one evaporation station (assume net evaporation).";
	}
	else if ( comp_precip.hasData() ) {
		StateMod_ReservoirClimate climate = new StateMod_ReservoirClimate ();
		TS ts = (TS)((List)comp_precip.getData()).get(0);
		climate.setID ( ts.getLocation() );
		climate.setType ( StateMod_ReservoirClimate.CLIMATE_PTPX );
		climate.setWeight ( 100.0 );
		res.addClimate ( climate );
		climate_note = "  * Added one precipitation station.";
	}
	
	// Define a monthly historical EOM content time series.

	TS ts = addTimeSeriesToComponent (
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY),
		"ACFT", year1, year2, d_tsvalue1 );
	res.setContentMonthTS ( (MonthTS)ts );

	// Define a monthly target minimum time series.

	ts = addTimeSeriesToComponent (
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (
		StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) +
		"Min", "ACFT", year1, year2, d_tsvalue2 );
	res.setMinTargetMonthTS ( (MonthTS)ts );

	// Define a monthly target maximum time series.

	ts = addTimeSeriesToComponent (
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (	StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) +
		"Max", "ACFT", year1, year2, d_tsvalue3 );
	res.setMaxTargetMonthTS ( (MonthTS)ts );

	// Close this dialog...

	dispose();

	// Refresh the reservoir window so the user can check the new data...

	StateMod_Reservoir_JFrame res_JFrame = (StateMod_Reservoir_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_RESERVOIR, true );

	// Select the specific identifier...

	res_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The reservoir " + StateMod_Util.createDataLabel ( res, true ) + " has been added.\n" +
		"The following important defaults have been set and can be changed:\n" +
		"  * Maximum content specified by user.\n" +
		"  * Historical end of month content time series (monthly) specified by user.\n" +
		"  * Target time series (monthly) values specified by user.\n" +
		"  * Added one reservoir right with zero decree and 99999 administration number.\n" +
		"  * Added one reservoir owner account with the maximum storage.\n" +
		"  * Added an area/capacity curve with two points.\n" + climate_note + "\n\n" +
		"You should use the Reservoirs window to modify other defaults, in particular:\n" +
		"  * Verify the main data.\n" +
		"  * Verify the water right(s).\n" +
		"  * Verify the reservoir account(s)\n" +
		"  * Verify the climate station assignment.\n" +
		"  * Verify the reservoir area/capacity curve.\n" );
	new ResponseJDialog(res_JFrame, "New Reservoir", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree reservoir data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_RESERVOIR_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( res_JFrame != null ) {
		res_JFrame.toFront();
	}

	if (__network != null) {
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_RES,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}	
}

/**
Add a new stream estimate station.
*/
private void addStreamEstimate()
{	String routine = "StateModGUI_AddComponent_JDialog.addStreamEstimate";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning += "\nThe new stream estimate station identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new stream estimate station identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new stream estimate station name is > 24 characters.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe stream estimate station identifier is " +
			"already in use as a stream estimate station.";
	}
	data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe stream estimate station identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning += "\nThe stream estimate station identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe stream estimate station identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nA stream estimate station name must be specified.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a river node...

	helperAddRiverNode ( new_id, new_name );

	// Create a new stream estimate station...

	StateMod_StreamEstimate sta = new StateMod_StreamEstimate();
	sta.setID ( new_id );
	sta.setName ( new_name );
	sta.setCgoto ( new_id ); // River node ID assumed to be the same as the common ID
	DataSetComponent comp = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS );
	List stadata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( stadata_Vector.size() == 0 ) {
			stadata_Vector.add ( sta );
		}
		else {
			stadata_Vector.add ( 0, sta );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		stadata_Vector.add ( sta );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = stadata_Vector.size();
		StateMod_StreamEstimate sta2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			sta2 = (StateMod_StreamEstimate)stadata_Vector.get(i);
			if ( new_id.compareTo(sta2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				stadata_Vector.add(i,sta);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			stadata_Vector.add(sta);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		stadata_Vector.add(StateMod_Util.indexOf(stadata_Vector,before), sta );
	}
	comp.setDirty ( true );

	// Add the stream estimate coefficients...

	StateMod_StreamEstimate_Coefficients coef = new StateMod_StreamEstimate_Coefficients ();
	coef.setFlowX ( new_id );
	coef.setID ( new_id );
	coef.setName ( new_name );
	DataSetComponent comp2 = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_STREAMESTIMATE_COEFFICIENTS );
	List coef_Vector = (List)comp2.getData();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( coef_Vector.size() == 0 ) {
			coef_Vector.add ( coef );
		}
		else {
			coef_Vector.add ( 0, coef );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		coef_Vector.add ( coef );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = coef_Vector.size();
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			if ( new_id.compareTo(coef.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				coef_Vector.add(i,coef);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			coef_Vector.add(coef);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		coef_Vector.add(StateMod_Util.indexOf(coef_Vector,before), coef );
	}
	comp2.setDirty ( true );

	// Close this dialog...

	dispose();

	// Refresh the stream estimate stations window so the user can check the new data...

	StateMod_StreamEstimate_JFrame sta_JFrame = (StateMod_StreamEstimate_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_STREAMESTIMATE, true );

	// Select the specific identifier...

	sta_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The stream estimate station " + StateMod_Util.createDataLabel ( sta, true ) + " has been added.\n\n" +
		"You should use the Stream Estimate Stations window to modify the defaults,\n" +
		"in particular the stream estimate coefficients (none are assigned by default).\n\n" +
		"Developing stream estimate coefficients requires skill and" +
		" external software/analysis may be needed.\n" +
		"Refer to the StateMod documentation for more information." );
	new ResponseJDialog(sta_JFrame, "New Stream Estimate Station", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree stream estimate data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_STREAMESTIMATE_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( sta_JFrame != null ) {
		sta_JFrame.toFront();
	}

	if (__network != null) {
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_BASEFLOW,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}		
}

/**
Add a new stream gage station.
*/
private void addStreamGage()
{	String routine = "StateModGUI_AddComponent_JDialog.addStreamGage";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String [] value1 = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value1[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning +="\nThe new stream gage identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new stream gage identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new stream gage name is > 24 characters.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe stream gage identifier is already in use as a stream gage.";
	}
	data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe stream gage identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning +="\nThe stream gage identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe stream gage identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nA stream gage name must be specified.";
	}
	double [] dvalue1 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value1[i]) ) {
			warning += "\nHistoric flow value (" + value1[i] +
			") is not a number.";
		}
		else {
			dvalue1[i] = StringUtil.atod(value1[i]);
		}
	}
	DataSetComponent comp_flowts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp_flowts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nHistorical flow time series identifier (" + new_id + ") is already in use.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a river node...

	helperAddRiverNode ( new_id, new_name );

	// Create a new stream gage station...

	StateMod_StreamGage gage = new StateMod_StreamGage();
	gage.setID ( new_id );
	gage.setName ( new_name );
	gage.setCgoto ( new_id );	// River node ID assumed to be the same as the common ID
	DataSetComponent comp = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS );
	List gagedata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( gagedata_Vector.size() == 0 ) {
			gagedata_Vector.add ( gage );
		}
		else {
			gagedata_Vector.add ( 0, gage );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		gagedata_Vector.add ( gage );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = gagedata_Vector.size();
		StateMod_StreamGage gage2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			gage2 = (StateMod_StreamGage)gagedata_Vector.get(i);
			if ( new_id.compareTo(gage2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				gagedata_Vector.add(i,gage);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			gagedata_Vector.add(gage);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		gagedata_Vector.add(StateMod_Util.indexOf(gagedata_Vector,before), gage );
	}
	comp.setDirty ( true );

	// Define a monthly historical flow time series.

	TS ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY),
		"ACFT", year1, year2, dvalue1 );
	gage.setHistoricalMonthTS ( (MonthTS)ts );

	// Close this dialog...

	dispose();

	// Refresh the stream gage window so the user can check the new data...

	StateMod_StreamGage_JFrame gage_JFrame = (StateMod_StreamGage_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_STREAMGAGE, true );

	// Select the specific identifier...

	gage_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The stream gage " + StateMod_Util.createDataLabel ( gage, true ) + " has been added.\n" +
		"The following important defaults have been set and can be changed:\n" +
		"  * Historical flow time series (monthly) specified by user.\n\n" +
		"You should use the Stream Gage Stations window to modify the defaults.\n" );
	new ResponseJDialog(gage_JFrame, "New Stream Gage", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree stream gage data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_STREAMGAGE_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( gage_JFrame != null ) {
		gage_JFrame.toFront();
	}

	if (__network != null) {
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_FLOW,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}			
}

/**
Add a new time series to a component.  This method performs data checks.
@param units data units for time series.
*/
private void addTimeSeries ()
{	String routine = "StateModGUI_AddComponent_JDialog.addTimeSeries";
	String new_id = StringUtil.getToken(__primary_data_JComboBox.getSelected(), " -", 0, 0 );
	// Use this to figure out if a diversion or well-only...
	String new_fullid = __primary_data_JComboBox.getSelected();
	String year1 = "0";
	if ( __year1_JTextField != null ) {
		year1 = __year1_JTextField.getText().trim();
	}
	String year2 = "0";
	if ( __year2_JTextField != null ) {
		year2 = __year2_JTextField.getText().trim();
	}
	String [] tsvalue1 = new String[12];
	String [] tsvalue2 = new String[12];
	String value1 = null;
	String value2 = null;	// Used for reservoir target max
	boolean is_monthly = true;
	if ( (__comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_DIVERSION_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY) ) {
		is_monthly = false;
	}
	if ( is_monthly ) {
		// Monthly data - use 12 values...
		for ( int i = 0; i < 12; i++ ) {
			tsvalue1[i] = __initial_tsvalue1_JTextField[i].getText().trim();
		}
	}
	else {
		// Daily data - use one value...
		value1 = __initial_value1_JTextField.getText().trim();
	}
	if ( (__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
		// Need to handle the second (max target) time series...
		if ( is_monthly ) { // Monthly data - use 12 values...
			for ( int i = 0; i < 12; i++ ) {
				tsvalue2[i] = __initial_tsvalue2_JTextField[i].getText().trim();
			}
		}
		else {
			// Daily data - use one value...
			value2 = __initial_value2_JTextField.getText().trim();
		}
	}

	// Verify data...

	String warning = "";

	// Identifiers should be fine because station list was read from a StateMod file...

	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}

	List data_Vector = (List)(__dataset.getComponentForComponentType(__comp_type)).getData();
	int pos = TSUtil.indexOf ( data_Vector, new_id,"Location",1);
	if ( pos >= 0 ) {
		int response = new ResponseJDialog(this, "Add Time Series",
		"A time series for \"" + new_id + "\" already exists.\n" +
		"Press OK to replace the existing time series.\n" +
		"Press Cancel to go the previous step.",
		ResponseJDialog.OK | ResponseJDialog.CANCEL).response();
		if ( response == ResponseJDialog.CANCEL ) {
			return;
		}
		// Delete the existing...
		data_Vector.remove ( pos );
		if ((__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
			(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
			// Look for and remove a second instance for the max target time series...
			int pos2 = TSUtil.indexOf ( data_Vector, new_id,"Location",1);
			if ( pos2 >= 0 ) {
				data_Vector.remove ( pos2 );
			}
		}
	}

	double [] dvalue1 = null;
	double [] dvalue2 = null;
	if ( is_monthly ) {
		dvalue1 = new double[12];
		for ( int i = 0; i < 12; i++ ) {
			if ( !StringUtil.isDouble(tsvalue1[i]) ) {
				warning += "\nTime series value ("+tsvalue1[i] + ") is not a number.";
			}
			else {
				dvalue1[i] = StringUtil.atod(tsvalue1[i]);
			}
		}
	}
	else {
		if ( !StringUtil.isDouble(value1) ) {
			warning += "\nTime series value (" + value1 + ") is not a number.";
		}
		else {	dvalue1 = new double[1];
			dvalue1[0] = StringUtil.atod(value1);
		}
	}
	if ( (__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
		// Get a second set of values for the maximum target...
		if ( is_monthly ) {
			dvalue2 = new double[12];
			for ( int i = 0; i < 12; i++ ) {
				if ( !StringUtil.isDouble(tsvalue2[i]) ) {
					warning += "\nTime series value (" + tsvalue2[i] +") is not a number.";
				}
				else {
					dvalue2[i] = StringUtil.atod(tsvalue2[i]);
				}
			}
		}
		else {
			if ( !StringUtil.isDouble(value2) ) {
				warning += "\nTime series value (" + value2 + ") is not a number.";
			}
			else {
				dvalue2 = new double[1];
				dvalue2[0] = StringUtil.atod(value2);
			}
		}
	}

	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Define a time series.

	TS ts = null, ts2 = null;
	if ( (__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
		// Need to deal with two time series and add the "Min" and "Max" to the data types.
		ts = addTimeSeriesToComponent (	
			__dataset.getComponentForComponentType ( __comp_type), new_id, new_id,
			StateMod_DataSet.lookupTimeSeriesDataType (__comp_type)+ "Min",
			StateMod_DataSet.lookupTimeSeriesDataUnits(__comp_type), year1, year2, dvalue1 );
		ts2 = addTimeSeriesToComponent (	
			__dataset.getComponentForComponentType ( __comp_type), new_id, new_id,
			StateMod_DataSet.lookupTimeSeriesDataType (__comp_type)+ "Max",
			StateMod_DataSet.lookupTimeSeriesDataUnits(__comp_type), year1, year2, dvalue2 );
	}
	else {
		ts = addTimeSeriesToComponent (	
			__dataset.getComponentForComponentType ( __comp_type), new_id, new_id,
			StateMod_DataSet.lookupTimeSeriesDataType (__comp_type),
			StateMod_DataSet.lookupTimeSeriesDataUnits(__comp_type), year1, year2, dvalue1 );
	}
	__dataset.setDirty ( __comp_type, true );

	// Close this dialog...

	dispose();

	if ( (__comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY) ) {
		// Refresh the stream gage window so the user can check the new data...

		DataSetComponent comp2 = __dataset.getComponentForComponentType(
			StateMod_DataSet.COMP_STREAMGAGE_STATIONS );
		List data2 = (List)comp2.getData();
		int pos2 = StateMod_Util.indexOf ( data2, new_id );
		StateMod_StreamGage gage = (StateMod_StreamGage)data2.get(pos2);
		StateMod_StreamGage_JFrame gage_JFrame = (StateMod_StreamGage_JFrame)
			__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_STREAMGAGE, true);

		// Set the time series before selecting in the GUI so that the
		// checkboxes will be correct...

		if ( __comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY )  {
			gage.setHistoricalMonthTS ( (MonthTS)ts );
		}
		else if(__comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY )  {
			gage.setHistoricalDayTS ( (DayTS)ts );
		}

		// Select the specific identifier...

		gage_JFrame.selectID ( new_id );

		// Tell the user that they can make further edits...

		StringBuffer info = new StringBuffer ();
		if ( __comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY )  {
			info.append (
			"A new stream gage historical monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( gage, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if ( __comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY )  {
			info.append (
			"A new stream gage historical daily time series has been defined for " +
			StateMod_Util.createDataLabel ( gage, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		new ResponseJDialog(gage_JFrame, "New Stream Gage Time Series",
			info.toString(), ResponseJDialog.OK);

		// Bring the appropriate data window to the front after closing the dialog...

		if ( gage_JFrame != null ) {
			gage_JFrame.toFront();
		}
	}
	else if((__comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_DIVERSION_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY) ||
		((__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY) &&
		(new_fullid.indexOf(__WELL_ONLY) < 0)) ) {
		// Refresh the diversion window so the user can check the new data...

		DataSetComponent comp2 = __dataset.getComponentForComponentType(
			StateMod_DataSet.COMP_DIVERSION_STATIONS );
		List data2 = (List)comp2.getData();
		int pos2 = StateMod_Util.indexOf ( data2, new_id );

		StateMod_Diversion div = (StateMod_Diversion)data2.get(pos2);
		StateMod_Diversion_JFrame div_JFrame = (StateMod_Diversion_JFrame)
			__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_DIVERSION, true);

		// Need to set the time series before refreshing the display so
		// the checkboxes will be correct...

		if ( __comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY )  {
			div.setDiversionMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DIVERSION_TS_DAILY){
			div.setDiversionDayTS ( (DayTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DEMAND_TS_MONTHLY){
			div.setDemandMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY ) {
			div.setDemandOverrideMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY ) {
			div.setDemandAverageMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY ){
			div.setDemandDayTS ( (DayTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ){
			div.setConsumptiveWaterRequirementMonthTS (	(MonthTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY ){
			div.setConsumptiveWaterRequirementDayTS ( (DayTS)ts );
		}

		// Select the specific identifier...

		div_JFrame.selectID ( new_id );

		// Tell the user that they can make further edits...

		StringBuffer info = new StringBuffer ();
		if ( __comp_type == StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY )  {
			info.append (
			"A new diversion historical monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DIVERSION_TS_DAILY){
			info.append (
			"A new diversion historical daily time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DEMAND_TS_MONTHLY){
			info.append (
			"A new diversion demand monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY ) {
			info.append (
			"A new diversion demand override monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY ) {
			info.append (
			"A new diversion demand average monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY ){
			info.append (
			"A new diversion demand daily time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ){
			info.append (
			"A new consumptive water requirement monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY ){
			info.append (
			"A new consumptive water requirement daily time series has been defined for " +
			StateMod_Util.createDataLabel ( div, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		new ResponseJDialog(div_JFrame, "New Diversion Time Series", info.toString(), ResponseJDialog.OK);

		// Bring the appropriate data window to the front after closing the dialog...

		if ( div_JFrame != null ) {
			div_JFrame.toFront();
		}
	}
	else if((__comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ) {
		// Refresh the reservoir window so the user can check the new data...

		DataSetComponent comp2 = __dataset.getComponentForComponentType(
			StateMod_DataSet.COMP_RESERVOIR_STATIONS );
		List data2 = (List)comp2.getData();
		int pos2 = StateMod_Util.indexOf ( data2, new_id );
		StateMod_Reservoir res = (StateMod_Reservoir)data2.get(pos2);
		StateMod_Reservoir_JFrame res_JFrame = (StateMod_Reservoir_JFrame)
			__dataset_wm.refreshWindow ( StateMod_DataSet_WindowManager.WINDOW_RESERVOIR, true);

		// Need to set the time series before refreshing the display so
		// the checkboxes will be correct...

		if ( __comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY )  {
			res.setContentMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY){
			res.setContentDayTS ( (DayTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY){
			res.setMinTargetMonthTS ( (MonthTS)ts );
			res.setMaxTargetMonthTS ( (MonthTS)ts2 );
		}
		else if (__comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY){
			res.setMinTargetDayTS ( (DayTS)ts );
			res.setMaxTargetDayTS ( (DayTS)ts2 );
		}

		// Select the specific identifier...

		res_JFrame.selectID ( new_id );

		// Tell the user that they can make further edits...

		StringBuffer info = new StringBuffer ();
		if ( __comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY )  {
			info.append (
			"A new reservoir historical end-of-month content monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( res, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY){
			info.append (
			"A new reservoir historical end-of-day content daily time series has been defined for " +
			StateMod_Util.createDataLabel ( res, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY){
			info.append (
			"New reservoir target monthly time series have been defined for " +
			StateMod_Util.createDataLabel ( res, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY){
			info.append (
			"New reservoir target daily time series have been defined for " +
			StateMod_Util.createDataLabel ( res, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		new ResponseJDialog(res_JFrame, "New Reservoir Time Series", info.toString(), ResponseJDialog.OK);

		// Bring the appropriate data window to the front after closing the dialog...

		if ( res_JFrame != null ) {
			res_JFrame.toFront();
		}
	}
	else if((__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY) ) {
		// Refresh the instream flow window so the user can check the new data...

		DataSetComponent comp2 = __dataset.getComponentForComponentType(
			StateMod_DataSet.COMP_INSTREAM_STATIONS );
		List data2 = (List)comp2.getData();
		int pos2 = StateMod_Util.indexOf ( data2, new_id );
		StateMod_InstreamFlow isf = (StateMod_InstreamFlow)data2.get(pos2);
		StateMod_InstreamFlow_JFrame isf_JFrame = (StateMod_InstreamFlow_JFrame)
			__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_INSTREAM, true);

		// Need to set the time series before refreshing the display so
		// the checkboxes will be correct...

		if ( __comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY){
			isf.setDemandMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY ) {
			isf.setDemandAverageMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY ){
			isf.setDemandDayTS ( (DayTS)ts );
		}

		// Select the specific identifier...

		isf_JFrame.selectID ( new_id );

		// Tell the user that they can make further edits...

		StringBuffer info = new StringBuffer ();
		if ( __comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY) {
			info.append (
			"A new instream flow demand monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( isf, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if ( __comp_type==StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY ) {
			info.append (
			"A new instream flow demand average monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( isf, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY ){
			info.append (
			"A new instream flow demand daily time series has been defined for " +
			StateMod_Util.createDataLabel ( isf, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		new ResponseJDialog(isf_JFrame, "New Instream Flow Time Series",
			info.toString(), ResponseJDialog.OK);

		// Bring the appropriate data window to the front after closing the dialog...

		if ( isf_JFrame != null ) {
			isf_JFrame.toFront();
		}
	}
	else if((__comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY)||
		(__comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY) ||
		(__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY) ||
		((__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY) &&
		(new_fullid.indexOf(__WELL_ONLY) >= 0)) ) {
		// Refresh the well window so the user can check the new data...

		DataSetComponent comp2 = __dataset.getComponentForComponentType(StateMod_DataSet.COMP_WELL_STATIONS );
		List data2 = (List)comp2.getData();
		int pos2 = StateMod_Util.indexOf ( data2, new_id );
		StateMod_Well well = (StateMod_Well)data2.get(pos2);
		StateMod_Well_JFrame well_JFrame = (StateMod_Well_JFrame)
			__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_WELL, true);

		// Need to set the time series before refreshing the display so
		// the checkboxes will be correct...

		if ( __comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY )  {
			well.setPumpingMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY){
			well.setPumpingDayTS ( (DayTS)ts );
		}
		else if (__comp_type==StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY){
			well.setDemandMonthTS ( (MonthTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY ){
			well.setDemandDayTS ( (DayTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ){
			well.setConsumptiveWaterRequirementMonthTS ((MonthTS)ts );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY ){
			well.setConsumptiveWaterRequirementDayTS ( (DayTS)ts );
		}

		// Select the specific identifier...

		well_JFrame.selectID ( new_id );

		// Tell the user that they can make further edits...

		StringBuffer info = new StringBuffer ();
		if (	__comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY )  {
			info.append (
			"A new well pumping monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( well, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY){
			info.append (
			"A new well pumping daily time series has been defined for " +
			StateMod_Util.createDataLabel ( well, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type==StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY){
			info.append (
			"A new well demand monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( well, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY ){
			info.append (
			"A new well demand daily time series has been defined for " +
			StateMod_Util.createDataLabel ( well, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ){
			info.append (
			"A new consumptive water requirement monthly time series has been defined for " +
			StateMod_Util.createDataLabel ( well, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		else if (__comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY ){
			info.append (
			"A new consumptive water requirement daily time series has been defined for " +
			StateMod_Util.createDataLabel ( well, true ) + ".\n\n" +
			"Use the time series table to edit the data.\n" );
		}
		new ResponseJDialog(well_JFrame, "New Well Time Series", info.toString(), ResponseJDialog.OK);

		// Bring the appropriate data window to the front after closing the dialog...

		if ( well_JFrame != null ) {
			well_JFrame.toFront();
		}
	}
}

/**
Add a time series to a data component.  This method can be called whenever a
time series is being added to a list.  The only tricky case is the reservoir
targets, which include a minimum and maximum target time series for each
reservoir.  It is assumed that if the component is
COMP_RESERVOIR_TARGET_TS_MONTHLY that the minimum will be added first and then
the maximum.  Consequently, if the datatype for this component includes "Max",
the time series will be added one position after the normal insert position.
@param data list of time series to add to.
@param id Identifier (location) for the new time series.
@param datatype Data type for the new time series.
@param units Data units for the new time series.
@param year1 First year of the time series, after being checked for validity.
@param year2 Last year of the time series, after being checked for validity.
*/
private TS addTimeSeriesToComponent ( DataSetComponent comp, String id, String name,
	String datatype, String units, String year1, String year2, double [] dvalue )
{	// Create new time series...

	int comp_type = comp.getComponentType();
	boolean is_monthly = true;	// Default is to add monthly TS
	if ( (comp_type == StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_DIVERSION_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_DEMAND_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY) ||
		(comp_type == StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY) ) {
		is_monthly = false;
	}
	TS ts = null;
	DateTime date1 = null;
	DateTime date2 = null;
	if ( is_monthly ) {
		ts = new MonthTS();
		date1 = new DateTime ( DateTime.PRECISION_MONTH );
		date2 = new DateTime ( DateTime.PRECISION_MONTH );
	}
	else {	ts = new DayTS();
		date1 = new DateTime ( DateTime.PRECISION_DAY );
		date2 = new DateTime ( DateTime.PRECISION_DAY );
	}
	YearType yeartype = __dataset.getCyrl();
	// OK to set day here... it will be ignored if not needed...
	if ( yeartype == YearType.WATER ) {
		date1.setYear ( StringUtil.atoi(year1) - 1 );
		date1.setMonth ( 10 );
		date2.setYear ( StringUtil.atoi(year2) );
		date2.setMonth ( 9 );
	}
	else if ( yeartype == YearType.NOV_TO_OCT ) {
		date1.setYear ( StringUtil.atoi(year1) - 1 );
		date1.setMonth ( 11 );
		date2.setYear ( StringUtil.atoi(year2) );
		date2.setMonth ( 10 );
	}
	else {
		// Calendar...
		date1.setYear ( StringUtil.atoi(year1) );
		date1.setMonth ( 1 );
		date2.setYear ( StringUtil.atoi(year2) );
		date2.setMonth ( 12 );
	}
	if ( (comp_type == StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY)||
		(comp_type == StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY) ) {
		// Only twelve values are specified and they will be reused.
		// The year is reset to zero, regardless of the calendar type.
		date1.setYear ( 0 );
		date2.setYear ( 0 );
	}
	// Same for all cases...
	date1.setDay ( 1 );
	date2.setDay ( TimeUtil.numDaysInMonth(date2) );
	try {
		if ( is_monthly ) {
			ts.setIdentifier(id+".StateMod."+ datatype + ".Month" );
		}
		else {
			ts.setIdentifier(id+".StateMod."+ datatype + ".Day" );
		}
	}
	catch ( Exception e ) {
	}

	List data = (List)comp.getData();

	ts.setDate1 ( date1 );
	ts.setDate1Original ( date1 );
	ts.setDate2 ( date2 );
	ts.setDate2Original ( date2 );
	ts.setDescription ( name );
	ts.setLocation ( id );
	ts.setDataUnits ( units );
	ts.setDataUnitsOriginal ( units );
	if ( is_monthly ) {
		((MonthTS)ts).allocateDataSpace ( dvalue[0] );
	}
	else {
		((DayTS)ts).allocateDataSpace ( dvalue[0] );
	}
	// Initialize the values for each month (if daily, the values were
	// initialized in the allocateDataSpace() call)...
	if ( is_monthly ) {
		DateTime date = new DateTime ( date1 );
		for ( ; date.lessThanOrEqualTo(date2); date.addMonth(1) ) {
			ts.setDataValue ( date, dvalue[date.getMonth() - 1] );
		}
	}
	// Add to the vector in the desired position...
	int offset = 0;
	if ( ((comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY) ||
		(comp_type == StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY)) &&
		(StringUtil.indexOfIgnoreCase(datatype,"Max",0) >= 0 ) ) {
		offset = 1;
	}
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( data.size() == 0 ) {
			data.add ( ts );
		}
		else {
			data.add ( offset, ts );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		// Should also work for reservoir content...
		data.add ( ts );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = data.size();
		TS ts2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			ts2 = (TS)data.get(i);
			if ( id.compareTo(ts2.getLocation()) < 0 ) {
				// Current ID is less so add before the item being compared.  Should also work for
				// reservoir target...
				data.add(i,ts);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add
			// as last item.  Should also work for reservoir target...
			data.add(ts);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before.  It should exist because the
		// original list was created from the same data array...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		data.add(TSUtil.indexOf(data,before,"Location",1) + offset, ts );
	}

	// Set the time series component dirty...

	comp.setDirty ( true );

	return ts;
}

/**
Add a new well to the network.
*/
private void addWell()
{	String routine = "StateModGUI_AddComponent_JDialog.addWell";
	String new_id = __new_id_JTextField.getText().trim();
	String new_name = __new_name_JTextField.getText().trim();
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();
	String value1[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value1[i] = __initial_tsvalue1_JTextField[i].getText().trim();
	}
	String value2[] = new String[12];
	for ( int i = 0; i < 12; i++ ) {
		value2[i] = __initial_tsvalue2_JTextField[i].getText().trim();
	}

	// Verify data...

	String warning = "";
	if ( new_id.length() == 0 ) {
		warning += "\nThe new well identifier must be specified.";
	}
	if ( new_id.length() > 12 ) {
		warning += "\nThe new well identifier is > 12 characters.";
	}
	if ( new_name.length() > 24 ) {
		warning += "\nThe new well name is > 24 characters.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nStart year must be an integer.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nEnd year must be an integer.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	List data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_WELL_STATIONS)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe well identifier is already in use as a well.";
	}
	data_Vector = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( StateMod_Util.indexOf ( data_Vector, new_id) >= 0 ) {
		warning += "\nThe well identifier is already in use in the network.";
	}
	if ( new_id.indexOf(' ') >= 0 ) {
		warning += "\nThe well identifier cannot contain spaces.";
	}
	if ( new_id.indexOf('-') >= 0 ) {
		warning += "\nThe well identifier cannot contain a dash (-).";
	}
	if ( new_name.length() <= 0 ) {
		warning += "\nA well name must be specified.";
	}
	double [] dvalue1 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value1[i]) ) {
			warning += "\nPumping value (" + value1[i] +
			") is not a number.";
		}
		else {
			dvalue1[i] = StringUtil.atod(value1[i]);
		}
	}
	DataSetComponent comp_wellts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp_wellts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nWell time series identifier (" + new_id + ") is already in use.";
	}
	double [] dvalue2 = new double[12];
	for ( int i = 0; i < 12; i++ ) {
		if ( !StringUtil.isDouble(value2[i]) ) {
			warning += "\nDemand value (" + value2[i] + ") is not a number.";
		}
		else {
			dvalue2[i] = StringUtil.atod(value2[i]);
		}
	}
	DataSetComponent comp_demts = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY );
	if ( TSUtil.indexOf((List)comp_demts.getData(),new_id,"Location",1) >= 0 ) {
		warning += "\nDemand time series identifier (" + new_id + ") is already in use.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Add a river node...

	helperAddRiverNode ( new_id, new_name );

	// Create a new well...

	StateMod_Well well = new StateMod_Well();
	well.setID ( new_id );
	well.setName ( new_name );
	well.setCgoto ( new_id ); // River node ID assumed to be the same as the common ID
	DataSetComponent comp = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_WELL_STATIONS );
	List welldata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( welldata_Vector.size() == 0 ) {
			welldata_Vector.add ( well );
		}
		else {
			welldata_Vector.add ( 0, well );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		welldata_Vector.add ( well );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = welldata_Vector.size();
		StateMod_Well well2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			well2 = (StateMod_Well)welldata_Vector.get(i);
			if ( new_id.compareTo(well2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				welldata_Vector.add(i,well);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			welldata_Vector.add(well);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		welldata_Vector.add(StateMod_Util.indexOf(welldata_Vector,before), well );
	}
	comp.setDirty ( true );

	// Assign a default water right...

	StateMod_WellRight new_right = new StateMod_WellRight();
	new_right.setID ( new_id + ".01" ); // Same as the well + ".01"
	new_right.setName ( new_name );	// Same as the well
	new_right.setCgoto ( new_id );	// Same as the well

	// Always insert alphabetically.  This should hopefully get the right near the well...
	comp =	__dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_RIGHTS );
	List wellrights_Vector = (List)comp.getData();
	int size = wellrights_Vector.size();
	StateMod_WellRight wellright;
	boolean found = false;
	for ( int i = 0; i < size; i++ ) {
		wellright = (StateMod_WellRight)wellrights_Vector.get(i);
		if ( new_id.compareTo(wellright.getID()) < 0 ) {
			// Current ID is less so add before the item being compared.
			wellrights_Vector.add(i,new_right);
			found = true;
			break;
		}
	}
	if ( !found ) {
		// Current item was not later than any in list so add as last item...
		wellrights_Vector.add(new_right);
	}
	comp.setDirty ( true );
	// Connect the rights...
	well.connectRights ( wellrights_Vector );

	// Assign a default return flow pattern (single return to downstream
	// node).  Note that the return is specified for the river node.

	String delay_table = __delay_table1_JComboBox.getSelected();
	String downstream_node = StringUtil.getToken(__downstream_nodes_JComboBox.getSelected()," -", 0, 0 );
	if ( !delay_table.equals(__NONE) && !downstream_node.equals(__NONE) ) {
		StateMod_ReturnFlow new_return = new StateMod_ReturnFlow (StateMod_DataSet.COMP_WELL_STATIONS);
		new_return.setCrtnid ( downstream_node );
		well.addReturnFlow ( new_return );
	}

	// Assign a default depletion pattern (single depletion of downstream
	// node).  Note that the return is specified for the river node.

	String delay_table2 = __delay_table2_JComboBox.getSelected();
	if ( !delay_table2.equals(__NONE) && !downstream_node.equals(__NONE) ) {
		StateMod_ReturnFlow new_return = new StateMod_ReturnFlow ( StateMod_DataSet.COMP_WELL_STATIONS);
		new_return.setCrtnid ( downstream_node );
		well.addDepletion ( new_return );
	}

	// Define a monthly historical pumping time series.

	TS ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY),
		"ACFT", year1, year2, dvalue1 );
	well.setPumpingMonthTS ( (MonthTS)ts );

	// Demand time series (monthly)...

	ts = addTimeSeriesToComponent (	
		__dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY),
		new_id, new_name,
		StateMod_DataSet.lookupTimeSeriesDataType (StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY),
		"ACFT", year1, year2, dvalue2 );
	well.setDemandMonthTS ( (MonthTS)ts );

	// Close this dialog...

	dispose();

	// Refresh the well window so the user can check the new data...

	StateMod_Well_JFrame well_JFrame = (StateMod_Well_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_WELL, true );

	// Select the specific identifier...

	well_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The well " + StateMod_Util.createDataLabel ( well, true ) + " has been added.\n" +
		"The following important defaults have been set and can be changed:\n" +
		"  * Capacity of 0.0 CFS.\n" +
		"  * Historical pumping time series (monthly) specified by user.\n" +
		"  * Demand time series (monthly) values specified by user.\n" +
		"  * Efficiencies set to 60% and monthly efficiencies will be used.\n" +
		"  * Estimate daily data from monthly time series.\n" );
	if ( delay_table.equals (__NONE) ) {
		info.append ( "  * No return flow assignment.\n" );
	}
	else {
		info.append ( "  * One monthly return flow assignment (100% to immediate downstream node).\n" );
	}
	if ( delay_table2.equals (__NONE) ) {
		info.append ( "  * No depletion assignment.\n" );
	}
	else {
		info.append ( "  * One depletion assignment (100% to immediate downstream node).\n" );
	}
	info.append (
		"  * Added one well right with zero decree and 99999 administration number.\n\n" +
		"You should use the Wells window to modify other defaults, in particular:\n" +
		"  * Verify the main data.\n" +
		"  * Verify the water rights.\n" +
		"  * Verify the return flow locations.\n" +
		"  * Verify the depletion locations.\n" );
	new ResponseJDialog(well_JFrame, "New Well", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree well data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_WELL_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( well_JFrame != null ) {
		well_JFrame.toFront();
	}

	if (__network != null) {
		__network.addNode(new_id, HydrologyNode.NODE_TYPE_WELL,
			determineUpstreamNode(), determineDownstreamNode(), false, false);
	}			
}

/**
Browse for a file/directory.  Currently this is only used when creating a new data set.
*/
private void browse ()
{	String lastDirectorySelected = JGUIUtil.getLastFileDialogDirectory();
	JFileChooser fc = JFileChooserFactory.createJFileChooser ( lastDirectorySelected );

	fc.setDialogTitle("Select StateMod Data Set Directory");

	//fc.setAcceptAllFileFilterUsed(true);
	fc.setFileSelectionMode ( JFileChooser.DIRECTORIES_ONLY );
	fc.setDialogType(JFileChooser.OPEN_DIALOG);	
	
	JGUIUtil.setWaitCursor(this, false);
	int retVal = fc.showOpenDialog(this);
	if (retVal != JFileChooser.APPROVE_OPTION) {
		return;
	}

	IOUtil.setProgramWorkingDir ( fc.getSelectedFile().getParent() );
	JGUIUtil.setLastFileDialogDirectory ( fc.getSelectedFile().getParent());

	// Set the directory in the text field...

	__dataset_dir_JTextField.setText ( fc.getSelectedFile().getPath() );
}

/**
Convert an existing node to a stream estimate station.
*/
private void convertStreamEstimate()
{	String routine = "StateModGUI_AddComponent_JDialog.convertStreamEstimate";
	String new_id = StringUtil.getToken( __downstream_nodes_JComboBox.getSelected(), " -", 0, 0 );
	String new_name = StringUtil.getToken( __downstream_nodes_JComboBox.getSelected(), " -", 0, 1 );

	// Verify data (anything to check?)...

	String warning = "";
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	StateMod_StreamEstimate sta = new StateMod_StreamEstimate();
	sta.setID ( new_id );
	sta.setName ( new_name );
	sta.setCgoto ( new_id );	// River node ID assumed to be the same as the common ID
	DataSetComponent comp = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS );
	List stadata_Vector = (List)comp.getData();
	String insertpos = __insertpos_JComboBox.getSelected();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( stadata_Vector.size() == 0 ) {
			stadata_Vector.add ( sta );
		}
		else {
			stadata_Vector.add ( 0, sta );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		stadata_Vector.add ( sta );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = stadata_Vector.size();
		StateMod_StreamEstimate sta2;
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			sta2 = (StateMod_StreamEstimate)stadata_Vector.get(i);
			if ( new_id.compareTo(sta2.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				stadata_Vector.add(i,sta);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			stadata_Vector.add(sta);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		stadata_Vector.add(StateMod_Util.indexOf(stadata_Vector,before), sta );
	}
	comp.setDirty ( true );

	// Add the stream estimate coefficients...

	StateMod_StreamEstimate_Coefficients coef = new StateMod_StreamEstimate_Coefficients ();
	coef.setID ( new_id );
	coef.setFlowX ( new_id );
	coef.setName ( new_name );
	DataSetComponent comp2 = __dataset.getComponentForComponentType (
		StateMod_DataSet.COMP_STREAMESTIMATE_COEFFICIENTS );
	List coef_Vector = (List)comp2.getData();
	if ( insertpos.equals(__INSERT_FIRST) ) {
		if ( coef_Vector.size() == 0 ) {
			coef_Vector.add ( coef );
		}
		else {
			coef_Vector.add ( 0, coef );
		}
	}
	else if ( insertpos.equals(__INSERT_LAST) ) {
		coef_Vector.add ( coef );
	}
	else if ( insertpos.equals(__INSERT_ALPHABETICAL_ID) ) {
		int size = coef_Vector.size();
		boolean found = false;
		for ( int i = 0; i < size; i++ ) {
			if ( new_id.compareTo(coef.getID()) < 0 ) {
				// Current ID is less so add before the item being compared.
				coef_Vector.add(i,coef);
				found = true;
				break;
			}
		}
		if ( !found ) {
			// Current item was not later than any in list so add as last item...
			coef_Vector.add(coef);
		}
	}
	else if ( insertpos.startsWith(__INSERT_BEFORE) ) {
		// Find the ID to insert before...
		String before = StringUtil.getToken ( insertpos, " ", StringUtil.DELIM_ALLOW_STRINGS, 1 );
		coef_Vector.add(StateMod_Util.indexOf(coef_Vector,before), coef );
	}
	comp2.setDirty ( true );

	// Close this dialog...

	dispose();

	// Refresh the stream estimate stations window so the user can check the new data...

	StateMod_StreamEstimate_JFrame sta_JFrame = (StateMod_StreamEstimate_JFrame)
		__dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_STREAMESTIMATE, true );

	// Select the specific identifier...

	sta_JFrame.selectID ( new_id );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The existing node " + new_id + " has been converted to a stream estimate station.\n" +
		"You should use the Stream Estimate Stations window to modify the defaults,\n" +
		"in particular the stream estimate coefficients (none are assigned by default).\n" +
		"\n" +
		"Developing stream estimate coefficients requires skill and" +
		" external software/analysis may be needed.\n" +
		"Refer to the StateMod documentation for more information." );
	new ResponseJDialog(sta_JFrame, "New Stream Estimate Station",
		info.toString(), ResponseJDialog.OK);

	// Refresh the JTree stream estimate data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_STREAMESTIMATE_GROUP );
	
	// Bring the appropriate data window to the front after closing the dialog...

	if ( sta_JFrame != null ) {
		sta_JFrame.toFront();
	}
}

/**
Clean up before garbage collection.
*/
protected void finalize ()
throws Throwable
{	__new_id_JTextField = null;
	__new_name_JTextField = null;
	__downstream_nodes_JComboBox = null;
	__upstream_nodes_JComboBox = null;

	__add_JButton = null;
	__help_JButton = null;
	__cancel_JButton = null;
	super.finalize();
}

/**
Helper method to add a new river node.  This is called when adding a new
diversion, reservoir, etc. and modifies the river network.
@param new_id New node identifier.
@param new_name New node name.
*/
private void helperAddRiverNode ( String new_id, String new_name )
{	// Create and add the new river node...

	StateMod_RiverNetworkNode new_node = new StateMod_RiverNetworkNode ();
	new_node.setID ( new_id );	// Same as reservoir
	new_node.setName ( new_name );
	new_node.setCstadn ( StringUtil.getToken(
		__downstream_nodes_JComboBox.getSelected(), "- ", 0, 0) );
	// Comments are not set.

	// Add network node to river network data...

	DataSetComponent comp = __dataset.getComponentForComponentType(StateMod_DataSet.COMP_RIVER_NETWORK );
	List netdata_Vector = (List)comp.getData();
	String downstream_node = StringUtil.getToken(__downstream_nodes_JComboBox.getSelected(), " -", 0, 0 );
	if ( downstream_node.equals(__NONE) ) {
		// Add as the first item...
		netdata_Vector.add ( 0, new_node );
	}
	else {
		// Add network node to river network data, before the
		// downstream node ("NONE" is in position 0).
		netdata_Vector.add (__downstream_nodes_JComboBox.getSelectedIndex() - 1, new_node);
	}
	comp.setDirty ( true );

	// Modify upstream node, if specified, to connect to the new node...

	String upstream_node_string = __upstream_nodes_JComboBox.getSelected();
	if ( !upstream_node_string.equals(__NONE) ) {
		// Need to set the downstream node on the upstream node...
		upstream_node_string = StringUtil.getToken (upstream_node_string, " -", 0, 0 );
		int index = StateMod_Util.indexOf ( netdata_Vector,upstream_node_string );
		if ( index >= 0 ) {
			((StateMod_RiverNetworkNode)netdata_Vector.get(index)).setCstadn ( new_id );
		}
	}

	// Refresh the network window...

	__dataset_wm.refreshWindow (
		StateMod_DataSet_WindowManager.WINDOW_RIVER_NETWORK, false );
}

/*
Handle item (list) events.  For example, when the downstream node is selected,
this causes the available upstream nodes to be listed as choices.
@param e ItemEvent to handle.
*/
public void itemStateChanged ( ItemEvent e )
{	Object o = e.getSource();
	if ( o == __downstream_nodes_JComboBox ) {
		if ( __upstream_nodes_JComboBox != null ) {
			refreshUpstreamNodes();
		}
	}
}

/**
Create a new StateMod data set.
*/
private void newDataSet()
{	String routine = "StateModGUI_AddComponent_JDialog.newDataSet";
	String basename = __initial_value1_JTextField.getText().trim();
	String dataset_dir = __dataset_dir_JTextField.getText().trim();
	String year_type = StringUtil.getToken ( __year_type_JComboBox.getSelected(), " -", 0, 0 );
	String year1 = __year1_JTextField.getText().trim();
	String year2 = __year2_JTextField.getText().trim();

	// Verify data...

	String warning = "";
	if ( basename.length() == 0 ) {
		warning += "\nThe base name must be specified.";
	}
	/* TODO Any restriction?
	if ( new_id.length() > 12 ) {
		warning += "\nThe base name is > 12 characters.";
	}
	*/
	if ( basename.indexOf(' ') >= 0 ) {
		warning += "\nThe base name cannot contain spaces.";
	}
	if ( !IOUtil.fileExists(dataset_dir) ) {
		warning += "\nThe data set directory does not exist.";
	}
	if ( !StringUtil.isInteger(year1) ) {
		warning += "\nThe start year is not a number.";
	}
	if ( !StringUtil.isInteger(year2) ) {
		warning += "\nThe end year is not a number.";
	}
	if ( StringUtil.isInteger(year1) && StringUtil.isInteger(year2) &&
		StringUtil.atoi(year2) < StringUtil.atoi(year1) ) {
		warning += "\nThe start year is greater than the end year.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Define a new data set and initialize using the base name.  Initialize
	// all file names, even though some may not be used...

	StateMod_DataSet new_dataset = new StateMod_DataSet ();
	// Initialize data file names, based on the basename...
	new_dataset.setBaseName ( basename );
	new_dataset.initializeDataFileNames();
	new_dataset.setDataSetDirectory ( dataset_dir );
	new_dataset.setIystr ( year1 );
	new_dataset.setIyend ( year2 );
	new_dataset.setCyrl ( year_type );
	// Hide components that are not used, based on the control data...
	new_dataset.checkComponentVisibility();

	// Mark the response and control data dirty...

	DataSetComponent comp = new_dataset.getComponentForComponentType ( StateMod_DataSet.COMP_RESPONSE );
	comp.setDirty ( true );
	comp = new_dataset.getComponentForComponentType (StateMod_DataSet.COMP_CONTROL );
	comp.setDirty ( true );

	// Add an end river node to the network...

	comp = new_dataset.getComponentForComponentType (StateMod_DataSet.COMP_RIVER_NETWORK );
	List data = (List)comp.getData();
	StateMod_RiverNetworkNode net = new StateMod_RiverNetworkNode ();
	// Use the first 8 characters of the base name...
	String endid = basename + "_end";
	if ( endid.length() > 12 ) {
		endid = basename.substring(0,8) + "_end";
	}
	net.setID ( endid );
	net.setName ( "END" );
	data.add ( net );
	comp.setDirty ( true );

	// Add a default monthly and daily delay table...

	StateMod_DelayTable delay = new StateMod_DelayTable ( true );
	delay.setID ( "1" );
	delay.addRet_val ( 100.0 );
	comp = new_dataset.getComponentForComponentType (StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY );
	List delaydata_Vector = (List)comp.getData();
	delaydata_Vector.add ( delay );
	comp.setDirty ( true );

	delay = new StateMod_DelayTable ( false );
	delay.setID ( "1" );
	delay.addRet_val ( 100.0 );
	comp = new_dataset.getComponentForComponentType (StateMod_DataSet.COMP_DELAY_TABLES_DAILY );
	delaydata_Vector = (List)comp.getData();
	delaydata_Vector.add ( delay );
	comp.setDirty ( true );

	// Close this dialog...

	dispose();

	// Set the new data set in the main interface - this will cause the main interface to refresh.

	__parent.setNewDataSet ( new_dataset );

	// Refresh the stream estimate stations window so the user can check the new data...

	StateMod_Response_JFrame rsp_JFrame = (StateMod_Response_JFrame)
		__parent.getDataSetWindowManager().refreshWindow (
		StateMod_DataSet_WindowManager.WINDOW_RESPONSE, true );

	// Tell the user that they can make further edits...

	StringBuffer info = new StringBuffer (
		"The StateMod data set " +
		new_dataset.getBaseName() + " has been created with the following defaults:\n" +
		"* A single node (" +endid+") has been added to the network.\n"+
		"* A single monthly delay table has been added, returning 100% in the first interval.\n" +
		"* A single daily delay table has been added, returning 100% in the first interval.\n" +
		"* The year type and run period have been specified by the user.\n" +
		"* Control information has been defaulted to a monthly data set with no advanced features.\n" +
		"\n" +
		"You should use the Response File window to modify data component file names,\n" +
		"which have been defaulted using the base name and standard file extensions.\n" +
		"\n" +
		"You should use the Data...Control...Control menu to change important control parameters.\n" +
		"Refer to the StateMod documentation for more information.\n" +
		"\n" +
		"The data set files will not be saved until the File...Save menu is selected." );
	new ResponseJDialog(rsp_JFrame, "New StateMod Data Set", info.toString(), ResponseJDialog.OK);

	// Refresh the JTree...

	// Bring the appropriate data window to the front after closing the dialog...

	if ( rsp_JFrame != null ) {
		rsp_JFrame.toFront();
	}
}

/**
Refresh the list of upstream nodes based on what was selected in the downstream node.
*/
private void refreshUpstreamNodes()
{	// Downstream node is selected so pick the upstream node to insert between.
	if ( __upstream_nodes_JComboBox == null ) {
		// GUI is still starting up...
		return;
	}
	int downstream_nodes_index = __downstream_nodes_JComboBox.getSelectedIndex();
	List v = StateMod_Util.getUpstreamNetworkNodes ( 
		(List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData(),
		StringUtil.getToken (__downstream_nodes_JComboBox.getSelected(), " -", 0,0));
	
	int size = v.size();
	__upstream_nodes_JComboBox.removeAll();

	// Add one that is empty so that the user can add a headwater node at any time...

	__upstream_nodes_JComboBox.add ( __NONE );

	// Add additional nodes for available upstream nodes...

	if ( downstream_nodes_index != 0 ) {
		// A valid node is selected...
		for ( int i = 0; i < size; i++ ) {
			__upstream_nodes_JComboBox.add (StateMod_Util.createDataLabel((StateMod_Data)v.get(i), true ) );	
		}
	}
}

/**
Set up the add position GUI components.
*/
private int setupAddPosition (	JPanel main_JPanel, int y, GridBagConstraints gbc )
{	JGUIUtil.addComponent (	main_JPanel,
		new JLabel( "Add Position:"),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__insertpos_JComboBox = new SimpleJComboBox ();
	__insertpos_JComboBox.addItem ( __INSERT_FIRST );
	__insertpos_JComboBox.addItem ( __INSERT_LAST );
	__insertpos_JComboBox.addItem ( __INSERT_ALPHABETICAL_ID );
	__insertpos_JComboBox.select ( __INSERT_ALPHABETICAL_ID );
	/* Not needed?
	DataSetComponent comp = __dataset.getComponentForComponentType ( __comp_type );
	List data;
	int size;
	if ( (__comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY)){
		// Operate on TS...
		data = (List)comp.getData();
		size = data.size();
		TS ts = null;
		for ( int i = 0; i < size; i++ ) {
			ts = (TS)data.elementAt(i);
			__insertpos_JComboBox.addItem ( __INSERT_BEFORE + " \"" + ts.getLocation() + "\"" );
		}
	}
	else {
		// Operate on StateMod_Data...
		comp = __dataset.getComponentForComponentType( __comp_type );
		data = (List)comp.getData();
		size = data.size();
		StateMod_Data smdata;
		for ( int i = 0; i < size; i++ ) {
			smdata = (StateMod_Data)data.elementAt(i);
			__insertpos_JComboBox.addItem ( __INSERT_BEFORE +
				" \"" + StateMod_Util.formatDataLabel(smdata.getID(), smdata.getName() ) + "\"" );
		}
	}
	*/
	JGUIUtil.addComponent ( main_JPanel, __insertpos_JComboBox,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the data set directory browse GUI components.
*/
private int setupDataSetDirectory (	JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (main_JPanel,new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__dataset_dir_JTextField = new JTextField ("",40);
	JGUIUtil.addComponent ( main_JPanel,__dataset_dir_JTextField,
		1, y, 2, 1, 1.0, 0.0,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	__browse_JButton = new SimpleJButton ( "Browse", this );
	JGUIUtil.addComponent ( main_JPanel,__browse_JButton,
		3, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the data units GUI components.
*/
private int setupDataUnits ( JPanel main_JPanel, int y, GridBagConstraints gbc )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel( "Data units:"),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__units_JComboBox = new SimpleJComboBox ();
	__units_JComboBox.addItem ( "FT" );
	__units_JComboBox.addItem ( "IN" );
	__units_JComboBox.select( "IN" );
	JGUIUtil.addComponent ( main_JPanel, __units_JComboBox,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	DataSetComponent comp = __dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY );
	// If existing time series are in the component, set the units
	// to the same - cannot mix units because the StateMod time
	// series file can only handle one set of units...
	if ( comp.hasData() ) {
		List data = (List)comp.getData();
		if ( data.size() > 0 ) {
			TS ts = (TS)data.get(0);
			String units = ts.getDataUnits();
			if ( units.equalsIgnoreCase("FT") || units.equalsIgnoreCase("FEET") ) {
				__units_JComboBox.select("FT");
				// setEditable does not work...
				__units_JComboBox.setEnabled ( false );
			}
			else if(units.equalsIgnoreCase("IN") || units.equalsIgnoreCase("INCH") ) {
				__units_JComboBox.select("IN");
				// setEditable does not work...
				__units_JComboBox.setEnabled ( false );
			}
		}
	}
	return y;
}

/**
Set up the delay table GUI components.
*/
private int setupDelayTable ( JPanel main_JPanel, int y, GridBagConstraints gbc, int pos, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	if ( pos == 1 ) {
		__delay_table1_JComboBox = new SimpleJComboBox ();
		__delay_table1_JComboBox.addItemListener ( this );
	}
	else if ( pos == 2 ) {
		__delay_table2_JComboBox = new SimpleJComboBox ();
		__delay_table2_JComboBox.addItemListener ( this );
	}
	DataSetComponent comp = null;
	if ( __dataset.getIday() == 1 ) {
		// Daily delay tables...
		comp = __dataset.getComponentForComponentType( StateMod_DataSet.COMP_DELAY_TABLES_DAILY );
	}
	else {
		// Monthly delay tables...
		comp = __dataset.getComponentForComponentType( StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY );
	}
	List data_list = StateMod_Util.createIdentifierList((List)(comp.getData()), true );
	int size = data_list.size();
	if ( pos == 1 ) {
		__delay_table1_JComboBox.addItem ( __NONE );
		for ( int i = 0; i < size; i++ ) {
			__delay_table1_JComboBox.addItem ((String)data_list.get(i));
		}
		JGUIUtil.addComponent ( main_JPanel, __delay_table1_JComboBox,
			1, y, 1, 1, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.WEST );
	}
	else {	__delay_table2_JComboBox.addItem ( __NONE );
		for ( int i = 0; i < size; i++ ) {
			__delay_table2_JComboBox.addItem ((String)data_list.get(i));
		}
		JGUIUtil.addComponent ( main_JPanel, __delay_table2_JComboBox,
			1, y, 1, 1, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.WEST );
	}
	return y;
}

/**
Set up the downstream node GUI components.
*/
private int setupDownstreamNodes ( JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	List net_list = (List)(__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RIVER_NETWORK)).getData();
	if ( (__comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS)&& (__newnode == false) ) {
		// Converting an existing node to a stream estimate node.
		// Remove nodes that are already in the stream estimate station file...
		DataSetComponent comp1 = __dataset.getComponentForComponentType(
			StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS );
		List data1 = (List)comp1.getData();
		DataSetComponent comp2 = __dataset.getComponentForComponentType(
			StateMod_DataSet.COMP_STREAMGAGE_STATIONS );
		List data2 = (List)comp2.getData();
		int size = net_list.size();
		String id;
		StateMod_Data smdata;
		for ( int i = 0; i < size; i++ ) {
			smdata = (StateMod_Data)net_list.get(i);
			id = smdata.getID();
			if ( StateMod_Util.indexOf(data1,id) >= 0 ) {
				// In the stream estimate stations...
				net_list.remove(i);
				--size;
			}
			else if ( StateMod_Util.indexOf(data2,id) >= 0 ) {
				// In the stream gage stations...
				net_list.remove(i);
				--size;
			}
		}
	}
	// Now create the list for the combo box...
	List data_list = StateMod_Util.createIdentifierList( net_list, true );
	__downstream_nodes_JComboBox = new SimpleJComboBox ();
	__downstream_nodes_JComboBox.addItem ( __NONE );
	int size = data_list.size();
	for ( int i = 0; i < size; i++ ) {
		__downstream_nodes_JComboBox.addItem ((String)data_list.get(i) );
	}
	__downstream_nodes_JComboBox.addItemListener ( this );
	JGUIUtil.addComponent (main_JPanel,__downstream_nodes_JComboBox,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the ID GUI components.
*/
private int setupID ( JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__new_id_JTextField = new JTextField ( "", 15 );
	JGUIUtil.addComponent ( main_JPanel, __new_id_JTextField,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the initial monthly data values.
*/
private int setupInitialTSValues ( JPanel main_JPanel, int y, GridBagLayout gb,
	GridBagConstraints gbc, int pos, String label )
{	JGUIUtil.addComponent (main_JPanel,new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	if ( pos == 1 ) {
		__initial_tsvalue1_JTextField = new JTextField[12];
	}
	else if ( pos == 2 ) {
		__initial_tsvalue2_JTextField = new JTextField[12];
	}
	else if ( pos == 3 ) {
		__initial_tsvalue3_JTextField = new JTextField[12];
	}
	// Put these in a separate panel to simplify layouts...
	JPanel value_JPanel = new JPanel();
	value_JPanel.setLayout ( gb );
	for ( int i = 0; i < 12; i++ ) {
		if ( pos == 1 ) {
			__initial_tsvalue1_JTextField[i]=new JTextField ("0",4);
			JGUIUtil.addComponent ( value_JPanel,
				__initial_tsvalue1_JTextField[i],
				i, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST );
		}
		else if ( pos == 2 ) {
			__initial_tsvalue2_JTextField[i]=new JTextField ("0",4);
			JGUIUtil.addComponent ( value_JPanel,
				__initial_tsvalue2_JTextField[i],
				i, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST );
		}
		else if ( pos == 3 ) {
			__initial_tsvalue3_JTextField[i]=new JTextField ("0",4);
			JGUIUtil.addComponent ( value_JPanel,
				__initial_tsvalue3_JTextField[i],
				i, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NONE, GridBagConstraints.WEST );
		}
	}
	JGUIUtil.addComponent ( main_JPanel, value_JPanel,
		1, y, 5, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the initial data value.
*/
private int setupInitialValue (	JPanel main_JPanel, int y, GridBagConstraints gbc, int pos,
	String initial_value, String label )
{	JGUIUtil.addComponent (main_JPanel,new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	if ( pos == 1 ) {
		__initial_value1_JTextField = new JTextField (initial_value,15);
		JGUIUtil.addComponent ( main_JPanel,__initial_value1_JTextField,
			1, y, 2, 1, 1.0, 0.0,
			GridBagConstraints.NONE, GridBagConstraints.WEST );
	}
	else if ( pos == 2 ) {
		__initial_value2_JTextField = new JTextField (initial_value,15);
		JGUIUtil.addComponent ( main_JPanel,__initial_value2_JTextField,
			1, y, 2, 1, 1.0, 0.0,
			GridBagConstraints.NONE, GridBagConstraints.WEST );
	}
	return y;
}

/**
Set up the name GUI components.
*/
private int setupName (	JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__new_name_JTextField = new JTextField ( "", 15 );
	JGUIUtil.addComponent ( main_JPanel, __new_name_JTextField,
		1, y, 2, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the notes GUI components.
*/
private int setupNotes ( JPanel main_JPanel, int y, GridBagConstraints gbc, String [] notes )
{	for ( int i = 0; i < notes.length; i++ ) {
		JGUIUtil.addComponent ( main_JPanel, new JLabel(notes[i]),
			0, ++y, 5, 1, 1.0, 0.0,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	}
	return y;
}

/**
Set up the list of available operational rights.
*/
private int setupOperationalRights ( JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel( label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	// First one indicates unrecognized.
	List<StateMod_OperationalRight_Metadata> operationalRightMetadataList =
		StateMod_OperationalRight_Metadata.getAllMetadata();
	__new_oprights_type_JComboBox = new SimpleJComboBox();
	for ( int i = 0; i < operationalRightMetadataList.size(); i++ ) {
		__new_oprights_type_JComboBox.add ( "" + i + " - " +
			operationalRightMetadataList.get(i).getRightTypeName() );	
	}
	__new_oprights_type_JComboBox.select ( 0 );
	JGUIUtil.addComponent ( main_JPanel,
		__new_oprights_type_JComboBox,
		1, y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the primary data list GUI components.  For example, this is used to list
the stream gages when one will be selected for a new time series.
@param comp_type1 The component to be used to generate the list.
@param comp_type2 A second component to be used to generate the list, currently
only used for well stations.
*/
private int setupPrimaryDataList ( JPanel main_JPanel, int y, GridBagConstraints gbc, String label,
	int comp_type1, int comp_type2 )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	List data_list = (List)(__dataset.getComponentForComponentType(comp_type1)).getData();
	List choices = StateMod_Util.createIdentifierList(
		(List)__dataset.getComponentForComponentType(comp_type1).getData(),true);
	int size = choices.size();
	__primary_data_JComboBox = new SimpleJComboBox ();
	for ( int i = 0; i < size; i++ ) {
		__primary_data_JComboBox.addItem ((String)choices.get(i) );
	}
	// If comp_type2 is well stations, add wells that are well-only and
	// do not refer to a diversion...
	if ( comp_type2 == StateMod_DataSet.COMP_WELL_STATIONS ) {
		data_list = (List)(__dataset.getComponentForComponentType(comp_type2)).getData();
		StateMod_Well well;
		size = data_list.size();
		for ( int i = 0; i < size; i++ ) {
			well = (StateMod_Well)data_list.get(i);
			if (StateMod_Util.isMissing(well.getIdvcow2()) ||
				well.getIdvcow2().equalsIgnoreCase("N/A") ||
				well.getIdvcow2().equalsIgnoreCase("NA")) {
				__primary_data_JComboBox.addItem (
					StateMod_Util.createDataLabel(well,true) + " - " + __WELL_ONLY );
			}
		}
	}
	JGUIUtil.addComponent (main_JPanel,__primary_data_JComboBox,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the upstream nodes GUI components.
*/
private int setupUpstreamNodes ( JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__upstream_nodes_JComboBox = new SimpleJComboBox ();
	__upstream_nodes_JComboBox.addItemListener ( this );
	JGUIUtil.addComponent ( main_JPanel, __upstream_nodes_JComboBox,
		1, y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Setup the start and end year GUI components.  The default years shown are taken
from available data in the data set and the control file years.
*/
private int setupYear1Year2 ( JPanel main_JPanel, int y, GridBagConstraints gbc )
{	String yeartype = " (Calendar Year - Jan...Dec)";
	if ( __comp_type == StateMod_DataSet.COMP_RESPONSE ) {
		yeartype = "";
	}
	else {
		if ( __dataset.getCyrl() == YearType.WATER ) {
			yeartype = " (Water Year - Oct...Sep)";
		}
		else if ( __dataset.getCyrl() == YearType.NOV_TO_OCT ) {
			yeartype = " (Irrigation Year - Nov...Oct)";
		}
	}
	JGUIUtil.addComponent (	main_JPanel, new JLabel(
		"Start year" + yeartype + ":"),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__year1_JTextField = new JTextField ( "", 15 );
	DateTime earliest = null;
	if ( __dataset != null ) {
		earliest = StateMod_Util.findEarliestDateInPOR ( __dataset );
	}
	if ( earliest != null ) {
		// Use the date...
		__year1_JTextField.setText ( "" + earliest.getYear() );
	}
	JGUIUtil.addComponent ( main_JPanel, __year1_JTextField,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	JGUIUtil.addComponent (	main_JPanel, new JLabel( "End year" +
		yeartype + ":"),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__year2_JTextField = new JTextField ( "", 15 );
	DateTime latest = null;
	if ( __dataset != null ) {
		latest = StateMod_Util.findLatestDateInPOR ( __dataset );
	}
	if ( latest != null ) {
		// Use the date...
		__year2_JTextField.setText ( "" + latest.getYear() );
	}
	JGUIUtil.addComponent ( main_JPanel, __year2_JTextField,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

/**
Set up the year type, used when creating a new data set.
*/
private int setupYearType (	JPanel main_JPanel, int y, GridBagConstraints gbc )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel( "Year type:"),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__year_type_JComboBox = new SimpleJComboBox ();
	__year_type_JComboBox.addItem ( "CYR - Calendar Year (Jan - Dec)" );
	__year_type_JComboBox.addItem ( "IYR - Irrigation Year (Nov - Oct)" );
	__year_type_JComboBox.addItem ( "WYR - Water Year (Oct - Sep)" );
	__year_type_JComboBox.select( 0 );
	JGUIUtil.addComponent ( main_JPanel, __year_type_JComboBox,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	return y;
}

private StateMod_Network_JFrame __network = null;

private String determineDownstreamNode() {
	String downstream_node = StringUtil.getToken(__downstream_nodes_JComboBox.getSelected()," -", 0, 0 );
	if (downstream_node.equals(__NONE)) {
		return null;
	}
	else {	
		return downstream_node;
	}
}

private String determineUpstreamNode() {
	String upstream_node = StringUtil.getToken(__upstream_nodes_JComboBox.getSelected(), " -", 0, 0 );
	if (upstream_node.equals(__NONE)) {
		return null;
	}
	else {
		return upstream_node;
	}
}

}
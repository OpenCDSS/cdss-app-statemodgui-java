// StateModGUI_JFrame - main window for StateModGUI

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

//------------------------------------------------------------------------------
// StateModGUI_JFrame - main window for StateModGUI
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 
// 20 Aug 1997	Catherine E.  		Created initial version of class.
//		Nutting-Lane, RTi	
// 17 Jan 1998	CEN, RTi		Adding improved look to interface, even
//					though functionality is still lacking.
// 18 Mar 1998	CEN, RTi		Changed log file name from "logfile" to
//					"smgui.log".
// 17 May 1999	CEN, RTi		In response to CRDSS MEMO(01Apr1999)
//					from Ray Bennett, changed "report" to
//					"view", moved run to its own menu,)) {
//					enabled add/delete menu and added
//					dialog warning if user tries to
//					add/delete but hasn't read in TS.
// 14 Jul 1999	CEN, RTi		Added GIS functionality.
// 08 Sep 1999	CEN, RTi		Adding legend to GIS.
// 25 Oct 1999	CEN, RTi		Added __templateLocation.
// 					Added baseflowx to run menu.
// 02 Mar 2001	Steven A. Malers, RTi	Update because SMRiverInfo.getAdnl* has
//					been removed.
// 01 Apr 2001	Steven A. Malers, RTi	Change GUI to JGUIUtil.  Add finalize().
//					Remove import *.
// 02 May 2001	SAM, RTi		Add call to DataUnits.readUnitsFile()to
//					be more standard.  Add StopWatch calls
//					to time total read/set time and time for
//					the individual parts.  Clean up messages
//					in read errors to not just print
//					exception at warning level 1.
// 18 Jul 2001	SAM, RTi		Change File...Select Scenario to
//					File...Open.  Catch exceptions running
//					StateMod.
// 2001-08-08	SAM, RTi		Change GeoLayer getUserType()to
//					getAppLayerType().
// 2001-08-13	SAM, RTi		Support response and control file as of
//					StateMod 9.92.  Put in check to make
//					sure file paths have parts that conform
//					to 8.3 conventions since StateMod does
//					not support long names.
// 2001-09-23	SAM, RTi		ESRIShapeFile moved to GeoView package.
//					where when in select mode zooming should
//					not occur.
// 2001-10-18	SAM, RTi		Update to use latest GeoView that is
//					being used with StateView/CWRAT - only
//					update to patch together - do not
//					actually use the GeoViewJPanel.  Do get
//					rid of the zoom history since it is not
//					part of the GeoView.  Use a GeoView
//					instead of the ReferenceGeoView.  Add
//					an Info choice similar to select and
//					zoom to figure out data.
// 2001-11-21	SAM, RTi		Use the generic HelpAboutDialog and
//					call the new URLHelp.initialize().
// 2001-12-11	SAM, RTi		Update the GeoView code to use the new
//					 naming convention.
// 2002-03-06	SAM, RTi		Update to use GeoViewCanvas instead of
//					GeoViewCanvas.
// 2002-04-29	SAM, RTi		Make warning messages for files 2 lines
//					to be easier to read.  Fix problem where
//					command line had extra spaces.
//					Add TSSupplier and new plotting features
//					to work with new TSProduct concept.
//					Change the Preferences menu to Tools.
//					Replace SMJMenuItem with SimpleJMenuItem
//					do the same thing and no need to have
//					redundancy.  Alphabetize methods - long
//					overdue.
// 2002-07-10	SAM, RTi		Start adding new GeoView map interface
//					in test mode to prepare for more
//					standard and advanced visualization.
// 2002-08-02	SAM, RTi		Implement new menu structure in test
//					mode.
// 2002-08-22	SAM, RTi		Update to 5.10.02.  Move new menu
//					structure to official (non-test)
//					version.  Add methods to the SMControls
//					class to simplify checks for daily data.
// 2002-09-19	SAM, RTi		Use isDirty() instead of setDirty() to
//					indicate edits.
// 2002-09-24	SAM, RTi		Move most of the code for adding a
//					summary layer to the GeoViewPanel class.
// 2002-10-07	SAM, RTi		Update linkSpatialAndStateModData() to
//					process streamflow stations.
// 2003-01-31	SAM, RTi		Make minor cosmetic changes while
//					finalizing the 05.11.00 release.
//					Stub out on-line documentation in an
//					generic way.
//------------------------------------------------------------------------------
// 2003-06-03	J. Thomas Sapienza, RTi	Began work on initial swing version.
// 2003-06-04	JTS, RTi		Continued work ...
// 2003-06-09	JTS, RTi		... work continued.
// 2003-06-10	JTS, RTi		Implemented the StateMod_DataSet 
//					* Began work opening the Reservoir
//					  window.
// 2003-06-11	JTS, RTi		* Began trimming out unnecessary stuff
//					  like the data vectors (they're now in
//					  the data set)
//					* Removed readInputData() (it's now in
//					  the data set)
// 2003-07-30	SAM, RTi		* Rename class from
//					  StateModGUI_Main_JFrame to
//					  StateModGUI_JFrame.
//					* Change COMP_STREAM_STATIONS to
//					  COMP_RIVER_STATIONS.
//					* Remove StateMod_DataSet_JTree_Node.
//					* Remove constructor user parameter -
//					  not needed.
//					* Reenable GeoView interface.
//					* Remove swap() and start phasing out
//					  invasive test code - need to move to
//					  production version.
//					* Rename menus to be more consistent
//					  with other code and reflect current
//					  data organization.
//					* Add checkGUIState() and updateStatus()
//					  methods, similar to StateDMI, to
//					  manage the GUI.  Delete disableMenus()
//					  since it is covered by
//					  checkGUIState().  Remove
//					  enableMenusBasedOnDataSetType()
//					  because StateMod has one data set
//					  type and checkGUIState() handles menu
//					  state.
//					* Remove the startup image - we need to
//					  move on and use GeoView, etc.!
//					* In actionPerformed(), check objects
//					  instead of strings and order the
//					  actions the same as the GUI.
// 2003-08-12	SAM, RTi		* Main displays now require the title
//					  to be passed in to constructor.
// 2003-08-13	SAM, RTi		* Remove the network tree - it will be
//					  awhile before that is enabled.
//					* Remove the old data vectors - they are
//					  now in the data sets.
//					* Alphabetize methods.
//					* Remove setupUnits() - this is handled
//					  in the StateModGUI class.
//					* Remove __basinName - the data set
//					  base name now stores this.
//					* Remove diagnostics window from managed
//					  windows - it takes care of itself.
//					* Enable basin summary window but move
//					  report generation to StateMod_DataSet
//					  and change name to Data Set Summary.
//					* Enable linkSpatialAndStateModData().
//					* Change displayDataWindow() to
//					  displayWindow().
// 2003-08-18	JTS, RTi		* Added the River data window.
// 2003-08-18	SAM, RTi		* Enable display for precipitation and
//					  evaporation data.
//					* Change checkDataAvailable() (old
//					  modifyBasinAllowed() to
//					  canEditDataSetComponent() (JTS change)
//					  and begin support for adding nodes.
// 2003-08-26	SAM, RTi		* Start using the
//					  StateMod_DataSet_WindowManager class
//					  to manage data windows.  Previously,
//					  some code was here and some was in
//					  StateMod_GUIUtil.
//					* Remove displayWindow() and
//					  displayDataSetSummary() - move them
//					  to StateMod_DataSet_WindowManager.
// 2003-08-27	JTS, RTi		* Convert code tfrom using
//					  StateMod_GUIUtil.displayWindow() to
//					  the method in 
//					  StateMod_DataSet_WindowManager.
// 2003-09-10	JTS, RTi		* Added Response GUI.
//					* Added Save Dialog that opens when
//					  the user chooses to open or quit
//					  and the current dataset is dirty.
// 2003-09-11	SAM, RTi		* Change based on changes in the
//					  StateMod package - chagnes to river
//					  station definitions.
//					* Update menus because of the above.
// 2003-09-18	SAM, RTi		* Make sure that the data menus are
//					  disabled when no data set has been
//					  opened - was not calling
//					  checkGUIState() after initial setup.
//					* Disable all the add/delete menus and
//					  start enabling them one by one.
//					* Add a Tools...Report - Unused Data"
//					  menu to help identify extra data in
//					  files.  Later may add a tool to
//					  remove.
//					* Start phasing in calls to
//					  updateStatus().
//					* Remove canEditDataSetComponent().
// 2003-10-08	SAM, RTi		* Use the new JFileChooser factory
//					  method to work around persistent
//					  problems with JFileChooser.
//					* Change updateStatus() to
//					  updateWindowStatus() as required by
//					  the StateMod_GUIUpdatable interface.
//					* Enable all add/delete menus for
//					  primary data components.
// 2003-10-20	SAM, RTi		* When deleting a stream estimate, just
//					  have one menu.  The code will
//					  determine whether the estimate is also
//					  another node and warn the user - this
//					  seems to work well in the old GUI.
//					* Add Edit...Add menus for all the
//					  sub-components.
// 2003-10-22	SAM, RTi		* Add File...New to add a new response
//					  file and data set.
//					* Add setDataSet() to allow the newly
//					  created data set to be recognized by
//					  the main interface.
//					* Enable running the model.
// 2003-10-29	SAM, RTi		* Take out TSSupplier code - it is not
//					  used.
// 2003-11-06	SAM, RTi		* Add Tools...
//					  Graph - Demand/Supply Summary.
// 2003-11-29	SAM, RTi		* Clean up for an official release.
// 2004-03-23	JTS, RTi		Added the network display JFrame.
// 2004-08-05	JTS, RTi		Added animation.
// 2004-08-05	JTS, RTi		Added reservoir teacup animation.
// 2004-08-12	JTS, RTi		Added addDifferenceMapLayer().
// 2004-08-25	JTS, RTi		* Commented out the help menu.
//					* Updated "About" to read that the
//					  App was developed with StateMod 
//					  version 10.34.
//					* Network file chooser not defaults to
//					  the XML Network version.
// 2004-10-25	SAM, RTi		* Move some Results and Tools menus
//					  around to reflect recent additions and
//					  be more consistent.
// 2005-04-11	JTS, RTi		* Changed to use a non-deprecated
//					  DiagnosticsJFrame constructor.
//					* Added the "New" submenu.
//					* Added support for creating a new
//					  network.
// 2005-04-12	JTS, RTi		Added code to generate network summaries
//					for debugging purposes.
// 2006-01-06	JTS, RTi		Update to version 06.03.01.
//					* Added a copyright to the 
//					  HelpAboutJDialog.
// 2006-01-20	SAM, RTi		* Move the New menu before Open since
//					  Open will be used most often.
//					* Change the Help About to indicate that
//					  development is using StateMod 11.06.
// 2006-02-28	SAM, RTi		Update for version 06.03.02.
//					* Add to the Help About the copyright
//					  information for JFreeChart.
//					* Clean up code based on documentation
//					  review, in particular for new network
//					  features.
// 2006-03-07	JTS, RTi		* For Phase 3, the menu items to delete
//					  delay tables have been disabled.
//					* Added this class as a window listener
//					  for all the Network JFrames.
// 2006-08-16	SAM, RTi		Update for version 06.04.00.
//					* Re-enable check for edits - was
//					  disabled in utility code for some
//					  reason.
// 2007-02-18	SAM, RTi		Update for versio 7.00.00.
//					* Clean up code based on Eclipse feedback.
//					* Update copyright to 2007.
//-----------------------------------------------------------------------------
// EndHeader

package DWR.StateModGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

//import DWR.DMI.HydroBaseDMI.HydrologyNode;
//import DWR.DMI.HydroBaseDMI.HydrologyNodeNetwork;
import cdss.domain.hydrology.network.HydrologyNode;

import DWR.StateCU.StateCU_DataSet;
import DWR.StateMod.StateMod_BTS;
import DWR.StateMod.StateMod_Data;
import DWR.StateMod.StateMod_DataSet;
import DWR.StateMod.StateMod_DataSet_JTree;
import DWR.StateMod.StateMod_DataSet_WindowManager;
import DWR.StateMod.StateMod_Diversion;
import DWR.StateMod.StateMod_Diversion_JFrame;
import DWR.StateMod.StateMod_GUIUpdatable;
import DWR.StateMod.StateMod_GUIUtil;
import DWR.StateMod.StateMod_GraphingTool_JFrame;
import DWR.StateMod.StateMod_InstreamFlow_JFrame;
import DWR.StateMod.StateMod_Network_JFrame;
import DWR.StateMod.StateMod_NodeNetwork;
import DWR.StateMod.StateMod_OutputControl_JFrame;
import DWR.StateMod.StateMod_OutputFiles_JFrame;
import DWR.StateMod.StateMod_Reservoir;
import DWR.StateMod.StateMod_Reservoir_JFrame;
import DWR.StateMod.StateMod_RiverNetworkNode;
import DWR.StateMod.StateMod_RunSmDelta_JFrame;
import DWR.StateMod.StateMod_RunReport_JFrame;
import DWR.StateMod.StateMod_Save_JDialog;
import DWR.StateMod.StateMod_StreamGage_JFrame;
import DWR.StateMod.StateMod_Util;
import DWR.StateMod.StateMod_Well_JFrame;
import RTi.GIS.GeoView.GeoLayer;
import RTi.GIS.GeoView.GeoLayerView;
import RTi.GIS.GeoView.GeoRecord;
import RTi.GIS.GeoView.GeoViewAnimationData;
import RTi.GIS.GeoView.GeoViewAnimationJFrame;
import RTi.GIS.GeoView.GeoViewAnimationLayerData;
import RTi.GIS.GeoView.GeoViewJPanel;
import RTi.GIS.GeoView.GeoViewListener;
import RTi.GIS.GeoView.HasGeoRecord;
import RTi.GR.GRLegend;
import RTi.GR.GRLimits;
import RTi.GR.GRPoint;
import RTi.GR.GRShape;
import RTi.GR.GRSymbol;
import RTi.GRTS.TSProduct;
import RTi.GRTS.TSViewJFrame;
import RTi.TS.MonthTS;
import RTi.TS.TS;
import RTi.TS.TSLimits;
import RTi.TS.TSUtil;
import RTi.TS.YearTS;
import RTi.Util.GUI.HelpAboutJDialog;
import RTi.Util.GUI.JFileChooserFactory;
import RTi.Util.GUI.JGUIUtil;
import RTi.Util.GUI.ReportJFrame;
import RTi.Util.GUI.ResponseJDialog;
import RTi.Util.GUI.SimpleFileFilter;
import RTi.Util.GUI.SimpleJMenuItem;
import RTi.Util.Help.URLHelp;
import RTi.Util.IO.DataSetComponent;
import RTi.Util.IO.IOUtil;
import RTi.Util.IO.ProcessListener;
import RTi.Util.IO.PropList;
import RTi.Util.Message.DiagnosticsJFrame;
import RTi.Util.Message.Message;
import RTi.Util.String.StringUtil;
import RTi.Util.Table.DataTable;
import RTi.Util.Table.TableField;
import RTi.Util.Table.TableRecord;
import RTi.Util.Time.DateTime;
import RTi.Util.Time.StopWatch;

@SuppressWarnings("serial")
public class StateModGUI_JFrame extends JFrame implements 
ActionListener, ComponentListener, GeoViewListener, ProcessListener,
StateMod_GUIUpdatable,	// Requires updateWindowStatus()
WindowListener
{

// Menu items are listed in the order that they appear in the interface.

// File menu items...

private JMenu __File_JMenu;
private JMenu __File_New_JMenu;
private JMenuItem __File_Open_JMenuItem;
private JMenuItem __File_New_Dataset_JMenuItem;
private JMenuItem __File_New_Network_JMenuItem;
private JMenuItem __File_Save_JMenuItem;
private JMenuItem __File_RemoveStateModOutputFiles_JMenuItem;
private JMenuItem __File_Exit_JMenuItem;

// Edit menu item...

private JMenu __Edit_JMenu;
private JMenu __Edit_Add_JMenu;
private JMenuItem __Edit_Add_StreamGageStation_JMenuItem;
private JMenuItem __Edit_Add_StreamGageHistoricalTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_StreamGageHistoricalTSDaily_JMenuItem;
private JMenuItem __Edit_Add_DelayTableMonthly_JMenuItem;
private JMenuItem __Edit_Add_DelayTableDaily_JMenuItem;
private JMenuItem __Edit_Add_Diversion_JMenuItem;
private JMenuItem __Edit_Add_DiversionHistoricalTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_DiversionHistoricalTSDaily_JMenuItem;
private JMenuItem __Edit_Add_DiversionDemandTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_DiversionDemandOverrideTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_DiversionDemandTSAverageMonthly_JMenuItem;
private JMenuItem __Edit_Add_DiversionDemandTSDaily_JMenuItem;
private JMenuItem __Edit_Add_DiversionIrrigationPracticeTSYearly_JMenuItem;
private JMenuItem __Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_JMenuItem;
private JMenuItem __Edit_Add_PrecipitationTS_JMenuItem;
private JMenuItem __Edit_Add_EvaporationTS_JMenuItem;
private JMenuItem __Edit_Add_Reservoir_JMenuItem;
private JMenuItem __Edit_Add_ReservoirContentTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_ReservoirContentTSDaily_JMenuItem;
private JMenuItem __Edit_Add_ReservoirTargetTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_ReservoirTargetTSDaily_JMenuItem;
private JMenuItem __Edit_Add_InstreamFlow_JMenuItem;
private JMenuItem __Edit_Add_InstreamFlowDemandTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_InstreamFlowDemandTSAverageMonthly_JMenuItem;
private JMenuItem __Edit_Add_InstreamFlowDemandTSDaily_JMenuItem;
private JMenuItem __Edit_Add_Well_JMenuItem;
private JMenuItem __Edit_Add_WellPumpingTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_WellPumpingTSDaily_JMenuItem;
private JMenuItem __Edit_Add_WellDemandTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_WellDemandTSDaily_JMenuItem;
private JMenuItem __Edit_Add_WellIrrigationPracticeTSYearly_JMenuItem;
private JMenuItem __Edit_Add_WellConsumptiveWaterRequirementTSMonthly_JMenuItem;
private JMenuItem __Edit_Add_WellConsumptiveWaterRequirementTSDaily_JMenuItem;
private JMenuItem __Edit_Add_StreamEstimateStationConvert_JMenuItem;
private JMenuItem __Edit_Add_StreamEstimateStationNew_JMenuItem;
private JMenuItem __Edit_Add_OtherNode_JMenuItem;
private JMenuItem __Edit_Add_OperationalRight_JMenuItem;
// TODO SAM 2007-02-18 Evaluate change node to baseflow
//private JMenuItem __Edit_Change_NodeToRiverBaseflowStation_JMenuItem;
private JMenu __Edit_Delete_JMenu;
private JMenuItem __Edit_Delete_StreamGageStation_JMenuItem;
private JMenuItem __Edit_Delete_DelayTableMonthly_JMenuItem;
private JMenuItem __Edit_Delete_DelayTableDaily_JMenuItem;
private JMenuItem __Edit_Delete_Diversion_JMenuItem;
private JMenuItem __Edit_Delete_PrecipitationTS_JMenuItem;
private JMenuItem __Edit_Delete_EvaporationTS_JMenuItem;
private JMenuItem __Edit_Delete_Reservoir_JMenuItem;
private JMenuItem __Edit_Delete_InstreamFlow_JMenuItem;
private JMenuItem __Edit_Delete_Well_JMenuItem;
private JMenuItem __Edit_Delete_StreamEstimateStation_JMenuItem;
private JMenuItem __Edit_Delete_OtherNode_JMenuItem;
private JMenuItem __Edit_Delete_OperationalRight_JMenuItem;

// View menu items...

private JMenu __View_JMenu;
private JMenuItem __View_DataSetSummary_JMenuItem;
private JCheckBoxMenuItem __View_Map_ShowFeaturesInDataSet_JMenuItem;
private JCheckBoxMenuItem __View_Map_ShowFeaturesNotInDataSet_JMenuItem;
private JCheckBoxMenuItem __View_Network_JMenuItem;

// Data menu items...

// TODO - any way to combine control? - for now leave control and response file separate
private JMenu __Data_JMenu;
private JMenu __Data_Control_JMenu;
private JMenuItem __Data_Control_Control_JMenuItem;
private JMenuItem __Data_Control_Response_JMenuItem;
private JMenuItem __Data_Control_OutputControl_JMenuItem;
private JMenuItem __Data_ConsumptiveUse_JMenuItem;
private JMenuItem __Data_StreamGage_JMenuItem;
private JMenu __Data_DelayTables_JMenu;
private JMenuItem __Data_DelayTables_Monthly_JMenuItem;
private JMenuItem __Data_DelayTables_Daily_JMenuItem;
private JMenuItem __Data_Diversions_JMenuItem;
private JMenu __Data_Precipitation_JMenu;
private JMenuItem __Data_Precipitation_Monthly_JMenuItem;
private JMenu __Data_Evaporation_JMenu;
private JMenuItem __Data_Evaporation_Monthly_JMenuItem;
private JMenuItem __Data_Reservoirs_JMenuItem;
private JMenuItem __Data_InstreamFlows_JMenuItem;
private JMenuItem __Data_Wells_JMenuItem;
private JMenuItem __Data_Plans_JMenuItem;
private JMenuItem __Data_StreamEstimate_JMenuItem;
private JMenuItem __Data_RiverNetwork_JMenuItem;
private JMenuItem __Data_OperationalRights_JMenuItem;
// TODO - add later once there is good documentation
//private JMenuItem __Data_SanJuan_JMenuItem;

// Run menu items...

private JMenu __Run_JMenu;
private JMenuItem __Run_Baseflows_JMenuItem;
private JMenuItem __Run_BaseflowsFast_JMenuItem;
private JMenuItem __Run_DataCheck_JMenuItem;
private JMenuItem __Run_SimulateFast_JMenuItem;
private JMenuItem __Run_Simulate_JMenuItem;
private JMenuItem __Run_Report_JMenuItem;
private JMenuItem __Run_Help_JMenuItem;
private JMenuItem __Run_Update_JMenuItem;
private JMenuItem __Run_Version_JMenuItem;

// Results menu...

private JMenu __Results_JMenu;
private JMenuItem __Results_GraphingTool_JMenuItem;
private JMenuItem __Results_GraphDemandSupplySummary_JMenuItem;
private JMenuItem __Results_ProcessTSProduct_JMenuItem;
private JMenuItem __Results_OutputFiles_JMenuItem;

// Tools menu...

private JMenu __Tools_JMenu;
private JMenuItem __Tools_RunSmDelta_JMenuItem;
private JMenuItem __Tools_AddSummaryMapLayer_JMenuItem;
private JMenuItem __Tools_AddDifference_JMenuItem;
private JMenuItem __Tools_AddPercentDifference_JMenuItem;
private JMenuItem __Tools_AddAnimationMapLayer_JMenuItem;
private JMenuItem __Tools_QueryTool_JMenuItem;
private JMenuItem __Tools_Options_JMenuItem;
private JMenuItem __Tools_ReportUnusedData_JMenuItem;
private JMenuItem __Tools_ReportModifiedData_JMenuItem;
private JMenuItem __Tools_NetworkSummary_JMenuItem;
private JMenuItem __Tools_SaveDataSetAsKML_JMenuItem;
// Diagnostics adds its own menu item.

// Help menu

private JMenu __Help_JMenu;
private JMenuItem
	__Help_AboutStateModGUI_JMenuItem,
	__Help_ViewStateModDocumentation_JMenuItem,
	__Help_ViewStateModGUIDocumentation_JMenuItem,
	__Help_ViewStateModGUITrainingMaterials_JMenuItem,
	__Help_TroubleshootInstall_JMenuItem;

private JSplitPane __mainSplitPane;
private GeoViewJPanel __geoViewJPanel = null;
private StateMod_DataSet_JTree __datasetJTree = null;

private final String TAB = "    ";

// File menu items...

private final String
	__File_String = "File",
	__File_Open_String = "Open...",
	__File_New_Menu_String = "New",
	__File_New_DataSet_String = "Dataset...",
	__File_New_Network_String = "Network...",
	__File_Save_String = "Save...",
	__File_RemoveStateModOutputFiles_String = "Remove StateMod binary output files...",
	__File_Exit_String = "Exit";

// Edit menu item...

private final String
	__Edit_String = "Edit",
	__Edit_Add_String = "Add",
	__Edit_Add_StreamGageStation_String = "Stream Gage Station...",
	__Edit_Add_StreamGageHistoricalTSMonthly_String = TAB + "Stream Gage Historical TS (Monthly)...",
	__Edit_Add_StreamGageHistoricalTSDaily_String = TAB + "Stream Gage Historical TS (Daily)...",
	__Edit_Add_DelayTableMonthly_String = "Delay Table (Monthly)...",
	__Edit_Add_DelayTableDaily_String = "Delay Table (Daily)...",
	__Edit_Add_Diversion_String = "Diversion...",
	__Edit_Add_DiversionHistoricalTSMonthly_String = TAB + "Diversion Historical Diversion TS (Monthly)...",
	__Edit_Add_DiversionHistoricalTSDaily_String = TAB + "Diversion Historical Diversion TS (Daily)...",
	__Edit_Add_DiversionDemandTSMonthly_String = TAB + "Diversion Demand TS (Monthly)...",
	__Edit_Add_DiversionDemandOverrideTSMonthly_String = TAB + "Diversion Demand Override TS (Monthly)...",
	__Edit_Add_DiversionDemandTSAverageMonthly_String = TAB + "Diversion Demand TS (Average Monthly)...",
	__Edit_Add_DiversionDemandTSDaily_String = TAB + "Diversion Demand TS (Daily)...",
	__Edit_Add_DiversionIrrigationPracticeTSYearly_String = TAB + "Irrigation Practice TS (Yearly)...",
	__Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_String =
		TAB + "Consumptive Water Requirement TS (Monthly)...",
	__Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_String =
		TAB + "Consumptive Water Requirement TS (Daily)...",
	__Edit_Add_PrecipitationTS_String = "Precipitation TS (Monthly)...",
	__Edit_Add_EvaporationTS_String = "Evaporation TS (Monthly)...",
	__Edit_Add_Reservoir_String = "Reservoir...",
	__Edit_Add_ReservoirContentTSMonthly_String = TAB + "Reservoir Content End of Month TS (Monthly)...",
	__Edit_Add_ReservoirContentTSDaily_String = TAB + "Reservoir Content End of Day TS (Daily)...",
	__Edit_Add_ReservoirTargetTSMonthly_String = TAB + "Reservoir Target TS (Monthly)...",
	__Edit_Add_ReservoirTargetTSDaily_String = TAB + "Reservoir Target TS (Daily)...",
	__Edit_Add_InstreamFlow_String = "Instream Flow...",
	__Edit_Add_InstreamFlowDemandTSMonthly_String = TAB + "Instream Flow Demand TS (Monthly)...",
	__Edit_Add_InstreamFlowDemandTSAverageMonthly_String = TAB + "Instream Flow Demand TS (Average Monthly)...",
	__Edit_Add_InstreamFlowDemandTSDaily_String = TAB + "Instream Flow Demand TS (Daily)...",
	__Edit_Add_Well_String = "Well...",
	__Edit_Add_WellPumpingTSMonthly_String = TAB + "Well Historical Pumping TS (Monthly)...",
	__Edit_Add_WellPumpingTSDaily_String = TAB + "Well Historical Pumping TS (Daily)...",
	__Edit_Add_WellDemandTSMonthly_String = TAB + "Well Demand TS (Monthly)...",
	__Edit_Add_WellDemandTSDaily_String = TAB + "Well Demand TS (Daily)...",
	__Edit_Add_WellIrrigationPracticeTSYearly_String = TAB + "Irrigation Practice TS (Yearly)...",
	__Edit_Add_WellConsumptiveWaterRequirementTSMonthly_String =
		TAB + "Consumptive Water Requirement TS (Monthly)...",
	__Edit_Add_WellConsumptiveWaterRequirementTSDaily_String =
		TAB + "Consumptive Water Requirement TS (Daily)...",
	__Edit_Add_StreamEstimateStationConvert_String = "Stream Estimate Station (Convert Node to Stream Estimate)...",
	__Edit_Add_StreamEstimateStationNew_String = "Stream Estimate Station (Create New Stream Estimate Node)...",
	__Edit_Add_OtherNode_String = "Other Node...",
	__Edit_Add_OperationalRight_String = "Operational Right...",
	__Edit_Delete_String = "Delete",
	__Edit_Delete_StreamGageStation_String = "Stream Gage Station...",
	__Edit_Delete_DelayTableMonthly_String = "Delay Table (Monthly)...",
	__Edit_Delete_DelayTableDaily_String = "Delay Table (Daily)...",
	__Edit_Delete_Diversion_String = "Diversion...",
	__Edit_Delete_InstreamFlow_String = "Instream Flow...",
	__Edit_Delete_PrecipitationTS_String = "Precipitation TS (Monthly)...",
	__Edit_Delete_EvaporationTS_String = "Evaporation TS (Monthly)...",
	__Edit_Delete_Reservoir_String = "Reservoir...",
	__Edit_Delete_Well_String = "Well...",
	__Edit_Delete_StreamEstimateStation_String = "Stream Estimate Station...",
	__Edit_Delete_OtherNode_String = "Other Node...",
	__Edit_Delete_OperationalRight_String = "Operational Right...";

// View Menu items
private final String
	__View_String = "View",
	__View_DataSetSummary_String = "Data Set Summary",
	__View_Map_ShowFeaturesInDataSet_String = "Map - Show Stations in Data Set",
	__View_Map_ShowFeaturesNotInDataSet_String = "Map - Show Stations not in Data Set",
	__View_Network_String = "Network";

// Data menu items (since the menu is already "Data", do not put "Data" in any of the sub-menus...

private final String
	__Data_String = "Data",
	__Data_Control_String = "Control",
		__Data_Control_Response_String = "Response",
		__Data_Control_Control_String = "Control",
		__Data_Control_OutputControl_String = "Output Request...",
	__Data_ConsumptiveUse_String = "Consumptive Use...",
	__Data_StreamGage_String = "Stream Gage...",
	__Data_DelayTables_String = "Delay Tables",
		__Data_DelayTables_Monthly_String = "Monthly...",
		__Data_DelayTables_Daily_String = "Daily...",
	__Data_Diversions_String = "Diversions...",
	__Data_Precipitation_String = "Precipitation...",
		__Data_Precipitation_Monthly_String = "Precipitation - Monthly...",
	__Data_Evaporation_String = "Evaporation...",
		__Data_Evaporation_Monthly_String = "Evaporation - Monthly...",
	__Data_Reservoirs_String = "Reservoirs...",
	__Data_InstreamFlows_String = "Instream Flows...",
	__Data_Wells_String = "Wells...",
	__Data_Plans_String = "Plans...",
	__Data_StreamEstimate_String = "Stream Estimate...",
	__Data_OperationalRights_String = "Operational Rights...",
	__Data_RiverNetwork_String = "River Network...";
	// TODO SAM 2007-02-18 Need to implement San Juan
	//__Data_SanJuan_String = "San Juan Sediment Recovery...";

// Run menus...
private final String 
	__Run_String = "Run",
	__Run_Baseflows_String = "Baseflows (statemod -baseflow)",
	__Run_BaseflowsFast_String = "Baseflows, ungaged areas only (statemod -baseflowx)",
	__Run_DataCheck_String = "Data Check (statemod -check)",
	__Run_SimulateFast_String = "Simulate Fast, no reports (statemod -simx)",
	__Run_Simulate_String = "Simulate, generate standard reports (statemod -sim)",
	__Run_Report_String = "Report (statemod -report)...",
	__Run_Help_String = "Help (statemod -help)",
	__Run_Update_String = "Update History (statemod -update)",
	__Run_Version_String = "Version (statemod -version)";

// Results menu items
private final String
	__Results_String = "Results",
	__Results_GraphTool_String = "Graphing Tool...",
	__Results_GraphDemandSupplySummary_String =	"Graph - Demand/Supply Summary",
	__Results_ProcessTSProduct_String = "Process TS Product...",
	__Results_OutputFiles_String = "Output Files...";

// Tools menu items...

private final String
	__Tools_String = "Tools",
	__Tools_RunSmDelta_String = "Run SmDelta (to display summary/delta data on map)...",
	__Tools_AddSummaryMapLayer_String = "Add Summary Map Layer...",
	__Tools_AddDifference_String = "Add Historical/Simulated Streamflow Difference to Map",
	__Tools_AddPercentDifference_String = "Add Historical/Simulated Streamflow Difference (Percent) to Map",
	__Tools_AddAnimationMapLayer_String = "Add Animation Map Layer",
	__Tools_QueryTool_String = "Query Tool...",
	__Tools_Options_String = "Options...",

	// Diagnostics added automatically.
	__Tools_ReportUnusedData_String = "Diagnostics Report - Unused Data...",
	__Tools_ReportModifiedData_String = "Diagnostics Report - Modified Data...",
	
	__Tools_NetworkSummary_String = "Network Summary",
    __Tools_SaveDataSetAsKML_String = "Save Data Set as KML...";
// Help menu items...

private final String
	__Help_String = "Help",
	__Help_AboutStateModGUI_String = "About StateMod/StateMod GUI...",
	__Help_ViewStateModDocumentation_String = "View StateMod Model Documentation",
	__Help_ViewStateModGUIDocumentation_String = "View StateMod GUI (this software) Documentation",
	__Help_ViewStateModGUITrainingMaterials_String = "View StateMod GUI Training Materials",
	__Help_TroubleshootInstall_String = "Troubleshoot the StateMod/GUI Install";

public static final int STATUS = 1;
public static final int DEBUG = 2;
public static final int WARNING = 3;

public static final int WAIT = 0;
public static final int READY = 1;

// Done with StateMod input files.  Additional internal data...

/**
One-line status area at bottom of the main GUI, for text messages.
*/
private JTextField __statusBar;

/**
Small text area that is used for READY/WAIT indicator.
*/
private JTextField __statusBarReadyWait;

/**
The active StateMod data set, populated after a response file is opened.
*/
private StateMod_DataSet __dataset = null;

/**
The active StateCU data set, populated after a response file is opened.
This is only used because some StateCU code refers to a StateCU_DataSet.  However, StateCU files that
are used by StateMod are also part of the StateMod data set, for core data management.
*/
private StateCU_DataSet __datasetStateCU = null;

/**
The window manager for the various StateMod data component windows.
*/
private StateMod_DataSet_WindowManager __dataset_wm = null;

/**
The separate window for the model network.
*/
private StateMod_Network_JFrame __networkJFrame = null;

/**
The folder from which StateMod response files have been opened to help the user more
efficiently navigate to new response files.  This is used by openResponseFile().
*/
private String __lastResponseFileFolder = null;

/**
Constructor.
@param home directory for application, used to locate documentation.
*/
public StateModGUI_JFrame ( String home )
{	// Use StateMod for the title (not StateModGUI) because users expect a GUI and
	// there is no need to tell them they are using one.
	super("StateMod");

	JGUIUtil.setIcon(this, JGUIUtil.getIconImage());

	// TODO SAM 2008-03-16 Evaluate UI setting
	// Do this so that when ComboBoxes are disabled in the App, the 
	// text in them becomes black, not light-grey.

	UIManager.put("ComboBox.disabledForeground", Color.black);

	Message.setTopLevel(this);
	
	setupGUI();
	checkGUIState(); // Checks menus.

	// Run the StateMod executable silently to get the version.  If there is
	// an error, then StateMod is probably not in the path.
	Runnable r = new Runnable() {
		public void run () {
			String routine = "StateModGUI_JFrame";
			try {
				printStatus("Running \"statemod -version\" to get the model software version.", WAIT);
				StateMod_Util.runStateMod((String)null,"-version", false, null);
				String version = StateMod_Util.getStateModVersion();
				if ( (version == null) || version.equals("") ) {
					version = "unknown (check StateModExecutable property in StateModGUI.cfg)";
				}
				printStatus("StateMod software version used with GUI determined to be:  " +
					StateMod_Util.getStateModVersion(), READY );
			}
			catch (Exception e) {
				Message.printWarning(1, routine,
					"Error getting the StateMod version.\n"
					+ "Check the StateModExecutable property in StateModGUI.cfg\n"
					+ "The model will not run and data from output files cannot be displayed.");
				Message.printWarning(2, routine, e);
				printStatus("Unable to determine the StateMod software version - " +
					"defaulting to behavior of version in Help About.", READY );
			}
		}
	};
    if ( SwingUtilities.isEventDispatchThread() )
    {
        r.run();
    }
    else 
    {
        SwingUtilities.invokeLater ( r );
    }

	printStatus("Use File...Open to select a StateMod response file (*.rsp).", READY);
//	requestFocus();
	
	// TODO SAM 2011-08-29 Create a splash screen while things load
	
	/*
	try {
		new StateModGUI_SplashScreen ( new URL("http://google-maps-icons.googlecode.com/files/water.png") );
	}
	catch ( Exception e ) {
		Message.printWarning ( 3, "", e );
	}
	*/
}

/**
Handle action events.
@param e Action event to handle.
*/
public void actionPerformed(ActionEvent e)
{	String routine = "StateModGUI_JFrame.actionPerformed";
	if (Message.isDebugOn) {
		Message.printDebug(20, routine,	"actionPerformed: "+e.getActionCommand());
	}
	Object source = e.getSource();

	// List in the order of the GUI.

	if (IOUtil.testing()) {
		if (e.getActionCommand().equals(TESTING_MENUITEM)) {
			scrubDataSet();
		}
	}

	if ( source == __File_New_Dataset_JMenuItem ) {
		if ( (__dataset != null) && __dataset.isDirty()) {
			// An existing data set is open and has been modified...
			int x = new ResponseJDialog(this, 
			"Save StateMod Data Set?",
			"The existing data set has been modified.\n" +
			"Do you want to save the changes before opening a new data set?",
			ResponseJDialog.YES | ResponseJDialog.NO | ResponseJDialog.CANCEL).response();
			if ( x == ResponseJDialog.YES ) {
				// Save the data set...
				new StateMod_Save_JDialog(this, __dataset, __dataset_wm);
			}
			else if ( x == ResponseJDialog.CANCEL ) {
				return;
			}
			// else NO so continue below opening the new data set.
		}
		// Close down the existing data set views
		closeOpenDataViews();
		// Create an empty new data set...
		__dataset = new StateMod_DataSet ( StateMod_DataSet.TYPE_UNKNOWN );
		StateMod_Data.setDataSet(__dataset);
		__datasetStateCU = new StateCU_DataSet ();

		// This will create a new data set and show the response window...
		new StateModGUI_AddComponent_JDialog ( this, 
				StateMod_DataSet.COMP_RESPONSE, __dataset, __dataset_wm, __datasetJTree );
		checkGUIState ();
	}
	if ( source == __File_New_Network_JMenuItem ) {	
		// FIXME SAM 2008-03-18 Need to add this?
	}
	else if ( source == __File_Open_JMenuItem ) {
		if ( (__dataset != null) && __dataset.isDirty()) {
			// An existing data set is open and has been modified...
			int x = new ResponseJDialog(this, "Save StateMod Data Set?",
			"The existing data set has been modified.\n" +
			"Do you want to save the changes before opening a new data set?",
			ResponseJDialog.YES | ResponseJDialog.NO | ResponseJDialog.CANCEL).response();
			if ( x == ResponseJDialog.YES ) {
				// Save the data set...
				new StateMod_Save_JDialog(this, __dataset, __dataset_wm);
			}
			else if ( x == ResponseJDialog.CANCEL ) {
				return;
			}
			// else NO so continue below opening the new data set.
		}
		openResponseFile();
	}
	else if ( source == __File_Save_JMenuItem ) {
		if ( (__dataset != null) && !__dataset.isDirty()) {
			new ResponseJDialog(this, "No data have been modified.  No need to save.", ResponseJDialog.OK);
			return;
		}
		new StateMod_Save_JDialog ( this, __dataset, __dataset_wm );
	}
	else if ( source == __File_RemoveStateModOutputFiles_JMenuItem ) {
		removeStateModBinaryOutputFiles ();
	}
	else if ( source == __File_Exit_JMenuItem ) {
		closeGUI();
	}

	// Edit menu - the dialogs are modal so there is no reason to manage like other windows.

	else if ( source == __Edit_Add_StreamGageStation_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_StreamGageHistoricalTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY,	__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_StreamGageHistoricalTSDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY, __dataset, __dataset_wm, __datasetJTree, 
			__networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DelayTableMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog (this,
			StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DelayTableDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog (this,
			StateMod_DataSet.COMP_DELAY_TABLES_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_Diversion_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog (this,
		StateMod_DataSet.COMP_DIVERSION_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionHistoricalTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY,	__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionHistoricalTSDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_DIVERSION_TS_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionDemandTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_DEMAND_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionDemandOverrideTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionDemandTSAverageMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionDemandTSDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_DEMAND_TS_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	// TODO - irrigation practice TS currently cannot be added.
	else if ( source == __Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY,
			__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY,
			__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_PrecipitationTS_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog (this,
		StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_EvaporationTS_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog (this,
		StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_Reservoir_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_RESERVOIR_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_ReservoirContentTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_ReservoirContentTSDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_ReservoirTargetTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_ReservoirTargetTSDaily_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY, __dataset,	__dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_InstreamFlow_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_INSTREAM_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_InstreamFlowDemandTSMonthly_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_InstreamFlowDemandTSAverageMonthly_JMenuItem){
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY,
			__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_InstreamFlowDemandTSDaily_JMenuItem){
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_DAILY,	__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_Well_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_WELL_STATIONS, __dataset,	__dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_WellPumpingTSMonthly_JMenuItem ){
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_WellPumpingTSDaily_JMenuItem ){
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_WellDemandTSMonthly_JMenuItem ){
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_WellDemandTSDaily_JMenuItem ){
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY,	__dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_WellConsumptiveWaterRequirementTSMonthly_JMenuItem ){
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY,	__dataset, __dataset_wm, __datasetJTree, 
			__networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_WellConsumptiveWaterRequirementTSMonthly_JMenuItem ){
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_StreamEstimateStationConvert_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS, __dataset, __dataset_wm, __datasetJTree, false, __networkJFrame);
		checkGUIState ();
	}
	else if ( source == __Edit_Add_StreamEstimateStationNew_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS, __dataset, __dataset_wm, __datasetJTree,  true, __networkJFrame);
		checkGUIState ();
	}
	else if ( source == __Edit_Add_OtherNode_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this, 
		StateMod_DataSet.COMP_OTHER_NODE, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Add_OperationalRight_JMenuItem ) {
		new StateModGUI_AddComponent_JDialog ( this,
		StateMod_DataSet.COMP_OPERATION_RIGHTS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}

	else if ( source == __Edit_Delete_StreamGageStation_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_DelayTableMonthly_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog (this,
		StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_DelayTableDaily_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog (this,
		StateMod_DataSet.COMP_DELAY_TABLES_DAILY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_Diversion_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog (this,
		StateMod_DataSet.COMP_DIVERSION_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_PrecipitationTS_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog (this,
		StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_EvaporationTS_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog (this,
		StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_Reservoir_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this,
		StateMod_DataSet.COMP_RESERVOIR_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_InstreamFlow_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this,
		StateMod_DataSet.COMP_INSTREAM_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_Well_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this, 
		StateMod_DataSet.COMP_WELL_STATIONS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_StreamEstimateStation_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this, 
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS, __dataset, __dataset_wm, __datasetJTree,  __networkJFrame);
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_OtherNode_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this, 
		StateMod_DataSet.COMP_OTHER_NODE, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}
	else if ( source == __Edit_Delete_OperationalRight_JMenuItem ) {
		new StateModGUI_DeleteComponent_JDialog ( this,
		StateMod_DataSet.COMP_OPERATION_RIGHTS, __dataset, __dataset_wm, __datasetJTree, __networkJFrame );
		checkGUIState ();
	}

	// View menu...

	else if ( source == __View_DataSetSummary_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_DATASET_SUMMARY);
	}
	else if ( source == __View_Map_ShowFeaturesInDataSet_JMenuItem ) {
		setMapFeatureVisibilityAndRedrawMap ( __View_Map_ShowFeaturesInDataSet_JMenuItem.getState(),
			__View_Map_ShowFeaturesNotInDataSet_JMenuItem.getState() );
	}
	else if ( source == __View_Map_ShowFeaturesNotInDataSet_JMenuItem ) {
		setMapFeatureVisibilityAndRedrawMap ( __View_Map_ShowFeaturesInDataSet_JMenuItem.getState(),
			__View_Map_ShowFeaturesNotInDataSet_JMenuItem.getState() );
	}
	else if ( source == __View_Network_JMenuItem ) {
		if ( !__View_Network_JMenuItem.getState() ) {
			if (__networkJFrame != null) {
				__networkJFrame.setVisible(false);
			}
		}
		else {
			if (__networkJFrame == null) {
				// Would only be null if the user chose not to read in a network file when the 
				// rsp file was read.  Too late now.
				__View_Network_JMenuItem.setState(false);
			}
			else {
				__networkJFrame.setVisible(true);
			}
		}
	}

	// Data menu...

	else if ( source == __Data_Control_Control_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_CONTROL,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_CONTROL));
	}
	else if ( source == __Data_Control_Response_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_RESPONSE,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_RESPONSE));
	}
	else if ( source == __Data_Control_OutputControl_JMenuItem ) {
		// TODO - why not open similar to other windows?
		new StateMod_OutputControl_JFrame(__dataset);
	}
	else if ( source == __Data_ConsumptiveUse_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_CONSUMPTIVE_USE,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_STATECU_STRUCTURE));
	}
	else if ( source == __Data_StreamGage_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_STREAMGAGE,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_STREAMGAGE_STATIONS));
	}
	else if ( source == __Data_DelayTables_Monthly_JMenuItem ) {
		__dataset_wm.displayWindow(	StateMod_DataSet_WindowManager.WINDOW_DELAY_TABLE_MONTHLY,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY));
	}
	else if ( source == __Data_DelayTables_Daily_JMenuItem ) {
		__dataset_wm.displayWindow(	StateMod_DataSet_WindowManager.WINDOW_DELAY_TABLE_DAILY,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DELAY_TABLES_DAILY));
	}
	else if ( source == __Data_Diversions_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_DIVERSION,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DIVERSION_STATIONS));
	}
	else if ( source == __Data_Precipitation_Monthly_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_PRECIPITATION,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY));
	}
	else if ( source == __Data_Evaporation_Monthly_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_EVAPORATION,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY));
	}
	else if ( source == __Data_Reservoirs_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_RESERVOIR,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_RESERVOIR_STATIONS));
	}
	else if ( source == __Data_InstreamFlows_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_INSTREAM,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_INSTREAM_STATIONS));
	}
	else if ( source == __Data_StreamEstimate_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_STREAMESTIMATE,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS));
	}
	else if ( source == __Data_Wells_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_WELL,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_WELL_STATIONS));
	}
	else if ( source == __Data_Plans_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_PLAN,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_PLANS));
	}
	else if ( source == __Data_RiverNetwork_JMenuItem ) {
		__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_RIVER_NETWORK,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_RIVER_NETWORK));
	}
	else if ( source == __Data_OperationalRights_JMenuItem ) {
		__dataset.DUMP_DIRTY = true;
		__dataset_wm.displayWindow(	StateMod_DataSet_WindowManager.WINDOW_OPERATIONAL_RIGHT,
			shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_OPERATION_RIGHTS));
	}

	// Run menu...

	else if ( source == __Run_Help_JMenuItem ) {
		try {
			StateMod_Util.runStateMod (	__dataset, "-help", true, this );
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod to print the help.");
			Message.printWarning(2, routine, e2);
		}
	}
	else if ( source == __Run_Update_JMenuItem ) {
		try {
			StateMod_Util.runStateMod ( __dataset, "-update",true, this);
		}
		catch (Exception e2) {
			Message.printWarning(1, routine,"Error running StateMod to print the update history.");
			Message.printWarning(2, routine, e2);
		}
	}
	else if ( source == __Run_Version_JMenuItem ) { 
		try {
			StateMod_Util.runStateMod (	__dataset ,"-version",true,this);
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod to print the version.");
			Message.printWarning(2, routine, e2);
		}
	}
	else if ( source == __Run_Baseflows_JMenuItem ) {
		if ( !checkForEdits() ) {
			return;
		}
		try {
			StateMod_Util.runStateMod ( __dataset, "-baseflow", true, this );
			updateBaseFlows ();
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod in baseflow (-baseflow) mode.");
			Message.printWarning(2, routine, e2);
		}
	}
	else if ( source == __Run_BaseflowsFast_JMenuItem ) {
		if ( !checkForEdits() ) {
			return;
		}
		try {
			StateMod_Util.runStateMod( __dataset, "-baseflowx", true, this );
			updateBaseFlows ();
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod in baseflow (-baseflowx) mode.");
			Message.printWarning(2, routine, e2);
		}

	}
	else if ( source == __Run_SimulateFast_JMenuItem ) {
		if ( !checkForEdits() ) {
			return;
		}
		try {
			StateMod_Util.runStateMod(__dataset,"-simx",true,this);
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod in simulation (-simx) mode.");
			Message.printWarning(2, routine, e2);
		}

	}
	else if ( source == __Run_Simulate_JMenuItem ) {
		if ( !checkForEdits() ) {
			return;
		}
		try {
			StateMod_Util.runStateMod(__dataset,"-sim", true, this);
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod in simulation (-sim) mode.");
			Message.printWarning(2, routine, e2);
		}
	}
	else if ( source == __Run_DataCheck_JMenuItem ) {
		if ( !checkForEdits() ) {
			return;
		}
		String ocfile = __dataset.getDataSetDirectory() + File.separator + __dataset.getBaseName() + ".XOU";
		File output_control_file = new File( ocfile );
		if ( output_control_file.exists()) {
			int x = new ResponseJDialog(this, 
			"Rename output control file",
			"Running data check will overwrite the output control file \"" + ocfile +
			"\".\nDo you want to rename this file?",
			ResponseJDialog.YES|ResponseJDialog.NO).response();

			if ( x == ResponseJDialog.YES ) {
				String lastDirectorySelected = JGUIUtil.getLastFileDialogDirectory();
				JFileChooser fc = JFileChooserFactory.createJFileChooser ( lastDirectorySelected );
				fc.setDialogTitle("Select Control File Name");
				SimpleFileFilter sff = new SimpleFileFilter("out", "Output Control File");
				fc.addChoosableFileFilter(sff);
				sff = new SimpleFileFilter("xou", "Output Control File");
				fc.addChoosableFileFilter(sff);
				fc.setFileFilter(sff);
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				int retVal = fc.showSaveDialog(this);
				if (retVal != JFileChooser.APPROVE_OPTION) {
					// Cancel...
					return;
				}
				String currDir = (fc.getCurrentDirectory()).toString();
				if ( !currDir.equalsIgnoreCase(lastDirectorySelected)) {
					JGUIUtil.setLastFileDialogDirectory(currDir);
				}
				String filename =fc.getSelectedFile().getPath();
	
				output_control_file.renameTo(new File(filename) );
			}
		}
		try {
			StateMod_Util.runStateMod(__dataset,"-check",true,this);
		}
		catch (Exception e2) {
			Message.printWarning(1, routine, "Error running StateMod in check (-check) mode.");
			Message.printWarning(2, routine, e2);
		}
	}
	else if ( source == __Run_Report_JMenuItem ) {
		if (__dataset_wm.isWindowOpen(StateMod_DataSet_WindowManager.WINDOW_RUN_REPORT)) {
			__dataset_wm.getWindow( StateMod_DataSet_WindowManager.WINDOW_RUN_REPORT).toFront();
		}
		else {	
			new StateMod_RunReport_JFrame(__dataset, __dataset_wm);
		}
	}

	// Results menu...

	else if ( source == __Results_GraphingTool_JMenuItem ) {
		JGUIUtil.setWaitCursor(this, true);

		// may need to create a list of stream gages that are valid
		// for graphing since _streamGageVector contains extraneous
		// nodes aside from stream gage nodes.

		new StateMod_GraphingTool_JFrame(__dataset, __dataset_wm);
		JGUIUtil.setWaitCursor(this, false);
	}
	else if ( source == __Results_GraphDemandSupplySummary_JMenuItem ) {
		displayDemandSupplySummaryGraph();
	}
	else if ( source == __Results_ProcessTSProduct_JMenuItem ) {
	/**
	TODO - UNDER CONSTRUCTION (2003-06-04) Use TSTool instead - may never need in the GUI

		try {
			processTSProductFile(true);
		}
		catch (Exception te) {
			Message.printWarning(1, "",
			"Error processing TSProduct file.");
			Message.printWarning(2, "", te);
		}
		*/

	}
	else if ( source == __Results_OutputFiles_JMenuItem ) {
		JGUIUtil.setWaitCursor(this, true);
		new StateMod_OutputFiles_JFrame(__dataset);
		JGUIUtil.setWaitCursor(this, false);
	}

	// Tools menu...

	else if ( source == __Tools_RunSmDelta_JMenuItem ) {
		JGUIUtil.setWaitCursor(this, true);
		new StateMod_RunSmDelta_JFrame(__dataset);
		JGUIUtil.setWaitCursor(this, false);
	}
	else if ( source == __Tools_AddSummaryMapLayer_JMenuItem ) {
		JGUIUtil.setWaitCursor(this, true);
		String lastDirectorySelected = JGUIUtil.getLastFileDialogDirectory();

		JFileChooser fc = null;
		if (lastDirectorySelected != null) {
			fc = new JFileChooser(lastDirectorySelected);
		}
		else {
			fc = new JFileChooser();
		}

		fc.setDialogTitle("Select Data File");
		SimpleFileFilter txt = new SimpleFileFilter("txt", "Comma-delimited Files");
		fc.addChoosableFileFilter(txt);

		fc.setFileFilter(txt);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);	
	
		JGUIUtil.setWaitCursor(this, false);
		if ( fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		String filename = fc.getSelectedFile().getPath();

		JGUIUtil.setWaitCursor(this, true);
		addSummaryMapLayer ( filename );
		JGUIUtil.setWaitCursor(this, false);
	}
	else if ( source == __Tools_AddDifference_JMenuItem ) {
		addDifferenceMapLayer(false);
	}
	else if ( source == __Tools_AddPercentDifference_JMenuItem ) {
		addDifferenceMapLayer(true);
	}
	else if ( source == __Tools_AddAnimationMapLayer_JMenuItem ) {
		addAnimationMapLayer();
	}
	else if ( source == __Tools_QueryTool_JMenuItem ) {
		// Use the window manager since the query tool depends on a data set...
		__dataset_wm.displayWindow( StateMod_DataSet_WindowManager.WINDOW_QUERY_TOOL);
	}
	else if ( source == __Tools_ReportModifiedData_JMenuItem ) {
		PropList props = new PropList ("Modified Data");
		props.set ("Title="+__dataset.getBaseName()+" - Modified Data");
		new ReportJFrame ( __dataset.getModifiedDataSummary(), props );
	}
	else if (source == __Tools_NetworkSummary_JMenuItem) {
		createNetworkSummary();
	}
	else if ( source == __Tools_ReportUnusedData_JMenuItem ) {
		PropList props = new PropList ("Unused Data");
		props.set ( "Title=" +__dataset.getBaseName()+" - Unused Data");
		new ReportJFrame ( __dataset.getUnusedDataSummary(), props );
	}
	else if ( source == __Tools_Options_JMenuItem ) {
		new StateModGUI_Options_JFrame ( this );
	}
	else if ( source == __Tools_SaveDataSetAsKML_JMenuItem) {
		saveToFile();
		//writeKML(__dataset);
	}

	// Help menu...

	else if ( source == __Help_AboutStateModGUI_JMenuItem ) {
		StringBuffer helptext = new StringBuffer();
		helptext.append("StateMod Graphical User Interface (StateModGUI)\n");
		helptext.append("Version " + IOUtil.getProgramVersion() + "\n");
		helptext.append("Developed with StateMod Version 13.00.00 (2012/02/15)\n");
		helptext.append("\n"+
		"StateMod GUI is a part of Colorado's Decision Support Systems (CDSS)\n" +
		"Copyright (C) 1997-2019 Colorado Department of Natural Resources\n" +
		" \n" +
		"StateMod GUI is free software:  you can redistribute it and/or modify\n" +
		"    it under the terms of the GNU General Public License as published by\n" +
		"    the Free Software Foundation, either version 3 of the License, or\n" +
		"    (at your option) any later version.\n" +
		" \n" +
		"StateMod GUI is distributed in the hope that it will be useful,\n" +
		"    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
		"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
		"    GNU General Public License for more details.\n" +
		" \n" +
		"You should have received a copy of the GNU General Public License\n" +
		"    along with StateMod GUI.  If not, see <https://www.gnu.org/licenses/>.\n" +
		" \n" );
		String responseFile = "";
		StateMod_DataSet dataSet = getDataSet();
		if ( dataSet != null ) {
			responseFile = dataSet.getDataFilePath(dataSet.getDataSetFileName());
		}
		String statemod_path = StateMod_Util.getStateModExecutable (IOUtil.getApplicationHomeDir(),
			responseFile );
		String stateModVersion = StateMod_Util.getStateModVersion();
		if ( stateModVersion.length() > 0 ) {
			helptext.append("StateMod software version used with GUI determined to be " +
				StateMod_Util.getStateModVersion() + " revision date: " +
				StateMod_Util.getStateModRevisionDate() + " from running:\n" +
				statemod_path + " -version\n");
		}
		else {
			helptext.append( "Unable to detect StateMod version from running:\n" + statemod_path + " -version\n");
		}
		helptext.append( "\n...\nThis software incorporates JFreeChart (C)opyright 2000-2004 by\n" +
		"Object Refinery Limited and Contributers,\n" +
		"which is distributed according to the GNU Lesser General Public License (LGPL)\n...\n" );
		helptext.append("Colorado Department of Natural Resources\n");
		helptext.append("Colorado Division of Water Resources\n");
		helptext.append("Colorado Water Conservation Board\n");
		helptext.append("Send comments about this interface to\n");
		helptext.append("DNR_OpenCDSS@state.co.us\n");
		new HelpAboutJDialog(this, "About StateMod/StateMod GUI", helptext.toString(), true);
	}
	else if ( source == __Help_ViewStateModDocumentation_JMenuItem ) {
	    // The location of the documentation is relative to the application home
	    String docFileName = IOUtil.getApplicationHomeDir() + "/doc/UserManual/StateMod.pdf";
	    // Convert for the operating system
	    docFileName = IOUtil.verifyPathForOS(docFileName, true);
	    // Now display using the default application for the file extension
	    Message.printStatus(2, routine, "Opening StateMod documentation \"" + docFileName + "\"" );
	    try {
	        Desktop desktop = Desktop.getDesktop();
	        desktop.open ( new File(docFileName) );
	    }
	    catch ( Exception e2 ) {
	        Message.printWarning(1, "", "Unable to display StateMod documentation at \"" + docFileName + "\" (" +
	        	e2 + ")." );
	    }
	}
	else if ( source == __Help_ViewStateModGUIDocumentation_JMenuItem ) {
	    // The location of the documentation is relative to the application home
	    String docFileName = IOUtil.getApplicationHomeDir() + "/doc/UserManual/StateModGUI.pdf";
	    // Convert for the operating system
	    docFileName = IOUtil.verifyPathForOS(docFileName, true);
	    // Now display using the default application for the file extension
	    Message.printStatus(2, routine, "Opening StateMod GUI documentation \"" + docFileName + "\"" );
	    try {
	        Desktop desktop = Desktop.getDesktop();
	        desktop.open ( new File(docFileName) );
	    }
	    catch ( Exception e2 ) {
	        Message.printWarning(1, "", "Unable to display StateMod GUI documentation at \"" + docFileName + "\" (" +
	        	e2 + ")." );
	    }
	}
    else if ( source == __Help_ViewStateModGUITrainingMaterials_JMenuItem ) {
        uiAction_Help_ViewTrainingMaterials ();
    }
    else if ( source == __Help_TroubleshootInstall_JMenuItem ) {
        uiAction_Help_TroubleshootInstall ();
    }
}

/**
Adds an animation map layer to the geoview display.
*/
private void addAnimationMapLayer() {
	String routine = "StateModGUI_JFrame.addAnimationMapLayer";
	JGUIUtil.setWaitCursor(this, true);

	// Make sure the layer does not already exist in the geo view
	/*
	Vector layerViews = __geoViewJPanel.getGeoView().getLayerViews();
	GeoLayerView layerView = null;
	int size = layerViews.size();
	String name = null;
	for (int i = 0; i < size; i++) {
		layerView = (GeoLayerView)layerViews.elementAt(i);
		name = layerView.getName();
		if (name != null && name.equals(layerName)) {
			JGUIUtil.setWaitCursor(this, false);
			return;
		}
	}
	*/

	// Set up the table fields

	List fields = new Vector();
	fields.add(new TableField(TableField.DATA_TYPE_STRING, "Identifier", 20));
	fields.add(new TableField(TableField.DATA_TYPE_STRING, "Name", 40));
	fields.add(new TableField(TableField.DATA_TYPE_STRING, "Date", 40));
	fields.add(new TableField(TableField.DATA_TYPE_DOUBLE, "Stream (Historical)", 11, 3));
	fields.add(new TableField(TableField.DATA_TYPE_DOUBLE, "Stream (Simulated)", 11, 3));

	DataTable table = new DataTable(fields);
	TableRecord record = null;

	String rspFile = __dataset.getComponentForComponentType( StateMod_DataSet.COMP_RESPONSE).getDataFileName();
	int index = rspFile.indexOf(".rsp");
	String basename = rspFile.substring(0, index);
	
	printStatus("Reading streamflow historical time series", WAIT);

	//////////////////////////////////////////////////
	// Stream Time Series

	List stations = (List)__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS).getData();

	int size = stations.size();
	String[] streamIDs = new String[size];
	StateMod_Data data = null;
	for (int i = 0; i < size; i++) {
		data = (StateMod_Data)stations.get(i);
		record = new TableRecord(5);
		record.addFieldValue(data.getID());
		streamIDs[i] = data.getID();
		record.addFieldValue(data.getName());
		record.addFieldValue("No date set yet.");
		record.addFieldValue(new Double(0));
		record.addFieldValue(new Double(0));

		try {
			table.addRecord(record);
		}
		catch (Exception e) {
			Message.printWarning(1, routine, "Error adding row to table.");
			Message.printWarning(2, routine, e);
		}
	}

	List tsStreamHist = (List)__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY).getData();
	int errorCount = 0;
	
	/////////////////////////////////////////
	// BTS Stream
	List tsStreamBTS = new Vector();
	TS tts = null;
	for (int i = 0; i < size; i++) {
		printStatus("Reading streamflow simulated time series " + (i + 1) + " of " + size, WAIT);
		try {
			tts = StateMod_BTS.readTimeSeries("" + streamIDs[i] 
				+ ".StateMod.River_Outflow.Month", basename + ".b43", null, null, null, true, null);
			tsStreamBTS.add(tts);
		}
		catch (Exception e) {
			Message.printWarning(1, routine, "Error reading time series '" + streamIDs[i]
				+ ".StateMod.River_Outflow.Month from "	+ basename + ".b43");
			Message.printWarning(2, routine, e);
			errorCount++;
		}
	}
	if (errorCount > 0) {
		Message.printWarning(1, routine, 
			"Error reading " + errorCount + " time series from data file.  Some simulated streamflow "
			+ "data will not be displayed.  See error log for details.");
	}

	// Get the maximum values for all time series and dates

	DateTime[] range = getMonthTSDateRange(tsStreamHist, null, null);
	range = getMonthTSDateRange(tsStreamBTS, range[0], range[1]);
	
	double[] max = new double[2];
	max[0] = getMonthTSMax(tsStreamHist);
	max[1] = getMonthTSMax(tsStreamBTS);

	List dataV = new Vector();
		
	try {
		printStatus("Setting up streamflow animation layer", WAIT);
		String[] idStrings = new String[1];
		idStrings[0] = "Identifier";
		GeoViewAnimationLayerData layerData = new GeoViewAnimationLayerData(table, "Streamflow Animation",
			GRSymbol.SYM_VBARSIGNED, idStrings, null, new Vector(), false, null);
		layerData.setMissingDoubleReplacementValue(-1);

		GeoViewAnimationData animationData = null;

		animationData = new GeoViewAnimationData(layerData, tsStreamHist,
			"Stream (Historical)", "Date", "Stream (Historical): ",
			"Streamflow", true, GeoViewAnimationData.CHECKBOX, max[0]);
		dataV.add(animationData);

		animationData = new GeoViewAnimationData(layerData, tsStreamBTS,
			"Stream (Simulated)", "Date", "Stream (Simulated): ",
			"Streamflow", true, GeoViewAnimationData.CHECKBOX, max[1]);
		dataV.add(animationData);

	}
	catch (Exception e) {
		Message.printWarning(1, routine, "Error setting up animation layer data.");
		Message.printWarning(2, routine, e);
	}


//////////////////////////////////////////////////////////////////////////////
//			RESERVOIR
//////////////////////////////////////////////////////////////////////////////

	// Set up the table fields

	fields = new Vector();
	fields.add(new TableField(TableField.DATA_TYPE_STRING,"Identifier", 20));
	fields.add(new TableField(TableField.DATA_TYPE_STRING,"Name", 40));
	fields.add(new TableField(TableField.DATA_TYPE_STRING,"Date", 40));
	fields.add(new TableField(TableField.DATA_TYPE_DOUBLE,"MaxCapacity", 11, 3));
	fields.add(new TableField(TableField.DATA_TYPE_DOUBLE,"MinCapacity", 11, 3));
	fields.add(new TableField(TableField.DATA_TYPE_DOUBLE,"CurrentCapacity", 11, 3));

	table = new DataTable(fields);

	printStatus("Reading reservoir content historical time series", WAIT);

	//////////////////////////////////////////////////
	// Reservoir Time Series

	stations = (List)__dataset.getComponentForComponentType(StateMod_DataSet.COMP_RESERVOIR_STATIONS).getData();

	size = stations.size();
	String[] resIDs = new String[size];
	for (int i = 0; i < size; i++) {
		data = (StateMod_Data)stations.get(i);
		record = new TableRecord(5);
		record.addFieldValue(data.getID());
		resIDs[i] = data.getID();
		record.addFieldValue(data.getName());
		record.addFieldValue("No date set yet.");
		record.addFieldValue(new Double(0));
		record.addFieldValue(new Double(0));
		record.addFieldValue(new Double(0));

		try {
			table.addRecord(record);
		}
		catch (Exception e) {
			Message.printWarning(1, routine, "Error adding row to table.");
			Message.printWarning(2, routine, e);
		}
	}

/*
	List tsResHist = new Vector();
	for (int i = 0; i < size; i++) {
		StateMod_Reservoir r = (StateMod_Reservoir)stations.elementAt(i);
		tsResHist.add(r.getContentMonthTS());
	}
*/	
	List tsResHist = (List)__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY).getData();

	/////////////////////////////////////////
	// BTS Res
	
	errorCount = 0;
	List tsResBTS = new Vector();
	for (int i = 0; i < size; i++) {
		printStatus("Reading reservoir content simulated time series " + (i + 1) + " of " + size, WAIT);
		try {
			tts = StateMod_BTS.readTimeSeries( "" + resIDs[i] + ".StateMod.Sim_EOM.Month",
				basename + ".b44", null, null, null, true, null);
			tsResBTS.add(tts);
		}
		catch (Exception e) {
			Message.printWarning(1, routine, "Error reading time series '" + resIDs[i]
				+ ".StateMod.Sim_EOM.Month from '" + basename + ".b44'");
			Message.printWarning(2, routine, e);
			errorCount++;
		}
	}
	if (errorCount > 0) {
		Message.printWarning(1, routine, 
			"Error reading " + errorCount + " time series from data file.  Some simulated reservoir "
			+ "data will not be displayed.  See error log for details.");
	}

	// Get the maximum values for all time series and dates

	range = getMonthTSDateRange(tsResHist, null, null);
	range = getMonthTSDateRange(tsResBTS, range[0], range[1]);

	max = new double[1];
	double mtemp1 = getMonthTSMax(tsResHist);
	double mtemp2 = getMonthTSMax(tsResBTS);

	if (mtemp1 > mtemp2) {
		max[0] = mtemp1;
	}
	else {
		max[0] = mtemp2;
	}

	try {
		printStatus("Setting up reservoir animation layer", WAIT);
		PropList props = new PropList("");
		props.set("MaxCapacity", "" + max[0]);
		props.set("TeacupSize", "50");

		String[] idStrings = new String[1];
		idStrings[0] = "Identifier";

		String[] dataStrings = new String[3];
		dataStrings[0] = "MaxCapacity";
		dataStrings[1] = "MinCapacity";
		dataStrings[2] = "CurrentCapacity";
		
		GeoViewAnimationLayerData layerData = new GeoViewAnimationLayerData(table, "Reservoir Content Animation",
			GRSymbol.SYM_TEACUP, idStrings, dataStrings, new Vector(), false, props);
		layerData.setMissingDoubleReplacementValue(-1);
		
		GeoViewAnimationData animationData = null;

		size = tsResHist.size();
		animationData = new GeoViewAnimationData(layerData, tsResHist,
			"CurrentCapacity", "Date",
			"Reservoir EOM (Historical): ",
			"Reservoir EOM", true,
			GeoViewAnimationData.RADIOBUTTON, max[0]);
		animationData.setNumHelperTS(2);
		MonthTS tsNew;
		MonthTS ts;
		StateMod_Reservoir res = null;
		for (int i = 0; i < size; i++) {
			res = (StateMod_Reservoir)stations.get(i);
			ts = (MonthTS)tsResHist.get(i);	

			if (ts != null) {
				tsNew = (MonthTS)TSUtil.newTimeSeries(ts.getIdentifierString(), true);
				tsNew.setIdentifier(ts.getIdentifierString());
				tsNew.setDescription("MaxCapacity");
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDate1(ts.getDate1());
				tsNew.setDate1Original(ts.getDate1());
				tsNew.setDate2(ts.getDate2());
				tsNew.setDate2Original(ts.getDate2());
				tsNew.allocateDataSpace(res.getVolmax());
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDataUnitsOriginal(ts.getDataUnits());
			}
			else {
				tsNew = null;
			}
			animationData.setHelperTS(0, i, tsNew);
		}
		animationData.setHelperTSField(0, 3);
		for (int i = 0; i < size; i++) {
			res = (StateMod_Reservoir)stations.get(i);

			ts = (MonthTS)tsResHist.get(i);	

			if (ts != null) {
				tsNew = (MonthTS)TSUtil.newTimeSeries(ts.getIdentifierString(), true);
				tsNew.setIdentifier(ts.getIdentifierString());
				tsNew.setDescription("MinCapacity");
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDate1(ts.getDate1());
				tsNew.setDate1Original(ts.getDate1());
				tsNew.setDate2(ts.getDate2());
				tsNew.setDate2Original(ts.getDate2());
				tsNew.allocateDataSpace(res.getVolmin());
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDataUnitsOriginal(ts.getDataUnits());
			}
			else {
				tsNew = null;
			}
			animationData.setHelperTS(1, i, tsNew);
		}		
		animationData.setHelperTSField(1, 4);
		dataV.add(animationData);

		size = tsResBTS.size();
		animationData = new GeoViewAnimationData(layerData, tsResBTS,
			"CurrentCapacity", "Date",
			"Reservoir EOM (Simulated): ",
			"Reservoir EOM", true, 
			GeoViewAnimationData.RADIOBUTTON, max[0]);
		animationData.setNumHelperTS(2);
		for (int i = 0; i < size; i++) {
			res = (StateMod_Reservoir)stations.get(i);

			ts = (MonthTS)tsResHist.get(i);	

			if (ts != null) {
				tsNew = (MonthTS)TSUtil.newTimeSeries(ts.getIdentifierString(), true);
				tsNew.setIdentifier(ts.getIdentifierString());
				tsNew.setDescription("MaxCapacity");
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDate1(ts.getDate1());
				tsNew.setDate1Original(ts.getDate1());
				tsNew.setDate2(ts.getDate2());
				tsNew.setDate2Original(ts.getDate2());
				tsNew.allocateDataSpace(res.getVolmax());
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDataUnitsOriginal(ts.getDataUnits());
			}
			else {
				tsNew = null;
			}
			animationData.setHelperTS(0, i, tsNew);
		}
		animationData.setHelperTSField(0, 3);
		for (int i = 0; i < size; i++) {
			res = (StateMod_Reservoir)stations.get(i);

			ts = (MonthTS)tsResHist.get(i);	

			if (ts != null) {
				tsNew = (MonthTS)TSUtil.newTimeSeries(ts.getIdentifierString(), true);
				tsNew.setIdentifier(ts.getIdentifierString());
				tsNew.setDescription("MinCapacity");
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDate1(ts.getDate1());
				tsNew.setDate1Original(ts.getDate1());
				tsNew.setDate2(ts.getDate2());
				tsNew.setDate2Original(ts.getDate2());
				tsNew.allocateDataSpace(res.getVolmin());
				tsNew.setDataUnits(ts.getDataUnits());
				tsNew.setDataUnitsOriginal(ts.getDataUnits());
			}
			else {
				tsNew = null;
			}
			animationData.setHelperTS(1, i, tsNew);
		}	
		animationData.setHelperTSField(1, 4);
		dataV.add(animationData);

	}
	catch (Exception e) {
		Message.printWarning(1, routine,"Error setting up animation layer data.");
		Message.printWarning(2, routine, e);
	}

	try {
		printStatus("Opening animation control GUI", WAIT);
		GeoViewAnimationJFrame g = new GeoViewAnimationJFrame(this,__geoViewJPanel, dataV, range[0], range[1]);
		g.setVisible(true);
	}
	catch (Exception e) {
		Message.printWarning(1, routine,"Error opening animation control GUI.");
		Message.printWarning(2, routine, e);
	}


	printStatus("Ready", READY);
	
	__geoViewJPanel.getGeoView().redraw();
	JGUIUtil.forceRepaint(__geoViewJPanel.getGeoView());
	JGUIUtil.setWaitCursor(this, false);
}

/**
Create and display a summary layer on the map.
@param delimited_file Delimited file containing attributes to create a new
layer.  Currently the first column must be identifiers that match the
AppJoinField information in available layers.  The second column is an optional
description, and the remaining fields are numerical data values.
*/
public void addSummaryMapLayer ( String delimited_file ) 
{	String rtn = "StateModGUI_JFrame.addSummaryMapLayer";
	try {
		List avail_app_layer_types = new Vector ();
		avail_app_layer_types.add ( "Diversion" );
		avail_app_layer_types.add ( "DiversionWell" );
		avail_app_layer_types.add ( "InstreamFlow" );
		avail_app_layer_types.add ( "Reservoir" );
		avail_app_layer_types.add ( "Streamflow" );
		avail_app_layer_types.add ( "Well" );

/*
		Vector tableFields = DataTable.parseDelimitedFileHeader (delimited_file, ",");
		int num_fields = tableFields.size();

		Message.printStatus ( 1, rtn,
		"Read " + (num_fields - 2) + " data fields (" + num_fields +
		" total fields) from \"" + delimited_file+ "\"" );

		// Default field type is string so set data fields to double...

		for ( int i=2; i<num_fields; i++ ) {
			((TableField)tableFields.elementAt(i)).setDataType ( TableField.DATA_TYPE_DOUBLE );	
		}

		// Now read the file and properly handle the field types...

		DataTable attribute_table =	DataTable.parseDelimitedFile ( delimited_file,",", tableFields, 1 );

		__geoViewJPanel.addSummaryLayerView ( attribute_table, "Summary", 0, 2, avail_app_layer_types, true );
*/
		__geoViewJPanel.addSummaryMapLayer(delimited_file);
	}
	catch ( Exception e ) {
		Message.printWarning ( 1, rtn, "Unable to add summary layer." );
		Message.printWarning ( 2, rtn, e );
	}
}

/**
Check whether any data set components (files) have been edited.  If so, remind
the user to save files.  This should be called before running the model with assumed current files.
@return false if the run process should be cancelled, true to continue with the run.
*/
private boolean checkForEdits ()
{	if ( __dataset.isDirty() ) {
		int x = new ResponseJDialog(
			__dataset_wm.getWindow(StateMod_DataSet_WindowManager.WINDOW_MAIN), "Run StateMod",
			"Some data have been changed but not saved to file.\n"
			+ "You can save these changes by selecting the\n"
			+ "\"File...Save\" menu.\n"
			+ "Do you wish to continue without saving these changes?",
			ResponseJDialog.YES|ResponseJDialog.NO).response();
		if (x == ResponseJDialog.NO) {
			return false;
		}
	}
	return true;
}

/**
Check the GUI state.  This method disables/enables menus as necessary based on
the status of the data set.  For example, if no data set has been read in, then
Data menus are not enabled.  This method should be called directly as data
lists are edited (e.g., so that when a list has zero items, the data menus
will be disabled.  See also the updateWindowStatus() method, which should be
called when the user opens/saves/closes a data set.
*/
private void checkGUIState ()
{	
	Runnable r = new Runnable() {
        public void run() {
			boolean has_data;	// Used to check whether any data component in
						// a group has data (therefore the group menu
						// should be set a certain way).
			boolean enabled;	// Used when enabling/disabling secondary components.
		
			// File menu...
		
			if ( (__dataset == null) || !__dataset.isDirty() ) {
				JGUIUtil.setEnabled ( __File_Save_JMenuItem, false );
			}
			else {
				JGUIUtil.setEnabled ( __File_Save_JMenuItem, true );
			}
			
			// FIXME SAM 2008-03-19 Evaluate whether network should be enabled
			// For now always disable.
			JGUIUtil.setEnabled ( __File_New_Network_JMenuItem, false );
		
			// Edit menu...
		
			if ( __dataset != null ) {
				// The add menu is always enabled because some components will have data...
		
				JGUIUtil.setEnabled ( __Edit_JMenu, true );
				JGUIUtil.setEnabled ( __Edit_Add_JMenu, true );
		
				// Only enable the main data if the time series files were
				// read.  Otherwise the time series files cannot be updated correctly.
		
				if ( __dataset.areTSRead() ) {
					if ( !shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_STREAMGAGE_STATIONS) ) {
						JGUIUtil.setEnabled ( __Edit_Add_StreamGageStation_JMenuItem, false );
						enabled = false;
					}
					else {
						JGUIUtil.setEnabled ( __Edit_Add_StreamGageStation_JMenuItem, true );
						if ( __dataset.getComponentForComponentType(StateMod_DataSet.COMP_STREAMGAGE_STATIONS).hasData() ) {
							// Only allow time series to be added when the stations have data...
							enabled = true;
						}
						else {
							enabled = false;
						}
					}
					JGUIUtil.setEnabled ( __Edit_Add_StreamGageHistoricalTSMonthly_JMenuItem, enabled );
					JGUIUtil.setEnabled ( __Edit_Add_StreamGageHistoricalTSDaily_JMenuItem, enabled );
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Add_StreamGageStation_JMenuItem, false );
					JGUIUtil.setEnabled ( __Edit_Add_StreamGageHistoricalTSMonthly_JMenuItem, false );
					JGUIUtil.setEnabled ( __Edit_Add_StreamGageHistoricalTSDaily_JMenuItem,false);
				}
				// Delay tables..
				if ( !shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY) ) {
					JGUIUtil.setEnabled ( __Edit_Add_DelayTableMonthly_JMenuItem, false );
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Add_DelayTableMonthly_JMenuItem, true );
				}
				if ( !shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DELAY_TABLES_DAILY) ) {
					JGUIUtil.setEnabled(__Edit_Add_DelayTableDaily_JMenuItem, false);
				}
				else {
					JGUIUtil.setEnabled(__Edit_Add_DelayTableDaily_JMenuItem, true);
				}
		
				if ( __dataset.areTSRead() ) {
					JGUIUtil.setEnabled ( __Edit_Add_Diversion_JMenuItem, true );
					if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_DIVERSION_STATIONS)) {
						JGUIUtil.setEnabled ( __Edit_Add_Diversion_JMenuItem, false );
						enabled = false;
					}
					else {
						JGUIUtil.setEnabled ( __Edit_Add_Diversion_JMenuItem, true );
						if ( __dataset.getComponentForComponentType( StateMod_DataSet.COMP_DIVERSION_STATIONS).hasData()) {
							// Only allow time series to be added when the stations have data...
							enabled = true;
						}
						else {
							enabled = false;
						}
					}
					JGUIUtil.setEnabled ( __Edit_Add_DiversionHistoricalTSMonthly_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionHistoricalTSDaily_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandTSMonthly_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandOverrideTSMonthly_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandTSAverageMonthly_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandTSDaily_JMenuItem,enabled);
					// TODO - for now don't allow irrigation practice to be edited via StateMod GUI...
					//JGUIUtil.setEnabled (__Edit_Add_DiversionIrrigationPracticeTSYearly_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionIrrigationPracticeTSYearly_JMenuItem,	false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_JMenuItem, enabled);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_JMenuItem,	enabled);
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Add_Diversion_JMenuItem, false );
					JGUIUtil.setEnabled ( __Edit_Add_DiversionHistoricalTSMonthly_JMenuItem, false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionHistoricalTSDaily_JMenuItem,false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandTSMonthly_JMenuItem,false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandOverrideTSMonthly_JMenuItem,false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandTSAverageMonthly_JMenuItem, false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionDemandTSDaily_JMenuItem,false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionIrrigationPracticeTSYearly_JMenuItem,false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_JMenuItem,false);
					JGUIUtil.setEnabled ( __Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_JMenuItem,false);
				}
				if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY)) {
					JGUIUtil.setEnabled (__Edit_Add_PrecipitationTS_JMenuItem,false);
				}
				else {
					// Always enable the time series for precipitation because these time series are always read...
					JGUIUtil.setEnabled (__Edit_Add_PrecipitationTS_JMenuItem,true);
				}
				if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY)) {
					JGUIUtil.setEnabled ( __Edit_Add_EvaporationTS_JMenuItem, false);
				}
				else {
					// Always enable the time series for evaporation because these time series are always read...
					JGUIUtil.setEnabled ( __Edit_Add_EvaporationTS_JMenuItem, true);
				}
				if ( __dataset.areTSRead() ) {
					if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_RESERVOIR_STATIONS)) {
						JGUIUtil.setEnabled ( __Edit_Add_Reservoir_JMenuItem, false );
						enabled = false;
					}
					else {
						JGUIUtil.setEnabled ( __Edit_Add_Reservoir_JMenuItem, true );
						if (__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_RESERVOIR_STATIONS).hasData()) {
							// Only allow time series to be added when the stations have data...
							enabled = true;
						}
						else {
							enabled = false;
						}
					}
					JGUIUtil.setEnabled (__Edit_Add_ReservoirContentTSMonthly_JMenuItem,enabled);
					JGUIUtil.setEnabled (__Edit_Add_ReservoirContentTSDaily_JMenuItem, enabled );
					JGUIUtil.setEnabled (__Edit_Add_ReservoirTargetTSMonthly_JMenuItem, enabled);
					JGUIUtil.setEnabled (__Edit_Add_ReservoirTargetTSDaily_JMenuItem, enabled );
				}
				else {
					JGUIUtil.setEnabled (__Edit_Add_Reservoir_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_ReservoirContentTSMonthly_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_ReservoirContentTSDaily_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_ReservoirTargetTSMonthly_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_ReservoirTargetTSDaily_JMenuItem, false );
				}
				if ( __dataset.areTSRead() ) {
					if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_INSTREAM_STATIONS)) {
						JGUIUtil.setEnabled (__Edit_Add_InstreamFlow_JMenuItem, false );
						enabled = false;
					}
					else {
						JGUIUtil.setEnabled (__Edit_Add_InstreamFlow_JMenuItem, true );
						if (__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_INSTREAM_STATIONS).hasData()) {
							// Only allow time series to be added when the stations have data...
							enabled = true;
						}
						else {
							enabled = false;
						}
					}
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlowDemandTSMonthly_JMenuItem,enabled);
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlowDemandTSAverageMonthly_JMenuItem,enabled );
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlowDemandTSDaily_JMenuItem,enabled );
				}
				else {
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlow_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlowDemandTSMonthly_JMenuItem,false);
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlowDemandTSAverageMonthly_JMenuItem,false );
					JGUIUtil.setEnabled (__Edit_Add_InstreamFlowDemandTSDaily_JMenuItem, false );
				}
				if ( __dataset.areTSRead() ) {
					if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_WELL_STATIONS)) {
						JGUIUtil.setEnabled (__Edit_Add_Well_JMenuItem, false );
						enabled = false;
					}
					else {
						JGUIUtil.setEnabled (__Edit_Add_Well_JMenuItem, true );
						if (__dataset.getComponentForComponentType(StateMod_DataSet.COMP_WELL_STATIONS).hasData()) {
							// Only allow time series to be added when the stations have data...
							enabled = true;
						}
						else {
							enabled = false;
						}
					}
					JGUIUtil.setEnabled (__Edit_Add_WellPumpingTSMonthly_JMenuItem, enabled );
					JGUIUtil.setEnabled (__Edit_Add_WellPumpingTSDaily_JMenuItem, enabled );
					JGUIUtil.setEnabled (__Edit_Add_WellDemandTSMonthly_JMenuItem, enabled );
					JGUIUtil.setEnabled (__Edit_Add_WellDemandTSDaily_JMenuItem, enabled );
					// TODO - for now don't allow irrigation practice to be edited via StateMod GUI...
					//JGUIUtil.setEnabled (__Edit_Add_WellIrrigationPracticeTSYearly_JMenuItem,enabled);
					JGUIUtil.setEnabled (__Edit_Add_WellIrrigationPracticeTSYearly_JMenuItem,false);
					JGUIUtil.setEnabled (__Edit_Add_WellConsumptiveWaterRequirementTSMonthly_JMenuItem,enabled);
					JGUIUtil.setEnabled (__Edit_Add_WellConsumptiveWaterRequirementTSDaily_JMenuItem,enabled);
				}
				else {
					JGUIUtil.setEnabled (__Edit_Add_Well_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_WellPumpingTSMonthly_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_WellPumpingTSDaily_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_WellDemandTSMonthly_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_WellDemandTSDaily_JMenuItem, false );
					JGUIUtil.setEnabled (__Edit_Add_WellConsumptiveWaterRequirementTSMonthly_JMenuItem,false);
					JGUIUtil.setEnabled (__Edit_Add_WellConsumptiveWaterRequirementTSDaily_JMenuItem,false);
				}
				if ( __dataset.areTSRead() ) {
					JGUIUtil.setEnabled (__Edit_Add_StreamEstimateStationConvert_JMenuItem,true);
					JGUIUtil.setEnabled (__Edit_Add_StreamEstimateStationNew_JMenuItem, true );
				}
				else {
					JGUIUtil.setEnabled (__Edit_Add_StreamEstimateStationConvert_JMenuItem,false );
					JGUIUtil.setEnabled (__Edit_Add_StreamEstimateStationNew_JMenuItem, false );
				}
				if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_RIVER_NETWORK) ) {
					JGUIUtil.setEnabled ( __Edit_Add_OtherNode_JMenuItem, false );
				}
				else {
					// Always allow other node to be added...
					JGUIUtil.setEnabled ( __Edit_Add_OtherNode_JMenuItem, true );
				}
				if ( !shouldComponentEditsBeAllowed( StateMod_DataSet.COMP_OPERATION_RIGHTS)) {
					JGUIUtil.setEnabled ( __Edit_Add_OperationalRight_JMenuItem,false );
				}
				else {
					// Always allow operational rights to be added...
					JGUIUtil.setEnabled ( __Edit_Add_OperationalRight_JMenuItem,true );
				}
		
				has_data = false;
				if (__dataset.areTSRead() && shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_STREAMGAGE_STATIONS) &&
						__dataset.getComponentForComponentType(StateMod_DataSet.COMP_STREAMGAGE_STATIONS).hasData()) {
					JGUIUtil.setEnabled (__Edit_Delete_StreamGageStation_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled (__Edit_Delete_StreamGageStation_JMenuItem, false );
				}
				if ( shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY) &&
					__dataset.getComponentForComponentType( StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY).hasData()) {
					// TODO (JTS - 2006-03-07) Disabled for Phase 3
					// JGUIUtil.setEnabled ( __Edit_Delete_DelayTableMonthly_JMenuItem, true );
					JGUIUtil.setEnabled (__Edit_Delete_DelayTableMonthly_JMenuItem, false );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_DelayTableMonthly_JMenuItem, false );
				}
				if ( shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DELAY_TABLES_DAILY) &&
					__dataset.getComponentForComponentType( StateMod_DataSet.COMP_DELAY_TABLES_DAILY).hasData()) {
					// TODO (JTS - 2006-03-07) Disabled for Phase 3
					// JGUIUtil.setEnabled ( __Edit_Delete_DelayTableDaily_JMenuItem, true );
					JGUIUtil.setEnabled ( __Edit_Delete_DelayTableDaily_JMenuItem, false );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_DelayTableDaily_JMenuItem, false );
				}
				if ( __dataset.areTSRead() && shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_DIVERSION_STATIONS) &&
					__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_DIVERSION_STATIONS).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_Diversion_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled (__Edit_Delete_Diversion_JMenuItem, false );
				}
				if (shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY) &&
					__dataset.getComponentForComponentType(StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_PrecipitationTS_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_PrecipitationTS_JMenuItem, false );
				}
				if ( shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY) &&
					__dataset.getComponentForComponentType(StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_EvaporationTS_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled (__Edit_Delete_EvaporationTS_JMenuItem, false );
				}
				if ( __dataset.areTSRead() && shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_RESERVOIR_STATIONS) &&
					__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_RESERVOIR_STATIONS).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_Reservoir_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_Reservoir_JMenuItem, false );
				}
				if ( __dataset.areTSRead() && shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_INSTREAM_STATIONS) &&
					__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_INSTREAM_STATIONS).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_InstreamFlow_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_InstreamFlow_JMenuItem, false );
				}
				if ( __dataset.areTSRead() && shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_WELL_STATIONS) &&
					__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_WELL_STATIONS).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_Well_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_Well_JMenuItem, false );
				}
				if ( __dataset.areTSRead() && shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS) &&
					__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_StreamEstimateStation_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_StreamEstimateStation_JMenuItem, false );
				}
				// For now enable if the network has data.
				// TODO - for more robust code, only enable if at least one
				// node in the network does not show up in the other files.
				// Do not put the check in now because it will be a performance
				// hit every time this method is called...
				if ( shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_RIVER_NETWORK) &&
					__dataset.getComponentForComponentType( StateMod_DataSet.COMP_RIVER_NETWORK).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_OtherNode_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_OtherNode_JMenuItem, false );
				}
				if ( shouldComponentEditsBeAllowed(StateMod_DataSet.COMP_OPERATION_RIGHTS) &&
					__dataset.getComponentForComponentType( StateMod_DataSet.COMP_OPERATION_RIGHTS).hasData()) {
					JGUIUtil.setEnabled ( __Edit_Delete_OperationalRight_JMenuItem, true );
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_OperationalRight_JMenuItem, false );
				}
				// Set the main menu...
				if ( has_data ) {
					JGUIUtil.setEnabled ( __Edit_Delete_JMenu, true );
				}
				else {
					JGUIUtil.setEnabled ( __Edit_Delete_JMenu, false );
				}
			}
			else {
				// Disable access to all the menus...
				JGUIUtil.setEnabled ( __Edit_JMenu, false );
				JGUIUtil.setEnabled ( __Edit_Add_JMenu, false );
				JGUIUtil.setEnabled ( __Edit_Delete_JMenu, false );
			}
		
			// View menu...
		
			if ( __dataset != null ) {
				JGUIUtil.setEnabled ( __View_JMenu, true );
				JGUIUtil.setEnabled ( __View_DataSetSummary_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __View_JMenu, false );
				JGUIUtil.setEnabled ( __View_DataSetSummary_JMenuItem, false );
			}
		
			// Data (Control)...
		
			if ( __dataset != null ) {
				JGUIUtil.setEnabled ( __Data_JMenu, true );
				JGUIUtil.setEnabled ( __Data_Control_JMenu, true );
				if ( (__dataset.getComponentForComponentType( StateMod_DataSet.COMP_CONTROL)).hasData() ) {
					JGUIUtil.setEnabled ( __Data_Control_Control_JMenuItem, true );
				}
				else {
					JGUIUtil.setEnabled ( __Data_Control_Control_JMenuItem, false );
				}
				// TODO - should this always be enabled?
				//if (	(__dataset.getComponentForComponentType(StateMod_DataSet.COMP_RESPONSE)).hasData()) {
					JGUIUtil.setEnabled ( __Data_Control_Response_JMenuItem, true );
				//}
				//else {
					//JGUIUtil.setEnabled (__Data_Control_Response_JMenuItem, false );
				//}
				// TODO - always allow output control to be enabled?
				// Right now the file is not read in until the output control
				// window is displayed.
			}
			else {
				JGUIUtil.setEnabled ( __Data_JMenu, false );
				JGUIUtil.setEnabled ( __Data_Control_JMenu, false );
			}
			
			// Consumptive use (StateCU location)...
			
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_STATECU_STRUCTURE)).hasData()) {
				JGUIUtil.setEnabled ( __Data_ConsumptiveUse_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_ConsumptiveUse_JMenuItem, false );
			}
		
			// Data (Stream Gage)...
		
			if ( (__dataset != null) &&	(__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_STREAMGAGE_STATIONS)).hasData()) {
				JGUIUtil.setEnabled ( __Data_StreamGage_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_StreamGage_JMenuItem, false );
			}
		
			// Data (Delay Tables)...
		
			has_data = false;
			if ( __dataset != null ) {
				if ( (__dataset.getComponentForComponentType(
					StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY)).hasData()) {
					JGUIUtil.setEnabled ( __Data_DelayTables_Monthly_JMenuItem,true);
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Data_DelayTables_Monthly_JMenuItem,false);
				}
				if ( (__dataset.getComponentForComponentType(
					StateMod_DataSet.COMP_DELAY_TABLES_DAILY)).hasData()) {
					JGUIUtil.setEnabled ( __Data_DelayTables_Daily_JMenuItem,true);
					has_data = true;
				}
				else {
					JGUIUtil.setEnabled ( __Data_DelayTables_Daily_JMenuItem,false);
				}
			}
			if ( has_data ) {
				JGUIUtil.setEnabled ( __Data_DelayTables_JMenu, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_DelayTables_JMenu, false );
			}
		
			// Data (Diversions)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_DIVERSION_STATIONS)).hasData() ) {
				JGUIUtil.setEnabled ( __Data_Diversions_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_Diversions_JMenuItem, false );
			}
		
			// Data (Precipitation)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY)).hasData()) {
				JGUIUtil.setEnabled ( __Data_Precipitation_Monthly_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_Precipitation_Monthly_JMenuItem, false );
			}
		
			// Data (Evaporation)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY)).hasData()) {
				JGUIUtil.setEnabled ( __Data_Evaporation_Monthly_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_Evaporation_Monthly_JMenuItem, false );
			}
		
			// Data (Reservoirs)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_RESERVOIR_STATIONS)).hasData()) {
				JGUIUtil.setEnabled ( __Data_Reservoirs_JMenuItem, true);
			}
			else {
				JGUIUtil.setEnabled ( __Data_Reservoirs_JMenuItem, false);
			}
		
			// Data (Instream Flows)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_INSTREAM_STATIONS)).hasData()) { 
				JGUIUtil.setEnabled ( __Data_InstreamFlows_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_InstreamFlows_JMenuItem, false );
			}
		
			// Data (Wells)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_WELL_STATIONS)).hasData()) {
				JGUIUtil.setEnabled ( __Data_Wells_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_Wells_JMenuItem, false );
			}
		
			// Data (Plans)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_PLANS)).hasData()) {
				JGUIUtil.setEnabled ( __Data_Plans_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_Plans_JMenuItem, false );
			}
		
			// Data (River Baseflows)...
			// Ray Bennett has not adopted the StreamEstimateStations response file keyword
			// (or at least old data sets don't have), so as long as stream gages are available show this.
		
			if ( (__dataset != null) && ((__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS)).hasData() ||
				(__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_STREAMGAGE_STATIONS)).hasData()) ) {
				JGUIUtil.setEnabled ( __Data_StreamEstimate_JMenuItem, true);
			}
			else {
				JGUIUtil.setEnabled ( __Data_StreamEstimate_JMenuItem, false);
			}
		
			// Data (River network)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_RIVER_NETWORK)).hasData()) {
				JGUIUtil.setEnabled ( __Data_RiverNetwork_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Data_RiverNetwork_JMenuItem, false );
			}
		
			// Data (Operational rights)...
		
			if ( (__dataset != null) && (__dataset.getComponentForComponentType(
				StateMod_DataSet.COMP_OPERATION_RIGHTS)).hasData()) {
				JGUIUtil.setEnabled ( __Data_OperationalRights_JMenuItem,true);
			}
			else {
				JGUIUtil.setEnabled ( __Data_OperationalRights_JMenuItem,false);
			}
		
			// Run menu...
		
			if ( __dataset != null ) {
				JGUIUtil.setEnabled ( __Run_Baseflows_JMenuItem, true );
				JGUIUtil.setEnabled ( __Run_BaseflowsFast_JMenuItem, true );
				JGUIUtil.setEnabled ( __Run_SimulateFast_JMenuItem, true );
				JGUIUtil.setEnabled ( __Run_Simulate_JMenuItem, true );
				JGUIUtil.setEnabled ( __Run_DataCheck_JMenuItem, true );
				JGUIUtil.setEnabled ( __Run_Report_JMenuItem, true );
			}
			else {
				JGUIUtil.setEnabled ( __Run_Baseflows_JMenuItem, false );
				JGUIUtil.setEnabled ( __Run_BaseflowsFast_JMenuItem, false );
				JGUIUtil.setEnabled ( __Run_SimulateFast_JMenuItem, false );
				JGUIUtil.setEnabled ( __Run_Simulate_JMenuItem, false );
				JGUIUtil.setEnabled ( __Run_DataCheck_JMenuItem, false );
				JGUIUtil.setEnabled ( __Run_Report_JMenuItem, false );
			}
		
			// Results menu...
		
			if ( __dataset != null ) {
				JGUIUtil.setEnabled ( __Results_JMenu, true );
				JGUIUtil.setEnabled ( __Results_GraphingTool_JMenuItem,true);
				if ( __dataset.areTSRead() ) {
					JGUIUtil.setEnabled ( __Results_GraphDemandSupplySummary_JMenuItem, true);
				}
				else {
					JGUIUtil.setEnabled ( __Results_GraphDemandSupplySummary_JMenuItem, false);
				}
				JGUIUtil.setEnabled (__Results_ProcessTSProduct_JMenuItem,true);
			}
			else {
				JGUIUtil.setEnabled ( __Results_JMenu, false );
				JGUIUtil.setEnabled ( __Results_GraphingTool_JMenuItem,false);
				JGUIUtil.setEnabled ( __Results_GraphDemandSupplySummary_JMenuItem, false);
				JGUIUtil.setEnabled(__Results_ProcessTSProduct_JMenuItem,false);
			}
		
			// Tools menu...
		
			if ( __dataset != null ) {
				JGUIUtil.setEnabled ( __Tools_RunSmDelta_JMenuItem, true );
				JGUIUtil.setEnabled (__Tools_AddSummaryMapLayer_JMenuItem,true);
				JGUIUtil.setEnabled (__Tools_AddDifference_JMenuItem, true);
				JGUIUtil.setEnabled (__Tools_AddPercentDifference_JMenuItem, true);
				JGUIUtil.setEnabled (__Tools_AddAnimationMapLayer_JMenuItem, true);
				JGUIUtil.setEnabled (__Tools_QueryTool_JMenuItem, true);
				JGUIUtil.setEnabled (__Tools_ReportModifiedData_JMenuItem,true);
				JGUIUtil.setEnabled (__Tools_NetworkSummary_JMenuItem,true);
				JGUIUtil.setEnabled ( __Tools_ReportUnusedData_JMenuItem, true);
				JGUIUtil.setEnabled ( __Tools_SaveDataSetAsKML_JMenuItem, true);
			}
			else {
				JGUIUtil.setEnabled ( __Tools_RunSmDelta_JMenuItem, false );
				JGUIUtil.setEnabled(__Tools_AddSummaryMapLayer_JMenuItem,false);
				JGUIUtil.setEnabled (__Tools_AddDifference_JMenuItem, false);
				JGUIUtil.setEnabled (__Tools_AddPercentDifference_JMenuItem, false);		
				JGUIUtil.setEnabled(__Tools_AddAnimationMapLayer_JMenuItem,	false);
				JGUIUtil.setEnabled (__Tools_QueryTool_JMenuItem, false);
				JGUIUtil.setEnabled(__Tools_ReportModifiedData_JMenuItem,false);
				JGUIUtil.setEnabled ( __Tools_ReportUnusedData_JMenuItem,false);
				JGUIUtil.setEnabled (__Tools_NetworkSummary_JMenuItem, false);
				JGUIUtil.setEnabled ( __Tools_SaveDataSetAsKML_JMenuItem, false);
		
			}
        }
	};
    if ( SwingUtilities.isEventDispatchThread() ) {
        r.run();
    }
    else 
    {
        SwingUtilities.invokeLater ( r );
    }
}

/**
Close the GUI, allowing the user to save the data set if something has changed.
*/
public void closeGUI()
{	if ( (__dataset != null) && __dataset.isDirty() ) {
		// There are modified data so print a slightly longer message...
		int x = new ResponseJDialog(this, "Exit StateMod",
		"Some data have been changed.\nThese changes will not " +
		"be saved unless you select \"File...Save\"." +
		"\n\nAre you sure you want to exit without saving?",
		ResponseJDialog.YES | ResponseJDialog.NO).response();
		if (x == ResponseJDialog.YES) {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			StateModGUI.quitProgram (0);
		}
	}
	else {
		int x = new ResponseJDialog(this, "Exit StateMod", "Are you sure you want to exit?",
		ResponseJDialog.YES | ResponseJDialog.NO).response();
		if (x == ResponseJDialog.YES) {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			StateModGUI.quitProgram(0);
		}
	}
	// Cancel so return control to calling code...
	setDefaultCloseOperation (WindowConstants.DO_NOTHING_ON_CLOSE);
	setVisible(true);
}

/**
Close any open data views, called before opening a new data set.
*/
private void closeOpenDataViews()
{
	// Close open data windows.
	__dataset_wm.closeAllWindows();
	// Close the network editor...
	StateMod_Network_JFrame networkJFrame = getNetworkEditor();
	__dataset_wm.setNetworkEditor(null);
	if ( networkJFrame != null ) {
		// Close it
		networkJFrame.close ( true );
		setNetworkEditor ( null );
	}
	// Clear the tree...
	getDatasetJTree().clear();
	// Clear the map...
	getGeoViewJPanel().removeAllLayerViews();
	getGeoViewJPanel().removeAllAnnotations();
	// TODO SAM 2010-12-19 Need to fix - GUI takes over control immediately with a dialog 
	// and the screen does not refresh well.
}

/**
Event for component listener
*/
public void componentHidden ( ComponentEvent event )
{
	// No need to do anything
}

/**
Event for component listener
*/
public void componentMoved ( ComponentEvent event )
{
	// No need to do anything
}

/**
Event for component listener.  Use this to do some intelligent sizing on the components, in particular
to lock the JTree size to its previous value, rather than letting the layout manager do it - seems to
have a hard time with the split pane, etc.
*/
public void componentResized ( ComponentEvent event )
{
	/* TODO SAM 2010-12-19 This does not seem to be needed with the splitpane setup now
	 * but leave code for now in case something more complex is needed.
	StateMod_DataSet_JTree tree = getDatasetJTree();
	Dimension treeCurrentDimension = tree.getSize(); // for height
	Dimension treeDesiredDimension = treeCurrentDimension;
	if ( __treeDimension != null ) {
		// Retain the previous width but use the current height
		treeDesiredDimension = new Dimension ( __treeDimension.width, treeCurrentDimension.height );
		// This may be ignored by the layout manager
		tree.setSize ( treeDesiredDimension );
		// This hopefully is used by the layout manager...
		tree.setPreferredSize(treeDesiredDimension);
	}
	// Force a redraw for better sizing...
	tree.invalidate();
	// Save the dimensions for the next resize
	__geoViewDimension = getGeoViewJPanel().getSize();
	Message.printStatus ( 2, "", "In componentResized:  GeoView panel size is " + __geoViewDimension );
	__treeDimension = getDatasetJTree().getSize();
	Message.printStatus ( 2, "", "In componentResized:  Tree size is " + __treeDimension );
	*/
}

//private Dimension __geoViewDimension = null;
//private Dimension __treeDimension = null;
/**
Event for component listener.  This should be called the first time that the UI is 
*/
public void componentShown ( ComponentEvent event )
{
	// Save the initial dimensions of the GeoView and tree
	/*
	__geoViewDimension = getGeoViewJPanel().getSize();
	Message.printStatus ( 2, "", "In componentShown:  GeoView panel size is " + __geoViewDimension );
	__treeDimension = getDatasetJTree().getSize();
	Message.printStatus ( 2, "", "In componentShown:  Tree size is " + __treeDimension );
	*/
}

/**
TODO - may move this to where it is more easily accessed by other code.
Display a graph showing the major water components to help illustrate demand and supply.
*/
private void displayDemandSupplySummaryGraph ()
{	String routine = "StateModGUI_JFrame.displayDemandSupplySummaryGraph";
	TS ts = null;

	// Create needed time series, that will be filled from monthly data...

	// Reservoir storage...

	MonthTS reservoir_MonthTS = new MonthTS();
	try {
		reservoir_MonthTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY ) + ".Month" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	reservoir_MonthTS.setDescription ( "Total Storage" );
	reservoir_MonthTS.setDate1 ( __dataset.getDataStart() );
	reservoir_MonthTS.setDate2 ( __dataset.getDataEnd() );
	reservoir_MonthTS.allocateDataSpace ( 0.0 );
	reservoir_MonthTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY ) );
	DateTime date_end = new DateTime ( reservoir_MonthTS.getDate2() );

	YearTS reservoir_YearTS = new YearTS();
	try {
		reservoir_YearTS.setIdentifier ( "Total.StateMod.Content.Year");
	}
	catch ( Exception e ) {
		// should not happen
	}
	reservoir_YearTS.setDescription ( "End of June Storage" );
	reservoir_YearTS.setDate1 ( __dataset.getDataStart() );
	reservoir_YearTS.setDate2 ( __dataset.getDataEnd() );
	reservoir_YearTS.allocateDataSpace ( 0.0 );
	reservoir_YearTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY ) );

	double value;
	DataSetComponent comp = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_RESERVOIR_STATIONS );
	List data = (List)comp.getData();
	int size = data.size();
	StateMod_Reservoir res;
	DateTime date;
	for ( int i = 0; i < size; i++ ) {
		res = (StateMod_Reservoir)data.get(i);
		ts = res.getContentMonthTS();
		if ( ts == null ) {
			continue;
		}
		date = new DateTime ( reservoir_MonthTS.getDate1() );
		for ( ; date.lessThanOrEqualTo(date_end); date.addMonth ( 1 )){
			value = ts.getDataValue ( date );
			if ( !ts.isDataMissing(value) ) {
				reservoir_MonthTS.setDataValue ( date, reservoir_MonthTS.getDataValue(date) + value );
				if ( date.getMonth() == 6 ) {
					reservoir_YearTS.setDataValue ( date, reservoir_YearTS.getDataValue(date) + value );
				}
			}
		}
	}

	// Historical diversions...

	MonthTS div_MonthTS = new MonthTS();
	try {
		div_MonthTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY ) + ".Month" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	div_MonthTS.setDescription ( "Total Diversions" );
	div_MonthTS.setDate1 ( __dataset.getDataStart() );
	div_MonthTS.setDate2 ( __dataset.getDataEnd() );
	div_MonthTS.allocateDataSpace ( 0.0 );
	div_MonthTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY ) );

	YearTS div_YearTS = new YearTS();
	try {
		div_YearTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY ) + ".Year" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	div_YearTS.setDescription ( "Total Diversions (Yearly)" );
	div_YearTS.setDate1 ( __dataset.getDataStart() );
	div_YearTS.setDate2 ( __dataset.getDataEnd() );
	div_YearTS.allocateDataSpace ( 0.0 );
	div_YearTS.setDataUnits ( StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY ) );

	// Consumptive water requirement...

	MonthTS iwr_MonthTS = new MonthTS();
	try {
		iwr_MonthTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ) +
			".Month" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	iwr_MonthTS.setDescription ( "Total Consumptive Water Requirement" );
	iwr_MonthTS.setDate1 ( __dataset.getDataStart() );
	iwr_MonthTS.setDate2 ( __dataset.getDataEnd() );
	iwr_MonthTS.allocateDataSpace ( 0.0 );
	iwr_MonthTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ) );

	YearTS iwr_YearTS = new YearTS();
	try {	iwr_YearTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ) +
		".Year" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	iwr_YearTS.setDescription("Total Consumptive Water Requirement (Yearly)" );
	iwr_YearTS.setDate1 ( __dataset.getDataStart() );
	iwr_YearTS.setDate2 ( __dataset.getDataEnd() );
	iwr_YearTS.allocateDataSpace ( 0.0 );
	iwr_YearTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY ) );

	// Demands...

	MonthTS ddm_MonthTS = new MonthTS();
	try {	ddm_MonthTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_DEMAND_TS_MONTHLY ) + ".Month" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	ddm_MonthTS.setDescription ( "Total Demand" );
	ddm_MonthTS.setDate1 ( __dataset.getDataStart() );
	ddm_MonthTS.setDate2 ( __dataset.getDataEnd() );
	ddm_MonthTS.allocateDataSpace ( 0.0 );
	ddm_MonthTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_DEMAND_TS_MONTHLY ) );

	YearTS ddm_YearTS = new YearTS();
	try {	ddm_YearTS.setIdentifier ( "Total.StateMod." +
		StateMod_DataSet.lookupTimeSeriesDataType(StateMod_DataSet.COMP_DEMAND_TS_MONTHLY ) + ".Year" );
	}
	catch ( Exception e ) {
		// should not happen
	}
	ddm_YearTS.setDescription("Total Demand (Yearly)" );
	ddm_YearTS.setDate1 ( __dataset.getDataStart() );
	ddm_YearTS.setDate2 ( __dataset.getDataEnd() );
	ddm_YearTS.allocateDataSpace ( 0.0 );
	ddm_YearTS.setDataUnits (
		StateMod_DataSet.lookupTimeSeriesDataUnits(StateMod_DataSet.COMP_DEMAND_TS_MONTHLY ) );

	comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_STATIONS );
	data = (List)comp.getData();
	size = data.size();
	StateMod_Diversion div;
	TS div_ts = null;
	TS iwr_ts = null;
	TS ddm_ts = null;
	for ( int i = 0; i < size; i++ ) {
		div = (StateMod_Diversion)data.get(i);
		div_ts = div.getDiversionMonthTS();
		iwr_ts = div.getConsumptiveWaterRequirementMonthTS();
		ddm_ts = div.getDemandMonthTS();
		date = new DateTime ( div_MonthTS.getDate1() );
		for ( ; date.lessThanOrEqualTo(date_end); date.addMonth ( 1 )){
			if ( div_ts != null ) {
				value = div_ts.getDataValue ( date );
				if ( !div_ts.isDataMissing(value) ) {
					div_MonthTS.setDataValue ( date, div_MonthTS.getDataValue(date) + value);
					div_YearTS.setDataValue ( date, div_YearTS.getDataValue(date) + value );
				}
			}
			if ( iwr_ts != null ) {
				value = iwr_ts.getDataValue ( date );
				if ( !iwr_ts.isDataMissing(value) ) {
					iwr_MonthTS.setDataValue ( date, iwr_MonthTS.getDataValue(date) + value);
					iwr_YearTS.setDataValue ( date, iwr_YearTS.getDataValue(date) + value );
				}
			}
			if ( ddm_ts != null ) {
				value = ddm_ts.getDataValue ( date );
				if ( !ddm_ts.isDataMissing(value) ) {
					ddm_MonthTS.setDataValue ( date, ddm_MonthTS.getDataValue(date) + value);
					ddm_YearTS.setDataValue ( date, ddm_YearTS.getDataValue(date) + value );
				}
			}
		}
	}

	// Set the graph properties.  Do separate graphs for monthly and yearly...

	PropList display_props = new PropList("TSView");

	display_props.set("InitialView", "Graph");
	display_props.set("TSViewTitleString", "Demand/Supply Summary" );
	display_props.set("DisplayFont", "Courier");
	display_props.set("DisplaySize", "11");
	display_props.set("PrintFont", "Courier");
	display_props.set("PrintSize", "7");
	display_props.set("PageLength", "100");

	PropList props_MonthTS = new PropList("Month");
	props_MonthTS.set("Product.TotalWidth", "600");
	props_MonthTS.set("Product.TotalHeight", "400");

	int sub = 0;
	int its = 0;

	// Monthly graph...

	List tslist_MonthTS = new Vector();

	// Reservoir content...

	sub = 0;
	its = 0;
	props_MonthTS.set ( "SubProduct " + sub + ".GraphType=Line" );
	props_MonthTS.set ( "SubProduct " + ++sub + ".SubTitleString=Total Reservoir Storage (Monthly)" );
	props_MonthTS.set ( "SubProduct " + sub + ".SubTitleFontSize=12" );
	props_MonthTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + reservoir_MonthTS.getIdentifierString() );
	tslist_MonthTS.add ( reservoir_MonthTS );

	// Diversions and other ACFT "flows"...

	++sub;
	its = 0;
	props_MonthTS.set ( "SubProduct " + sub + ".GraphType=Line" );
	props_MonthTS.set ( "SubProduct " + sub + ".SubTitleString=Total Demand and Supply Terms (Monthly)" );
	props_MonthTS.set ( "SubProduct " + sub + ".SubTitleFontSize=12" );
	props_MonthTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + div_MonthTS.getIdentifierString() );
	tslist_MonthTS.add ( div_MonthTS );
	props_MonthTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + iwr_MonthTS.getIdentifierString() );
	tslist_MonthTS.add ( iwr_MonthTS );
	props_MonthTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + ddm_MonthTS.getIdentifierString() );
	tslist_MonthTS.add ( ddm_MonthTS );

	// Yearly graph...

	PropList props_YearTS = new PropList("Year");
	props_YearTS.set("Product.TotalWidth", "600");
	props_YearTS.set("Product.TotalHeight", "400");
	List tslist_YearTS = new Vector();

	// Reservoir content...

	sub = 0;
	its = 0;
	props_YearTS.set ( "SubProduct " + sub + ".GraphType=Line" );
	props_YearTS.set ( "SubProduct " + ++sub + ".SubTitleString=Total Reservoir Storage (End of June, Yearly)" );
	props_YearTS.set ( "SubProduct " + sub + ".SubTitleFontSize=12" );
	props_YearTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + reservoir_YearTS.getIdentifierString() );
	tslist_YearTS.add ( reservoir_YearTS );

	// Diversions and other ACFT "flows"...

	++sub;
	its = 0;
	props_YearTS.set ( "SubProduct " + sub + ".GraphType=Line" );
	props_YearTS.set ( "SubProduct " + sub + ".SubTitleString=Total Demand and Supply Terms (Yearly)" );
	props_YearTS.set ( "SubProduct " + sub + ".SubTitleFontSize=12" );
	props_YearTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + div_YearTS.getIdentifierString() );
	tslist_YearTS.add ( div_YearTS );
	props_YearTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + iwr_YearTS.getIdentifierString() );
	tslist_YearTS.add ( iwr_YearTS );
	props_YearTS.set ( "Data " + sub + "." + (++its) + ".TSID=" + ddm_YearTS.getIdentifierString() );
	tslist_YearTS.add ( ddm_YearTS );

	// Display both at once...

	try {
		TSProduct tsproduct_MonthTS = new TSProduct ( props_MonthTS, display_props );
		tsproduct_MonthTS.setTSList ( tslist_MonthTS );
		new TSViewJFrame ( tsproduct_MonthTS );
		TSProduct tsproduct_YearTS = new TSProduct ( props_YearTS, display_props );
		tsproduct_YearTS.setTSList ( tslist_YearTS );
		new TSViewJFrame ( tsproduct_YearTS );
	}
	catch (Exception e) {
		Message.printWarning(1,routine, "Error displaying time series (" + e + ").", this);
		Message.printWarning(2, routine, e);
	}
}

/**
Clean up before garbage collection.
*/
protected void finalize()
throws Throwable
{	super.finalize();
}

/**
Return the label for a GeoRecord.
*/
public String geoViewGetLabel(GeoRecord geoRecord)
{	if ( geoRecord.getShape().associated_object == null ) {
		return null;
	}
	return ( (StateMod_Data)geoRecord.getShape().associated_object).getMapLabel();
}

public void geoViewInfo(GRShape devlim, GRShape datalim, List selected) {}
public void geoViewInfo(GRPoint devlim, GRPoint datalim, List selected) {}
public void geoViewInfo(GRLimits devlim, GRLimits datalim, List selected) {}
public void geoViewMouseMotion(GRPoint devpt, GRPoint datapt) {}

/**
This routine responds to a user's single click in the map area.  The GUI
will react by displaying the edit window for the selected record.
@param dev_shape Select shape, using device coordinates.
@param data_shape Select shape, using data coordinates.
@param selected Selected GeoRecord.
@param append Indicates if selects are being appended (ignored in this method).
*/
public void geoViewSelect(	GRShape dev_shape, GRShape data_shape,List selected, boolean append)
{	String routine = "StateModGUI_JFrame.geoViewSelect";
	GeoRecord geoRecord = null;
	Message.printStatus ( 2, routine, "Features have been selected on the "+
		"map.  Attempting to display corresponding data window..." );
	if (dev_shape.type != GRShape.POINT) {
		// Don't know how to do a region...
		Message.printWarning(1, routine, "Can only select points\nClick near a feature to display details.");
		return;
	}
	List data_Vector = null;	// Used below for data Vector
	try {
		if ((selected == null)||(selected.size() == 0)) {
			Message.printStatus(1, routine, "Nothing selected.");
			return;
		}
		
		// Now have >= 1 selected records...
		// Get the first geoRecord from "selected" vector, which will
		// be the nearest object.  During the initial read of spatial
		// data, the shape is matched with an object and only shapes
		// match the data set are visible.  Therefore, there should
		// always be a match with StateMod data.  This is the case with
		// either the old interface or the new GeoView interface.

		geoRecord = (GeoRecord)selected.get(0);
		Message.printStatus(1, routine, "Number of stations returned: "+
		selected.size());

		// Get the selected identifier to be matched with those in the StateMod data set...

		String type = geoRecord.getLayer().getAppLayerType();
		Message.printStatus(1, routine, "Selected application layer type \"" + type + "\"");

		String station_id;
		// New way uses the AppJoinField to link to the application...
		GeoLayer layer = geoRecord.getLayer();
		GeoLayerView layer_view=geoRecord.getLayerView();
		GRLegend legend = layer_view.getLegend();
		String join_field_string = layer_view.getPropList().getValue("AppJoinField");
		if (join_field_string == null) {
			Message.printWarning(1, routine, "Need to specify AppJoinField in GeoView " +
			"project file\nto link to StateMod in layer \"" + legend.getText() + "\".");
			return;
		}
		DataTable table = layer.getAttributeTable();
		if (table == null) {
			Message.printWarning(1, routine, "No attributes available to search in \"" + legend.getText() + "\".");
			return;
		}
		int field_index = table.getFieldIndex(join_field_string);
		if (field_index <= 0) {
			Message.printWarning(1, routine,
			"Can't find field \"" + join_field_string + "\" to search in \"" + legend.getText() +"\".");
			return;
		}
		// Get the field value...
		try {
			Object o = table.getFieldValue(geoRecord.getShape().index,field_index);
			station_id = o.toString();
		}
		catch (Exception e) {
			Message.printWarning(1, routine, "Could not find value for field \"" +
			join_field_string + "\" in \"" + legend.getText() + "\"");
			return;
		}
		// Should now have the identifier to match.

		Message.printStatus(1, routine, "Selected identifier \"" + station_id + "\"");

		// Display corresponding edit window and select station_id

		if ( type.equalsIgnoreCase("Baseflow")) {
			data_Vector = (List)
			(__dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS).getData());
			int pos = StateMod_Util.indexOf(data_Vector,station_id);
			if ( pos < 0 ) {
				Message.printStatus(1, routine,	"Unable to find Baseflow \"" + station_id + "\" in StateMod data.");
				return;
			}
		/* TODO- need to enable window
			__dataset_wm.displayWindow(__dataset_wm.WINDOW_STREAMESTIMATE);
			((StateMod_RiverBaseflow_JFrame)__dataset_wm.getWindow(__dataset_wm.WINDOW_STREAMESTIMATE)).selectTableIndex(pos);
		*/
		}
		//else if (type.equals(SMGISResponse.DIVERSIONS)) {}
		// Above is "diversions"
		else if (type.equalsIgnoreCase("Diversion")||type.equalsIgnoreCase("DiversionWell")) {
			data_Vector = (List)(__dataset.getComponentForComponentType (
			StateMod_DataSet.COMP_DIVERSION_STATIONS).getData());
			int pos = StateMod_Util.indexOf(data_Vector,station_id);
			if ( pos < 0 ) {
				Message.printStatus(1, routine, "Unable to find Diversion \"" + station_id + "\" in StateMod data.");
				return;
			}
			__dataset_wm.displayWindow(	StateMod_DataSet_WindowManager.WINDOW_DIVERSION);
			((StateMod_Diversion_JFrame)__dataset_wm.getWindow(
				StateMod_DataSet_WindowManager.WINDOW_DIVERSION)).selectTableIndex(pos, true, true);
		}
		//else if (type.equals(SMGISResponse.WELLS)) {}
		// Above is "wells"
		else if (type.equalsIgnoreCase("Well")) {
			data_Vector = (List)(__dataset.getComponentForComponentType (
			StateMod_DataSet.COMP_WELL_STATIONS).getData());
			int pos = StateMod_Util.indexOf(data_Vector,station_id);
			if ( pos < 0 ) {
				Message.printStatus(1, routine, "Unable to find Well \"" + station_id + "\" in StateMod data.");
				return;
			}
			__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_WELL);
			((StateMod_Well_JFrame)__dataset_wm.getWindow(
				StateMod_DataSet_WindowManager.WINDOW_WELL)).selectTableIndex(pos);
		}
		//else if (type.equalsIgnoreCase(
		// SMGISResponse.INSTREAM)) {}
		// Above is "instream"
		else if (type.equalsIgnoreCase("InstreamFlow")) {
			data_Vector = (List)(__dataset.getComponentForComponentType (
			StateMod_DataSet.COMP_INSTREAM_STATIONS).getData());
			int pos = StateMod_Util.indexOf(data_Vector,station_id);
			if ( pos < 0 ) {
				Message.printStatus(1, routine, "Unable to find InstreamFlow \"" + station_id + "\" in StateMod data.");
				return;
			}
			__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_INSTREAM);
			((StateMod_InstreamFlow_JFrame)
				__dataset_wm.getWindow(StateMod_DataSet_WindowManager.WINDOW_INSTREAM)).selectTableIndex(pos);
		}
		else if (type.equals("Precipitation")) {
			__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_PRECIPITATION);
			// TODO - still need to fill in
		}
		//else if (type.equalsIgnoreCase(
		//SMGISResponse.RESERVOIRS)) {}
		// Above is "reservoirs"
		else if (type.equalsIgnoreCase("Reservoir")) {
			data_Vector = (List)(__dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_STATIONS).getData());
			int pos = StateMod_Util.indexOf(data_Vector,station_id);
			if ( pos < 0 ) {
				Message.printStatus(1, routine, "Unable to find Reservoir \"" + station_id + "\" in StateMod data.");
				return;
			}
			Message.printStatus ( 2, routine, "Displaying reservoir window for record " + pos );
			__dataset_wm.displayWindow( StateMod_DataSet_WindowManager.WINDOW_RESERVOIR);
			((StateMod_Reservoir_JFrame)__dataset_wm.getWindow(
				StateMod_DataSet_WindowManager.WINDOW_RESERVOIR)).selectTableIndex(pos);
		}
		//else if (
		// type.equalsIgnoreCase(SMGISResponse.STREAMFLOW)) {}
		// above is "streamflows"
		else if (type.equalsIgnoreCase("Streamflow")) {
			data_Vector = (List)
			(__dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_STATIONS).getData());
			int pos = StateMod_Util.indexOf(data_Vector,station_id);
			if ( pos < 0) {
				Message.printStatus(1, routine, "Unable to find Streamflow \"" + station_id + "\" in StateMod data.");
				return;
			}
			__dataset_wm.displayWindow(StateMod_DataSet_WindowManager.WINDOW_STREAMGAGE);
			((StateMod_StreamGage_JFrame)__dataset_wm.getWindow(
				StateMod_DataSet_WindowManager.WINDOW_STREAMGAGE)).selectTableIndex(pos);
		}
		geoRecord = null;
		station_id = null;
		type = null;
	}
	catch (Exception e) {
		Message.printWarning(2, routine, e);
	}
}

public void geoViewSelect(GRPoint devShape, GRPoint dataShape, List selected, boolean append) {
	geoViewSelect((GRShape)devShape,(GRShape)dataShape, selected, append);
}

public void geoViewSelect(GRLimits devShape, GRLimits dataShape, List selected, boolean append) {
	geoViewSelect((GRShape)devShape,(GRShape)dataShape, selected, append);
}

public void geoViewZoom(GRShape devlim, GRShape datalim) {}
public void geoViewZoom(GRPoint devlim, GRPoint datalim) {}
public void geoViewZoom(GRLimits devlim, GRLimits datalimt) {}

/**
Return the current data set being used, or null if dataset has not been selected.
*/
protected StateMod_DataSet getDataSet ()
{
	return __dataset;
}

/**
Return the dataset JTree that displays components.
*/
private StateMod_DataSet_JTree getDatasetJTree ()
{
	return __datasetJTree;
}

/**
Return the data set window manager that is currently being used.
@return the data set window manager that is currently being used.
*/
public StateMod_DataSet_WindowManager getDataSetWindowManager ( )
{	return __dataset_wm;
}

/**
Return the application layer types for a StateMod component.
*/
private List<String> getGeoViewAppLayerTypeList ( int compType )
{	List<String> appLayerTypeList = new Vector();
	if ( compType == StateMod_DataSet.COMP_DIVERSION_STATIONS ) {
		appLayerTypeList.add ( "Diversion" );
		appLayerTypeList.add ( "DiversionWell" );
	}
	else if ( compType == StateMod_DataSet.COMP_INSTREAM_STATIONS ) {
		appLayerTypeList.add ( "InstreamFlow" );
	}
	else if ( compType == StateMod_DataSet.COMP_PLANS ) {
		appLayerTypeList.add ( "Plan" );
	}
	else if ( compType == StateMod_DataSet.COMP_RESERVOIR_STATIONS ) {
		appLayerTypeList.add ( "Reservoir" );
	}
	else if ( compType == StateMod_DataSet.COMP_STREAMGAGE_STATIONS ) {
		appLayerTypeList.add ( "Streamflow" );
	}
	else if ( compType == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS ) {
		appLayerTypeList.add ( "Streamflow" );
	}
	else if ( compType == StateMod_DataSet.COMP_WELL_STATIONS ) {
		appLayerTypeList.add ( "Well" );
		appLayerTypeList.add ( "DiversionWell" );
	}
	return appLayerTypeList;
}

/**
Return the GeoViewJPanel.
*/
private GeoViewJPanel getGeoViewJPanel()
{
	return __geoViewJPanel;
}

/**
Return the last folder from which a response file was selected.
*/
private String getLastResponseFileFolder ()
{
	return __lastResponseFileFolder;
}

/**
Checks a MonthTS and determines if its date range is greater than the date
range passed in.  Returns the maximum date range of all the dates available.
@param tsV a Vector of MonthTS.
@param first the first date to check the Vector of TS's dates against.  Can be null.
@param last the last date to check the Vector of TS's dates against.  Can be null.
@return a 2-element DateTime array, the first value of which is the earliest
date (from among 'first' and all the MonthTS) and the second value of which 
is the latest date (from among 'last' and all the MonthTS).
*/
private DateTime[] getMonthTSDateRange(List tsv, DateTime first, DateTime last)
{
	DateTime[] range = new DateTime[2];
	MonthTS mts = null;
	int size = tsv.size();
	DateTime temp;
	for (int i = 0; i < size; i++) {
		mts = (MonthTS)tsv.get(i);

		if (mts != null) {
			temp = mts.getDate1();
			if (first == null) {
				first = temp;
			}
			else if (temp.lessThan(first)) {
				first = temp;
			}
	
			temp = mts.getDate2();
			if (last == null){ 
				last = temp;
			}
			else if (temp.greaterThan(last)) {
				last = temp;
			}
		}
	}
	range[0] = first;
	range[1] = last;
	return range;
}

/**
Returns the maximum value from a Vector of MonthTS objects. 
@param tsv Vector of MonthTS.
@return the maximum value from a Vector of MonthTS objects.
*/
private double getMonthTSMax(List tsv) {
	double max = -999.0;
	int size = tsv.size();
	MonthTS mts = null;
	TSLimits limits = null;
	for (int i = 0; i < size; i++) {
		mts = (MonthTS)tsv.get(i);
		if (mts != null) {
			limits = TSUtil.getDataLimits(mts, mts.getDate1(), mts.getDate2());
			if (limits.getMaxValue() > max) {
				max = limits.getMaxValue();
			}
		}
	}
	return max;
}

/**
Return the network editor.
@return the network editor.
*/
private StateMod_Network_JFrame getNetworkEditor ()
{
	return __networkJFrame;
}

// FIXME SAM 2008-03-16 Evaluate putting in more generic code.
/**
 * Determines if specified file is write-able.
 * <p>
 * A file is write-able if it:
 * <ul>
 * <li> exists
 * <li> is a file (not a directory)
 * <li> user has write permission
 * <ul>
 * 
 * @param selectedFile
 * @return true, if file is write-able, otherwise false
 */
public boolean isWriteable(File selectedFile)
{
	if (selectedFile != null && (selectedFile.canWrite() == false || selectedFile.isFile() == false)) {
		// Post an error message and return
		JOptionPane.showMessageDialog(this, "You do not have permission to write to file:" + "\n" +
			selectedFile.getPath(), "File not Writable", JOptionPane.ERROR_MESSAGE);
		return false;
    }
	else {
		return true;
	}
}

/**
Link spatial data to StateMod data objects.  This requires joining the data based on the AppJoinField
property in the GeoView map definition.
*/
private void linkSpatialAndStateModData ()
{	String routine = getClass().getName() + "linkSpatialAndStateModData";
	// Loop through each category of data, getting the StateMod list of data and the associated layer
	// views.  Then process each.  This allows the same methods to be called when adding new stations or
	// deleting from the data set.
	GeoViewJPanel geoViewJPanel = getGeoViewJPanel();
	
	int [] compTypes = {
		// TODO SAM 2010-12-21 How to handle precipitation/evap since not true stations?
		StateMod_DataSet.COMP_DIVERSION_STATIONS,
		StateMod_DataSet.COMP_INSTREAM_STATIONS,
		StateMod_DataSet.COMP_RESERVOIR_STATIONS,
		StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS,
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS,
		StateMod_DataSet.COMP_WELL_STATIONS,
		StateMod_DataSet.COMP_PLANS
	};
	StateMod_DataSet dataset = getDataSet();
	for ( int iComp = 0; iComp < compTypes.length; iComp++ ) {
		try {
			List<String> appTypeList = getGeoViewAppLayerTypeList ( compTypes[iComp] );
			DataSetComponent comp = dataset.getComponentForComponentType(compTypes[iComp]);
			List dataList = (List)comp.getData();
			//geoViewJPanel.setAppFeatureVisibility ( appTypeList, StateMod_Util.getIDList(diversionList,true),
	//			showFeaturesInDataSet, showFeaturesNotInDataSet );
			List<StateMod_Data> smdataList = (List<StateMod_Data>)dataList;
			if ( appTypeList.size() > 0 ) {
				linkSpatialAndStateModDataForOneComponent ( comp.getComponentName(), geoViewJPanel,
					appTypeList, smdataList );
			}
			// Create CSV files for StateMod component types that were not matched.
			linkSpatialAndStateModData_CreateMissingCSV ( smdataList,
				dataset.getComponentFileExtension(compTypes[iComp]) );
		}
		catch ( Exception e ) {
			Message.printWarning ( 3, routine, "Error linking component (" + e + ").");
			Message.printWarning ( 3, routine, e );
		}
	}
	// The river network nodes are not shown on the map legend because nodes match points from all layers.
	// However, it is useful to be able to annotate the river nodes on the map so loop through the stations
	// for each data type and match the spatial data with the river nodes...
	HasGeoRecord hasGeoRecord;
	GeoRecord geoRecord;
	List<StateMod_RiverNetworkNode> riverNetworkNodeList = (List<StateMod_RiverNetworkNode>)
	    dataset.getComponentForComponentType(StateMod_DataSet.COMP_RIVER_NETWORK).getData();
	int rinMatchCount = 0;
	for ( int iComp = 0; iComp < compTypes.length; iComp++ ) {
		DataSetComponent comp = dataset.getComponentForComponentType(compTypes[iComp]);
		List dataList = (List)comp.getData();
		List<StateMod_Data> smdataList = (List<StateMod_Data>)dataList;
		for ( StateMod_Data smdata: smdataList ) {
			if ( smdata instanceof HasGeoRecord ) {
				hasGeoRecord = (HasGeoRecord)smdata;
				geoRecord = hasGeoRecord.getGeoRecord();
				if ( (geoRecord != null) && (geoRecord.getShape() != null) ) {
					// Have spatial data.  Try to match network node ID
					for ( StateMod_RiverNetworkNode node: riverNetworkNodeList ) {
						if ( node.getID().equalsIgnoreCase(smdata.getID())) {
							if ( (node.getGeoRecord() != null) && (node.getGeoRecord().getShape() != null) ) {
								// Already matched...
							}
							else {
								// No georecord matched yet so use this object's...
								node.setGeoRecord(hasGeoRecord.getGeoRecord());
								++rinMatchCount;
							}
							// No need to keep processing river nodes...
							break;
						}
					}
				}
			}
		}
	}
	Message.printStatus ( 2, routine, "River network nodes matched " + rinMatchCount + " of " +
		riverNetworkNodeList.size() + " nodes with spatial data (using locations from other StateMod data)." );
	// Create CSV files for StateMod component types that were not matched.
	linkSpatialAndStateModData_CreateMissingCSV ( riverNetworkNodeList,
		dataset.getComponentFileExtension(StateMod_DataSet.COMP_RIVER_NETWORK) );
}

/**
Set the visibility of features based on a check of an attribute value (e.g., a
string identifier).  The AppLayerType data in the GeoView project is used to
identify suitable layers for the check.
@param appLayerTypeList If specified, this contains a list of AppLayerType
string properties for layers that should be searched.  Specifying this
information increases the speed of searches because it limits the layers that are searched.
@param smdataList The data attributes corresponding to the AppJoinField
property saved with a GeoLayerView.  One or more field values can be given, separated by commas.
@return list of GeoRecord for the selected features, or null if nothing is selected.
*/
public void linkSpatialAndStateModDataForOneComponent ( String dataType, GeoViewJPanel geoViewPanel,
	List<String> appLayerTypeList, List<StateMod_Data> smdataList0 )
{	String routine = getClass().getName() + ".linkSpatialAndStateModData";

	// Copy the StateMod data list into a new list.  As items are matched, they are removed from the
	// list to shorten it, thus speeding up execution.  Also copy the identifiers into an array
	// and convert to uppercase to reduce equalsIgnoreCase() hit.
    int smdataList0Size = smdataList0.size();
	List<StateMod_Data> smdataList = new Vector(smdataList0.size());
	List<String> upperCaseIDList = new Vector(smdataList0.size());
	StateMod_Data smdata;
	for ( int i = 0; i < smdataList0.size(); i++ ) {
		upperCaseIDList.add( smdataList0.get(i).getID().toUpperCase());
	}
	smdataList.addAll(smdataList0);
	int smdataListSize = smdataList.size();
	int smdataListSizeOriginal = smdataListSize;
	
	List<GeoLayerView> layerViewList = geoViewPanel.getLayerViews(appLayerTypeList);

	String layerAppJoinField; // Fields to join the application data to the spatial data
	String joinFieldValue;
	GRShape shape;
	String featureID;
	GeoRecord geoRecord;
	StopWatch sw = new StopWatch();
	sw.start();
	int matchCount = 0;
	for ( GeoLayerView layerView: layerViewList ) {
		// Get the layer and join field...
		layerAppJoinField = layerView.getPropList().getValue ("AppJoinField");
		if ( layerAppJoinField == null ) {
			// The layer is not attached to any application data so no need to process...
			continue;
		}
		GeoLayer layer = layerView.getLayer();
		DataTable table = layer.getAttributeTable();
		int layerAppJoinFieldIndex = -1;
		try {
			layerAppJoinFieldIndex = layer.getAttributeTable().getFieldIndex(layerAppJoinField);
		}
		catch ( Exception e ) {
			Message.printWarning(3, routine, "Error getting layer \"" + layerView.getName() +
				"\" column number for attribute \"" + layerAppJoinField +
				"\" cannot set feature visibility (" + e + ").");
			continue;
		}
		if ( layerAppJoinFieldIndex < 0 ) {
			// The attribute table does not have the requested field
			Message.printWarning(3, routine, "Layer \"" + layerView.getName() +
				"\" does not have attribute \"" + layerAppJoinField + "\" cannot match to StateMod data." );
			continue;
		}
		// Loop through the features and try to match the join field
		int nFeatures = layer.getShapes().size();
		for ( int iFeature = 0; iFeature < nFeatures; iFeature++ ) {
			shape = layer.getShape(iFeature);
			// TODO SAM 2010-12-21 Allow other than strings to be joined
			try {
				// Important - let the table retrieve the value because it may be coming from
				// the disk on not memory
				joinFieldValue = ("" + table.getFieldValue(iFeature,layerAppJoinFieldIndex)).toUpperCase();
			}
			catch ( Exception e ) {
				// Should not happen
				Message.printWarning(3, routine, "Error getting attribute \"" + layerAppJoinField +
					"\" value from layer \"" + layerView.getName() +
					"\" - match to StateMod data (" + e + ")." );
				Message.printWarning(3, routine, e);
				continue;
			}
			for ( int iSmdata = 0; iSmdata < smdataListSize; iSmdata++ ) {
				smdata = smdataList.get(iSmdata );
				featureID = upperCaseIDList.get(iSmdata);
				//Message.printStatus(2, routine, "Comparing feature ID \"" + featureID +
				//	"\" with attribute table value \"" + joinFieldValue + "\"" );
				if ( joinFieldValue.equals(featureID) ) {
					// Have a match
					// Set the shape associated object (this is used later for fast searches of
					// features in our out of the data set).
					if ( shape == null ) {
						// Need to keep searching for a shape that has is not null
						// This can occur, for example, when the layer is read from a CSV and only
						// some coordinates are specified correctly
						continue;
					}
					shape.associated_object = smdata;
					// TODO - because the table records may not exist in memory can't call
					//table.getRecord((int)shape.index
					// for now pass null
					geoRecord = new GeoRecord( shape,
						//rec,
						null, layer, layerView );
					// StateMod_Data does not have GeoRecord - only specific types do...
					if ( smdata instanceof HasGeoRecord ) {
						// Call the generic interface method...
						((HasGeoRecord)smdata).setGeoRecord(geoRecord);
					}
					// Remove the item from the StateMod list to speed performance in next iterations
					upperCaseIDList.remove(iSmdata);
					smdataList.remove(iSmdata);
					--smdataListSize;
					// No need to keep searching.
					++matchCount;
					break;
				}
				if ( smdataListSize == 0 ) {
					// No need to keep searching for matches
					break;
				}
			}
			if ( matchCount == smdataList0Size ) {
				// All StateMod data objects have been matched so no need to keep searching
				break;
			}
		}
	}
	sw.stop();
	Message.printStatus(2, routine, "Matched " + matchCount + " of " + smdataListSizeOriginal +
		" " + dataType + " objects to " + layerViewList.size() + " layers in " +
		sw.getSeconds() + " seconds.  AppLayerType values used to select layers = " +
		StringUtil.toString(appLayerTypeList,",") );
}

/**
Create a file in the data set folder named "x-gui-MissingSpatial-diversions.csv" or similar, if
any StateMod data object has missing data.
@param stateModDataList list of StateMod data to check
*/
private void linkSpatialAndStateModData_CreateMissingCSV ( List<? extends StateMod_Data> stateModDataList,
	String compNameShort )
{	String routine = getClass().getName() + ".linkSpatialAndStateModData_CreateMissingCSV";
	int size = 0;
	if ( stateModDataList != null ) {
		size = stateModDataList.size();
	}
	// Go through the logic even with zero size because csv file will need to be removed below.
	// First pass detects if GeoRecord is missing or null shape
	int matchCount = 0;
	boolean [] hasGeoRecord = new boolean[size];
	GeoRecord geoRecord = null;
	int i = -1;
	for ( StateMod_Data smdata : stateModDataList ) {
		++i;
		if ( smdata instanceof HasGeoRecord ) {
			geoRecord = ((HasGeoRecord)smdata).getGeoRecord();
			if ( geoRecord != null ) {
				// Has spatial data...
				if ( geoRecord.getShape() != null ) {
					hasGeoRecord[i] = true;
					++matchCount;
				}
			}
		}
	}
	Message.printStatus(2, routine, "StateMod component \"" + compNameShort + "\" has " +
		(size - matchCount) + " locations out of " + size + " with missing spatial data." );
	String filename = __dataset.getDataSetDirectory() + "/x-gui-MissingSpatial-" + compNameShort + ".csv";
	if ( matchCount == size ) {
		// No missing locations so remove the file with missing so as to not confuse the user
		File f = new File ( filename );
		if ( f.exists() ) {
			try {
				f.delete();
			}
			catch ( Exception e ) {
				// Ignore... could be permissions
			}
		}
	}
	else if ( matchCount < size ) {
		// Second pass, open the file and write out the records
		PrintWriter fout = null;
		try {
		    FileOutputStream fos = new FileOutputStream ( filename );
			fout = new PrintWriter ( fos );
			IOUtil.printCreatorHeader(fout, "#", 80, 0 );
			fout.println ( "#" );
			fout.println ( "# " + compNameShort + " has " + (size - matchCount) + " out of " + size +
				" locations with missing spatial data." );
			fout.println ( "#" );
			fout.println ( "# Specify X and Y in projected coordinates to match other layer data." );
			fout.println ( "#" );
			fout.println ( "\"ID\",\"Name\",\"Long\",\"Lat\",\"X\",\"Y\",\"Note\"" );
			StateMod_Data smdata;
			for ( i = 0; i < size; i++ ) {
				if ( hasGeoRecord[i] ) {
					// No need to output this one
					continue;
				}
				smdata = stateModDataList.get(i);
				fout.println(StringUtil.formatStringForCsv(smdata.getID(),false) + "," +
					StringUtil.formatStringForCsv(smdata.getName(),false) +
					",,,,,\"GUI Detected no coordinates\"");
			}
		}
		catch ( Exception e ) {
			Message.printWarning(3, routine, e);
		}
		finally {
			if ( fout != null ) {
				fout.close();
			}
		}
	}
}

/**
Read a GeoView Project file and display the map.
@param gvp_file GeoView project file to use.
@exception if there is an error displaying the data in the GeoView project file.
*/
public void openGVP ( String gvp_file )
throws Exception
{	String routine = "StateModGUI_JFrame.openGVP";
	String full_gvp_file = IOUtil.getPathUsingWorkingDir ( gvp_file );
	if ( !IOUtil.fileExists(full_gvp_file) ) {
		Message.printWarning ( 1, routine, "GeoView project file \"" + gvp_file + "\" does not exist." );
		return;
	}

	// Clear the map...

	__geoViewJPanel.removeAllLayerViews();

	// Now display the new information...

	printStatus("Initializing map interface.", WAIT);
	Message.printStatus(1, "", "Opening GVP \"" + full_gvp_file+"\"...");
	__geoViewJPanel.openGVP(gvp_file);
	// Associate spatial data with the data set...
	try {
		linkSpatialAndStateModData();
	}
	catch ( Exception e ) {
		Message.printWarning(3, routine, "Error linking spatial data to StateMod objects (" + e + ")." );
		Message.printWarning(3,routine,e);
	}
	// Set the visibility so that only spatial data matched with StateMod are shown...
	try {
		setMapFeatureVisibilityAndRedrawMap(__View_Map_ShowFeaturesInDataSet_JMenuItem.getState(),
			__View_Map_ShowFeaturesNotInDataSet_JMenuItem.getState() );
	}
	catch ( Exception e ) {
		Message.printWarning(2, routine, "Error setting map feature visibility (" + e + ")." );
	}
	// Redraw the map
	__geoViewJPanel.getGeoView().redraw();
	printStatus("Opened GVP \"" + full_gvp_file + "\"", READY);
}

/**
Displays a dialog box for the user to select a file and then opens the file 
they selected, readings its contents into __dataset.  This method is called due
to the user selecting File...Open.
*/
private void openResponseFile()
{	String routine = "StateModGUI_JFrame.openResponseFile";

	// TODO - need to clear the existing data set first

	// allow user to select scenario
	JGUIUtil.setWaitCursor(this, true);
	String lastDirectorySelected = getLastResponseFileFolder();
	if ( lastDirectorySelected == null ) {
		// Use the DataHomeDefault property from the GUI configuration file if it is set and exists.
		String propValue = StateModGUI.getConfigurationPropValue("DataHomeDefault");
		if ( (propValue != null) && IOUtil.fileExists(propValue) ) {
			lastDirectorySelected = propValue;
		}
	}

	JFileChooser fc = JFileChooserFactory.createJFileChooser (lastDirectorySelected );

	fc.setDialogTitle("Select StateMod Response File");
	SimpleFileFilter rsp = new SimpleFileFilter("rsp", "StateMod Response Files");
	fc.addChoosableFileFilter(rsp);

	fc.setAcceptAllFileFilterUsed(false);
	fc.setFileFilter(rsp);
	fc.setDialogType(JFileChooser.OPEN_DIALOG);		

	JGUIUtil.setWaitCursor(this, false);
	int retVal = fc.showOpenDialog(this);
	if (retVal != JFileChooser.APPROVE_OPTION) {
		return;
	}
	
	String currDir = (fc.getCurrentDirectory()).toString();
	// Relative paths will be determined from the response file directory...
	IOUtil.setProgramWorkingDir ( currDir );

	if (!currDir.equalsIgnoreCase(lastDirectorySelected)) {
		JGUIUtil.setLastFileDialogDirectory(currDir);
	}
	setLastResponseFileFolder(currDir);
	String filename = fc.getSelectedFile().getName();
	String path = fc.getSelectedFile().getPath();

	JGUIUtil.setWaitCursor(this, true);
	
	// Close down the existing data set views
	closeOpenDataViews();

	// Declare a new data set...

	__dataset = new StateMod_DataSet ( StateMod_DataSet.TYPE_UNKNOWN );
	StateMod_Data.setDataSet(__dataset);
	__datasetStateCU = new StateCU_DataSet();
	//StateCU_Data.setDataSet(__datasetStateCU);

	// Add a process listener so that this GUI can show the progress of reading the data set...

	__dataset.addProcessListener ( this );
	
	try {
		String msg = "Reading data from: \"" + path + "\"";
		Message.printStatus(1, routine, msg);
		printStatus(msg, READY);
		try {
			readStateModResponseFile(path);
		}
		catch ( IllegalArgumentException e ) {
			// Response file is not free format
			Message.printWarning ( 1, routine, "Response file \"" + path +
				"\" is not a valid free-format response file.  If an older format, use SmNewRsp to convert." );
			Message.printWarning ( 2, routine, e );
			JGUIUtil.setWaitCursor(this, false);
			return;
		}
		catch ( Exception e ) {
			// Unexpected error reading file
			Message.printWarning ( 1, routine, "Unexpected error reading response file \"" + path +
			"\".  Verify that the format is correct." );
			Message.printWarning ( 2, routine, e );
			JGUIUtil.setWaitCursor(this, false);
			return;
		}
		// Let the window manager know the data set so that it can get data for displays...
		__dataset_wm.setDataSet ( __dataset );
		__dataset_wm.setDataSet ( __datasetStateCU );
		updateWindowStatus ();
		// Read the GeoView file referenced in the data set...
		try {
			DataSetComponent geoview_comp = __dataset.getComponentForComponentType(StateMod_DataSet.COMP_GEOVIEW);
			if ( !geoview_comp.getDataFileName().equals("") ) {
				openGVP ( __dataset.getDataFilePathAbsolute(geoview_comp ) );
			}
			else {
				Message.printWarning ( 2, routine,
					"No GeoView project file is specified in the data set - will not display map." );
			}
		}
		catch ( Exception e ) {
			Message.printWarning ( 1, routine, "Error displaying map data (" + e + ")." );
			Message.printWarning ( 2, routine, e );
		}
		JGUIUtil.setWaitCursor(this, false);
	}
	catch (Exception ex) {
		printStatus("Unexpected error reading response file (" + ex +
			").  Use File...Open to select a valid StateMod response file (*.rsp).", READY );
		JGUIUtil.setWaitCursor(this, false);
		return;		
	}

	// Set base name for the data set (response file without extension)...

	int last = filename.indexOf(".");
	__dataset.setBaseName ( filename.substring(0,last) );
	String msg = "Read all information for: \"" + filename + "\"";
	Message.printStatus(1, routine, msg);
	printStatus(msg, READY);

	if (!__dataset.getComponentForComponentType(StateMod_DataSet.COMP_NETWORK).hasData()) {

		int x = (new ResponseJDialog(this, "No network file found.",
			"No network file has been found using the \"Network\" file type in the response file.\n\n"
			+ "To select a network file (*.net) now, press 'Yes.'\nTo create a new blank "
			+ "network, press 'No.'\nPressing 'Cancel' will skip reading the network "
		    + "and continue reading other files.",
			ResponseJDialog.YES | ResponseJDialog.NO | ResponseJDialog.CANCEL)).response();

		if (x == ResponseJDialog.YES) {
			fc = JFileChooserFactory.createJFileChooser(JGUIUtil.getLastFileDialogDirectory());
			fc.setDialogTitle("Select Network File");
			SimpleFileFilter xff = new SimpleFileFilter("net", "StateDMI/StateMod GUI XML Network Files");
			fc.addChoosableFileFilter(xff);
			fc.setFileFilter(xff);
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			if ((fc.showOpenDialog(this)) != JFileChooser.APPROVE_OPTION) {
				// Cancel...
				__View_Network_JMenuItem.setState(false);
				return;
			}

			currDir = (fc.getCurrentDirectory()).toString();
			filename = fc.getSelectedFile().getPath();
			
			try {
				StateMod_NodeNetwork network = StateMod_NodeNetwork.readStateModNetworkFile( filename, null, true);
				__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_NETWORK).setData(network);
				String dir = __dataset.getDataSetDirectory();
				Message.printStatus(1, "", "Dir: " + dir);
				Message.printStatus(1, "", "Filename: " + filename);
				filename = IOUtil.toRelativePath(dir,filename);
				Message.printStatus(1, "", "Filename: " + filename);
				__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_NETWORK).setDataFileName(filename);
				__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_NETWORK).setVisible(true);
				__networkJFrame = new StateMod_Network_JFrame( network);
				__dataset_wm.setNetworkEditor(__networkJFrame);
				__networkJFrame.setInStateModGUI(true);
				__networkJFrame.addWindowListener(this);
				__View_Network_JMenuItem.setState(true);
				// dirty because it now has a .net file
				__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_RESPONSE).setDirty(true);
			}
			catch (Exception ex) {
				Message.printWarning(1, routine, "Unexpected error reading network (" + ex + ").");
				Message.printWarning(2, routine, ex);
				__networkJFrame = null;
				__View_Network_JMenuItem.setState(false);
			}		
		}
		else if (x == ResponseJDialog.NO) {
			try {
				__networkJFrame = new StateMod_Network_JFrame( null, null, true);
				__networkJFrame.setInStateModGUI(true);
				__networkJFrame.addWindowListener(this);
				__View_Network_JMenuItem.setState(true);
				__dataset_wm.setNetworkEditor(__networkJFrame);
				// with a new network there are some complications ...
				// 1) need to pull out the network when the dataset is saved and save it
				// 2) need to set the dataset as dirty if the network is saved
			}
			catch (Exception ex) {
				Message.printWarning(1, routine, "Unexpected error creating network (" + ex + ").");
				Message.printWarning(2, routine, ex);
				__networkJFrame = null;
				__View_Network_JMenuItem.setState(false);
			}		
		}
		else {}
	}
	else {
		try {
			StateMod_NodeNetwork network = (StateMod_NodeNetwork)
				__dataset.getComponentForComponentType(	StateMod_DataSet.COMP_NETWORK).getData();
			__networkJFrame = new StateMod_Network_JFrame(network);	
			__networkJFrame.setInStateModGUI(true);
			__networkJFrame.addWindowListener(this);
			__View_Network_JMenuItem.setState(true);
			__dataset_wm.setNetworkEditor(__networkJFrame);
			Message.printStatus(1, "", "Network read from response file.");
		}
		catch (Exception ex) {
			Message.printWarning(1, routine, "Error reading network (" + ex + ").");
			Message.printWarning(2, routine, ex);
			__networkJFrame = null;
			__View_Network_JMenuItem.setState(false);
		}
	}

	checkGUIState();	// Checks menus.

	__datasetJTree.clear();
	__datasetJTree.setDataSet(__dataset);
	__datasetJTree.displayDataSet();

	JGUIUtil.setWaitCursor(this, false);
}

/**
Prints message in the one line status bar on the main GUI.
@param message the message to print in the status bar
@param status the status to display (either WAIT or READY)
*/
public void printStatus(String message, int status)
{	__statusBar.setText(message);
	if (status == WAIT) {
		__statusBarReadyWait.setText("WAIT");
	}
	else {
		__statusBarReadyWait.setText("READY");
	}
	JGUIUtil.forceRepaint(__statusBar);
	JGUIUtil.forceRepaint(__statusBarReadyWait);
}

/**
Handle error output from a ProcessListener (currently not used).
*/
public void processError(String error)
{
}

/**
Handle output from a ProcessListener (currently not used).
*/
public void processOutput ( String output )
{
}

/**
Handle output from a ProcessListener.  This is used to display progress as input files are read.
*/
public void processStatus(int code, String message)
{	if ( code == StateMod_GUIUtil.STATUS_READ_START ) {
		// Message is assumed to contain the full message...
		printStatus ( message, WAIT);
	}
	else if ( code == StateMod_GUIUtil.STATUS_READ_COMPLETE ) {
		// Message is assumed to contain the full message...
		printStatus ( message, READY );
	}
}

// TODO SAM 2007-02-18 Evaluate whether needed
/**
Method to read a TSProduct file and process the product.
@param show_view If true, the graph will be displayed on the screen.  If false,
an output file will be prompted for.
*/
/*
private void processTSProductFile(boolean view_gui)
throws Exception
{
// TOD (SAM - 2003-06-04)
// Test code -- need to phase in

	TODO - UNDER CONSTRUCTION 
2003-06-03
JFileChooser fd = new JFileChooser(this, "Select TS Product File",
	JFileChooser.LOAD);
	fd.setFile("*.tspd");
	if (__lastDirectorySelected != null) {
		fd.setDirectory(__lastDirectorySelected);
	}
	fd.setVisible(true);

	// Return if no file name is selected...

	if (fd.getFile() == null || fd.getFile().equals("")) {
		return;
	}
	if (fd.getDirectory()!= null) {
		__lastDirectorySelected = fd.getDirectory();
	}

	// Determine the name of the export file as specified from 
	// the JFileChooser object...

	//String filename = fd.getDirectory() + "/" + fd.getFile();

	String filename = fd.getDirectory() + fd.getFile();        

	if (filename == null) {
		return;
	}

	PropList override_props = new PropList("StateModGUI");
	//DateTime now = new DateTime(DateTime.DATE_CURRENT);
	//override_props.set("CurrentDateTime=", now.toString());
	if (view_gui) {
		override_props.set("InitialView", "Graph");
		override_props.set("PreviewOutput", "True");
	}
	// this was in RTI.GRTS ... REVISIT ...
	TSProcessor p = new TSProcessor();
	p.addTSSupplier(this);
	p.processProduct(filename, override_props);

}
*/

/**
Read the StateMod response file.
@param filename name of the response file
*/
private void readStateModResponseFile ( String filename ) 
throws IOException
{	String routine = "StateMod_JFrame.readStateModResponseFile";
	JGUIUtil.setWaitCursor(this, true);
	__dataset.readStateModFile( filename,
			true, // read the data
			true, // read time series
			true, // Indicates that a GUI is being used, for interactive prompts
			this );
	// Also create a dataset for StateCU, primarily because the Consumptive Use menu uses a StateCU
	// dialog that depends on a StateCU dataset.
	// Set the CU Locations to the equivalent StateMod data set component
	DataSetComponent comp = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_STATECU_STRUCTURE );
	// Need to make a new component and set the information
	// OK to not do a deep copy
	try {
		DataSetComponent compStateCU = __datasetStateCU.getComponentForComponentType ( StateCU_DataSet.COMP_CU_LOCATIONS );
		compStateCU.setData(comp.getData());
		__datasetStateCU.setBaseName(__dataset.getBaseName());
	}
	catch ( Exception e ) {
		Message.printWarning(3, routine, "Could not copy StateCU Location data from StateMod data set to StateCU.");
		Message.printWarning ( 3, routine, e );
	}
}

/**
Remove StateMod binary output files.  This is useful to ensure a clean run and also if the data set
is to be distributed with a smaller footprint.
*/
private void removeStateModBinaryOutputFiles ()
{
	// TODO SAM 2010-12-19 Need to generate a list of files to remove and then do it.
}

/**
Saves selected features to a KML file
*/
public void saveToFile()
{
    JGUIUtil.setWaitCursor(this, true);
    new StateModGUI_KML_Features(this, __dataset);
    JGUIUtil.setWaitCursor(this, false);
}

/**
Set the last folder from which a response file was selected.
@param lastResponseFileFolder the folder from which the last response file was selected
*/
private void setLastResponseFileFolder ( String lastResponseFileFolder )
{
	__lastResponseFileFolder = lastResponseFileFolder;
}

/**
Set the map feature visibility and redraw the map.  This allows the user to turn data set features
on/off.  The definition whether a feature is in the data set is whether it has non-null GeoRecord data
in the StateMod object.
@param showFeaturesInDataSet if true show the data set features, if false do not show.
@param showFeaturesNotInDataSet if true show the non-data set features, if false do not show.
*/
private void setMapFeatureVisibilityAndRedrawMap ( boolean showFeaturesInDataSet,
	boolean showFeaturesNotInDataSet )
{	String routine = getClass().getName() + ".setMapFeatureVisibilityAndRedrawMap";
	// Loop through each category of data, getting the StateMod list of data and the associated layer
	// views.  Then process each.  This allows the same methods to be called when adding new stations or
	// deleting from the data set.
	GeoViewJPanel geoViewJPanel = getGeoViewJPanel();
	
	int [] compTypes = {
		// TODO SAM 2010-12-21 How to handle precipitation/evap since not true stations?
		StateMod_DataSet.COMP_DIVERSION_STATIONS,
		StateMod_DataSet.COMP_INSTREAM_STATIONS,
		StateMod_DataSet.COMP_RESERVOIR_STATIONS,
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS,
		StateMod_DataSet.COMP_WELL_STATIONS,
		StateMod_DataSet.COMP_PLANS
	};
	for ( int iComp = 0; iComp < compTypes.length; iComp++ ) {
		try {
			List<String> appTypeList = getGeoViewAppLayerTypeList ( compTypes[iComp] );
			if ( appTypeList.size() > 0 ) {
				setAppFeatureVisibilityUsingAssociatedDataObject ( geoViewJPanel, appTypeList,
					showFeaturesInDataSet, showFeaturesNotInDataSet );
			}
		}
		catch ( Exception e ) {
			Message.printWarning ( 3, routine, "Error setting feature visibility (" + e + ")." );
			Message.printWarning ( 3, routine, e );
		}
	}
	// Redraw the map...
	geoViewJPanel.getGeoView().redraw();
}

/**
Set the visibility of features based on a check of whether the spatial data objects do or don't have
associated data objects (which would have been set to the StateMod objects at startup or after adding
new objects.
@param appLayerTypes If specified, this contains a list of AppLayerType
string properties for layers that should be searched.  Specifying this
information increases the speed of searches because it limits the layers that are searched.
*/
public void setAppFeatureVisibilityUsingAssociatedDataObject ( GeoViewJPanel geoViewPanel,
	List<String> appLayerTypes, boolean showFeaturesInDataSet, boolean showFeaturesNotInDataSet )
{	List<GeoLayerView> layerViewList = geoViewPanel.getLayerViews(appLayerTypes);

	// Special cases...
	if ( (showFeaturesInDataSet && showFeaturesNotInDataSet) ||
		(!showFeaturesInDataSet && !showFeaturesNotInDataSet) ) {
		// Show all the features or none.
		for ( GeoLayerView layerView: layerViewList ) {
			GeoLayer layer = layerView.getLayer();
			layer.setShapesVisible(showFeaturesInDataSet, true, true);
		}
		return;
	}

	GRShape shape;
	for ( GeoLayerView layerView: layerViewList ) {
		GeoLayer layer = layerView.getLayer();
		// Loop through the features and check to see whether the associated data object is set.  If
		// it is, then the object exists in the StateMod data set.  If not, the object was never
		// matched.
		int nFeatures = layer.getShapes().size();
		for ( int iFeature = 0; iFeature < nFeatures; iFeature++ ) {
			shape = layer.getShape(iFeature);
			if ( (shape != null) && (shape.associated_object != null) ) {
				// Have a match
				if ( showFeaturesInDataSet ) {
					shape.is_visible = true;
				}
				else {
					shape.is_visible = false;
				}
			}
			else if ( shape != null ) {
				// Feature is not in the list
				if ( showFeaturesNotInDataSet ) {
					shape.is_visible = true;
				}
				else {
					shape.is_visible = false;
				}
			}
		}
	}
}

/**
Set the network editor that is used for the session.
*/
private void setNetworkEditor ( StateMod_Network_JFrame networkJFrame )
{
	__networkJFrame = networkJFrame;
}

/**
Set the new data set to be used by the main interface.  This is used when a new
data set is created with File...New.
It is assumed that the old data set has been cleared from the application.
*/
public void setNewDataSet ( StateMod_DataSet dataset )
{	__dataset = dataset;
	__dataset_wm.setDataSet ( __dataset );
	updateWindowStatus ();
	checkGUIState();	// Checks menus.
	__datasetJTree.clear();
	__datasetJTree.setDataSet(__dataset);
	__datasetJTree.displayDataSet();
}

/**
Sets up the GUI.
*/
private void setupGUI()
{	addWindowListener(this);
	String routine = "StateModGUI_JFrame.setupGUI";
	GridBagLayout gb = new GridBagLayout();

	try {

	setTitle("StateMod");

	JPanel main_JPanel = new JPanel();
	main_JPanel.setLayout(gb);
	// Add a new GeoViewJPanel for the new map interface...
	PropList props = new PropList("geoview");
	__geoViewJPanel = new GeoViewJPanel(this, props);
	Message.printStatus ( 2, routine, "Adding StateMod GUI as GeoView listener..." );
	__geoViewJPanel.getGeoView().addGeoViewListener ( this );
	__datasetJTree = new StateMod_DataSet_JTree(this, true, true);
	__datasetJTree.setMinimumSize(new Dimension(175,600));
	__datasetJTree.setPreferredSize(new Dimension(175,900));

	JPanel treePanel = new JPanel();
	treePanel.setLayout(new GridBagLayout());
	JPanel treePanel2 = new JPanel();
	treePanel2.add(new JScrollPane(__datasetJTree));
	// FIXME SAM 2010-12-19 Cannot seem to get the JTree to work with a JScroll pane and fill the
	// vertical space
	/*JGUIUtil.addComponent(
			treePanel, new JLabel(" Data Set Components"),
			0, 0, 1, 1,
			1.0, 1.0,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);*/
	JGUIUtil.addComponent(
			treePanel, treePanel2,
			0, 1, 1, 1,
			1.0, 1.0,
			GridBagConstraints.BOTH, GridBagConstraints.NORTH);
	__mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, __geoViewJPanel, treePanel );
	// Keep the JTree size constant when resizing (user can move it themselves).
	// This requires that the minimum and preferred dimensions are set on the tree above
	__mainSplitPane.setResizeWeight(1.0);
	
	getContentPane().add("Center", __mainSplitPane);

	// South panel - status bars - the same regardless of what the main interface uses...

	JPanel south_JPanel = new JPanel();
	south_JPanel.setLayout(gb);
	__statusBar = new JTextField();
	__statusBar.setEditable(false);
	__statusBarReadyWait = new JTextField("READY",6);
	__statusBarReadyWait.setEditable(false);

	JGUIUtil.addComponent(
		south_JPanel, __statusBarReadyWait,
		8, 0, 2, 1,
		0, 0,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH);
	JGUIUtil.addComponent(
		south_JPanel, __statusBar,
		0, 0, 8, 1,
		1, 0,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
	getContentPane().add("South", south_JPanel);

	// Do not want debug for the GUI
	Message.setWarningLevel(Message.STATUS_HISTORY_OUTPUT, 2);
	Message.setStatusLevel(Message.STATUS_HISTORY_OUTPUT, 2);

	// Create menubar and initialize menus...
	JMenuBar mb = new JMenuBar();
	setJMenuBar(mb);

	mb.add(	__File_JMenu = new JMenu(__File_String) );
	__File_New_JMenu = new JMenu(__File_New_Menu_String);
	__File_JMenu.add( __File_Open_JMenuItem = new SimpleJMenuItem(__File_Open_String, this));
	__File_JMenu.addSeparator();
	__File_New_JMenu.add( __File_New_Dataset_JMenuItem = new SimpleJMenuItem(__File_New_DataSet_String, this));
	__File_JMenu.add(__File_New_JMenu);
	__File_JMenu.addSeparator();
	__File_New_JMenu.add( __File_New_Network_JMenuItem = new SimpleJMenuItem(__File_New_Network_String, this));		
	__File_JMenu.add( __File_Save_JMenuItem = new SimpleJMenuItem(__File_Save_String, this));
	// TODO SAM 2011-01-25 Add later to help with data set packaging
	//__File_JMenu.addSeparator();
	//__File_JMenu.add( __File_RemoveStateModOutputFiles_JMenuItem =
	//	new SimpleJMenuItem(__File_RemoveStateModOutputFiles_String, this));
	__File_JMenu.addSeparator();
	__File_JMenu.add( __File_Exit_JMenuItem = new SimpleJMenuItem(__File_Exit_String, this));

	// Put in the order of the data menus...
	mb.add( __Edit_JMenu = new JMenu ( __Edit_String ) );
	__Edit_JMenu.add ( __Edit_Add_JMenu = new JMenu ( __Edit_Add_String ) );
	__Edit_Add_JMenu.add ( __Edit_Add_StreamGageStation_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_StreamGageStation_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_StreamGageHistoricalTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_StreamGageHistoricalTSMonthly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_StreamGageHistoricalTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_StreamGageHistoricalTSDaily_String, this));
	__Edit_Add_JMenu.add (	__Edit_Add_DelayTableMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_DelayTableMonthly_String, this));
	__Edit_Add_JMenu.add (	__Edit_Add_DelayTableDaily_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DelayTableDaily_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_Diversion_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_Diversion_String, this) );
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionHistoricalTSMonthly_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionHistoricalTSMonthly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionHistoricalTSDaily_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionHistoricalTSDaily_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionDemandTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_DiversionDemandTSMonthly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionDemandOverrideTSMonthly_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionDemandOverrideTSMonthly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionDemandTSAverageMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_DiversionDemandTSAverageMonthly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionDemandTSDaily_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionDemandTSDaily_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionIrrigationPracticeTSYearly_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionIrrigationPracticeTSYearly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionConsumptiveWaterRequirementTSMonthly_String, this));
	__Edit_Add_JMenu.add ( __Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_DiversionConsumptiveWaterRequirementTSDaily_String, this));
	__Edit_Add_JMenu.add (	__Edit_Add_PrecipitationTS_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_PrecipitationTS_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_EvaporationTS_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_EvaporationTS_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_Reservoir_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_Reservoir_String, this) );
	__Edit_Add_JMenu.add ( __Edit_Add_ReservoirContentTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_ReservoirContentTSMonthly_String, this) );
	__Edit_Add_JMenu.add ( __Edit_Add_ReservoirContentTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_ReservoirContentTSDaily_String, this) );
	__Edit_Add_JMenu.add ( __Edit_Add_ReservoirTargetTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_ReservoirTargetTSMonthly_String, this) );
	__Edit_Add_JMenu.add ( __Edit_Add_ReservoirTargetTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_ReservoirTargetTSDaily_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_InstreamFlow_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_InstreamFlow_String, this) );
	__Edit_Add_JMenu.add (__Edit_Add_InstreamFlowDemandTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_InstreamFlowDemandTSMonthly_String, this) );
	__Edit_Add_JMenu.add (__Edit_Add_InstreamFlowDemandTSAverageMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_InstreamFlowDemandTSAverageMonthly_String, this) );
	__Edit_Add_JMenu.add (__Edit_Add_InstreamFlowDemandTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_InstreamFlowDemandTSDaily_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_Well_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_Well_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_WellPumpingTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellPumpingTSMonthly_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_WellPumpingTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellPumpingTSDaily_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_WellDemandTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellDemandTSMonthly_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_WellDemandTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellDemandTSDaily_String, this) );
	__Edit_Add_JMenu.add (__Edit_Add_WellIrrigationPracticeTSYearly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellIrrigationPracticeTSYearly_String, this));
	__Edit_Add_JMenu.add (__Edit_Add_WellConsumptiveWaterRequirementTSMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellConsumptiveWaterRequirementTSMonthly_String,	this));
	__Edit_Add_JMenu.add (__Edit_Add_WellConsumptiveWaterRequirementTSDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_WellConsumptiveWaterRequirementTSDaily_String,this));
	__Edit_Add_JMenu.add(__Edit_Add_StreamEstimateStationConvert_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_StreamEstimateStationConvert_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_StreamEstimateStationNew_JMenuItem =
		new SimpleJMenuItem(__Edit_Add_StreamEstimateStationNew_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_OtherNode_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_OtherNode_String, this) );
	__Edit_Add_JMenu.add (	__Edit_Add_OperationalRight_JMenuItem =
		new SimpleJMenuItem( __Edit_Add_OperationalRight_String, this));

	__Edit_JMenu.add ( __Edit_Delete_JMenu=new JMenu(__Edit_Delete_String));
	__Edit_Delete_JMenu.add (__Edit_Delete_StreamGageStation_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_StreamGageStation_String, this ));
	__Edit_Delete_JMenu.add( __Edit_Delete_DelayTableMonthly_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_DelayTableMonthly_String, this) );
	__Edit_Delete_JMenu.add ( __Edit_Delete_DelayTableDaily_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_DelayTableDaily_String,this));
	__Edit_Delete_JMenu.add ( __Edit_Delete_Diversion_JMenuItem =
		new SimpleJMenuItem( __Edit_Delete_Diversion_String, this) );
	__Edit_Delete_JMenu.add ( __Edit_Delete_PrecipitationTS_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_PrecipitationTS_String,this));
	__Edit_Delete_JMenu.add ( __Edit_Delete_EvaporationTS_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_EvaporationTS_String, this) );
	__Edit_Delete_JMenu.add ( __Edit_Delete_Reservoir_JMenuItem =
		new SimpleJMenuItem( __Edit_Delete_Reservoir_String, this) );
	__Edit_Delete_JMenu.add ( __Edit_Delete_InstreamFlow_JMenuItem =
		new SimpleJMenuItem( __Edit_Delete_InstreamFlow_String, this) );
	__Edit_Delete_JMenu.add ( __Edit_Delete_Well_JMenuItem =
		new SimpleJMenuItem( __Edit_Delete_Well_String, this) );
	__Edit_Delete_JMenu.add (__Edit_Delete_StreamEstimateStation_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_StreamEstimateStation_String, this));
	__Edit_Delete_JMenu.add ( __Edit_Delete_OtherNode_JMenuItem =
		new SimpleJMenuItem( __Edit_Delete_OtherNode_String, this) );
	__Edit_Delete_JMenu.addSeparator();
	__Edit_Delete_JMenu.add ( __Edit_Delete_OperationalRight_JMenuItem =
		new SimpleJMenuItem(__Edit_Delete_OperationalRight_String,this) );

	mb.add( __View_JMenu = new JMenu(__View_String) );
	__View_JMenu.add( __View_DataSetSummary_JMenuItem =
		new SimpleJMenuItem( __View_DataSetSummary_String, this ) );

	__View_JMenu.addSeparator();
	__View_Map_ShowFeaturesInDataSet_JMenuItem = new JCheckBoxMenuItem(__View_Map_ShowFeaturesInDataSet_String);
	__View_Map_ShowFeaturesInDataSet_JMenuItem.setState(true);
	__View_Map_ShowFeaturesInDataSet_JMenuItem.setToolTipText(
		"If checked show map features that have been associated with StateMod data objects.");
	__View_JMenu.add( __View_Map_ShowFeaturesInDataSet_JMenuItem );
	__View_Map_ShowFeaturesInDataSet_JMenuItem.addActionListener(this);

	__View_Map_ShowFeaturesNotInDataSet_JMenuItem = new JCheckBoxMenuItem(
		__View_Map_ShowFeaturesNotInDataSet_String);
	__View_Map_ShowFeaturesNotInDataSet_JMenuItem.setToolTipText(
		"If checked show map features that have NOT been associated with StateMod data objects.");
	__View_Map_ShowFeaturesNotInDataSet_JMenuItem.setState(false);
	__View_JMenu.add( __View_Map_ShowFeaturesNotInDataSet_JMenuItem );
	__View_Map_ShowFeaturesNotInDataSet_JMenuItem.addActionListener(this);
	
	__View_JMenu.addSeparator();
	__View_Network_JMenuItem = new JCheckBoxMenuItem(__View_Network_String);
	__View_Network_JMenuItem.addActionListener(this);
	__View_JMenu.add( __View_Network_JMenuItem );

	mb.add( __Data_JMenu = new JMenu( __Data_String ) );
	__Data_JMenu.add ( __Data_Control_JMenu =new JMenu ( __Data_Control_String ) );
	__Data_Control_JMenu.add( __Data_Control_Response_JMenuItem =
		new SimpleJMenuItem( __Data_Control_Response_String, this ) );
	__Data_Control_JMenu.add( __Data_Control_Control_JMenuItem =
		new SimpleJMenuItem( __Data_Control_Control_String, this) );
	__Data_Control_JMenu.add( __Data_Control_OutputControl_JMenuItem =
		new SimpleJMenuItem(__Data_Control_OutputControl_String, this));

	__Data_JMenu.add(__Data_ConsumptiveUse_JMenuItem = new SimpleJMenuItem(__Data_ConsumptiveUse_String, this) );
	__Data_JMenu.add(__Data_StreamGage_JMenuItem = new SimpleJMenuItem(__Data_StreamGage_String, this) );
	__Data_JMenu.add ( __Data_DelayTables_JMenu =new JMenu ( __Data_DelayTables_String ) );
	__Data_DelayTables_JMenu.add( __Data_DelayTables_Monthly_JMenuItem =
		new SimpleJMenuItem( __Data_DelayTables_Monthly_String, this));
	__Data_DelayTables_Daily_JMenuItem = new SimpleJMenuItem(__Data_DelayTables_Daily_String, this);

	__Data_JMenu.add( __Data_Diversions_JMenuItem = new SimpleJMenuItem(__Data_Diversions_String, this) );

	__Data_JMenu.add ( __Data_Precipitation_JMenu = new JMenu ( __Data_Precipitation_String ) );
	__Data_Precipitation_JMenu.add( __Data_Precipitation_Monthly_JMenuItem = new SimpleJMenuItem(__Data_Precipitation_Monthly_String,this) );

	__Data_JMenu.add ( __Data_Evaporation_JMenu = new JMenu ( __Data_Evaporation_String ) );
	__Data_Evaporation_JMenu.add( __Data_Evaporation_Monthly_JMenuItem = new SimpleJMenuItem(__Data_Evaporation_Monthly_String,this) );

	__Data_JMenu.add( __Data_Reservoirs_JMenuItem = new SimpleJMenuItem(__Data_Reservoirs_String, this) );

	__Data_JMenu.add( __Data_InstreamFlows_JMenuItem = new SimpleJMenuItem(__Data_InstreamFlows_String, this) );

	__Data_JMenu.add( __Data_Wells_JMenuItem = new SimpleJMenuItem(	__Data_Wells_String, this) );
	
	__Data_JMenu.add( __Data_Plans_JMenuItem = new SimpleJMenuItem(	__Data_Plans_String, this) );
	
	__Data_JMenu.add( __Data_StreamEstimate_JMenuItem = new SimpleJMenuItem(__Data_StreamEstimate_String, this) );
	
	__Data_JMenu.add( __Data_RiverNetwork_JMenuItem = new SimpleJMenuItem(__Data_RiverNetwork_String, this) );

	__Data_JMenu.add( __Data_OperationalRights_JMenuItem =
		new SimpleJMenuItem( __Data_OperationalRights_String, this) );

	mb.add( __Run_JMenu = new JMenu(__Run_String ) );
	__Run_JMenu.add( __Run_Baseflows_JMenuItem = new SimpleJMenuItem(__Run_Baseflows_String, this) );
	__Run_JMenu.add( __Run_BaseflowsFast_JMenuItem = new SimpleJMenuItem(__Run_BaseflowsFast_String, this) );
	__Run_JMenu.addSeparator();
	__Run_JMenu.add( __Run_DataCheck_JMenuItem = new SimpleJMenuItem(__Run_DataCheck_String, this) );
	__Run_JMenu.addSeparator();
	__Run_JMenu.add( __Run_SimulateFast_JMenuItem = new SimpleJMenuItem(__Run_SimulateFast_String, this) );
	__Run_JMenu.add( __Run_Simulate_JMenuItem = new SimpleJMenuItem(__Run_Simulate_String, this) );
	__Run_JMenu.addSeparator();
	__Run_JMenu.add( __Run_Report_JMenuItem =new SimpleJMenuItem(__Run_Report_String, this) );
	__Run_JMenu.addSeparator();
	__Run_JMenu.add( __Run_Help_JMenuItem =	new SimpleJMenuItem( __Run_Help_String, this) );
	__Run_JMenu.add( __Run_Update_JMenuItem = new SimpleJMenuItem(__Run_Update_String, this) );
	__Run_JMenu.add( __Run_Version_JMenuItem = new SimpleJMenuItem(__Run_Version_String, this) );

	mb.add ( __Results_JMenu = new JMenu(__Results_String) );
	__Results_JMenu.add( __Results_GraphingTool_JMenuItem = new SimpleJMenuItem( __Results_GraphTool_String, this) );
	if (IOUtil.testing()) {
		__Results_JMenu.add( __Results_ProcessTSProduct_JMenuItem =
			new SimpleJMenuItem( __Results_ProcessTSProduct_String, this) );
	}
	__Results_JMenu.addSeparator();
	__Results_JMenu.add( __Results_GraphDemandSupplySummary_JMenuItem =
		new SimpleJMenuItem( __Results_GraphDemandSupplySummary_String,	this));
	__Results_JMenu.addSeparator();
	__Results_JMenu.add( __Results_OutputFiles_JMenuItem =
		new SimpleJMenuItem( __Results_OutputFiles_String, this) );

	mb.add( __Tools_JMenu = new JMenu(__Tools_String) );
	__Tools_JMenu.add( __Tools_RunSmDelta_JMenuItem = new SimpleJMenuItem( __Tools_RunSmDelta_String, this) );
	__Tools_JMenu.add( __Tools_AddSummaryMapLayer_JMenuItem =
		new SimpleJMenuItem( __Tools_AddSummaryMapLayer_String, this));
	__Tools_JMenu.add( __Tools_AddDifference_JMenuItem = 
		new SimpleJMenuItem( __Tools_AddDifference_String, this));
	__Tools_JMenu.add( __Tools_AddPercentDifference_JMenuItem = 
		new SimpleJMenuItem( __Tools_AddPercentDifference_String,this));
	__Tools_JMenu.addSeparator();
	__Tools_JMenu.add( __Tools_AddAnimationMapLayer_JMenuItem =
		new SimpleJMenuItem( __Tools_AddAnimationMapLayer_String,this));
	__Tools_JMenu.addSeparator();
	__Tools_JMenu.add( __Tools_QueryTool_JMenuItem = new SimpleJMenuItem( __Tools_QueryTool_String,this));
	__Tools_JMenu.addSeparator();
	__Tools_JMenu.add( __Tools_Options_JMenuItem = new SimpleJMenuItem( __Tools_Options_String, this) );
	
	__Tools_JMenu.addSeparator();
	__Tools_JMenu.add( __Tools_ReportModifiedData_JMenuItem =
		new SimpleJMenuItem( __Tools_ReportModifiedData_String, this));
	__Tools_JMenu.add( __Tools_ReportUnusedData_JMenuItem =
		new SimpleJMenuItem( __Tools_ReportUnusedData_String, this) );

	__Tools_JMenu.addSeparator();
	__Tools_JMenu.add(__Tools_NetworkSummary_JMenuItem = 	
		new SimpleJMenuItem(__Tools_NetworkSummary_String,this));
	__Tools_JMenu.addSeparator();
	
	/* FIXME SAM 2008-03-10 Enable when State pays for KML features
	__Tools_JMenu.add(__Tools_SaveDataSetAsKML_JMenuItem = 
		new SimpleJMenuItem( __Tools_SaveDataSetAsKML_String, this));
	__Tools_JMenu.addSeparator();
	*/
	
	DiagnosticsJFrame diagnostics_gui = new DiagnosticsJFrame();
	diagnostics_gui.attachMainMenu(__Tools_JMenu);

	mb.add( __Help_JMenu = new JMenu(__Help_String) );
	// As of 2003-06-04, JMenuBar.setHelpMenu was not implemented and threw an exception.
	// mb.setHelpMenu(helpJMenu);
	__Help_JMenu.add( __Help_AboutStateModGUI_JMenuItem =
		new SimpleJMenuItem( __Help_AboutStateModGUI_String, this) );
	__Help_JMenu.addSeparator();
    __Help_JMenu.add ( __Help_ViewStateModDocumentation_JMenuItem =
    	new SimpleJMenuItem(__Help_ViewStateModDocumentation_String,this));
    __Help_JMenu.add ( __Help_ViewStateModGUIDocumentation_JMenuItem =
    	new SimpleJMenuItem(__Help_ViewStateModGUIDocumentation_String,this));
    __Help_JMenu.add ( __Help_ViewStateModGUITrainingMaterials_JMenuItem =
    	new SimpleJMenuItem(__Help_ViewStateModGUITrainingMaterials_String,this));
    __Help_JMenu.addSeparator();
    __Help_JMenu.add ( __Help_TroubleshootInstall_JMenuItem =
    	new SimpleJMenuItem(__Help_TroubleshootInstall_String,this));

	// Initialize the on-line help system...

	//List helphome = new Vector();
	// Specify the URL end parts for documentation home and help index file...
	// To debug the help file...
	//helphome.addElement("J:/crdss/statemod/gui/doc/UserManual/05.08.00/html");
	//helphome.addElement(__home.replace('\\','/') + "/doc/StateModGUI/UserManual");
	//helphome.addElement(__home + "\\doc\\StateModGUI\\UserManual");
	//helphome.addElement("C:\\cdss\\doc\\StateModGUI\\UserManual");
	//helphome.addElement("D:\\cdss\\doc\\StateModGUI\\UserManual");
	//helphome.addElement("http://cdss.state.co.us/manuals/smgui");
	//URLHelp.initialize(null, helphome, "StateModGUI_help_index.txt");
	// Now hook in the help menu...
	// TODO SAM 2007-02-18 Evaluate on-line help
	/*
	try {
		__helpIndexGUI = new URLHelpJFrame();
		//__helpIndexGUI.attachMainJMenu(__Help_JMenu);
	}
	catch (Exception e) {
		Message.printWarning(2, routine,
		"Error setting up help system:");
		Message.printWarning(2, routine, e);
	}
	*/
	// For now, use the PDF documentation rather than the on-line...

	List help_override = new Vector(3);
	help_override.add("On-line help is currently not available.");
	help_override.add("Instead, please print the PDF file that is supplied with documentation.");
	help_override.add("For example:  \\cdss\\doc\\StateModGUI\\UserManual\\StateModGUI.pdf");
	URLHelp.setHelpOverride(help_override);

	// Declare a window manager.  Initially, it will have no data set...

	__dataset_wm = new StateMod_DataSet_WindowManager ();
	__dataset_wm.setWindow(StateMod_DataSet_WindowManager.WINDOW_MAIN,this);
	__datasetJTree.setDataSetWindowManager(__dataset_wm);
	// Add the map panel to the window manager to allow interaction between map and edit windows
	__dataset_wm.setMapPanel(__geoViewJPanel);

	updateWindowStatus (); // Sets the main status messages/title and call checkGUIState()

	pack();
	setSize(1000, 750);
	// Old...
	//setSize(790, 550);
	JGUIUtil.center(this);
	// Add a component listener here to capture the initial size and listen for resize events...
	addComponentListener ( this );
	setVisible(true);

	}
	catch (Exception e) {
		Message.printWarning ( 2, routine, "Error initializing the StateMod GUI" );
		Message.printWarning ( 3, routine, e );
	}
}

/**
Indicate whether edits to a component should be allowed.  In particular, edits should not be allowed
if there was an error reading the original input file or perhaps in other situations (like the data set
is read-only through some metadata.
*/
private boolean shouldComponentEditsBeAllowed ( int compType )
{
	DataSetComponent comp = __dataset.getComponentForComponentType(compType);
	if ( comp == null ) {
		return false;
	}
	else if ( comp.getErrorReadingInputFile() ) {
		// Original data had an error so there is a potential for further data corruption
		return false;
	}
	else if ( compType == StateMod_DataSet.COMP_STATECU_STRUCTURE ) {
		// Do not allow edits - just viewing
		return false;
	}
	return true;
}

/**
Troubleshoot the installation.  Ideally this will check the StateMod version for specific things (like long
filenames?) but for now just include things that have tripped up users/developers.
*/
private void uiAction_Help_TroubleshootInstall ()
{	String routine = getClass().getName() + ".uiAction_Help_TroubleshootInstall";

	StringBuffer message = new StringBuffer();
	// Warn the user if the response file path may cause issues
	StateMod_DataSet dataSet = getDataSet();
	String responseNote = "";
	boolean problemFound = false;
	if ( dataSet == null ) {
		responseNote = "More checks can be performed when a response file is selected.\n";
	}
	else {
		String responseFile = dataSet.getDataFilePath(dataSet.getDataSetFileName());
		if ( responseFile.indexOf("-") >= 0 ) {
			message.append ( "The response file path contains a dash (-).\n");
			message.append ( "  Some StateMod versions may erroneously interpret as a command line option.\n");
			message.append ( "  Recommendation:  Rename or copy the folder to not use a dash.\n");
			problemFound = true;
		}
	}
	if ( message.length() > 0 ) {
		Message.printWarning(1, routine, message.toString());
	}
	else {
		String moreHelpNote = "";
		if ( !problemFound ) {
			moreHelpNote = "If problems are occurring, report the issue to CDSS support (see Help...About).\n";
		}
		new ResponseJDialog(this, 
			"Troubleshoot Install",
			"There do not appear to be issues with the install.\n" + responseNote + moreHelpNote,
			ResponseJDialog.OK).response();
	}
}

/**
View the training materials by displaying in file browser.
*/
private void uiAction_Help_ViewTrainingMaterials ()
{   String routine = getClass().getName() + ".uiAction_ViewTrainingMaterials";
    // The location of the documentation is relative to the application home
    String trainingFolderName = IOUtil.getApplicationHomeDir() + "/doc/Training";
    // Convert for the operating system
    trainingFolderName = IOUtil.verifyPathForOS(trainingFolderName, true);
    // Now display using the default application for the file extension
    Message.printStatus(2, routine, "Opening training material folder \"" + trainingFolderName + "\"" );
    try {
        Desktop desktop = Desktop.getDesktop();
        desktop.open ( new File(trainingFolderName) );
    }
    catch ( Exception e ) {
        Message.printWarning(1, "", "Unable to display training materials at \"" +
            trainingFolderName + "\" (" + e + ")." );
    }
}

/**
Update the base flow time series after a baseflow run.
*/
private void updateBaseFlows()
{	/* TODO - after feedback from Ray Bennett
	int x = new ResponseJDialog(this, 
	"Read new Baseflow Time Series?",
	"The baseflow time series have been updated.\n" +
	"Do you want to copy \"" + __dataset.getBaseName() + ".xbm\" to \"" +
		__dataset.getComponent-read so that displays are consistent?",
	ResponseJDialog.YES | ResponseJDialog.NO).response();
	if ( x == ResponseJDialog.YES ) {
		// Save the data set...
		// TODO - need to copy from xbm to data set, also handle the
		// daily.  Also need to make sure that the base flow file name
		// is defined in the response file.
	}
	*/
}

/**
Update the text fields at the bottom of the main interface.
@param level Message level.  If > 0 and the message is not null, call
Message.printStatus() to record a message.
@param routine Routine name used if Message.printStatus() is called.
@param message If not null, update the __message_JTextField to contain this
text.  If null, leave the contents as previously shown.  Specify "" to clear the text.
@param commands_status If not null, update the __commands_JPanel border to
contain this text.  If null, leave the contents as previously shown.  Specify "" to clear the text.
@param status If not null, update the __status_JTextField to contain
this text.  If null, leave the contents as previously shown.  Specify "" to clear the text.
*/
/* TODO
private void updateTextFields (	int level, String routine, String message,
				String commands_status, String status )
{	if ( (level > 0) && (message != null) ) {
		// Print a status message to the messaging system...
		Message.printStatus ( 1, routine, message );
	}
	if ( message != null ) {
		__message_JTextField.setText (message);
	}
	if ( commands_status != null ) {
		__commands_JPanel.setBorder (BorderFactory.createTitledBorder (
		BorderFactory.createLineBorder(Color.black),"Commands:  " + commands_status) );
	}
	if ( status != null ) {
		__status_JTextField.setText (status);
	}
}
*/

/**
Update the main status information when the list contents have changed.  Interface tasks include:
<ol>
<li>	Set the title bar.
	If no commands file has been read, the title will be: "StateMod - no data set has been opened".
	If a data set has been read but not modified, the title will be: StateMod - "filename"".
	If a data set has been read and modified, the title will be: "StateMod - "filename" (modified)".
	</li>
<li>	Call updateTextFields() for general message changes.
	</li>
<li>	Call checkGUIState() to reset menus, etc.  Note this should be called
	independently when the data set contents changes, or controlling components are selected.
	</li>
</ol>
*/
public void updateWindowStatus ()
{	
	Runnable r = new Runnable() {
        public void run() {
			if ( __dataset == null ) {
				setTitle ( "StateMod - no data set has been opened");
			}
			else {
				if ( __dataset.isDirty() ) {
					setTitle ( "StateMod - \"" +
					__dataset.getDataSetDirectory() + File.separator +
					__dataset.getDataSetFileName()  + "\" (modified)");
				}
				else {
					setTitle ( "StateMod - \"" +
					__dataset.getDataSetDirectory() + File.separator +
					__dataset.getDataSetFileName()  + "\"" );
				}
			}
			// TODO
			//updateTextFields ( -1, "StateModGUI_JFrame.updateStatus", null,
					//"Selected " + selected_size +
					//" of " + __commands_JListModel.size() + " commands",
					//__STATUS_READY );
			checkGUIState ();
        }
	};
    if ( SwingUtilities.isEventDispatchThread() )
    {
        r.run();
    }
    else 
    {
        SwingUtilities.invokeLater ( r );
    }
}

/**
Responds to window activated events; checks the GUI state.
@param evt the WindowEvent that happened.
*/
public void windowActivated(WindowEvent evt) {
	checkGUIState();
}

/**
Responds to window closed events.  Currently check to see if the TSViewJFrame
used for precipitation and evaporation time series data is closed.  If so,
the StateMod window for these data components can be set to closed.
@param e the WindowEvent that happened.
*/
public void windowClosed ( WindowEvent e )
{	Component c = e.getComponent();
	if ( c == __dataset_wm.getWindow (StateMod_DataSet_WindowManager.WINDOW_PRECIPITATION ) ) {
		__dataset_wm.closeWindow(StateMod_DataSet_WindowManager.WINDOW_PRECIPITATION);
	}
	else if ( c == __dataset_wm.getWindow ( StateMod_DataSet_WindowManager.WINDOW_EVAPORATION ) ) {
		__dataset_wm.closeWindow(StateMod_DataSet_WindowManager.WINDOW_EVAPORATION);
	}
}

/**
Responds to window closing events; closes the application.
@param evt the WindowEvent that happened.
*/
public void windowClosing(WindowEvent evt) {
	Component c = evt.getComponent();
	if ( c == __networkJFrame ) {
		__View_Network_JMenuItem.setState(false);
	}
	else {
		closeGUI();
	}
}

/**
Responds to window deactivated events; does nothing.
@param evt the WindowEvent that happened.
*/
public void windowDeactivated(WindowEvent evt) {}

/**
Responds to window deiconified events; does nothing.
@param evt the WindowEvent that happened.
*/
public void windowDeiconified(WindowEvent evt) {}

/**
Responds to window iconified events; does nothing.
@param evt the WindowEvent that happened.
*/
public void windowIconified(WindowEvent evt) {}

/**
Responds to window opened events; does nothing.
@param evt the WindowEvent that happened.
*/
public void windowOpened(WindowEvent evt) {}

private void addDifferenceMapLayer(boolean percent) {
	String routine = "StateModGUI_JFrame.addDifferenceMapLayer";
	JGUIUtil.setWaitCursor(this, true);

	// Set up the table fields

	List fields = new Vector();
	fields.add(new TableField(TableField.DATA_TYPE_STRING, "Identifier", 20));
	fields.add(new TableField(TableField.DATA_TYPE_STRING, "Name", 40));
	fields.add(new TableField(TableField.DATA_TYPE_DOUBLE, "Difference", 11, 3));

	DataTable table = new DataTable(fields);
	StateMod_Data data = null;
	TableRecord record = null;

	String rspFile = __dataset.getComponentForComponentType(StateMod_DataSet.COMP_RESPONSE).getDataFileName();
	int index = rspFile.indexOf(".rsp");
	String basename = rspFile.substring(0, index);
	
	printStatus("Reading streamflow historical time series", WAIT);

	//////////////////////////////////////////////////
	// Stream Time Series

	List stations = (List)__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_STREAMGAGE_STATIONS).getData();

	List tsStreamHist = (List)__dataset.getComponentForComponentType(
		StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY).getData();

	int errorCount = 0;
	
	printStatus("Reading streamflow simulated time series", WAIT);

	/////////////////////////////////////////
	// BTS Stream
	
	int size = stations.size();
	String[] streamIDs = new String[size];
	for (int i = 0; i < size; i++) {
		data = (StateMod_Data)stations.get(i);
		streamIDs[i] = data.getID();
	}

	List tsStreamBTS = new Vector();
	TS tts = null;
	for (int i = 0; i < size; i++) {
		try {
			tts = StateMod_BTS.readTimeSeries(
				"" + streamIDs[i] + ".StateMod.River_Outflow.Month",
				basename + ".b43", null, null, null, true);
			tsStreamBTS.add(tts);
		}
		catch (Exception e) {
			Message.printWarning(1, routine,
				"Error reading time series '" + streamIDs[i]
				+ ".StateMod_RiverOutflow.Month from '" + basename + ".b43' (" + e + ")");
			Message.printWarning(2, routine, e);
			errorCount++;
		}
	}
	if (errorCount > 0) {
		Message.printWarning(1, routine, 
			"Error reading " + errorCount + " time series from data file.  Some simulated streamflow "
			+ "data will not be displayed.  See error log for details.");
	}

	double result = -1;
	size = tsStreamHist.size();
	int size2 = tsStreamBTS.size();
	int size3 = stations.size();
	String id1 = null;
	String id2 = null;
	TS ts1 = null;
	TS ts2 = null;
	for (int i = 0; i < size; i++) {
		ts1 = (TS)tsStreamHist.get(i);
		id1 = StringUtil.getToken(ts1.getIdentifierString(), ".", 0, 0).trim();
		
		for (int j = 0; j < size2; j++) {
			ts2 = (TS)tsStreamBTS.get(j);
			if (ts2 == null) {
				continue;
			}

			id2 = StringUtil.getToken(ts2.getIdentifierString(), ".", 0, 0).trim();

			if (id2.equalsIgnoreCase(id1)) {
				j = size2 + 1;
			}
			else {
				ts2 = null;
			}
		}

		for (int j = 0; j < size3; j++) {
			data = (StateMod_Data)stations.get(j);
			if (data.getID().trim().equalsIgnoreCase(id1)) {
				j = size3 + 1;
			}
			else {
				data = null;
			}
		}

		if (ts2 != null) {	
			record = new TableRecord(5);
			record.addFieldValue(id1);
			if (data != null) {
				record.addFieldValue(data.getName());
			}
			else {
				record.addFieldValue("");
			}
			try {
				result = StateMod_Util.calculateTimeSeriesDifference(ts1, ts2, percent);
			}
			catch (Exception e) {
				Message.printWarning(1, routine, "Error calculating time series difference (" + e + ").");
				Message.printWarning(2, routine, e);
				result = -999;
			}
			
			record.addFieldValue(new Double(result));

			try {
				if (result != -999) {
					table.addRecord(record);
				}
			}
			catch (Exception e) {
				Message.printWarning(1, routine, "Error adding row to table (" + e + ").");
				Message.printWarning(2, routine, e);
			}
		}
	}

	try {
		// for debugging ...
		// table.dumpTable("\t");
		__geoViewJPanel.addSummaryLayerView(table, "Historical/Simulated Streamflow Difference",
			0, 2, new Vector(), true);		
	}
	catch (Exception e) {
		Message.printWarning(1, routine, "Error adding summary layer to map display (" + e + ").");
		Message.printWarning(2, routine, e);
	}

	JGUIUtil.setWaitCursor(this, false);
}

private final String TESTING_MENUITEM = "Set DataSet Clean";

private void scrubDataSet() {
	int[] groups = __dataset.getComponentGroupNumbers();

	DataSetComponent dsc = null;
	List v = null;

	// Go through each of the groups and get their data out.  Group data
	// consists of the DataSetComponents the group contains.  For each
	// of the group's DataSetComponents, if it has data, then add its
	// component type to the accumulation vector.
	for (int i = 0; i < groups.length; i++) {
		dsc = __dataset.getComponentForComponentType(groups[i]);
		v = (List)dsc.getData();
		if (v == null) {
			v = new Vector();
		}
		for (int j = 0; j < v.size(); j++) {
			dsc = (DataSetComponent)v.get(j);
			// the following makes sure that the response file 
			// is not added here ... the response file is added
			// below because it must always be in the GUI.
			dsc.setDirty(false);
		}
	}
}

private void createNetworkSummary() {
	StateMod_NodeNetwork network = null;
	if ( __networkJFrame != null ) {
		network = __networkJFrame.getNetwork();
	}
	
	List report = new Vector();

	report.add("Network Summary Report");
	report.add("----------------------");
	report.add("");
	if ( network == null ) {
		report.add ( "The network has not been read." );
		new ReportJFrame(report, new PropList(""));
		return;
	}
	report.add("   Number of nodes: " + network.getNodeCount());
	int num = 0;
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_BASEFLOW).size();
	if (num > 0) {
		report.add("          Baseflow: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_CONFLUENCE)
	    .size();
	if (num > 0) {
		report.add("        Confluence: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_DIV).size();
	if (num > 0) {
		report.add("         Diversion: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_DIV_AND_WELL).size();
	if (num > 0) {
		report.add("Diversion and Well: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_IMPORT).size();
	if (num > 0) {
		report.add("            Import: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_ISF).size();
	if (num > 0) {
		report.add("     Instream Flow: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_OTHER).size();
	if (num > 0) {
		report.add("             Other: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_RES).size();
	if (num > 0) {
		report.add("         Reservoir: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_FLOW).size();
	if (num > 0) {
		report.add("        Streamflow: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_WELL).size();
	if (num > 0) {
		report.add("              Well: " + num);
	}
	num = network.getNodesForType(HydrologyNode.NODE_TYPE_END).size();
	if (num > 0) {
		report.add("               End: " + num);
	}

	// Confluence

	// Diversion
	createNetworkSummaryHelper(report, network, StateMod_DataSet.COMP_DIVERSION_STATIONS,
		HydrologyNode.NODE_TYPE_DIV, "Diversion stations");
	// Diversion and Well

	// Instream Flow
	createNetworkSummaryHelper(report, network, StateMod_DataSet.COMP_INSTREAM_STATIONS,
		HydrologyNode.NODE_TYPE_ISF, "Instream stations");

	// Other
	createNetworkSummaryHelper(report, network, StateMod_DataSet.COMP_OTHER_NODE,
		HydrologyNode.NODE_TYPE_OTHER, "Other nodes");

	// Reservoir
	createNetworkSummaryHelper(report, network, StateMod_DataSet.COMP_RESERVOIR_STATIONS,
		HydrologyNode.NODE_TYPE_RES, "Reservoir stations");

	// Flow
	createNetworkSummaryHelper(report, network, StateMod_DataSet.COMP_STREAMGAGE_STATIONS,
		HydrologyNode.NODE_TYPE_FLOW, "Streamflow stations");

	// Well
	createNetworkSummaryHelper(report, network, StateMod_DataSet.COMP_WELL_STATIONS,
		HydrologyNode.NODE_TYPE_WELL, "Well stations");

	// End

	new ReportJFrame(report, new PropList(""));
}

private void createNetworkSummaryHelper(List report, StateMod_NodeNetwork network, int componentType, int nodeType, 
String reportLabel)
{
	int size = -1;
	boolean[] marked = null;
	boolean found = false;
	String[] ids = null;
	String id = null;
	List notFound = null;
	List v = null;

	int[] type = { nodeType };
	try {
		v = network.getNodeIdentifiersByType(type);
	}
	catch (Exception e) {
		Message.printWarning(2, "", e);
		v = new Vector();
	}
	ids = new String[v.size()];
	marked = new boolean[v.size()];
	for (int i = 0; i < ids.length; i++) {
		marked[i] = false;
		ids[i] = (String)v.get(i);
	}

	DataSetComponent component = __dataset.getComponentForComponentType(componentType);

	if (component == null) {
		return;
	}

	v = (List)component.getData();	
	size = v.size();
	StateMod_Data data = null;
	notFound = new Vector();
	for (int i = 0; i < size; i++) {
		data = (StateMod_Data)v.get(i);
		id = data.getID();
		found = false;
		for (int j = 0; j < marked.length; j++) {
			if (id.equals(ids[j])) {
				marked[j] = true;
				found = true;
				j = marked.length;
			}
		}

		if (!found) {
			notFound.add(id);
		}
	}

	report.add("");
	report.add(reportLabel + " in data set but not in network:");
	size = notFound.size();
	for (int i = 0; i < size; i++) {
		report.add("      " + notFound.get(i));
	}
	
	report.add("");
	report.add(reportLabel + " in network but not in data set:");
	size = marked.length;
	for (int i = 0; i < size; i++) {
		if (!marked[i]) {
			report.add("      " + ids[i]);
		}
	}
}

}

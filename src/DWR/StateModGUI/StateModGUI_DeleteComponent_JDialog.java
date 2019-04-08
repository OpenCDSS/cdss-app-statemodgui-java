// StateModGUI_DeleteComponent_JDialog - dialog to delete an object from a data set component

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
// StateModGUI_DeleteComponent_JDialog - dialog to delete an object from a data set component
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 
// 18 Mar 1998	Catherine E.		Created initial version of class.
//		Nutting-Lane, RTi
// 01 Apr 2001	Steven A. Malers, RTi	Change GUI to GUIUtil.  Add finalize().
//					Remove import *.
// 2003-02-01	SAM, RTi		Change "Close" to "Cancel".
// 2003-07-14	SAM, RTi		* Display common identifiers and node
//					  type to simplify deletes.
//					* Also display river nodes that are not
//					  associated with other node types.
//					  these are the "other" node types.
// 2003-09-29	SAM, RTi		* Port from the 1.1.8 version from
//					  SMdeleteNodeWindow.  Enable delete for
//					  the following components:
//						Evaporation TS (Monthly)
//						Precipitation TS (Monthly)
//						Diversion
// 2003-10-10	SAM, RTi		Respond to comments from 2003-10-07
//					progress meeting.
//					* Details panel was not being scrolled.
// 2003-10-14	SAM, RTi		* Change irrigation water requirement
//					  to consumptive water requirement.
// 2004-06-29	J. Thomas Sapienza, RTi	Tied in delete code for stations to
//					the network and the component data.
// 2004-07-06	JTS, RTI		* Filled out notes for stations, 
//					  reservoirs, etc. 
//					* Changed delete code to handle 
//					  sub-component data.
// 2006-03-06	SAM, RTi		Comment out help button since on-line
//					help is currently disabled.
//------------------------------------------------------------------------------
// EndHeader

package DWR.StateModGUI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import DWR.StateMod.StateMod_Data;
import DWR.StateMod.StateMod_DataSet;
import DWR.StateMod.StateMod_DataSet_JTree;
import DWR.StateMod.StateMod_DataSet_WindowManager;
import DWR.StateMod.StateMod_DiversionRight;
import DWR.StateMod.StateMod_GUIUtil;
import DWR.StateMod.StateMod_InstreamFlowRight;
import DWR.StateMod.StateMod_Network_JFrame;
import DWR.StateMod.StateMod_ReservoirRight;
import DWR.StateMod.StateMod_RiverNetworkNode;
import DWR.StateMod.StateMod_Util;
import DWR.StateMod.StateMod_WellRight;
import RTi.TS.TS;
import RTi.TS.TSUtil;
import RTi.Util.GUI.JGUIUtil;
import RTi.Util.GUI.SimpleJButton;
import RTi.Util.GUI.SimpleJComboBox;
import RTi.Util.Help.URLHelp;
import RTi.Util.IO.DataSetComponent;
import RTi.Util.Message.Message;
import RTi.Util.String.StringUtil;

/**
Delete an object from a data set component.
*/
public class StateModGUI_DeleteComponent_JDialog 
extends JDialog 
implements ActionListener
{

/**
Data set component type to delete.
*/
private int __comp_type;
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
/**
List of identifiers for data to be deleted.
*/
private SimpleJComboBox __id_JComboBox = null;
/**
Panel for details about time series or other component.
*/
private JPanel __details_JPanel;
/**
Text area for details.
*/
private JTextArea __details_JTextArea;

private JButton __delete_JButton;
private JButton __details_JButton;
private JButton __help_JButton;
private JButton __cancel_JButton;

private StateMod_Network_JFrame __network = null;

/**
Constructor.
@param parent Parent JFrame, used to manage the dialog window position.
@param comp_type the StateMod data set component to delete.  The components
typically correspond to the primary component in a group, not the group itself.
@param dataset The StateMod data set to edit.
@param dataset_wm The window manager for the data set.
@param dataset_JTree The data set JTree.  If non-null, its contents will be
refreshed when data objects are added.
*/
public StateModGUI_DeleteComponent_JDialog ( JFrame parent, int comp_type, StateMod_DataSet dataset,
	StateMod_DataSet_WindowManager dataset_wm, StateMod_DataSet_JTree dataset_JTree )
{	this ( parent, comp_type, dataset, dataset_wm, dataset_JTree, null);
}

/**
Constructor.
@param parent Parent JFrame, used to manage the dialog window position.
@param comp_type the StateMod data set component to delete.  The components
typically correspond to the primary component in a group, not the group itself.
@param dataset The Statemod data set to edit.
@param dataset_wm The window manager for the data set.
@param dataset_JTree The data set JTree.  If non-null, its contents will be
refreshed when data objects are added.
@param network the network tied to the nodes.
*/
public StateModGUI_DeleteComponent_JDialog ( JFrame parent, int comp_type, StateMod_DataSet dataset,
	StateMod_DataSet_WindowManager dataset_wm, StateMod_DataSet_JTree dataset_JTree,
	StateMod_Network_JFrame network)
{	super ( parent, true ); // Modal
	String routine = "StateMod_DeleteComponent_JDialog";
	__comp_type = comp_type;
	__dataset = dataset;
	__dataset_wm = dataset_wm;
	__dataset_JTree = dataset_JTree;
	__network = network;

	try {
	GridBagConstraints gbc = new GridBagConstraints();
	GridBagLayout gbl = new GridBagLayout();
	JPanel main_JPanel = new JPanel();
	main_JPanel.setLayout ( gbl );

	int y = 0;

	if ( comp_type == StateMod_DataSet.COMP_STREAMGAGE_STATIONS ) {
		setTitle ( "StateMod GUI - Delete Streamgage Station" );
		String [] notes = { 
			"Stream gages are locations where flows have been measured historically.",
			"Deleting a stream gage will result in the following actions:",
			"1. References to the stream gages will be removed from the network, reconnecting as necessary.",
			"2. Historical stream flow, and base flow time series for the stream gage will be removed."
		};
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Streamgage ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Streamgage)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY ) {
		// TODO (SAM - 2004-07-04) need to enable as part of maintenance
		/*
		setTitle ( "StateMod GUI - Delete Delay Table (Monthly)" );
		JGUIUtil.addComponent (	main_JPanel, new JLabel("New delay table identifier:"),
			0, ++y, 5, 1, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.EAST );
		__new_id_JTextField = new JTextField ( "", 15 );
		JGUIUtil.addComponent (	main_JPanel, __new_id_JTextField,
			5, y, 5, 1, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.WEST );
		 */
	}
	else if ( comp_type == StateMod_DataSet.COMP_DELAY_TABLES_DAILY ) {
		// TODO (SAM - 2004-07-04)
		// need to enable as part of maintenance
		/*
		setTitle ( "StateMod GUI - Delete Delay Table (Daily)" );
		JGUIUtil.addComponent (	main_JPanel, new JLabel("New delay table identifier:"),
			0, ++y, 5, 1, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.EAST );
		__new_id_JTextField = new JTextField ( "", 15 );
		JGUIUtil.addComponent (	main_JPanel, __new_id_JTextField,
			5, y, 5, 1, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.WEST );
		*/
	}
	else if ( comp_type == StateMod_DataSet.COMP_DIVERSION_STATIONS ) {
		setTitle ( "StateMod GUI - Delete Diversion" );
		String [] notes = {
			"Diversion stations are on-channel point features.",
			"Deleting a diversion will result in the following actions:",
			"1. References to the diversion will be removed from the" +
				" network, reconnecting as necessary.",
			"2. Water rights for the diversion will be removed.",
			"3. Time series for the diversion (historical, demands," +
			" consumptive requirement) will be removed.",
			"References to the diversion in operational rights and" +
			" return flows will NOT be automatically be removed.",
			"4. References to the diversion will be removed from the" +
			" stream estimate coefficients and baseflow time series."
		};
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Diversion ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Diversion)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY ){
		StateMod_GUIUtil.setTitle ( this, __dataset, "Delete Precipitation Time Series (Monthly)", null );
		String [] notes = {
			"Precipitation time series (monthly) are referenced by reservoirs.",
			"Each time series has a location identifier (i.e., station identifier).",
			"Precipitation time series may be used by more than one reservoir.",
			"References to the time series series will not automatically be removed." };
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Precipitation station ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc,
			"Details (References to Selected Precipitation Time Series)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY ){
		StateMod_GUIUtil.setTitle ( this, __dataset,
		"Delete Evaporation Time Series (Monthly)", null );
		String [] notes = {
			"Evaporation time series (monthly) are referenced by reservoirs.",
			"Each time series has a location identifier (i.e., station identifier).",
			"Evaporation time series may be used by more than one reservoir.",
			"References to the time series series will not automatically be removed." };
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Evaporation station ID:" );
		y = setupDetails ( main_JPanel, y, gbl, gbc,
			"Details (References to Selected Evaporation Time Series)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_RESERVOIR_STATIONS ) {
		setTitle ( "StateMod GUI - Delete Reservoir" );
		String [] notes = { 
			"Reservoir stations are point features that can exist on or off the stream.",
			"Deleting a reservoir will result in the following actions:",
			"1. References to the reservoir will be removed from the network, reconnecting as necessary.",
			"2. Water rights for the reservoir will be removed.",
			"3. Time series for the reservoir (historical end of month/day and targets) will be removed.",
			"4. References to the reservoir will be removed from the stream estimate "
				+ "coefficients and baseflow time series."
		};
		// TODO (JTS - 2004-07-06) note #4 for baseflow nodes only
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Reservoir ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Reservoir)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_INSTREAM_STATIONS ) {
		setTitle ( "StateMod GUI - Delete Instream Station" );
		String [] notes = { 
			"An instream flow station is a point or reach where instream flow conditions are controlled.",
			"Deleting an instream flow station will result in the following actions:",
			"1. Referenes to the instream flow station will be removed from the network, reconnecting as necessary.",
			"2. Water rights for the instream flow station will be removed.",
			"3. Time series for the instream flow station (demand time series) will be removed.",
			"4. References to the instream flow station will be removed from the stream "
				+ "estimate coefficients and baseflow time series."
		};
		// TODO (JTS - 2004-07-06) note #4 for baseflow nodes only		
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Instream Station ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Instream Station)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_WELL_STATIONS ) {
		setTitle ( "StateMod GUI - Delete Well" );
		String [] notes = { 
			"A well station is a point where ground-water-only supply is used.",
			"Deleting a wall station will result in the following actions:",
			"1. References to the well station will be removed from the network, reconnecting as necessary.",
			"2. Water rights for the well station will be removed.",
			"3. Time series for the well station (historical pumping, demand time series) will be removed.",
			"4. References to the well station will be removed from the stream estimate "
				+ "coefficients and baseflow time series."
		};
		// TODO (JTS - 2004-07-06)
		// note #4 for baseflow nodes only		
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Well ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Well)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS ) {
		setTitle ( "StateMod GUI - Delete Stream Estimate Station" );
		String [] notes = { 
			"Stream estimate stations are locations where stream flows are estimated "
				+ "(e.g. headwater above diversions)",
			"Stream estimate stations are existing diversion, reservoir, instream flow, well, or other nodes.",
			"Deleting a stream estimate will result in the following action:",
			"1. Stream estimate coefficients and baseflow time series will be removed."
		};
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Stream Estimate Station ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc,
			"Details (References to Selected Stream Estimate Station)" );
	}
	else if ( comp_type == StateMod_DataSet.COMP_OTHER_NODE ) {
		setTitle ( "StateMod GUI - Delete Other" );
		String [] notes = { 
			"\"Other\" stations (those that are not a stream gage, diversion, reservoir, instream flow or well)",
			"are locations where StateMod should create output.",
			"The station may also be defined as a stream estimate station.",
			"Because \"other\" stations do not have water rights, they appear primarily "
				+ "in the network (and stream estimate) data.",
			"Deleting an \"other\" station will result in the following actions:",
			"1. References to the \"other\" station will be removed from the network, reconnecting as necessary.",
			"2. Stream estimate coefficients and baseflow time series will be removed."
		};
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Other ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Other)" );
	}
// REVISIT (JTS - 2004-07-06)
// note #2 for baseflow nodes only	
	else if ( comp_type == StateMod_DataSet.COMP_OPERATION_RIGHTS ) {
		setTitle ( "StateMod GUI - Delete Operational Right" );
		String [] notes = { 
			"Operational rights define the rules by which water is diverted, released, exchanged, etc.",
			"Operational rights often use one or more water sources and one or more water destinations.",
			"Deleting an operational right will result in the following actions:",
			"1. The operational right is deleted.",
			"References to the operational right are NOT adjusted."
		};
		y = setupNotes ( main_JPanel, y, gbc, notes );
		y = setupID ( main_JPanel, y, gbc, "Operational Right ID:");
		y = setupDetails ( main_JPanel, y, gbl, gbc, "Details (References to Selected Operational Right)" );
	}
	getContentPane().add ( "Center", main_JPanel );

	// Add help and close buttons

	JPanel button_JPanel = new JPanel();
	button_JPanel.setLayout ( new FlowLayout ( FlowLayout.CENTER ));

	__delete_JButton = new SimpleJButton("Delete", this);
	button_JPanel.add ( __delete_JButton );
	__help_JButton = new SimpleJButton("Help",this);
	__help_JButton.setEnabled ( false );
	//button_JPanel.add ( __help_JButton );
	__cancel_JButton = new SimpleJButton("Cancel",this);
	button_JPanel.add ( __cancel_JButton );

	getContentPane().add( "South", button_JPanel );
	} catch ( Exception e ) {
		Message.printWarning ( 3, routine, e );
	}
	setSize ( 500, 400 );	// Slack should be taken up by the details
	pack();
	JGUIUtil.center(this);	
	setVisible(true);
}

/**
Clean up before garbage collection.
*/
protected void finalize ()
throws Throwable
{	__delete_JButton = null;
	__details_JButton = null;
	__help_JButton = null;
	__cancel_JButton = null;
	super.finalize();
}

/**
Handle action events.
@param e ActionEvent to handle.
*/
public void actionPerformed ( ActionEvent e)
{	Object o = e.getSource();

	if ( o == __help_JButton ) {
		URLHelp.showHelpForKey ( "StateModGUI.DeleteNode" );
	}
	else if ( o == __cancel_JButton ) {
		dispose ();
	}
	else if ( o == __delete_JButton ) {
		// List in the order of data set components in the main GUI...
		if ( __comp_type == StateMod_DataSet.COMP_STREAMGAGE_STATIONS ){
			deleteStreamGage();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY ) {
			deleteDelayTable ( true );
		}
		else if ( __comp_type == StateMod_DataSet.COMP_DELAY_TABLES_DAILY ) {
			deleteDelayTable ( false );
		}
		else if ( __comp_type == StateMod_DataSet.COMP_DIVERSION_STATIONS ) {
			deleteDiversionStation();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY ) {
			deletePrecipitationTS();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY ) {
			deleteEvaporationTS();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_RESERVOIR_STATIONS ) {
			deleteReservoir();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_INSTREAM_STATIONS ) {
			deleteInstream();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_WELL_STATIONS ) {
			deleteWell();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_OTHER_NODE ) {
			deleteOtherNode();
		}
		else if ( __comp_type == StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS ) {
			deleteStreamEstimate();
		}
		else if(__comp_type == StateMod_DataSet.COMP_OPERATION_RIGHTS) {
			deleteOperationalRight();
		}
	}
	else if ( o == __details_JButton ) {
		detailsForDataObject();
	}
}

/**
Delete a delay table from the data set.
@param monthly_data If true, then a monthly delay table will be deleted.  If
false, a daily delay table will be deleted.
*/
private void deleteDelayTable ( boolean monthly_data )
{	// Do not enable because the StateMod GUI relies on outside applications
	// to build the file.
}

/**
Delete a diversion from the network.
*/
private void deleteDiversionStation()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteDiversionStation";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	String warning = "";
	if ( pos1 < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}
	
	// Delete the water rights...

	DataSetComponent comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_RIGHTS );
	List data2 = (List)comp2.getData();
	StateMod_DiversionRight divright;
	int size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		divright = (StateMod_DiversionRight)data2.get(i);
		if ( divright.getCgoto().equalsIgnoreCase(id) ) {
			data2.remove ( i );
			comp2.setDirty ( true );
			--i;
			--size2;
		}
	}

	// Remove from the network...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RIVER_NETWORK );
	data2 = (List)comp2.getData();
	// Find the node in the network.  There is a chance it won't be in the network...
	int pos2 = StateMod_Util.indexOf ( data2, id );
	StateMod_RiverNetworkNode netnode = null;
	if ( pos2 >= 0 ) {
		netnode = (StateMod_RiverNetworkNode)data2.get(pos2);
	}
	// Remove references to the node as the downstream...
	StateMod_RiverNetworkNode netnode2;
	size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		netnode2 = (StateMod_RiverNetworkNode)data2.get(i);
		if ( netnode2.getCstadn().equalsIgnoreCase(id) ) {
			// This node has the deleted node as downstream so need to re-attach...
			if ( netnode != null ) {
				netnode2.setCstadn(netnode.getCstadn());
			}
			else {
				netnode2.setCstadn("");
			}
		}
	}
	// Remove the node itself...
	if ( netnode != null ) {
		// Remove the node from the network...
		data2.remove ( pos2 );
		comp2.setDirty ( true );
	}

	// Remove the historical diversion (monthly) series...

	comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_DIVERSION_TS_MONTHLY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical diversion (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_DIVERSION_TS_DAILY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the demands (monthly) series...

	comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_DEMAND_TS_MONTHLY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the demands (override monthly) series...

	comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_DEMAND_TS_OVERRIDE_MONTHLY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the demands (override average monthly) series...

	comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_DEMAND_TS_AVERAGE_MONTHLY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the demands (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_DEMAND_TS_DAILY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// TODO - assume for now that the irrigation practice time series is provided by StateCU and is not edited.

	// Remove the consumptive water requirement (monthly) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the consumptive water requirement (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_CONSUMPTIVE_WATER_REQUIREMENT_TS_DAILY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}
	
	// Remove related stream estimate data...

	removeStreamEstimate(id);

	// Remove the diversion itself...

	data1.remove ( pos1 );
	comp.setDirty ( true );

	// Refresh the diversion time series display...

	JFrame div_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_DIVERSION, true );

	// Refresh the JTree diversion data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_DIVERSION_GROUP );

	// Close this window...
	dispose();

	// Push the diversion window to the front...

	// This does not seem to be working...
	if ( div_JFrame != null ) {
		div_JFrame.toFront();
	}

	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Delete a monthly evaporation time series from the data set.
*/
private void deleteEvaporationTS ( )
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteEvaporationTS";
	String id = __id_JComboBox.getSelected();

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY );
	List data = (List)comp.getData();
	int pos = TSUtil.indexOf(data,id,"Location",1);
	String warning = "";
	if ( pos < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Delete the time series...

	data.remove ( pos );

	comp.setDirty ( true );

	// Refresh the evaporation time series display...

	JFrame evap_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_EVAPORATION, true );

	// Refresh the JTree evaporation data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_EVAPORATION_GROUP );

	// Close this window...
	dispose();

	// Push the evaporation window to the front...

	// This does not seem to be working...
	if ( evap_JFrame != null ) {
		evap_JFrame.toFront();
	}
}

/**
Delete an instream node/reach from the network.
*/
private void deleteInstream()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteInstream";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_INSTREAM_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	String warning = "";
	if ( pos1 < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Delete the water rights...

	DataSetComponent comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_INSTREAM_RIGHTS );
	List data2 = (List)comp2.getData();
	StateMod_InstreamFlowRight isfright;
	int size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		isfright = (StateMod_InstreamFlowRight)data2.get(i);
		if ( isfright.getCgoto().equalsIgnoreCase(id) ) {
			data2.remove ( i );
			comp2.setDirty ( true );
			--i;
			--size2;
		}
	}

	// Remove from the network...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RIVER_NETWORK );
	data2 = (List)comp2.getData();
	// Find the node in the network.  There is a chance it won't be in the network...
	int pos2 = StateMod_Util.indexOf ( data2, id );
	StateMod_RiverNetworkNode netnode = null;
	if ( pos2 >= 0 ) {
		netnode = (StateMod_RiverNetworkNode)data2.get(pos2);
	}
	// Remove references to the node as the downstream...
	StateMod_RiverNetworkNode netnode2;
	size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		netnode2 = (StateMod_RiverNetworkNode)data2.get(i);
		if ( netnode2.getCstadn().equalsIgnoreCase(id) ) {
			// This node has the deleted node as downstream so need to re-attach...
			if ( netnode != null ) {
				netnode2.setCstadn(netnode.getCstadn());
			}
			else {
				netnode2.setCstadn("");
			}
		}
	}
	// Remove the node itself...
	if ( netnode != null ) {
		// Remove the node from the network...
		data2.remove ( pos2 );
		comp2.setDirty ( true );
	}

	// Remove the historical instream flow (monthly) series...

	comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical instream flow (monthly) series...

	comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_AVERAGE_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the demands (monthly) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_INSTREAM_DEMAND_TS_MONTHLY );
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}
	
	// Remove related stream estimate data...

	removeStreamEstimate(id);

	// Remove the diversion itself...

	data1.remove ( pos1 );
	comp.setDirty ( true );

	// Refresh the instrema flow display...

	JFrame isf_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_INSTREAM, true );

	// Refresh the JTree diversion data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_INSTREAM_GROUP );

	// Close this window...
	dispose();

	// Push the instream flow window to the front...

	// This does not seem to be working...
	if ( isf_JFrame != null ) {
		isf_JFrame.toFront();
	}

	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Delete an operational right from the data set.
*/
private void deleteOperationalRight ()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteOperationalRight";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_OPERATION_RIGHTS);
	List data = (List)comp.getData();
	int pos = StateMod_Util.indexOf(data, id);
	String warning = "";
	if ( pos < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}		
}

/**
Delete an "other" node from the network.
*/
private void deleteOtherNode()
{
	String routine = "StateModGUI_DeleteComponent_JDialog.deleteOtherNode";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_RIVER_NETWORK);
	List data = (List)comp.getData();
	int pos = StateMod_Util.indexOf(data, id);
	String warning = "";
	if ( pos < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Delete the node ...

	data.remove ( pos );

	comp.setDirty ( true );

	// Refresh the display...

	removeStreamEstimate(id);

	JFrame riverNetwork_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_RIVER_NETWORK, true );

	// Refresh the JTree evaporation data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_RIVER_NETWORK);

	// Close this window...
	dispose();

	// Push the window to the front...

	// This does not seem to be working...
	if ( riverNetwork_JFrame != null ) {
		riverNetwork_JFrame.toFront();
	}
	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Delete a monthly precipitation time series from the data set.
*/
private void deletePrecipitationTS ( )
{	String routine = "StateModGUI_DeleteComponent_JDialog.deletePrecipitationTS";
	String id = __id_JComboBox.getSelected();

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_PRECIPITATION_TS_MONTHLY );
	List data = (List)comp.getData();
	int pos = TSUtil.indexOf(data,id,"Location",1);
	String warning = "";
	if ( pos < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Delete the time series...

	data.remove ( pos );

	comp.setDirty ( true );

	// Refresh the precipitation time series display...

	JFrame precip_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_PRECIPITATION, true );

	// Refresh the JTree evaporation data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_PRECIPITATION_GROUP );

	// Close this window...
	dispose();

	// Push the precipitation window to the front...

	// This does not seem to be working...
	if ( precip_JFrame != null ) {
		precip_JFrame.toFront();
	}
}

/**
Delete a new reservoir from the network.
*/
private void deleteReservoir()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteReservoir";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	String warning = "";
	if ( pos1 < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}
	
	// Delete the water rights...

	DataSetComponent comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_RIGHTS );
	List data2 = (List)comp2.getData();
	StateMod_ReservoirRight isfright;
	int size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		isfright = (StateMod_ReservoirRight)data2.get(i);
		if ( isfright.getCgoto().equalsIgnoreCase(id) ) {
			data2.remove ( i );
			comp2.setDirty ( true );
			--i;
			--size2;
		}
	}

	// Remove from the network...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RIVER_NETWORK );
	data2 = (List)comp2.getData();
	// Find the node in the network.  There is a chance it won't be in the network...
	int pos2 = StateMod_Util.indexOf ( data2, id );
	StateMod_RiverNetworkNode netnode = null;
	if ( pos2 >= 0 ) {
		netnode = (StateMod_RiverNetworkNode)data2.get(pos2);
	}
	// Remove references to the node as the downstream...
	StateMod_RiverNetworkNode netnode2;
	size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		netnode2 = (StateMod_RiverNetworkNode)data2.get(i);
		if ( netnode2.getCstadn().equalsIgnoreCase(id) ) {
			// This node has the deleted node as downstream so need to re-attach...
			if ( netnode != null ) {
				netnode2.setCstadn(netnode.getCstadn());
			}
			else {
				netnode2.setCstadn("");
			}
		}
	}
	// Remove the node itself...
	if ( netnode != null ) {
		// Remove the node from the network...
		data2.remove ( pos2 );
		comp2.setDirty ( true );
	}

	// Remove the historical reservoir content (monthly) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical reservoir content (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_CONTENT_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical reservoir target (monthly) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical reservoir target (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RESERVOIR_TARGET_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}
	
	// Remove related stream estimate data...

	removeStreamEstimate(id);

	// Remove the reservoir itself...

	data1.remove ( pos1 );
	comp.setDirty ( true );

	// Refresh the display...

	JFrame res_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_RESERVOIR, true );

	// Refresh the JTree diversion data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_RESERVOIR_GROUP );

	// Close this window...
	dispose();

	// Push the window to the front...

	// This does not seem to be working...
	if ( res_JFrame != null ) {
		res_JFrame.toFront();
	}

	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Delete a stream estimate station.
*/
private void deleteStreamEstimate()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteStreamEstimate";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	String warning = "";
	if ( pos1 < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Remove from the network...

	DataSetComponent comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RIVER_NETWORK );
	List data2 = (List)comp2.getData();
	// Find the node in the network.  There is a chance it won't be in the network...
	int pos2 = StateMod_Util.indexOf ( data2, id );
	StateMod_RiverNetworkNode netnode = null;
	if ( pos2 >= 0 ) {
		netnode = (StateMod_RiverNetworkNode)data2.get(pos2);
	}
	// Remove references to the node as the downstream...
	StateMod_RiverNetworkNode netnode2;
	int size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		netnode2 = (StateMod_RiverNetworkNode)data2.get(i);
		if ( netnode2.getCstadn().equalsIgnoreCase(id) ) {
			// This node has the deleted node as downstream so need to re-attach...
			if ( netnode != null ) {
				netnode2.setCstadn(netnode.getCstadn());
			}
			else {
				netnode2.setCstadn("");
			}
		}
	}
	// Remove the node itself...
	if ( netnode != null ) {
		// Remove the node from the network...
		data2.remove ( pos2 );
		comp2.setDirty ( true );
	}

	// Remove the stream estimate coefficients

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_COEFFICIENTS);
	data2 = (List)comp2.getData();
	pos2 = StateMod_Util.indexOf ( data2, id);
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical natural flow ts (monthly) series ...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_NATURAL_FLOW_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical baseflow ts (daily) series ...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_NATURAL_FLOW_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the diversion itself...

	data1.remove ( pos1 );
	comp.setDirty ( true );

	// Refresh the display...

	JFrame stre_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_STREAMESTIMATE, true );

	// Refresh the JTree diversion data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_STREAMESTIMATE_GROUP );

	// Close this window...
	dispose();

	// Push the window to the front...

	// This does not seem to be working...
	if ( stre_JFrame != null ) {
		stre_JFrame.toFront();
	}

	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Delete a river station from the network.
*/
private void deleteStreamGage()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteStreamGage";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	String warning = "";
	if ( pos1 < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Remove from the network...

	DataSetComponent comp2 = __dataset.getComponentForComponentType ( StateMod_DataSet.COMP_RIVER_NETWORK );
	List data2 = (List)comp2.getData();
	// Find the node in the network.  There is a chance it won't be in the network...
	int pos2 = StateMod_Util.indexOf ( data2, id );
	StateMod_RiverNetworkNode netnode = null;
	if ( pos2 >= 0 ) {
		netnode = (StateMod_RiverNetworkNode)data2.get(pos2);
	}
	// Remove references to the node as the downstream...
	StateMod_RiverNetworkNode netnode2;
	int size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		netnode2 = (StateMod_RiverNetworkNode)data2.get(i);
		if ( netnode2.getCstadn().equalsIgnoreCase(id) ) {
			// This node has the deleted node as downstream so need to re-attach...
			if ( netnode != null ) {
				netnode2.setCstadn(netnode.getCstadn());
			}
			else {
				netnode2.setCstadn("");
			}
		}
	}
	// Remove the node itself...
	if ( netnode != null ) {
		// Remove the node from the network...
		data2.remove ( pos2 );
		comp2.setDirty ( true );
	}

	// Remove the historical ts (monthly) series ...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical ts (daily) series ...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_HISTORICAL_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the natural flow ts (monthly) series ...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_NATURAL_FLOW_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the natural flow ts (daily) series ...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMGAGE_NATURAL_FLOW_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	removeStreamEstimate(id);

	// Remove the station itself...

	data1.remove ( pos1 );
	comp.setDirty ( true );

	// Refresh the display...

	JFrame strg_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_STREAMGAGE, true );

	// Refresh the JTree data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_STREAMGAGE_GROUP );

	// Close this window...
	dispose();

	// Push the window to the front...

	// This does not seem to be working...
	if ( strg_JFrame != null ) {
		strg_JFrame.toFront();
	}

	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Delete a well from the network.
*/
private void deleteWell()
{	String routine = "StateModGUI_DeleteComponent_JDialog.deleteWell";
	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);

	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	String warning = "";
	if ( pos1 < 0 ) {
		warning += "\nIdentifier (" + id + ") is not found in the data set.";
	}
	if ( warning.length() > 0 ) {
	 	Message.printWarning ( 1, routine, warning );
		return;
	}

	// Delete the water rights...

	DataSetComponent comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_RIGHTS );
	List data2 = (List)comp2.getData();
	StateMod_WellRight isfright;
	int size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		isfright = (StateMod_WellRight)data2.get(i);
		if ( isfright.getCgoto().equalsIgnoreCase(id) ) {
			data2.remove ( i );
			comp2.setDirty ( true );
			--i;
			--size2;
		}
	}

	// Remove from the network...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_RIVER_NETWORK );
	data2 = (List)comp2.getData();
	// Find the node in the network.  There is a chance it won't be in the network...
	int pos2 = StateMod_Util.indexOf ( data2, id );
	StateMod_RiverNetworkNode netnode = null;
	if ( pos2 >= 0 ) {
		netnode = (StateMod_RiverNetworkNode)data2.get(pos2);
	}
	// Remove references to the node as the downstream...
	StateMod_RiverNetworkNode netnode2;
	size2 = data2.size();
	for ( int i = 0; i < size2; i++ ) {
		netnode2 = (StateMod_RiverNetworkNode)data2.get(i);
		if ( netnode2.getCstadn().equalsIgnoreCase(id) ) {
			// This node has the deleted node as downstream so need to re-attach...
			if ( netnode != null ) {
				netnode2.setCstadn(netnode.getCstadn());
			}
			else {
				netnode2.setCstadn("");
			}
		}
	}

	// Remove the node itself...
	if ( netnode != null ) {
		// Remove the node from the network...
		data2.remove ( pos2 );
		comp2.setDirty ( true );
	}

	// Remove the historical well pumping (monthly) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_PUMPING_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the historical well pumping (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_PUMPING_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the well demand (monthly) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_DEMAND_TS_MONTHLY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	// Remove the well demand (daily) series...

	comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_WELL_DEMAND_TS_DAILY);
	data2 = (List)comp2.getData();
	pos2 = TSUtil.indexOf ( data2, id, "Location", 1 );
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}
	
	// Remove related stream estimate data...

	removeStreamEstimate(id);

	// Remove the well itself...

	data1.remove( pos1 );
	comp.setDirty ( true );

	// Refresh the well display...

	JFrame well_JFrame = __dataset_wm.refreshWindow (StateMod_DataSet_WindowManager.WINDOW_WELL, true );

	// Refresh the JTree well data...

	__dataset_JTree.refresh ( StateMod_DataSet.COMP_WELL_GROUP );

	// Close this window...
	dispose();

	// Push the well window to the front...

	// This does not seem to be working...
	if ( well_JFrame != null ) {
		well_JFrame.toFront();
	}

	if (__network != null) {
		__network.deleteNode(id);
	}
}

/**
Display the details for a diversion station.
*/
private void detailsForDataObject ()
{	String id = StringUtil.getToken(__id_JComboBox.getSelected()," -",0,0);
	__details_JTextArea.setText ( StringUtil.toString(__dataset.getDataObjectDetails(  __comp_type, id),
		System.getProperty("line.separator")) );
}

private void removeStreamEstimate(String id) 
{	DataSetComponent comp = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_STATIONS );
	List data1 = (List)comp.getData();
	int pos1 = StateMod_Util.indexOf ( data1, id );
	if ( pos1 < 0 ) {
		// identifier not in data set -- not a baseflow node
		return;
	}

	DataSetComponent comp2 = __dataset.getComponentForComponentType (StateMod_DataSet.COMP_STREAMESTIMATE_COEFFICIENTS);
	List data2 = (List)comp2.getData();
	int pos2 = StateMod_Util.indexOf ( data2, id);
	if ( pos2 >= 0 ) {
		data2.remove(pos2);
		comp2.setDirty ( true );
	}

	data1.remove ( pos1 );
	comp.setDirty ( true );
}

/**
Set up the details GUI components.
*/
private int setupDetails ( JPanel main_JPanel, int y, GridBagLayout gbl, GridBagConstraints gbc, String title )
{	__details_JButton = new SimpleJButton("Update Details", this);
	JGUIUtil.addComponent ( main_JPanel, __details_JButton,
		2, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	__details_JPanel = new JPanel ();
	__details_JPanel.setLayout ( gbl );
	__details_JPanel.setBorder ( BorderFactory.createTitledBorder(title) );
	__details_JTextArea = new JTextArea ();
	__details_JTextArea.setEditable(false);
	__details_JTextArea.setLineWrap(false);
	JGUIUtil.addComponent ( __details_JPanel,
		new JScrollPane(__details_JTextArea),
		0, 0, 1, 1, 1.0, 1.0,
		GridBagConstraints.BOTH, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel, __details_JPanel,
		0, ++y, 5, 5, 1.0, 1.0,
		GridBagConstraints.BOTH, GridBagConstraints.WEST );
	return y;
}

/**
Set up the ID GUI components.
*/
private int setupID ( JPanel main_JPanel, int y, GridBagConstraints gbc, String label )
{	JGUIUtil.addComponent (	main_JPanel, new JLabel(label),
		0, ++y, 1, 1, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__id_JComboBox = new SimpleJComboBox ();
	JGUIUtil.addComponent ( main_JPanel, __id_JComboBox,
		1, y, 1, 1, 1.0, 0.0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	DataSetComponent comp = __dataset.getComponentForComponentType(__comp_type );
	if (comp == null) {
		return y;
	}
	List data = (List)comp.getData();
	int size = data.size();
	if ( (__comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY) ||
		(__comp_type == StateMod_DataSet.COMP_EVAPORATION_TS_MONTHLY)){
		TS ts;
		for ( int i = 0; i < size; i++ ) {
			ts = (TS)data.get(i);
			__id_JComboBox.addItem ( StateMod_Util.formatDataLabel(ts.getLocation(), ts.getDescription()) );
		}
	}
	else {
		StateMod_Data smdata;
		for ( int i = 0; i < size; i++ ) {
			smdata = (StateMod_Data)data.get(i);
			__id_JComboBox.addItem ( StateMod_Util.formatDataLabel(smdata.getID(), smdata.getName()) );
		}
	}
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

}

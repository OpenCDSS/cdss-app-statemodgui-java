package DWR.StateModGUI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DWR.StateMod.StateMod_DataSet;
import DWR.StateMod.StateMod_GUIUtil;
import DWR.StateMod.StateMod_Util;
import RTi.Util.GUI.JGUIUtil;
import RTi.Util.GUI.SimpleJButton;
import RTi.Util.IO.IOUtil;
import RTi.Util.Message.Message;

/**
This StateModGUI_Options_JFrame class displays options for the current
StateMod GUI session.  Currently, options cannot be saved between sessions.
*/
public class StateModGUI_Options_JFrame extends JFrame 
implements ActionListener, WindowListener
{

StateModGUI_JFrame __gui = null; // Parent UI
SimpleJButton __help_JButton;
SimpleJButton __cancel_JButton;
SimpleJButton __ok_JButton;
JTextField __editor_JTextField;
JTextField __statemodpath_JTextField;
JTextField __smdeltaPath_JTextField;

/**
Constructor.
*/
public StateModGUI_Options_JFrame( StateModGUI_JFrame gui )
{	JGUIUtil.setIcon(this, JGUIUtil.getIconImage());
	StateMod_GUIUtil.setTitle ( this, null, "Options", null );
	__gui = gui;

	addWindowListener ( this );

	JPanel main_JPanel = new JPanel();
	main_JPanel.setLayout ( new GridBagLayout() );

	int y=-1;
	JGUIUtil.addComponent ( main_JPanel, new JLabel (
		"Options only apply to the current session and are not saved." ), 
		0, ++y, 2, 1, 
		0, 0, 
		5, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel, new JLabel (
		"To make changes persistent, edit and save properties in the ${Home}\\system\\StateModGUI.cfg configuration file." ), 
		0, ++y, 2, 1, 
		0, 0, 
		5, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel, new JLabel (
		"Use the notation ${Home} to indicate the folder where the StateMod GUI is installed." ), 
		0, ++y, 2, 1, 
		0, 0, 
		5, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel, new JLabel (
		"Use the notation ${WorkingDir} to indicate the folder where the response file is located." ), 
		0, ++y, 2, 1, 
		0, 0, 
		5, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel,new JLabel("${Home}:" ),
		0, ++y, 1, 1, 
		0, 0, 
		0, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	int fieldWidth = 60;
	JTextField home_JTextField = new JTextField ( IOUtil.getApplicationHomeDir(), fieldWidth );
	home_JTextField.setToolTipText("Folder where StateMod GUI is installed.");
	home_JTextField.setEditable ( false );
	JGUIUtil.addComponent ( main_JPanel, home_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel, new JLabel ( "Editor:" ), 
		0, ++y, 1, 1, 
		0, 0, 
		0, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__editor_JTextField = new JTextField ( fieldWidth );
	__editor_JTextField.setToolTipText("Editor program for text files such as StateMod output files.");
	__editor_JTextField.setText ( StateMod_GUIUtil.getEditorPreference() );
	JGUIUtil.addComponent ( main_JPanel, __editor_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel,new JLabel("Response file:" ),
		0, ++y, 1, 1, 
		0, 0, 
		0, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	JTextField responseFile_JTextField = new JTextField ( fieldWidth );
	responseFile_JTextField.setToolTipText( "File that lists all dataset input files." );
	StateMod_DataSet dataSet = __gui.getDataSet();
	String responseFile = "Not specified";
	String responseFileStateModExecutable = "Response file not specified";
	if ( dataSet != null ) {
		responseFile = dataSet.getDataFilePath(dataSet.getDataSetFileName());
		String propVal =
			dataSet.getUnhandledResponseFileProperties().getValue("StateModExecutable");
		if ( responseFileStateModExecutable == null ) {
			responseFileStateModExecutable = "Not specified in response file";
		}
		else {
			responseFileStateModExecutable = propVal;
		}
	}
	responseFile_JTextField.setText ( responseFile );
	responseFile_JTextField.setEditable ( false );
	JGUIUtil.addComponent ( main_JPanel, responseFile_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel,new JLabel("Response file StateModExecutable property:" ),
		0, ++y, 1, 1, 
		0, 0, 
		0, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	JTextField responseFileStateModExecutable_JTextField =
		new JTextField ( responseFileStateModExecutable, fieldWidth );
	responseFileStateModExecutable_JTextField.setToolTipText(
		"Response file property to specify StateMod executable location, relative to response file." );
	responseFileStateModExecutable_JTextField.setEditable ( false );
	JGUIUtil.addComponent ( main_JPanel, responseFileStateModExecutable_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel,new JLabel("StateModExecutablePath:" ),
		0, ++y, 1, 1, 
		0, 0, 
		0, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	__statemodpath_JTextField = new JTextField ( fieldWidth );
	__statemodpath_JTextField.setToolTipText(
		"Path to StateMod executable, used if not specified in response file StateModExecutable property.");
	__statemodpath_JTextField.setText (	StateMod_Util.getStateModExecutable(null,null,false) );
	JGUIUtil.addComponent ( main_JPanel, __statemodpath_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel,new JLabel("SmDeltaExecutablePath:" ),
			0, ++y, 1, 1, 
			0, 0, 
			0, 5, 0, 0,
			GridBagConstraints.NONE, GridBagConstraints.EAST );
	__smdeltaPath_JTextField = new JTextField ( fieldWidth );
	__smdeltaPath_JTextField.setToolTipText("Path to SmDelta utility program executable.");
	__smdeltaPath_JTextField.setText ( StateMod_Util.getSmDeltaExecutable(false) );
	JGUIUtil.addComponent ( main_JPanel, __smdeltaPath_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
	JGUIUtil.addComponent ( main_JPanel,new JLabel("DataHomeDefault:" ),
		0, ++y, 1, 1, 
		0, 0, 
		0, 5, 0, 0,
		GridBagConstraints.NONE, GridBagConstraints.EAST );
	JTextField dataHome_JTextField =
		new JTextField ( StateModGUI.getConfigurationPropValue("DataHomeDefault"), fieldWidth );
	dataHome_JTextField.setToolTipText("Path to StateMod datasets, for File...Open menu default.");
	dataHome_JTextField.setEditable ( false );
	JGUIUtil.addComponent ( main_JPanel, dataHome_JTextField, 
		1, y, 1, 1, 
		0, 0, 
		0, 5, 0, 5,
		GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );

	// Add bottom buttons...

	JPanel button_JPanel = new JPanel();
	button_JPanel.setLayout ( new FlowLayout ( FlowLayout.CENTER ) );

	//__help_JButton = new SimpleJButton ( "Help", this );
	//final_panel.add ( __help_JButton );
	__ok_JButton = new SimpleJButton ( "OK", this );
	button_JPanel.add ( __ok_JButton );
	__cancel_JButton = new SimpleJButton ( "Cancel", this );
	button_JPanel.add ( __cancel_JButton );

	/*
	JGUIUtil.addComponent ( this, button_JPanel, 
		0, gbc.RELATIVE, 2, 1, 
		0, 0, 
		GridBagConstraints.NONE, GridBagConstraints.SOUTH );
	*/
	getContentPane().add ( main_JPanel, "Center" );
	getContentPane().add ( button_JPanel, "South" );

	pack();
	JGUIUtil.center (this);
	setResizable(false);
	setVisible(true);
}

/**
Handle action events.
@param e ActionEvent to handle.
*/
public void actionPerformed ( ActionEvent e )
{	//if ( e.getSource() == __help_JButton ) {
	//	URLHelp.showHelpForKey ( "StateModGUI.GeneralPrefs" );
	//}
	if ( e.getSource() == __cancel_JButton ) {
		closeWindow ();
	}
	else if ( e.getSource() == __ok_JButton ) {
		if ( savePreferences() ) {
			closeWindow ();
		}
	}
}

/**
Checks the text fields for validity before they are saved back into the data object.
@return 0 if the text fields are okay, 1 if fatal errors exist, and -1 if only non-fatal errors exist.
*/
private int checkInput()
{	String routine = "StateModGUI_Options_JFrame.checkInput";
	//String name = __diversionName_JTextField.getText().trim();
	String warning = "";
	int fatal_count = 0;

/*
	if ( name.length() > 24 ) {
		warning += "\nDiversion name is > 24 characters.";
		++fatal_count;
	}
	if ( !StringUtil.isDouble(capacity) ) {
		warning += "\nCapacity (" + capacity + ") is not a number.";
		++fatal_count;
	}
	if ( !acres.equals("") && !StringUtil.isDouble(acres) ) {
		warning += "\nIrrigated acreage (" + acres +
			") is not a number.";
		++fatal_count;
	}
	if ( !awc.equals("") && !StringUtil.isDouble(awc) ) {
		warning += "\nAWC (" + awc + ") is not a number.";
		++fatal_count;
	}
*/
	if ( warning.length() > 0 ) {
		warning = warning + "\nCorrect or Cancel.";
		Message.printWarning ( 1, routine, warning, this );
		if ( fatal_count > 0 ) {
			// Fatal errors...
			return 1;
		}
		else {
			// Nonfatal errors...
			return -1;
		}
	}
	else {
		// No errors...
		return 0;
	}
}

/**
Close the window.
*/
protected void closeWindow ( )
{	setVisible(false);
	dispose();
}

/**
Clean up before garbage collection.
*/
protected void finalize ()
throws Throwable
{	__help_JButton = null;
	__cancel_JButton = null;
	__ok_JButton = null;
	__editor_JTextField = null;
	__statemodpath_JTextField = null;
	super.finalize();
}

/**
 * Save the preferences to in-memory locations (not the configuration file).
 * @return
 */
protected boolean savePreferences ()
{	if ( checkInput() == 1 ) {
		return false;
	}
	StateMod_GUIUtil.setEditorPreference ( __editor_JTextField.getText().trim() );
	StateMod_Util.setStateModExecutable (__statemodpath_JTextField.getText().trim() );
	StateMod_Util.setSmDeltaExecutable (__smdeltaPath_JTextField.getText().trim() );
	return true;
}

/**
Responds to Window Activated events; does nothing.
@param e the WindowEvent that happened.
*/
public void windowActivated(WindowEvent e) {}

/**
Responds to Window closed events; does nothing.
@param e the WindowEvent that happened.
*/
public void windowClosed(WindowEvent e) {}

/**
Responds to Window closing events - same as cancel.
@param e the WindowEvent that happened.
*/
public void windowClosing(WindowEvent e)
{	closeWindow();
}

/**
Responds to Window de-activated events - does nothing.
@param e the WindowEvent that happened.
*/
public void windowDeactivated(WindowEvent e)
{
}

/**
Responds to Window de-iconified events; does nothing.
@param e the WindowEvent that happened.
*/
public void windowDeiconified(WindowEvent e) {}

/**
Responds to Window iconified events - does nothing.
@param e the WindowEvent that happened.
*/
public void windowIconified(WindowEvent e) {
}

/**
Responds to Window opened events; does nothing.
@param e the WindowEvent that happened.
*/
public void windowOpened(WindowEvent e) {}

/**
Responds to Window opening events; does nothing.
@param e the WindowEvent that happened.
*/
public void windowOpening(WindowEvent e) {}

}
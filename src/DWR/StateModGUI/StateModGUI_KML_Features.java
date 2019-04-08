package DWR.StateModGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

// FIXME SAM 2008-03-10 Need to re-enable KML writer
// import rti.kml.KMLWriter;
import DWR.StateMod.StateMod_DataSet;
import DWR.StateMod.StateMod_Diversion;
import DWR.StateMod.StateMod_Reservoir;
import DWR.StateMod.StateMod_StreamGage;
import RTi.GIS.GeoView.GeoProjection;
import RTi.GIS.GeoView.GeoRecord;
import RTi.GIS.GeoView.GeographicProjection;
import RTi.GIS.GeoView.UTMProjection;
import RTi.GR.GRPoint;
import RTi.GR.GRShape;
import RTi.Util.GUI.JFileChooserFactory;
import RTi.Util.GUI.JGUIUtil;
import RTi.Util.IO.DataSetComponent;
import RTi.Util.Message.Message;

/**
 * Controls the generation of the KML file.
 * <p>
 * Displays a window providing selection of which features are to be
 * included and a directory chooser for the KML output directory. 
 */
public class StateModGUI_KML_Features extends JFrame implements ActionListener
//FIXME SAM 2008-03-10 Need to re-enable KML writer
//,KMLWriter.WriteImpl
{
  public static final int HEIGHT = 400;

  public static final int WIDTH = 500;

  private JButton _buttonCancel;

  private JButton _buttonOK;

  private StateMod_DataSet _dataset;

  private JCheckBox _diversionsCheckbox;

  private GeographicProjection _geographicProjection;

  private JCheckBox _reservoirsCheckbox;

  private JCheckBox _streamGageStationsCheckbox;

  private UTMProjection _uTMProjection;

  private JTextField _pathTF;

  private StateModGUI_JFrame _stateModGUI_JFrame;

  /**
   * Creates a window providing selection of features that are to be
   * written to KML and a directory chooser for the KML output directory. 
   * 
   * @param jFrame Parent JFrame
   * @param dataset 
   */
  public StateModGUI_KML_Features(StateModGUI_JFrame jFrame, StateMod_DataSet dataset) {
    _stateModGUI_JFrame = jFrame;
    setTitle("Save Data Set as KML");
    
    _dataset = dataset;
    
    // Set up for conversion from UTM to longlat
    try
      {
        _uTMProjection = UTMProjection.parse("UTM,13");
        _geographicProjection = new GeographicProjection();
      }
    catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    initGUI();

    pack();
    center();
    setVisible(true);
  }

  /**
   * Dispatches action depending on what control button pressed
   */
  public void actionPerformed(ActionEvent event)
  {
    Object source = event.getSource();

    if (source == _buttonCancel)
      {
        setVisible(false);
        dispose();
      }
    else if (source == _buttonOK)
      {
        setVisible(false);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() 
          {
            try 
            {
              outputKML(_dataset);
            }
            catch(Throwable t)
            {
              Message.printWarning(1, "actionPerformed",
              "Problem generating KML");
            }
          }
        });

      }
  }
  
  /**
   * Centers window on screen
   */
  private void center()
  {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension window = this.getSize();
    setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
  }
  
  /**
   * Returns the display name for the folder used by GoogleEarth.
   */
  public String getKMLDisplayName() 
  {
    return "CDSS_Gunnison_Demo";
  }
  
  /**
   * Initialize GUI
   */
  private void initGUI()
  {
    JGUIUtil.setIcon(this, JGUIUtil.getIconImage());
    setBackground(Color.lightGray);

    JPanel outerPNL = new JPanel();
    outerPNL.setLayout(new BorderLayout());
    getContentPane().add(outerPNL);

    TitledBorder border = new TitledBorder("");
    outerPNL.setBorder(border);

    JLabel instructions = new JLabel("Select Features to be included in KML");
    outerPNL.add(instructions, BorderLayout.NORTH);
    JPanel middlePNL = new JPanel();
    middlePNL.setLayout(new BorderLayout());
    outerPNL.add(middlePNL, BorderLayout.CENTER);

    JPanel featuresPNL = new JPanel();
    featuresPNL.setBorder(new TitledBorder("Features"));
    featuresPNL.setLayout(new GridLayout(0,1));
    //featuresPNL.setBorder(new EmptyBorder(2, 2, 2, 2));
    middlePNL.add(featuresPNL, BorderLayout.CENTER);

    _diversionsCheckbox = new JCheckBox("Diversions");
    _diversionsCheckbox.setSelected(true);
    _reservoirsCheckbox = new JCheckBox("Reservoirs");
    _reservoirsCheckbox.setSelected(true);
    _streamGageStationsCheckbox = new JCheckBox("Stream Gage Stations");
    _streamGageStationsCheckbox.setSelected(true);
    featuresPNL.add(_diversionsCheckbox);
    featuresPNL.add(_reservoirsCheckbox);
    featuresPNL.add(_streamGageStationsCheckbox);

    JPanel outputPNL = new JPanel();
    outputPNL.setLayout(new BorderLayout());
    outputPNL .setBorder(new TitledBorder("Output"));
    middlePNL.add(outputPNL, BorderLayout.SOUTH);

    _pathTF = new JTextField(JGUIUtil.getLastFileDialogDirectory());
    outputPNL.add(_pathTF, BorderLayout.CENTER);

    JButton browseBTN = new JButton("Browse...");
    browseBTN.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            try {
              selectKMLDirectory();
            }
            catch(Throwable t){
              Message.printWarning(1, "actionPerformed",
              "Problem selecting KML output directory");
            }
          }
        });
      }
    });
    outputPNL.add(browseBTN,BorderLayout.EAST);

    // install buttonDismiss
    JPanel southPNL = new JPanel();
    southPNL.setLayout(new BorderLayout());
    outerPNL.add(southPNL, BorderLayout.SOUTH);

    JPanel controlPNL = new JPanel();
    southPNL.add(controlPNL, BorderLayout.CENTER);

    _buttonOK = new JButton("Save");
    _buttonOK.addActionListener(this);
    controlPNL.add(_buttonOK);

    _buttonCancel = new JButton("Cancel");
    _buttonCancel.addActionListener(this);
    controlPNL.add(_buttonCancel);

    this.addWindowListener(new StateModGUI_KML_FeaturesAdapter());
  }
  
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

    if (selectedFile != null && (selectedFile.canWrite() == false || selectedFile.isFile() == false))
      {
        // Post an error message and return
        JOptionPane.showMessageDialog(this, "You do not have permission to write to file:" + "\n" + selectedFile.getPath(), "File not Writable",
            JOptionPane.ERROR_MESSAGE);

        return false;
      }
    else
      return true;
  }

  /**
   * Generate KML for the specified data set.
   * 
   * @param dataset
   */
  private void outputKML(StateMod_DataSet dataset)
  {

    if (dataset == null)
      {
        throw new RuntimeException("dataset null");
      }
    File out = new File("kml-output");
   // TODO: File out = new File(_pathTF.getText());
   String path = out.getAbsolutePath();
   _stateModGUI_JFrame.printStatus("Generating KML output in: "
        + path, StateModGUI_JFrame.WAIT);
/* FIXME SAM 2008-03-10 Need to re-enable KML writer
    try 
    {
      KMLWriter.writeKML(out,getKMLDisplayName(), this);
    } 
    catch (IOException e) 
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    _stateModGUI_JFrame.printStatus("Generated KML output in :"
        + path, StateModGUI_JFrame.READY);
        */
  }

  /**
   * Displays a JFileChooser for selection of KML directory.
   */
  public void selectKMLDirectory()
  {
    JGUIUtil.setWaitCursor(this, true);

    JFileChooser jfc = JFileChooserFactory.createJFileChooser(JGUIUtil.getLastFileDialogDirectory());
    jfc.setDialogTitle("Select KML output directory");
    jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    jfc.setSelectedFile(new File(JGUIUtil.getLastFileDialogDirectory()));

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension window = jfc.getSize();
    jfc.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
    JGUIUtil.setWaitCursor(this, false);

    /*
     * The right way to check that file is write-able is to override
     * JFileChooser.approveSelection(). However I am reluctant to modify
     * JFileChooserFactory to use an instance of JFileChooser with
     * approveSelection() overridden to do the checking because of my
     * unfamiliarity with how it is being used
     */
    while (true)
      {
        
        int retVal = jfc.showSaveDialog(this);

        if (retVal != JFileChooser.APPROVE_OPTION)
          {
            return;
          }
        else
          {
            if (jfc.getCurrentDirectory().canWrite())
              {
                String currDir = (jfc.getSelectedFile()).toString();
                JGUIUtil.setLastFileDialogDirectory(currDir);
                _pathTF.setText(currDir);
                return;
              }
            else
              {
                String msg = (jfc.getCurrentDirectory()).toString()
                + "is not write-able"
                + "\nCheck the directory permissions!";
                JOptionPane.showConfirmDialog(null, msg, 
                    "Directory not write-able", JOptionPane.ERROR_MESSAGE);
              }
          }
      } // eof while
  } // eof saveToFile
 
  
  /**
   * Generate KML for Diversions.
   * 
   * @param writer 
   */
/*FIXME SAM 2008-03-10 Need to re-enable KML writer
  private void writeDiversionsKML(KMLWriter writer) 
  {
    String id;
    String name;
    String alias;
    boolean pointFound = false;
    GRShape tmpShape; 
    StateMod_Diversion div;
    
    DataSetComponent comp = _dataset.getComponentForComponentType (
        StateMod_DataSet.COMP_DIVERSION_STATIONS );
    Vector data = (Vector)comp.getData();
    int size = data.size();
    
    for (int i = 0; i < size; i++) 
      {
        div = (StateMod_Diversion)data.elementAt(i);

        name = div.getName();
        id = div.getID();
        alias = id + " - " + name;
        GeoRecord geoRecord = div.getGeoRecord();
        
        if (geoRecord != null)
          {
            GRShape gRShape = div.getGeoRecord().getShape();
            if (gRShape != null	&& gRShape.type == GRShape.POINT)
              {
                // Create a Diversions folder only if a Diversion with a location is found
                if (!pointFound)
                  {
               
                    writer.startFolder("Diversions", null, null);
                    pointFound = true;
                  }

                tmpShape = GeoProjection.projectShape(_uTMProjection, _geographicProjection,
                    gRShape, false);
//                System.out.print("--> Name: " + name + " id: " + id );
//                System.out.println("        Point: " + ((GRPoint)gRShape).toString() + " ==> " + tmpShape.toString());
String tmp =
  "<a href='280500.pdf'>Ditch Background Memo</a>"
  + "<br><a href='280500_Model.html'>Summary of Use in Data Set</a>"
 + "<br><a href='280500_Model.png'>Historical and Simulated Data Graph</a>";
// + "<br><a href='GunnisonInfo_200407.pdf'>Gunnison Basin Summary Report</a>";

//String tmp = "<a href='document.html'>" + id 
//+ "</a><br><a href='C:\\CDSS\\datasets\\Test_Gunnison\\StateMod\\280500_Monthly.png'>Historical and Simulated Data Graph</a>"
//+ "<br><a href='C:\\CDSS\\datasets\\Test_Gunnison\\StateMod\\280500_Summary.html'>Summary</a>"
//+ "<br><a href='C:\\CDSS\\datasets\\Test_Gunnison\\StateMod\\GunnisonInfo_200407.pdf'>Gunnison Basin Summary Report</a>";


String tmp1 = getCDSS_URL(id);
if (tmp1 != null)
  {
  tmp = tmp + "<br><a href='" + tmp1 + "'>On-line HydroBase Data</a>";
  }
                writer.addPlacemark(alias, tmp, null, ((GRPoint)tmpShape).x,
                    ((GRPoint)tmpShape).y, true);
              }
          }
      }
    
    if (pointFound)
      {
        writer.endFolder();
      }
  }
  */
  
  /**
   * Returns the URL of structure page on the CDSS data web site.
   * 
   * @param id
   * @return
   */
private String getCDSS_URL(String id)
{
  String ret = null;
 /* FIXME SAM 2008-03-16 Need to decouple the StateMod GUI from HydroBaseDMI
  * put the water district code in cdss.domain
  try
    {
      int wdid[]= DWR.DMI.HydroBaseDMI.HydroBase_WaterDistrict.parseWDID(id);
      
      ret = "http://cdss.state.co.us/structure/structure.aspx?wd=" + wdid[0]
             + "&strid=" + wdid[1];
    }
  catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    */
 
  return ret;
}
  /**
   * Checks if a string is a valid integer
   *
   * @param s possibleInteger
   * @return whether it is a integer
   */
  private static final boolean isInt(String s) {
      boolean ret = false;

      try {
          String ss = s.trim();
          if (!ss.equals("")) {
              Integer.parseInt(ss);
              ret = true;
          }
      } catch (Exception ex) {
      }
      return ret;
  }
  /**
   * Generate KML for selected features.
   */
  /* FIXME SAM 2008-03-10 Need to re-enable KML writer
  public void writeKML(KMLWriter writer) throws IOException 
  {
    // Add links to Agricultural features
    // TODO: Should not be hard coded!
    writer.addNetworkLink("Irrigated Lands", null, "Irrigated_Lands.kmz");
    writer.addNetworkLink("Rivers", null, "Rivers.kmz");
    writer.addNetworkLink("Water Districts", null, "Water_Districts.kmz");
    
    
    if ( _diversionsCheckbox.isSelected())
      {
        writeDiversionsKML(writer);
      }
    if (_reservoirsCheckbox.isSelected())
      {
        writeReservoirsKML(writer);
      }
    if (_streamGageStationsCheckbox.isSelected())
      {
        writeStreamGageStationsKML(writer);
      }
  }
  */

  /**
   * Generate KML for Reservoirs.
   * @param writer 
   */
/*FIXME SAM 2008-03-10 Need to re-enable KML writer
  private void writeReservoirsKML(KMLWriter writer) 
  {
    String id;
    String name;
    boolean pointFound = false;
    GRShape tmpShape; 
    StateMod_Reservoir res;
    DataSetComponent comp = _dataset.getComponentForComponentType (
        StateMod_DataSet.COMP_RESERVOIR_STATIONS );
    Vector data = (Vector)comp.getData();
    int size = data.size();
    
    for (int i = 0; i < size; i++) 
      {
        res = (StateMod_Reservoir)data.elementAt(i);

        name = res.getName();
        id = res.getID();

        GeoRecord geoRecord = res.getGeoRecord();
        
        if (geoRecord != null)
          {
            GRShape gRShape = res.getGeoRecord().getShape();
            if (gRShape != null && gRShape.type == GRShape.POINT)
              {
                // Create a Diversions folder only if a Diversion with a location is found
                if (!pointFound)
                  {
                    writer.startFolder("Reservoirs", null, null);
                    pointFound = true;
                  }

                tmpShape = GeoProjection.projectShape(_uTMProjection, _geographicProjection,
                    gRShape, false);
               
                writer.addPlacemark(name, id, null, ((GRPoint)tmpShape).x,
                    ((GRPoint)tmpShape).y, true);
              }
          }
      }
    if (pointFound)
      {
        writer.endFolder();
      }
  }
  */
  
  /**
   * Generate KML for Stream Gage.
   * @param writer 
   */
/*FIXME SAM 2008-03-10 Need to re-enable KML writer
  private void writeStreamGageStationsKML(KMLWriter writer) 
  {
    String id;
    String name;
    boolean pointFound = false;
    GRShape tmpShape; 
    StateMod_StreamGage streamGage;
    
    DataSetComponent comp = _dataset.getComponentForComponentType (
        StateMod_DataSet.COMP_STREAMGAGE_STATIONS );
    Vector data = (Vector)comp.getData();
    int size = data.size();
    
    for (int i = 0; i < size; i++) 
      {
        streamGage = (StateMod_StreamGage)data.elementAt(i);

        name = streamGage.getName();
        id = streamGage.getID();

        GeoRecord geoRecord = streamGage.getGeoRecord();
        if (geoRecord != null)
          {
            GRShape gRShape = streamGage.getGeoRecord().getShape();
            if (gRShape != null && gRShape.type == GRShape.POINT)
              {
                // Create a StreamGage folder only if a StreamGage with a location is found
                if (!pointFound)
                  {
                    writer.startFolder("StreamGage", null, null);
                    pointFound = true;
                  }

                tmpShape = GeoProjection.projectShape(_uTMProjection, _geographicProjection,
                    gRShape, false);

                writer.addPlacemark(name, id, null, ((GRPoint)tmpShape).x,
                    ((GRPoint)tmpShape).y, true);
              }
          }
      }
    if (pointFound)
      {
        writer.endFolder();
      }
  }
  */
  
  class StateModGUI_KML_FeaturesAdapter extends WindowAdapter
  {
    public void windowClosing(WindowEvent event)
    {
      setVisible(false); // hide the Frame
      dispose();
    }
  }
} //eof StateModGUI_KML_Features

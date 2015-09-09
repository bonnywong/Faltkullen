package faltkullen;

import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ArrayList;


public class SwingGUI implements ActionListener{
 private JFrame frame;
 private JMenuBar menuBar1;
 private JMenuBar menuBar2;
 private JMenu fileMenu;
 private JMenu helpMenu;
 private JMenu editMenu;
 private JMenu viewMenu;
 private JTabbedPane tabbedPane;
 private JPanel container; // the frames child
 private JPanel tab1Panel;
 private JPanel tab2Panel;
 private JPanel tab3Panel;
 private JPanel tab4Panel;
    private JPanel tab5Panel;
 private JPanel mapPanel;
 private JPanel startPanel;
 private JPanel consolePanel;
 private JLabel attributeLabel1;
 private JLabel attributeLabel2;
 private JLabel attributeLabel3;
 private JLabel attributeLabel4;
 private JLabel attributeLabel5;
 private JLabel attributeLabel6;
 private JLabel attributeLabel7;
 private JLabel attributeLabel8;
 private JLabel attributeLabel9;
 private JLabel attributeLabel10;
 private JTextField attribute1TextFieldMin;
 private JTextField attribute2TextFieldMin;
 private JTextField attribute3TextFieldMin;
 private JTextField attribute4TextFieldMin;
 private JTextField attribute5TextFieldMin;
 private JTextField attribute6TextFieldMin;
 private JTextField attribute7TextFieldMin;
 private JTextField attribute8TextFieldMin;
 private JTextField attribute9TextFieldMin;
 private JTextField attribute10TextFieldMin;
 private JTextField attribute1TextFieldBase;
 private JTextField attribute2TextFieldBase;
 private JTextField attribute3TextFieldBase;
 private JTextField attribute4TextFieldBase;
 private JTextField attribute5TextFieldBase;
 private JTextField attribute6TextFieldBase;
 private JTextField attribute7TextFieldBase;
 private JTextField attribute8TextFieldBase;
 private JTextField attribute9TextFieldBase;
 private JTextField attribute10TextFieldBase;
 private JTextField attribute1TextFieldMax;
 private JTextField attribute2TextFieldMax;
 private JTextField attribute3TextFieldMax;
 private JTextField attribute4TextFieldMax;
 private JTextField attribute5TextFieldMax;
 private JTextField attribute6TextFieldMax;
 private JTextField attribute7TextFieldMax;
 private JTextField attribute8TextFieldMax;
 private JTextField attribute9TextFieldMax;
 private JTextField attribute10TextFieldMax;
 private Checkbox att1Checkbox;
 private Checkbox att2Checkbox;
 private Checkbox att3Checkbox;
 private Checkbox att4Checkbox;
 private Checkbox att5Checkbox;
 private Checkbox att6Checkbox;
 private Checkbox att7Checkbox;
 private Checkbox att8Checkbox;
 private Checkbox att9Checkbox;
 private Checkbox att10Checkbox;
 
 private JButton startButton;
 private JButton stopButton;
 private JButton resetButton;
 private JLabel sims;
 private JLabel generations;
 private JScrollPane consoleScrollPane;
 public JTextArea consoleText;
 private BufferedImage tank;
 private JMenuItem exit;
 private Main main;
 private TestOfMap map;
 
 private JLabel[] evolutionLabels;
 private JTextField[] evolutionTexts;
 private Checkbox[] evolutionCheckboxes;
 
 private JLabel[] tankLabels;
 private JTextField[][] tankTexts;
 private Checkbox[] tankCheckboxes;
 private String[] tankAttributeNames = {"Weapon Damage", "Weapon Range", "Attack Interval", "Splash Radius", "Protection", "Velocity", "Fuel per Kilometer", "Fuel Capacity", "Sensor Range", "Sensor Interval"};
 
 private JLabel[] soldierLabels;
 private JTextField[][] soldierTexts;
 private Checkbox[] soldierCheckboxes;
 private String[] soldierAttributeNames = {"Weapon Damage", "Weapon Accuracy", "Weapon Range", "Protection", "Sensor Range", "Sensor Interval", "Soldier Accuracy", "Soldier Morale", "Soldier Movespeed"};
 private String[][] soldierBaseValues = new String[9][3];
 private int[] soldierCosts = {100, 100, 80, 100, 225, -75, 125, 25, 75};
   //Weapon Damage = 100
   //Protection = 100
   //Soldier Accuracy = 125

    //GUI Components for loading and saving settings
    private JFileChooser fc;
 
 public SwingGUI(Main m) {
   System.out.println("RUN");
   main = m;
  initComponents();
 }
 public static void main(String[] args) {
  new SwingGUI(null);
 }
 
 public int currentMapID = 0;
 
 public void resetMap(){
   if(map!=null){
     return;
   }
  TestOfMap m = new TestOfMap(2);
  map = m;
  map.mapID = currentMapID;
  currentMapID++;
  m.test = m;
  mapPanel.add(m);
  main.setMap(m);
 }

    private void initComponents() {
        frame = new JFrame("Advanced Combat Simulator by SuccessTrain");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        container = new JPanel();
        frame.setContentPane(container);
        menuBar1 = new JMenuBar();
        menuBar2 = new JMenuBar();
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        viewMenu = new JMenu("View");
        helpMenu = new JMenu("Help");
        tabbedPane = new JTabbedPane();
        tab1Panel = new JPanel();
        tab2Panel = new JPanel();
        tab3Panel = new JPanel();
        tab4Panel = new JPanel();
        tab5Panel = new JPanel();
        mapPanel = new JPanel();
        startPanel = new JPanel();
        consolePanel = new JPanel();
        exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        fileMenu.add(exit);

        fc = new JFileChooser();


  /*
  try {
   tank = ImageIO.read(new URL("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTPzQ5sUKeSv8q6Bu8uditeu8yMCVboWzlge4DIjhxrKQFeZNU5"));
  } catch (IOException e) {    
   System.out.println("Unable to retrieve image");    
   e.printStackTrace();  
  }
  frame.setIconImage(tank);
  */
  

  att1Checkbox = new Checkbox();
  att1Checkbox.setMaximumSize(new Dimension(10,10));
  att2Checkbox = new Checkbox();
  att2Checkbox.setMaximumSize(new Dimension(10,10));
  att3Checkbox = new Checkbox();
  att3Checkbox.setMaximumSize(new Dimension(10,10));
  att4Checkbox = new Checkbox();
  att4Checkbox.setMaximumSize(new Dimension(10,10));
  att5Checkbox = new Checkbox();
  att5Checkbox.setMaximumSize(new Dimension(10,10));
  att6Checkbox = new Checkbox();
  att6Checkbox.setMaximumSize(new Dimension(10,10));
  att7Checkbox = new Checkbox();
  att7Checkbox.setMaximumSize(new Dimension(10,10));
  att8Checkbox = new Checkbox();
  att8Checkbox.setMaximumSize(new Dimension(10,10));
  att9Checkbox = new Checkbox();
  att9Checkbox.setMaximumSize(new Dimension(10,10));
  att10Checkbox = new Checkbox();
  att10Checkbox.setMaximumSize(new Dimension(10,10));
 
  attributeLabel1 = new JLabel("Weapon Damage");
  attributeLabel2 = new JLabel("Weapon Accuracy");
  attributeLabel3 = new JLabel("Weapon Range");
  attributeLabel4 = new JLabel("Ammo Capacity");
  attributeLabel5 = new JLabel("Protection");
  attributeLabel6 = new JLabel("Sensor Range");
  attributeLabel7 = new JLabel("Sensor Interval");
  attributeLabel8 = new JLabel("Soldier Accuracy");
  attributeLabel9 = new JLabel("Soldier Morale");
  attributeLabel10 = new JLabel("Soldier Movespeed");


  attribute1TextFieldMin = new JTextField("1");
  attribute2TextFieldMin = new JTextField("90");
  attribute3TextFieldMin = new JTextField("10");
  attribute4TextFieldMin = new JTextField("12");
  attribute5TextFieldMin = new JTextField("5");
  attribute6TextFieldMin = new JTextField("5");
  attribute7TextFieldMin = new JTextField("1");
  attribute8TextFieldMin = new JTextField("1");
  attribute9TextFieldMin = new JTextField("70");
  attribute10TextFieldMin = new JTextField("1");
  
  attribute1TextFieldBase = new JTextField("75");
  attribute2TextFieldBase = new JTextField("95");
  attribute3TextFieldBase = new JTextField("60");
  attribute4TextFieldBase = new JTextField("30");
  attribute5TextFieldBase = new JTextField("10");
  attribute6TextFieldBase = new JTextField("100");
  attribute7TextFieldBase = new JTextField("10");
  attribute8TextFieldBase = new JTextField("25");
  attribute9TextFieldBase = new JTextField("100");
  attribute10TextFieldBase = new JTextField("5");
  
  attribute1TextFieldMax = new JTextField("115");
  attribute2TextFieldMax = new JTextField("100");
  attribute3TextFieldMax = new JTextField("100");
  attribute4TextFieldMax = new JTextField("100");
  attribute5TextFieldMax = new JTextField("15");
  attribute6TextFieldMax = new JTextField("300");
  attribute7TextFieldMax = new JTextField("20");
  attribute8TextFieldMax = new JTextField("100");
  attribute9TextFieldMax = new JTextField("100");
  attribute10TextFieldMax = new JTextField("10");

  startPanel.setLayout(null);
  
  startButton = new JButton("Start");
  startButton.addActionListener(this);
  startButton.setActionCommand("Start");
  startButton.setBounds(10, 10, 120, 30);
  startPanel.add(startButton);
  stopButton = new JButton("Stop");
  stopButton.addActionListener(this);
  stopButton.setActionCommand("Stop");
  stopButton.setBounds(140, 10, 120, 30);
  startPanel.add(stopButton);
  resetButton = new JButton("Reset");
  resetButton.addActionListener(this);
  resetButton.setActionCommand("Reset");
  resetButton.setBounds(270, 10, 120, 30);
  startPanel.add(resetButton);
  sims = new JLabel("Battles simulated : 0");
  sims.setBounds(10, 50, 380, 30);
  startPanel.add(sims);
  generations = new JLabel("Generations completed : 0");
  generations.setBounds(10, 80, 380, 30);
  startPanel.add(generations);
  JButton status = new JButton("Status");
  status.addActionListener(this);
  status.setActionCommand("Status");
  status.setBounds(10, 120, 120, 30);
  startPanel.add(status);
  consoleText = new JTextArea("Console", 10, 30);
  consoleText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
  consoleText.setEnabled(false);
  consoleText.setDisabledTextColor(Color.BLACK);
  consoleScrollPane = new JScrollPane(consoleText);
  consoleScrollPane.setEnabled(false);
  consoleScrollPane.setViewportView(consoleText);
  consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
  consoleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


  /*
  menuBar1.add(fileMenu);  //
  menuBar1.add(editMenu);
  menuBar1.add(viewMenu);
  menuBar1.add(helpMenu);
  frame.setJMenuBar(menuBar1);
  */
  //startPanel.add(startButton);
  consolePanel.add(consoleScrollPane);
  resetMap();

  
  tab1Panel.setLayout(null);
  soldierLabels = new JLabel[9];
  soldierTexts = new JTextField[9][3];
  soldierCheckboxes = new Checkbox[9];
  String[] labels = {"Weapon Damage:", "Weapon Accuracy:", "Weapon Range:", "Protection:", "Sensor Range:", "Sensor Interval:", "Soldier Accuracy:", "Soldier Morale:", "Soldier Movespeed:"};
  String[] minValues = {"1", "90", "10", "5", "5", "1", "1", "70", "1"};
  String[] baseValues = {"75", "95", "60", "10", "100", "10", "25", "100", "5"};
  String[] maxValues = {"115", "100", "100", "15", "300", "20", "100", "100", "10"};
  int column1Size = 25;
  int column2Size = 160;
  int column3Size = 50;
  for(int a=0;a<9;a++){
    Checkbox box = new Checkbox();
    box.setBounds(10, 10 + 30*a, column1Size, 25);
    soldierCheckboxes[a] = box;
    tab1Panel.add(box);
    JLabel label = new JLabel(labels[a]);
    label.setBounds(10 + column1Size, 10 + 30*a, column2Size, 25);
    soldierLabels[a] = label;
    tab1Panel.add(label);
    JTextField field = new JTextField(minValues[a]);
    field.setBounds(10 + column1Size + column2Size, 10 + 30*a, column3Size, 25);
    soldierTexts[a][0] = field;
    tab1Panel.add(field);
    field = new JTextField(baseValues[a]);
    field.setBounds(10 + column1Size + column2Size + column3Size, 10 + 30*a, column3Size, 25);
    soldierTexts[a][1] = field;
    tab1Panel.add(field);
    field = new JTextField(maxValues[a]);
    field.setBounds(10 + column1Size + column2Size + column3Size + column3Size, 10 + 30*a, column3Size, 25);
    soldierTexts[a][2] = field;
    tab1Panel.add(field);
    soldierBaseValues[a][0] = minValues[a];
    soldierBaseValues[a][1] = baseValues[a];
    soldierBaseValues[a][2] = maxValues[a];
  }

  /*
  GroupLayout tab1PanelLayout = new GroupLayout(tab1Panel);
  tab1Panel.setLayout(tab1PanelLayout);
  tab1PanelLayout.setHorizontalGroup(
    tab1PanelLayout.createSequentialGroup()
    .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(att1Checkbox)
      .addComponent(att2Checkbox)
      .addComponent(att3Checkbox)
      .addComponent(att4Checkbox)
      .addComponent(att5Checkbox)
      .addComponent(att6Checkbox)
      .addComponent(att7Checkbox)
      .addComponent(att8Checkbox)
      .addComponent(att9Checkbox)
      .addComponent(att10Checkbox))
    .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(attributeLabel1)
      .addComponent(attributeLabel2)
      .addComponent(attributeLabel3)
      .addComponent(attributeLabel4)
      .addComponent(attributeLabel5)
      .addComponent(attributeLabel6)
      .addComponent(attributeLabel7)
      .addComponent(attributeLabel8)
      .addComponent(attributeLabel9)
      .addComponent(attributeLabel10))
      .addGap(30)
      .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(attribute1TextFieldMin, 50, 50, 50)
        .addComponent(attribute2TextFieldMin, 50, 50, 50)
        .addComponent(attribute3TextFieldMin, 50, 50, 50)
        .addComponent(attribute4TextFieldMin, 50, 50, 50)
        .addComponent(attribute5TextFieldMin, 50, 50, 50)
        .addComponent(attribute6TextFieldMin, 50, 50, 50)
        .addComponent(attribute7TextFieldMin, 50, 50, 50)
        .addComponent(attribute8TextFieldMin, 50, 50, 50)
        .addComponent(attribute9TextFieldMin, 50, 50, 50)
        .addComponent(attribute10TextFieldMin, 50, 50, 50))
        .addGap(5)
        .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(attribute1TextFieldBase, 50, 50, 50)
          .addComponent(attribute2TextFieldBase, 50, 50, 50)
          .addComponent(attribute3TextFieldBase, 50, 50, 50)
          .addComponent(attribute4TextFieldBase, 50, 50, 50)
          .addComponent(attribute5TextFieldBase, 50, 50, 50)
          .addComponent(attribute6TextFieldBase, 50, 50, 50)
          .addComponent(attribute7TextFieldBase, 50, 50, 50)
          .addComponent(attribute8TextFieldBase, 50, 50, 50)
          .addComponent(attribute9TextFieldBase, 50, 50, 50)
          .addComponent(attribute10TextFieldBase, 50, 50, 50))
          .addGap(5)
          .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(attribute1TextFieldMax, 50, 50, 50)
            .addComponent(attribute2TextFieldMax, 50, 50, 50)
            .addComponent(attribute3TextFieldMax, 50, 50, 50)
            .addComponent(attribute4TextFieldMax, 50, 50, 50)
            .addComponent(attribute5TextFieldMax, 50, 50, 50)
            .addComponent(attribute6TextFieldMax, 50, 50, 50)
            .addComponent(attribute7TextFieldMax, 50, 50, 50)
            .addComponent(attribute8TextFieldMax, 50, 50, 50)
            .addComponent(attribute9TextFieldMax, 50, 50, 50)
            .addComponent(attribute10TextFieldMax, 50, 50, 50)));
          





  tab1PanelLayout.setVerticalGroup(
    tab1PanelLayout.createSequentialGroup()
    .addGap(10)
    .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(att1Checkbox)
      .addComponent(attributeLabel1)
      .addComponent(attribute1TextFieldMin, 20, 20, 20)
      .addComponent(attribute1TextFieldBase, 20, 20, 20)
      .addComponent(attribute1TextFieldMax, 20, 20, 20))
      .addGap(10)
      .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(att2Checkbox)
        .addComponent(attributeLabel2)
        .addComponent(attribute2TextFieldMin, 20, 20, 20)
        .addComponent(attribute2TextFieldBase, 20, 20, 20)
        .addComponent(attribute2TextFieldMax, 20, 20, 20))
        .addGap(10)
        .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
          .addComponent(att3Checkbox)
          .addComponent(attributeLabel3)
          .addComponent(attribute3TextFieldMin, 20, 20, 20)
          .addComponent(attribute3TextFieldBase, 20, 20, 20)
          .addComponent(attribute3TextFieldMax, 20, 20, 20))
          .addGap(10)
          .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(att4Checkbox)
            .addComponent(attributeLabel4)
            .addComponent(attribute4TextFieldMin, 20, 20, 20)
            .addComponent(attribute4TextFieldBase, 20, 20, 20)
            .addComponent(attribute4TextFieldMax, 20, 20, 20))
            .addGap(10)
            .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addComponent(att5Checkbox)
              .addComponent(attributeLabel5)
              .addComponent(attribute5TextFieldMin, 20, 20, 20)
              .addComponent(attribute5TextFieldBase, 20, 20, 20)
              .addComponent(attribute5TextFieldMax, 20, 20, 20)) 
              .addGap(10)
              .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(att6Checkbox)
                .addComponent(attributeLabel6)
                .addComponent(attribute6TextFieldMin, 20, 20, 20)
                .addComponent(attribute6TextFieldBase, 20, 20, 20)
                .addComponent(attribute6TextFieldMax, 20, 20, 20))
                .addGap(10)
                .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(att7Checkbox)
                  .addComponent(attributeLabel7)
                  .addComponent(attribute7TextFieldMin, 20, 20, 20)
                  .addComponent(attribute7TextFieldBase, 20, 20, 20)
                  .addComponent(attribute7TextFieldMax, 20, 20, 20))
                  .addGap(10)
                  .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(att8Checkbox)
                    .addComponent(attributeLabel8)
                    .addComponent(attribute8TextFieldMin, 20, 20, 20)
                    .addComponent(attribute8TextFieldBase, 20, 20, 20)
                    .addComponent(attribute8TextFieldMax, 20, 20, 20))  
                    .addGap(10)
                    .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(att9Checkbox)
                      .addComponent(attributeLabel9)
                      .addComponent(attribute9TextFieldMin, 20, 20, 20)
                      .addComponent(attribute9TextFieldBase, 20, 20, 20)
                      .addComponent(attribute9TextFieldMax, 20, 20, 20)) 
                      .addGap(10)
                      .addGroup(tab1PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(att10Checkbox)
                        .addComponent(attributeLabel10)
                        .addComponent(attribute10TextFieldMin, 20, 20, 20)
                        .addComponent(attribute10TextFieldBase, 20, 20, 20)
                        .addComponent(attribute10TextFieldMax, 20, 20, 20)));
                        */

        tabbedPane.addTab("Soldier", tab1Panel);
        tabbedPane.addTab("Tank", tab2Panel);
        tabbedPane.addTab("UAV", tab3Panel);
        tabbedPane.addTab("Evolution Settings", tab4Panel);
        tabbedPane.addTab("Load / Save", tab5Panel);


        mapPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        startPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        consolePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tabbedPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));


  /*
  GroupLayout startPanelLayout = new GroupLayout(startPanel);
  startPanel.setLayout(startPanelLayout);
  startPanelLayout.setHorizontalGroup(
    startPanelLayout.createSequentialGroup()
    .addGap(60)
    .addComponent(startButton)
    .addGap(30)
    .addComponent(stopButton)
    .addGap(30)
    .addComponent(resetButton));


  startPanelLayout.setVerticalGroup(
    startPanelLayout.createSequentialGroup()
    .addGap(80)
    .addGroup(startPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(startButton)
      .addComponent(stopButton)
      .addComponent(resetButton)));
  */


  GroupLayout containerLayout = new GroupLayout(container);
  container.setLayout(containerLayout);
  containerLayout.setHorizontalGroup(
    containerLayout.createSequentialGroup()
    .addGroup(containerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(tabbedPane, 400, 400, 400)
      .addComponent(startPanel, 400, 400, 400))
      .addGroup(containerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(mapPanel, 400, 400, 400)
        .addComponent(consolePanel, 400, 400, 400)));


  containerLayout.setVerticalGroup(
    containerLayout.createSequentialGroup()
    .addGroup(containerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(tabbedPane, 400, 400, 400)
      .addComponent(mapPanel, 400, 400, 400))
      .addGroup(containerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(startPanel, 200, 200, 200)
        .addComponent(consolePanel, 200, 200, 200)));

  
  tab2Panel.setLayout(null);
  tankLabels = new JLabel[10];
  tankTexts = new JTextField[10][3];
  tankCheckboxes = new Checkbox[10];
  labels = new String[]{"Weapon Damage:", "Weapon Range:", "Attack Interval (s):", "Splash Radius (m):", "Protection (vs Explosive):", "Velocity (km/h):", "Fuel per Kilometer (l/km):", "Fuel Capacity (l):", "Sensor Range:", "Sensor Interval:"};
  minValues = new String[]{"150", "100", "1", "2", "50", "25", "1", "1000", "50", "1"};
  baseValues = new String[]{"250", "200", "5", "5", "150", "40", "4", "1700", "100", "5"};
  maxValues = new String[]{"350", "300", "10", "10", "250", "50", "10", "2500", "200", "10"};
  column1Size = 25;
  column2Size = 160;
  column3Size = 50;
  for(int a=0;a<10;a++){
    Checkbox box = new Checkbox();
    box.setBounds(10, 10 + 30*a, column1Size, 25);
    tankCheckboxes[a] = box;
    tab2Panel.add(box);
    JLabel label = new JLabel(labels[a]);
    label.setBounds(10 + column1Size, 10 + 30*a, column2Size, 25);
    tankLabels[a] = label;
    tab2Panel.add(label);
    JTextField field = new JTextField(minValues[a]);
    field.setBounds(10 + column1Size + column2Size, 10 + 30*a, column3Size, 25);
    tankTexts[a][0] = field;
    tab2Panel.add(field);
    field = new JTextField(baseValues[a]);
    field.setBounds(10 + column1Size + column2Size + column3Size, 10 + 30*a, column3Size, 25);
    tankTexts[a][1] = field;
    tab2Panel.add(field);
    field = new JTextField(maxValues[a]);
    field.setBounds(10 + column1Size + column2Size + column3Size + column3Size, 10 + 30*a, column3Size, 25);
    tankTexts[a][2] = field;
    tab2Panel.add(field);
  }
  tab4Panel.setLayout(null);
  evolutionLabels = new JLabel[6];
  evolutionTexts = new JTextField[4];
  evolutionCheckboxes = new Checkbox[2];
  labels = new String[]{"Certainty:", "Radicality:", "Population Size:", "Amount of Generations:", "Display Map during Simulations:", "No Generation Limit:"};
  String[] values = {"10", "5", "100", "100"};
  column1Size = 190;
  for(int a=0;a<6;a++){
    JLabel label = new JLabel(labels[a]);
    label.setBounds(10, 10 + 30*a, column1Size, 25);
    tab4Panel.add(label);
    if(a<4){
      JTextField area = new JTextField(values[a]);
      area.setBounds(10 + column1Size, 10 + 30*a, 50, 25);
      evolutionTexts[a] = area;
      tab4Panel.add(area);
    }
    else{
      Checkbox check = new Checkbox();
      check.setBounds(10 + column1Size, 10 + 30*a, 25, 25);
      evolutionCheckboxes[a-4] = check;
      tab4Panel.add(check);
    }
  }
        tab5Panel.setLayout(null);
        JButton load = new JButton("Load Settings");
        load.addActionListener(this);
        load.setActionCommand("Load");
        load.setBounds (50, 50, 100, 50);
        tab5Panel.add(load);

        JButton save = new JButton("Save Settings");
        save.addActionListener(this);
        save.setActionCommand("Save");
        save.setBounds(50, 110, 100, 50);
        tab5Panel.add(save);

        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent E) {
        if(E.getActionCommand() == "Exit") {
            System.exit(0);
        }
        if(E.getActionCommand() == "Start"){
            //if(main instanceof MainThreaded){
            //MainThreaded mt = (MainThreaded)main;
            if(main.startNew){
                //main.setThreads(1);
                //main.setEvolutionValues(getEvolutionValues());
                main.startEvolutionThread(getSettings());
            }
            else{
                main.unpauseThreads();
            }
           // }
            /*
            else{
              if(map.isPaused()){
                map.unpause();
              }
              else{
                //resetEvolution();
                main.setEvolutionValues(getEvolutionValues());
                main.startEvolutionThread(getSettings());
                map.unpause();
              }
            }
            */
        }
        else if(E.getActionCommand() == "Stop"){
            //if(main instanceof MainThreaded){
            //MainThreaded mt = (MainThreaded)main;
            main.pauseThreads();
            /*
             }
             else{
             map.pause();
             }
             */
        }
        else if(E.getActionCommand() == "Reset"){
            //if(main instanceof MainThreaded){
            // MainThreaded mt = (MainThreaded)main;
            main.resetThreads();
            /*
             }
             else{
             main.forceBreakEvolution();
             map.reset();
             map.unpause();
             }
            */
        }
        else if(E.getActionCommand() == "Status"){
            map.getStatus();
        }
        else if(E.getActionCommand() == "Load"){
            int val = fc.showOpenDialog(null);

            if(val == JFileChooser.APPROVE_OPTION){
                File loadFrom = fc.getSelectedFile();
                ArrayList<Settings> settings = SettingsIO.loadSettings(loadFrom, main);
                Settings base = settings.remove(0);
                ArrayList<Settings> population = new ArrayList<Settings>();
                ArrayList<Settings> reported = new ArrayList<Settings>();
                while(settings.size() > 0){
                    Settings find = settings.remove(0);
                    if(find.currentFitness == 0){
                        population.add(find);
                    }
                    else{
                        reported.add(find);
                    }
                }
                if(population.size() > 0 || + reported.size() > 0){
                    main.loadSettings(population, reported);
                }
                else{
                    main.resetThreads();
                }
                setBaseSettings(base);
                main.battles = 0;
                main.generations = 0;
            }
        }
        else if(E.getActionCommand() == "Save"){
            int val = fc.showSaveDialog(null);

            if(val == JFileChooser.APPROVE_OPTION){
                File saveTo = fc.getSelectedFile();
                ArrayList<Settings> settings = new ArrayList<Settings>();
                settings.add(getSettings());
                SettingsIO.saveSettings(settings, saveTo);
            }
        }
    }

    public void resetEvolution(){
        main.forceBreakEvolution();
        map.reset();
        map.unpause();
    }
 
 public Settings getSettings(){
   Settings s = new Settings(main);
   /*
   Attribute a = new Attribute("Weapon Damage",
                               Integer.parseInt(attribute1TextFieldBase.getText()),
                               Integer.parseInt(attribute1TextFieldMin.getText()),
                               Integer.parseInt(attribute1TextFieldMax.getText()),
                               100,
                               s);
   s.attributes.add(a);
   if(att1Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Weapon Accuracy",
                               Integer.parseInt(attribute2TextFieldBase.getText()),
                               Integer.parseInt(attribute2TextFieldMin.getText()),
                               Integer.parseInt(attribute2TextFieldMax.getText()),
                               100,
                               s);
   s.attributes.add(a);
   if(att2Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Weapon Range",
                               Integer.parseInt(attribute3TextFieldBase.getText()),
                               Integer.parseInt(attribute3TextFieldMin.getText()),
                               Integer.parseInt(attribute3TextFieldMax.getText()),
                               80,
                               s);
   s.attributes.add(a);
   if(att3Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Ammo Capacity",
                               Integer.parseInt(attribute4TextFieldBase.getText()),
                               Integer.parseInt(attribute4TextFieldMin.getText()),
                               Integer.parseInt(attribute4TextFieldMax.getText()),
                               100,
                               s);
   s.attributes.add(a);
   if(att4Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Protection",
                               Integer.parseInt(attribute5TextFieldBase.getText()),
                               Integer.parseInt(attribute5TextFieldMin.getText()),
                               Integer.parseInt(attribute5TextFieldMax.getText()),
                               225,
                               s);
   s.attributes.add(a);
   if(att5Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Sensor Range",
                               Integer.parseInt(attribute6TextFieldBase.getText()),
                               Integer.parseInt(attribute6TextFieldMin.getText()),
                               Integer.parseInt(attribute6TextFieldMax.getText()),
                               75,
                               s);
   s.attributes.add(a);
   if(att6Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Sensor Interval",
                               Integer.parseInt(attribute7TextFieldBase.getText()),
                               Integer.parseInt(attribute7TextFieldMin.getText()),
                               Integer.parseInt(attribute7TextFieldMax.getText()),
                               -75,
                               s);
   s.attributes.add(a);
   if(att7Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Soldier Accuracy",
                               Integer.parseInt(attribute8TextFieldBase.getText()),
                               Integer.parseInt(attribute8TextFieldMin.getText()),
                               Integer.parseInt(attribute8TextFieldMax.getText()),
                               125,
                               s);
   s.attributes.add(a);
   if(att8Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Soldier Morale",
                               Integer.parseInt(attribute9TextFieldBase.getText()),
                               Integer.parseInt(attribute9TextFieldMin.getText()),
                               Integer.parseInt(attribute9TextFieldMax.getText()),
                               25,
                               s);
   s.attributes.add(a);
   if(att9Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   a = new Attribute("Soldier Movespeed",
                               Integer.parseInt(attribute10TextFieldBase.getText()),
                               Integer.parseInt(attribute10TextFieldMin.getText()),
                               Integer.parseInt(attribute10TextFieldMax.getText()),
                               75,
                               s);
   s.attributes.add(a);
   if(att10Checkbox.getState()){
     s.changeableAttributes.add(a);
   }
   */
   for(int a=0;a<9;a++){
     int[] values = new int[3];
     for(int b=0;b<3;b++){
       boolean failed = false;
       try{
         values[b] = Integer.parseInt(soldierTexts[a][b].getText());
       }
       catch(NumberFormatException e){
         failed = true;
       }
       if(failed || values[b]<=0){
         values[b] = Integer.parseInt(soldierBaseValues[a][b]);
         soldierTexts[a][b].setText(soldierBaseValues[a][b]);
       }
     }
     if(values[0] > values[2]){
       values[0] = values[2];
     }
     if(values[1] < values[0]){
       values[1] = values[0];
     }
     else if(values[1] > values[2]){
       values[1] = values[2];
     }
     //Name, base, min, max, cost, Settings
     Attribute att = new Attribute(soldierAttributeNames[a],
                       values[1],
                       values[0],
                       values[2],
                       soldierCosts[a],
                       s);
     s.attributes.add(att);
     if(soldierCheckboxes[a].getState()){
         att.allowsChange = true;
         s.changeableAttributes.add(att);
     }
   }
                       
   s.maxCost = s.getTotalCost();
   return s;
 }
    public void setBaseSettings(Settings s){
        for(int a=0;a<s.attributes.size();a++){
            Attribute att = s.attributes.get(a);
            int[] values = new int[3];
            values[0] = att.minimum;
            values[1] = att.base;
            values[2] = att.maximum;
            for(int b=0;b<soldierAttributeNames.length;b++){
                if(soldierAttributeNames[b].equals(att.type)){
                    for(int c=0;c<3;c++){
                        soldierTexts[b][c].setText(""+values[c]);
                        soldierCheckboxes[b].setState(att.allowsChange);
                    }
                    break;
                }
            }
        }
    }
 
 public int[] getEvolutionValues(){
   int[] retur = new int[6];
   int[] baseValues = {3, 5, 25, 25};
   for(int a=0;a<4;a++){
     try{
       retur[a] = Integer.parseInt(evolutionTexts[a].getText());
     }
     catch(NumberFormatException e){
       retur[a] = baseValues[a];
       evolutionTexts[a].setText(""+baseValues[a]);
     }
   }
   for(int a=0;a<2;a++){
     if(evolutionCheckboxes[a].getState()){
       retur[a+4] = 1;
     }
     else{
       retur[a+4] = 0;
     }
   }
   
   return retur;
 }
 
 public void setSimulationsDone(int a){
   sims.setText("Battles simulated : " + a);
 }
 
 public void setGenerationsCompleted(int a){
   generations.setText("Generations completed : " + a);
 }
 
 public void setMap(TestOfMap map){
   mapPanel.remove(this.map);
   this.map = map;
   mapPanel.add(map);
 }

}



import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import java.util.Random;
import java.awt.geom.*;
import java.util.Vector;

/*
 * 
 */

 /*
 * @author patel
 */
public class MoonLanding extends JFrame {

    /**
     * GAME CONSTANTS
     */
    private final double X_DELTA = 1.2; //change in x for speed
    private final double Y_DELTA = 2.4; //change in y for speed
    private final double GRAVITY = 9.8 / 6.0; //gravity of the moon
    private final double DRAG = 0.1; //the drag caused during horizontal motion
    private final double SAFE_LANDING_SPEED = 3.0; //MAXIMUM speed needed to land at
    private static final double MAX_FUEL = 100;
    private final double H_FUEL = 1.0;
    private final double V_FUEL = 2.0;

    private JPanel framePanel = new JPanel();
    private ViewerPanel viewerPanel = new ViewerPanel();
    private JPanel controlPanel = new JPanel();
    private JLabel timeLabel = new JLabel();
    private JTextField timeTextField = new JTextField();
    private JLabel positionLabel = new JLabel();
    private JLabel speedLabel = new JLabel();
    private JLabel altitudeLabel = new JLabel();
    private JTextField altitudePositionTextField = new JTextField();
    private JTextField altitudeSpeedTextField = new JTextField();
    private JLabel lateralLabel = new JLabel();
    private JTextField lateralPositionTextField = new JTextField();
    private JTextField lateralSpeedTextField = new JTextField();
    private JPanel fuelPanel = new JPanel();
    private TrajecPanel trajectoryPanel = new TrajecPanel();
    private GuidePanel guidePanel = new GuidePanel();
    private JButton leftThrustButton = new JButton(new ImageIcon("leftarrow.gif"));
    private JButton downThrustButton = new JButton(new ImageIcon("downarrow.gif"));
    private JButton rightThrustButton = new JButton(new ImageIcon("rightarrow.gif"));
    private JLabel messageLabel = new JLabel();
    private Font ArialBOLD = new Font("Arial", Font.BOLD, 12);
    private Font ArialPlain = new Font("Arial", Font.PLAIN, 12);
    private JLabel fuelLabel = new JLabel();
    private JProgressBar fuelProgressBar = new JProgressBar();
    private JButton startPauseButton = new JButton();
    private JButton exitStopButton = new JButton();
    private JButton completeButton = new JButton();
    private JPanel optionsPanel = new JPanel();
    private JPanel pilotPanel = new JPanel();
    private JPanel unitsPanel = new JPanel();
    private ButtonGroup pilotButtonGroup = new ButtonGroup();
    private JRadioButton beginnerRadioButton = new JRadioButton();
    private JRadioButton noviceRadioButton = new JRadioButton();
    private JRadioButton juniorRadioButton = new JRadioButton();
    private JRadioButton seniorRadioButton = new JRadioButton();
    private JRadioButton advancedRadioButton = new JRadioButton();
    private JCheckBox autoPilotCheckBox = new JCheckBox();
    private Color lightBlue = new Color(208, 208, 255);
    private ButtonGroup unitsButtonGroup = new ButtonGroup();
    private JRadioButton metricRadioButton = new JRadioButton();
    private JRadioButton usRadioButton = new JRadioButton();

    private int pilotLevel = 1; //the difficulty the user picks
    double multiplier = 1.0; //to convert from m to ft, and vise versa
    Timer landingTimer; //update the game
    double time;
    private static double landerX, landerY; //position of the lander
    double landerXSpeed, landerYSpeed; //speed of the lander

    private static Image landscape;
    private static Image lander;
    private static Image pad;
    private static Image hThrust;
    private static Image vThrust;

    private static int landscapeWidth, landscapeHeight;
    private static int landerWidth, landerHeight;
    private static int padWidth, padHeight;
    private static int hThrustWidth, hThrustHeight;
    private static int vThrustWidth, vThrustHeight;

    private static double padX, padY;
    private static double landscapeX, landscapeY;
    private double altitude, lateral; //altitude and lateral of lander
    private Random myRandom; //random position for the landing pad and lander

    private static double fuelRemaining;
    private static double landerX0, landerY0;

    private static Vector trajectoryPoints = new Vector(50, 10);

    private static double landerXView, landerYView;
    private static double centerX, centerY;
    private static boolean vThrustOn, lThrustOn, rThrustOn;
    
    private double altitude0, lateral0, miss;

    public MoonLanding() {
        setTitle("Moon Landing");
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc;

        framePanel.setPreferredSize(new Dimension(300, 400));
        framePanel.setBackground(Color.GRAY);
        framePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        getContentPane().add(framePanel, gbc);

        viewerPanel.setPreferredSize(new Dimension(280, 380));
        viewerPanel.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        framePanel.add(viewerPanel, gbc);

        UIManager.put("TitledBorder.font", new Font("Arial", Font.BOLD, 14));
        controlPanel.setPreferredSize(new Dimension(260, 360));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Landing Control-Beginner"));
        controlPanel.setBackground(lightBlue);
        controlPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        getContentPane().add(controlPanel, gbc);

        timeLabel.setPreferredSize(new Dimension(70, 25));
        timeLabel.setText("Time (s)");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(timeLabel, gbc);

        timeTextField.setPreferredSize(new Dimension(70, 25));
        timeTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        timeTextField.setText("0.0");
        timeTextField.setEditable(false);
        timeTextField.setBackground(Color.WHITE);
        timeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        timeTextField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        controlPanel.add(timeTextField, gbc);

        positionLabel.setPreferredSize(new Dimension(70, 25));
        positionLabel.setText("Position (m)");
        positionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        controlPanel.add(positionLabel, gbc);

        speedLabel.setPreferredSize(new Dimension(70, 25));
        speedLabel.setText("Speed (m/s)");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        controlPanel.add(speedLabel, gbc);

        altitudeLabel.setPreferredSize(new Dimension(70, 25));
        altitudeLabel.setText("Altitude");
        altitudeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(altitudeLabel, gbc);

        altitudePositionTextField.setPreferredSize(new Dimension(70, 25));
        altitudePositionTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        altitudePositionTextField.setText("0.0");
        altitudePositionTextField.setEditable(false);
        altitudePositionTextField.setBackground(Color.WHITE);
        altitudePositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        altitudePositionTextField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        controlPanel.add(altitudePositionTextField, gbc);

        altitudeSpeedTextField.setPreferredSize(new Dimension(70, 25));
        altitudeSpeedTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        altitudeSpeedTextField.setText("0.0");
        altitudeSpeedTextField.setEditable(false);
        altitudeSpeedTextField.setBackground(Color.WHITE);
        altitudeSpeedTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        altitudeSpeedTextField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        controlPanel.add(altitudeSpeedTextField, gbc);

        lateralLabel.setPreferredSize(new Dimension(70, 25));
        lateralLabel.setText("Lateral");
        lateralLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(lateralLabel, gbc);

        lateralPositionTextField.setPreferredSize(new Dimension(70, 25));
        lateralPositionTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lateralPositionTextField.setText("0.0");
        lateralPositionTextField.setEditable(false);
        lateralPositionTextField.setBackground(Color.WHITE);
        lateralPositionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        lateralPositionTextField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        controlPanel.add(lateralPositionTextField, gbc);

        lateralSpeedTextField.setPreferredSize(new Dimension(70, 25));
        lateralSpeedTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lateralSpeedTextField.setText("0.0");
        lateralSpeedTextField.setEditable(false);
        lateralSpeedTextField.setBackground(Color.WHITE);
        lateralSpeedTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        lateralSpeedTextField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        controlPanel.add(lateralSpeedTextField, gbc);

        fuelPanel.setPreferredSize(new Dimension(240, 40));
        fuelPanel.setBackground(lightBlue);
        fuelPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        controlPanel.add(fuelPanel, gbc);

        trajectoryPanel.setPreferredSize(new Dimension(240, 130));
        trajectoryPanel.setBackground(Color.BLACK);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        controlPanel.add(trajectoryPanel, gbc);

        guidePanel.setPreferredSize(new Dimension(240, 20));
        guidePanel.setBackground(Color.GRAY);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        controlPanel.add(guidePanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 25, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        controlPanel.add(leftThrustButton, gbc);
        leftThrustButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                leftThrustButtonActionPerformed(e);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 20, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        controlPanel.add(downThrustButton, gbc);
        downThrustButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downThrustButtonActionPerformed(e);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 20, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        controlPanel.add(rightThrustButton, gbc);
        rightThrustButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rightThrustButtonActionPerformed(e);
            }
        });
        messageLabel.setText("Auto-Pilot On");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(11, 0, 0, 0);
        controlPanel.add(messageLabel, gbc);

        fuelLabel.setText("Fuel");
        fuelLabel.setFont(ArialBOLD);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 4);
        fuelPanel.add(fuelLabel, gbc);

        fuelProgressBar.setPreferredSize(new Dimension(200, 25));
        fuelProgressBar.setMinimum(0);
        fuelProgressBar.setMaximum(100);
        fuelProgressBar.setValue(100);
        fuelProgressBar.setBackground(Color.WHITE);
        fuelProgressBar.setForeground(Color.BLUE);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        fuelPanel.add(fuelProgressBar, gbc);

        startPauseButton.setText("Start Game");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 0);
        getContentPane().add(startPauseButton, gbc);
        startPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startPauseButtonActionPerformed(e);
            }
        });
        exitStopButton.setText("Exit");
        exitStopButton.setPreferredSize(startPauseButton.getPreferredSize());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 0, 10);
        getContentPane().add(exitStopButton, gbc);
        exitStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitStopButtonActionPerformed(e);
            }
        });
        completeButton.setText("Landing Complete-Click to Continue");
        completeButton.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 0, 10);
        getContentPane().add(completeButton, gbc);
        completeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                completeButtonActionPerformed(e);
            }
        });

        controlPanel.setVisible(false);
        optionsPanel.setPreferredSize(new Dimension(260, 360));
        optionsPanel.setBackground(Color.BLUE);
        optionsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        getContentPane().add(optionsPanel, gbc);

        pilotPanel.setBorder(BorderFactory.createTitledBorder("Pilot Level"));
        pilotPanel.setPreferredSize(new Dimension(140, 190));
        pilotPanel.setBackground(lightBlue);
        pilotPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        optionsPanel.add(pilotPanel, gbc);

        unitsPanel.setBorder(BorderFactory.createTitledBorder("Units"));
        unitsPanel.setPreferredSize(new Dimension(140, 80));
        unitsPanel.setBackground(lightBlue);
        unitsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        optionsPanel.add(unitsPanel, gbc);

        UIManager.put("TitledBorder.font", new Font("Arial", Font.BOLD, 14));

        beginnerRadioButton.setText("Beginner");
        beginnerRadioButton.setBackground(lightBlue);
        beginnerRadioButton.setSelected(true);
        pilotButtonGroup.add(beginnerRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pilotPanel.add(beginnerRadioButton, gbc);
        beginnerRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pilotRadioButtonActionPerformed(e);
            }
        });

        noviceRadioButton.setText("Novice");
        noviceRadioButton.setBackground(lightBlue);
        pilotButtonGroup.add(noviceRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pilotPanel.add(noviceRadioButton, gbc);
        noviceRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pilotRadioButtonActionPerformed(e);
            }
        });
        juniorRadioButton.setText("Junior");
        juniorRadioButton.setBackground(lightBlue);
        pilotButtonGroup.add(juniorRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        pilotPanel.add(juniorRadioButton, gbc);
        juniorRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pilotRadioButtonActionPerformed(e);
            }
        });
        seniorRadioButton.setText("Senior");
        seniorRadioButton.setBackground(lightBlue);
        pilotButtonGroup.add(seniorRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pilotPanel.add(seniorRadioButton, gbc);
        seniorRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pilotRadioButtonActionPerformed(e);
            }
        });
        advancedRadioButton.setText("Advanced");
        advancedRadioButton.setBackground(lightBlue);
        pilotButtonGroup.add(advancedRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        pilotPanel.add(advancedRadioButton, gbc);
        advancedRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pilotRadioButtonActionPerformed(e);
            }
        });
        autoPilotCheckBox.setText("Auto Pilot On?");
        autoPilotCheckBox.setBackground(lightBlue);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        pilotPanel.add(autoPilotCheckBox, gbc);

        metricRadioButton.setText("Metric");
        metricRadioButton.setBackground(lightBlue);
        metricRadioButton.setSelected(true);
        unitsButtonGroup.add(metricRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        unitsPanel.add(metricRadioButton, gbc);
        metricRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unitsRadioButtonActionPerformed(e);
            }
        });
        usRadioButton.setText("US (English)");
        usRadioButton.setBackground(lightBlue);
        unitsButtonGroup.add(usRadioButton);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        unitsPanel.add(usRadioButton, gbc);
        usRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unitsRadioButtonActionPerformed(e);
            }
        });

        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int) (0.5 * (screenSize.width - getWidth())), (int) (0.5 * (screenSize.width - getHeight())), getWidth(), getHeight());

        landingTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                landingTimerActionPerformed(e);
            }
        });

        landscape = new ImageIcon("landscape.png").getImage();
        landscapeWidth = landscape.getWidth(this);
        landscapeHeight = landscape.getHeight(this);

        System.out.println(landscapeWidth);
        System.out.println(landscapeHeight);

        lander = new ImageIcon("lander.png").getImage();
        landerWidth = lander.getWidth(this);
        landerHeight = lander.getHeight(this);

        pad = new ImageIcon("pad.png").getImage();
        padWidth = pad.getWidth(this);
        padHeight = pad.getHeight(this);

        hThrust = new ImageIcon("hThrust.png").getImage();
        hThrustHeight = hThrust.getHeight(this);
        hThrustWidth = hThrust.getWidth(this);

        vThrust = new ImageIcon("vThrust.png").getImage();
        vThrustWidth = vThrust.getWidth(this);
        vThrustHeight = vThrust.getHeight(this);

        lander = Transparency.makeColorTransparent(lander, new Color(0, 0, 0));
        pad = Transparency.makeColorTransparent(pad, new Color(0, 0, 0));
        hThrust = Transparency.makeColorTransparent(hThrust, new Color(0, 0, 0));
        vThrust = Transparency.makeColorTransparent(vThrust, new Color(0, 0, 0));

        centerX = (viewerPanel.getWidth() - landerWidth) / 2;
        centerY = (viewerPanel.getHeight() - landerHeight) / 2;
        viewerPanel.setVisible(false);

    }//end constructor()

    private void exitForm(WindowEvent evt) {
        System.exit(0);
    }//end exitForm()

    private void leftThrustButtonActionPerformed(ActionEvent e) {

        myRandom = new Random();

        if (fuelRemaining > 0) {
            lThrustOn = true;
            if (pilotLevel != 5) {
                landerXSpeed += X_DELTA;
            } else {
                landerXSpeed += 2.0 * X_DELTA * myRandom.nextDouble();
            }//end if

            if (pilotLevel > 3) {
                fuelRemaining -= H_FUEL;
            }//end if
        }//end if(fuelRemaining)

    }//end leftThrustButtonActionPerformed()

    private void rightThrustButtonActionPerformed(ActionEvent e) {

        myRandom = new Random();

        if (fuelRemaining > 0) {
            rThrustOn = true;
            if (pilotLevel != 5) {
                landerXSpeed -= X_DELTA;
            } else {
                landerXSpeed -= 2.0 * X_DELTA * myRandom.nextDouble();
            }//end if

            if (pilotLevel > 3) {
                fuelRemaining -= H_FUEL;
            }//end if
        }//end if (fuelReamaining)
    }//end rightThrustButtonActionPerformed()

    private void downThrustButtonActionPerformed(ActionEvent e) {

        myRandom = new Random();

        if (fuelRemaining > 0) {
            vThrustOn = true;
            if (pilotLevel != 5) {
                landerYSpeed -= Y_DELTA;
            } else {
                landerYSpeed -= 2.0 * Y_DELTA * myRandom.nextDouble();
            }//end if

            if (pilotLevel > 3) {
                fuelRemaining -= V_FUEL;
            }//end if (pilotLevel)

        }//end (fuelRemaining)

    }//end downThrustButtonActionPerformed()

    private void turnButtonsOn() {

        if (!autoPilotCheckBox.isSelected()) {

            switch (pilotLevel) {

                case 1:
                    controlPanel.setBorder(BorderFactory.createTitledBorder("Landing Control-Beginner"));
                    leftThrustButton.setVisible(false);
                    rightThrustButton.setVisible(false);
                    downThrustButton.setVisible(true);
                    fuelLabel.setVisible(false);
                    fuelProgressBar.setVisible(false);
                    break;
                case 2:
                    controlPanel.setBorder(BorderFactory.createTitledBorder("Landing Control-Novice"));
                    downThrustButton.setVisible(false);
                    leftThrustButton.setVisible(true);
                    rightThrustButton.setVisible(true);
                    fuelLabel.setVisible(false);
                    fuelProgressBar.setVisible(false);
                    break;
                case 3:
                    controlPanel.setBorder(BorderFactory.createTitledBorder("Landing Control-Junior"));
                    downThrustButton.setVisible(true);
                    leftThrustButton.setVisible(true);
                    rightThrustButton.setVisible(true);
                    fuelLabel.setVisible(false);
                    fuelProgressBar.setVisible(false);
                    break;
                case 4:
                    controlPanel.setBorder(BorderFactory.createTitledBorder("Landing Control-Senior"));
                    downThrustButton.setVisible(true);
                    leftThrustButton.setVisible(true);
                    rightThrustButton.setVisible(true);
                    fuelLabel.setVisible(true);
                    fuelProgressBar.setVisible(true);
                    break;
                case 5:
                    controlPanel.setBorder(BorderFactory.createTitledBorder("Landing Control-Advanced"));
                    downThrustButton.setVisible(true);
                    leftThrustButton.setVisible(true);
                    rightThrustButton.setVisible(true);
                    fuelLabel.setVisible(true);
                    fuelProgressBar.setVisible(true);
                    break;
            }//end switch(pilotLevel)

        }//end if

    }//end turnButtonsOn()

    private void startPauseButtonActionPerformed(ActionEvent e) {

        if (startPauseButton.getText().equals("Start Game")) {

            startPauseButton.setText("Pause Game");
            exitStopButton.setText("Stop Game");
            optionsPanel.setVisible(false);
            controlPanel.setVisible(true);

            turnButtonsOn();

            //AUTO-PILOT ON
            if (autoPilotCheckBox.isSelected()) {
                leftThrustButton.setVisible(false);
                rightThrustButton.setVisible(false);
                downThrustButton.setVisible(false);
                messageLabel.setText("Auto-Pilot ON");
                messageLabel.setVisible(true);
            } else {
                messageLabel.setVisible(false);
            }//end if (auto-pilot)

            if (metricRadioButton.isSelected()) {
                positionLabel.setText("Position (m)");
                speedLabel.setText("Speed (m/s)");
            } else {
                positionLabel.setText("Position (ft)");
                speedLabel.setText("Speed (ft/s)");
            }//end if (metric or imperial)

            myRandom = new Random();

            time = 0.0;
            timeTextField.setText("0.0");
            viewerPanel.setVisible(true);
            landerX = (landscapeWidth - landerWidth) * myRandom.nextDouble();
            landerY = 0;
            landerXSpeed = 0;
            landerYSpeed = 0;

            if (pilotLevel != 1) {
                padX = (landscapeWidth - padWidth) * myRandom.nextDouble();
            } else {
                padX = landerX + ((landerWidth - padWidth)/2);
            }//end if

            System.out.println("Lander mid : " + (landerX + (landerWidth / 2)));
            System.out.println("Pad mid : " + (padX + (padWidth / 2)));
            
            //testing 
            //System.out.println("PadX : " + padX);
            //System.out.println("landerX: " + landerX);
            padY = (landscapeHeight - padHeight);
            fuelRemaining = MAX_FUEL;
            fuelProgressBar.setValue(100);
            trajectoryPoints.removeAllElements();
            updateStatus();
            updateTrajectory();
            updateViewer();
            viewerPanel.setVisible(true);
            landingTimer.start();

            //testing
            // System.out.println("landerX: " + landerX);
            //System.out.println("padX: " + padX);
        } else if (startPauseButton.getText().equals("Pause Game")) {

            startPauseButton.setText("Restart Game");
            exitStopButton.setEnabled(false);
            downThrustButton.setVisible(false);
            leftThrustButton.setVisible(false);
            rightThrustButton.setVisible(false);
            landingTimer.stop();

        } else if (startPauseButton.getText().equals("Restart Game")) {

            startPauseButton.setText("Pause Game");
            exitStopButton.setEnabled(true);
            turnButtonsOn();
            landingTimer.start();

        }//end if

    }//end startPauseButtonActionPerformed()

    private void exitStopButtonActionPerformed(ActionEvent e) {

        if (exitStopButton.getText().equals("Stop Game")) {

            exitStopButton.setText("Exit");
            startPauseButton.setText("Start Game");
            optionsPanel.setVisible(true);
            controlPanel.setVisible(false);
            viewerPanel.setVisible(false);
            landingTimer.stop();

        } else {
            System.exit(0);
        }//end if()

    }// end exitStopButtonActionPerformed()

    private void completeButtonActionPerformed(ActionEvent e) {

        completeButton.setVisible(false);
        startPauseButton.setVisible(true);
        exitStopButton.setVisible(true);
        exitStopButton.doClick();

    }// end completeButtonActionPerformed()

    private void pilotRadioButtonActionPerformed(ActionEvent e) {

        String t = e.getActionCommand();

        if (t.equals("Beginner")) {
            pilotLevel = 1;
        } else if (t.equals("Novice")) {
            pilotLevel = 2;
        } else if (t.equals("Junior")) {
            pilotLevel = 3;
        } else if (t.equals("Senior")) {
            pilotLevel = 4;
        } else {
            pilotLevel = 5;
        }//end if

    }//end pilotRadioButtonActionPerformed()

    private void unitsRadioButtonActionPerformed(ActionEvent e) {

        String s = e.getActionCommand();

        if (s.equals("Metric")) {
            multiplier = 1.0;
        } else {
            multiplier = 3.28084;
        }//end if

    }//end unitsRadioButtonActionPerformed()

    private void landingTimerActionPerformed(ActionEvent e) {

        time += (double) (landingTimer.getDelay()) / 1000;
        timeTextField.setText(new DecimalFormat("0.00").format(time));

        landerX += landerXSpeed * (double) (landingTimer.getDelay()) / 1000;
        landerY += landerYSpeed * (double) (landingTimer.getDelay()) / 1000;

        updateStatus();
        updateTrajectory();
        updateViewer();

        //check for landing
        if (altitude <= 0) {
            vThrustOn = false;
            rThrustOn = false;
            lThrustOn = false;
            updateViewer();
            landingTimer.stop();
            leftThrustButton.setVisible(false);
            rightThrustButton.setVisible(false);
            downThrustButton.setVisible(false);

            if (autoPilotCheckBox.isSelected()) {
                messageLabel.setText("Auto-Pilot ");
            } else {
                messageLabel.setText("You ");
                messageLabel.setVisible(true);
            }//end if

            //crash?
            if (landerYSpeed > SAFE_LANDING_SPEED) {
                messageLabel.setText(messageLabel.getText() + "Crashed!");
            } else {
                messageLabel.setText(messageLabel.getText() + "Landed Safely");
            }//end if

            //bring up complete button
            startPauseButton.setVisible(false);
            exitStopButton.setVisible(false);
            completeButton.setVisible(true);
            return;

        }//end if(altitutde < 0)

        // autopilot or Novice level - adjust vertical thrust
        if (autoPilotCheckBox.isSelected() || pilotLevel == 2) {

            if (altitude > 300) {
                if (landerYSpeed > 12) {
                    downThrustButton.doClick();
                }//end speed check
            } else if (altitude > 100) {
                if (landerYSpeed > 6) {
                    downThrustButton.doClick();
                }//end speed check
            } else {
                if (landerYSpeed > (2 + (0.04 * altitude))) {
                    downThrustButton.doClick();
                }//end speed check
            }//end if

        }//end if

        //autopilot - adjust horizonal thrust
        if (autoPilotCheckBox.isSelected()){
            //distance from the trajectory line
            miss = lateral - (altitude * (lateral0 / altitude0));
            if(miss > 2){
              rightThrustButton.doClick();
            } else if(miss < -2){
                leftThrustButton.doClick();
            }//end if
            
        }//end if
        
        landerX += landerXSpeed;
        landerY += landerYSpeed;

        //horizontal drag
        if (landerXSpeed > 0) {
            landerXSpeed -= DRAG;
        } else if (landerXSpeed < 0) {
            landerXSpeed += DRAG;
        } //end if

        //gravity
        landerYSpeed += GRAVITY * (double) (landingTimer.getDelay()) / 1000;

    }//end landingTimerActionPerformed()

    private void updateStatus() {

        altitude = (landscapeHeight - (padHeight / 2)) - (landerY + landerHeight);
        lateral = (landerX + landerWidth / 2) - (padX + padWidth / 2);

        //altitude check
        if (altitude > 0) {
            altitudePositionTextField.setText(new DecimalFormat("0").format(altitude * multiplier));
        } else {
            altitudePositionTextField.setText(new DecimalFormat("0").format(0));
        }//end if (altitude)

        //lateral check
        if (Math.abs(lateral) < 1) {
            lateralPositionTextField.setText("Above");
        } else {
            lateralPositionTextField.setText(new DecimalFormat("0").format(lateral * multiplier));
        }//end if(lateral)

        altitudeSpeedTextField.setText(new DecimalFormat("0").format(-landerYSpeed * multiplier));
        lateralSpeedTextField.setText(new DecimalFormat("0").format(landerXSpeed * multiplier));

        //update fuel guage
        int fuelPercent = (int) (100 * fuelRemaining / MAX_FUEL);
        fuelProgressBar.setValue(fuelPercent);
        if (fuelPercent <= 10) {
            Toolkit.getDefaultToolkit().beep();
        }//end if

        //update guide display
        guidePanel.repaint();

    }//end updateStatus()

    private void updateTrajectory() {

        if (time == 0.0) {

            landerX0 = landerX;
            landerY0 = landerY;
            altitude0 = altitude;
            lateral0 = lateral;

        }//end if

        trajectoryPoints.add(new Point2D.Double(landerX + landerWidth / 2, landerY
                + landerHeight));
        trajectoryPanel.repaint();

    }//end update Trajectory()

    private void updateViewer() {

        //adjust landscape background
        landscapeX = landerX - centerX;
        landerXView = centerX;

        if (landscapeX <= 0) {
            landscapeX = 0;
            landerXView = landerX;
        } //end if to the left

        if (landscapeX >= landscapeWidth - viewerPanel.getWidth()) {
            landscapeX = landscapeWidth - viewerPanel.getWidth();
            landerXView = landerX - landscapeX;
        }//end if to the right

        landscapeY = landerY - centerY;
        landerYView = centerY;

        if (landscapeY <= 0) {
            landscapeY = 0;
            landerYView = landerY;
        }//end if at the top

        if (landscapeY >= landscapeHeight - viewerPanel.getHeight()) {
            landscapeY = landscapeHeight - viewerPanel.getHeight();
            landerYView = landerY - landscapeY;
        }//end if at the bottom;

        //draw landscape
        viewerPanel.repaint();

    }//end updateViewer()

    public static void main(String[] args) {

        //create frame 
        new MoonLanding().show();

    }//end main()

    class TrajecPanel extends JPanel {

        public void paintComponent(Graphics g) {

            Graphics2D g2D = (Graphics2D) g;
            super.paintComponent(g2D);

            double trajectoryXScale = (double) (getWidth()) / MoonLanding.landscapeWidth;
            double trajectoryYScale = (double) (getHeight()) / MoonLanding.landscapeHeight;

            //System.out.println("lander X0 Y0 : " + landerX0 + ", " + landerY0);
            //System.out.println("pad XY : " + padX + ", " + padY);
            
            g2D.setStroke(new BasicStroke(2));
            g2D.setPaint(Color.RED);
            Line2D.Double trajectoryLine = new Line2D.Double(trajectoryXScale
                    * (MoonLanding.landerX0 + MoonLanding.landerWidth / 2), trajectoryYScale
                    * (MoonLanding.landerY0 + MoonLanding.landerHeight), trajectoryXScale
                    * (MoonLanding.padX + MoonLanding.padWidth / 2), trajectoryYScale
                    * (MoonLanding.padY - MoonLanding.padHeight / 2));

            g2D.draw(trajectoryLine);

            //debugging
            //System.out.println("x1 : " + trajectoryLine.getX1() + " y1 : " + trajectoryLine.getY1());
            //System.out.println("x2 : " + trajectoryLine.getX2() + " y2 : " + trajectoryLine.getY2());

            g2D.setStroke(new BasicStroke(1));
            for (int i = 0; i < MoonLanding.trajectoryPoints.size(); i++) {
                Point2D.Double thisPoint = (Point2D.Double) MoonLanding.trajectoryPoints.elementAt(i);
                Ellipse2D.Double trajectoryCircle = new Ellipse2D.Double(trajectoryXScale
                        * thisPoint.getX() - 3, trajectoryYScale * thisPoint.getY() - 3, 6, 6);
                g2D.setPaint(Color.GREEN);
                g2D.draw(trajectoryCircle);
            }//end forloop
            g2D.dispose();

        }//end paintComponent()

    }//end subClass()

    class GuidePanel extends JPanel {

        public void paintComponent(Graphics g) {

            Graphics2D g2D = (Graphics2D) g;
            super.paintComponent(g);

            // x coord for lander guide
            int x = (int) ((MoonLanding.landerX + MoonLanding.landerWidth / 2) * getWidth()
                    / MoonLanding.landscapeWidth - 5);
            Rectangle2D.Double guideRectangle = new Rectangle2D.Double(x, 1, 10, getHeight()
                    - 2);
            g2D.setPaint(Color.GREEN);
            g2D.fill(guideRectangle);

            //x coord for pad guide
            x = (int) ((MoonLanding.padX + MoonLanding.padWidth / 2) * getWidth()
                    / MoonLanding.landscapeWidth - 5);
            guideRectangle = new Rectangle2D.Double(x, 1,
                    10, getHeight() - 2);
            g2D.setPaint(Color.RED);
            g2D.fill(guideRectangle);
            g2D.dispose();

        }//end paintComponent()

    }//end subclass()

    class ViewerPanel extends JPanel {

        public void paintComponent(Graphics g) {

            Graphics2D g2D = (Graphics2D) g;
            super.paintComponent(g);
            g2D.drawImage(MoonLanding.landscape, 0, 0, getWidth() - 1, getHeight() - 1, (int) MoonLanding.landscapeX, (int) MoonLanding.landscapeY, (int) (MoonLanding.landscapeX + getWidth() - 1), (int) (MoonLanding.landscapeY
                    + getHeight() - 1), null);
            // add pad
            g2D.drawImage(MoonLanding.pad, (int) (MoonLanding.padX
                    - MoonLanding.landscapeX), (int) (MoonLanding.padY - MoonLanding.landscapeY), null);
            // add lander
            g2D.drawImage(MoonLanding.lander, (int) MoonLanding.landerXView, (int) MoonLanding.landerYView, null);
            //add thrusters if on
            if (MoonLanding.vThrustOn) {
                g2D.drawImage(MoonLanding.vThrust, (int) (MoonLanding.landerXView + 0.5
                        * MoonLanding.landerWidth - 0.5 * MoonLanding.vThrustWidth), (int) (MoonLanding.landerYView + MoonLanding.landerHeight - MoonLanding.vThrustHeight),
                        null);
                MoonLanding.vThrustOn = false;
            }//end if vertical thrust
            if (MoonLanding.lThrustOn) {
                g2D.drawImage(MoonLanding.hThrust, (int) (MoonLanding.landerXView - 5), (int) (MoonLanding.landerYView + 40), null);
                MoonLanding.lThrustOn = false;
            }//end if left thrust
            if (MoonLanding.rThrustOn) {
                g2D.drawImage(MoonLanding.hThrust, (int) (MoonLanding.landerXView
                        + MoonLanding.landerWidth - MoonLanding.hThrustWidth + 5), (int) (MoonLanding.landerYView + 40), null);
                MoonLanding.rThrustOn = false;
            }//end if right thrust

            g2D.dispose();

        }//end paintComponent()

    }//end subClass()

}//end class()


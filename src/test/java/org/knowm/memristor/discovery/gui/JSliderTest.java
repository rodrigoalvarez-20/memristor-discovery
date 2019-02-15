package org.knowm.memristor.discovery.gui;

import static javax.swing.BorderFactory.createEmptyBorder;

import java.awt.*;
import java.util.Hashtable;
import javax.swing.*;

public class JSliderTest {

  private static void createAndShowGUI() {

    // Create and set up the window.
    JFrame frame = new JFrame("JSlider Demo");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // Add content to the window.
    JPanel mainPanel = new JPanel();
    frame.add(mainPanel);

    mainPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    JScrollPane jScrollPane =
        new JScrollPane(
            mainPanel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane.setBorder(createEmptyBorder());
    frame.add(jScrollPane, BorderLayout.WEST);

    Box waveformRadioButtonBox;
    ButtonGroup waveformRadioButtonGroup;
    JRadioButton sineRadioButton;
    JRadioButton triangleRadioButton;
    JRadioButton squareRadioButton;
    sineRadioButton = new JRadioButton("Sine");
    triangleRadioButton = new JRadioButton("Triangle");
    squareRadioButton = new JRadioButton("Square");
    waveformRadioButtonGroup = new ButtonGroup();
    waveformRadioButtonGroup.add(sineRadioButton);
    waveformRadioButtonGroup.add(triangleRadioButton);
    // waveformRadioButtonGroup.add(squareRadioButton);
    mainPanel.add(sineRadioButton);
    mainPanel.add(triangleRadioButton);
    mainPanel.add(squareRadioButton);
    waveformRadioButtonBox = Box.createVerticalBox();
    waveformRadioButtonBox.setBorder(BorderFactory.createTitledBorder("Waveform"));
    waveformRadioButtonBox.add(sineRadioButton);
    waveformRadioButtonBox.add(triangleRadioButton);
    // waveformRadioButtonBox.add(squareRadioButton);
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(0, 6, 4, 6);
    mainPanel.add(waveformRadioButtonBox, c);

    JSlider offsetSlider;
    offsetSlider = new JSlider(JSlider.HORIZONTAL, -200, 100, 0);
    offsetSlider.setBorder(BorderFactory.createTitledBorder("Offset [V]"));
    offsetSlider.setMajorTickSpacing(25);
    offsetSlider.setMinorTickSpacing(5);
    offsetSlider.setPaintTicks(true);
    offsetSlider.setPaintLabels(true);
    offsetSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
    labelTable.put(-200, new JLabel("-2"));
    labelTable.put(-150, new JLabel("-1.5"));
    labelTable.put(-100, new JLabel("-1"));
    labelTable.put(-50, new JLabel("-.5"));
    labelTable.put(0, new JLabel("0"));
    labelTable.put(50, new JLabel(".5"));
    labelTable.put(100, new JLabel("1"));
    offsetSlider.setLabelTable(labelTable);
    offsetSlider.setPreferredSize(new Dimension(300, 80));
    c.gridy++;
    mainPanel.add(offsetSlider, c);

    JSlider amplitudeSlider;
    amplitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
    amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Amplitude [V]"));
    amplitudeSlider.setMajorTickSpacing(10);
    amplitudeSlider.setMinorTickSpacing(2);
    amplitudeSlider.setPaintTicks(true);
    amplitudeSlider.setPaintLabels(true);
    amplitudeSlider.setSnapToTicks(true);
    labelTable = new Hashtable<>();
    labelTable.put(0, new JLabel("0"));
    labelTable.put(20, new JLabel(".2"));
    labelTable.put(40, new JLabel(".4"));
    labelTable.put(60, new JLabel(".6"));
    labelTable.put(80, new JLabel(".8"));
    labelTable.put(100, new JLabel("1"));
    amplitudeSlider.setLabelTable(labelTable);
    c.gridy++;
    mainPanel.add(amplitudeSlider, c);

    // Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {

    // Schedule a job for the event dispatch thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(
        new Runnable() {

          @Override
          public void run() {
            try {
              UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
              System.out.println(
                  "UIManager.getSystemLookAndFeelClassName() = "
                      + UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            } catch (InstantiationException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
              e.printStackTrace();
            }
            UIManager.put("Slider.paintValue", Boolean.FALSE);
            createAndShowGUI();
          }
        });
  }
}

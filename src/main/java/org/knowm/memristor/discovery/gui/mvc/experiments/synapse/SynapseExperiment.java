/**
 * Memristor-Discovery is distributed under the GNU General Public License version 3
 * and is also available under alternative licenses negotiated directly
 * with Knowm, Inc.
 *
 * Copyright (c) 2016-2017 Knowm Inc. www.knowm.org
 *
 * This package also includes various components that are not part of
 * Memristor-Discovery itself:
 *
 * * `Multibit`: Copyright 2011 multibit.org, MIT License
 * * `SteelCheckBox`: Copyright 2012 Gerrit, BSD license
 *
 * Knowm, Inc. holds copyright
 * and/or sufficient licenses to all components of the Memristor-Discovery
 * package, and therefore can grant, at its sole discretion, the ability
 * for companies, individuals, or organizations to create proprietary or
 * open source (even if not GPL) modules which may be dynamically linked at
 * runtime with the portions of Memristor-Discovery which fall under our
 * copyright/license umbrella, or are distributed under more flexible
 * licenses than GPL.
 *
 * The 'Knowm' name and logos are trademarks owned by Knowm, Inc.
 *
 * If you have any questions regarding our licensing policy, please
 * contact us at `contact@knowm.org`.
 */
package org.knowm.memristor.discovery.gui.mvc.experiments.synapse;

import static org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel.EVENT_INSTRUCTION_UPDATE;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.SwingWorker;

import org.knowm.memristor.discovery.DWFProxy;
import org.knowm.memristor.discovery.gui.mvc.experiments.Experiment;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.ExperimentPlotPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.AHaHController_21.Instruction;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.control.ControlPanel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot.PlotControlModel;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot.PlotController;
import org.knowm.memristor.discovery.gui.mvc.experiments.synapse.plot.PlotPanel;
import org.knowm.memristor.discovery.utils.gpio.MuxController;

public class SynapseExperiment extends Experiment {

  private final ControlModel controlModel = new ControlModel();
  private ControlPanel controlPanel;

  private PlotPanel plotPanel;
  private final PlotControlModel plotModel = new PlotControlModel();
  private final PlotController plotController;

  private AHaHController_21 aHaHController;
  private final MuxController muxController;

  /**
   * Constructor
   *
   * @param dwfProxy
   * @param mainFrameContainer
   */
  public SynapseExperiment(DWFProxy dwfProxy, Container mainFrameContainer, boolean isV1Board) {

    super(dwfProxy, mainFrameContainer, isV1Board);

    controlPanel = new ControlPanel();
    plotPanel = new PlotPanel();
    plotController = new PlotController(plotPanel, plotModel);
    new ControlController(controlPanel, controlModel, dwfProxy);
    System.out.println(controlModel.getInstruction());
    dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

    aHaHController = new AHaHController_21(controlModel);
    aHaHController.setdWFProxy(dwfProxy);
    // aHaHController.setAmplitude(controlModel.getAmplitude());
    // aHaHController.setCalculatedFrequency(controlModel.getCalculatedFrequency());
    // aHaHController.setWaveform(controlModel.getWaveform());

    muxController = new MuxController();

  }

  @Override
  public void doCreateAndShowGUI() {
    controlPanel.clearPlotButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

       plotController.resetChart();

      }
    });
  }

  private class CaptureWorker extends SwingWorker<Boolean, Double> {

    @Override
    protected Boolean doInBackground() throws Exception {

      // aHaHController.setAmplitude(controlModel.getAmplitude());
      // aHaHController.setCalculatedFrequency(controlModel.getCalculatedFrequency());
      // aHaHController.setWaveform(controlModel.getWaveform());

      // NOTE: everytime start is clicked this runs. It first applies the desired instruction (no recording of what happens directly from the
      // operation), followed by continuous FFLV pulses, reading the `y` value.

      aHaHController.executeInstruction(controlModel.getInstruction());

      // ////////////////////////////////
      // Analog In /////////////////
      // ////////////////////////////////

      // ////////////////////////////////
      // FFLV READ PULSES /////////////////
      // ////////////////////////////////

      // muxController.setW2(Destination.OUT);
      // muxController.setW1(Destination.A);
      // muxController.setScope2(Destination.Y);
      // muxController.setScope1(Destination.B);
      // dwfProxy.setUpper8IOStates(muxController.getGPIOConfig());
      //
      // double readVoltage = .1;
      // int samplesPerPulse = 300;
      // int sampleFrequency = 10_000 * samplesPerPulse;

      while (!isCancelled()) {

        try {
          Thread.sleep(controlModel.getSampleRate() * 1000);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          // dwfProxy.getDwf().stopWave(DWF.WAVEFORM_CHANNEL_1);
          // dwfProxy.getDwf().stopAnalogCaptureBothChannels();
        }

        aHaHController.executeInstruction(Instruction.FFLV);
        System.out.println("Vy=" + aHaHController.getVy());

        publish(aHaHController.getGa(), aHaHController.getGb(), aHaHController.getVy());

      }
      return true;
    }

    @Override
    protected void process(List<Double> chunks) {

      plotController.updateYChartData(chunks.get(0), chunks.get(1), chunks.get(2));
      plotController.repaintYChart();
    }
  }

  // public void executeInstruction() {
  //
  // // 1. the IO-bits are set
  // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());
  //
  // // 2. set the waveforms ( change this to correct amplitude and sign based on instruction)
  // // Get the waveform for the selected instruction
  // double W2Amplitude = 0;
  // double[] customWaveformW2 = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), W2Amplitude, controlModel.getCalculatedFrequency());
  // double W1Amplitude = controlModel.getAmplitude() * controlModel.getInstruction().getW1VoltageMultiplier();
  // double[] customWaveformW1 = WaveformUtils.generateCustomWaveform(controlModel.getWaveform(), W1Amplitude, controlModel.getCalculatedFrequency());
  //
  // dwfProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_1, controlModel.getCalculatedFrequency(), 0, controlModel.getPulseNumber(), customWaveformW1);
  // dwfProxy.getDwf().setCustomPulseTrain(DWF.WAVEFORM_CHANNEL_2, controlModel.getCalculatedFrequency(), 0, controlModel.getPulseNumber(), customWaveformW2);
  // dwfProxy.getDwf().startPulseTrain(DWF.WAVEFORM_CHANNEL_BOTH);
  //
  // System.out.println("controlModel.getCalculatedFrequency(): " + controlModel.getCalculatedFrequency());
  // System.out.println("pulse width: " + controlModel.getPulseWidth());
  // System.out.println("pulse num: " + controlModel.getPulseNumber());
  //
  // /*
  // * must wait here to allow pulses from AWG to finish.
  // */
  //
  // int ms = controlModel.getPulseNumber() * controlModel.getPulseWidth() * 2 / 1000000;
  // if (ms <= 0) {
  // ms = 1;
  // }
  // System.out.println("sleeping for " + ms + " ms to allow pulse execution");
  // try {
  // Thread.sleep(ms);
  // } catch (InterruptedException e) {
  //
  // }
  //
  // }

  /**
   * These property change events are triggered in the controlModel in the case where the underlying controlModel is updated. Here, the controller can respond to those events and make sure the
   * corresponding GUI
   * components get updated.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    String propName = evt.getPropertyName();

    switch (propName) {

    case EVENT_INSTRUCTION_UPDATE:

      // System.out.println(controlModel.getInstruction());
      // dwfProxy.setUpper8IOStates(controlModel.getInstruction().getBits());

      break;

    default:
      break;
    }
  }

  // // this is for trigger from the analog out channel instead of analog in
  // public boolean startAnalogCaptureBothChannelsTriggerW1(double sampleFrequency, int bufferSize) {
  //
  // // System.out.println("triggerLevel = " + triggerLevel);
  // if (bufferSize > DWF.AD2_MAX_BUFFER_SIZE) {
  // // logger.error("Buffer size larger than allowed size. Setting to " + DWF.AD2_MAX_BUFFER_SIZE);
  // bufferSize = DWF.AD2_MAX_BUFFER_SIZE;
  // }
  //
  // boolean success = true;
  // success = success && dwfProxy.getDwf().FDwfAnalogInFrequencySet(sampleFrequency);
  // success = success && dwfProxy.getDwf().FDwfAnalogInBufferSizeSet(bufferSize);
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerPositionSet((bufferSize / 2) / sampleFrequency); // no buffer prefill
  //
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_1, 2.5);
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
  // success = success && dwfProxy.getDwf().FDwfAnalogInChannelRangeSet(DWF.OSCILLOSCOPE_CHANNEL_2, 2.5);
  // success = success && dwfProxy.getDwf().FDwfAnalogInAcquisitionModeSet(AcquisitionMode.Single.getId());
  // // Trigger single capture on rising edge of analog signal pulse
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerAutoTimeoutSet(0); // disable auto trigger
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerSourceSet(DWF.TriggerSource.trigsrcAnalogOut1.getId()); // one of the analog in channels
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerTypeSet(AnalogTriggerType.trigtypeEdge.getId());
  // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerChannelSet(0); // first channel
  // // Trigger Level
  // // / if (triggerLevel > 0) {
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondRisingPositive.getId());
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(triggerLevel);
  // // } else {
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerConditionSet(AnalogTriggerCondition.trigcondFallingNegative.getId());
  // // success = success && dwfProxy.getDwf().FDwfAnalogInTriggerLevelSet(triggerLevel);
  // // }
  //
  // // arm the capture
  // success = success && dwfProxy.getDwf().FDwfAnalogInConfigure(true, true);
  // if (!success) {
  // dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_1, true);
  // dwfProxy.getDwf().FDwfAnalogInChannelEnableSet(DWF.OSCILLOSCOPE_CHANNEL_2, true);
  // dwfProxy.getDwf().FDwfAnalogInConfigure(false, false);
  // throw new DWFException(dwfProxy.getDwf().FDwfGetLastErrorMsg());
  // }
  // return true;
  // }

  @Override
  public ExperimentControlModel getControlModel() {

    return controlModel;
  }

  @Override
  public ExperimentControlPanel getControlPanel() {

    return controlPanel;
  }

  @Override
  public ExperimentPlotPanel getPlotPanel() {

    return plotPanel;
  }

  @Override
  public SwingWorker getCaptureWorker() {

    return new CaptureWorker();
  }
}

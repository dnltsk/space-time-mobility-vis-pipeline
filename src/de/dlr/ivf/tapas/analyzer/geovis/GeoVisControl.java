package de.dlr.ivf.tapas.analyzer.geovis;

import javax.swing.JComponent;

import de.dlr.ivf.tapas.analyzer.core.CoreProcessInterface;
import de.dlr.ivf.tapas.analyzer.gui.ControlInputInterface;

public class GeoVisControl implements ControlInputInterface {

	private GeoVisControlPanel controlPanel = null;
	
	public GeoVisControl() {
		controlPanel = new GeoVisControlPanel();
	}
	
	@Override
	public JComponent getComponent() {
		return this.controlPanel;
	}

	@Override
	public CoreProcessInterface getProcessImpl() {
		return new GeoVisProcessor(controlPanel.getOptions());
	}

	@Override
	public boolean isActive() {
		return controlPanel.isActivated();
	}

	@Override
	public int getIndex() {
		return 0;
	}
	
}

package de.dlr.ivf.tapas.analyzer.geovis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GeoVisControlPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7539757774609826278L;
	/**
	 * 
	 */

	private Boolean activationFlag = null;
	private GeoVisOptions options = null;
	
	/**
	 * 
	 */
	public GeoVisControlPanel() {
		this.activationFlag = false;
		this.options = new GeoVisOptions();
		
		final JPanel panel = new JPanel();
		createPanelBorder(panel);
		createPanel(panel);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isActivated(){
		return this.activationFlag;
	}
	
	/**
	 * 
	 * @return
	 */
	public GeoVisOptions getOptions(){
		return this.options;
	}
	
	/**
	 * 
	 * @param panelBorder
	 * @param panel
	 */
	private void createPanelBorder(final JPanel panel) {
		this.setBorder(new TitledBorder(new LineBorder(new Color(46, 90,
				214), 2, true), "GeoVis", TitledBorder.LEADING, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		FormLayout formLayout = new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("200px:grow"), }, new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC, 
				FormFactory.DEFAULT_ROWSPEC });
		this.setLayout(formLayout);
		
		final JCheckBox checkActivate = new JCheckBox("interaktive Geovisualisierungen");
		checkActivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
		        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
		        boolean isSelected = abstractButton.getModel().isSelected();
	            activationFlag = isSelected;
		        panel.setVisible(activationFlag);
		    }
		});
		this.add(checkActivate, "2, 1, fill, fill");
		this.add(panel, "2, 2, fill, fill");
	}
	
	/**
	 * 
	 * @param panel
	 */
	private void createPanel(final JPanel panel) {
		
		panel.setVisible(activationFlag);

		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		panel.add(createBorderDummy());
		panel.add(createVisGroup());
		panel.add(createBorderDummy());
		panel.add(createBgGroup());
		panel.add(createBorderDummy());
		panel.add(createFilterGroup());
		panel.add(Box.createHorizontalGlue());
	}

	/**
	 * 
	 * @return
	 */
	private JPanel createVisGroup() {
		JPanel visGroup = new JPanel();
		visGroup.setLayout(new BoxLayout(visGroup, BoxLayout.Y_AXIS));
		
		visGroup.add(new JLabel("<html><b>Geovisualisierungen</b></html>"));
		visGroup.add(createVisCheckbox("einzelne Individuen", VisType.SINGLE, true, true));
		visGroup.add(createVisCheckbox("aggregierte Individuen", VisType.AGG, true, true));
		JCheckBox allCheckbox = createVisCheckbox("alle Individuen", VisType.ALL, false, false);
		allCheckbox.setToolTipText("module is under construction (currently unstable)");
		visGroup.add(allCheckbox);
		visGroup.add(Box.createVerticalGlue());
		
		visGroup.setMaximumSize(new Dimension(visGroup.getMinimumSize().width, visGroup.getMaximumSize().height));
		//visGroup.setBackground(Color.blue);
		
		return visGroup;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel createBgGroup() {
		JPanel bgGroup = new JPanel();
		bgGroup.setLayout(new BoxLayout(bgGroup, BoxLayout.Y_AXIS));

		bgGroup.add(new JLabel("<html><b>Hintergrundlayer</b></html>"));
		bgGroup.add(createBgCheckbox("Bezirke", BgType.BEZIRKE, true, true));
		JCheckBox checkbox = createBgCheckbox("Ortsteile", BgType.ORTSTEILE, true, false);
		checkbox.setToolTipText("Startansicht");
		bgGroup.add(checkbox);
		bgGroup.add(createBgCheckbox("Teilverkehrszellen", BgType.TVZ, true, true));
		bgGroup.add(createBgCheckbox("Hexagon-Raster", BgType.HEXAGON, false, true));
		bgGroup.add(createBgCheckbox("Quadrat-Raster", BgType.SQUARE, false, true));
		bgGroup.add(createBgCheckbox("Dreieck-Raster", BgType.TRIANGLE, false, true));
		bgGroup.add(Box.createVerticalGlue());
		
		bgGroup.setMaximumSize(new Dimension(bgGroup.getMinimumSize().width, bgGroup.getMaximumSize().height));
		//bgGroup.setBackground(Color.green);
		
		return bgGroup;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel createFilterGroup() {
		JPanel filterGroup = new JPanel();
		filterGroup.setLayout(new BoxLayout(filterGroup, BoxLayout.Y_AXIS));
		filterGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel title = new JLabel("<html><b>Filter</b></html>");
		JLabel excludeLabel = new JLabel("Exclude: ");
		JLabel includeLabel = new JLabel("Include: ");
		
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setMaximumSize(new Dimension(300,15));
		titlePanel.setMinimumSize(new Dimension(300,15));
		titlePanel.setAlignmentX(RIGHT_ALIGNMENT);
		titlePanel.add(title, BorderLayout.WEST);
		filterGroup.add(titlePanel);
		
		JPanel includePanel = new JPanel(new BorderLayout());
		includePanel.setMaximumSize(new Dimension(300,15));
		includePanel.add(includeLabel, BorderLayout.WEST);
		includePanel.setAlignmentX(RIGHT_ALIGNMENT);
		final JTextField includeText = new JTextField("", 20);
		includeText.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println("includeFilter = "+includeText.getText());
				options.setIncludeFilter(includeText.getText());
			}
		});
		//includeText.setMaximumSize(new Dimension(150, 15));
		includePanel.add(includeText,BorderLayout.CENTER);
		filterGroup.add(includePanel);
		
		filterGroup.add(Box.createVerticalStrut(5));
		
		JPanel excludePanel = new JPanel(new BorderLayout());
		excludePanel.setMaximumSize(new Dimension(300,15));
		excludePanel.setMinimumSize(new Dimension(300,15));
		excludePanel.add(excludeLabel,BorderLayout.WEST);
		final JTextField excludeText = new JTextField();
		excludeText.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println("excludeFilter = "+excludeText.getText());
				options.setExcludeFilter(excludeText.getText());
			}
		});
		excludePanel.add(excludeText,BorderLayout.CENTER);
		excludePanel.setAlignmentX(RIGHT_ALIGNMENT);
		filterGroup.add(excludePanel);
		
		includeLabel.setPreferredSize(new Dimension(60,15));
		excludeLabel.setPreferredSize(new Dimension(60,15));

		filterGroup.add(Box.createVerticalStrut(5));
		
		JPanel textfieldPanel = createPossibleAttributes();
		textfieldPanel.setAlignmentX(RIGHT_ALIGNMENT);
		filterGroup.add(textfieldPanel);
		
		filterGroup.add(Box.createVerticalGlue());
		
		filterGroup.setMaximumSize(new Dimension(filterGroup.getMinimumSize().width, filterGroup.getMaximumSize().height));
		
		//filterGroup.setBackground(Color.red);
		
		return filterGroup;
	}

	/**
	 * 
	 * @return
	 */
	private JPanel createPossibleAttributes() {
		Map<String, Method> personMethodes = new GeoVisPersonFilter(options).getPersonMethodes();
		List<String> personMethodeNames = new ArrayList<String>();
		int longestLength = 0;
    	for(String methodeName : personMethodes.keySet()){
    		personMethodeNames.add(methodeName);
    		if(methodeName.length() > longestLength){
    			longestLength = methodeName.length();
    		}
    	}
		java.util.Collections.sort(personMethodeNames);
		
		String panelText = "";
		for(String methodeName : personMethodeNames){
			panelText += methodeName;
			int spaces = longestLength - methodeName.length() + 1;
			for(int i=0; i<spaces; i++){
				panelText += " ";
			}
			panelText += "("+personMethodes.get(methodeName).getReturnType().getSimpleName()+")\n";
		}
		
		JTextArea text = new JTextArea(panelText);
		text.setRows(5);
		text.setEditable(false);
		text.setFont(text.getFont().deriveFont(11f));
		
		JScrollPane sp = new JScrollPane();   
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(text);
		
		
		JPanel textfieldPanel = new JPanel();
		textfieldPanel.setLayout(new BorderLayout());
		textfieldPanel.setMaximumSize(new Dimension(300,15));
		textfieldPanel.setMinimumSize(new Dimension(300,15));
		JLabel possibleAttributes = new JLabel("<html>Verfügbare<br/>Attribute:</html>");
		possibleAttributes.setPreferredSize(new Dimension(60,15));
		possibleAttributes.setVerticalAlignment(SwingConstants.TOP);
		textfieldPanel.add(possibleAttributes, BorderLayout.WEST);
		textfieldPanel.add(sp, BorderLayout.CENTER);
		return textfieldPanel;
	}

	/**
	 * 
	 * @param text
	 * @param visType
	 * @param defaultActivation
	 * @return
	 */
	private JCheckBox createVisCheckbox(String text, final VisType visType, boolean defaultActivation, boolean isEnabled) {
		final JCheckBox checkbox = new JCheckBox(text, defaultActivation);
		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
		        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
		        boolean isSelected = abstractButton.getModel().isSelected();
		        if(isSelected){
		        	options.selectVisType(visType);
		        }else{
		        	options.deselectVisType(visType);
		        }
		        
		    }
		});
		checkbox.setEnabled(isEnabled);
		if(defaultActivation){
			options.selectVisType(visType);
		}else{
			options.deselectVisType(visType);
		}
		return checkbox;
	}
	
	/**
	 * 
	 * @param text
	 * @param bgType
	 * @param defaultActivation
	 * @return
	 */
	private JCheckBox createBgCheckbox(String text, final BgType bgType, boolean defaultActivation, boolean isEnabled) {
		final JCheckBox checkbox = new JCheckBox(text, defaultActivation);
		checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
		        AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
		        boolean isSelected = abstractButton.getModel().isSelected();
		        if(isSelected){
		        	options.selectBgType(bgType);
		        }else{
		        	options.deselectBgType(bgType);
		        }
		        
		    }
		});
		checkbox.setEnabled(isEnabled);
		if(defaultActivation){
			options.selectBgType(bgType);
		}else{
			options.deselectBgType(bgType);
		}
		return checkbox;
	}
	
	/**
	 * 
	 * @return
	 */
	private Component createBorderDummy() {
		Component box = Box.createRigidArea(new Dimension(20,20));
		box.setBackground(Color.red);
		box.setVisible(true);
		return box;
	}
	
}

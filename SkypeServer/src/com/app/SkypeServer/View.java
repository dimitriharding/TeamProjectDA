package com.app.SkypeServer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class View extends JFrame implements ActionListener{
	private JTextArea txtArActivity;
	private JButton btnStart, btnStop;
	private JScrollPane scpnActivity;
	private JPanel pan1, pan2;
	private Controller controller;
	
	public View(Controller controller){
		this.controller = controller;
		this.setTitle("Chat "
				+ ""
				+ ""
				+ "Server");
		initializeComponenets();
		setInterface();
		this.setLayout(new BorderLayout());
		addComponentsToPanel();
		addPanelToWindow();
		setWindowProperties();
		registerListeners();
	}
	private void initializeComponenets(){
		txtArActivity = new JTextArea();
		txtArActivity.setEditable(false);
		scpnActivity = new JScrollPane(txtArActivity);
		btnStart = new JButton("START");
		btnStop = new JButton("STOP");
		pan1 = new JPanel(new GridLayout(1,1));
		pan2 = new JPanel(new GridLayout(1,3));
	}
	private void addComponentsToPanel(){
		pan1.add(scpnActivity);
		pan2.add(btnStart);
		pan2.add(btnStop);
	}
	private void addPanelToWindow(){
		this.add(pan1, BorderLayout.CENTER);
		this.add(pan2, BorderLayout.SOUTH);
	}
	private void setWindowProperties(){
		this.setSize(500,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	private void registerListeners(){
		btnStart.addActionListener(this);
		btnStop.addActionListener(this);
		scpnActivity.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
			public void adjustmentValueChanged(AdjustmentEvent e){
				txtArActivity.select(txtArActivity.getCaretPosition()*11 ,0);
				}
		});
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(btnStart)){
			controller.startSvr();
		}
		if(event.getSource().equals(btnStop)){
			controller.stopSvr();
		}
	}
	public void updateTextArea(String serverMessage){
		txtArActivity.append(serverMessage + '\n');
	}
	private void setInterface(){
		/*if (UIManager.getLookAndFeel().getSupportsWindowDecorations())
		{
		    this.setUndecorated(true);
		    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		    txtArActivity.append(UIManager.getLookAndFeel().getName() + " supports undecorated windows");
		}
		else
		{
			txtArActivity.append(UIManager.getLookAndFeel().getName() + " doesn't support undecorated windows :o(");
		    this.setUndecorated(false);
		}*/
    	String name = UIManager.getInstalledLookAndFeels()[0].getClassName();
		try {
			UIManager.setLookAndFeel(name);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//Change title font
		/*UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.BOLD, 15));
		SwingUtilities.updateComponentTreeUI(this);*/
		//set Icon
		setIconImage(Toolkit.getDefaultToolkit().getImage("img/titleIcon.png"));
	}
}

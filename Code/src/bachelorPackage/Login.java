/* -------------------

 * HistogramDemo1.java
 * -------------------
 * (C) Copyright 2004-2009, by Object Refinery Limited.
 *
 */

package bachelorPackage;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo of the {@link HistogramDataset} class.
 */
public class Login extends ApplicationFrame implements ActionListener {

	//those string will contain the connexion arguments 
	static String Host="";
	static String databaseName="";
	static String user="";
	static String password="";

	//this boolean variable will let the user the time to enter his logins to the interface
	static boolean connexionState = false;

	Font font = new Font("Serif", Font.BOLD,20);

	//setting up the preferences of the interface
	JLabel titleLabel = new JLabel("Login parameters");
	JLabel hostLabel = new JLabel("choose host : ");
	JLabel dataBaseNameLabel = new JLabel("choose database name : ");
	JLabel userLabel = new JLabel("choose user : ");
	JLabel passwordLabel = new JLabel("choose password : ");
	JTextArea hostArea = new JTextArea(2, 30);
	JTextArea dataBaseNameArea = new JTextArea(2, 30);
	JTextArea userArea = new JTextArea(2, 30);
	JTextArea passwordArea = new JTextArea(2, 30);


	public Login(String title) {
		super(title);

		//creating the chartPanel and setting it's preferences and adding labels and buttons
		JPanel chartPanel = new JPanel(new GridBagLayout());	
		chartPanel.setLayout(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		titleLabel.setFont(font);
		titleLabel.setForeground(Color.BLACK);
		hostLabel.setFont(font);
		dataBaseNameLabel.setFont(font);
		userLabel.setFont(font);
		passwordLabel.setFont(font);
		chartPanel.add(titleLabel);
		chartPanel.add(hostLabel);
		hostLabel.setForeground(Color.BLACK);
		chartPanel.add(hostArea);
		chartPanel.add(dataBaseNameLabel);
		dataBaseNameLabel.setForeground(Color.BLACK);
		chartPanel.add(dataBaseNameArea);
		chartPanel.add(userLabel);
		userLabel.setForeground(Color.BLACK);
		chartPanel.add(userArea);
		chartPanel.add(passwordLabel);
		passwordLabel.setForeground(Color.BLACK);
		chartPanel.add(passwordArea);
		chartPanel.setBackground(Color.gray);


		// setting the position of my buttons and labels in the panel 
		Insets insets = getInsets();
		Dimension size = titleLabel.getPreferredSize();
		titleLabel.setBounds(300 + insets.left,  10+ insets.top, size.width, size.height);
		size = hostLabel.getPreferredSize();
		hostLabel.setBounds(10 + insets.left,  100+ insets.top, size.width, size.height);		
		size=hostArea.getPreferredSize();
		hostArea.setBounds(300 + insets.left,  100+ insets.top, size.width, size.height);	
		size = dataBaseNameLabel.getPreferredSize();
		dataBaseNameLabel.setBounds(10 + insets.left, 200 + insets.top, size.width+20, size.height);		
		size=dataBaseNameArea.getPreferredSize();
		dataBaseNameArea.setBounds(300 + insets.left,  200 + insets.top, size.width, size.height);		
		size = userLabel.getPreferredSize();
		userLabel.setBounds(10 + insets.left, 300 + insets.top, size.width +20, size.height );
		size=userArea.getPreferredSize();
		userArea.setBounds(300 + insets.left,  300 + insets.top, size.width, size.height);		
		size = passwordLabel.getPreferredSize();
		passwordLabel.setBounds(10 + insets.left, 400 + insets.top, size.width +20, size.height );
		size=passwordArea.getPreferredSize();
		passwordArea.setBounds(300 + insets.left,  400 + insets.top, size.width, size.height);

		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));	
		final JButton submit = new JButton("submit");
		submit.setFont(font);
		submit.setActionCommand("submit");
		submit.addActionListener(this);
		chartPanel.add(submit);
		size=submit.getPreferredSize();
		submit.setBounds(400 + insets.left,  500 + insets.top, size.width+30, size.height+30);
		setContentPane(chartPanel);
		setVisible(true);
	}




	public static void main(String[] args) throws IOException {

		Login demo = new Login(
				" Connexion variables");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

		//this loop will keep the frame on until the user press submit
		while(!connexionState) {
			System.out.println();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("submit")) {
			
			//getting the connexions logins from the user inputs
			Host=hostArea.getText();
			databaseName=dataBaseNameArea.getText();
			user=userArea.getText();
			password=passwordArea.getText();

			//setting up the connexion and connecting
			Connection con = null;
			String conUrl = "jdbc:sqlserver://"+Host+"; databaseName="+databaseName+"; user="+user+"; password="+password+";";
			try 
			{
				con = DriverManager.getConnection(conUrl);
			} 
			catch (SQLException e1) 
			{
				e1.printStackTrace();
			}
			connexionState=true;
			dispose();  
		}
	}
}
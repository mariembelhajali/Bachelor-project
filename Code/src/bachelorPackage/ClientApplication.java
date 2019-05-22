package bachelorPackage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;



/**
 *This class calls the login class and lauches the 
 *
 */
public class ClientApplication extends ApplicationFrame implements ActionListener {

    //series for the dynamic charts
	private TimeSeries seriesAvg ;//average
	private TimeSeries series99;//99 percentile
	private TimeSeries series95;//95 percentile
	
	//NBR is the number of datasets I have to plot
	public static final int NBR= 4;
	//creating the chart axis and setting their name to Time
	final CombinedDomainXYPlot plot= new CombinedDomainXYPlot (new DateAxis("Time"));
	final CombinedDomainXYPlot plotDynamicCombined= new CombinedDomainXYPlot (new DateAxis("Time"));
	
	//Creating the cahrtPanel from the chart: the dynamic chart of the separated plots
	final JFreeChart chart = new JFreeChart("PocketCampus App Data Analysis (Real Time Mode)", plot);
	final ChartPanel chartPanel = new ChartPanel(chart);

	//Creating the cahrtPanel from the chart: the dynamic chart of the combined plots
	final JFreeChart chartAll = new JFreeChart("PocketCampus App Data Analysis (Real Time Mode)", plotDynamicCombined);
	final ChartPanel chartPanelAll = new ChartPanel(chartAll);
	
	//creating the JTabbedPanne which will be the recepie of my charts
	final JTabbedPane tp;
	// Declaring the Frame that I'm going to add all my charts into it
	static JFrame frame;
	
	//The panel that's going to contain the buttons of the dynamic charts
	final JPanel buttonPanelDynamic = new JPanel(new FlowLayout());
	
	//The panel that's going to conatin the buttons of the static diagram
	final JPanel buttonPanelStatic = new JPanel(new FlowLayout());
	
	//creating the panels of the different modes
	JPanel panelAll = new JPanel(new BorderLayout());
	JPanel panelSep = new JPanel(new BorderLayout());
	JPanel panelStatic = new JPanel(new BorderLayout());	
	
	//This variable will be used in the graph() methode in order
	//to let it loop to infinte since the real time shouldn't stop 
	//but sometimes it will be set to false in order to modify the graph and
	//adapt it to attributes choosen by the user
	static boolean infinite=true;
	
	//this the Name of the chart when the user choose to values of days in the 
	//static graph
	static String domainLabel="Per day";
	
	//setting default parameters 
	static String actionString="%" ;
	static String featureString="%" ;
	
	//since I'm going to use procedure I need to use callableStatement
	static CallableStatement pstmt ;
	//creatin a tables of all features and actions found in the data in logs.txt
	static String[] features = new String[] {"All", "GET","GET /alumni/raw-survey","GET /ping","GET /raw-authentication","GET /raw-cloudprint","GET /raw-events","null","POST","POST /alumni/authentication"
		,"POST /alumni/directory","POST /alumni/events","POST /alumni/raw-survey","POST /authentication","POST /camipro","POST /cloudprint","POST /directory","POST /events","POST /events/survey",
		"POST /food","POST /freeroom","POST /google/survey","POST /isacademia","POST /library","POST /map","POST /moodle","POST /news","POST /raw-camipro","POST /raw-cloudprint","POST /raw-events",
		"POST /raw-isacademia","POST /raw-moodle","POST /recommendedapps","POST /satellite","POST /transport"};
	
	//creating the JComboBox's of the dynamic mode and the static mode
	static JComboBox<String> featuresListDynamic = new JComboBox<>(features);
	static JComboBox<String> featuresListStatic = new JComboBox<>(features);
	static String[] action = new String[] {"All", "(a GET request?)","(raw call?)","autoCompleteRoom","checkAvailability","destroySession","getAllFeeds","getBalanceAndTransactions","getBeers",
		"getCamiproSettings", "getChildrenForTab(tabId: -1, poolId: -1, skip: 0, limit: 15)","getChildrenForTab(tabId: -1, poolId: -1, skip: 1, limit: 15)","getChildrenForTab(tabId: -1, poolId: -1, skip: 2, limit: 15) "
		,"getChildrenForTab(tabId: -5, poolId: -1, skip: 0, limit: 15)", "getChildrenForTab(tabId: -5, poolId: -1, skip: 15, limit: 15)","getChildrenForTab(tabId: 0, poolId: 135000010, skip: 0, limit: 15)"
		,"getChildrenForTab(tabId: 0, poolId: 135000010, skip: 15, limit: 15)","getChildrenForTab(tabId: 0, poolId: 135000010, skip: 30, limit: 15)","getChildrenForTab(tabId: 0, poolId: 135000010, skip: 42, limit: 15)",
		"getChildrenForTab(tabId: 0, poolId: 135000020, skip: 0, limit: 15)","getChildrenForTab(tabId: 0, poolId: 135000020, skip: 13, limit: 15)","getChildrenForTab(tabId: 0, poolId: 135000040, skip: 0, limit: 15)"
		,"getChildrenForTab(tabId: 0, poolId: 135000040, skip: 15, limit: 15)","getChildrenForTab(tabId: 0, poolId: 136000040, skip: 0, limit: 15)","getChildrenForTab(tabId: 0, poolId: 136000040, skip: 2, limit: 15)"
		,"getChildrenForTab(tabId: 0, poolId: 136010001, skip: 0, limit: 15)","getChildrenForTab(tabId: 0, poolId: 32000000, skip: 0, limit: 15)","getChildrenForTab(tabId: 0, poolId: 32000000, skip: 4, limit: 15)",
		"getChildrenForTab(tabId: null, poolId: -1, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: -1, skip: 0, limit: 15)","getChildrenForTab(tabId: null, poolId: -1, skip: 3, limit: 15)",
		"getChildrenForTab(tabId: null, poolId: -1, skip: 4, limit: 15)", "getChildrenForTab(tabId: null, poolId: 135000005, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 135000010, skip: 0, limit: 15)",
		"getChildrenForTab(tabId: null, poolId: 135000020, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 135000020, skip: 0, limit: 15)","getChildrenForTab(tabId: null, poolId: 135000020, skip: 1, limit: 15)"
		,"getChildrenForTab(tabId: null, poolId: 135000030, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 135000040, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 135000040, skip: 0, limit: 15)",
		"getChildrenForTab(tabId: null, poolId: 135000050, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 136000040, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 136000080, skip: 0, limit: 1)",
		"getChildrenForTab(tabId: null, poolId: 136010001, skip: 0, limit: 1)", "getChildrenForTab(tabId: null, poolId: 137000020, skip: 0, limit: 1)","getChildrenForTab(tabId: null, poolId: 32000000, skip: 0, limit: 1)",
		"getCourses","getDefaultStations","getEventItem","getEventItem2","getEventPool","getEventPool2","getFeedItemContent", "getFood","getForm","getGrades","getIsaSettings",
		"getLayers","getLocalizedFood","getOAuth2TokensFromCode","getOccupancy","getPerson","getRecommendedApps","getSchedule","getSections","getStatsAndLoadingInfo","getUserAttributes",
		"getUserMessages","indicateImWorking","logout","logPageView","populateFilterList(poolId: -1, tabId: -1)","populateFilterList(poolId: -1, tabId: -2)","populateFilterList(poolId: -1, tabId: -5)",
		"populateFilterList(poolId: 135000010, tabId: -2)","populateFilterList(poolId: 135000010, tabId: 0)","populateFilterList(poolId: 135000020, tabId: -2)","populateFilterList(poolId: 135000020, tabId: 0)",
		"populateFilterList(poolId: 135000040, tabId: -2)","populateFilterList(poolId: 135000040, tabId: 0)","populateFilterList(poolId: 136000040, tabId: 0)","populateFilterList(poolId: 136010001, tabId: 0)",
		"populateFilterList(poolId: 32000000, tabId: 0)","populateTabList(poolId: -1)","populateTabList(poolId: 135000005)","populateTabList(poolId: 135000010)","populateTabList(poolId: 135000020)",
		"populateTabList(poolId: 135000030)","populateTabList(poolId: 135000040)","populateTabList(poolId: 135000050)","populateTabList(poolId: 136000040)","populateTabList(poolId: 136000080)",
		"populateTabList(poolId: 136010001)","populateTabList(poolId: 137000020)","populateTabList(poolId: 32000000)","printDocument","printFile","printPreview","search",
		"searchDirectory", "searchForStations","searchForTrips","searchLibrary","sendLoadingInfoByEmail","setCamiproSettings","setIsaSettings", "setStringLocalizerOverride","submitForm","vote"};
	static JComboBox<String> actionsListDynamic = new JComboBox<>(action);
	static JComboBox<String> actionsListStatic = new JComboBox<>(action);

	//creating the labels of my buttons in different button panels
	static JLabel yearLabelStatic = new JLabel("Year: YYYY ");
	static JLabel monthLabelStatic = new JLabel("Month: MM");
	static JLabel dayLabelStatic = new JLabel("Day: DD");
	static JLabel minuteLabelStatic = new JLabel("choose hour: ");

	JLabel timeLabel = new JLabel("choose time: \n format mm:ss:ss ");
	static JTextArea textAreaYearStatic = new JTextArea(2, 8);
	static JTextArea textAreaMonthStatic = new JTextArea(2, 5);

	static JTextArea textAreaDayStatic = new JTextArea(2, 5);

	static JTextArea textAreaDateDynamic = new JTextArea(2, 10);
	static JTextArea textAreaMinuteStatic=new JTextArea(2, 5);
	JTextArea textAreaTime = new JTextArea(2, 10);

	// Defining a Font in order to set my titles with it
	Font font = new Font("Serif", Font.BOLD,15);//$NON-NLS-1$
	
	//this my datasets which going to containg values the calculated average and 99percentile and 95 percentile
	private TimeSeriesCollection[] datasets;
	
	private String y="Average";
	
	//creating the subplot which is my XY domain 
	XYPlot subplot;

	// default parameters as all for actions and features for the static diagram
	static CategoryDataset datasetStatic ;

	static String actionStringH="%" ;
	static String featureStringH="%" ;
	static String year="2016";
	static String month="09";
	static String day="";
	static String hourString="" ;
	
	//when the user change the values of the above attributes this number 
	//will be update in order to plot the correct chart
	static int queryNumber=0;	  


	static Font fontStatic = new Font("Serif", Font.BOLD,15);
	static Font font1 = new Font("Serif", Font.BOLD,10);

	// a callable statement of procedure that I'm going to use to execute query
	static CallableStatement cstmt ;
	
	//Creating the cahrtPanel and the chart of the static mode
	static JFreeChart chartStatic;
	static ChartPanel chartPanelStatic;

	
	/**
	 * @param title : the title of the chart
	 * @throws SQLException 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ParseException
	 * in this methode we are going to generate a panel with 3 charts in it which defines 3 modes: the static mode, the dynamic mode 
	 * with different aspects, the separted one and the comibined one
	 */

	@SuppressWarnings("deprecation")
	public ClientApplication(final String title) throws SQLException, InterruptedException, IOException, ParseException {
		//call to the super class constructor of the frame
		super(title);
		//setting the pereferencies of my chart
		chart.getTitle().setFont(font);;
		chart.getTitle().setPaint(Color.red);
		chartPanel.setPreferredSize(new Dimension(700, 350));
		chartPanelAll.setPreferredSize(new Dimension(700, 350));
		chartAll.getTitle().setFont(font);;
		chartAll.getTitle().setPaint(Color.red);
		
		//creating the frame with PocketCampus as title
		frame = new JFrame("PocketCampus");

		//intiating my datasets which will contains values I am going to plot
		this.datasets=new TimeSeriesCollection[NBR];

		// creating the average datasets and plotting it
		y="Average (ms)";
		seriesAvg = new TimeSeries("avg ", Millisecond.class);
		this.datasets[0] = new TimeSeriesCollection(seriesAvg);
		plot.add(createChart(datasets[0],y));

		// creating the 99 percentile datasets and plotting it
		y="99 percentile (ms)";
		series99 = new TimeSeries("99% ", Millisecond.class);
		this.datasets[1] = new TimeSeriesCollection(series99);
		plot.add(createChart(datasets[1],y));

		// creating the 95 percentile datasets and plotting it
		y="95 percentile (ms)";
		series95 = new TimeSeries("95% ", Millisecond.class);
		this.datasets[2] = new TimeSeriesCollection(series95);
		plot.add(createChart(datasets[2],y));
		
		//creating the chart with all the variables
		y="All the variables";
		this.datasets[3] = new TimeSeriesCollection();
		this.datasets[3].addSeries(seriesAvg);
		this.datasets[3].addSeries(series99);
		this.datasets[3].addSeries(series95);
		
		plotDynamicCombined.add(createChart(datasets[3],y));

		//setting the X-Axis of my charts
		final ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(60000.0);
		Font font = new Font("Serif", Font.BOLD,15);
		plot.getDomainAxis().setLabelPaint(Color.red);
		plot.getDomainAxis().setLabelFont(font);
		plotDynamicCombined.getDomainAxis().setLabelPaint(Color.red);
		plotDynamicCombined.getDomainAxis().setLabelFont(font);

		//creating Labes and buttons of my frame and charts
		JLabel labelFeatureDynamic = new JLabel();
		labelFeatureDynamic.setText("choose feature: ");
		labelFeatureDynamic.setForeground(Color.WHITE);
		JLabel labelFeatureStatic = new JLabel();
		labelFeatureStatic.setText("choose feature: ");
		labelFeatureStatic.setForeground(Color.WHITE);
		JLabel labelActionSepDynamic = new JLabel();
		labelActionSepDynamic.setText("choose action: ");
		labelActionSepDynamic.setForeground(Color.WHITE);
		JLabel labelActionStatic = new JLabel();
		labelActionStatic.setText("choose action: ");
		labelActionStatic.setForeground(Color.WHITE);
		final JButton showDynamic = new JButton("show");
		showDynamic.setActionCommand("show");
		showDynamic.addActionListener(this);
		final JButton showStatic = new JButton("showStatic");
		showStatic.setActionCommand("showStatic");
		showStatic.addActionListener(this);

		//setting the frame preferences
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// creating my JTabbedPane which is the container of my charts and adding it to the Frame panel
		tp=new JTabbedPane();
		Container pane = frame.getContentPane();
		pane.add(tp);

		//adding buttons my button panel and the panel with static configuration and the panel with all values also 
		//panel with separated values
		buttonPanelStatic.add(yearLabelStatic);
		buttonPanelStatic.add(textAreaYearStatic);
		textAreaYearStatic.setHighlighter( null );

		buttonPanelStatic.add(monthLabelStatic);
		buttonPanelStatic.add(textAreaMonthStatic);

		buttonPanelStatic.add(dayLabelStatic);
		buttonPanelStatic.add(textAreaDayStatic);
		yearLabelStatic.setForeground(Color.WHITE);
		monthLabelStatic.setForeground(Color.WHITE);
		dayLabelStatic.setForeground(Color.WHITE);
		minuteLabelStatic.setForeground(Color.WHITE);

		buttonPanelStatic.add(minuteLabelStatic);
		buttonPanelStatic.add(textAreaMinuteStatic);

		buttonPanelDynamic.add(labelFeatureDynamic);
		buttonPanelStatic.add(labelFeatureStatic);

		buttonPanelDynamic.add(featuresListDynamic);
		buttonPanelStatic.add(featuresListStatic);

		buttonPanelDynamic.add(labelActionSepDynamic);
		buttonPanelStatic.add(labelActionStatic);

		buttonPanelDynamic.add(actionsListDynamic);
		buttonPanelStatic.add(actionsListStatic);

		buttonPanelDynamic.add(showDynamic);
		buttonPanelStatic.add(showStatic);

		buttonPanelDynamic.setBackground(Color.BLACK);	
		buttonPanelStatic.setBackground(Color.BLACK);	

		panelAll.add(buttonPanelDynamic,BorderLayout.SOUTH);
		panelSep.add(buttonPanelDynamic,BorderLayout.SOUTH);
		panelSep.add(chartPanel);
		panelAll.add(chartPanelAll);

		tp.add("separated",panelSep);
		tp.add("All",panelAll);

		//call to the function createDataset in order to fill my datasetStatic with the values I have in the start of the program
		datasetStatic = createDataset();
		chartStatic = createChart(datasetStatic);
		
		//setting the static charte preferences
		chartStatic.setTitle("PocketCampus App Data Analysis (History Mode)");
		org.jfree.chart.axis.CategoryAxis domainAxis = chartStatic.getCategoryPlot().getDomainAxis();  
		domainAxis.setLabelPaint(Color.red);
		domainAxis.setTickLabelFont(font1);
		domainAxis.setLabelFont(font);
		chartStatic.getCategoryPlot().getRangeAxis().setLabelFont(font);
		chartStatic.getCategoryPlot().getRangeAxis().setLabelPaint(Color.RED);
		domainAxis.setLabelPaint(Color.RED);

		//
		chartPanelStatic = new ChartPanel(chartStatic, false);
		panelStatic.add(buttonPanelStatic, BorderLayout.SOUTH);

		panelStatic.add(chartPanelStatic);
		tp.add("Static",panelStatic);
		tp.setBackground(Color.white);

		//settin the frame size at the lunch of the program
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setUndecorated(true);
		frame.setVisible(true);
	}//end of the constructor of the class
	
	
	
	/**
	 * @param dataset
	 * @return chart tha contain the 
	 */
	private static JFreeChart createChart(CategoryDataset dataset) {
		//the title of the chart depending of the attributes chosen by the user
		if (queryNumber==0) {domainLabel="Per day";}
		else if (queryNumber==1) {domainLabel="Per hour";}
		else if (queryNumber==2) {domainLabel="Per minute";}
		
		// setting the title 
		String rangeLabel="Average (ms)";
		// create the chart...
		JFreeChart chart = ChartFactory.createBarChart(
				"Static",         // chart title
				domainLabel,               // domain axis label
				rangeLabel,                  // range axis label
				dataset,                  // data
				PlotOrientation.VERTICAL, // orientation
				false,                     // include legend
				true,                     // tooltips
				false                     // URLs
				);
		
		//setting the title preferences and format
		chart.getTitle().setFont(fontStatic);
		chart.getTitle().setPaint(Color.red);
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		chart.getCategoryPlot().getRangeAxis().setLabelPaint(Color.RED);
		chart.getCategoryPlot().getDomainAxis().setLabelPaint(Color.RED);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		for (int i =0;i<25;i++)       renderer.setSeriesPaint(i, Color.BLUE);
		return chart;
	}//end of the createChart methode

	
	/**
	 * @return This method creates the dataset for the static diagram .
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	private static CategoryDataset createDataset() throws SQLException, InterruptedException 
	{   
		// creating dataset 
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		//setting up the connexion
		Connection con = null;
		String conUrl = "jdbc:sqlserver://"+Login.Host+"; databaseName="+Login.databaseName+"; user="+Login.user+"; password="+Login.password+";";
		try 
		{
			con = DriverManager.getConnection(conUrl);
		} 
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
		
		//this counter will allow me to run my while loop only once
		int cnt=0;
		while(cnt<1)
		{   
			//check the query number at each call to the function in order to know which procedure must me executed
			if (queryNumber==0){
				cnt++;
				cstmt = con.prepareCall("{call avgMonthly(?,?,?,?)}");
				
				//setting the procedures parameters with the one chosen by the user
				cstmt.setInt(1, Integer.parseInt(year));
				cstmt.setInt(2, Integer.parseInt(month));
				cstmt.setString(3, featureStringH);	
				cstmt.setString(4, actionStringH);	
				ResultSet aveTempAvg = cstmt.executeQuery();
				
				// extracting the data of my query in order to plot it
				ResultSetMetaData rsmd = aveTempAvg.getMetaData();
				if(rsmd!=null) 
				{
					while (aveTempAvg.next()) 
					{
						String columnValue = aveTempAvg.getString(1);//latency
						int dayAxisNum=aveTempAvg.getInt(2);//average
						if(columnValue!=null) {
							//Adding the result of the query to the data set and setting y value to the average latency and x to the corresponding day
							dataset.setValue(Double.parseDouble(columnValue),"series",Integer.toString(dayAxisNum));
						}
						else 
						{
							dataset.setValue(0, ("series" +""+0), "");
						}
					}
				}

			}

			else if (queryNumber==1){
				cnt++;
				cstmt = con.prepareCall("{call avgDaily(?,?,?)}");	
				/*for the second query ,  the user will enter seperate year month and day 
				 so we wil have to concatenate the date into a string before setting the date value*/
				String newDate= year+"-"+month+"-"+day;
				cstmt.setString(1, newDate);
				cstmt.setString(2, featureStringH);
				cstmt.setString(3, actionStringH);	
				ResultSet aveTempAvg = cstmt.executeQuery();
				
				//extracting the data of the query
				ResultSetMetaData rsmd = aveTempAvg.getMetaData();
				if(rsmd!=null) 
				{
					int hour = 0;
					while (aveTempAvg.next()) 
					{
						String columnValue = aveTempAvg.getString(1);
						int hour2=aveTempAvg.getInt(2);
						if(columnValue!=null) {
							//setting y value to the average and the x is the corresponding hour
							dataset.setValue(Double.parseDouble(columnValue),"series",Integer.toString(hour2));
						}
						else 
						{
							//I will add 0 if I don't have the corresponding value of the specified Hour
							dataset.setValue(0, ("series" +""+hour), "");
						}
					}
				}
			}
			else if (queryNumber==2){
				cnt++;
				//Thread.sleep(100);
				cstmt = con.prepareCall("{call avgHour(?,?,?,?)}");	
				String newDate2= year+"-"+month+"-"+day;
				//same as for the daily query we have to set the parameters , we add to that the hour as last parameter
				cstmt.setString(1, newDate2);
				cstmt.setString(2, featureStringH);
				cstmt.setString(3, actionStringH);
				cstmt.setInt(4,Integer.parseInt(hourString));
				ResultSet aveTempAvg = cstmt.executeQuery();
				
				//extract the data from the query
				ResultSetMetaData rsmd = aveTempAvg.getMetaData();
				if(rsmd!=null) 
				{
					while (aveTempAvg.next()) 
					{
						String columnValue = aveTempAvg.getString(1);
						int min=aveTempAvg.getInt(2);
						if(columnValue!=null) {
							//setting y value to the average and the x is the corresponding minute

							dataset.setValue(Double.parseDouble(columnValue),"series",Integer.toString(min));
						}
						else 
						{
							//adding 0 if I don't have the corresponding minute
							dataset.setValue(0, ("series"+min),"");
						}
					}
				}

			}
		}
		return dataset;        
	}

	/**
	 * @param dataset
	 * @param title
	 * @return the xyplot for the dynamic charts
	 */
	private XYPlot createChart(final XYDataset dataset,String title) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart("PocketCampus App RealTime Data Analysis", "Time", title, dataset,true, true, false);
		subplot = result.getXYPlot();
		//setting colors of the lines
		if (title.equals("Avg (ms)")){
			subplot.getRenderer().setSeriesPaint(0, Color.red);
		}
		if (title.equals("99 percentile (ms)")){
			subplot.getRenderer().setSeriesPaint(0, Color.black);
		}
		if (title.equals("95 percentile (ms)")){
			subplot.getRenderer().setSeriesPaint(0, Color.blue);
		}
		if (title.equals("All the variables")){
			subplot.getRenderer().setSeriesPaint(0, Color.red);
			subplot.getRenderer().setSeriesPaint(1, Color.black);
			subplot.getRenderer().setSeriesPaint(2, Color.blue);
		}
		
		//setting the Axis preferences 
		subplot.getRangeAxis().setLabelPaint(Color.red);
		subplot.getRangeAxis().setLabelFont(font);
		NumberAxis range=(NumberAxis)subplot.getRangeAxis();
		NumberFormat formatter=DecimalFormat.getInstance();
		
		//change the format of the result. Setting the result to maximum two digits after the dot
		formatter.setMaximumFractionDigits(2);
		range.setNumberFormatOverride(formatter);

		//setting the color and preferences of the plot
		subplot.setBackgroundPaint(Color.white);
		subplot.setBackgroundPaint(Color.white);
		subplot.setDomainGridlinesVisible(true);
		subplot.setRangeGridlinesVisible(true);
		subplot.setRangeGridlinePaint(Color.black);
		subplot.setDomainGridlinePaint(Color.black);

		return subplot;
	}

	/**
	 * this method calls the procedures for the static graph and fills the dataset for the plots
	 * @throws ParseException
	 * @throws InterruptedException 
	 */
	public void graph() throws ParseException, InterruptedException {

		//setting up the connextion URL
		Connection con = null;
		String conUrl = "jdbc:sqlserver://"+Login.Host+"; databaseName="+Login.databaseName+"; user="+Login.user+"; password="+Login.password+";";
		try {
			con = DriverManager.getConnection(conUrl);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Statement stmt;
		try {

			stmt = con.createStatement( );

			//in this loop I will draw the dynamic charts (separeted and combined)
			while(infinite){
				try {
					//this thread is necessary otherwise I will be adding 2 values at the same moment which will not be accepted 
					//by the datatset 
					Thread.sleep(100);
					
					//preparing the call of the procedure
					pstmt = con.prepareCall("{call avgPr(?,?)}");		   
					pstmt.setString(1, featureString);
					pstmt.setString(2, actionString);				  		
					ResultSet aveTempAvg = pstmt.executeQuery();
					
					//extracting the data from the query
					ResultSetMetaData rsmd = aveTempAvg.getMetaData();
					if(rsmd!=null) {
						int columnsNumber = rsmd.getColumnCount();
						while (aveTempAvg.next()) {
							for (int i = 1; i <= columnsNumber; i++) {
								if (i > 1) System.out.print(",  ");
								String columnValue = aveTempAvg.getString(i);
								if(columnValue!=null) {
									this.seriesAvg.add( new Millisecond(),Double.parseDouble(columnValue));		
								}
								else {
									//I will add 0 if there is no value
									this.seriesAvg.add( new Millisecond(),0);
								}
							}
						}
					}
					else {
						this.seriesAvg.add( new Millisecond(),0);
					}
					
					//preparing for the call of 99 percentile procedure
					pstmt = con.prepareCall("{call ninetyNinePr(?,?)}");
					pstmt.setString(1, featureString);
					pstmt.setString(2, actionString);
					ResultSet ave99 = pstmt.executeQuery();
					
					//extracting data from the query
					ResultSetMetaData rsmd2 = aveTempAvg.getMetaData();
					if(rsmd2!=null) {

						int columnsNumber2 = rsmd2.getColumnCount();
						while (ave99.next()) {
							for (int i = 1; i <= columnsNumber2; i++) {
								if (i > 1) System.out.print(",  ");
								String columnValue2 = ave99.getString(i);

								if(columnValue2!=null ) {

									this.series99.add(new Millisecond(),Double.parseDouble(columnValue2));
								}
								else {
									// if I don't have a value I will add 0 
									this.series99.add(new Millisecond(),0);

								}
							}
						}
					}
					else {
						this.series99.add( new Millisecond(),0);
					}
					
					//preparing for the call of the 95 procedure
					pstmt = con.prepareCall("{call ninetyFivePr(?,?)}");
					pstmt.setString(1, featureString);
					pstmt.setString(2, actionString); 
					ResultSet ave95 = pstmt.executeQuery();
					
					//extracting the data from my query
					ResultSetMetaData rsmd3 = aveTempAvg.getMetaData();
					if(rsmd3!=null) {
						int columnsNumber3 = rsmd3.getColumnCount();
						while (ave95.next()) {
							for (int i = 1; i <= columnsNumber3; i++) {
								String columnValue3 = ave95.getString(i);
								if(columnValue3!=null ) {

									this.series95.add( new Millisecond(	),Double.parseDouble(columnValue3));
								}
								else {
									this.series95.add( new Millisecond(),0);

								}
							}
						}
					}
					else {

						this.series95.add( new Millisecond(),0);

					}
				} catch(SQLException ex) {
					Thread.currentThread().interrupt();
				} 
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}//end of the graph methode

	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 *if the button show is pressed I will update my graphs
	 */
	public void actionPerformed(final ActionEvent e) {
		//dynmanic graph modification
		if (e.getActionCommand().equals("show")) {

			//clear the datatsets I have to put a new fresh one 
			// set infinite to false in order to let my button be released
			infinite=false;
			seriesAvg.clear();
			series99.clear();
			series95.clear();

			//getting the entred values
			actionString = actionsListDynamic.getSelectedItem().toString();
			featureString = featuresListDynamic.getSelectedItem().toString();
			
			//modifying the query with the entred values
			if(actionString.equals("All") && featureString.equals("All")){
				actionString="%";
				featureString="%";
			}
			else if(!actionString.equals("All") && featureString.equals("All")) {						
				featureString="%";	
			}
			else if(actionString.equals("All") &&  !featureString.equals("All")) {
				actionString="%";	
			}
			else {
				//do nothing
			}
			infinite=true;
		}
		
		//static chart modification
		if (e.getActionCommand().equals("showStatic")) {	
			//getting the entred values
			actionStringH = actionsListStatic.getSelectedItem().toString();
			featureStringH = featuresListStatic.getSelectedItem().toString();
			year=textAreaYearStatic.getText();
			month=textAreaMonthStatic.getText();
			day=textAreaDayStatic.getText();
			hourString=textAreaMinuteStatic.getText().toString();	
			
			//modifying the graph with entered values  
			if (year.isEmpty()&&month.isEmpty()&&day.isEmpty()){
				queryNumber=0;
				year="2016";
				month="09";

			}

			if ((!year.isEmpty())&&(!month.isEmpty())&&(day.isEmpty())){
				queryNumber=0;

			}

			if ((!day.isEmpty())&&(hourString.isEmpty())) {
				queryNumber=1; 
			}	

			if (!(hourString.isEmpty())) {
				queryNumber=2; 
			}

			if(actionStringH.equals("All") && featureStringH.equals("All") ){
				actionStringH="%";
				featureStringH="%";
			}
			else if(!actionStringH.equals("All") && featureStringH.equals("All")) {						
				featureStringH="%";	
			}
			else if(actionStringH.equals("All") &&  !featureStringH.equals("All")) {
				actionStringH="%";	
			}
			else {
				//do nothing
			}
			try {
				datasetStatic=createDataset();
			} catch (SQLException | InterruptedException e1) {
				e1.printStackTrace();
			}	

			//remove what is drawing in the static chart and replace it with a fresh 
			//vearsion of the chart depending on the values entred by the user
			panelStatic.remove(chartPanelStatic);
			chartStatic = createChart(datasetStatic);
			org.jfree.chart.axis.CategoryAxis domainAxis = chartStatic.getCategoryPlot().getDomainAxis();  
			domainAxis.setTickLabelFont(font1);
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI/6));
			chartPanelStatic = new ChartPanel(chartStatic, false);
			panelStatic.add(chartPanelStatic);
			panelStatic.setVisible(true);
			panelStatic.repaint();
			panelStatic.validate();			
		}		
	}//end of actionPerformed method
	
	

	/**
	 * @param args
	 * @throws ParseException
	 * @throws SQLException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(final String[] args) throws ParseException, SQLException, InterruptedException, IOException {
		// I will call the main of the Login class to let the user enter his logins 
		// and the using them to connect to the data base
		while(!Login.connexionState) {
			Login.main(null);
		}
		
		//calaling the constructor and starting my graphs
		final ClientApplication drawObject = new ClientApplication("PocketCampus App RealTime Data Analysis");
		RefineryUtilities.centerFrameOnScreen(drawObject);		
		drawObject.graph();
	}
}
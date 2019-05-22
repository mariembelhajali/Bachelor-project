# Pocket Campus data analysis  GUI

This application is designed for Pocket Campus.


## Requirements: 

To be able to run the program and to take advantage of it's features you need to install some softwares and to set them as we will explain next.

. Eclipse Oxygen : http://www.eclipse.org/downloads/eclipse-packages/
. SQL Server(Free version will be only valid for 6 months): https://www.microsoft.com/en-us/sql-server/sql-server-downloads
.  Install the external Jars that we attached with the project code folder:
	. right click on the project folder in eclipse
	. choose build path 
	. add external jars
	. apply and close
. include JfreeChart 

## Execution:

After doing the previous installations you can now execute the project as follow: 
1) First of all we have registered procedure in the Class JavaSqlFiles that we need to execute them, in the project folder we attached an sql file. 
So we must change the path of the Sql file in our java class to be able to run it.
2) Java Server Process
- If we want to run the Java Client Process we need to change the location of the text file that we read from, in our case it's logs.txt 
- After we modified the file we can press on 'Run' to start uploading data into our database
- A login interface will pop-up in order to specify the logins: host name, database name, user and password
3) Java Client Process
- to be able to run this java process we need to have some data in our database(at least one record).
- We need only to execute this java with 'Run' on eclipse.
- A login interface will pop-up in order to specify the logins: host name, database name, user and password.

## Interface parameters: 

Our java client go to database an execute the queries in sql file and plot the corresponding results. 

You will see three tabs: separated , all and static

The first and the second tabs are related, they have the same button panel. 

The static mode have it's own button panel.

Let's start with dynamic mode-> separated : 

the observer will see three separated plots: the average , 99 percentile and 95 percentile

in the bottom of the first page the will be panel that contain two scrollable list:
	- The first one is choose feature: This is by default set to All
	- The second one is choose actions: This is by default is set to All.
	==> we can filter our data using a specified action or feature but every time we change we need to refresh the page by clicking the button show.
	
for the second tab which is the all tab:
	. it's have the same panel as the first one, so we can filter our results using feature and /or action.
The third tab is the history mode:
In the history mode we will plot histograms of the average of all day of a month, all hours of a day or all minute of an hour.
. The user will see by default the average of all days in the first month of our database.
	
. If the user user put the year and the month then he click on show, he will display the average over all day of that month
	
. If he add also the day, he will get the average of all hour of the specified day
	
. He can also choose an hour of the day so that he will get the average of all minutes

Attention: Please do not try to trick the interface: choose either year month , year month day or year month day and hour. Those may run exceptions.
Also in the history mode the user can select a feature and/or action.

## Authors:
. Sirin utku

. Mariem Belhaj Ali

. Mouadh Hamdi

/** 
 * CheckFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
 * 
 * This is the tool!
 * 
 * Execute it in the following way:
 * java VisualFoV arg1 arg2 -v 
 * arg1 defines the CSV file as the input for the visual configuratons, or 0 for random
 * arg2 defines the CSV file as the input for the targets configuratons, or 0 for random
 * The third argument is for verbose mode when it is -v
 * */

import java.io.*;
import java.util.*;

public class CheckFoV
{
	//Basic structure for sensors and targets
	private ArrayList<VSensor> sensors;	
	private ArrayList<Target> targets;
	
	//Configuration files
	private static String inputSensorsCSV = "";
	private static String inputTargetsCSV = "";
	private static String inputRandomCSV = "./randomConfigurations.csv";

	//Sensors and FoV are drawn using auxiliary export files. This helps if other programs want to process the exported data
	private String exportAllVertices = "./FileAllVertices.txt";
	private String exportAllTargets = "./FileTargets.txt";
	private String exportInterVertices = "./FileInterVertices.txt"; //Auxiliary file with the overlapping intersection vertices. Currently NOT displayed by the tool
	private String exportResults = "./FileResults.txt";
	
	//Basic configurations
	static int optionSensors=-1; //It says what kind of option was selected by the user. -1 for file inputs and 0 for random configurations
	static int optionTargets=-1; //It says what kind of option was selected by the user. -1 for file inputs and 0 for random configurations
	static boolean verbose=false;

	//Numerical results
	int totalSensors=0; //Total number of sensors
	int totalTargets=0; //Total number of targets
	double totalFoVArea=0.0; //Sum of all original FoV of all sensors
	double totalOverlappedArea=0.0; //Sum of all overlapped areas
	int numberViewedTargets=0; //Total number of viewed targets
	int numberRedundantTargets=0; //Total number of targets viewed more than once
	int numberHitsTargets=0; //Total number of times that a target is viewed

	//***************//
	public CheckFoV()
	{
		//Initial configuration
		createSensors(); //Create visual sensors and targets

		//Computation of the overlapping
		//A new class was used since this computation is more complex
		totalOverlappedArea = new ComputeOverlap().selectRedundants(sensors);

		//Computation of targets coverage - an auxiliary function is used
		computeCoverage();
		
		//Auxiliary method
		if (verbose)
			printSensors();

		//Draw the results visually (Java 2D)
		drawResults();
	}

	//***************//
	static public void main (String input[])
	{
		//Process the users input arguments
		if (input.length >= 2)
		{
			try {
				optionSensors = Integer.parseInt(input[0]);
			} catch (NumberFormatException e) {
				inputSensorsCSV = input[0]; //File name for visual sensors
			}
	
			try {
				optionTargets = Integer.parseInt(input[1]);
			} catch (NumberFormatException e) {
				inputTargetsCSV = input[1]; //File name	for targets
			}
		}
		else 
		{
			System.err.println ("Two minimum args required: input CSV for visual sensors (or 0 por random), and input CSV for targets (or 0 por random).\nThe third argument may be -v for verbose mode.\nConsult documentation.");
			System.exit(0);
		}
				
		//Verify verbose mode
		for (String arg : input) 
		{
			if (arg.equals("-v")) 
			{
				verbose = true;
				break;	
			}
		}
			
		new CheckFoV();			
	}
		
	//***************//
	//Sensors creation
	public void createSensors()
	{
		try
		{
			//Sensors creation
			sensors = new ArrayList();
			Scanner sc;

			if (optionSensors == 0)  //Visual sensors are randomly created using the inputRandomCSV as reference (second line)
			{
				createRandomSensors(sensors);
			}
			else if (optionSensors == -1) //Read the file in the format: Ax,Ay,RADIUS,ANGLE,ORIENTATION
			{
			
				sc = new Scanner(new File(inputSensorsCSV)); //Provided by the user 
				
				//Skip the first line - header of the file
				sc.nextLine();
					
				int counter = 1;
				while (sc.hasNextLine()) 
				{  
					String[] columns = sc.nextLine().split(",");

					int Ax = Integer.parseInt(columns[0]);
					int Ay = Integer.parseInt(columns[1]);
					int radius = Integer.parseInt(columns[2]);
					int vAngle = Integer.parseInt(columns[3]);
					int orientation = Integer.parseInt(columns[4]);

					VSensor s = new VSensor (counter++, Ax, Ay, radius, vAngle, orientation);

					sensors.add (s);
					totalFoVArea += s.getFoV().getArea(); //Numerical result
				}   
				totalSensors = sensors.size();
				sc.close();  //closes the scanner  
			}

			//Targets creation
			targets = new ArrayList();  
			if (optionTargets == 0)  //Targets are randomly created using the inputRandomCSV as reference (third line)
			{
				createRandomTargets(targets);
			}
			else if (optionTargets == -1) //Read the file in the format: Tx,Ty
			{
			
				sc = new Scanner(new File(inputTargetsCSV)); //Provided by the user 
				
				//Skip the first line
				sc.nextLine();
					
				int counter = 1;
				while (sc.hasNextLine()) 
				{  
					String[] columns = sc.nextLine().split(",");

					int Tx = Integer.parseInt(columns[0]);
					int Ty = Integer.parseInt(columns[1]);
				
					Target t = new Target (counter, Tx, Ty);

					targets.add (t);
				}   
				totalTargets = targets.size();
				sc.close();  //closes the scanner  
			}
		}
		catch (Exception exc)
		{
			System.err.println ("Input CSV file could not be read: " + exc.toString());
			exc.printStackTrace();
			System.exit(0);
		}
	}

	//***************//
	//Randomzied creation of sensors and targets
	//The random file has the following configuration: 
	//NumberSensors,NumberTargets,Width,Height,MinRadius,MaxRadius,MinAngle,MaxAngle
	public void createRandomSensors(ArrayList<VSensor> sensors)
	{
		try
		{
			Scanner sc = new Scanner(new File(inputRandomCSV));
			
			//Skip the first line
			sc.nextLine();

			String[] columns = sc.nextLine().split(",");
			sc.close();  //closes the scanner  

			//Information for random configuration of sensors
			int numSensors = Integer.parseInt(columns[0]);
			int width = Integer.parseInt(columns[2]);
			int height = Integer.parseInt(columns[3]);
			int minRadius = Integer.parseInt(columns[4]);
			int maxRadius = Integer.parseInt(columns[5]);
			int minAngle = Integer.parseInt(columns[6]);
			int maxAngle = Integer.parseInt(columns[7]);

			for (int counter=1; counter <= numSensors; counter++)
			{
				VSensor s = new VSensor (counter,(int)(Math.random()*width+50),(int)(Math.random()*height+100),minRadius + (int)(Math.random()*(maxRadius - minRadius)), minAngle + (int)(Math.random()*(maxAngle - minAngle)),(int)(Math.random()*360));
				sensors.add (s);
				totalFoVArea += s.getFoV().getArea();
			}
			totalSensors = sensors.size();
		}
		catch (Exception exc)
		{
			System.err.println ("Input random CSV file could not be read: " + exc.toString());
			exc.printStackTrace();
			System.exit(0);
		}
	}

	//***************//
	//The random file has the following configuration: 
	//NumberSensors,NumberTargets,Width,Height,MinRadius,MaxRadius,MinAngle,MaxAngle
	public void createRandomTargets(ArrayList<Target> targets)
	{
		try
		{
			Scanner sc = new Scanner(new File(inputRandomCSV));
			//Skip the first line
			sc.nextLine();

			String[] columns = sc.nextLine().split(",");
			sc.close();  //closes the scanner  

			//Information for random configuration of sensors
			int numTargets = Integer.parseInt(columns[1]);
			int width = Integer.parseInt(columns[2]);
			int height = Integer.parseInt(columns[3]);

			for (int counter=1; counter <= numTargets; counter++)
			{
				Target t = new Target (counter,(int)(Math.random()*width+50),(int)(Math.random()*height+100));
				targets.add (t);
			}
			totalTargets = targets.size();
		}
		catch (Exception exc)
		{
			System.err.println ("Input random CSV file could not be read: " + exc.toString());
			System.exit(0);
		}
	}

	//***************//
	public void computeCoverage()
	{
		//First, go over the targets
		for (int t = 0; t < targets.size(); t++)
		{
			Target target = targets.get(t);	

			//Now, check the target for all visual sensors
			for (int s = 0; s < sensors.size(); s++)
			{
				VSensor sensor = sensors.get(s);

				if (checkViewing(sensor, target))
				{
					target.setViewed();
				}

			}

			//Account for the number of viewed targets and view hits (for redundancy)
			if (target.isViewed())
			{
				numberViewedTargets++;
				numberHitsTargets += target.getNumberViews();

				if (target.getNumberViews() > 1)
					numberRedundantTargets++;
			}
		}

	}

	//***************//
	//This method check if a target t is being seen by a visual sensor v
	public boolean checkViewing (VSensor s, Target t)
	{
		//Positions of the targets
		int x = t.getTx();
		int y = t.getTy();

		FoV fov = s.getFoV();
		ArrayList<Vertex> vert = fov.getVertices();
		double Ax = vert.get(0).getX();
		double Ay = vert.get(0).getY();
		double Bx = vert.get(1).getX();
		double By = vert.get(1).getY();
		double Cx = vert.get(2).getX();
		double Cy = vert.get(2).getY();

		double ABC = Math.abs (Ax * (By - Cy) + Bx * (Cy - Ay) + Cx * (Ay - By));

		double ABT = Math.abs (Ax * (By - y) + Bx * (y - Ay) + x * (Ay - By));
		double ATC = Math.abs (Ax * (y - Cy) + x * (Cy - Ay) + Cx * (Ay - y));
		double TBC = Math.abs (x * (By - Cy) + Bx * (Cy - y) + Cx * (y - By));

		if ((int)(ABT + ATC + TBC) == (int)ABC)
			//Target is being viewed
			return true;
		else
			//Target is not being viewed
			return false;
	}

	//***************//
	//Exhibit information on the terminal, when in verbose mode
	public void printSensors()
	{
		//Sensors printing
		int elements = sensors.size();
		for (int i =0; i < elements; i++)
		{
			VSensor s = (VSensor) sensors.get(i);
			FoV fov = s.getFoV();
			ArrayList<Vertex> vert = fov.getVertices();
			System.out.println("Visual sensor " + (i+1));
			for (int h=0; h < vert.size(); h++)
			{
				System.out.print ("V" + (h+1) + "(x,y): " + vert.get(h).getX() + "," + vert.get(h).getY() + "\n");
			}
			System.out.print ("Radius: " + s.getRadius() + "\n");
			System.out.print ("Angle: " + s.getAngle() + "\n");
			System.out.print ("Orientation: " + s.getOrientation() + "\n");

			System.out.println();
		}

		//Targets printing
		elements = targets.size();
		for (int i =0; i < elements; i++)
		{
			Target t = (Target) targets.get(i);
			System.out.println("Target " + (i+1));
			System.out.print ("(Tx,Ty): " + t.getTx() + "," + t.getTy() + "\n");
			System.out.println();
		}
	}
	
	//***************//
	//This method first exports the vertices to be printed in a screen
	//Then, it calls PaintVisualFoV, which actually exhibit the sensors and FoV
 	public void drawResults()
	{
		BufferedWriter bw;
		try
		{			
			//Export all vertices. It is required for drawing the sensors, targets and FoV
			File f = new File (exportAllVertices);
			if (!f.exists())
				f.createNewFile();

			FileWriter fw = new FileWriter (f.getAbsoluteFile());
			bw = new BufferedWriter (fw);
		
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i < sensors.size(); i++)
			{				
				VSensor s = sensors.get(i);
				FoV fov = s.getFoV();
				ArrayList<Vertex> vert = fov.getVertices();
				for (int h=0; h < vert.size(); h++)
				{
					buffer.append (vert.get(h).getX() + "\n");
					buffer.append (vert.get(h).getY() + "\n");
				}
			}
	
			bw.write (buffer.toString());
			bw.close();

			//Export all intersection vertices
			f = new File (exportInterVertices);
			if (!f.exists())
				f.createNewFile();

			//Export all targets
			//It is X,Y,Views parameters 
			f = new File (exportAllTargets);
			if (!f.exists())
				f.createNewFile();

			fw = new FileWriter (f.getAbsoluteFile());
			bw = new BufferedWriter (fw);
		
			buffer = new StringBuffer();
			for (int i=0; i < targets.size(); i++)
			{				
				Target t = targets.get(i);
				buffer.append (t.getTx() + "\n");
				buffer.append (t.getTy() + "\n");
				buffer.append (t.getNumberViews() + "\n");
			}
	
			bw.write (buffer.toString());
			bw.close();

			//Now, export the results
			f = new File (exportResults);
			if (!f.exists())
				f.createNewFile();

			fw = new FileWriter (f.getAbsoluteFile());
			bw = new BufferedWriter (fw);
		
			buffer = new StringBuffer();
			
			//Number of sensors
			buffer.append (totalSensors + "\n");
			//Number of target
			buffer.append (totalTargets + "\n");
			//FoV total area
			buffer.append (totalFoVArea + "\n");
			//Overlapped FoV total area
			buffer.append (totalOverlappedArea + "\n");
			//Number of viewed targets
			buffer.append (numberViewedTargets + "\n");
			//Number of redundat views of targets
			buffer.append (numberRedundantTargets + "\n");
			//Number of targets hits
			buffer.append (numberHitsTargets + "\n");
			
			bw.write (buffer.toString());
			bw.close();

			//Exhibit the painting of the sensors and FoV
			new DrawAllResults();
		}
		catch (Exception exc)
		{
			System.err.println (exc.toString());
		}
	}
}
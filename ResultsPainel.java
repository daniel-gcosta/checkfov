/** 
 * VisualFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;

class ResultsPainel extends JPanel
{
	String fileResults = "./FileResults.txt";

    
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		try
		{
			//Basic configurations for the readings
			Graphics2D g2 = (Graphics2D) g;
			File f;
			FileReader fr;
			BufferedReader br;

			/////////////////////////////////
			//Write the results
			f = new File (fileResults);
			fr = new FileReader (f.getAbsoluteFile());
			br = new BufferedReader (fr);
			
            //General configurations
            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            Font regularFont = new Font("Arial", Font.PLAIN, 13);
            Font boldFont = new Font("Arial", Font.BOLD, 13);
            Font labelFont = new Font("Arial", Font.BOLD, 14);

            //Read the results in the following order: 
            //Number of Sensors
            //Number of Targets
            //Total FoV Area
            //Total Overlapped FoV Area
            //Percentage of viewed targets
            //Targets view hits
            //Number of targets redundantly viewed

            g2.setFont(labelFont);
            g2.drawString ("Visual sensing", 20, 30);
            g2.drawLine (20, 35, 225, 35);

            double numSensors = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Number of visuals sensors:", 20, 55);
            g2.setFont(boldFont);
            g2.drawString ("" + (int)numSensors, 185, 55);
            
            double numTargets = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Number of targets:", 20, 75);
            g2.setFont(boldFont);
            g2.drawString ("" + (int)numTargets, 134, 75);
                
            g2.setFont(labelFont);
            g2.drawString ("FoV", 20, 115);
            g2.drawLine (20, 120, 225, 120);

            double areaFoV = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Total FoV area:", 20, 140);
            g2.setFont(boldFont);
            g2.drawString ("" + decimalFormat.format(areaFoV) + " m²", 115, 140);

            double overlapFoV = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Total overlapped FoV area:", 20, 160);
            g2.setFont(boldFont);
            g2.drawString ("" + decimalFormat.format(overlapFoV) + " m²", 182, 160);

            g2.setFont(labelFont);
            g2.drawString ("Targets coverage", 20, 200);
            g2.drawLine (20, 205, 225, 205);

            double viewedTargets = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Percentage of viewed targets:", 20, 225);
            g2.setFont(boldFont);
            g2.drawString (" (" + (int)viewedTargets + ") " + decimalFormat.format(viewedTargets*100.0/numTargets) + "%", 193, 225);

            double redundantTargets = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Number of targets redundantly viewed:", 20, 245);
            g2.setFont(boldFont);
            g2.drawString ("" + decimalFormat.format(redundantTargets), 248, 245);

            double hitsTargets = Double.parseDouble(br.readLine());
            g2.setFont(regularFont);
            g2.drawString ("Number of times targets were viewed:", 20, 265);
            g2.setFont(boldFont);
            g2.drawString ("" + decimalFormat.format(hitsTargets), 244, 265);
			
		}
		catch (Exception exc)
		{
			System.err.println (exc.toString());
		}
	}
}

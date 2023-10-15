/** 
 * VisualFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;

class VisualSensorsPainel extends JPanel
{
	String fileSensors = "./FileAllVertices.txt";
	String fileTargets = "./FileTargets.txt";
	String fileOverlaped = "./FileOverlapVertices.txt";

	Color sensorColor = Color.DARK_GRAY;
	Color targetUnviewedColor = new Color(0, 0, 204);
	Color targetViewedColor = new Color(204, 0, 0);
	
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
			String line;

			/////////////////////////////////
			//Draw the overlapped areas
			f = new File (fileOverlaped);
			fr = new FileReader (f.getAbsoluteFile());
			br = new BufferedReader (fr);

			//Overlapped area is a polygon
			while ((line = br.readLine()) != null)
			{
				int num = Integer.parseInt(line);
				int x[] = new int [num];
				int y[] = new int [num];

				for (int i=0; i < num; i++)
				{
					x[i] = (int)Double.parseDouble(br.readLine());
					y[i] = (int)Double.parseDouble(br.readLine());
				}
				
				g2.setColor(Color.LIGHT_GRAY);
				Polygon poly = new Polygon (x, y, num);
				g2.fillPolygon (poly);
			}

			//Draw the sensors
			f = new File (fileSensors);
			fr = new FileReader (f.getAbsoluteFile());
			br = new BufferedReader (fr);

			//triangle - 3 lines to draw
			while ((line = br.readLine()) != null)
			{
				double x1 = Double.parseDouble(line);				
				double y1 = Double.parseDouble(br.readLine());
				double x2 = Double.parseDouble(br.readLine());
				double y2 = Double.parseDouble(br.readLine());
				double x3 = Double.parseDouble(br.readLine());
				double y3 = Double.parseDouble(br.readLine());
	
				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(2));
				g2.draw (new Line2D.Double(x1, y1, x2, y2));
				g2.draw (new Line2D.Double(x2, y2, x3, y3));
				g2.draw (new Line2D.Double(x3, y3, x1, y1));
				
				//Point representing the visual sensor position: Vertex A
				g2.setColor(sensorColor);
				g2.fill (new Ellipse2D.Double(x1 - 5, y1 - 5, 10, 10));
			}

			/////////////////////////////////////
			//Draw the targets
			f = new File (fileTargets);
			fr = new FileReader (f.getAbsoluteFile());
			br = new BufferedReader (fr);

			Font targetFont = new Font("Arial", Font.BOLD, 10);
			g2.setFont(targetFont);
			int count=1;
			while ((line = br.readLine()) != null)
			{
				double tx = Double.parseDouble(line);				
				double ty = Double.parseDouble(br.readLine());
				int numViews = Integer.parseInt(br.readLine());
				
				//Point representing the target
				if (numViews == 0) //Not viewed
				{
					g2.setColor(targetUnviewedColor);
					g2.fillRoundRect((int)(tx - 4), (int)(ty - 4), 8, 8,3,3);
				}
				else
				{
					g2.setColor(targetViewedColor);
					g2.fillRoundRect((int)(tx - 4), (int)(ty - 4), 8, 8,3,3);
				}
				g2.setColor(Color.BLACK);
				g2.drawString("" + count++, (int)(tx+8), (int)(ty+4));
			}
			
		}
		catch (Exception exc)
		{
			System.err.println (exc.toString());
		}
	}
}

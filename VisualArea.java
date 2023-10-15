/** 
 * VisualFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
 * 
 * This is the painting code, to exhibit the results one window per type
*/

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.geom.*;

class PaintVisualFoV extends JPanel
{
	String file = "./FileAllVertices.txt";

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		try
		{
			Graphics2D g2 = (Graphics2D) g;
			
			File f = new File (file);
			FileReader fr = new FileReader (f.getAbsoluteFile());
			BufferedReader br = new BufferedReader (fr);
		
			//Draw the sensors' FoV as a triangle. There are 3 lines to draw for each triangle
			String line;
			while ((line = br.readLine()) != null)
			{
				double x1 = Double.parseDouble(line);				
				double y1 = Double.parseDouble(br.readLine());
				double x2 = Double.parseDouble(br.readLine());
				double y2 = Double.parseDouble(br.readLine());
				double x3 = Double.parseDouble(br.readLine());
				double y3 = Double.parseDouble(br.readLine());
	
				g2.draw (new Line2D.Double(x1, y1, x2, y2));
				g2.draw (new Line2D.Double(x2, y2, x3, y3));
				g2.draw (new Line2D.Double(x3, y3, x1, y1));
				
				//Point representing the visual sensor position (Vertex A)
				g2.fill (new Ellipse2D.Double(x1 - 2.5, y1 - 2.5, 5, 5));

			}
			
		}
		catch (Exception exc)
		{
			System.err.println (exc.toString());
		}
	}
}

/** 
 * CheckFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
*/

import javax.swing.*;

public class DrawAllResults
{
	//***************//
	public DrawAllResults ()
	{
		VisualSensorsPainel panelSensors = new VisualSensorsPainel();
		
		//Visual Sensors and Targets
		JFrame frame1 = new JFrame ("Draw Panel: visual sensors and targets");
		frame1.setBounds (50, 50, 550, 650);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame1.getContentPane().add (panelSensors);
		frame1.setVisible(true);

		ResultsPainel resultsPainel = new ResultsPainel();
		
		//Visual Sensors and Targets
		JFrame frame2 = new JFrame ("Results Panel: computed metrics");
		frame2.setBounds (620, 50, 300, 400);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame2.getContentPane().add (resultsPainel);
		frame2.setVisible(true);
	}
	static public void main (String ent[])
	{
		new DrawAllResults();
	}
		
}

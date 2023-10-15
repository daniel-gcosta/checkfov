/** 
 * VisualFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
*/

import java.math.*;

class Vertex
{
	//In order to increase precisoon
	BigDecimal pointX, pointY;

	public Vertex (BigDecimal x, BigDecimal y)
	{
		pointX = x.setScale (2, RoundingMode. HALF_EVEN);
		pointY = y.setScale (2, RoundingMode. HALF_EVEN);
	}
	public double getX()
	{
		return pointX.doubleValue();
	}
	public double getY()
	{
		return pointY.doubleValue();
	}
}

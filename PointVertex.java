/** 
 * CheckFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
 * 
 * This class is necessary to support FoV overlapping computations. It is auxiliary service, combining charactersitcs of
 * Vertex and Vsensor
*/

public class PointVertex 
{
	private double angle;
	private Vertex vertex;
	
	//***************//
	public double getAngle()
	{
		return angle;
	}

	//***************//
	public Vertex getVertex()
	{
		return vertex;
	}

	//***************//
	public void insertAngle(double a)
	{
		angle = a;
	}

	//***************//
	public void insertVertex (Vertex v)
	{
		vertex = v;
	}
}
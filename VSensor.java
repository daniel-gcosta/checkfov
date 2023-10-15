
/** 
 * CheckFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
 * 
 * This is the defintion of a Visual Sensor, the basic element in this tool
*/

import java.math.*;

class VSensor
{
	private int ID;
	
	//(x,y) position of the visual sensor
	private BigDecimal Ax;
	private BigDecimal Ay;

	//Viewing range
	private int radius;
	
	//This is the viewing angle
	private int angle;

	//Orientation angle
	private int orientation;

	/////
	//Computed FoV or OFoV
	private FoV fov;

	public VSensor (int id, int x, int y, int r, int a, int o)
	{
		//Initial configuration
		ID = id;
	
		Ax = new BigDecimal(x);
		Ay = new BigDecimal(y);
		radius = r;
		angle = a;
		orientation = o;
		
		fov = new FoV();
		computeVerticesIniciais();
	}

	//***************//
	public void computeVerticesIniciais()
	{
		//Vertice B
		BigDecimal Bx = new BigDecimal (Ax.doubleValue() + radius * Math.cos(Math.toRadians(orientation)));
		BigDecimal By = new BigDecimal (Ay.doubleValue() + radius * Math.sin(Math.toRadians(orientation)));

		//Vertice C
		BigDecimal Cx = new BigDecimal (Ax.doubleValue() + radius * Math.cos( (Math.toRadians(angle+orientation)) % (2 * Math.PI)));
		BigDecimal Cy = new BigDecimal (Ay.doubleValue() + radius * Math.sin( (Math.toRadians(angle+orientation)) % (2 * Math.PI)));
		
		fov.insertVertexA (new Vertex(Ax, Ay));
		fov.insertVertex (new Vertex(Bx, By));
		fov.insertVertex (new Vertex(Cx, Cy));
				
		fov.computeFoV();
	}
	
	//BASIC interaction methods
	//***************//
	public void updateFoV(FoV f)
	{
		fov = f;
		fov.computeFoV();
	}
	
	//***************//
	public void setFoV (FoV f)
	{
		fov = f;
	}

	//***************//
	public FoV getFoV ()
	{
		return fov;
	}

	//***************//
	public int getAngle ()
	{
		return angle;
	}

	//***************//
	public int getOrientation ()
	{
		return orientation;
	}

	//***************//
	public int getRadius ()
	{
		return radius;
	}

	//***************//
	public int getID()
	{
		return ID;
	}
}



/** 
 * CheckFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
*/

import java.util.*;

class FoV
{
	private ArrayList<Vertex> vertices;
	private double area;
	private Vertex A; //Vertezx A, which is the position of the visual sensors. 
	//A visual sensor is composed of vertices A, B and C
		
	public FoV()
	{
		vertices = new ArrayList();
		area  = 0;
		A = null;
	}
	
	//***************//
	public void resetVertex ()
	{
		vertices = new ArrayList();
		A = null;
	}
	
	//***************//
	public void insertVertex (Vertex v)
	{
		vertices.add(v);
	}
	
	//***************//
	public void insertVertexA (Vertex v)
	{
		A = v;
		insertVertex(v);
	}
	
	//***************//
	//It helps knowing the vertex A in some cases
	public Vertex getVerticeA ()
	{
		return A;
	}
	
	//***************//
	public ArrayList<Vertex> getVertices ()
	{
		return vertices;
	}

	//***************//
	public void insertArea (double a)
	{
		area = a;
	}
	
	//Important computation of the FoV area, for any number of vertices of the FoV
	//It is the shoelace algorithm
	public void computeFoV()
	{
		int numVertices = vertices.size();
	
		double a = 0.0;
		int j = numVertices-1;
		for (int i=0; i < numVertices; i++)
		{
			a = a + (vertices.get(i).getX() * vertices.get(j).getY()) - (vertices.get(j).getX() * vertices.get(i).getY());
			j = i;
		}
		area = Math.abs(a)/2; //Computation of the area of a triangle
	}
	
	//***************//
	public double getArea()	
	{	
		computeFoV();
		return area;
	}
}
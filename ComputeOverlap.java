/** 
 * CheckFoV: overlapping and occlusion tool for visual sensors
 * Developed by Daniel G. Costa
 * Version October 2023
 * 
 * This class implement the functions to compute overlapping
*/

import java.math.BigDecimal;
import java.util.ArrayList;
import java.io.*;

class ComputeOverlap
{
    private String exportOverlapped = "./FileOverlapVertices.txt";
	
	private ArrayList<FoV> overlapping; //List of overlapping FoV areas
	private ArrayList<Vertex> intersects; //List of intersection vertices

	//***************//
    public ComputeOverlap()
    {
		overlapping = new ArrayList();
		intersects = new ArrayList();
    }

	//***************//
    public double selectRedundants(ArrayList<VSensor> sensors) 
	{
		//Run over the sensors
		int elements = sensors.size();
		
		for (int i=0; i < elements; i++)
		{
			//Current sensor to be considered
			VSensor current = sensors.get(i);  
				
			for (int j=(i+1); j < elements; j++)
			{
					VSensor toBeTested = sensors.get(j);
				
					if (current.getID() != toBeTested.getID())  //Sensors are different
					{
						//Check Euclidean distance
						if ((Math.abs(current.getRadius() + toBeTested.getRadius()) >= Math.abs(current.getFoV().getVerticeA().getX()- toBeTested.getFoV().getVerticeA().getX())) || (Math.abs(current.getRadius() + toBeTested.getRadius()) >= Math.abs(current.getFoV().getVerticeA().getY() - toBeTested.getFoV().getVerticeA().getY())))
						{
							///If this is evaluated as true, overlapping is possible
							FoV overlap = computeOverlapping (current, toBeTested); 
							
							if ((overlap != null) && (overlap.getVertices().size() > 0))
							{
								overlapping.add(orderOverlap(overlap));  //Lista com todas as areas com overlapping. Funcoes de print. Sentido horario
							}
						}
					}
				}
		}
		
		//Export all vertices of the overlapped areas
		try
		{
			File f = new File (exportOverlapped);
			if (!f.exists())
				f.createNewFile();

			FileWriter fw = new FileWriter (f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter (fw);
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i < overlapping.size(); i++)
			{				
				FoV o = overlapping.get(i);
				ArrayList<Vertex> vert = o.getVertices();
				buffer.append (vert.size() + "\n");
				
				for (int h=0; h < vert.size(); h++)
				{
					buffer.append (vert.get(h).getX() + "\n");
					buffer.append (vert.get(h).getY() + "\n");
				}				
			}
			bw.write (buffer.toString());
			bw.close();
		}
		catch (Exception exc)
		{
			System.err.println ("Error exporting overlapping vertices: " + exc.toString());
			exc.printStackTrace();
			System.exit(0);
		}

		//Return the cumulatively computed overallep areas
		double countAreas=0.0;
		for (int i=0; i < overlapping.size();i++)
		{
			countAreas += overlapping.get(i).getArea();
		}
		return countAreas;
	}

	//***************//
	//It is required for shoelace algorithm
	public FoV orderOverlap(FoV f)
	{
		FoV overlap = new FoV();
		
		PointVertex angles [] = new PointVertex[f.getVertices().size()];  
		
		//Compute reference point
		double countx=0, county=0;
		for (int i =0; i < f.getVertices().size(); i++)
		{
			countx += f.getVertices().get(i).getX();
			county += f.getVertices().get(i).getY();
			
		}
		
		//Inner point of the computed polygon
		Vertex first = new Vertex(new BigDecimal(countx/f.getVertices().size()), new BigDecimal(county/f.getVertices().size()));
	
		for (int i =0; i < angles.length; i++)
		{
			//Compute angles
			PointVertex ang = new PointVertex();
			double angle = computeAngle(first, f.getVertices().get(i));
			ang.insertAngle(angle);
			ang.insertVertex(f.getVertices().get(i));
			angles[i] = ang;
		}
				
		//Bubblesort of angles
		for (int c = 0; c < angles.length; c++) 
		{
        	for (int d = 0; d < angles.length; d++) 
        	{
      			if (angles[c].getAngle() > angles[d].getAngle()) /* For descending order use < */
        		{
					PointVertex swap = angles[d];
          			angles[d] = angles[c];
          			angles[c] = swap;
       			 }
      		}
		}
		
		for (int e = 0; e < angles.length; e++) 
		{
			overlap.insertVertex(angles[e].getVertex());
		}
	
		return overlap;
	}
	
	//***************//
	//Auxiliary method for clockwise computation
	public static double computeAngle(Vertex p1, Vertex p2) 
	{ 
		double xDiff = p2.getX() - p1.getX(); 
		double yDiff = p2.getY() - p1.getY(); 
				
		double angle = Math.atan2(yDiff, xDiff) * 180 / Math.PI;
		
		if (angle < 0)
			angle += 360;
		
		return angle;
	}
	
	//***************//
	public FoV computeOverlapping (VSensor s1, VSensor s2)
	{
		FoV overlap = new FoV(); //computed overlpping for visual sensors s1 and s2

		/*PART I - Overlapping based on lines intersections*/

		ArrayList<Vertex> v1, v2; //FoV vertices
		v1 = s1.getFoV().getVertices();
		v2 = s2.getFoV().getVertices();
	
		int j = v1.size()-1;
		for (int i =0; i < v1.size(); i++)
		{
			Vertex v1s1 = v1.get(j);
			Vertex v2s1 = v1.get(i);
			j = i;
			
			int w = v2.size()-1;
			for (int x =0; x < v2.size(); x++)
			{
				Vertex v1s2 = v2.get(w);
				Vertex v2s2 = v2.get(x);
				w = x;
				
				//Lines intersection
				Vertex v = lineIntersect(v1s1.getX(), v1s1.getY(), v2s1.getX(), v2s1.getY(), v1s2.getX(), v1s2.getY(), v2s2.getX(), v2s2.getY());
				if (v != null)
				{
					intersects.add(v);
					overlap.insertVertex(v);
				}
			} 
		}
		
		/*PART II - Innet points */
		
		//First triangle
		for (int i =0; i < v1.size(); i++)
		{
			Vertex vs1 = v1.get(i);
			if (pointPolygon(vs1, s2.getFoV()))
			{
				overlap.insertVertex(vs1);
				intersects.add(vs1);
			}
		}	
		
		//Second triangle
		for (int i =0; i < v2.size(); i++)
		{
			Vertex vs2 = v2.get(i);
			if (pointPolygon(vs2, s1.getFoV()))
			{
				overlap.insertVertex(vs2);
				intersects.add(vs2);
			}
		}
		
		return overlap;
	}
	
	//***************//
	//Ray_Casting algorithm
	public boolean pointPolygon(Vertex v, FoV f)
	{
		try
		{
	 		boolean oddNodes=false;
 		
	 		double x = v.getX();
			double y = v.getY();
		
			int j = f.getVertices().size() - 1;

			for (int i=0; i < f.getVertices().size(); i++) 
			{
    			if ((f.getVertices().get(i).getY() < y && f.getVertices().get(j).getY() >= y) ||  (f.getVertices().get(j).getY() < y && f.getVertices().get(i).getY() >= y)) 
    			{
    		
  	  				double valor = f.getVertices().get(i).getX() + (y - f.getVertices().get(i).getY())/(f.getVertices().get(j).getY() - f.getVertices().get(i).getY()) * (f.getVertices().get(j).getX() - f.getVertices().get(i).getX());
    	 			if (valor < x) 
     				{
        				oddNodes=!oddNodes; 
 	    	   		}
    	    	}
    			j=i; 
   		 	}
		
			return oddNodes;
		}
		catch (Exception exc)
		{
			System.err.println ("Point-Polygon error\n" + exc.toString());
			return false;
		}
	}
	
	//***************//
	//To compute the intersection point between 2 lines
	public Vertex lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) 
	{
		try
		{
	 		double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
  			if (denom == 0.0) 
  			{ // Lines are parallel.
     			return null;
 	 		}
  
  			double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
  			double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
    
	    	if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) 
    		{
        		// Get the intersection point.
        		return new Vertex(new BigDecimal(x1 + ua*(x2 - x1)), new BigDecimal(y1 + ua*(y2 - y1)));
   			}

  			return null;
  		}
		catch (Exception exc)
		{
			System.err.println ("Line-Intersect error\n" + exc.toString());
			return null;
		}
  	}
	
	//***************//
	//Only add a vertex it was not added before
	public void addSensor (ArrayList<VSensor> list, VSensor sens)
	{
		int id = sens.getID();
    
    	boolean test = true;
                    
        for (int f=0; f < list.size(); f++)
        {
             VSensor v = list.get(f);
             if (id == v.getID())
                  test = false;
        }
                    
    	if (test)
        {
             list.add (sens);
        }
	}
}
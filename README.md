# CheckFoV - Visual FoV Analysis Tool

CheckFoV is a Java-based tool designed to analyze the field of view (FoV) for visual sensors in a given deployment area. The tool employs a modularized object-oriented approach with several key classes that define different elements of the analysis.

## Key Classes

1. **Vertex**: Defines a generic (x, y) point in the field.

2. **FoV**: It is an area representing both the FoV triangle of a visual sensor (3 vertices) and also the overlapped FoV area (from 3 to 6 vertices). Actually, the same class is used to represent both concepts (original and overlapped FoV), since they are equally processed as polygons, ensuring flexibility to the tool.

3. **VSensor**: It defines the concept of visual sensor, which is composed of a FoV (comprising 3 vertices), a viewing angle, a radius, and an orientation. For visual sensors, the FoV is an isosceles triangle and the vertex A defines its position;

4. **Target**: Represents the elements to be viewed by the visual sensors. Targets are defined as points with viewing counters, providing flexibility for various monitoring scenarios, with this approach being common in the literature.

5. **ComputeOverlap**: It comprises all required mathematical equations and algorithms to compute FoV overlapping, implementing vertices clockwise ordering (required for the shoelace formula), auxiliary methods to process the vertices of polygons, and additional methods for consistency checking.

## Overlapping Complexity

Overlapping is mathematical complex due to some reasons. First, it depends on analytic geometry with computations of FoV's lines intersection and identification of vertices (points) inside polygonal areas. Second, since more than two visual sensors may overlap, with some areas being viewed by multiple sensors, the computation may be not only about triangles intersections, but virtually the intersections of many concave or convex polygons. In order to have a reference for the numerical computations, we assume that overlapped FoV areas are accounted for a pair of visual sensors, even if part of it was already covered by another sensor, resulting in a K-2 overlapping reference.

## Targets viewing

Target viewing is other important element of the tool. Since targets are processed as points, and all FoV are polygons, a target is assumed as viewed when it is inside at least one FoV area.

## Visual Results

The CheckFoV tool graphically presents results to users through two Java Swing panels:

1. **Draw Panel**: This panel exhibits visual sensors, their FoV, viewed (in red) and unviewed (in blue) targets, and overlapped areas. The graphical functionality is implemented in a separate code (DrawAllResults class) that reads the text files exported by the CheckFoV tool. This interaction through text files (`FileAllVertices.txt`, `FileTargets.txt`, and `FileOverlapVertices.txt`) allows the processing of computed data by any external tool.

2. **Results Panel**: This panel presents computed numerical results, which are exported to the `FileResults.txt` file. The computed results include the number of visual sensors, the number of targets, total FoV area, total overlapped FoV area, the number of viewed targets, the number of redundant viewed targets (targets viewed at least twice), and the number of target hits (cumulative number of times any target is viewed by any visual sensor).

## Getting Started

To get started with CheckFoV, you can download the latest release.

First, the code has to be compiled:

```bash
javac *.java
```
Then, after sucessful compilation, the tool can be executed in the following way:

```bash
java CheckFoV arg1 arg2 -v
```

In this execution:

**arg1** may by "0" (random configuration) or the path to a CSV file (Ax,Ay,Radius,Angle,Orientation). This is the configuration of visual sensors.

**arg2** may by "0" (random configuration) or the path to a CSV file (Tx,Ty). This is the configuration of targets.

The **-v** is the verbose mode (outputs in the standard output)

The configurations for random definitions can be set in the "randomConfigurations.csv" file (NumberSensors,NumberTargets,Width,Height,MinRadius,MaxRadius,MinAngle,MaxAngle).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

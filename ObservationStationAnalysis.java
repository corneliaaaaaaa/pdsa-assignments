import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.GrahamScan;

class ObservationStationAnalysis {
    private ArrayList<Point2D> stationList;
    private Point2D[] stationArray;
    private ArrayList<Point2D> convexHull;
    private GrahamScan g;
    private Point2D polar;

    public ObservationStationAnalysis(ArrayList<Point2D> stations) {
        // you can do something in Constructor or not
        stationList = new ArrayList<Point2D>(stations);
        stationArray = new Point2D[stations.size()];
        for (int i = 0; i < stations.size(); i++) {
            stationArray[i] = stationList.get(i);
        }

        g = new GrahamScan(stationArray);
        convexHull = new ArrayList<Point2D>();
        for (Point2D p : g.hull()) {
            convexHull.add(p);
        }
        polar = convexHull.get(0);
    }

    public Point2D[] findFarthestStations() {
        Point2D[] farthest = new Point2D[] { new Point2D(0, 0), new Point2D(1, 1) }; // Example
        double max = -1;
        // find the farthest two stations
        for (int i = 0; i < convexHull.size(); i++) {
            for (int j = i; j < convexHull.size(); j++) {
                Point2D p1 = convexHull.get(i);
                Point2D p2 = convexHull.get(j);

                double distance = p1.distanceTo(p2);
                if (max < distance) {
                    max = distance;
                    farthest = new Point2D[] { p1, p2 };
                }
            }
        }
        Arrays.sort(farthest, Point2D.R_ORDER);

        return farthest; // it should be sorted (ascendingly) by polar radius; please sort (ascendingly)
                         // by y coordinate if there are ties in polar radius.
    }

    public double coverageArea() {
        double area = 0.0;
        // calculate the area surrounded by the existing stations
        // use triangle in order to compute the size
        Point2D prev = convexHull.get(1);
        for (int i = 2; i < convexHull.size(); i++) {
            Point2D cur = convexHull.get(i);
            area += Math.abs(Point2D.area2(polar, prev, cur));
            prev = cur;
        }

        area /= 2;

        return area;
    }

    public void addNewStation(Point2D newStation) {
        stationList.add(newStation);

        // copy convexHull + new to find the updated convex hull
        ArrayList<Point2D> tmp = new ArrayList<Point2D>();
        for (Point2D p : convexHull) {
            tmp.add(p);
        }
        tmp.add(newStation);

        // reset convex hull
        convexHull = new ArrayList<Point2D>();

        // find polar
        Collections.sort(tmp, Point2D.Y_ORDER);
        polar = tmp.get(0);

        // sort by radius
        Collections.sort(tmp, polar.polarOrder());

        // form the new convex hull
        convexHull.add(tmp.get(0));
        convexHull.add(tmp.get(1));
        for (int i = 2; i < tmp.size(); i++) {
            Point2D p = tmp.get(i);
            // add and check
            while (true) {
                Point2D first = convexHull.get(convexHull.size() - 2);
                Point2D mid = convexHull.get(convexHull.size() - 1);
                if (Point2D.ccw(first, mid, p) >= 0) {
                    convexHull.add(p);
                    break;
                } else {
                    convexHull.remove(convexHull.size() - 1);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {

        ArrayList<Point2D> stationCoordinates = new ArrayList<>();
        stationCoordinates.add(new Point2D(0, 0));
        stationCoordinates.add(new Point2D(2, 0));
        stationCoordinates.add(new Point2D(3, 2));
        stationCoordinates.add(new Point2D(2, 6));
        stationCoordinates.add(new Point2D(0, 4));
        stationCoordinates.add(new Point2D(1, 1));
        stationCoordinates.add(new Point2D(2, 2));

        ObservationStationAnalysis Analysis = new ObservationStationAnalysis(stationCoordinates);
        System.out.println("Farthest Station A: " + Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: " + Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: " + Analysis.coverageArea());

        System.out.println("Add Station (10, 3): ");
        Analysis.addNewStation(new Point2D(10, 3));

        System.out.println("Farthest Station A: " + Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: " + Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: " + Analysis.coverageArea());
    }
}
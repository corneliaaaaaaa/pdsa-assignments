import java.util.*;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.PrimMST;

class LabNetworkCabling {
    PrimMST mst;
    int vertexCount = 0;
    Map<Integer, String> dt;
    EdgeWeightedGraph g;
    EdgeWeightedGraph a;
    int serverIndex = -1;
    int routerIndex = -1;    
    ArrayList<Integer> printerIndices = new ArrayList<>();
    ArrayList<Integer> computerIndices = new ArrayList<>();
    boolean LOG = false;

    public LabNetworkCabling(Map<Integer, String> deviceTypes, List<int[]> links){
        // create a Minimum Spanning Tree
        
        // initialize
        vertexCount = deviceTypes.size();
        dt = deviceTypes;
        for (Map.Entry<Integer, String> entry : dt.entrySet()) {
            switch (entry.getValue()) {
                case "Server" -> serverIndex = entry.getKey();
                case "Router" -> routerIndex = entry.getKey();
                case "Computer" -> computerIndices.add(entry.getKey());
                case "Printer" -> printerIndices.add(entry.getKey());
            }
        }
        
        a = new EdgeWeightedGraph(vertexCount);
        for (int[] edge: links){
            a.addEdge(new Edge(edge[0], edge[1], edge[2]));
        }
        mst = new PrimMST(a);
    };
    
    public int cablingCost() {
        // calculate the total cost
        int cost = 0;

        for (Edge e: mst.edges()){
            cost += e.weight();
        }
        return cost;
    }

    public int serverToRouter(){
        // find the path distance between the server and the router
        
        // initialize graph and indices to facilitate dfs
        g = new EdgeWeightedGraph(vertexCount);
        for (Edge e: mst.edges()){
            g.addEdge(e);
        }

        DijkstraUndirectedSP sp = new DijkstraUndirectedSP(g, serverIndex);
        
        return sp.distTo(routerIndex);
        
    }


    public int mostPopularPrinter(){
        // find the most popular printer and return its index

        // initialize
        int[] printerUsage = new int[vertexCount];
        Arrays.fill(printerUsage, 0);

        // for each printer, compute its nearest distance to all computers
        int[] minD = new int[vertexCount];
        int[] selectedPrinter = new int[vertexCount];
        Arrays.fill(minD, Integer.MAX_VALUE);
        Arrays.fill(selectedPrinter, -1);
        for (int p: printerIndices){
            DijkstraUndirectedSP sp = new DijkstraUndirectedSP(g, p);
            for (int c: computerIndices){
                int currentMin = sp.distTo(c);
                if (minD[c] > currentMin){
                    minD[c] = currentMin;
                    selectedPrinter[c] = p;
                } else if (minD[c] == currentMin && p < selectedPrinter[c]){
                    selectedPrinter[c] = p;
                }
            }
        }

        // compute selected printer
        for (int c: computerIndices){
            printerUsage[selectedPrinter[c]] += 1;
        }

        int maxUsagePrinter = 0;
        int maxUsage = printerUsage[0];
        for (int i = 1; i < printerUsage.length; i++) {
            if (printerUsage[i] > maxUsage) {
                maxUsage = printerUsage[i];
                maxUsagePrinter = i;
            }
        }

        return maxUsagePrinter;
    }

    public static void main(String[] args) {
        
        // [device index, device type]
        Map<Integer, String> deviceTypes = Map.of(
            0, "Router",
            1, "Server",
            2, "Printer",
            3, "Printer",
            4, "Computer",
            5, "Computer",
            6, "Computer"
        );

        // [device a, device b, link distance (cable length)]
        List<int[]> links = List.of(
                    new int[]{0, 1, 4},
                    new int[]{1, 2, 2},
                    new int[]{2, 4, 1},
                    new int[]{0, 3, 3},
                    new int[]{1, 3, 8},
                    new int[]{3, 5, 7},
                    new int[]{3, 6, 9},
                    new int[]{0, 6, 5}
                );

        LabNetworkCabling Network = new LabNetworkCabling(deviceTypes, links);
        System.out.println("Total Cabling Cost: " + Network.cablingCost());
        System.out.println("Distance between Server and Router: " + Network.serverToRouter());
        System.out.println("Most Popular Printer: " + Network.mostPopularPrinter());
    }
}


class DijkstraUndirectedSP {
    private int[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private final PriorityQueue<Node> pq;
    private final boolean[] marked;

    private static class Node {
        int vertex;
        int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    public DijkstraUndirectedSP(EdgeWeightedGraph G, int s) {
        distTo = new int[G.V()];
        edgeTo = new Edge[G.V()];
        marked = new boolean[G.V()];
        
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[s] = 0;
        
        pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        pq.add(new Node(s, 0));

        while (!pq.isEmpty()) {
            int v = pq.poll().vertex;
            if (!marked[v]) {
                marked[v] = true;
                for (Edge e : G.adj(v)) {
                    relax(e, v);
                }
            }
        }
    }

    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + (int) e.weight()) {
            distTo[w] = distTo[v] + (int) e.weight();
            pq.add(new Node(w, distTo[w]));
        }
    }

    public int distTo(int v) {
        // validateVertex(v);
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v) {
        Stack<Edge> path = new Stack<Edge>();
        int x = v;
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        return path;
    }

    public static void main(String[] args) {
    }

}

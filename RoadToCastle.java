import java.util.*;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;
// import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import java.util.List;
import edu.princeton.cs.algs4.Stack;

class RoadToCastle {
    private EdgeWeightedDigraph g;
    private int col_count = 0;
    private DijkstraSP sp;
    private Iterable<DirectedEdge> pathToTarget;
    private List<int[]> result;

    public List<int[]> shortest_path() {
        result = new ArrayList<>();
        int i = 0;
        for (DirectedEdge e : pathToTarget) {
            // System.out.println("edge   " + indexToPosition(e.from())[0] + " " + indexToPosition(e.from())[1] + "   " + indexToPosition(e.to())[0] + "  "+ indexToPosition(e.to())[1]);
            if (i == 0) {
                result.add(indexToPosition(e.from()));
                result.add(indexToPosition(e.to()));
            } else {
                result.add(indexToPosition(e.to()));
            }
            i++;
        }
        
        return result;
    }

    public int shortest_path_len() {
        int length = 0;
        for (DirectedEdge e : pathToTarget) {
            length += e.weight();
        }
        
        return length;
    }

    private int positionToIndex(int row, int col) {
        return (row - 1) * col_count + col - 1;
    }

    private int[] indexToPosition(int id) {
        return new int[] { id / col_count + 1, id % col_count + 1}; 
    }

    public RoadToCastle(int[][] map, int[] init_pos, int[] target_pos) {
        // build graph
        int vertex_count = (map.length - 2) * (map[0].length - 2);
        col_count = map[0].length - 2;
        g = new EdgeWeightedDigraph(vertex_count);

        int[] dr = {0, 0, -1, 1};
        int[] dc = {-1, 1, 0, 0};
        for (int i = 1; i < map.length - 1; i++) {
            for (int j = 1; j < map[0].length - 1; j++) {
                if (map[i][j] == 0) {
                    continue;
                }
                int fromIndex = positionToIndex(i, j);
                for (int k = 0; k < 4; k++) {
                    int row = i + dr[k];
                    int col = j + dc[k];
                    if (map[row][col] == 0) {
                        continue;
                    }
                    int weight = -1;
                    if (map[row][col] == 3) {
                        weight = 5;
                    } else {
                        weight = 1;
                    }

                    if (map[row][col] == 3 && map[i][j] == 3) {
                        weight = 6;
                    }

                    // add edge
                    int toIndex = positionToIndex(row, col);
                    DirectedEdge e = new DirectedEdge(fromIndex, toIndex, weight);
                    g.addEdge(e);
                }
            }
        }
        
        // find sp
        int s = positionToIndex(init_pos[0], init_pos[1]);
        // System.out.println("s " + s);
        sp = new DijkstraSP(g, s);
        int target = positionToIndex(target_pos[0], target_pos[1]);
        pathToTarget = sp.pathTo(target);
    }
    public static void main(String[] args) {
        RoadToCastle sol = new RoadToCastle(new int[][]{
                        {0,0,0,0,0},
                        {0,2,3,2,0},  //map[1][2]=3
                        {0,2,0,2,0},
                        {0,2,0,2,0},
                        {0,2,2,2,0},
                        {0,0,0,0,0}
                },
                new int[]{1,1},
                new int[]{1,3}
        );
        System.out.println(sol.shortest_path_len());
        List<int[]> path = sol.shortest_path();
        for(int[] coor : path)
            System.out.println("x: "+Integer.toString(coor[0]) + " y: "+Integer.toString(coor[1]));

        //ans: best_path:{{1, 1}, {1, 2}, {1, 3}}
        //Path 1 (the best): [1, 1] [1, 2] [1, 3] -> 0+5+1 = 6, cost to reach init_pos is zero!
        //Path 2 (example of other paths): [1, 1] [2, 1] [3, 1] [4, 1] [4, 2] [4, 3] [3, 3] [2, 3] [1, 3] -> 8
    }
}

class DijkstraSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private DirectedEdge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    public DijkstraSP(EdgeWeightedDigraph G, int s) {
        
        distTo = new double[G.V()];
        edgeTo = new DirectedEdge[G.V()];


        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (DirectedEdge e : G.adj(v))
                relax(e);
        }

    }

    // relax edge e and update pq if changed
    private void relax(DirectedEdge e) {
        int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /**
     * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges, and {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<DirectedEdge> pathTo(int v) {
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.push(e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(EdgeWeightedDigraph G, int s) {

        // check that edge weights are non-negative
        for (DirectedEdge e : G.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                int w = e.to();
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            DirectedEdge e = edgeTo[w];
            int v = e.from();
            if (w != e.to()) return false;
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code DijkstraSP} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        
    }

}
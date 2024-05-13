import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.*;
import edu.princeton.cs.algs4.StdDraw;
import java.util.LinkedList;
import java.util.*;
import java.util.Queue;
import edu.princeton.cs.algs4.*;

class IntervalST<Key extends Comparable<Key>, Value> {
    private Node root;

    private class Node {
        private Key lo, hi, max;
        private Value val;
        private int size;
        private Node left, right;

        public Node(Key lo, Key hi, Value val) {
            // initializes the node if required.
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            // this.size = 1;
            this.max = hi;
        }
    }

    public IntervalST() {
        // initializes the tree if required.
    }

    public int size(Node x) {
        if (x == null) {
            return 0;
        }
        return x.size;
    }

    public void put(Key lo, Key hi, Value val) {
        root = put(root, lo, hi, val);
        // System.out.println("after put");
        // printRecursive(root);
    }

    public void printRecursive(Node cur) {
        Queue<Node> s = new LinkedList<>();
        s.offer(cur);

        while (!s.isEmpty()) {
            Node n = s.poll();
            if (n != null) {
                System.out.println(n.val + " " + n.lo + " " + n.hi + " " + n.max);

            } else {
                System.out.println("n is null");
            }
            if (n != null && n.left != null) {
                s.offer(n.left);
            }
            if (n != null && n.right != null) {
                s.offer(n.right);
            }
        }
        System.out.println("================================");

    }

    private Node put(Node cur, Key lo, Key hi, Value val) {
        if (cur == null) {
            return new Node(lo, hi, val);
        }

        int cmpLo = lo.compareTo(cur.lo);
        int cmpHi = hi.compareTo(cur.hi);

        if (cmpLo < 0) {
            cur.left = put(cur.left, lo, hi, val);
        } else if (cmpLo > 0) {
            cur.right = put(cur.right, lo, hi, val);
        } else {
            if (cmpHi < 0) {
                cur.left = put(cur.left, lo, hi, val);
            } else if (cmpHi > 0) {
                cur.right = put(cur.right, lo, hi, val);
            } else {
                cur.val = val;
            }
        }

        cur.max = maxOf(cur.max, hi);

        return cur;
    }

    private Key maxOf(Key a, Key b) {
        return (a.compareTo(b) >= 0) ? a : b;
    }

    public void delete(Key lo, Key hi) {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        root = delete(root, lo, hi);
        // System.out.println("after delete");
        // printRecursive(root);
    }

    private Node delete(Node cur, Key lo, Key hi) {
        if (cur == null) {
            return null;
        }

        int cmpLo = lo.compareTo(cur.lo);
        int cmpHi = hi.compareTo(cur.hi);

        if (cmpLo < 0) {
            cur.left = delete(cur.left, lo, hi);
        } else if (cmpLo > 0) {
            cur.right = delete(cur.right, lo, hi);
        } else {
            if (cmpHi < 0) {
                cur.left = delete(cur.left, lo, hi);
            } else if (cmpHi > 0) {
                cur.right = delete(cur.right, lo, hi);
            } else {
                // delete and restructure
                if (cur.left == null) {
                    return cur.right;
                } else if (cur.right == null) {
                    return cur.left;
                }

                Node toDelete = cur;
                cur = findMin(toDelete.right);
                cur.right = deleteMin(toDelete.right); // the smallest node in the toDelete right tree need to be the
                                                       // parent
                cur.left = toDelete.left;
            }
        }
        // cur.size = size(cur.left) + size(cur.right) + 1;
        if (cur.right != null && cur.left != null) {
            if (cur.left.max.compareTo(cur.right.max) >= 0) {
                cur.max = maxOf(cur.max, cur.left.max);

            } else if (cur.left.max.compareTo(cur.right.max) < 0) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        } else {
            if (cur.right == null && cur.left == null) {
                cur.max = cur.hi;
            } else if (cur.right == null) {
                cur.max = maxOf(cur.max, cur.left.max);
            } else if (cur.left == null) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        }
        return cur;
    }

    private Node findMin(Node cur) {
        if (cur == null) {
            return null;
        }

        while (cur.left != null) {
            cur = cur.left;
        }
        return cur;
    }

    public void deleteMin() {
        root = deleteMin(root);
        // System.out.println("after deletion");
        // printRecursive(root);
    }

    private Node deleteMin(Node cur) {
        // delete the min node in the subtree and
        // replace the original position with the right child of the deleted node
        if (cur.left == null) {
            return cur.right;
        }
        cur.left = deleteMin(cur.left);
        // cur.size = 1 + size(cur.left) + size(cur.right);
        if (cur.right != null && cur.left != null) {
            if (cur.left.max.compareTo(cur.right.max) >= 0) {
                cur.max = maxOf(cur.max, cur.left.max);
            } else if (cur.left.max.compareTo(cur.right.max) < 0) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        } else {
            if (cur.right == null && cur.left == null) {
                cur.max = cur.hi;
            } else if (cur.right == null) {
                cur.max = maxOf(cur.max, cur.left.max);
            } else if (cur.left == null) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        }
        return cur;
    }

    public List<Value> intersects(Key lo, Key hi) {
        List<Value> result = new ArrayList<>();
        searchAll(lo, hi, result);
        return result;
    }

    private void searchAll(Key lo, Key hi, List<Value> result) {
        if (root == null) {
            return;
        }

        Queue<Node> nodes = new ArrayDeque<>();
        nodes.offer(root);

        while (!nodes.isEmpty()) {
            Node cur = nodes.poll();

            if (cur.max.compareTo(lo) < 0) {
                continue;
            }

            if (cur.lo.compareTo(hi) <= 0 && cur.hi.compareTo(lo) >= 0) {
                result.add(cur.val);
            }

            if (cur.left != null && cur.left.max.compareTo(lo) >= 0) {
                nodes.offer(cur.left);
            }

            if (cur.right != null && cur.lo.compareTo(hi) <= 0) {
                nodes.offer(cur.right);
            }
        }
    }

    public static void main(String[] args) {
        // Example
        IntervalST<Integer, String> IST = new IntervalST<>();
        IST.put(2, 5, "badminton");
        IST.put(1, 5, "PDSA HW7");
        IST.put(3, 5, "Lunch");
        IST.put(3, 6, "Workout");
        IST.put(3, 7, "Do nothing");
        IST.delete(2, 5); // delete "badminton"
        System.out.println(IST.intersects(1, 2));

        IST.put(8, 8, "Dinner");
        System.out.println(IST.intersects(6, 10));

        IST.put(3, 7, "Do something"); // If an interval is identical to an existing node, then the value of that node
                                       // is updated accordingly
        System.out.println(IST.intersects(7, 7));

        IST.delete(3, 7); // delete "Do something"
        System.out.println(IST.intersects(7, 7));
    }
}

class ImageMerge {
    private double[][] mergedBoxes;

    public double[][] mergeBox() {
        Arrays.sort(mergedBoxes, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                // Compare first element
                int cmp = Double.compare(o1[0], o2[0]);
                if (cmp != 0) {
                    return cmp;
                }
                // If first element is equal, compare second element
                cmp = Double.compare(o1[1], o2[1]);
                if (cmp != 0) {
                    return cmp;
                }
                // If first and second elements are equal, compare third element
                cmp = Double.compare(o1[2], o2[2]);
                if (cmp != 0) {
                    return cmp;
                }
                // If first, second, and third elements are equal, compare fourth element
                return Double.compare(o1[3], o2[3]);
            }
        });
        return mergedBoxes;
    }

    private double iou(double x1a, double y1a, double x2a, double y2a, double x1b, double y1b, double x2b, double y2b) {
        double areaA = (x2a - x1a) * (y2a - y1a);
        double areaB = (x2b - x1b) * (y2b - y1b);

        double x1Intersection = Math.max(x1a, x1b);
        double y1Intersection = Math.max(y1a, y1b);
        double x2Intersection = Math.min(x2a, x2b);
        double y2Intersection = Math.min(y2a, y2b);

        double intersectionArea = Math.max(0, x2Intersection - x1Intersection)
                * Math.max(0, y2Intersection - y1Intersection);
        double unionArea = areaA + areaB - intersectionArea;

        double iou = intersectionArea / unionArea;

        return iou;
    }

    public ImageMerge(double[][] bbs, double iou_thresh) {
        // implement sweep-line algorithm

        // create events based on x-coordinates
        List<double[]> events = new ArrayList<>();
        for (int i = 0; i < bbs.length; i++) {
            events.add(new double[] { bbs[i][0], i, 0 }); // {x-coordinate, index, start}
            events.add(new double[] { bbs[i][0] + bbs[i][2], i, 1 }); // {x-coordinate, index, end}
        }
        events.sort(Comparator.comparingDouble(a -> a[0]));

        // search for overlaps
        IntervalST<Double, Integer> ist = new IntervalST<>(); // stores y intervals
        UF uf = new UF(bbs.length); // stores overlapping relationships
        for (double[] x : events) {
            int index = (int) x[1];

            if (x[2] == 0) {
                List<Integer> overlaps = ist.intersects(bbs[index][1], bbs[index][1] + bbs[index][3]);
                for (Integer o : overlaps) {
                    // check if iou >= threshold
                    double x1a = bbs[o][0];
                    double y1a = bbs[o][1];
                    double x2a = bbs[o][0] + bbs[o][2];
                    double y2a = bbs[o][1] + bbs[o][3];
                    double x1b = bbs[index][0];
                    double y1b = bbs[index][1];
                    double x2b = bbs[index][0] + bbs[index][2];
                    double y2b = bbs[index][1] + bbs[index][3];
                    double iou = iou(x1a, y1a, x2a, y2a, x1b, y1b, x2b, y2b);
                    if (iou >= iou_thresh) {
                        if (uf.find(o) != uf.find(index)) {
                            uf.union(o, index);
                        }
                    }
                }
                ist.put(bbs[index][1], bbs[index][1] + bbs[index][3], index); // index of each rectangle will be used in
                                                                              // union find
            } else if (x[2] == 1) {
                ist.delete(bbs[index][1], bbs[index][1] + bbs[index][3]);
            }
        }

        Map<Integer, List<Integer>> parentToKids = new HashMap<>();
        // find connected components
        for (int i = 0; i < bbs.length; i++) {
            addValue(parentToKids, uf.find(i), i);
        }

        // find merged boxes
        mergedBoxes = new double[parentToKids.size()][4];
        int i = 0;
        for (Map.Entry<Integer, List<Integer>> entry : parentToKids.entrySet()) {
            List<Integer> values = entry.getValue();

            List<Double> leftX = new ArrayList<>();
            List<Double> upY = new ArrayList<>();
            List<Double> rightX = new ArrayList<>();
            List<Double> downY = new ArrayList<>();
            for (Integer v : values) {
                // v is index in bbs[][]
                leftX.add(bbs[v][0]);
                upY.add(bbs[v][1]);
                rightX.add(bbs[v][0] + bbs[v][2]);
                downY.add(bbs[v][1] + bbs[v][3]);
            }

            double[] mb = new double[] { Collections.min(leftX), Collections.min(upY),
                    Collections.max(rightX) - Collections.min(leftX), Collections.max(downY) - Collections.min(upY) };
            mergedBoxes[i] = mb;
            i++;
        }

    }

    private static void addValue(Map<Integer, List<Integer>> dictionary, Integer key, int value) {
        // If the key doesn't exist, create a new List and put it into the map
        dictionary.putIfAbsent(key, new ArrayList<>());
        // Add the value to the List associated with the key
        dictionary.get(key).add(value);
    }

    public static void draw(double[][] bbs) {
        // ** NO NEED TO MODIFY THIS FUNCTION, WE WON'T CALL THIS **
        // ** DEBUG ONLY, USE THIS FUNCTION TO DRAW THE BOX OUT**
        StdDraw.setCanvasSize(960, 540);
        for (double[] box : bbs) {
            double half_width = (box[2] / 2.0);
            double half_height = (box[3] / 2.0);
            double center_x = box[0] + half_width;
            double center_y = box[1] + half_height;
            // StdDraw use y = 0 at the bottom, 1-center_y to flip
            StdDraw.rectangle(center_x, 1 - center_y, half_width, half_height);
        }
    }

    public static void main(String[] args) {
        ImageMerge sol = new ImageMerge(
                // (x, y, w, h)
                new double[][] {
                        { 0.02, 0.01, 0.1, 0.05 }, { 0.0, 0.0, 0.1, 0.05 }, { 0.04, 0.02, 0.1, 0.05 },
                        { 0.06, 0.03, 0.1, 0.05 }, { 0.08, 0.04, 0.1, 0.05 },
                        { 0.24, 0.01, 0.1, 0.05 }, { 0.20, 0.0, 0.1, 0.05 }, { 0.28, 0.02, 0.1, 0.05 },
                        { 0.32, 0.03, 0.1, 0.05 }, { 0.36, 0.04, 0.1, 0.05 },
                },
                0.5);
        double[][] temp = sol.mergeBox();
        ImageMerge.draw(temp);
    }
}

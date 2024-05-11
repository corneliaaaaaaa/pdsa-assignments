import java.util.*;

class ImageSegmentation {

    private int segmentCount;
    private int largestColor;
    private int largestSize;
    private int[][] image;
    private boolean[][] visited;
    private int[] dx = { 0, 0, 1, -1 }; // represent up, down, right, left
    private int[] dy = { 1, -1, 0, 0 };

    public ImageSegmentation(int N, int[][] inputImage) {
        // Initialize a N-by-N image
        segmentCount = 0;
        largestColor = -1;
        largestSize = 0;
        image = new int[N][N];
        visited = new boolean[N][N];

        for (int i = 0; i < N; i++) {
            System.arraycopy(inputImage[i], 0, image[i], 0, N);
            Arrays.fill(visited[i], false);
        }

    }

    public int countDistinctSegments() {
        // Count the number of distinct segments in the image.
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (!visited[i][j] && image[i][j] != 0) {
                    bfs(i, j);
                    segmentCount++;
                }
            }
        }

        return segmentCount;
    }

    public int[] findLargestSegment() {
        // Find the largest connected segment and return an array
        // containing the number of pixels and the color of the segment.
        return new int[] { largestSize, largestColor };
    }

    // private object mergeSegment (object XXX, ...){
    // Maybe you can use user-defined function to
    // facilitate you implement mergeSegment method.
    // }
    private void bfs(int x, int y) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { x, y });
        visited[x][y] = true;
        int curSize = 0;

        // will stop until all neighbor elements are visited
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            curSize++;
            for (int i = 0; i < 4; i++) {
                int targetX = cur[0] + dx[i];
                int targetY = cur[1] + dy[i];
                if (isValid(targetX, targetY) && !visited[targetX][targetY] &&
                        image[targetX][targetY] == image[x][y]) {
                    queue.offer(new int[] { targetX, targetY });
                    visited[targetX][targetY] = true;
                }
            }
        }

        if (curSize > largestSize || (curSize == largestSize && image[x][y] < largestColor)) {
            largestSize = curSize;
            largestColor = image[x][y];
        }
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < image.length && y >= 0 && y < image[0].length;
    }

    public static void main(String args[]) {

        // Example 1:
        int[][] inputImage1 = {
                { 0, 0, 0 },
                { 0, 1, 1 },
                { 0, 0, 1 }
        };

        System.out.println("Example 1:");

        ImageSegmentation s = new ImageSegmentation(3, inputImage1);
        System.out.println("Number of Distinct Segments: " +
                s.countDistinctSegments());

        int[] largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);

        // Example 2:
        int[][] inputImage2 = {
                { 0, 0, 0, 3, 0 },
                { 0, 2, 3, 3, 0 },
                { 1, 2, 2, 0, 0 },
                { 1, 2, 2, 1, 1 },
                { 0, 0, 1, 1, 1 }
        };

        System.out.println("\nExample 2:");

        s = new ImageSegmentation(5, inputImage2);
        System.out.println("Number of Distinct Segments: " +
                s.countDistinctSegments());

        largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);

    }

}
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

import edu.princeton.cs.algs4.Quick;

import com.google.gson.*;

class Exam {
    public static List<int[]> getPassedList(Integer[][] scores) {
        double targetRatio = 0.2;
        int topN = (int) Math.ceil(targetRatio * scores[0].length);

        int subjectCount = scores.length;
        int targetValue = -1;
        boolean[] studentIncluded = new boolean[scores[0].length];
        int[] studentTotalScore = new int[scores[0].length];
        Integer[] tmp = new Integer[scores[0].length];

        // initialize
        for (int i = 0; i < studentTotalScore.length; i++) {
            studentTotalScore[i] = 0;
            studentIncluded[i] = true;
        }

        // find top students in each subject and get the result list of id and scores
        for (int i = 0; i < subjectCount; i++) {
            System.arraycopy(scores[i], 0, tmp, 0, scores[0].length);
            targetValue = (int) Quick.select(tmp, tmp.length - topN);

            // find students to include
            for (int j = 0; j < scores[0].length; j++) {
                if (studentIncluded[j] && scores[i][j] >= targetValue) {
                    studentTotalScore[j] += scores[i][j];
                } else {
                    studentIncluded[j] = false;
                }
            }
        }
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < studentIncluded.length; i++) {
            if (studentIncluded[i]) {
                int[] item = new int[2];
                item[0] = i;
                item[1] = studentTotalScore[item[0]];
                result.add(item);
            }
        }
        ;
        Collections.sort(result, new Comparator<int[]>() {
            @Override
            public int compare(int[] arr1, int[] arr2) {
                // Compare the second elements in reverse order
                return Integer.compare(arr2[1], arr1[1]);
            }
        });

        return result;
    }

    public static void main(String[] args) {
        List<int[]> ans = getPassedList(new Integer[][] {
                // ID:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                { 67, 82, 98, 32, 65, 76, 87, 12, 43, 75, 25 },
                { 42, 90, 80, 12, 76, 58, 95, 30, 67, 78, 10 }
        });
        for (int[] student : ans)
            System.out.print(Arrays.toString(student));
        // 11 students * 0.2 = 2.2 -> Top 3 students
        // Output -> [6, 182][2, 178][1, 172]

        System.out.println(); // For typesetting

        ans = getPassedList(new Integer[][] {
                // ID:[0, 1, 2, 3, 4, 5]
                { 67, 82, 64, 32, 65, 76 },
                { 42, 90, 80, 12, 76, 58 }
        });
        for (int[] student : ans)
            System.out.print(Arrays.toString(student));
        // 6 students * 0.2 = 1.2 -> Top 2 students
        // Output -> [1, 172]
    }
}

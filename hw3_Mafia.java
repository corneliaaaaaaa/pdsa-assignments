import java.util.Arrays; // Used to print the arrays

class member {
    int Level;
    int Range;
    int Index;

    member(int _level, int _range, int i) {
        Level = _level;
        Range = _range;
        Index = i;
    }
}

class Mafia {
    private int[][] attackPair;

    public int[] result(int[] levels, int[] ranges) {
        // Given the traits of each member and output
        // the leftmost and rightmost index of member
        // can be attacked by each member.
        // long startTime = System.nanoTime();
        // initialize attackPair
        attackPair = new int[levels.length][2];
        for (int i = 0; i < levels.length; i++) {
            attackPair[i][0] = -1;
            attackPair[i][1] = -1;
        }
        // find borders
        for (int i = 0; i < levels.length; i++) {
            attackPair[i][0] = AttackBorder(levels, ranges, i, true);
        }
        for (int i = levels.length - 1; i > -1; i--) {
            attackPair[i][1] = AttackBorder(levels, ranges, i, false);
        }

        // flatten attackPair
        int[] ans = new int[levels.length * 2];
        for (int i = 0; i < levels.length; i++) {
            ans[i * 2] = attackPair[i][0];
            ans[i * 2 + 1] = attackPair[i][1];
        }

        return ans;
        // complete the code by returning an int[]
        // flatten the results since we only need an 1-dimentional array.
    }

    private int AttackBorder(int[] levels, int[] ranges, int currentIndex,
            boolean lower) {
        int colIndex = 1;
        int step = 1;
        if (lower == true) {
            colIndex = 0;
            step = -1;
        }
        int range = ranges[currentIndex];

        // find border
        int attackTarget = currentIndex + step;
        int attackBorder = currentIndex;
        while (true) {
            if (range == 0) {
                break;
            } else if (lower == true
                    && (attackTarget < 0 || attackBorder == 0 || attackBorder == currentIndex - range)) {
                break;
            } else if (lower == false && (attackTarget > levels.length - 1 ||
                    attackBorder == levels.length - 1 || attackBorder == currentIndex + range)) {
                break;
            }

            if (Math.abs(attackTarget - currentIndex) <= range && levels[attackTarget] < levels[currentIndex]) {
                attackBorder = attackTarget;

                int jumpTo = attackPair[attackTarget][colIndex];
                if (jumpTo != -1 && jumpTo != attackTarget) {
                    if (Math.abs(jumpTo - currentIndex) <= range) {
                        // can directly jump there
                        attackTarget = jumpTo;
                    } else {
                        if (lower == true) {
                            attackTarget = currentIndex - range;
                        } else {
                            attackTarget = currentIndex + range;
                        }
                    }
                } else {
                    attackTarget += step;
                }
            } else {
                break;
            }
        }

        return attackBorder;

    }

    public static void main(String[] args) {
        Mafia sol = new Mafia();
        System.out.println(Arrays.toString(
                sol.result(new int[] { 11, 13, 11, 7, 15 },
                        new int[] { 1, 8, 1, 7, 2 })));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        // => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}

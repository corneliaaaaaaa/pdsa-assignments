import java.util.*;

class RPG {
    private int[] defence;
    private int[] attack;
    private int[] damage; // -1 represents not visited

    public RPG(int[] de, int[] at) {
        // Initialize some variables
        defence = new int[de.length + 1];
        attack = new int[de.length + 1];
        damage = new int[de.length + 1];

        for (int i = 0; i < de.length; i++) {
            defence[i + 1] = de[i];
            attack[i + 1] = at[i];
            damage[i + 1] = -1;
        }
        // System.arraycopy(de, 0, defence, 1, de.length);
        // System.arraycopy(at, 0, attack, 1, at.length);
        // Arrays.fill(damage, -1);
        defence[0] = 0;
        attack[0] = 0;
        damage[0] = 0;
        damage[1] = attack[1] - defence[1];
    }

    public int maxDamage(int n) {
        // return the highest total damage after n rounds.
        if (damage[n] != -1) {
            return damage[n];
        }

        if (n <= 1) {
            return damage[n];
        } else {
            int attackDamage = attack[n] - defence[n] + maxDamage(n - 1);
            int boostDamage = 2 * attack[n] - defence[n] + maxDamage(n - 2);
            damage[n] = Math.max(attackDamage, boostDamage);
            return damage[n];
        }
    }

    public static void main(String[] args) {
        RPG sol = new RPG(new int[] { 5, 4, 1, 7, 98, 2 }, new int[] { 200, 200, 200, 200, 200, 200 });
        System.out.println(sol.maxDamage(6));
        // 1: boost, 2: attack, 3: boost, 4: attack, 5: boost, 6: attack
        // maxDamage: 1187
    }
}

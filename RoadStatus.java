import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;

class RoadStatus {
    private int currentTime = -1;
    private int duration = 0;
    private int[] carNumber = { 0, 0, 0 };
    private int currentGreen = -1; // -1 means all red, 0 / 1 / 2 represents the road id that has green light
    private int nextGreen = -1;
    private int nextDuration = 0;
    private boolean specialOrder = false;

    public RoadStatus() {
    }

    public int[] roadStatus(int time) {
        if (currentTime != time) {
            while (currentTime != time) {
                if (specialOrder && duration == 0) {
                    determineNextGreenLight();
                    specialOrder = false;
                }
                // 0. update time and green light
                updateTimeAndGreenLight();

                // 1. exit only if a road light is green
                if (currentGreen != -1 && carNumber[currentGreen] > 0) {
                    carNumber[currentGreen] -= 1;
                }

                // 2. determine green light status for the future only if duration is now 0
                // (means it's time to change the light)
                if (duration == 0) {
                    specialOrder = true;
                }
            }
        } else {
            // if (specialOrder && duration == 0) {
            // determineNextGreenLight();
            // specialOrder = false;
            // }
        }
        // return the number of cars in each road.
        return carNumber;
    }

    public void addCar(int time, int id, int num_of_cars) {
        if (currentTime != time) {
            while (currentTime != time) {
                if (specialOrder && duration == 0) {
                    determineNextGreenLight();
                    specialOrder = false;
                }
                // 0. update time and green light
                updateTimeAndGreenLight();

                // 1. exit only if a road light is green
                if (currentGreen != -1 && carNumber[currentGreen] > 0) {
                    carNumber[currentGreen] -= 1;
                }

                // 2. add a car to the queue of a specific id
                if (currentTime == time) {
                    carNumber[id] += num_of_cars;
                }

                // 3. determine green light status for the future only if duration is now 0
                // (means it's time to change the light)
                if (duration == 0) {
                    determineNextGreenLight();
                    specialOrder = false;
                }
            }
        } else {
            // carNumber[id] += num_of_cars;
            if (specialOrder && duration == 0) {
                carNumber[id] += num_of_cars;
                determineNextGreenLight();
                specialOrder = false;
            } else {
                carNumber[id] += num_of_cars;
            }
        }
    }

    private void updateTimeAndGreenLight() {
        if (duration == 0) {
            currentGreen = nextGreen;
            duration = nextDuration;
        }
        duration = Math.max(0, duration - 1);
        currentTime += 1;
    }

    private void determineNextGreenLight() {
        // find max carNumber and the corresponding id
        int maxCarNumber = -1;
        int targetId = -1;
        for (int i = 0; i < carNumber.length; i++) {
            if (carNumber[i] > maxCarNumber) {
                maxCarNumber = carNumber[i];
                targetId = i;
            }
        }
        if (maxCarNumber <= 0) {
            nextGreen = -1;
            nextDuration = 0;
        } else {
            nextGreen = targetId;
            nextDuration = maxCarNumber;
        }
    }

    public static void main(String[] args) {
        // Example 1
        System.out.println("Example 1: ");
        RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        sol1.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        sol1.addCar(0, 1, 3);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
        sol1.addCar(2, 0, 4);
        for (int i = 2; i < 12; ++i)
            System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
        // ______________________________________________________________________
        // Example 2
        RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 2: ");
        sol2.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        sol2.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
        sol2.addCar(2, 1, 2);
        for (int i = 2; i < 7; ++i)
            System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));
        // ______________________________________________________________________
        // Example 3
        RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 3: ");
        sol3.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
        System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(4, 0, 2);
        for (int i = 4; i < 10; i++) {
            System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
        }
        // check below for full output explaination
    }
}
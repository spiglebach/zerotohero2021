package hu.szukacsm.zerotohero;

import hu.szukacsm.zerotohero.matrixcleaner.MatrixCleaner;

public class Main {
    public static void main(String[] args) {
        int[][] matrix = new int[][] {
                new int[] {0, 1, 0, 0},
                new int[] {0, 1, 0, 1},
                new int[] {1, 0, 1, 0},
                new int[] {0, 0, 0, 1}
        };
        print("Input matrix:", matrix);
        int[][] result = new MatrixCleaner().cleanWithQueue(matrix);
        print("Output matrix:", result);
    }

    public static void print(String header, int[][] matrix) {
        System.out.println("====================");
        System.out.println(header);
        System.out.println("--------------------");
        for (int i = 0; i < matrix.length; i++) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int j = 0; j < matrix[0].length; j++) {
                lineBuilder.append(matrix[i][j]);
                lineBuilder.append(" ");
            }
            System.out.println(lineBuilder.toString().trim());
        }
        System.out.println("====================");
    }
}

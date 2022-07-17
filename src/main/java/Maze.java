
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_DFS:
                    result = DFS();
                    break;
                case Definitions.ALGORITHM_BFS:
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }


    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean DFS(){
        boolean result = false;
        Stack<JButton> nodesStack = new Stack<>();
        JButton root = buttonList.get(0);
        nodesStack.add(root);
        while (!nodesStack.isEmpty()) {
            JButton currentNode = nodesStack.pop();
            int[] nodeCoordinates = getNodeXY(currentNode);
            if(!visited[nodeCoordinates[1]][nodeCoordinates[0]]) {
                setSquareAsVisited(nodeCoordinates[1], nodeCoordinates[0], true);
                for (JButton neighbor : getNeighbors(nodeCoordinates)) {
                        nodeCoordinates = getNodeXY(neighbor);
                        if (!neighbor.getBackground().equals(Color.BLACK)) {
                            if (!visited[nodeCoordinates[1]][nodeCoordinates[0]]) {
                                nodesStack.add(neighbor);
                            }
                        }

                }
            }
            if (visited[visited.length-1][visited.length-1]) {
                result = true;
                break;
            }
        }
        return result;
    }


    public ArrayList<JButton> getNeighbors(int[] coordinates){
        ArrayList<JButton> neighbors = new ArrayList<>();
        JButton[][] matrix = convertToNodeMatrix();
        int col = coordinates[0], row = coordinates[1];
        int matrixSize = visited.length;


        if (col == 0) {
            if (row == 0) {
                neighbors.add(matrix[0][1]) ;
                neighbors.add(matrix[1][0]) ;
            }
            else if (row == matrixSize - 1) {
                neighbors.add(matrix[row-1][0]) ;
                neighbors.add(matrix[row][1]) ;
            }
            else {
                neighbors.add(matrix[row+1][0]) ;
                neighbors.add(matrix[row-1][0]) ;
                neighbors.add(matrix[row][col+1]) ;

            }
        } else if (col == matrixSize - 1) {
            if (row == 0) {
                neighbors.add(matrix[0][col-1]) ;
                neighbors.add(matrix[1][col]) ;
            } else if (row == matrixSize -1) {
                neighbors.add(matrix[row][col-1]) ;
                neighbors.add(matrix[row-1][row]) ;
            }
            else {
                neighbors.add(matrix[row-1][col]) ;
                neighbors.add(matrix[row+1][col]) ;
                neighbors.add(matrix[row][col-1]) ;
            }
        }
        else {
            if (row==0) {
                neighbors.add(matrix[row][col+1]) ;
                neighbors.add(matrix[row][col-1]) ;
                neighbors.add(matrix[row+1][col]) ;
            }
            else if (row == matrixSize - 1) {
                neighbors.add(matrix[row][col+1]) ;
                neighbors.add(matrix[row][col-1]) ;
                neighbors.add(matrix[row-1][col]) ;
            }
            else {
                neighbors.add(matrix[row+1][col]) ;
                neighbors.add(matrix[row-1][col]) ;
                neighbors.add(matrix[row][col+1]) ;
                neighbors.add(matrix[row][col-1]) ;
            }
        }
        return neighbors;
    }

    public int[] getNodeXY(JButton currentNode) {
        int[] nodeXY = new int[2];
        JButton[][] matrix = convertToNodeMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j].getText().equals(currentNode.getText())) {
                    nodeXY[0] = j;
                    nodeXY[1] = i;
                    break;
                }
            }
        }

        return nodeXY;
    }



    public JButton[][] convertToNodeMatrix() {

        int matrixSize = visited.length;
        JButton[][] matrix = new JButton[matrixSize][matrixSize];
        int x=0, y=0;
        for (JButton currentButton : buttonList) {
            matrix[y][x] = currentButton;
            x++;
            if (x == matrixSize) {
                y++;
                x=0;
            }
        }
        return matrix;
    }

}



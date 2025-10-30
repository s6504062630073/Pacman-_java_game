import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        // width = 19 column * 32 px
        // height = 21 row * 32 px
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("PacManKung");
        
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); //player cannot resize the board
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();

        pacmanGame.requestFocus();

        frame.setVisible(true); //set here cuz all component int the window
        
    }
}

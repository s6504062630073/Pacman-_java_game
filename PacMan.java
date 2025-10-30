//import java.util.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.HashSet;


public class PacMan extends JPanel implements ActionListener, KeyListener{
    //implement to reflect new position
    //key listener to move pacman with arrow keyboard
    class Block{
        int x, y, width, height; 
        Image image;

        int startX; //จุดเริ่มต้น X
        int startY;

        char direction = 'U'; //set default as UP
        //direction U(Up), D(Down), L(Left), R(Right)
        int velocityX = 0; // move in X axis
        int velocityY = 0; // move in Y axis
        //but set as 0,0 == not moving at all.

        //consructure
        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.startX = x;
            this.startY = y;
        }

        //press arrow key -> update direction
        void updateDirection(char direction){
            //store previous direction
            char previousDirection = this.direction;

            this.direction = direction;

            updateVelocity(); //according to the direction

            //pacman can stoponly when collision
            this.x += this.velocityX;
            this.y += this.velocityY;

            for(Block wall : walls){
                if(collision(this, wall)){ //this = pacaman and ghost
                    //if collision take a step back
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = previousDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if(this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = -tileSize/4; // up 8 pxs
            }
            else if(this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if(this.direction == 'L'){
                this.velocityX = -tileSize/4; //moving forward to the zero column
                this.velocityY = 0;
            }
            else if(this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }

        void snapToCenter(){
            //snap pacman to the center for easying changing direction
            // snap center in Y - axis
            if(direction == 'L' || direction == 'R'){
                int row = (y + height/2) / PacMan.this.tileSize;
                y = row * PacMan.this.tileSize + PacMan.this.tileSize/2 - height/2;
            }
            // snap center in X - axis
            else if(direction == 'U' || direction == 'D'){
                int col = (x + width/2) / PacMan.this.tileSize;
                x = col * PacMan.this.tileSize + PacMan.this.tileSize/2 - width/2;
            }
        }

        void updateDirectionWithSnap(char direction){
            this.direction = direction;
            snapToCenter();
            updateVelocity();
        }

    }

    //panel to draw =  same size as window
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    boolean doubleScoreActive = false;

    //store image
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    //image pacman
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //image obstracle
    private Image cherryImage;
    private Image strawberryImage;
    private Image stonImage;   
    private Image bombImage;      
    private Image heartImage;                

    //tile map 2D array
        //O == empty(no food), r == red, o == orange, b == blue, p == pink, P == Pacman
        //start at (0,0), iterate thorogh map if found x = create block, iterate thought hashset

    private String[] tileMap = {

        "XXXXXXXXXXXXXXXXXXX",
        "X  c     X   N    X",
        "X XX XXX X XXX XX X",
        "X  B         H    X",
        "X XX X XXXXX X XX X",
        "X    X  c    X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O  H    bpo  s    O",
        "XXXX X XXXXX X XXXX",
        "OOOX X   B   X XOOO",
        "XXXX X XXXXX X XXXX",
        "X    s    X       X",
        "X XX XXX X XXX XX X",
        "X  X      P  c X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X B  X",
        "X XXXXXX X XXXXXX X",
        "X N    c          X",
        "XXXXXXXXXXXXXXXXXXX"
        };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    HashSet<Block> cherries;
    HashSet<Block> strawberries;
    HashSet<Block> stones;
    HashSet<Block> bombs;
    HashSet<Block> hearts;
    Block pacman;

    //timer
    Timer gameLoop;

    //for ghost to mmove
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random(); //for each ghost to randomly move

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    //contructure 
    PacMan(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.darkGray);

        //make the key work
        addKeyListener(this);
        setFocusable(true);

        //load image
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        // getClass -> PacManclass , getResouces -> where the image store
        
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();


        //obstracle
        cherryImage = new ImageIcon(getClass().getResource("./cherry.png")).getImage();
        strawberryImage = new ImageIcon(getClass().getResource("./strawberry.png")).getImage();
        stonImage = new ImageIcon(getClass().getResource("./stone.png")).getImage();
        bombImage = new ImageIcon(getClass().getResource("./bomb.png")).getImage();
        heartImage = new ImageIcon(getClass().getResource("./heart.png")).getImage();

        loadMap();
        //System.out.println(walls.size());
        //System.out.println(foods.size());
        //System.out.println(ghosts.size());

        //iterate through each ghost
        for(Block ghost : ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this); // 20fps
        //50 = delay, this = pacman object
        gameLoop.start();

    }

    public void loadMap(){
        //initailize hashset
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        cherries = new HashSet<Block>();
        strawberries = new HashSet<Block>();
        stones = new HashSet<Block>();
        bombs = new HashSet<Block>();
        hearts = new HashSet<Block>();

        
        for(int r = 0; r < rowCount; r++){
            for(int c = 0; c < columnCount; c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize; // how may tile from the left
                int y = r*tileSize; // how many row from the top

                if(tileMapChar == 'X'){ 
                    // Block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if(tileMapChar == 'b'){
                    //blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'o'){
                    //orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'r'){
                    //red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'p'){
                    //pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'P'){
                    //pacman -> right
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if(tileMapChar == ' '){
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
                else if(tileMapChar == 'c'){
                    Block cherry = new Block(cherryImage, x, y, tileSize, tileSize);
                    cherries.add(cherry);
                }
                else if(tileMapChar == 's'){
                    Block strawberry = new Block(strawberryImage, x, y, tileSize, tileSize);
                    strawberries.add(strawberry);
                }
                else if(tileMapChar == 'N'){
                    Block stone =  new Block(stonImage, x, y, tileSize, tileSize);
                    stones.add(stone);
                }
                else if(tileMapChar == 'B'){
                    Block bomb = new Block(bombImage, x, y, tileSize, tileSize);
                    bombs.add(bomb);
                }
                else if(tileMapChar == 'H'){
                    Block heart = new Block(heartImage, x, y, tileSize, tileSize);
                    hearts.add(heart);
                }
            }
        }
    }

    //draw
    public void paintComponent(Graphics g){
        super.paintComponent(g); //invoke fuction of the same name of JPa
        draw(g);
    }

    //draw
    public void draw(Graphics g) {
    //g.fillRect(pacman.x, pacman.y, pacman.width, pacman.height);
    if (pacman.image != null) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
    }

    for (Block ghost : ghosts) {
        if (ghost.image != null) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
    }

    for (Block wall : walls) {
        // drw wall
        if (wall.image != null) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
    }

    for (Block cherry : cherries) {
        if (cherry.image != null) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }
    }

    for (Block strawberry : strawberries) {
        if (strawberry.image != null) {
            g.drawImage(strawberry.image, strawberry.x, strawberry.y, strawberry.width, strawberry.height, null);
        }
    }

    for (Block stone : stones) {
        if (stone.image != null) {
            g.drawImage(stone.image, stone.x, stone.y, stone.width, stone.height, null);
        }
    }

    for (Block bomb : bombs) {
        if (bomb.image != null) {
            g.drawImage(bomb.image, bomb.x, bomb.y, bomb.width, bomb.height, null);
        }
    }

    for (Block heart : hearts) {
        if (heart.image != null) {
            g.drawImage(heart.image, heart.x, heart.y, heart.width, heart.height, null);
        }
    }

    g.setColor(Color.magenta);
    for (Block food : foods) {
        g.fillRect(food.x, food.y, food.width, food.height);
    }

    //score
    g.setFont(new Font("Arial", Font.BOLD, 18));
    if (gameOver) {
        g.drawString("GameOver: " + String.valueOf(score), tileSize/2, tileSize/2);
    } else {
        g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
    }
}

    //accually move pacman
    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //teleport form L to R, R to L
        if(pacman.x + pacman.width < 0){
            pacman.x = boardWidth;
        }
        else if(pacman.x > boardWidth){
            pacman.x = -pacman.width;
        }

        //check wall collision
        for(Block wall : walls){
            //collision between pacman and current wall
            if(collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //ckeck ghost collision
        for(Block ghost : ghosts){

            //check if ghost collide with pacman
            if(collision(ghost, pacman)){
                lives -= 1;
                if(lives == 0){
                    gameOver = true;
                    return;
                }
                resetPosition();
            }

            //check if the ghost on the 9 row
            if(ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U'); 
                //force to go up when tried to go left + right, unless it's U, D already
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            //break; //only one ghost will move

            //iterate wall for each ghost
            for(Block wall : walls){
                if(collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth){
                    //ghost.x <= 0 = left side, ghost.x + ghost.width >= boardWidth = rigth side
                    // if ghost collision will take astep back
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;

                    //make the ghost change direction immedieatly wgae collide to the wall
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //check food collision
        Block foodEaten = null;
        for(Block food : foods){
            if(collision(pacman, food)){
                foodEaten = food;
                increaseScore(10);
            }
        }
        if(foodEaten != null){
            foods.remove(foodEaten); //eat then gone
        }
        if(foods.isEmpty()){ //if all food was eaten
            loadMap();
            resetPosition();
        }

        Block cherryEaten = null;
        for(Block cherry : cherries){
            if(collision(pacman, cherry)){
                cherryEaten = cherry;
                increaseSpeed();
            }
        }
        if(cherryEaten != null){
            cherries.remove(cherryEaten); 
        }

        Block strawberryEaten = null;
        for(Block strawberry : strawberries){
            if(collision(pacman, strawberry)){
                strawberryEaten = strawberry;
                increaseScore(50);
                activateDoubleScore();
            }
        }
        if(strawberryEaten != null){
            strawberries.remove(strawberryEaten);
        }
        

        Block stoneEaten = null;
        for(Block stone : stones){
            if(collision(pacman, stone)){
                stoneEaten = stone;
                lives -= 1;
                if(lives <= 0){
                    gameOver = true;
                }
            }
        }
        if(stoneEaten != null){
            stones.remove(stoneEaten);
        }
        

        Block bombEaten = null;
        for(Block bomb : bombs){
            if(collision(pacman, bomb)){
                bombEaten = bomb;
                decreaseScore();
            }
        }
        if(bombEaten != null){
            bombs.remove(bombEaten);
        }
        


        Block heartEaten = null;
        for(Block heart : hearts){
            if(collision(pacman, heart)){
                heartEaten = heart;
                if(lives < 3){
                    lives += 1;
                }
            }
        }
        if(heartEaten != null){
            hearts.remove(heartEaten);
        }
        

    }

    //detect coliision -> found stop moving
    public boolean collision(Block a, Block b){
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPosition(){
        pacman.reset(); // if colliosion = back to the fist start
        pacman.velocityX = 0; // not moving until user press key
        pacman.velocityY = 0;
        pacman.direction = ' ';

        for(Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    public void increaseSpeed(){
        //System.out.println("SpeedUp");
        tileSize = 32;
        pacman.velocityX *= 2;
        pacman.velocityY *= 2;

        new Timer(5000, e ->{
            pacman.updateVelocity();
        }).start();
    }

    public void increaseScore(int points) {

        if(doubleScoreActive){
            points *= 2;
        }
        score += points;
        //System.out.println("Score increased! Current score: " + score);
    }

    public void activateDoubleScore(){
        doubleScoreActive = true;

        new Timer(5000, e->{
            doubleScoreActive = false;
        }).start();
    }

    public void decreaseScore(){
        int penaltyPoints = 50;
        score -= penaltyPoints;
        if(score < 0){
            score = 0;
        }
    }


    // use fo game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        move(); //update all position and redraw
        repaint(); //recall paintComponent
        //need game loop = timer to execute

        if(gameOver){
            gameLoop.stop();
        }
    }

    // type on a key -> get a charecter == not using
    @Override
    public void keyTyped(KeyEvent e) {
       
    }

    //not using it cuz don't want to hold on the key
    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    //nothing will happen unless let go >> usinggggg!
    @Override
    public void keyReleased(KeyEvent e) {

        if(gameOver){
            loadMap();
            resetPosition();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        //System.out.println("KeyEvent: " + e.getKeyCode());
        //checks, if press down arrow will update direction of pacman
        if(e.getKeyCode() == KeyEvent.VK_UP){ // 
            pacman.updateDirectionWithSnap('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirectionWithSnap('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirectionWithSnap('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirectionWithSnap('R');
        }

        //change picture of pacman according to direction
        if(pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        }
        else if(pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }

    }

}

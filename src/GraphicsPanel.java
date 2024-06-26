import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener {
    private BufferedImage background;
    private Player player;
    private boolean[] pressedKeys;
    private ArrayList<Coin> coins;
    private Timer timer;
    private int time;
    private JButton pause;

    private JButton reset;

    private boolean paused = false;

    private boolean set = false;

    public GraphicsPanel(String name) {
        try {
            background = ImageIO.read(new File("src/background.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        player = new Player("src/marioleft.png", "src/marioright.png", name);
        coins = new ArrayList<>();
        pressedKeys = new boolean[128];
        time = 0;
        timer = new Timer(1000, this); // this Timer will call the actionPerformed interface method every 1000ms = 1 second
        timer.start();
        addKeyListener(this);
        addMouseListener(this);
        reset = new JButton("Reset");
        pause = new JButton("Pause");
        add(pause);
        pause.addActionListener(this);
        add(reset);
        reset.addActionListener(this);
        setFocusable(true); // this line of code + one below makes this panel active for keylistener events
        requestFocusInWindow(); // see comment above
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // just do this
        g.drawImage(background, 0, 0, null);  // the order that things get "painted" matter; we put background down first
        g.drawImage(player.getPlayerImage(), player.getxCoord(), player.getyCoord(), null);
        if (paused) {
            timer.stop();
        } else {
            timer.start();
        }

        // this loop does two things:  it draws each Coin that gets placed with mouse clicks,
        // and it also checks if the player has "intersected" (collided with) the Coin, and if so,
        // the score goes up and the Coin is removed from the arraylist
        if (!paused) {
            for (int i = 0; i < coins.size(); i++) {
                Coin coin = coins.get(i);
                g.drawImage(coin.getImage(), coin.getxCoord(), coin.getyCoord(), null); // draw Coin
                if (player.playerRect().intersects(coin.coinRect())) { // check for collision
                    player.collectCoin();
                    coins.remove(i);
                    i--;
                    if (player.getScore() == 10) {
                        try {
                            background = ImageIO.read(new File("src/background2.png"));
                            set = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        // draw score
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.drawString(player.getName() + "'s Score: " + player.getScore(), 20, 40);
        g.drawString("Time: " + time, 20, 70);
        reset.setLocation(20, 110);
        pause.setLocation(20, 140);


        if (player.getScore() >= 10) {
            player.setLeft("src/mariofrogleft.png");
            player.setRight("src/mariofrogright.png");
        } else {
            player.setLeft("src/marioleft.png");
            player.setRight("src/marioright.png");
        }
        if (!paused) {

            // player moves left (A)
            if (pressedKeys[65]) {
                player.faceLeft();
                player.moveLeft();
            }

            // player moves right (D)
            if (pressedKeys[68]) {
                player.faceRight();
                player.moveRight();
            }

            // player moves up (W)
            if (pressedKeys[87]) {
                player.moveUp();
            }

            // player moves down (S)
            if (pressedKeys[83]) {
                player.moveDown();
            }
        }
    }

    // ----- KeyListener interface methods -----
    public void keyTyped(KeyEvent e) { } // unimplemented

    public void keyPressed(KeyEvent e) {
        // see this for all keycodes: https://stackoverflow.com/questions/15313469/java-keyboard-keycodes-list
        // A = 65, D = 68, S = 83, W = 87, left = 37, up = 38, right = 39, down = 40, space = 32, enter = 10
        if(!paused) {
            int key = e.getKeyCode();
            pressedKeys[key] = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = false;
    }

    // ----- MouseListener interface methods -----
    public void mouseClicked(MouseEvent e) { }  // unimplemented; if you move your mouse while clicking,
    // this method isn't called, so mouseReleased is best

    public void mousePressed(MouseEvent e) { } // unimplemented

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {  // left mouse click
            Point mouseClickLocation = e.getPoint();
            Coin coin = new Coin(mouseClickLocation.x, mouseClickLocation.y);
            coins.add(coin);
        } else {
            Point mouseClickLocation = e.getPoint();
            if (player.playerRect().contains(mouseClickLocation)) {
                player.turn();
            }
        }
    }

    public void mouseEntered(MouseEvent e) { } // unimplemented

    public void mouseExited(MouseEvent e) { } // unimplemented

    // ACTIONLISTENER INTERFACE METHODS: used for buttons AND timers!
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Timer) {
            time++;
        }
        else if (e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();
            if(button == reset) {
                player.setxCoord(50);
                player.setyCoord(435);
                player.setScore(0);
                try {
                    background = ImageIO.read(new File("src/background.png"));
                    set = true;
                } catch (IOException x) {
                    System.out.println(x.getMessage());
                }
            }
            else if(button == pause){
                if(paused == false){
                    paused = true;
                }
                else{
                    paused = false;
                }
                requestFocusInWindow();
            }
            requestFocusInWindow();
        }
    }
}

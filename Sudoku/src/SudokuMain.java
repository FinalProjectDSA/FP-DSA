    import java.awt.*;
    import java.awt.event.*;
    import javax.swing.*;
    /**
     * The main Sudoku program
     */
    public class SudokuMain extends JFrame {
        private static final long serialVersionUID = 1L;  // to prevent serial warning

        // private variables
        GameBoardPanel board = new GameBoardPanel();
        JButton restartGame = new JButton("Restart Game");

        // Constructor
        public SudokuMain() {

            showHomePage();

            Container cp = getContentPane();
            cp.setLayout(new BorderLayout());

            cp.add(board, BorderLayout.CENTER);

            JMenuBar menuBar = createMenuBar();
            setJMenuBar(menuBar);

            pack();     // Pack the UI components, instead of using setSize()
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
            setTitle("Sudoku");
            setVisible(true);

            JPopupMenu popupMenu = new JPopupMenu();

            cp.add(restartGame, BorderLayout.SOUTH);

            restartGame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    board.restartGame();
                }
            });


            board.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                private void showPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) { // Checks for right-click on different platforms
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });


            // Initialize the game board to start the game
            board.newGame();

            pack();     // Pack the UI components, instead of using setSize()
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
            setTitle("Sudoku");
            setVisible(true);
        }


        // Create the menu bar
        private JMenuBar createMenuBar() {
            JMenuBar menuBar = new JMenuBar();

            // File menu
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);

            // New Game Menu Item
            JMenuItem newGameItem = new JMenuItem("New Game");
            newGameItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    board.newGame(); // Start a new game
                }
            });
            fileMenu.add(newGameItem);

            // Exit Menu Item
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); // Exit the game
                }
            });
            fileMenu.add(exitItem);
            return menuBar;
        }

        // Show home page dialog
        private void showHomePage() {
            JDialog homeDialog = new JDialog(this, "Welcome to Sudoku", true);

            // Load the background image as a resource
            ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/homepagebg.jpg"));
            Image backgroundImage = backgroundIcon.getImage();

            // Create a custom panel to display the background image
            JPanel backgroundPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Draw the background image scaled to the panel size
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            backgroundPanel.setLayout(new BorderLayout()); // Set layout to add components

            // Add content to the background panel
            JLabel welcomeLabel = new JLabel(
                    "<html><h1 style='color:white;'>Welcome to Sudoku!</h1><p style='color:white;'>How to play:</p>" +
                            "<p style='color:white;'>Fill in a 9x9 grid so that each column, row, and 3x3 subgrid " +
                            "contains all digits from 1 to 9.</p></html>",
                    SwingConstants.CENTER
            );
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);

            // Add a transparent button over the background
            JButton startGameButton = new JButton("Start Game");
            startGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    homeDialog.dispose(); // Close the dialog when the button is clicked
                }
            });

            // Add components to the background panel
            backgroundPanel.add(welcomeLabel, BorderLayout.CENTER);
            backgroundPanel.add(startGameButton, BorderLayout.SOUTH);

            // Set the background panel as the content pane
            homeDialog.setContentPane(backgroundPanel);
            homeDialog.setSize(800, 500);
            homeDialog.setLocationRelativeTo(null); // Center the dialog
            homeDialog.setVisible(true); // Show the dialog
        }


        /**
         * The entry main() entry method
         */
        public static void main(String[] args) {
            // [TODO 1] Check "Swing program template" on how to run
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SudokuMain(); // Create and show the GUI
                }
            });
        }
    }
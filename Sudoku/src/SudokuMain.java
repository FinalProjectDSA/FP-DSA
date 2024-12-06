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
        private JLabel statusBar = new JLabel("Welcome to Sudoku!"); // Status bar
        private String playerName = ""; // Player's name

        // Constructor
        public SudokuMain() {

            showHomePage();

            Container cp = getContentPane();
            cp.setLayout(new BorderLayout());

            cp.add(board, BorderLayout.CENTER);

            JMenuBar menuBar = createMenuBar();
            setJMenuBar(menuBar);

            cp.add(restartGame, BorderLayout.SOUTH);

            restartGame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    board.restartGame();
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
            backgroundPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for precise control

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.CENTER;

            // Add the welcome label in the center (vertically and horizontally)
            gbc.gridy = 0;
            gbc.weighty = 0.5; // Allow the message to take up half the available vertical space
            JLabel welcomeLabel = new JLabel(
                    "<html><h1 style='color:white;'>Welcome to Sudoku!</h1><p style='color:white;'>How to play:</p>" +
                            "<p style='color:white;'>Fill in a 9x9 grid so that each column, row, and 3x3 subgrid " +
                            "contains all digits from 1 to 9.</p></html>",
                    SwingConstants.CENTER
            );
            backgroundPanel.add(welcomeLabel, gbc);

            // Create a panel to hold the name field and the start button
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center the input components

            // Add a text field for the player's name
            JTextField nameField = new JTextField(20); // Set the width of the text field
            inputPanel.add(nameField);

            // Add a start game button
            JButton startGameButton = new JButton("Start Game");
            inputPanel.add(startGameButton);

            startGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playerName = nameField.getText().trim();
                    if (playerName.isEmpty()) {
                        playerName = "Player";
                    }
                    statusBar.setText("Welcome, " + playerName + "!"); // Update the status bar
                    homeDialog.dispose(); // Close the dialog
                }
            });

            gbc.gridy = 1; // Move to the second row for input fields
            gbc.weighty = 0.5; // Push the input panel to the bottom
            backgroundPanel.add(inputPanel, gbc);

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
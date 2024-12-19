import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Connect-Four: Two-player Graphics version with Simple-OO in one class
 */
public class TTTGraphics extends JFrame {
    private static final long serialVersionUID = 1L; // untuk mencegah peringatan serializable

    // Definisikan konstanta untuk papan permainan
    public static final int ROWS = 6;  // Ubah menjadi 6
    public static final int COLS = 7;  // Ubah menjadi 7

    // Definisikan konstanta untuk menggambar grafik
    public static final int CELL_SIZE = 120; // lebar/tinggi sel (persegi)
    public static final int BOARD_WIDTH  = CELL_SIZE * COLS; // kanvas menggambar
    public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 10;                  // lebar garis grid
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final Color COLOR_BG = Color.WHITE;  // latar belakang
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_GRID   = Color.LIGHT_GRAY;  // garis grid
    public static final Color COLOR_CROSS  = new Color(211, 45, 65);  // Merah
    public static final Color COLOR_NOUGHT = new Color(76, 181, 245); // Biru
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Enum untuk menyimpan status permainan
    public enum State {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
    private State currentState;  // status permainan saat ini

    // Enum untuk menyimpan isi sel
    public enum Seed {
        CROSS, NOUGHT, NO_SEED
    }
    private Seed currentPlayer; // pemain saat ini
    private Seed[][] board;     // papan permainan berukuran ROWS x COLS

    // Komponen UI
    private GamePanel gamePanel; // Kanvas menggambar (JPanel) untuk papan permainan
    private JLabel statusBar;  // Status Bar

    /** Konstruktor untuk mengatur permainan dan komponen GUI */
    public TTTGraphics() {
        // Inisialisasi objek permainan
        initGame();

        // Atur komponen GUI
        gamePanel = new GamePanel();  // Buat kanvas menggambar (JPanel)
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // Kanvas (JPanel) memicu MouseEvent saat diklik
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // penanganan klik mouse
                int mouseX = e.getX();
                int colSelected = mouseX / CELL_SIZE; // Dapatkan kolom yang diklik

                if (currentState == State.PLAYING) {
                    if (colSelected >= 0 && colSelected < COLS) {
                        // Cari sel kosong mulai dari baris paling bawah
                        for (int row = ROWS - 1; row >= 0; row--) {
                            if (board[row][colSelected] == Seed.NO_SEED) {
                                board[row][colSelected] = currentPlayer; // Buat langkah
                                currentState = stepGame(currentPlayer, row, colSelected); // Perbarui status
                                // Ganti pemain
                                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                                break;
                            }
                        }
                    }
                } else { // Jika permainan sudah selesai
                    newGame(); // Mulai ulang permainan
                }
                repaint(); // Refresh tampilan
            }
        });

        // Atur status bar (JLabel) untuk menampilkan pesan status
        statusBar = new JLabel("       ");
        statusBar.setFont(FONT_STATUS);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        statusBar.setOpaque (true);
        statusBar.setBackground(COLOR_BG_STATUS);

        // Atur konten pane
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END); // sama dengan SOUTH

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // kemas semua komponen dalam JFrame ini
        setTitle("Connect Four");
        setVisible(true);  // tampilkan JFrame ini

        newGame();
    }

    /** Inisialisasi permainan (dijalankan sekali) */
    public void initGame() {
        board = new Seed[ROWS][COLS]; // alokasikan array
    }

    /** Reset konten papan permainan dan status, siap untuk permainan baru */
    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Seed.NO_SEED; // semua sel kosong
            }
        }
        currentPlayer = Seed.CROSS;    // pemain CROSS bermain pertama
        currentState  = State.PLAYING; // siap untuk bermain
    }

    /**
     * Pemain yang diberikan melakukan langkah pada (selectedRow, selectedCol).
     * Perbarui cells[selectedRow][selectedCol]. Hitung dan kembalikan
     * status permainan baru (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        // Perbarui papan permainan
        board[selectedRow][selectedCol] = player;

        // Hitung dan kembalikan status permainan baru
        if (hasWon(player, selectedRow, selectedCol)) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            // Tidak ada yang menang. Periksa DRAW (semua sel terisi) atau PLAYING.
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (board[row][col] == Seed.NO_SEED) {
                        return State.PLAYING; // masih ada sel kosong
                    }
                }
            }
            return State.DRAW; // tidak ada sel kosong, ini adalah hasil imbang
        }
    }

    /** Memeriksa apakah pemain telah menang */
    /** Memeriksa apakah pemain telah menang */
    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        // Cek horizontal
        if (checkDirection(theSeed, rowSelected, colSelected, 0, 1) || // Kanan
                checkDirection(theSeed, rowSelected, colSelected, 0, -1)) { // Kiri
            return true;
        }

        // Cek vertikal
        if (checkDirection(theSeed, rowSelected, colSelected, 1, 0)) { // Bawah
            return true;
        }

        // Cek diagonal (dari kiri atas ke kanan bawah)
        if (checkDirection(theSeed, rowSelected, colSelected, 1, 1) || // Kanan Bawah
                checkDirection(theSeed, rowSelected, colSelected, -1, -1)) { // Kiri Atas
            return true;
        }

        // Cek diagonal (dari kanan atas ke kiri bawah)
        if (checkDirection(theSeed, rowSelected, colSelected, 1, -1) || // Kiri Bawah
                checkDirection(theSeed, rowSelected, colSelected, -1, 1)) { // Kanan Atas
            return true;
        }

        return false; // Tidak ditemukan 4-in-a-line
    }

    /** Memeriksa arah tertentu untuk 4 dalam baris */
    private boolean checkDirection(Seed theSeed, int row, int col, int rowIncrement, int colIncrement) {
        int count = 1; // Mulai dengan 1 untuk sel yang baru saja dimainkan

        // Cek satu arah
        for (int i = 1; i < 4; i++) {
            int r = row + i * rowIncrement;
            int c = col + i * colIncrement;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                count++;
            } else {
                break; // Hentikan jika tidak ada lagi yang cocok
            }
        }

        // Cek arah yang berlawanan
        for (int i = 1; i < 4; i++) {
            int r = row - i * rowIncrement;
            int c = col - i * colIncrement;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                count++;
            } else {
                break; // Hentikan jika tidak ada lagi yang cocok
            }
        }

        return count >= 4; // Kembalikan true jika ada 4 dalam baris
    }

    /**
     * Kelas dalam GamePanel (extends JPanel) digunakan untuk menggambar grafik kustom.
     */
    class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L; // untuk mencegah peringatan serializable

        @Override
        public void paintComponent(Graphics g) {  // Callback melalui repaint()
            super.paintComponent(g);
            setBackground(COLOR_BG);  // set latar belakang

            // Gambar garis grid
            g.setColor(COLOR_GRID);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        BOARD_WIDTH - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH); // Menambahkan arcWidth dan arcHeight
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, BOARD_HEIGHT - 1, GRID_WIDTH, GRID_WIDTH); // Menambahkan arcWidth dan arcHeight
            }

            // Gambar biji dari semua sel jika tidak kosong
            Graphics2D g2d = (Graphics2D) g;
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_SIZE / 5;
                    int y1 = row * CELL_SIZE + CELL_SIZE / 5;
                    if (board[row][col] == Seed.CROSS) {  // gambar lingkaran merah
                        g2d.setColor(COLOR_CROSS);
                        g2d.fillOval(x1, y1, CELL_SIZE - CELL_SIZE / 5 * 2, CELL_SIZE - CELL_SIZE / 5 * 2);
                    } else if (board[row][col] == Seed.NOUGHT) {  // gambar lingkaran biru
                        g2d.setColor(COLOR_NOUGHT);
                        g2d.fillOval(x1, y1, CELL_SIZE - CELL_SIZE / 5 * 2, CELL_SIZE - CELL_SIZE / 5 * 2);
                    }
                }
            }

            // Cetak pesan status
            if (currentState == State.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                statusBar.setText((currentPlayer == Seed.CROSS) ? "Red's Turn" : "Blue's Turn");
            } else if (currentState == State.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("It's a Draw! Click to play again");
            } else if (currentState == State.CROSS_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("Red Won! Click to play again");
            } else if (currentState == State.NOUGHT_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("Blue Won! Click to play again");
            }
        }
    }

    /** Metode utama untuk menjalankan aplikasi */
    public static void main(String[] args) {
        // Jalankan kode GUI di thread Event-Dispatching untuk keamanan thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TTTGraphics(); // Biarkan konstruktor melakukan tugas
            }
        });
    }
}
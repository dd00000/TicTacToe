package dandobre.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private String[][] board = new String[3][3];
    private boolean isPlayerOneTurn = true;
    private TextView statusTextView;
    private String mode;
    private LinearLayout gameplayLayout, gameOverLayout;
    private TextView gameOverTextView;
    private final Button[][] buttonGrid = new Button[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        statusTextView = findViewById(R.id.text_status);
        gameplayLayout = findViewById(R.id.layout_gameplay);
        gameOverLayout = findViewById(R.id.layout_game_over);
        gameOverTextView = findViewById(R.id.text_game_over);

        mode = getIntent().getStringExtra("mode");
        setupBoard();

        Button restartButton = findViewById(R.id.button_restart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        Button mainMenuButton = findViewById(R.id.button_main_menu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // End this activity
            }
        });

        Button quitAppButton = findViewById(R.id.button_quit_app);
        quitAppButton.setOnClickListener(v -> finishAffinity());
    }

    private void setupBoard() {
        GridLayout gridLayout = findViewById(R.id.grid_board);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final int row = i / 3;
            final int col = i % 3;
            final Button cell = (Button) gridLayout.getChildAt(i);

            buttonGrid[row][col] = cell;

            cell.setText("");
            board[row][col] = null;
            gameplayLayout.setVisibility(View.VISIBLE);
            gameOverLayout.setVisibility(View.GONE);

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!cell.getText().toString().isEmpty() || isGameOver()) {
                        return;
                    }
                    if (isPlayerOneTurn) {
                        cell.setText("X");
                        cell.setTextColor(getResources().getColor(R.color.xColor));
                        board[row][col] = "X";

                        if (!checkWin() && !checkDraw()) {
                            isPlayerOneTurn = !isPlayerOneTurn;
                            statusTextView.setText(mode.equals("ai") ? "AI's Turn (O)" : "Player 2's Turn (O)");
                            if (mode.equals("ai")) {
                                aiMakeMove();
                            }
                        }
                    } else if (mode.equals("local")) {
                        cell.setText("O");
                        cell.setTextColor(getResources().getColor(R.color.oColor));
                        board[row][col] = "O";

                        if (!checkWin() && !checkDraw()) {
                            isPlayerOneTurn = true;
                            statusTextView.setText("Player 1's turn (X)");
                        }
                    }
                }
            });
        }
    }

    private void aiMakeMove() {
        // AI makes its move after a short delay
        GridLayout gridLayout = findViewById(R.id.grid_board);
        gridLayout.postDelayed(() -> {
            if (isGameOver()) return;

            // AI's move logic (basic algorithm)
            int[] bestMove = getBestMove();
            if (bestMove != null) {
                int row = bestMove[0];
                int col = bestMove[1];
                Button cell = (Button) gridLayout.getChildAt(row * 3 + col);
                cell.setText("O");
                cell.setTextColor(getResources().getColor(R.color.oColor));
                board[row][col] = "O";

                if (!checkWin()) {
                    isPlayerOneTurn = !isPlayerOneTurn;
                    statusTextView.setText("Your turn (X)");
                }
            }
        }, 500); // AI "thinks" for 500ms
    }

    private int[] getBestMove() {
        // Check for winning move for AI
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == null) {
                    board[row][col] = "O";
                    if (checkWin()) {
                        board[row][col] = null; // Undo move
                        return new int[]{row, col};
                    }
                    board[row][col] = null; // Undo move
                }
            }
        }

        // Check for blocking move (stop player from winning)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == null) {
                    board[row][col] = "X";
                    if (aICheckWin()) {
                        board[row][col] = null; // Undo move
                        return new int[]{row, col};
                    }
                    board[row][col] = null; // Undo move
                }
            }
        }

        // Otherwise, pick the first available spot
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == null) {
                    return new int[]{row, col};
                }
            }
        }

        return null; // No moves left
    }

    private boolean isGameOver() {
        return checkWin() || checkDraw();
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            // Check rows and columns
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                drawWinLineForRow(i);
                return true;
            } else if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                drawWinLineForColumn(i);
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            drawWinLineForLRD();
            return true;
        } else if (board[0][2] != null && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            drawWinLineForRLD();
            return true;
        }
        return false;
    }

    private boolean aICheckWin() {
        for (int i = 0; i < 3; i++) {
            // Check rows and columns
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                return true;
            } else if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            return true;
        } else return board[0][2] != null && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]);
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == null) {
                    return false; // Still moves left
                }
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> showGameOver("It's a draw!"), 2000);
        return true;
    }

    private void showGameOver(String message) {
        gameplayLayout.setVisibility(View.GONE);
        gameOverLayout.setVisibility(View.VISIBLE);
        gameOverTextView.setText(message);
    }

    private void resetGame() {
        // Reset the game board and switch back to gameplay layout
        board = new String[3][3];
        isPlayerOneTurn = true;
        statusTextView.setText(mode.equals("ai") ? "Your turn (X)" : "Player 1's turn (X)");
        gameplayLayout.setVisibility(View.VISIBLE);
        gameOverLayout.setVisibility(View.GONE);

        GridLayout gridLayout = findViewById(R.id.grid_board);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button cell = (Button) gridLayout.getChildAt(i);
            cell.setText("");
        }
    }

    private void showWinningLineAndDelay(float startX, float startY, float endX, float endY) {
        WinningLineView winningLineView = new WinningLineView(this);
        winningLineView.setLine(startX, startY, endX, endY);

        FrameLayout container = findViewById(R.id.game_container);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        container.addView(winningLineView, params);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            container.removeView(winningLineView);
            showGameOver("Player " + (isPlayerOneTurn ? "1" : "2") + " wins!");
        }, 2000);
    }

    private void drawWinLineForRow(int row) {
        Button startButton = buttonGrid[row][0];
        Button endButton = buttonGrid[row][2];

        adjustCoordinates(startButton, endButton);
    }

    private void drawWinLineForColumn(int column) {
        Button startButton = buttonGrid[0][column];
        Button endButton = buttonGrid[2][column];

        adjustCoordinates(startButton, endButton);
    }

    private void drawWinLineForLRD() {
        Button startButton = buttonGrid[0][0];
        Button endButton = buttonGrid[2][2];

        adjustCoordinates(startButton, endButton);
    }

    private void drawWinLineForRLD() {
        Button startButton = buttonGrid[0][2];
        Button endButton = buttonGrid[2][0];

        adjustCoordinates(startButton, endButton);
    }

    private void adjustCoordinates(Button startButton, Button endButton) {
        FrameLayout container = findViewById(R.id.game_container);

        int[] containerCoords = new int[2];
        container.getLocationOnScreen(containerCoords);
        float containerLeft = containerCoords[0];
        float containerTop  = containerCoords[1];

        int[] startCoords = new int[2];
        startButton.getLocationOnScreen(startCoords);
        float startButtonLeft = startCoords[0];
        float startButtonTop  = startCoords[1];

        int[] endCoords = new int[2];
        endButton.getLocationOnScreen(endCoords);
        float endButtonLeft = endCoords[0];
        float endButtonTop  = endCoords[1];

        float startLocalX = startButtonLeft - containerLeft;
        float startLocalY = startButtonTop  - containerTop;

        float endLocalX = endButtonLeft - containerLeft;
        float endLocalY = endButtonTop  - containerTop;

        float startCenterX = startLocalX + (startButton.getWidth() / 2f);
        float startCenterY = startLocalY + (startButton.getHeight() / 2f);

        float endCenterX   = endLocalX   + (endButton.getWidth() / 2f);
        float endCenterY   = endLocalY   + (endButton.getHeight() / 2f);

        showWinningLineAndDelay(startCenterX, startCenterY, endCenterX, endCenterY);
    }
}

package put.ai.games.Kolec_Nowacki;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class Kolec_Nowacki extends Player {

    private Random random = new Random();

    @Override
    public String getName() {
        return "Paweł Kolec 155873 Adam Nowacki 155838";
    }

    @Override
    public Move nextMove(Board b) {
        long startTime = System.currentTimeMillis();
        long timeLimit = getTime();
        List<Move> moves = b.getMovesFor(getColor());
        if (moves.size() == 1) {
            return moves.get(0);
        }
        int maxDepth = 3;
        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Move move : moves) {
            b.doMove(move);
            double value = alphaBeta(b,
                    maxDepth - 1,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    false,
                    startTime,
                    timeLimit);
            b.undoMove(move);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
            if (System.currentTimeMillis() - startTime > timeLimit * 0.90) {
                break;
            }
        }
        if (bestMove == null) {
            return moves.get(random.nextInt(moves.size()));
        }
        return bestMove;
    }

    private double alphaBeta(Board board,
                             int depth,
                             double alpha,
                             double beta,
                             boolean isMaximizingPlayer,
                             long startTime,
                             long timeLimit) {

        Player.Color w = board.getWinner(getColor());
        if (w != null) {
            if (w == getColor()) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
        if (depth == 0 || System.currentTimeMillis() - startTime > timeLimit * 0.95) {
            return evaluateBoard(board);
        }
        Player.Color current = isMaximizingPlayer ? getColor() : oppositeColor(getColor());
        List<Move> moves = board.getMovesFor(current);
        if (moves.isEmpty()) {
            return evaluateBoard(board);
        }
        if (isMaximizingPlayer) {
            double value = Double.NEGATIVE_INFINITY;
            for (Move m : moves) {
                board.doMove(m);
                value = Math.max(value,
                        alphaBeta(board, depth - 1, alpha, beta, false, startTime, timeLimit));
                board.undoMove(m);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break;
                }
                if (System.currentTimeMillis() - startTime > timeLimit * 0.95) {
                    break;
                }
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (Move m : moves) {
                board.doMove(m);
                value = Math.min(value,
                        alphaBeta(board, depth - 1, alpha, beta, true, startTime, timeLimit));
                board.undoMove(m);
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }
                if (System.currentTimeMillis() - startTime > timeLimit * 0.95) {
                    break;
                }
            }
            return value;
        }
    }

    private double evaluateBoard(Board b) {
        double score = 0.0;
        int myPawns = 0;
        int enemyPawns = 0;
        int size = b.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Player.Color col = b.getState(r, c);
                if (col == getColor()) {
                    myPawns++;
                } else if (col == oppositeColor(getColor())) {
                    enemyPawns++;
                }
            }
        }
        score = myPawns - enemyPawns;
        return score;
    }

    private Player.Color oppositeColor(Player.Color c) {
        return (c == Player.Color.PLAYER1) ? Player.Color.PLAYER2 : Player.Color.PLAYER1;
    }
}
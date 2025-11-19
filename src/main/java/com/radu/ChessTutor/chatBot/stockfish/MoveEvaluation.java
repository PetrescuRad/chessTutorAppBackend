package com.radu.ChessTutor.chatBot.stockfish;

public class MoveEvaluation {
    private String bestMove;
    private String evalType; // "cp" or "mate"
    private int eval;
    private int depth;
    private String pv;

    public MoveEvaluation(String bestMove, String evalType, int eval, int depth, String pv) {
        this.bestMove = bestMove;
        this.evalType = evalType;
        this.eval = eval;
        this.depth = depth;
        this.pv = pv;
    }

    // Getters and setters
    public String getBestMove() { return bestMove; }
    public String getEvalType() { return evalType; }
    public int getEval() { return eval; }
    public int getDepth() { return depth; }
    public String getPv() { return pv; }

    @Override
    public String toString() {
        return String.format("Best move: %s | Eval: %s %d | Depth: %d | PV: %s",
                bestMove, evalType, eval, depth, pv);
    }
}

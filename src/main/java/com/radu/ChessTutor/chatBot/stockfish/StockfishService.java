package com.radu.ChessTutor.chatBot.stockfish;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StockfishService {

    private Process engine;
    private BufferedWriter writer;
    private BufferedReader reader;

    @Value("${stockfish.path}")
    private String stockfishPath;

    @PostConstruct
    public void startEngine() {
        System.out.println("Starting Stockfish engine from: " + stockfishPath);
        try {
            engine = new ProcessBuilder(stockfishPath)
                    .redirectErrorStream(true)
                    .start();

            writer = new BufferedWriter(new OutputStreamWriter(engine.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));

            // Initialize the engine
            sendCommand("uci");
            waitForOutput("uciok"); // wait for UCI handshake to complete

            sendCommand("isready");
            waitForOutput("readyok"); // wait until Stockfish reports ready

            System.out.println("✅ Stockfish engine is ready!");
        } catch (IOException e) {
            System.err.println("❌ Failed to start stockfish: " + e.getMessage());
        }
    }


    @PreDestroy
    public void stopEngine() {
        System.out.println("Stopping Stockfish engine...");
        try {
            sendCommand("quit");
            if (engine != null) engine.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    private void waitForOutput(String expected) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Engine says: " + line);
            if (line.contains(expected)) {
                break;
            }
        }
    }

    public MoveEvaluation evaluatePosition(String fen) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth 15");
        String output = readUntilBestMove();

        return parseEvaluation(output);
    }

    private String readUntilBestMove() throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
            if (line.startsWith("bestmove"))
                break;
        }
        return sb.toString();
    }

    private MoveEvaluation parseEvaluation(String output) {
        String bestMove = "unknown";
        int eval = 0;
        String evalType = "cp";
        int depth = 0;
        String pvLine = "";

        // Match bestmove
        Matcher bm = Pattern.compile("bestmove\\s(\\S+)").matcher(output);
        if (bm.find()) bestMove = bm.group(1);

        // Match depth
        Matcher depthMatch = Pattern.compile("depth (\\d+)").matcher(output);
        while (depthMatch.find()) {
            depth = Integer.parseInt(depthMatch.group(1)); // last match is deepest
        }

        // Match evaluation: can be "cp" or "mate"
        Matcher evalMatch = Pattern.compile("score (cp|mate) (-?\\d+)").matcher(output);
        while (evalMatch.find()) {
            evalType = evalMatch.group(1);
            eval = Integer.parseInt(evalMatch.group(2)); // last one = final eval
        }

        // Match PV line
        Matcher pvMatch = Pattern.compile(" pv (.+)").matcher(output);
        while (pvMatch.find()) {
            pvLine = pvMatch.group(1); // last one = best PV
        }

        return new MoveEvaluation(bestMove, evalType, eval, depth, pvLine);
    }
}
package com.radu.ChessTutor.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "games")
public class ChessGame {

    @Id
    @Column(length = 500)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String pgn;

    private String timeControl;
    private long endTime;
    private boolean rated;
    @Column(columnDefinition = "TEXT")
    private String tcn;
    private UUID uuid;

    @Column(columnDefinition = "TEXT")
    private String initialSetup;

    @Column(length = 500)
    private String fen;
    private String timeClass;
    private String rules;
    @Column(length = 500)
    private String eco;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "rating", column = @Column(name = "white_rating")),
            @AttributeOverride(name = "result", column = @Column(name = "white_result")),
            @AttributeOverride(name = "playerId", column = @Column(name = "white_id")),
            @AttributeOverride(name = "username", column = @Column(name = "white_username")),
            @AttributeOverride(name = "uuid", column = @Column(name = "white_uuid"))
    })
    private Player white;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "rating", column = @Column(name = "black_rating")),
            @AttributeOverride(name = "result", column = @Column(name = "black_result")),
            @AttributeOverride(name = "playerId", column = @Column(name = "black_id")),
            @AttributeOverride(name = "username", column = @Column(name = "black_username")),
            @AttributeOverride(name = "uuid", column = @Column(name = "black_uuid"))
    })
    private Player black;

    public ChessGame() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public String getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        this.timeControl = timeControl;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public String getTcn() {
        return tcn;
    }

    public void setTcn(String tcn) {
        this.tcn = tcn;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getInitialSetup() {
        return initialSetup;
    }

    public void setInitialSetup(String initialSetup) {
        this.initialSetup = initialSetup;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public String getTimeClass() {
        return timeClass;
    }

    public void setTimeClass(String timeClass) {
        this.timeClass = timeClass;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public Player getWhite() {
        return white;
    }

    public void setWhite(Player white) {
        this.white = white;
    }

    public Player getBlack() {
        return black;
    }

    public void setBlack(Player black) {
        this.black = black;
    }
}

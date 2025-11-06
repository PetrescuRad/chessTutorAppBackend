package com.radu.ChessTutor.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
    @JsonProperty("player_id")
    private Long id;
    @JsonProperty("@id")
    private String profile;
    private String url;
    private String username;
    private int joined;
    private String country;
    private int lastOnline;
    private String status;
    private boolean isStreamer;
    private boolean verified;
    private String league;

    public UserDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(int lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isStreamer() {
        return isStreamer;
    }

    public void setStreamer(boolean streamer) {
        isStreamer = streamer;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", profile='" + profile + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", joined=" + joined +
                ", country='" + country + '\'' +
                ", lastOnline=" + lastOnline +
                ", status='" + status + '\'' +
                ", isStreamer=" + isStreamer +
                ", verified=" + verified +
                ", league='" + league + '\'' +
                '}';
    }
}

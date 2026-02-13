package com.monopoly.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "creater_id", nullable = false)
    private Long creater;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "secondplayer_id", nullable = true)
    private Long secondplayer;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "thirdplayer_id", nullable = true)
    private Long thirdplayer;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "fourthplayer_id", nullable = true)
    private Long fourthplayer;

    @Column(nullable = false)
    private Integer isActive;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Long winner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreater() {
        return creater;
    }

    public void setCreater(Long creater) {
        this.creater = creater;
    }

    public Long getSecondplayer() {
        return secondplayer;
    }

    public void setSecondplayer(Long secondplayer) {
        this.secondplayer = secondplayer;
    }

    public Long getThirdplayer() {
        return thirdplayer;
    }

    public void setThirdplayer(Long thirdplayer) {
        this.thirdplayer = thirdplayer;
    }

    public Long getFourthplayer() {
        return fourthplayer;
    }

    public void setFourthplayer(Long fourthplayer) {
        this.fourthplayer = fourthplayer;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Long getWinner() {
        return winner;
    }

    public void setWinner(Long winner) {
        this.winner = winner;
    }
}

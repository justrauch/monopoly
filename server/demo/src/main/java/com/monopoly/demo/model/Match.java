package com.monopoly.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creater_id", nullable = true)
    private User creater;

    @ManyToOne
    @JoinColumn(name = "secondplayer_id", nullable = true)
    private User secondplayer;

    @ManyToOne
    @JoinColumn(name = "thirdplayer_id", nullable = true)
    private User thirdplayer;

    @ManyToOne
    @JoinColumn(name = "fourthplayer_id", nullable = true)
    private User fourthplayer;

    @Column(nullable = false)
    private Integer isActive;

    @Column(nullable = false)
    private Integer community_money = 0;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    // --- Getter & Setter ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreater() {
        return creater;
    }

    public void setCreater(User creater) {
        this.creater = creater;
    }

    public User getSecondplayer() {
        return secondplayer;
    }

    public void setSecondplayer(User secondplayer) {
        this.secondplayer = secondplayer;
    }

    public User getThirdplayer() {
        return thirdplayer;
    }

    public void setThirdplayer(User thirdplayer) {
        this.thirdplayer = thirdplayer;
    }

    public User getFourthplayer() {
        return fourthplayer;
    }

    public void setFourthplayer(User fourthplayer) {
        this.fourthplayer = fourthplayer;
    }

    public Integer getCommunityMoney() {
        return community_money;
    }

    public void setCommunityMoney(Integer community_money) {
        this.community_money = community_money;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }
}

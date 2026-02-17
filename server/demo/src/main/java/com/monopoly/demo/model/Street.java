package com.monopoly.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "streets")
public class Street {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer houses = 0;

    @Column(nullable = false)
    private Integer hotels = 0;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "street_index", nullable = false)
    private int streetIndex;

    @Column(nullable = false)
    private Boolean isSpecial;

    // === Relations ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    // ===== Getter & Setter =====

    public Long getId() {
        return id;
    }

    public Integer getHouses() {
        return houses;
    }

    public void setHouses(Integer houses) {
        this.houses = houses;
    }

    public Integer getHotels() {
        return hotels;
    }

    public void setHotels(Integer hotels) {
        this.hotels = hotels;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getIndex() {
        return streetIndex;
    }

    public void setIndex(Integer streetindex) {
        this.streetIndex = streetindex;
    }

    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}

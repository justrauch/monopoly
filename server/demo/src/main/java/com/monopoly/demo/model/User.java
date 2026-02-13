package com.monopoly.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String password;

    private int money;
    private int position;
    private int figure;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public int getFigure() { return figure; }
    public void setFigure(int figure) { this.figure = figure; }
}

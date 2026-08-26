package com.example.demo.model;

/** 依舊專案 model.Player：玩家持有 15 張手牌與三個 Round。 */
public class Player {
    private String name;
    private final Card[] hand = new Card[15];
    private final Round[] rounds = { new Round(1, 3), new Round(2, 5), new Round(3, 5) };

    public Player() { for (int i = 0; i < hand.length; i++) hand[i] = new Card(); }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Card[] getHand() { return hand; }
    public Round[] getRounds() { return rounds; }
}

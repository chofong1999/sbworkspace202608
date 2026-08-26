package com.example.demo.model;

/** 依舊專案 model.Card 保留的卡牌模型。 */
public class Card {
    private String name = "";
    private String suits = "";
    private int suitsStrength = -1;
    private int point = -1;
    private int pointA14 = -1;
    private int number = -1;
    private int player = -1;
    private int position = -1;
    private int duplicateNumber;

    public Card() { }
    public Card(String suits, int point, int number) {
        this.suits = normalizeSuit(suits); this.point = point; this.number = number;
        pointA14 = point == 1 ? 14 : point;
        name = this.suits + switch (point) { case 1 -> "A"; case 11 -> "J"; case 12 -> "Q"; case 13 -> "K"; default -> String.valueOf(point); };
        suitsStrength = switch (this.suits) { case "♠" -> 3; case "♡" -> 2; case "♢" -> 1; case "♣" -> 0; default -> -2; };
    }
    /** 保留目前 Web 呼叫方式，內部仍使用舊模型欄位。 */
    public Card(int number, String suits, int point) { this(suits, point, number); }

    public void copy(Card card) {
        name = card.name; suits = card.suits; suitsStrength = card.suitsStrength; point = card.point;
        pointA14 = card.pointA14; number = card.number; player = card.player;
        position = card.position; duplicateNumber = card.duplicateNumber;
    }
    private static String normalizeSuit(String suit) {
        if ("♥".equals(suit)) return "♡";
        if ("♦".equals(suit)) return "♢";
        return suit;
    }

    public int id() { return number; }
    public String suit() { return suits; }
    public int rank() { return point; }
    public int suitStrength() { return suitsStrength; }
    public int highRank() { return pointA14; }
    public String name() { return name; }
    public String getName() { return name; }
    public String getSuits() { return suits; }
    public int getSuits_strength() { return suitsStrength; }
    public int getPoint() { return point; }
    public int getPoint_A14() { return pointA14; }
    public int getNumber() { return number; }
    public int getPlayer() { return player; }
    public int getPosition() { return position; }
    public int getDuplicate_number() { return duplicateNumber; }
    public void setName(String value) { name = value; }
    public void setSuits(String value) { suits = value; }
    public void setSuits_strength(int value) { suitsStrength = value; }
    public void setPoint(int value) { point = value; }
    public void setPoint_A14(int value) { pointA14 = value; }
    public void setNumber(int value) { number = value; }
    public void setPlayer(int value) { player = value; }
    public void setPosition(int value) { position = value; }
    public void setDuplicate_number(int value) { duplicateNumber = value; }
}

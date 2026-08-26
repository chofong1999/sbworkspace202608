package com.example.demo.model;

import com.example.demo.util.CardSort;

/** 依舊專案 model.Game：管理牌堆、兩位玩家、洗牌與發牌。房間連線狀態不放進此類別。 */
public class Game {
    private final Card[] deck = new Card[52];
    private final Player[] players = { new Player(), new Player() };

    public Game() {
        String[] suits = { "♠", "♡", "♢", "♣" };
        for (int suit = 0; suit < suits.length; suit++) for (int point = 1; point <= 13; point++) {
            deck[suit * 13 + point - 1] = new Card(suits[suit], point, suit * 13 + point - 1);
        }
        players[0].setName("玩家一"); players[1].setName("玩家二");
        shuffle(); deal();
    }

    /** 保留舊版逐一抽取空位置的均勻洗牌概念。 */
    private void shuffle() {
        for (int remaining = deck.length; remaining > 1; remaining--) {
            int selected = (int) (Math.random() * remaining);
            Card temporary = deck[selected]; deck[selected] = deck[remaining - 1]; deck[remaining - 1] = temporary;
        }
        for (int i = 0; i < deck.length; i++) deck[i].setPosition(i);
    }

    public void deal() {
        for (int i = 0; i < 30; i++) {
            int playerIndex = i % 2; int handIndex = i / 2;
            players[playerIndex].getHand()[handIndex].copy(deck[i]);
            players[playerIndex].getHand()[handIndex].setPlayer(playerIndex + 1);
        }
        CardSort.sortDependsOnNumber(players[0].getHand());
        CardSort.sortDependsOnNumber(players[1].getHand());
    }

    public Card[] getDeck() { return deck; }
    public Player[] getPlayers() { return players; }
}

package com.example.demo.poker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.model.Card;
import com.example.demo.model.Player;
import com.example.demo.util.GameTool;
import com.example.demo.model.HandResult;
import com.example.demo.service.impl.PokerRuleServiceImpl;

class HandEvaluatorTests {
    private final PokerRuleServiceImpl evaluator = new PokerRuleServiceImpl();

    @Test
    void recognizesFiveCardHands() {
        assertEquals("同花順", evaluate(card("♠", 10), card("♠", 11), card("♠", 12), card("♠", 13), card("♠", 1)).getName());
        assertEquals("鐵支", evaluate(card("♠", 9), card("♡", 9), card("♢", 9), card("♣", 9), card("♠", 2)).getName());
        assertEquals("葫蘆", evaluate(card("♠", 8), card("♡", 8), card("♢", 8), card("♠", 3), card("♡", 3)).getName());
        assertEquals("順子", evaluate(card("♠", 1), card("♡", 2), card("♢", 3), card("♣", 4), card("♠", 5)).getName());
    }

    @Test
    void recognizesThreeCardHandsAndComparesThem() {
        HandResult three = evaluate(card("♠", 6), card("♡", 6), card("♢", 6));
        HandResult pair = evaluate(card("♠", 11), card("♡", 11), card("♢", 2));
        assertEquals("三條", three.getName());
        assertEquals("一對", pair.getName());
        assertTrue(three.compare(pair) > 0);
    }

    @Test
    void autoChooseUsesOriginalThreeFiveFiveStructure() {
        Player player = new Player();
        String[] suits = { "♠", "♡", "♢", "♣" };
        for (int i = 0; i < player.getHand().length; i++) {
            player.getHand()[i].copy(new Card(suits[i % 4], i % 13 + 1, i));
        }
        int[] choice = new int[52];
        GameTool.auto_choose_best(player, choice);
        assertEquals(3, count(choice, 1));
        assertEquals(5, count(choice, 2));
        assertEquals(5, count(choice, 3));
        assertEquals(2, count(choice, 0));
    }

    private int count(int[] choices, int slot) {
        int count = 0;
        for (int i = 0; i < 15; i++) if (choices[i] == slot) count++;
        return count;
    }

    private HandResult evaluate(Card... cards) { return evaluator.evaluate(cards); }

    private Card card(String suit, int rank) { return new Card(suit, rank, Math.abs((suit + rank).hashCode())); }
}

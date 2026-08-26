package com.example.demo.util;

import com.example.demo.model.Card;

/** 由舊專案 util.Sort 搬移；集中卡牌排序運算。 */
public final class CardSort {
    private CardSort() { }
    public static void sortDependsOnNumber(Card[] cards) { bubble(cards, (a, b) -> a.getNumber() > b.getNumber()); }
    public static void sortDependsOnPointIncreasing(Card[] cards) { bubble(cards, (a, b) -> a.getPoint() > b.getPoint()); }
    public static void sortDependsOnPointA14Decreasing(Card[] cards) { bubble(cards, (a, b) -> a.getPoint_A14() < b.getPoint_A14()); }
    public static void sortDependsOnSuitDecreasing(Card[] cards) { bubble(cards, (a, b) -> a.getSuits_strength() < b.getSuits_strength()); }
    public static void sortDependsOnDuplicateDecreasing(Card[] cards) { bubble(cards, (a, b) -> a.getDuplicate_number() < b.getDuplicate_number()); }
    private static void bubble(Card[] cards, SwapWhen condition) {
        boolean sorted; Card temporary = new Card();
        do { sorted = true; for (int i = 0; i < cards.length - 1; i++) if (condition.test(cards[i], cards[i + 1])) {
            temporary.copy(cards[i]); cards[i].copy(cards[i + 1]); cards[i + 1].copy(temporary); sorted = false;
        } } while (!sorted);
    }
    private interface SwapWhen { boolean test(Card left, Card right); }
}

package com.example.demo.model;

import com.example.demo.util.CardSort;

/** 依舊專案 model.Round 保留牌型與強度。 */
public class Round {
    private final int roundNumber;
    private final Card[] roundHand;
    private String handType;
    private int handTypeNumber = -1;
    private long handStrength;

    public Round(int roundNumber, int handNumber) {
        this.roundNumber = roundNumber; roundHand = new Card[handNumber];
        for (int i = 0; i < handNumber; i++) roundHand[i] = new Card();
    }

    public void determineHand() {
        CardSort.sortDependsOnPointIncreasing(roundHand);
        int duplicateValue = 0;
        for (Card card : roundHand) card.setDuplicate_number(0);
        for (int i = 0; i < roundHand.length; i++) for (int j = 0; j < roundHand.length; j++) if (i != j && roundHand[i].getPoint() == roundHand[j].getPoint()) {
            duplicateValue++; roundHand[i].setDuplicate_number(roundHand[i].getDuplicate_number() + 1);
        }
        switch (duplicateValue) {
            case 0 -> { handType = "高牌"; handTypeNumber = 0; }
            case 2 -> { handType = "一對"; handTypeNumber = 1; }
            case 4 -> { handType = "兩對"; handTypeNumber = 2; }
            case 6 -> { handType = "三條"; handTypeNumber = 3; }
            case 8 -> { handType = "葫蘆"; handTypeNumber = 6; }
            case 12 -> { handType = "鐵支"; handTypeNumber = 7; }
            default -> { handType = "牌型特徵值異常"; handTypeNumber = -1; }
        }
        if (handTypeNumber == 0 && roundHand.length == 5) {
            boolean flush = true;
            for (int i = 0; i < 4; i++) if (!roundHand[i].getSuits().equals(roundHand[i + 1].getSuits())) flush = false;
            boolean straight = true;
            int start = roundHand[0].getPoint() == 1 && roundHand[1].getPoint() == 10 ? 1 : 0;
            for (int i = start; i < 4; i++) if (roundHand[i].getPoint() + 1 != roundHand[i + 1].getPoint()) straight = false;
            if (flush) { handType = "同花"; handTypeNumber = 5; }
            if (straight) { handType = "順子"; handTypeNumber = 4; }
            if (flush && straight) { handType = "同花順"; handTypeNumber = 8; }
        }
    }

    public void determineHandStrength() {
        handStrength = 0; determineHand(); handStrength = handTypeNumber * (long) Math.pow(10, 11);
        switch (handTypeNumber) {
            case 0, 5 -> {
                CardSort.sortDependsOnPointA14Decreasing(roundHand);
                for (int i = 0; i < roundHand.length; i++) handStrength += roundHand[i].getPoint_A14() * (long) Math.pow(10, 9 - i * 2);
                handStrength += roundHand[0].getSuits_strength();
            }
            case 1 -> {
                groupSort(); handStrength += roundHand[0].getPoint_A14() * (long) Math.pow(10, 9);
                for (int i = 2; i < roundHand.length; i++) handStrength += roundHand[i].getPoint_A14() * (long) Math.pow(10, 11 - i * 2);
                handStrength += roundHand[0].getSuits_strength();
            }
            case 2 -> {
                groupSort(); handStrength += roundHand[0].getPoint_A14() * (long) Math.pow(10, 9);
                handStrength += roundHand[2].getPoint_A14() * (long) Math.pow(10, 7);
                handStrength += roundHand[4].getPoint_A14() * (long) Math.pow(10, 5);
                handStrength += roundHand[0].getSuits_strength();
            }
            case 3, 6, 7 -> { groupSort(); handStrength += roundHand[0].getPoint_A14() * (long) Math.pow(10, 9); }
            case 4, 8 -> {
                CardSort.sortDependsOnPointIncreasing(roundHand);
                if (roundHand[0].getPoint() == 1 && roundHand[1].getPoint() == 10) handStrength += (roundHand[1].getPoint_A14() + 1) * (long) Math.pow(10, 9);
                else handStrength += roundHand[1].getPoint_A14() * (long) Math.pow(10, 9);
                handStrength += (handTypeNumber == 4 ? roundHand[4] : roundHand[0]).getSuits_strength();
            }
            default -> { }
        }
    }

    private void groupSort() {
        CardSort.sortDependsOnSuitDecreasing(roundHand); CardSort.sortDependsOnPointA14Decreasing(roundHand); CardSort.sortDependsOnDuplicateDecreasing(roundHand);
    }
    public int getRound_number() { return roundNumber; }
    public Card[] getRound_hand() { return roundHand; }
    public String getHand_type() { return handType; }
    public int getHand_type_number() { return handTypeNumber; }
    public long getHand_strength() { return handStrength; }
    public String showRoundHand() { StringBuilder text = new StringBuilder(); for (Card card : roundHand) text.append(card.getName()).append(' '); return text.toString(); }
}

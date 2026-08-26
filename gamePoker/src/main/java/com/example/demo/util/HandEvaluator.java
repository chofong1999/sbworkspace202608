package com.example.demo.util;

import java.util.List;

import com.example.demo.model.Card;
import com.example.demo.model.Round;
import com.example.demo.poker.HandResult;

import org.springframework.stereotype.Service;

@Service
public class HandEvaluator {
    public HandResult evaluate(List<Card> cards) {
        if (cards.size() != 3 && cards.size() != 5) throw new IllegalArgumentException("牌組必須是 3 張或 5 張");
        Round round = new Round(cards.size() == 3 ? 1 : 2, cards.size());
        for (int i = 0; i < cards.size(); i++) round.getRound_hand()[i].copy(cards.get(i));
        round.determineHandStrength();
        return new HandResult(round.getHand_type(), round.getHand_strength());
    }
}

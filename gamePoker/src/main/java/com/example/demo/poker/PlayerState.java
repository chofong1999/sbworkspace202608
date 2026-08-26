package com.example.demo.poker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.model.Card;

public class PlayerState {
    private final List<Card> hand = new ArrayList<>();
    private final Map<Integer, Integer> choices = new HashMap<>();
    private boolean confirmed;

    public List<Card> getHand() { return hand; }
    public Map<Integer, Integer> getChoices() { return choices; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}

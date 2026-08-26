package com.example.demo.poker;

public record HandResult(String type, long strength) implements Comparable<HandResult> {
    @Override
    public int compareTo(HandResult other) {
        return Long.compare(strength, other.strength);
    }
}

package com.example.demo.util;

import com.example.demo.model.Card;
import com.example.demo.model.Player;
import com.example.demo.model.Round;

/** 由舊專案 util.GameTool 搬移：第三輪、第二輪、第一輪依序挑選最佳牌組。 */
public final class GameTool {
    private GameTool() { }

    public static void autoChooseBest(Player player, int[] choice) {
        for (int i = 0; i < choice.length; i++) choice[i] = 0;
        for (Round round : player.getRounds()) for (Card card : round.getRound_hand()) card.copy(new Card());
        Card[] hand = copyCards(player.getHand()); boolean[] used = new boolean[hand.length];
        Card[] round3 = findBestCards(hand, used, 5); markUsed(hand, used, round3);
        Card[] round2 = findBestCards(hand, used, 5); markUsed(hand, used, round2);
        Card[] round1 = findBestCards(hand, used, 3);
        putRound(player, choice, 0, round1); putRound(player, choice, 1, round2); putRound(player, choice, 2, round3);
    }

    private static Card[] copyCards(Card[] cards) {
        Card[] copy = new Card[cards.length];
        for (int i = 0; i < cards.length; i++) { copy[i] = new Card(); copy[i].copy(cards[i]); }
        return copy;
    }
    private static Card[] findBestCards(Card[] hand, boolean[] used, int count) {
        Card[] current = new Card[count], best = new Card[count]; long[] bestScore = { -1 };
        findBestCards(hand, used, count, 0, 0, current, best, bestScore); return best;
    }
    private static void findBestCards(Card[] hand, boolean[] used, int count, int start, int depth, Card[] current, Card[] best, long[] bestScore) {
        if (depth == count) {
            long score = score(current);
            if (score > bestScore[0]) { bestScore[0] = score; for (int i = 0; i < count; i++) { best[i] = new Card(); best[i].copy(current[i]); } }
            return;
        }
        for (int i = start; i < hand.length; i++) if (!used[i]) {
            current[depth] = hand[i]; findBestCards(hand, used, count, i + 1, depth + 1, current, best, bestScore);
        }
    }
    private static long score(Card[] cards) {
        Round round = new Round(cards.length == 3 ? 1 : 2, cards.length);
        for (int i = 0; i < cards.length; i++) round.getRound_hand()[i].copy(cards[i]);
        round.determineHandStrength(); return round.getHand_strength();
    }
    private static void markUsed(Card[] hand, boolean[] used, Card[] selected) {
        for (Card selectedCard : selected) for (int i = 0; i < hand.length; i++) if (!used[i] && hand[i].getNumber() == selectedCard.getNumber()) { used[i] = true; break; }
    }
    private static void putRound(Player player, int[] choice, int roundIndex, Card[] cards) {
        for (int i = 0; i < cards.length; i++) {
            player.getRounds()[roundIndex].getRound_hand()[i].copy(cards[i]); choice[cards[i].getNumber()] = roundIndex + 1;
        }
    }
}

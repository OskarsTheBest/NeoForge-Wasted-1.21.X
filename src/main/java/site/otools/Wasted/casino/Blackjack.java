package site.otools.Wasted.casino;

import net.minecraft.util.RandomSource;

/**
 * Blackjack rules helpers. Cards are encoded 0..51 as suit*13 + rank,
 * where suit 0=hearts,1=diamonds,2=clubs,3=spades and rank 0=Ace,1=2,...,9=10,10=J,11=Q,12=K.
 */
public final class Blackjack {
    private Blackjack() {}

    public static final int MAX_CARDS = 12;

    public static int drawCard(RandomSource random) {
        return random.nextInt(52);
    }

    public static int suit(int card) {
        return card / 13;
    }

    public static int rank(int card) {
        return card % 13;
    }


    public static int cardValue(int card) {
        int rank = rank(card);
        if (rank == 0) return 11;       // ace
        if (rank >= 9) return 10;       // 10, J, Q, K
        return rank + 1;                // 2..9
    }


    public static int handValue(int[] cards, int count) {
        int total = 0;
        int aces = 0;
        for (int i = 0; i < count; i++) {
            total += cardValue(cards[i]);
            if (rank(cards[i]) == 0) aces++;
        }
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    public static boolean isBlackjack(int[] cards, int count) {
        return count == 2 && handValue(cards, count) == 21;
    }
}

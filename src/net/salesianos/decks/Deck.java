package net.salesianos.decks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<String> cards;

    public Deck() {
        cards = new ArrayList<>(List.of(
                "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"));
        Collections.shuffle(cards);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public String drawCard() {
        return cards.remove(0);
    }
}

package common;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private List<Card> hand = new ArrayList<Card>();

    public Hand () {}

    public void setHand(List<Card> h) {
        this.hand = h;
    }

    public List<Card> getHand() {
        return (this.hand);
    }

    public void pushCard(Card card) {
        hand.add(card);
    }

    public boolean contains(Card card) {
        for (Card current : hand) {
            if (card.equals(current))
                return true;
        }
        return false;
    }

    public boolean hasColor(String cardColor) {
        for (Card current : hand) {
            if (cardColor.equals(current.getColor()))
                return true;
        }
        return false;
    }
}

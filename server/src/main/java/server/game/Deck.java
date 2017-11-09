package server.game;

import common.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Deck {

    private static Deck instance;
    private List<Card> deck = new ArrayList<Card>();
    private final String[] value = {"7", "8", "9", "10", "jack", "queen", "king", "ace"};
    private final String[] color = {"spade", "club", "heart", "diamond"};
    private int cardTotal = 32;

    public static Deck getInstance(){
        if (instance == null)
            instance = new Deck();
        return instance;
    }

    public String[] getValue() {
        return (this.value);
    }

    private void initColor(int color)
    {
        for (int i = 0; i <= 7 ; i++) {
            deck.add(new Card(this.color[color], this.value[i]));

        }
    }

    public void initDeck()
    {
        this.cardTotal = 32;
        for (int i = 0; i <= 3; i++) {
            initColor(i);
        }
        Collections.shuffle(deck);
    }

    public void displayDeck(){
        int i = 0;
        for (common.Card Card : this.deck)
        {
            System.out.println(Card.getColor());
            System.out.println(Card.getValue());
            i++;
        }
    }

    public List<Card> getDeck() {
        return (this.deck);
    }

    public List<Card> distributeOneHandFirst() {
        List<Card> hand = new ArrayList<Card>();
        for (int i = 0; i < 8 ; i++) {
            hand.add(new Card(deck.get(0).getColor(), deck.get(0).getValue()));
            deck.remove(0);
            this.cardTotal -= 1;
        }
        return (hand);
    }

    public boolean isValidColor(String color) {
        return (Arrays.asList(this.color).contains(color));
    }

    public boolean isValidValue(String value) {
        return (Arrays.asList(this.value).contains(value));
    }

}

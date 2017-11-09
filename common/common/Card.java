package common;

public class Card {

    private final String color;
    private final String value;

    public Card(String color, String value) {
        this.color = color;
        this.value = value;
    }

    public String getValue() {
        return (this.value);
    }

    public String getColor() {
        return (this.color);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Card)) {
            return false;
        }
        Card that = (Card) other;
        return this.color.equals(that.color)
                && this.value.equals(that.value);
    }

    public String cardToString(Card card) {
        return (card.value + " " + card.color);
    }

}
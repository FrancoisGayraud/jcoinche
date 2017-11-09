package common;

public class CardPair {
    private int teamOwner;
    private int id;
    private Card card;

    public CardPair(Card card, int teamOwner, int id) {
        this.card = card;
        this.teamOwner = teamOwner;
        this.id = id;
    }

    public Card getCard() {
        return (this.card);
    }

    public int getTeamOwner() {
        return (this.teamOwner);
    }

    public int getId() {
        return (this.id);
    }

}

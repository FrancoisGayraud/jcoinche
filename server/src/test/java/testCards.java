import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.game.Deck;
import static org.junit.jupiter.api.Assertions.assertEquals;

import common.*;

import java.util.ArrayList;
import java.util.List;

class TestCards {
	private static List<Card> deckCards;

	@BeforeAll
	static void initTests() {
		Deck.getInstance().initDeck();
		deckCards = Deck.getInstance().getDeck();
	}

	@Test
	void testDeckSize() {
		assertEquals(32, deckCards.size());
	}

	@Test
	void testDeckCards() {
		for (Card card : deckCards) {
			assertEquals(true, Deck.getInstance().isValidColor(card.getColor()));
			assertEquals(true, Deck.getInstance().isValidValue(card.getValue()));
		}
	}

	@Test
	void testCardEquals() {
		Card card = new Card("diamond", "ace");
		Card sameCard = new Card("diamond", "ace");

		assertEquals(true, card.equals(sameCard));
	}

	@Test
	void testDistribute() {
		Hand[] hands = new Hand[4];

		for (int i = 0; i < 4; i++) {
			hands[i] = new Hand();
			hands[i].setHand(Deck.getInstance().distributeOneHandFirst());
		}

		for (int i = 0 ; i < 3 ; i++) {
			hands[i].getHand().retainAll(hands[i + 1].getHand());
			assertEquals(0, hands[i].getHand().size());
		}
	}
}

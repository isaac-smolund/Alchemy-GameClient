package models.cards;

import models.exceptions.*;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> cards;

    public Deck(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public Deck() {
        cards = new ArrayList<Card>();
    }

    public Card draw() throws DeckOutOfCardsException {
        try {
            return cards.remove(0);
        }
        catch (IndexOutOfBoundsException e) {
            throw new DeckOutOfCardsException("No more cards to draw!", null);
        }
    }

    public int cardsRemaining() {
        return cards.size();
    }

    public ArrayList<Card> draw(int numberOfTimes) throws DeckOutOfCardsException {
        ArrayList<Card> toReturn = new ArrayList<Card>();
        for (int i = 0; i < numberOfTimes; i++) {
            if (cardsRemaining() <= 0) {
                throw new DeckOutOfCardsException("No more cards to draw!", null);
            }
            else {
                toReturn.add(draw());
            }
        }
        return toReturn;
    }

    public void shuffle() {
        ArrayList<Card> shuffledCards = new ArrayList<Card>();
        Random rand = new Random();
        while (!cards.isEmpty()) {
            shuffledCards.add(cards.remove(rand.nextInt(cards.size())));
        }
        cards = shuffledCards;
    }

    public void addCard(Card cardToAdd) {
        cards.add(cardToAdd.generateCard());
    }

    public void addCards(Card ... cards) {
        for (Card card : cards) {
            addCard(card);
        }
    }

    public void addCards(ArrayList<Card> cardsToAdd) {
        for (Card card : cardsToAdd) {
            addCard(card);
        }
    }

}
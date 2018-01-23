package models;

import models.cards.Card;
import models.exceptions.*;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards;

    public Hand() {
        this.cards = new ArrayList<Card>();
    }

    public Hand(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card newCard) {
        cards.add(newCard);
    }

    public void addCards(ArrayList<Card> newCards) {
        cards.addAll(newCards);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Card findCard(String cardName) throws CardNotFoundException {
        for (Card card : cards) {
            if (card.getNameNoColor().equalsIgnoreCase(cardName)) {
                return card;
            }
        }
        throw new CardNotFoundException("Your hand does not contain this card!\n", null);
    }

    public Card findCardById(int id) {
        System.out.println("Searching for ID: " + id);
        for (Card card : cards) {
            if (card.getId() == id) {
                return card;
            }
        }
        return null;
    }

    public void playCard(Card card) throws CardNotFoundException {
        if (!cards.remove(card)) {
            throw new CardNotFoundException("Your hand does not contain this card!\n", null);
        }
    }

    public String toString() {
        if (cards.isEmpty()) {
            return "\nYour hand is empty\n";
        }

        StringBuilder toReturn = new StringBuilder("\nYour hand contains:\n");
        for (Card card : cards) {
            toReturn.append(card.getName()).append("\n");
        }
        return toReturn.toString();
    }
}
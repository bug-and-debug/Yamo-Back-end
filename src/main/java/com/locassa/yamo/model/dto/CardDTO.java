package com.locassa.yamo.model.dto;

import com.locassa.yamo.model.Content;
import com.locassa.yamo.model.enums.CardType;
import com.locassa.yamo.model.enums.ContentType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CardDTO implements Serializable {

    private CardType cardType;

    private int cardTypeValue;

    private Set<Content> contents = new HashSet<>();

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public int getCardTypeValue() {
        if (CardType.UNSPECIFIED.getValue() != cardTypeValue) {
            return cardTypeValue;
        } else {
            return null == cardType ? ContentType.UNSPECIFIED.getValue() : cardType.getValue();
        }
    }

    public void setCardTypeValue(int cardTypeValue) {
        this.cardTypeValue = cardTypeValue;
    }

    public Set<Content> getContents() {
        return contents;
    }

    public void setContents(Set<Content> contents) {
        this.contents = contents;
    }
}

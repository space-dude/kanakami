package kana;

import java.awt.image.BufferedImage;

/**
 * 
 * @author Cory Carpenter
 *
 */

public class Card {
	
	public enum type {HIRAGANA, KATAKANA};
	
	private int cardNumber;
	
	private String romanji;
	private BufferedImage kana;
	//button
	//sound
	
	public Card() {
		this("Untitled");
	}
	
	public Card(String romanji) {
		this.romanji = romanji;
	}
	
	public Card(BufferedImage kana, String romanji, int cardNumber) {
		this.kana = kana;
		this.romanji = romanji;
		this.cardNumber = cardNumber;
	}
	
	public String getRomanji() {
		return this.romanji;
	}
	
	public BufferedImage getKana() {
		return this.kana;
	}
	
	public int getCardNumber() {
		return this.cardNumber;
	}
	
	public String toString() {
		return this.romanji;
	}
	
	public void update() {}
	
}
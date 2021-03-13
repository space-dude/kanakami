package kana;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author Cory Carpenter
 * 
 * Lucky Star Borders used with permission, and created by muddy-mudkip
 * http://muddy-mudkip.deviantart.com/art/Lucky-Star-Hiragana-Chart-No-1-158243979
 * 
 * Character Vector (c) Fluke
 * http://www.animecanvas.net/
 * 
 * Lucky Star (c) Kagami YOSHIMIZU
 *
 * To Be Implemented:
 * 	Add Dakuten and Handakuden for Hiragana
 * 	Add Katakana
 * 	Add Reference Charts
 * 
 * 	(Done)Change Quiz to be able to activate both Hiragana and Katakana using if else
 * 	NEED to change currentKanaIndex to know if Dakuten is on or off to view cards
 * 
 */

public class Game implements Runnable, KeyListener {

	private final int WIDTH = 704;
	private final int HEIGHT = 736;

	private JFrame frame;
	private JPanel panel;
	private Canvas canvas;
	private BufferStrategy bufferStrategy;

	public static enum GameState{MAINMENU, STUDYHIRAGANA, STUDYKATAKANA, QUIZHIRAGANA, QUIZKATAKANA, CHART};

	public static GameState gameState;

	private Font font = new Font("kanaRomanjiFont", Font.BOLD, 58);

	private final String[] romanjiStrings = {"a", "i", "u", "e", "o",
			"ka", "ki", "ku", "ke", "ko",
			"sa", "shi", "su", "se", "so",
			"ta", "chi", "tsu", "te", "to",
			"na", "ni", "nu", "ne", "no",
			"ha", "hi", "fu", "he", "ho",
			"ma", "mi", "mu", "me", "mo",
			"ya", "yu", "yo",
			"ra", "ri", "ru", "re", "ro",
			"wa", "wo",
			"n",
			"ga", "gi", "gu", "ge", "go",
			"za", "ji", "zu", "ze", "zo",
			"da", "ji", "ji", "de", "do",
			"ba", "bi", "bu", "be", "bo",
			"pa", "pi", "pu", "pe", "po"};

	private BufferedImage[] hiraganaKana = new BufferedImage[46];
	private BufferedImage[] katakanaKana = new BufferedImage[46];
	private BufferedImage[] hiraganaDakuten = new BufferedImage[25];
	private BufferedImage[] katakanaDakuten = new BufferedImage[25];

	private Card[] hiraganaCardInit = new Card[46]; // Needed to efficiently add cards to decks by allowing for a loop
	private Card[] katakanaCardInit = new Card[46];
	private Card[] hiraganaDakutenCardInit = new Card[25];
	private Card[] katakanaDakutenCardInit = new Card[25];

	private Deck hiraganaDeck = new Deck();
	private Deck katakanaDeck = new Deck();

	private Deck sortedHiraganaDeck = new Deck();
	private Deck sortedKatakanaDeck = new Deck();

	private int currentKanaIndex = 0;

	private boolean isShuffled = false;

	private boolean isDakuten = false;

	private boolean quizOn = false;
	private boolean isQuiz = false;
	private boolean quizSetUp = false;

	private int[] kanaQuizCards = new int[5];
	private int quizCardIndex = 0;
	private int falseCardIndex1 = 0;
	private int falseCardIndex2 = 0;
	private int positionAnswers1 = 0;
	private int positionAnswers2 = 0;
	private int positionAnswers3 = 0;
	private int locationCorrectAnswers = 0;
	private int quizPositionCount = 1;
	private int quizCurserLocation = 50;

	private int mainMenuPositionCount = 1;
	private int mainMenuCurserLocationWidth = 60;
	private int mainMenuCurserLocationHeight = HEIGHT/4 - 10;

	private Random rndm = new Random();


	//Images for Main Menu
	private BufferedImage mainMenu;

	private BufferedImage studyHiraganaImg;

	private BufferedImage studyKatakanaImg;

	private BufferedImage chartsImg;

	private BufferedImage menuSelectImg;

	//Images for Studying Kana
	private BufferedImage kanaKamiLayout;

	private BufferedImage orderDeckImg;

	private BufferedImage shuffleDeckImg;

	private BufferedImage quizOnImg;

	private BufferedImage quizOffImg;

	private BufferedImage dakutenOnImg;
	private BufferedImage dakutenOffImg;

	private BufferedImage currentDakutenImg;
	private BufferedImage currentQuizStatusImg;
	private BufferedImage currentSortStatusImg;

	//Images for Quiz mode
	private BufferedImage kanaKamiQuizLayout;

	private BufferedImage quizSelectImg;


	public Game() {
		frame = new JFrame("KanaKami (‚©‚È?_)");

		panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);

		canvas.addKeyListener(this);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(580, 140); // Specify to computer monitor screen info*0.5
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();

		LoadContent();

		//playSound("Nyanpasu!.wav");

		gameState = GameState.MAINMENU;
	}

	private void InitializeKanaImage() {

		int index = 0;
		int indexDakuten = 0;
		int indexKatakana = 0;

		try {

			//HIRAGANA

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaA.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaI.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaU.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaE.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaO.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaKa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaKi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaKu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaKe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaKo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaSa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaShi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaSu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaSe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaSo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaTa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaChi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaTsu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaTe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaTo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaNa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaNi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaNu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaNe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaNo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaHa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaHi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaFu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaHe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaHo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaMa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaMi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaMu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaMe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaMo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaYa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaYu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaYo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaRa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaRi.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaRu.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaRe.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaRo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaWa.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaWo.png" );

			hiraganaKana[index++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaN.png" );

		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}

		/**
		 * DAKUTEN
		 */
		
		try {

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaGa.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaGi.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaGu.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaGe.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaGo.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaZa.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaJi.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaZu.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaZe.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaZo.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaDa.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaTJi.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaTJi2.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaDe.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaDo.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaBa.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaBi.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaBu.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaBe.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaBo.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaPa.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaPi.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaPu.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaPe.png" );

		   hiraganaDakuten[indexDakuten++] = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaPo.png" );

	   } catch (Exception ex) {
    	   Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
       }

		/**
		 * KATAKANA
		 */

		try {

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaA.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaI.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaU.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaE.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaO.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaKa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaKi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaKu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaKe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaKo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaSa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaShi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaSu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaSe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaSo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaTa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaChi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaTsu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaTe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaTo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaNa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaNi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaNu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaNe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaNo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaHa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaHi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaFu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaHe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaHo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaMa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaMi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaMu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaMe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaMo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaYa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaYu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaYo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaRa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaRi.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaRu.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaRe.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaRo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaWa.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaWo.png" );

			katakanaKana[indexKatakana++] = Utils.getInstance().getBufferedImage( "kana/resources/images/katakanaN.png" );

		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void InitializeKanaCard() {

		for(int i = 0; i < hiraganaCardInit.length; i++) {
			hiraganaCardInit[i] = new Card(hiraganaKana[i], romanjiStrings[i], i);
		}

		for(int i = hiraganaCardInit.length, j = 0; i < hiraganaDakutenCardInit.length + hiraganaCardInit.length-1; i++, j++) {
			hiraganaDakutenCardInit[j] = new Card(hiraganaDakuten[j], romanjiStrings[i], i);
		}

		for(int i = 0; i < katakanaCardInit.length; i++) {
			katakanaCardInit[i] = new Card(katakanaKana[i], romanjiStrings[i], i);
		}
	}

	private void InitializeKanaDeck() {

		for(int i = 0; i < hiraganaCardInit.length; i++) {
			hiraganaDeck.addCard(hiraganaCardInit[i]);
			sortedHiraganaDeck.addCard(hiraganaCardInit[i]);
		}
		
		//TESTING
		for(int i = 0; i < hiraganaDakutenCardInit.length; i++) {
			hiraganaDeck.addCard(hiraganaCardInit[i]);
		}

		for(int i = 0; i < katakanaCardInit.length; i++) {
			katakanaDeck.add(katakanaCardInit[i]);
			sortedKatakanaDeck.addCard(katakanaCardInit[i]);
		}
	}

	private void LoadContent() {

		InitializeKanaImage();
		InitializeKanaCard();
		InitializeKanaDeck();

		try {

			mainMenu = Utils.getInstance().getBufferedImage( "kana/resources/images/StartingScreen2.png" );

			studyHiraganaImg = Utils.getInstance().getBufferedImage( "kana/resources/images/hiraganaButton.png" );

			studyKatakanaImg = Utils.getInstance().getBufferedImage( "kana/resources/images/KatakanaButton.png" );

			chartsImg = Utils.getInstance().getBufferedImage( "kana/resources/images/ChartsButton.png" );

			menuSelectImg = Utils.getInstance().getBufferedImage( "kana/resources/images/MainMenuSelectImg.png" );

			kanaKamiLayout =  Utils.getInstance().getBufferedImage( "kana/resources/images/KanaKamiLayout.png" );

			quizOnImg =  Utils.getInstance().getBufferedImage( "kana/resources/images/QuizOnImg.png" );

			quizOffImg =  Utils.getInstance().getBufferedImage( "kana/resources/images/QuizOffImg.png" );

			orderDeckImg = Utils.getInstance().getBufferedImage( "kana/resources/images/DeckSortedImg.png" );

			shuffleDeckImg = Utils.getInstance().getBufferedImage( "kana/resources/images/DeckShuffledImg.png" );

			dakutenOnImg = Utils.getInstance().getBufferedImage( "kana/resources/images/DakutenOn.png" );

			dakutenOffImg = Utils.getInstance().getBufferedImage( "kana/resources/images/DakutenOff.png" );

			currentQuizStatusImg = quizOffImg;
			currentSortStatusImg = orderDeckImg;
			currentDakutenImg = dakutenOffImg;

			kanaKamiQuizLayout = Utils.getInstance().getBufferedImage( "kana/resources/images/KanaKamiQuizLayout.png" );

			quizSelectImg = Utils.getInstance().getBufferedImage( "kana/resources/images/QuizSelectImg.png" );

		} catch (Exception ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void quizOn() {
		quizOn = true;
		currentQuizStatusImg = quizOnImg;
	}

	private void quizOff() {
		quizOn = false;
		currentQuizStatusImg = quizOffImg;
	}

	private void shuffleDeck(Deck deck) {
		// If isDakuten then change currentKanaIndex to 0
		deck.shuffle();
		currentSortStatusImg = shuffleDeckImg;
	}

	private void organizeDeck(Deck deck) {
		// If isDakuten then change currentKanaIndex to 0
		deck.sortDeck();
		currentSortStatusImg = orderDeckImg;
	}

	private void toMainMenu() {
		gameState = GameState.MAINMENU;
	}

	private void toCharts() {
		gameState = GameState.CHART;
	}

	private void toHiraganaStudy() {
		gameState = GameState.STUDYHIRAGANA;	   	   
	}

	private void toKatakanaStudy() {
		gameState = GameState.STUDYKATAKANA;
	}

	private void dakutenOn() {
		isDakuten = true;
		System.out.println(hiraganaDeck.getSize());
		for(int i = 0; i < hiraganaDakutenCardInit.length; i++) {
			hiraganaDeck.add(hiraganaDakutenCardInit[i]);
			System.out.println(hiraganaDeck.getSize());
			//katakanaDeck.add(katakanaDakutenCardInit[i]);
		}
		
		System.out.println(hiraganaDeck.getSize());

		if(isShuffled) {
			if(gameState == GameState.STUDYHIRAGANA)
				hiraganaDeck.shuffle();
			else
				katakanaDeck.shuffle();
		}
		else {
			if(gameState == GameState.STUDYHIRAGANA)
				hiraganaDeck.sortDeck();
			else
				katakanaDeck.sortDeck();
		}

		currentDakutenImg = dakutenOnImg;
	}

	private void dakutenOff() {
		isDakuten = false;
		for(int i = 0; i < hiraganaDakutenCardInit.length; i++) {
			hiraganaDeck.remove(hiraganaDakutenCardInit[i]);
			katakanaDeck.remove(katakanaDakutenCardInit[i]);
		}
		
		if(isShuffled) {
			if(gameState == GameState.STUDYHIRAGANA)
				hiraganaDeck.shuffle();
			else
				katakanaDeck.shuffle();
		}
		else {
			if(gameState == GameState.STUDYHIRAGANA)
				hiraganaDeck.sortDeck();
			else
				katakanaDeck.sortDeck();
		}

		currentDakutenImg = dakutenOffImg;
	}



	long desiredFPS = 60;
	long desiredDeltaLoop = (1000*1000*1000)/desiredFPS;

	boolean running = true;

	public void run() {

		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		while(running) {
			beginLoopTime = System.nanoTime();

			render(); 
			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update((int) ((currentUpdateTime - lastUpdateTime)/(1000*1000)));

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if(deltaLoop > desiredDeltaLoop) {
				//Do nothing. We are already late.
			} else {
				try {
					Thread.sleep((desiredDeltaLoop - deltaLoop)/(1000*1000));
				} catch(InterruptedException e) {
					//Do nothing
				}
			}
		}
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		g.setFont(font);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}

	private void setQuizAnswers() {

		int testValue = rndm.nextInt(300);

		//make the location to put the kana during quiz
		if(testValue <= 100) {
			//Location value
			positionAnswers1 = WIDTH/5 - 25;
			positionAnswers2 = WIDTH/2 - 45;
			positionAnswers3 = WIDTH - 210;

			//Physical Location
			locationCorrectAnswers = 2;
		}
		else if (testValue > 100 && testValue <= 200) {
			positionAnswers1 = WIDTH/2 - 45;
			positionAnswers2 = WIDTH - 210;
			positionAnswers3 = WIDTH/5 - 25;

			locationCorrectAnswers = 3;
		}
		else {
			positionAnswers1 = WIDTH - 210;
			positionAnswers2 = WIDTH/5 - 25;
			positionAnswers3 = WIDTH/2 - 45;

			locationCorrectAnswers = 1;
		}
		
		if(!isDakuten) {

			falseCardIndex1 = rndm.nextInt(45);
			while(falseCardIndex1 == kanaQuizCards[quizCardIndex])
				falseCardIndex1 = rndm.nextInt(45);
			falseCardIndex2 = rndm.nextInt(45);
			while(falseCardIndex2 == kanaQuizCards[quizCardIndex] || falseCardIndex2 == falseCardIndex1)
				falseCardIndex2 = rndm.nextInt(45);

		}
		
		else {
			
			falseCardIndex1 = rndm.nextInt(45 + 25);
			while(falseCardIndex1 == kanaQuizCards[quizCardIndex])
				falseCardIndex1 = rndm.nextInt(45 + 25);
			falseCardIndex2 = rndm.nextInt(45 + 25);
			while(falseCardIndex2 == kanaQuizCards[quizCardIndex] || falseCardIndex2 == falseCardIndex1)
				falseCardIndex2 = rndm.nextInt(45 + 25);

		}

	}

	// Created by pek at http://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
	public static synchronized void playSound(final String url) {
		new Thread(new Runnable() {
			// The wrapper thread is unnecessary, unless it blocks on the
			// Clip finishing; see comments.
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(
							this.getClass().getResourceAsStream("/kana/resources/sounds/" + url));
					clip.open(inputStream);
					clip.start(); 
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	/**
	 * Rewrite this method for your game
	 */
	protected void update(int deltaTime) {

		switch (gameState)
		{
		case MAINMENU:
			break;
		case STUDYKATAKANA:
		case STUDYHIRAGANA:
			if(quizOn && isQuiz && quizSetUp) {
				if(gameState == GameState.STUDYHIRAGANA)
					gameState = GameState.QUIZHIRAGANA;
				else
					gameState = GameState.QUIZKATAKANA;
				for(int i = 0; i < 5; i++) {
					kanaQuizCards[i] = rndm.nextInt(45);
				}

				//set inital given answers for quiz
				setQuizAnswers();

				quizSetUp = false;
			}
			break;
		case QUIZHIRAGANA:
			break;
		case QUIZKATAKANA:
			break;
		case CHART:
			break;
		}
	}

	/**
	 * Rewrite this method for your game
	 */
	protected void render(Graphics2D g) {

		switch (gameState)
		{
		case MAINMENU:
			g.drawImage(mainMenu, 0, 0, WIDTH, HEIGHT, null);
			g.drawImage(studyHiraganaImg, WIDTH/12 + 30, HEIGHT/4, studyHiraganaImg.getWidth(), studyHiraganaImg.getHeight(), null);
			g.drawImage(studyKatakanaImg, WIDTH/2 + 30, HEIGHT/4, studyKatakanaImg.getWidth(), studyKatakanaImg.getHeight(), null);
			g.drawImage(chartsImg, WIDTH/3 + 30, HEIGHT/3 + 40, chartsImg.getWidth(), chartsImg.getHeight(), null);
			g.drawImage(menuSelectImg, mainMenuCurserLocationWidth, mainMenuCurserLocationHeight, menuSelectImg.getWidth(), menuSelectImg.getHeight(), null);
			break;
		case STUDYHIRAGANA:
			g.drawImage(kanaKamiLayout, 0, 0, WIDTH, HEIGHT, null);//for some reason the right side and bottom side has a blank border
			g.drawImage(hiraganaDeck.getCard(currentKanaIndex).getKana(), WIDTH/3 + 30, HEIGHT/5, hiraganaDeck.getCard(currentKanaIndex).getKana().getWidth(), hiraganaDeck.getCard(currentKanaIndex).getKana().getHeight(), null);
			g.drawString(hiraganaDeck.getCard(currentKanaIndex).getRomanji(), WIDTH/2 - 50, HEIGHT/2 + 15);
			g.drawImage(currentSortStatusImg, 590, 4, currentSortStatusImg.getWidth(), currentSortStatusImg.getHeight(), null);
			g.drawImage(currentQuizStatusImg, 5, 4, currentQuizStatusImg.getWidth(), currentQuizStatusImg.getHeight(), null);
			g.drawImage(currentDakutenImg, 270, 4, currentDakutenImg.getWidth(), currentDakutenImg.getHeight(), null);
			break;
		case STUDYKATAKANA:
			g.drawImage(kanaKamiLayout, 0, 0, WIDTH, HEIGHT, null);//for some reason the right side and bottom side has a blank border
			//System.out.println("Current Index = " + currentKanaIndex);
			//g.drawImage(hiraganaDeck.getCard(currentKanaIndex).getKana(), WIDTH/3 + 30, HEIGHT/5, hiraganaDeck.getCard(currentKanaIndex).getKana().getWidth(), hiraganaDeck.getCard(currentKanaIndex).getKana().getHeight(), null);
			//g.drawString(katakanaDeck.getCard(currentKanaIndex).getRomanji(), WIDTH/2 - 50, HEIGHT/2 + 15);
			g.drawImage(currentSortStatusImg, 590, 4, currentSortStatusImg.getWidth(), currentSortStatusImg.getHeight(), null);
			g.drawImage(currentQuizStatusImg, 5, 4, currentQuizStatusImg.getWidth(), currentQuizStatusImg.getHeight(), null);
			g.drawImage(currentDakutenImg, 270, 4, currentDakutenImg.getWidth(), currentDakutenImg.getHeight(), null);
			break;
		case QUIZHIRAGANA:
			g.drawImage(kanaKamiQuizLayout, 0, 0, WIDTH, HEIGHT, null);
			g.drawImage(hiraganaDeck.getCard(kanaQuizCards[quizCardIndex]).getKana(), WIDTH/3 + 30, HEIGHT/10 - 20, hiraganaDeck.getCard(kanaQuizCards[quizCardIndex]).getKana().getWidth(), hiraganaDeck.getCard(kanaQuizCards[quizCardIndex]).getKana().getHeight(), null);
			g.drawString(hiraganaDeck.getCard(falseCardIndex1).getRomanji(), positionAnswers1, HEIGHT/3 + 50);
			g.drawString(hiraganaDeck.getCard(kanaQuizCards[quizCardIndex]).getRomanji(), positionAnswers2, HEIGHT/3 + 50);
			g.drawString(hiraganaDeck.getCard(falseCardIndex2).getRomanji(), positionAnswers3, HEIGHT/3 + 50);
			g.drawImage(quizSelectImg, quizCurserLocation, HEIGHT/3 - 20, quizSelectImg.getWidth(), quizSelectImg.getHeight(), null);
			break;
		case QUIZKATAKANA:
			g.drawImage(kanaKamiQuizLayout, 0, 0, WIDTH, HEIGHT, null);
			g.drawImage(katakanaDeck.getCard(kanaQuizCards[quizCardIndex]).getKana(), WIDTH/3 + 30, HEIGHT/10 - 20, katakanaDeck.getCard(kanaQuizCards[quizCardIndex]).getKana().getWidth(), katakanaDeck.getCard(kanaQuizCards[quizCardIndex]).getKana().getHeight(), null);
			g.drawString(katakanaDeck.getCard(falseCardIndex1).getRomanji(), positionAnswers1, HEIGHT/3 + 50);
			g.drawString(katakanaDeck.getCard(kanaQuizCards[quizCardIndex]).getRomanji(), positionAnswers2, HEIGHT/3 + 50);
			g.drawString(katakanaDeck.getCard(falseCardIndex2).getRomanji(), positionAnswers3, HEIGHT/3 + 50);
			g.drawImage(quizSelectImg, quizCurserLocation, HEIGHT/3 - 20, quizSelectImg.getWidth(), quizSelectImg.getHeight(), null);
			break;
		case CHART:
			break;
		}
	}

	public static void main(String [] args) {
		Game ex = new Game();
		new Thread(ex).start();
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (gameState)
		{
		case MAINMENU:
			//Right Arrow Key
			if(e.getKeyCode() == 39) {
				if(mainMenuPositionCount == 2) {
					mainMenuPositionCount++;
					mainMenuCurserLocationWidth = WIDTH/3 - 25;
					mainMenuCurserLocationHeight = HEIGHT/3 + 30;
				}
				else if(mainMenuPositionCount == 1) {
					mainMenuPositionCount++;
					mainMenuCurserLocationWidth += 300;
				}
				else {
					mainMenuCurserLocationHeight = HEIGHT/4 - 10;
					mainMenuCurserLocationWidth = 60;
					mainMenuPositionCount = 1;
				}
			}
			//Down Arrow Key
			else if(e.getKeyCode() == 37) {
				if(mainMenuPositionCount == 2) {
					mainMenuPositionCount--;
					mainMenuCurserLocationWidth -= 300;
				}
				else if(mainMenuPositionCount == 1) {
					mainMenuPositionCount = 3;
					mainMenuCurserLocationWidth = WIDTH/3 - 25;
					mainMenuCurserLocationHeight = HEIGHT/3 + 30;
				}
				else {
					mainMenuCurserLocationHeight = HEIGHT/4 - 10;
					mainMenuCurserLocationWidth = 360;
					mainMenuPositionCount--;
				}
			}
			//Enter Key
			else if(e.getKeyCode() == 10) {
				if(mainMenuPositionCount == 1) {
					toHiraganaStudy();
				}

				else if(mainMenuPositionCount == 2) {
					toKatakanaStudy();
				}
			}
			break;
		case STUDYKATAKANA:
		case STUDYHIRAGANA:
			if(!isQuiz) {
				boolean quizImmediately = true;

				//Right Arrow Key
				if(e.getKeyCode() == 39) {

					// If not isDakuten
					if(!isDakuten) {

						if(currentKanaIndex < 45)
							currentKanaIndex++;
						else
							currentKanaIndex = 0;

						if(quizOn && (quizImmediately || rndm.nextInt(30) == 5)) {
							isQuiz = true;
							quizSetUp = true;
						}
					}
					
					else {
						
						if(currentKanaIndex < 45 + 25)
							currentKanaIndex++;
						else
							currentKanaIndex = 0;

						if(quizOn && (quizImmediately || rndm.nextInt(30) == 5)) {
							isQuiz = true;
							quizSetUp = true;
						}
						
					}

				}
				//Left Arrow Key
				else if(e.getKeyCode() == 37) {

					// if not isDakuten
					if(!isDakuten) {

						if(currentKanaIndex > 0)
							currentKanaIndex--;
						else
							currentKanaIndex = 45;

					}

					else {
						
						if(currentKanaIndex > 0)
							currentKanaIndex--;
						else
							currentKanaIndex = 45 + 25;

					}

				}

				// S Key
				else if(e.getKeyCode() == 83) {

					if(isShuffled == false) {
						if(gameState == GameState.STUDYHIRAGANA)
							shuffleDeck(hiraganaDeck);
						else
							shuffleDeck(katakanaDeck);

						isShuffled = true;
					}

					else {

						if(gameState == GameState.STUDYHIRAGANA) {
							currentKanaIndex = hiraganaDeck.getCard(currentKanaIndex).getCardNumber();
							organizeDeck(hiraganaDeck);
						}
						else {
							currentKanaIndex = katakanaDeck.getCard(currentKanaIndex).getCardNumber();
							organizeDeck(katakanaDeck);
						}
						isShuffled = false;
					}

				}

				// Q Key
				else if(e.getKeyCode() == 81) {

					if(quizOn) {
						quizOff();
					}

					else {
						quizOn();
					}

				}

				// D Key
				else if(e.getKeyCode() == 68) {

					if(isDakuten) {
						dakutenOff();
					}

					else {
						dakutenOn();
					}

				}

				// Esc Key
				else if(e.getKeyCode() == 27) {
					currentKanaIndex = 0;
					toMainMenu();
				}
			}
			break;
		case QUIZKATAKANA:
		case QUIZHIRAGANA:

			//Right Arrow Key
			if(e.getKeyCode() == 39) {
				if(quizPositionCount == 3) {
					quizPositionCount = 1;
					quizCurserLocation = 50;
				}
				else {
					quizPositionCount++;
					quizCurserLocation += 190;
				}

			}

			//Left Arrow Key
			else if(e.getKeyCode() == 37) {

				if(quizPositionCount == 1) {
					quizPositionCount = 3;
					quizCurserLocation = 430;
				}
				else {
					quizPositionCount--;
					quizCurserLocation -= 190;
				}

			}

			//Enter Key
			else if(e.getKeyCode() == 10) {
				if(quizPositionCount == locationCorrectAnswers) {
					playSound("Good Job!.wav");
					quizPositionCount = 1;
					quizCurserLocation = 50;

					//Test if quiz is over (5 questions)
					if(quizCardIndex >= kanaQuizCards.length-1) {
						quizCardIndex = 0;
						isQuiz = false;
						if(gameState == GameState.QUIZHIRAGANA)
							gameState = GameState.STUDYHIRAGANA;
						else
							gameState = GameState.STUDYKATAKANA;
					}

					//Next question
					quizCardIndex++;
					setQuizAnswers();
				}

				else {
					//playSound("BuBu Noise.wav");	//Need to normalize sounds
				}
			}

			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
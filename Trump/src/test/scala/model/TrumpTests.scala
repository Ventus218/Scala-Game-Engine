package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Decks.*
import Cards.*
import PlayersInfo.*
import Field.*
import Trump.*

class TrumpTests extends AnyFlatSpec:
  given seed: Int = 10

  val initialDeck = Deck.stockDeck.shuffle
  val p1 = "P1"
  val p2 = "P2"
  val playersInfo = PlayersInfo(p1, p2).get
  val game = Trump(initialDeck, playersInfo).right.get

  "Trump" should "construct a game only if the deck has enough cards to play a turn" in:
    val validDeck = Deck(initialDeck.cards.take(8).toSeq*).shuffle
    val invalidDeck = validDeck.deal.right.get._1
    Trump(validDeck, playersInfo).isRight shouldBe true
    Trump(invalidDeck, playersInfo).isRight shouldBe false

  "Game" should "let players play in the correct order" in:
    game.currentPlayer.info shouldBe playersInfo.player1

  it should "return the correct player" in:
    game.player(p1) shouldBe game.currentPlayer

  it should "return the correct otherPlayer" in:
    game.otherPlayer(p1) shouldBe game.nextPlayer

  it should "give three cards to each player hand initially" in:
    game.player(p1).hand.size shouldBe 3
    game.player(p2).hand.size shouldBe 3

  it should "alternate giving cards to player" in:
    val deckCards = initialDeck.cards.toSeq
    game
      .player(p1)
      .hand
      .cards
      .toSeq should contain theSameElementsInOrderAs Seq(
      deckCards(0),
      deckCards(2),
      deckCards(4)
    )
    game
      .player(p2)
      .hand
      .cards
      .toSeq should contain theSameElementsInOrderAs Seq(
      deckCards(1),
      deckCards(3),
      deckCards(5)
    )

  it should "deal a trump card after dealing player hands" in:
    game.trumpCard shouldBe Some(initialDeck.cards.toSeq(6))

  it should "have dealt 7 cards (two hands an the trump card) initially" in:
    game.deck.size shouldBe initialDeck.size - 7

  it should "have no card placed on the field initially" in:
    game.field.size shouldBe 0

  it should "allow the current player to place a card on the field" in:
    val player = game.currentPlayer
    val card = player.hand.cards.head

    val newGame = game.playCard(card).right.get._1
    newGame.field.placedCards should contain theSameElementsInOrderAs Seq(
      PlacedCard(card, game.currentPlayer.info)
    )
    newGame.player(player.info).hand.cards should contain noElementsOf Seq(card)

  it should "deny the current player to place a card which is not in his hand" in:
    val card = game.nextPlayer.hand.cards.head // notice wrong player

    game.playCard(card) shouldBe a[Left[TrumpError.RuleBroken, Game[String]]]

  it should "empty the field after the second player has played his turn" in:
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1
    newGame.field.placedCards should contain theSameElementsAs Seq()

  it should "deal one card to each player after the end of a turn" in:
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1
    newGame.currentPlayer.hand.size shouldBe 3
    newGame.nextPlayer.hand.size shouldBe 3

  val trumpCard = Card(Cups, Two)
  val winningCards = Seq(Card(Cups, King), Card(Cups, Three), Card(Cups, Ace))
  val losingCards = Seq(Card(Clubs, Two), Card(Clubs, Four), Card(Clubs, Five))
  // whatever p1 will play it will win
  val deckP1Lucky = ShuffledDeck.makeShuffledDeck(
    winningCards(0),
    losingCards(0),
    winningCards(1),
    losingCards(1),
    winningCards(2),
    losingCards(2),
    trumpCard,
    Card(Coins, Ace)
  )
  // whatever p2 will play it will win
  val deckP2Lucky = ShuffledDeck.makeShuffledDeck(
    losingCards(0),
    winningCards(0),
    losingCards(1),
    winningCards(1),
    losingCards(2),
    winningCards(2),
    trumpCard,
    Card(Coins, Ace)
  )

  it should "not swap player turns if the first player won the turn" in:
    val game = Trump(deckP1Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1
    newGame.currentPlayer.info shouldBe p1

  it should "swap player turns if the second player won the turn" in:
    val game = Trump(deckP2Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1
    newGame.currentPlayer.info shouldBe p2

  it should "give the played cards to the turn winner" in:
    val game = Trump(deckP1Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1

    val acquiredCards = Seq(c1, c2)
    newGame.currentPlayer.acquiredCards should contain theSameElementsAs acquiredCards

  it should "deal new cards at the end of the turn starting from the winner of that turn" in:
    val game = Trump(deckP1Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val cardToWinner = game.deck.cards.head
    val cardToLoser = game.trumpCard.get
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1
    newGame.currentPlayer.hand.cards.exists(_ == cardToWinner) shouldBe true
    newGame.nextPlayer.hand.cards.exists(_ == cardToLoser) shouldBe true

  it should "deal the trumpCard when there are no more cards in the deck" in:
    val game = Trump(deckP1Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val cardToWinner = game.deck.cards.head
    val cardToLoser = game.trumpCard.get
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
    yield (g2)).right.get._1
    newGame.currentPlayer.hand.cards.exists(_ == cardToWinner) shouldBe true
    newGame.nextPlayer.hand.cards.exists(_ == cardToLoser) shouldBe true

  it should "stop dealing cards when the deck is finished and the trumpCard was given" in:
    val game = Trump(deckP1Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val c3 = game.currentPlayer.hand.cards.drop(1).head
    val c4 = game.nextPlayer.hand.cards.drop(1).head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
      g3 <- g2._1.playCard(c3)
      g4 <- g3._1.playCard(c4)
    yield (g4)).right.get._1
    newGame.currentPlayer.hand.cards.size shouldBe 2
    newGame.nextPlayer.hand.cards.size shouldBe 2

  import TrumpResult.*
  it should "give a winner when the game is finished if one of the two players have higher points than the other" in:
    val game = Trump(deckP1Lucky, playersInfo).right.get
    val c1 = game.currentPlayer.hand.cards.head
    val c2 = game.nextPlayer.hand.cards.head
    val c3 = game.currentPlayer.hand.cards.drop(1).head
    val c4 = game.nextPlayer.hand.cards.drop(1).head
    val c5 = game.currentPlayer.hand.cards.drop(2).head
    val c6 = game.nextPlayer.hand.cards.drop(2).head
    val c7 = game.currentPlayer.hand.cards.drop(3).head
    val c8 = game.nextPlayer.hand.cards.drop(3).head
    val newGame = (for
      g1 <- game.playCard(c1)
      g2 <- g1._1.playCard(c2)
      g3 <- g2._1.playCard(c3)
      g4 <- g3._1.playCard(c4)
      g5 <- g4._1.playCard(c4)
      g6 <- g5._1.playCard(c4)
      g7 <- g6._1.playCard(c4)
      g8 <- g7._1.playCard(c4)
    yield (g4)).right.get._1

    newGame shouldBe Some(Win(p1))

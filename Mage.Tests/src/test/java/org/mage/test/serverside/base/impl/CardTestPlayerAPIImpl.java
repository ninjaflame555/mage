package org.mage.test.serverside.base.impl;

import mage.constants.CardType;
import mage.constants.PhaseStep;
import mage.abilities.Ability;
import mage.cards.Card;
import mage.cards.decks.Deck;
import mage.cards.decks.importer.DeckImporterUtil;
import mage.cards.repository.CardInfo;
import mage.cards.repository.CardRepository;
import mage.cards.repository.CardScanner;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.Filter;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.GameException;
import mage.game.command.CommandObject;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentCard;
import mage.players.Player;
import org.junit.Assert;
import org.mage.test.player.TestPlayer;
import org.mage.test.serverside.base.CardTestAPI;
import org.mage.test.serverside.base.MageTestPlayerBase;

import java.util.List;
import java.util.UUID;

/**
 * API for test initialization and asserting the test results.
 *
 * @author ayratn
 */
public abstract class CardTestPlayerAPIImpl extends MageTestPlayerBase implements CardTestAPI {

    static {
        CardScanner.scan();
    }

    /**
     * Default game initialization params for red player (that plays with Mountains)
     */
    @Override
    public void useRedDefault() {
        // *** ComputerA ***
        // battlefield:ComputerA:Mountain:5
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        // hand:ComputerA:Mountain:4
        addCard(Zone.HAND, playerA, "Mountain", 5);
        // library:ComputerA:clear:0
        removeAllCardsFromLibrary(playerA);
        // library:ComputerA:Mountain:10
        addCard(Zone.LIBRARY, playerA, "Mountain", 10);

        // *** ComputerB ***
        // battlefield:ComputerB:Plains:2
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        // hand:ComputerB:Plains:2
        addCard(Zone.HAND, playerB, "Plains", 2);
        // library:ComputerB:clear:0
        removeAllCardsFromLibrary(playerB);
        // library:ComputerB:Plains:10
        addCard(Zone.LIBRARY, playerB, "Plains", 10);
    }

    /**
     * Default game initialization params for white player (that plays with Plains)
     */
    public void useWhiteDefault() {
        // *** ComputerA ***
        addCard(Zone.BATTLEFIELD, playerA, "Plains", 5);
        addCard(Zone.HAND, playerA, "Plains", 5);
        removeAllCardsFromLibrary(playerA);
        addCard(Zone.LIBRARY, playerA, "Plains", 10);

        // *** ComputerB ***
        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.HAND, playerB, "Plains", 2);
        removeAllCardsFromLibrary(playerB);
        addCard(Zone.LIBRARY, playerB, "Plains", 10);
    }

    protected TestPlayer createPlayer(Game game, TestPlayer player, String name) throws GameException {
        player = createNewPlayer(name);
        player.setTestMode(true);
        logger.debug("Loading deck...");
        Deck deck = Deck.load(DeckImporterUtil.importDeck("RB Aggro.dck"), false, false);
        logger.debug("Done!");
        if (deck.getCards().size() < 40) {
            throw new IllegalArgumentException("Couldn't load deck, deck size=" + deck.getCards().size());
        }
        game.addPlayer(player, deck);
        game.loadCards(deck.getCards(), player.getId());

        return player;
    }

    protected TestPlayer createNewPlayer(String playerName) {
        return createPlayer(playerName);
    }

    /**
     * Removes all cards from player's library from the game.
     * Usually this should be used once before initialization to form the library in certain order.
     *
     * @param player {@link Player} to remove all library cards from.
     */
    @Override
    public void removeAllCardsFromLibrary(TestPlayer player) {
        getCommands(player).put(Zone.LIBRARY, "clear");
    }

    /**
     * Removes all cards from player's hand from the game.
     * Usually this should be used once before initialization to set the players hand.
     *
     * @param player {@link Player} to remove all cards from hand.
     */
    public void removeAllCardsFromHand(TestPlayer player) {
        getCommands(player).put(Zone.HAND, "clear");
    }

    /**
     * Add a card to specified zone of specified player.
     *
     * @param gameZone {@link mage.constants.Zone} to add cards to.
     * @param player   {@link Player} to add cards for. Use either playerA or playerB.
     * @param cardName Card name in string format.
     */
    @Override
    public void addCard(Zone gameZone, TestPlayer player, String cardName) {
        addCard(gameZone, player, cardName, 1, false);
    }

    /**
     * Add any amount of cards to specified zone of specified player.
     *
     * @param gameZone {@link mage.constants.Zone} to add cards to.
     * @param player   {@link Player} to add cards for. Use either playerA or playerB.
     * @param cardName Card name in string format.
     * @param count    Amount of cards to be added.
     */
    @Override
    public void addCard(Zone gameZone, TestPlayer player, String cardName, int count) {
        addCard(gameZone, player, cardName, count, false);
    }

    /**
     * Add any amount of cards to specified zone of specified player.
     *
     * @param gameZone {@link mage.constants.Zone} to add cards to.
     * @param player   {@link Player} to add cards for. Use either playerA or playerB.
     * @param cardName Card name in string format.
     * @param count    Amount of cards to be added.
     * @param tapped   In case gameZone is Battlefield, determines whether permanent should be tapped.
     *                 In case gameZone is other than Battlefield, {@link IllegalArgumentException} is thrown
     */
    @Override
    public void addCard(Zone gameZone, TestPlayer player, String cardName, int count, boolean tapped) {

        if (gameZone.equals(Zone.BATTLEFIELD)) {
            for (int i = 0; i < count; i++) {
                CardInfo cardInfo = CardRepository.instance.findCard(cardName);
                Card card = cardInfo != null ? cardInfo.getCard() : null;
                if (card == null) {
                    throw new IllegalArgumentException("[TEST] Couldn't find a card: " + cardName);
                }
                PermanentCard p = new PermanentCard(card, null);
                p.setTapped(tapped);
                getBattlefieldCards(player).add(p);
            }
        } else {
            if (tapped) {
                throw new IllegalArgumentException("Parameter tapped=true can be used only for Zone.BATTLEFIELD.");
            }
            List<Card> cards = getCardList(gameZone, player);
            for (int i = 0; i < count; i++) {
                CardInfo cardInfo = CardRepository.instance.findCard(cardName);
                Card card = cardInfo != null ? cardInfo.getCard() : null;
                if (card == null) {
                    throw new AssertionError("Couldn't find a card: " + cardName);
                }
                cards.add(card);
            }
        }
    }

    /**
     * Returns card list containter for specified game zone and player.
     *
     * @param gameZone
     * @param player
     * @return
     */
    private List<Card> getCardList(Zone gameZone, TestPlayer player) {
        if (gameZone.equals(Zone.HAND)) {
            return getHandCards(player);
        } else if (gameZone.equals(Zone.GRAVEYARD)) {
            return getGraveCards(player);
        } else if (gameZone.equals(Zone.LIBRARY)) {
            return getLibraryCards(player);
        }

        throw new AssertionError("Zone is not supported by test framework: " + gameZone);
    }

    /**
     * Set player's initial life count.
     *
     * @param player {@link Player} to set life count for.
     * @param life   Life count to set.
     */
    @Override
    public void setLife(TestPlayer player, int life) {
        getCommands(player).put(Zone.OUTSIDE, "life:" + String.valueOf(life));
    }

    /**
     * Define turn number to stop the game on.
     * @param turn
     */
    @Override
    public void setStopOnTurn(int turn) {
        stopOnTurn = turn == -1 ? null : Integer.valueOf(turn);
        stopAtStep = PhaseStep.UNTAP;
    }

    /**
     * Define turn number and step to stop the game on.
     * @param turn
     * @param step
     */
    @Override
    public void setStopAt(int turn, PhaseStep step) {
        stopOnTurn = turn == -1 ? null : Integer.valueOf(turn);
        stopAtStep = step;
    }

    /**
     * Assert turn number after test execution.
     *
     * @param turn Expected turn number to compare with. 1-based.
     */
    @Override
    public void assertTurn(int turn) throws AssertionError {
        Assert.assertEquals("Turn numbers are not equal", turn, currentGame.getTurnNum());
    }

    /**
     * Assert game result after test execution.
     *
     * @param result Expected {@link GameResult} to compare with.
     */
    @Override
    public void assertResult(Player player, GameResult result) throws AssertionError {
        if (player.equals(playerA)) {
            GameResult actual = CardTestAPI.GameResult.DRAW;
            switch (currentGame.getWinner()) {
                case "Player PlayerA is the winner":
                    actual = CardTestAPI.GameResult.WON;
                    break;
                case "Player PlayerB is the winner":
                    actual = CardTestAPI.GameResult.LOST;
                    break;
            }
            Assert.assertEquals("Game results are not equal", result, actual);
        } else if (player.equals(playerB)) {
            GameResult actual = CardTestAPI.GameResult.DRAW;
            switch (currentGame.getWinner()) {
                case "Player PlayerB is the winner":
                    actual = CardTestAPI.GameResult.WON;
                    break;
                case "Player PlayerA is the winner":
                    actual = CardTestAPI.GameResult.LOST;
                    break;
            }
            Assert.assertEquals("Game results are not equal", result, actual);
        }
    }

    /**
     * Assert player's life count after test execution.
     *
     * @param player {@link Player} to get life for comparison.
     * @param life   Expected player's life to compare with.
     */
    @Override
    public void assertLife(Player player, int life) throws AssertionError {
        int actual = currentGame.getPlayer(player.getId()).getLife();
        Assert.assertEquals("Life amounts are not equal for player " + player.getName(), life, actual);
    }

    /**
     * Assert creature's power and toughness by card name.
     * <p/>
     * Throws {@link AssertionError} in the following cases:
     * 1. no such player
     * 2. no such creature under player's control
     * 3. depending on comparison scope:
     * 3a. any: no creature under player's control with the specified p\t params
     * 3b. all: there is at least one creature with the cardName with the different p\t params
     *
     * @param player    {@link Player} to get creatures for comparison.
     * @param cardName  Card name to compare with.
     * @param power     Expected power to compare with.
     * @param toughness Expected toughness to compare with.
     * @param scope     {@link mage.filter.Filter.ComparisonScope} Use ANY, if you want "at least one creature with given name should have specified p\t"
     *                  Use ALL, if you want "all creature with gived name should have specified p\t"
     */
    @Override
    public void assertPowerToughness(Player player, String cardName, int power, int toughness, Filter.ComparisonScope scope)
            throws AssertionError {
        int count = 0;
        int fit = 0;
        int foundPower = 0;
        int foundToughness = 0;
        int found = 0;
        for (Permanent permanent : currentGame.getBattlefield().getAllPermanents()) {
            if (permanent.getName().equals(cardName) && permanent.getControllerId().equals(player.getId())) {
                count++;
                if (scope.equals(Filter.ComparisonScope.All)) {
                    Assert.assertEquals("Power is not the same (" + power + " vs. " + permanent.getPower().getValue() + ")",
                            power, permanent.getPower().getValue());
                    Assert.assertEquals("Toughness is not the same (" + toughness + " vs. " + permanent.getToughness().getValue() + ")",
                            toughness, permanent.getToughness().getValue());
                } else if (scope.equals(Filter.ComparisonScope.Any)) {
                    if (power == permanent.getPower().getValue() && toughness == permanent.getToughness().getValue()) {
                        fit++;
                        break;
                    }
                    found++;
                    foundPower = permanent.getPower().getValue();
                    foundToughness = permanent.getToughness().getValue();
                }
            }
        }

        Assert.assertTrue("There is no such permanent under player's control, player=" + player.getName() +
                ", cardName=" + cardName, count > 0);

        if (scope.equals(Filter.ComparisonScope.Any)) {
            Assert.assertTrue("There is no such creature under player's control with specified power&toughness, player=" + player.getName() +
                    ", cardName=" + cardName + " (found similar: " + found + ", one of them: power=" + foundPower + " toughness=" + foundToughness + ")", fit > 0);
        }
    }

    /**
     * See {@link #assertPowerToughness(mage.players.Player, String, int, int, mage.filter.Filter.ComparisonScope)}
     * 
     * @param player
     * @param cardName
     * @param power
     * @param toughness
     */
    public void assertPowerToughness(Player player, String cardName, int power, int toughness) {
        assertPowerToughness(player, cardName, power, toughness, Filter.ComparisonScope.Any);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assertAbilities(Player player, String cardName, List<Ability> abilities)
            throws AssertionError {
        int count = 0;
        Permanent found = null;
        for (Permanent permanent : currentGame.getBattlefield().getAllActivePermanents(player.getId())) {
            if (permanent.getName().equals(cardName)) {
                found = permanent;
                count++;
            }
        }

        Assert.assertNotNull("There is no such permanent under player's control, player=" + player.getName() +
                ", cardName=" + cardName, found);

        Assert.assertTrue("There is more than one such permanent under player's control, player=" + player.getName() +
                ", cardName=" + cardName, count == 1);

        for (Ability ability : abilities) {
            Assert.assertTrue("No such ability=" + ability.toString() + ", player=" + player.getName() +
                    ", cardName" + cardName, found.getAbilities().contains(ability));
        }
    }

    /**
     *
     * @param player
     * @param cardName
     * @param ability
     * @param flag true if creature should contain ability, false if it should NOT contain it instead
     * @throws AssertionError
     */
    public void assertAbility(Player player, String cardName, Ability ability, boolean flag) throws AssertionError {
        int count = 0;
        Permanent found = null;
        for (Permanent permanent : currentGame.getBattlefield().getAllActivePermanents(player.getId())) {
            if (permanent.getName().equals(cardName)) {
                found = permanent;
                count++;
            }
        }

        Assert.assertNotNull("There is no such permanent under player's control, player=" + player.getName() +
                ", cardName=" + cardName, found);

        Assert.assertTrue("There is more than one such permanent under player's control, player=" + player.getName() +
                ", cardName=" + cardName, count == 1);

        if (flag) {
            Assert.assertTrue("No such ability=" + ability.toString() + ", player=" + player.getName() +
                    ", cardName" + cardName, found.getAbilities().containsRule(ability));
        } else {
            Assert.assertFalse("Card shouldn't have such ability=" + ability.toString() + ", player=" + player.getName() +
                    ", cardName" + cardName, found.getAbilities().containsRule(ability));
        }
    }

    /**
     * Assert permanent count under player's control.
     *
     * @param player {@link Player} which permanents should be counted.
     * @param count  Expected count.
     */
    @Override
    public void assertPermanentCount(Player player, int count) throws AssertionError {
        int actualCount = 0;
        for (Permanent permanent : currentGame.getBattlefield().getAllPermanents()) {
            if (permanent.getControllerId().equals(player.getId())) {
                actualCount++;
            }
        }
        Assert.assertEquals("(Battlefield) Card counts are not equal ", count, actualCount);
    }

    /**
     * Assert permanent count under player's control.
     *
     * @param player   {@link Player} which permanents should be counted.
     * @param cardName Name of the cards that should be counted.
     * @param count    Expected count.
     */
    @Override
    public void assertPermanentCount(Player player, String cardName, int count) throws AssertionError {
        int actualCount = 0;
        for (Permanent permanent : currentGame.getBattlefield().getAllPermanents()) {
            if (permanent.getControllerId().equals(player.getId())) {
                if (permanent.getName().equals(cardName)) {
                    actualCount++;
                }
            }
        }
        Assert.assertEquals("(Battlefield) Card counts are not equal (" + cardName + ")", count, actualCount);
    }

    /**
     * Assert emblem count under player's control
     *
     * @param player
     * @param count
     * @throws AssertionError
     */
    public void assertEmblemCount(Player player, int count) throws AssertionError {
        int actualCount = 0;
        for (CommandObject commandObject : currentGame.getState().getCommand()) {
            if (commandObject.getControllerId().equals(player.getId())) {
                actualCount++;
            }
        }
        Assert.assertEquals("Emblem counts are not equal", count, actualCount);
    }

    /**
     * Assert counter count on a permanent
     *
     * @param cardName  Name of the cards that should be counted.
     * @param type      Type of the counter that should be counted.
     * @param count     Expected count.
     */
    public void assertCounterCount(String cardName, CounterType type, int count) throws AssertionError {
        Permanent found = null;
        for (Permanent permanent : currentGame.getBattlefield().getAllActivePermanents()) {
            if (permanent.getName().equals(cardName)) {
                found = permanent;
                break;
            }
        }

        Assert.assertNotNull("There is no such permanent on the battlefield, cardName=" + cardName, found);

        Assert.assertEquals("(Battlefield) Counter counts are not equal (" + cardName + ":" + type + ")", count, found.getCounters().getCount(type));
    }

    /**
     * Assert counter count on a player
     *
     * @param player    The player whos counters should be counted.
     * @param type      Type of the counter that should be counted.
     * @param count     Expected count.
     */
    public void assertCounterCount(Player player, CounterType type, int count) throws AssertionError {
        Assert.assertEquals("(Battlefield) Counter counts are not equal (" + player.getName() + ":" + type + ")", count, player.getCounters().getCount(type));
    }

    /**
     * Assert whether a permanent is a specified type or not
     *
     * @param cardName  Name of the permanent that should be checked.
     * @param type      A type to test for
     * @param subType   a subtype to test for
     */
    public void assertType(String cardName, CardType type, String subType) throws AssertionError {
        Permanent found = null;
        for (Permanent permanent : currentGame.getBattlefield().getAllActivePermanents()) {
            if (permanent.getName().equals(cardName)) {
                found = permanent;
                break;
            }
        }

        Assert.assertNotNull("There is no such permanent on the battlefield, cardName=" + cardName, found);

        Assert.assertTrue("(Battlefield) card type not found (" + cardName + ":" + type + ")", found.getCardType().contains(type));

        Assert.assertTrue("(Battlefield) card sub-type not equal (" + cardName + ":" + subType + ")", found.getSubtype().contains(subType));
    }

    /**
     * Assert whether a permanent is tapped or not
     *
     * @param cardName  Name of the permanent that should be checked.
     * @param tapped    Whether the permanent is tapped or not
     */
    public void assertTapped(String cardName, boolean tapped) throws AssertionError {
        Permanent found = null;
        for (Permanent permanent : currentGame.getBattlefield().getAllActivePermanents()) {
            if (permanent.getName().equals(cardName)) {
                found = permanent;
            }
        }

        Assert.assertNotNull("There is no such permanent on the battlefield, cardName=" + cardName, found);

        Assert.assertEquals("(Battlefield) Tapped state is not equal (" + cardName + ")", tapped, found.isTapped());
    }

    /**
     * Assert card count in player's hand.
     *
     * @param player   {@link Player} who's hand should be counted.
     * @param count    Expected count.
     */
    public void assertHandCount(Player player, int count) throws AssertionError {
        int actual = currentGame.getPlayer(player.getId()).getHand().size();
        Assert.assertEquals("(Hand) Card counts are not equal ", count, actual);
    }

    /**
     * Assert card count in player's graveyard.
     *
     * @param player   {@link Player} who's graveyard should be counted.
     * @param count    Expected count.
     */
    public void assertGraveyardCount(Player player, int count) throws AssertionError {
        int actual = currentGame.getPlayer(player.getId()).getGraveyard().size();
        Assert.assertEquals("(Graveyard) Card counts are not equal ", count, actual);
    }

    /**
     * Assert card count in exile.
     *
     * @param cardName   Name of the cards that should be counted.
     * @param count    Expected count.
     */
    public void assertExileCount(String cardName, int count) throws AssertionError {
        int actualCount = 0;
        for (ExileZone exile: currentGame.getExile().getExileZones()) {
            for (Card card : exile.getCards(currentGame)) {
                if (card.getName().equals(cardName)) {
                    actualCount++;
                }
            }
        }

        Assert.assertEquals("(Exile) Card counts are not equal (" + cardName + ")", count, actualCount);
    }

    /**
     * Assert card count in player's graveyard.
     *
     * @param player   {@link Player} who's graveyard should be counted.
     * @param cardName Name of the cards that should be counted.
     * @param count    Expected count.
     */
    public void assertGraveyardCount(Player player, String cardName, int count) throws AssertionError {
        int actualCount = 0;
        for (Card card : player.getGraveyard().getCards(currentGame)) {
            if (card.getName().equals(cardName)) {
                actualCount++;
            }
        }

        Assert.assertEquals("(Graveyard) Card counts are not equal (" + cardName + ")", count, actualCount);
    }

    public Permanent getPermanent(String cardName, Player player) {
        return getPermanent(cardName, player.getId());
    }

    public Permanent getPermanent(String cardName, UUID controller) {
        Permanent permanent0 = null;
        int count = 0;
        for (Permanent permanent : currentGame.getBattlefield().getAllPermanents()) {
            if (permanent.getControllerId().equals(controller)) {
                if (permanent.getName().equals(cardName)) {
                    permanent0 = permanent;
                    count++;
                }
            }
        }
        Assert.assertNotNull("Couldn't find a card with specified name: " + cardName, permanent0);
        Assert.assertEquals("More than one permanent was found: " + cardName + "(" + count + ")", 1, count);
        return permanent0;
    }

    public void playLand(int turnNum, PhaseStep step, TestPlayer player, String cardName) {
        player.addAction(turnNum, step, "activate:Play " + cardName);
    }

    public void castSpell(int turnNum, PhaseStep step, TestPlayer player, String cardName) {
        player.addAction(turnNum, step, "activate:Cast " + cardName);
    }

    public void castSpell(int turnNum, PhaseStep step, TestPlayer player, String cardName, Player target) {
        player.addAction(turnNum, step, "activate:Cast " + cardName + ";targetPlayer=" + target.getName());
    }

    public void castSpell(int turnNum, PhaseStep step, TestPlayer player, String cardName, String targetName) {
        player.addAction(turnNum, step, "activate:Cast " + cardName + ";target=" + targetName);
    }

    /**
     * Spell will only be cast, if a spell with the given name is already on the stack
     * 
     * @param turnNum
     * @param step
     * @param player
     * @param cardName
     * @param targetName
     * @param spellOnStack 
     */
    public void castSpell(int turnNum, PhaseStep step, TestPlayer player, String cardName, String targetName, String spellOnStack) {
        player.addAction(turnNum, step, "activate:Cast " + cardName + ";target=" + targetName + ";spellOnStack=" + spellOnStack);
    }

    public void activateAbility(int turnNum, PhaseStep step, TestPlayer player, String ability) {
        player.addAction(turnNum, step, "activate:" + ability);
    }

    public void activateAbility(int turnNum, PhaseStep step, TestPlayer player, String ability, Player target) {
        player.addAction(turnNum, step, "activate:" + ability + ";targetPlayer=" + target.getName());
    }

    public void activateAbility(int turnNum, PhaseStep step, TestPlayer player, String ability, String targetName) {
        player.addAction(turnNum, step, "activate:" + ability + ";target=" + targetName);
    }

    public void addCounters(int turnNum, PhaseStep step, TestPlayer player, String cardName, CounterType type, int count) {
        player.addAction(turnNum, step, "addCounters:" + cardName + ";" + type.getName() + ";" + count);
    }

    public void attack(int turnNum, TestPlayer player, String attacker) {
        player.addAction(turnNum, PhaseStep.DECLARE_ATTACKERS, "attack:"+attacker);
    }

    public void attack(int turnNum, TestPlayer player, String attacker, String planeswalker) {
        player.addAction(turnNum, PhaseStep.DECLARE_ATTACKERS, new StringBuilder("attack:").append(attacker).append(";planeswalker=").append(planeswalker).toString());
    }

    public void block(int turnNum, TestPlayer player, String blocker, String attacker) {
        player.addAction(turnNum, PhaseStep.DECLARE_BLOCKERS, "block:"+blocker+";"+attacker);
    }

    public void setChoice(TestPlayer player, String choice) {
        player.addChoice(choice);
    }

    /**
     * Set the modes for modal spells
     * 
     * @param player
     * @param choice starting with "1" for mode 1, "2" for mode 2 and so on (to set multiple modes call the command multiple times)
     */
    public void setModeChoice(TestPlayer player, String choice) {
        player.addModeChoice(choice);
    }

    public void addTarget(TestPlayer player, String target) {
        player.addTarget(target);
    }
}

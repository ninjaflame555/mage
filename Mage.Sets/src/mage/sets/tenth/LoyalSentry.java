/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package mage.sets.tenth;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.BlocksCreatureTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.Outcome;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author Loki
 */
public class LoyalSentry extends CardImpl<LoyalSentry> {

    public LoyalSentry (UUID ownerId) {
        super(ownerId, 27, "Loyal Sentry", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{W}");
        this.expansionSetCode = "10E";
        this.subtype.add("Human");
        this.subtype.add("Soldier");

        this.color.setWhite(true);
        this.power = new MageInt(1);
        this.toughness = new MageInt(1);

        // When Loyal Sentry blocks a creature, destroy that creature and Loyal Sentry.
        this.addAbility(new BlocksCreatureTriggeredAbility(new LoyalSentryEffect(), false, true));
    }

    public LoyalSentry (final LoyalSentry card) {
        super(card);
    }

    @Override
    public LoyalSentry copy() {
        return new LoyalSentry(this);
    }
}

class LoyalSentryEffect extends OneShotEffect<LoyalSentryEffect> {
    LoyalSentryEffect() {
        super(Outcome.DestroyPermanent);
        staticText = "destroy that creature and {this}";
    }

    LoyalSentryEffect(LoyalSentryEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent p = game.getPermanent(source.getFirstTarget());        
        if (p != null) {
            p.destroy(source.getSourceId(), game, false);
        }
        Permanent s = game.getPermanent(source.getSourceId());
        if (s != null) {
            s.destroy(source.getSourceId(), game, false);
        }
        return true;
    }

    @Override
    public LoyalSentryEffect copy() {
        return new LoyalSentryEffect(this);
    }

}

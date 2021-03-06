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
package mage.sets.futuresight;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.cost.SpellsCostReductionEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.FilterSpell;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 *
 * @author North
 */
public class CentaurOmenreader extends CardImpl<CentaurOmenreader> {

    private static final FilterSpell filter = new FilterSpell("creature spells");
    static {
        filter.add(new CardTypePredicate(CardType.CREATURE));
    }

    public CentaurOmenreader(UUID ownerId) {
        super(ownerId, 143, "Centaur Omenreader", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{3}{G}");
        this.expansionSetCode = "FUT";
        this.supertype.add("Snow");
        this.subtype.add("Centaur");
        this.subtype.add("Shaman");

        this.color.setGreen(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // As long as Centaur Omenreader is tapped, creature spells you cast cost {2} less to cast.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new CentaurOmenreaderSpellsCostReductionEffect(filter)));
    }

    public CentaurOmenreader(final CentaurOmenreader card) {
        super(card);
    }

    @Override
    public CentaurOmenreader copy() {
        return new CentaurOmenreader(this);
    }
}

class CentaurOmenreaderSpellsCostReductionEffect extends SpellsCostReductionEffect {

    public CentaurOmenreaderSpellsCostReductionEffect(FilterSpell filter) {
        super(filter, 2);
        staticText = "As long as {this} is tapped, creature spells you cast cost {2} less to cast";
    }

    protected CentaurOmenreaderSpellsCostReductionEffect(SpellsCostReductionEffect effect) {
        super(effect);
    }

    @Override
    public boolean applies(Ability abilityToModify, Ability source, Game game) {
        Permanent sourcePermanent = game.getPermanent(source.getSourceId());
        if (sourcePermanent != null && sourcePermanent.isTapped()) {
            return super.applies(abilityToModify, source, game);
        }
        return false;
    }

    @Override
    public CentaurOmenreaderSpellsCostReductionEffect copy() {
        return new CentaurOmenreaderSpellsCostReductionEffect(this);
    }
}

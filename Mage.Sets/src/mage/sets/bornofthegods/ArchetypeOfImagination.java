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
package mage.sets.bornofthegods;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continious.CreaturesCantGetOrHaveAbilityEffect;
import mage.abilities.effects.common.continious.GainAbilityControlledEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.ControllerPredicate;

/**
 *
 * @author LevelX2
 */
public class ArchetypeOfImagination extends CardImpl<ArchetypeOfImagination> {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("Creatures your opponents control");
    static {
        filter.add(new ControllerPredicate(TargetController.OPPONENT));
    }

    public ArchetypeOfImagination(UUID ownerId) {
        super(ownerId, 32, "Archetype of Imagination", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT, CardType.CREATURE}, "{4}{U}{U}");
        this.expansionSetCode = "BNG";
        this.subtype.add("Human");
        this.subtype.add("Wizard");

        this.color.setBlue(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(2);

        // Creatures you control have flying.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GainAbilityControlledEffect(FlyingAbility.getInstance(), Duration.WhileOnBattlefield, new FilterCreaturePermanent("Creatures"))));
        // Creatures your opponents control lose flying and can't have or gain flying.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new CreaturesCantGetOrHaveAbilityEffect(FlyingAbility.getInstance(), Duration.WhileOnBattlefield, filter)));

    }

    public ArchetypeOfImagination(final ArchetypeOfImagination card) {
        super(card);
    }

    @Override
    public ArchetypeOfImagination copy() {
        return new ArchetypeOfImagination(this);
    }
}

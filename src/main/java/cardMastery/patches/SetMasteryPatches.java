package cardMastery.patches;

import cardMastery.CardMastery;
import cardMastery.helper.Mastery;
import cardMastery.vfx.MoveCardAroundEffect;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.ui.buttons.ReturnToMenuButton;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetMasteryPatches {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "update"
    )
    public static class MasteryCardsInDeck {
        @SpireInsertPatch(locator = ShowStatsLocator.class)
        public static void showCards() {
            if(AbstractDungeon.actNum >= 4) {
                ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
                if(CardMastery.requiresTwo()) {
                    cards = cards.stream()
                            .collect(Collectors.groupingBy(c -> c.cardID))
                            .values()
                            .stream()
                            .filter(l -> l.size() > 1)
                            .flatMap(l -> Stream.of(l.get(0)))
                            .collect(Collectors.toCollection(ArrayList::new));
                }
                cards = cards.stream()
                        .filter(c -> Mastery.master(c, AbstractDungeon.ascensionLevel))
                        .map(AbstractCard::makeStatEquivalentCopy)
                        .collect(Collectors.toCollection(ArrayList::new));
                Collections.reverse(cards);

                for (int i = 0; i < cards.size(); i++) {
                    AbstractCard c = cards.get(i);
                    AbstractDungeon.topLevelEffects.add(new MoveCardAroundEffect(c,
                            -AbstractCard.RAW_W,
                            Settings.WIDTH + AbstractCard.RAW_W,
                            Settings.HEIGHT/2f,
                            Settings.HEIGHT/2f,
                            5f,
                            0.5f * i
                    ));
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(ReturnToMenuButton.class, "hide");
                return LineFinder.findAllInOrder(ctMethodToPatch, methodCallMatcher);
            }
        }

        private static class ShowStatsLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher faMatcher = new Matcher.FieldAccessMatcher(VictoryScreen.class, "statsTimer");
                return LineFinder.findInOrder(ctMethodToPatch, faMatcher);
            }
        }
    }
}

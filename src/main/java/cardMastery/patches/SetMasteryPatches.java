package cardMastery.patches;

import cardMastery.helper.Mastery;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.ui.buttons.ReturnToMenuButton;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class SetMasteryPatches {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "update"
    )
    public static class MasteryCardsInDeck {
        private static HashMap<String, Integer> backupMasteries;
        @SpireInsertPatch(locator = Locator.class)
        public static void patch() {
            if(AbstractDungeon.actNum >= 4) {
                Mastery.masteredCards = backupMasteries;
                AbstractDungeon.player.masterDeck.group
                        .forEach(c -> Mastery.master(c, AbstractDungeon.ascensionLevel));
            }
        }

        private static Random rng = new Random();
        @SpireInsertPatch(locator = ShowStatsLocator.class)
        public static void showCards() {
            if(AbstractDungeon.actNum >= 4) {
                backupMasteries = new HashMap<>(Mastery.masteredCards);
                HashSet<String> distinct = new HashSet<>();
                AbstractDungeon.player.masterDeck.group.stream()
                        .filter(c -> Mastery.willMaster(c, AbstractDungeon.ascensionLevel))
                        .map(AbstractCard::makeStatEquivalentCopy)
                        .forEach(c -> {
                            if(!distinct.contains(c.cardID)) {
                                distinct.add(c.cardID);
                                Mastery.masteredCards.put(c.cardID, AbstractDungeon.ascensionLevel);
                                float minX = (AbstractCard.RAW_W / 2f) * Settings.scale;
                                float maxX = Settings.WIDTH - minX;
                                float minY = (AbstractCard.RAW_H / 2f) * Settings.scale;
                                float maxY = Settings.HEIGHT - minY;

                                float x = minX + rng.nextFloat() * (maxX - minX);
                                float y = minY + rng.nextFloat() * (maxY - minY);
                                AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c, x, y));
                            }
                        });


            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(ReturnToMenuButton.class, "hide");
                int[] lines = LineFinder.findAllInOrder(ctMethodToPatch, methodCallMatcher);
                return lines;
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

    //TODO: Add a nice animation to show the newly mastered cards
}

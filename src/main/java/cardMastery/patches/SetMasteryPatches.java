package cardMastery.patches;

import cardMastery.helper.Mastery;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.FTL;
import com.megacrit.cardcrawl.cards.colorless.FlashOfSteel;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.purple.Perseverance;
import com.megacrit.cardcrawl.cards.red.FlameBarrier;
import com.megacrit.cardcrawl.cards.red.ThunderClap;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.ui.buttons.ReturnToMenuButton;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

import java.util.*;
import java.util.stream.Collectors;

public class SetMasteryPatches {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "update"
    )
    public static class MasteryCardsInDeck {
        private static Random rng = new Random();
        @SpireInsertPatch(locator = ShowStatsLocator.class)
        public static void showCards() {
            if(AbstractDungeon.actNum >= 4) {
                ArrayList<AbstractCard> cards;

                AbstractDungeon.player.masterDeck.addToBottom(new Accuracy());
                AbstractDungeon.player.masterDeck.addToBottom(new AThousandCuts());
                AbstractDungeon.player.masterDeck.addToBottom(new FTL());
                AbstractDungeon.player.masterDeck.addToBottom(new Burst());
                AbstractDungeon.player.masterDeck.addToBottom(new Catalyst());
                AbstractDungeon.player.masterDeck.addToBottom(new FlameBarrier());
                AbstractDungeon.player.masterDeck.addToBottom(new FlashOfSteel());
                AbstractDungeon.player.masterDeck.addToBottom(new Perseverance());
                AbstractDungeon.player.masterDeck.addToBottom(new Backflip());
                AbstractDungeon.player.masterDeck.addToBottom(new Backstab());
                AbstractDungeon.player.masterDeck.addToBottom(new ThunderClap());

                cards = AbstractDungeon.player.masterDeck.group.stream()
                        .filter(c -> Mastery.master(c, AbstractDungeon.ascensionLevel))
                        .map(AbstractCard::makeStatEquivalentCopy)
                        .collect(Collectors.toCollection(ArrayList::new));

                float cW = AbstractCard.RAW_W * Settings.scale;
                float cH = AbstractCard.RAW_H * Settings.scale;
                float minX = (AbstractCard.RAW_W / 2f) * Settings.scale;
                float minY = (AbstractCard.RAW_H / 2f) * Settings.scale;
                float maxX = Settings.WIDTH - minX;
                float maxY = Settings.HEIGHT - minY;
                int maxInARow = (int) ((maxX - minX) / cW);
                int curRow = 0;
                int curPos = 0;
                for(AbstractCard c : cards) {
                    int x = (int) (minX + (curPos * cW));
                    int y = (int) (maxY - (curRow * cH));
                    curPos = (++curPos % maxInARow);
                    if(curPos == 0) curRow++;
                    AbstractGameEffect effect = new ShowCardBrieflyEffect(c, x, y);
                    effect.duration = effect.startingDuration = 3f + curRow + (curPos * 0.25f);
                    AbstractDungeon.topLevelEffects.add(effect);
                }
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
}

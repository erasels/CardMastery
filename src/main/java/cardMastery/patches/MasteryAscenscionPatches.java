package cardMastery.patches;

import cardMastery.CardMastery;
import cardMastery.helper.Mastery;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import org.apache.commons.lang3.math.NumberUtils;

public class MasteryAscenscionPatches {
    private static String uiText = CardCrawlGame.languagePack.getUIString(CardMastery.makeID("MasteryAscIndicator")).TEXT[0];
    @SpirePatch2(clz= SingleCardViewPopup.class, method = "render")
    public static class RenderAscenscionSCV {
        @SpirePostfixPatch
        public static void patch(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if(CardMastery.shouldIndicator()) {
                int rank = Mastery.getRank(___card);
                if(rank >= 0) {
                    String text = uiText + rank;
                    Color col = Settings.CREAM_COLOR.cpy();
                    col.lerp(Color.CYAN, NumberUtils.min(20, rank)/20f);
                    FontHelper.renderFontLeft(sb, FontHelper.tipHeaderFont, text, 8 * Settings.scale, Settings.HEIGHT * 0.9f, col);
                }
            }
        }
    }

    //Render the Asc on the bottom of the card, looked bad
    /*@SpirePatch2(clz = AbstractCard.class, method = "renderCard")
    @SpirePatch2(clz = AbstractCard.class, method = "renderInLibrary")
    public static class RenderAscOnCardSCV {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractCard __instance, SpriteBatch sb) {
            if(CardMastery.shouldIndicator()) {
                int rank = Mastery.getRank(__instance);
                if (rank >= 0) {
                    String text = String.valueOf(rank);
                    FontHelper.cardTitleFont.getData().setScale(__instance.drawScale * 0.7f);
                    if(rank >= 20) {
                        text = "*";
                        FontHelper.cardTitleFont.getData().setScale(__instance.drawScale * 0.8f);
                    }
                    Color col = Color.SALMON;
                    col.a = __instance.transparency * 0.45f;
                    FontHelper.renderRotatedText(sb,
                            FontHelper.cardTitleFont,
                            String.valueOf(rank),
                            __instance.current_x,
                            __instance.current_y,
                            //-(__instance.hb.width/2 - 10.0F * __instance.drawScale * Settings.scale),
                            0,
                            //-(__instance.hb.height/2 - 22.0F * __instance.drawScale * Settings.scale),
                            -(__instance.hb.height/2 - 8.0F * __instance.drawScale * Settings.scale),
                            __instance.angle,
                            false,
                            col
                    );
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                //Don't even ask me why, this should be renderHelper
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "renderTitle");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }*/
}

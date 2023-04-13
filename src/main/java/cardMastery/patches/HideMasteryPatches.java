package cardMastery.patches;

import cardMastery.CardMastery;
import cardMastery.helper.Mastery;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class HideMasteryPatches {
    @SpirePatch2(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class})
    @SpirePatch2(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class, CardGroup.class})
    public static class MoveHbOnOpen {
        @SpirePostfixPatch
        public static void patch() {
            toggleHb.move(Settings.WIDTH / 2.0F - 610.0F * Settings.scale, 70.0F * Settings.scale);
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "update")
    public static class UpdateHB {
        @SpirePostfixPatch
        public static void patch(AbstractCard ___card) {
            if(Mastery.isMastered(___card)) {
                update(___card);
            }
        }

        private static void update(AbstractCard c) {
            toggleHb.update();
            if (toggleHb.hovered && InputHelper.justClickedLeft)
                toggleHb.clickStarted = true;
            if (toggleHb.clicked || CInputActionSet.topPanel.isJustPressed()) {
                CInputActionSet.topPanel.unpress();
                toggleHb.clicked = false;
                if(Mastery.isHidden(c)) {
                    Mastery.unHide(c);
                } else {
                    Mastery.hide(c);
                }
            }
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "updateInput")
    public static class UpdateInputFix {
        @SpirePrefixPatch
        public static SpireReturn<?> patch() {
            if(toggleHb.hovered && InputHelper.justClickedLeft) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "render")
    public static class RenderBtn {
        @SpirePostfixPatch
        public static void patch(SpriteBatch sb, AbstractCard ___card) {
            if(Mastery.isMastered(___card)) {
                render(sb, ___card);
            }
        }
    }


    private static String uiText = CardCrawlGame.languagePack.getUIString(CardMastery.makeID("HideMasteryBtn")).TEXT[0];
    public static Hitbox toggleHb = new Hitbox(250.0F * Settings.scale, 80.0F * Settings.scale);
    private static void render(SpriteBatch sb, AbstractCard c) {
        if (toggleHb == null)
            return;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.CHECKBOX, toggleHb.cX - 80.0F * Settings.scale - 32.0F, toggleHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        if (toggleHb.hovered) {
            FontHelper.renderFont(sb, FontHelper.cardTitleFont, uiText, toggleHb.cX - 45.0F * Settings.scale, toggleHb.cY + 10.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);
        } else {
            FontHelper.renderFont(sb, FontHelper.cardTitleFont, uiText, toggleHb.cX - 45.0F * Settings.scale, toggleHb.cY + 10.0F * Settings.scale, Settings.GOLD_COLOR);
        }
        if (Mastery.isHidden(c)) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.TICK, toggleHb.cX - 80.0F * Settings.scale - 32.0F, toggleHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        toggleHb.render(sb);
    }
}

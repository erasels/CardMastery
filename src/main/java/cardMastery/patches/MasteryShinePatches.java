package cardMastery.patches;

import cardMastery.CardMastery;
import cardMastery.helper.ImageHelper;
import cardMastery.helper.Mastery;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public class MasteryShinePatches {
    //Copied nearly wholesale from vex's Fishing character's foil cards. Big thanks to him
    @SpirePatch2(clz = AbstractCard.class, method = "render", paramtypez = SpriteBatch.class)
    @SpirePatch2(clz = AbstractCard.class, method = "renderInLibrary", paramtypez = SpriteBatch.class)
    public static class FoilCardsShine {
        public static final ShaderProgram SHINE_SHADER = new ShaderProgram(SpriteBatch.createDefaultShader().getVertexShaderSource(), Gdx.files.internal("cardMasteryResources/img/card/shine.frag").readString(String.valueOf(StandardCharsets.UTF_8)));
        private static final FrameBuffer fbo = ImageHelper.createBuffer();

        private static int RUNNING_ON_STEAM_DECK = -1;

        private static final String OS = System.getProperty("os.name").toLowerCase();
        public static boolean IS_WINDOWS = (OS.indexOf("win") >= 0);

        public static boolean isOnSteamDeck() {
            if (RUNNING_ON_STEAM_DECK == -1) {
                try {
                    RUNNING_ON_STEAM_DECK = CardCrawlGame.clientUtils.isSteamRunningOnSteamDeck() ? 1 : 0;
                } catch (IllegalAccessError e) {
                    System.out.println("VEX OVERRIDE DETECTED: GOG PLAYER");
                    RUNNING_ON_STEAM_DECK = 0;
                }
            }
            return RUNNING_ON_STEAM_DECK == 1;
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> applyShader(AbstractCard __instance, SpriteBatch sb) {
            if (!Settings.hideCards) {
                if (IS_WINDOWS && !isOnSteamDeck() && CardMastery.shouldAnim() && Mastery.shouldShowMastery(__instance)) {
                    TextureRegion t = cardToTextureRegion(__instance, sb);
                    sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    ShaderProgram oldShader = sb.getShader();
                    sb.setShader(SHINE_SHADER);
                    SHINE_SHADER.setUniformf("x_time", CardMastery.time);
                    sb.draw(t, -Settings.VERT_LETTERBOX_AMT, -Settings.HORIZ_LETTERBOX_AMT);
                    sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    sb.setShader(oldShader);
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        public static TextureRegion cardToTextureRegion(AbstractCard card, SpriteBatch sb) {
            sb.end();
            ImageHelper.beginBuffer(fbo);
            sb.begin();
            IntBuffer buf_rgb = BufferUtils.newIntBuffer(16);
            IntBuffer buf_a = BufferUtils.newIntBuffer(16);
            Gdx.gl.glGetIntegerv(GL30.GL_BLEND_EQUATION_RGB, buf_rgb);
            Gdx.gl.glGetIntegerv(GL30.GL_BLEND_EQUATION_ALPHA, buf_a);

            Gdx.gl.glBlendEquationSeparate(buf_rgb.get(0), GL30.GL_MAX);
            Gdx.gl.glBlendEquationSeparate(GL30.GL_FUNC_ADD, GL30.GL_MAX);
            card.render(sb, false);
            Gdx.gl.glBlendEquationSeparate(GL30.GL_FUNC_ADD, GL30.GL_FUNC_ADD);
            Gdx.gl.glBlendEquationSeparate(buf_rgb.get(0), buf_a.get(0));

            sb.end();
            fbo.end();
            sb.begin();
            return ImageHelper.getBufferTexture(fbo);
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderEnergy")
    public static class FixSeeThroughWhenShining {
        //Copies the early return trigger condition, might still not work if something else spireReturns before the renderHelper call
        @SpirePrefixPatch
        public static void patch(AbstractCard __instance, SpriteBatch sb, boolean ___darken, Color ___renderColor) {
            if (__instance.cost <= -2 || ___darken || __instance.isLocked || !__instance.isSeen) {
                sb.setColor(___renderColor);
            }
        }
    }
}

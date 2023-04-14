package cardMastery.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class MoveCardAroundEffect extends AbstractGameEffect {
    private AbstractCard c;
    private float startX;
    float targetX;
    float startY;
    float targetY;
    float delay;

    public MoveCardAroundEffect(AbstractCard card, float startX, float targetX, float startY, float targetY, float duration, float delay) {
        c = card;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.duration = startingDuration = duration;
        this.delay = delay;

        c.current_x = c.target_x = startX;
        c.current_y = c.target_y = startY;
    }

    public MoveCardAroundEffect(AbstractCard card, float startX, float targetX, float startY, float targetY, float duration) {
        this(card, startX, targetX, startY, targetY, duration, 0);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if(delay > 0) return;
        c.render(spriteBatch);
    }

    @Override
    public void update() {
        if(delay > 0) {
            delay -= Gdx.graphics.getRawDeltaTime();
            return;
        }
        c.update();
        duration -= Gdx.graphics.getRawDeltaTime();
        c.current_x = c.target_x = Interpolation.linear.apply(startX, targetX, 1 - (duration/startingDuration));
        c.current_y = c.target_y = Interpolation.linear.apply(startY, targetY, 1 - (duration/startingDuration));
        if(duration <= 0) isDone = true;
    }

    @Override
    public void dispose() {

    }
}

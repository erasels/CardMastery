package cardMastery.helper;

import cardMastery.CardMastery;
import com.megacrit.cardcrawl.cards.AbstractCard;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class Mastery {
    public static HashMap<String, Integer> masteredCards = new HashMap<>();
    public static HashSet<String> hiddenMasteries = new HashSet<>();

    public static boolean isMastered(AbstractCard c) {
        return isMastered(c.cardID);
    }
    public static boolean isMastered(String cID) {
        return masteredCards.containsKey(cID);
    }

    public static int getRank(AbstractCard c) {
        return getRank(c.cardID);
    }
    public static int getRank(String cID) {
        return masteredCards.getOrDefault(cID, -1);
    }

    public static boolean shouldShowMastery(AbstractCard c) {
        return isMastered(c) && !hiddenMasteries.contains(c.cardID);
    }

    public static void master(AbstractCard c, int asc) {
        master(c.cardID, asc);
    }
    public static void master(String cID, int asc) {
        int rank = getRank(cID);
        if(rank >= 0) {
            if(asc > rank) {
                masteredCards.put(cID, asc);
                saveMasteries();
            }
        } else {
            masteredCards.put(cID, NumberUtils.max(0, asc));
            saveMasteries();
        }
    }

    public static boolean willMaster(AbstractCard c, int asc) {
        int rank = getRank(c.cardID);
        if(rank >= 0) {
            if(asc > rank) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public static void hide(AbstractCard c) {
        hiddenMasteries.add(c.cardID);
        saveHiddenMasteries();
    }
    public static void unHide(AbstractCard c) {
        hiddenMasteries.remove(c.cardID);
        saveHiddenMasteries();
    }

    public static boolean isHidden(AbstractCard c) {
        return hiddenMasteries.contains(c.cardID);
    }

    private static void saveMasteries() {
        ArrayList<String> cards = new ArrayList<>();
        for(Map.Entry<String, Integer> e : masteredCards.entrySet()) {
            cards.add(e.getKey() + "|" + e.getValue());
        }
        CardMastery.saveMasteries(String.join(",", cards));
    }

    private static void saveHiddenMasteries() {
        CardMastery.saveHiddenMasteries(String.join(",", hiddenMasteries));
    }

    public static void saveState() {
        saveMasteries();
        saveHiddenMasteries();
    }

    public static void init() {
        String masteries = CardMastery.loadMasteries();
        String hides = CardMastery.loadHiddenMasteries();

        if(!masteries.isEmpty()) {
            for (String cn : masteries.split(",")) {
                String[] e = cn.split("\\|");
                masteredCards.put(e[0], Integer.parseInt(e[1]));
            }

            hiddenMasteries.addAll(Arrays.asList(hides.split(",")));
        }
    }
}

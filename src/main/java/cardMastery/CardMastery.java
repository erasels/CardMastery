package cardMastery;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.devcommands.ConsoleCommand;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import cardMastery.commands.ClearCommand;
import cardMastery.commands.MasterCommand;
import cardMastery.commands.UnMasterCommand;
import cardMastery.helper.Mastery;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@SpireInitializer
public class CardMastery implements
        PostInitializeSubscriber,
        EditStringsSubscriber, PostUpdateSubscriber {

    private static SpireConfig modConfig = null;
    private static String modID;
    public static final Logger logger = LogManager.getLogger(CardMastery.class.getName());

    public static void initialize() {
        BaseMod.subscribe(new CardMastery());
        setModID("cardMastery");

        try {
            Properties defaults = new Properties();
            defaults.put("BG", Boolean.toString(true));
            defaults.put("Animation", Boolean.toString(true));
            defaults.put("AscIndicator", Boolean.toString(true));
            defaults.put("SkipCurses", Boolean.toString(true));
            defaults.put("ColoredVelvet", Boolean.toString(true));
            defaults.put("TwoCopies", Boolean.toString(false));
            defaults.put("MasteredCards", "");
            defaults.put("HiddenMasteries", "");
            modConfig = new SpireConfig("cardMastery", "Config", defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldBG() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("BG");
    }

    public static boolean shouldAnim() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("Animation");
    }

    public static boolean shouldIndicator() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("AscIndicator");
    }

    public static boolean shouldSkipCurses() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("SkipCurses");
    }

    public static boolean requiresTwo() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("TwoCopies");
    }

    public static void saveMasteries(String s) {
        if (modConfig == null) {
            return;
        }
        modConfig.setString("MasteredCards", s);
        try {
            modConfig.save();
        } catch (Exception e) {
            logger.error("Cannot save stuff for some reason: " + e + " \nInput: " + s);
        }
    }
    public static void saveHiddenMasteries(String s) {
        if (modConfig == null) {
            return;
        }
        modConfig.setString("HiddenMasteries", s);
        try {
            modConfig.save();
        } catch (Exception e) {
            logger.error("Cannot save stuff for some reason: " + e + " \nInput: " + s);
        }
    }

    public static String loadMasteries() {
        if (modConfig == null) {
            return "";
        }
        return modConfig.getString("MasteredCards");
    }

    public static boolean shouldColor() {
        if (modConfig == null) {
            return false;
        }
        return modConfig.getBool("ColoredVelvet");
    }


    public static String loadHiddenMasteries() {
        if (modConfig == null) {
            return "";
        }
        return modConfig.getString("HiddenMasteries");
    }

    @Override
    public void receivePostInitialize() {
        UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(CardMastery.makeID("OptionsMenu"));
        String[] TEXT = UIStrings.TEXT;

        ModPanel settingsPanel = new ModPanel();
        ModLabeledToggleButton BGBtn = new ModLabeledToggleButton(TEXT[0], 350, 700, Settings.CREAM_COLOR, FontHelper.charDescFont, shouldBG(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("BG", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(BGBtn);

        ModLabeledToggleButton AnimBtn = new ModLabeledToggleButton(TEXT[1], 350, 650, Settings.CREAM_COLOR, FontHelper.charDescFont, shouldAnim(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("Animation", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(AnimBtn);

        ModLabeledToggleButton IndcBtn = new ModLabeledToggleButton(TEXT[2], 350, 600, Settings.CREAM_COLOR, FontHelper.charDescFont, shouldIndicator(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("AscIndicator", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(IndcBtn);

        ModLabeledToggleButton SCBtn = new ModLabeledToggleButton(TEXT[3], 350, 550, Settings.CREAM_COLOR, FontHelper.charDescFont, shouldSkipCurses(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("SkipCurses", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(SCBtn);


        ModLabeledToggleButton VCBtn = new ModLabeledToggleButton(TEXT[4], 350, 500, Settings.CREAM_COLOR, FontHelper.charDescFont, shouldColor(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("ColoredVelvet", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(VCBtn);
  
        ModLabeledToggleButton TCBtn = new ModLabeledToggleButton(TEXT[4], 350, 500, Settings.CREAM_COLOR, FontHelper.charDescFont, requiresTwo(), settingsPanel, l -> {
        },
                button ->
                {
                    if (modConfig != null) {
                        modConfig.setBool("TwoCopies", button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingsPanel.addUIElement(TCBtn);

        ConsoleCommand.addCommand("mastercard", MasterCommand.class);
        ConsoleCommand.addCommand("unmastercard", UnMasterCommand.class);
        ConsoleCommand.addCommand("clearmasteries", ClearCommand.class);
        Mastery.init();

        BaseMod.registerModBadge(ImageMaster.loadImage(getModID() + "Resources/img/modBadge.png"), getModID(), "erasels", "TODO", settingsPanel);
    }

    @Override
    public void receiveEditStrings() {
        loadLocStrings("eng");
        if (!languageSupport().equals("eng")) {
            loadLocStrings(languageSupport());
        }
    }

    public static String getModID() {
        return modID;
    }

    public static void setModID(String id) {
        modID = id;
    }

    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    private String languageSupport() {
		String language = Settings.language.name().toLowerCase();
		String urlPath = getModID() + "Resources/localization/" + language + "/UI-Strings.json";
		ClassLoader classLoader = UIStrings.class.getClassLoader();
		URL url = classLoader.getResource(urlPath);

		if (url != null) {
			return language;
		} else {
			return "eng";
		}
		
    }

    private void loadLocStrings(String language) {
        BaseMod.loadCustomStringsFile(UIStrings.class, getModID() + "Resources/localization/" + language + "/UI-Strings.json");
    }
    public static String makePath(String resourcePath) {
        return getModID() + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return getModID() + "Resources/img/" + resourcePath;
    }

    public static float time = 0f;

    @Override
    public void receivePostUpdate() {
        time += Gdx.graphics.getRawDeltaTime();
    }
}
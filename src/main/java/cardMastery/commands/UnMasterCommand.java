package cardMastery.commands;

import basemod.BaseMod;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import cardMastery.helper.Mastery;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UnMasterCommand extends ConsoleCommand {
    public UnMasterCommand() {
        minExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (tokens.length != 2) {
            DevConsole.log("You need to add the ID of the card you want to unmaster.");
            return;
        }
        String cardID = tokens[1];
        if (BaseMod.underScoreCardIDs.containsKey(cardID)) {
            cardID = BaseMod.underScoreCardIDs.get(cardID);
        }

        if(CardLibrary.getCard(cardID) != null) {
            if (Mastery.isMastered(cardID)) {
                Mastery.masteredCards.remove(cardID);
                Mastery.hiddenMasteries.remove(cardID);
                Mastery.saveState();
                DevConsole.log("Removed card mastery for: " + cardID);
            } else {
                DevConsole.log("Card is not mastered.");
            }
        } else {
            errorMsg();
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        return Mastery.masteredCards.keySet().stream()
                .map(c -> c.replace(' ', '_'))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("Please put in a valid card ID.");
    }
}

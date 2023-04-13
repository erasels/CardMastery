package cardMastery.commands;

import basemod.BaseMod;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import cardMastery.CardMastery;
import cardMastery.helper.Mastery;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class MasterCommand extends ConsoleCommand {
    public MasterCommand() {
        minExtraTokens = 2;
        maxExtraTokens = 3;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (tokens.length < 2 || tokens.length > 3) {
            DevConsole.log("You need to add the ID and mastery rank of the card you want to master.");
            return;
        }
        String cardID = tokens[1].trim();
        if (BaseMod.underScoreCardIDs.containsKey(cardID)) {
            cardID = BaseMod.underScoreCardIDs.get(cardID);
        }

        if(CardLibrary.getCard(cardID) != null) {
            if (!Mastery.isMastered(cardID)) {
                try {
                    int rank = Integer.parseInt(tokens[2]);
                    if(rank < 0) rank = 0;
                    else if (rank > 20) rank = 20;
                    Mastery.master(cardID, rank);
                    CardMastery.logger.info("Mastered: " + cardID);
                } catch (Exception e) {
                    CardMastery.logger.error(e);
                }
            } else {
                DevConsole.log("Card is already mastered.");
            }
        } else {
            errorMsg();
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        if(tokens.length == 2) {
            Set<String> masteredCards = Mastery.masteredCards.keySet();
            return ConsoleCommand.getCardOptions().stream()
                    .filter(c -> !masteredCards.contains(c))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else{
            return smallNumbers();
        }
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("Please put in a valid card ID.");
    }
}

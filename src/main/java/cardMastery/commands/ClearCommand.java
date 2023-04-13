package cardMastery.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import cardMastery.CardMastery;
import cardMastery.helper.Mastery;

public class ClearCommand extends ConsoleCommand {
    public ClearCommand() {
        minExtraTokens = 0;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        Mastery.hiddenMasteries.clear();
        Mastery.masteredCards.clear();

        Mastery.saveState();
        CardMastery.logger.warn("Cleared all masteries! If this was a mistake... rip.");
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
    }
}

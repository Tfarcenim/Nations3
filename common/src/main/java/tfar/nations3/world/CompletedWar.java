package tfar.nations3.world;

import java.util.Objects;

public final class CompletedWar {
    private final Nation winner;
    private final Nation loser;
    private final WarTerms warTerms;

    public CompletedWar(Nation winner, Nation loser) {
        this.winner = winner;
        this.loser = loser;
        warTerms = new WarTerms();
    }

    public Nation winner() {
        return winner;
    }

    public Nation loser() {
        return loser;
    }

    public WarTerms getWarTerms() {
        return warTerms;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CompletedWar) obj;
        return Objects.equals(this.winner, that.winner) &&
                Objects.equals(this.loser, that.loser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner, loser);
    }

    @Override
    public String toString() {
        return "CompletedWar[" +
                "winner=" + winner + ", " +
                "loser=" + loser + ']';
    }


    public class WarTerms {
        public double money_percentage;

    }

}
//With a command /nation war_terms, opens a chest gui, after winning they have a timeframe to confirm this, for e.g 30 minutes
//The percentage of money you demand from the opposing side
//Whether they need to pay war reparations for a given timeframe, max of one real-life month for example
//Whether you take all the chunks or allow them to keep their land
//Going deeper into this, potentially after winning, the ability to pick specific chunks to keep and chunks the defeated nation can keep
//Nations can have multiple towns right? So what towns they want to take instead of specific chunks would be the best solution in my opinion. They can pick all, none, or some.
//All I can think of right now, but I think that rounds it up well
package core.actions.unitactions;

import core.TribesConfig;
import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;
import core.Types;

import java.util.LinkedList;

public class MakeVeteran extends UnitAction
{
    public MakeVeteran(int unitId)
    {
        super.unitId = unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        if(unit.getType() == Types.UNIT.SUPERUNIT)
            return false;
        return unit.getKills() >= gs.getTribesConfig().VETERAN_KILLS && !unit.isVeteran();
    }

    @Override
    public boolean execute(GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        if(isFeasible(gs))
        {
            unit.setVeteran(true);
            unit.setMaxHP(unit.getMaxHP() + gs.getTribesConfig().VETERAN_PLUS_HP);
            unit.setCurrentHP(unit.getMaxHP());
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        return new MakeVeteran(this.unitId);
    }

    public String toString() {
        return "MAKE_VETERAN by unit " + this.unitId;
    }
}

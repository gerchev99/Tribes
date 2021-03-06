package core.actions.unitactions;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Battleship;
import core.actors.units.Boat;
import core.actors.units.Ship;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import static core.Types.UNIT.*;

public class Upgrade extends UnitAction
{
    public Upgrade(int unitId)
    {
        super.unitId = unitId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Tribe tribe = gs.getTribe(unit.getTribeId());
        TechnologyTree ttree = tribe.getTechTree();

        int stars = gs.getTribe(unit.getTribeId()).getStars();
        return ((unit.getType() == BOAT && ttree.isResearched(Types.TECHNOLOGY.SAILING) && stars >= SHIP.getCost(SHIP.getKey(), gs.getTribesConfig())) ||
                (unit.getType() == SHIP && ttree.isResearched(Types.TECHNOLOGY.NAVIGATION) && stars >= BATTLESHIP.getCost(BATTLESHIP.getKey(), gs.getTribesConfig())));
    }

    @Override
    public boolean execute(GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Tribe tribe = gs.getTribe(unit.getTribeId());
        Board board = gs.getBoard();
        City city = (City) board.getActor(unit.getCityId());

        if(isFeasible(gs)){
            Types.UNIT unitType = unit.getType();
            Types.UNIT nextType;

            //get the correct type - or nothing!
            if(unitType == BOAT) nextType = SHIP;
            else if(unitType == SHIP) nextType = BATTLESHIP;
            else return false; //this shouldn't happen, isFeasible should've captured this case

            //Create the new unit
            Unit newUnit = Types.UNIT.createUnit(unit.getPosition(), unit.getKills(), unit.isVeteran(), unit.getCityId(), unit.getTribeId(), nextType,gs);
            newUnit.setCurrentHP(unit.getCurrentHP());
            newUnit.setMaxHP(unit.getMaxHP());
            if(nextType == SHIP)
                ((Ship)newUnit).setBaseLandUnit(((Boat)unit).getBaseLandUnit());
            else
                ((Battleship)newUnit).setBaseLandUnit(((Ship)unit).getBaseLandUnit());

            //adjustments in tribe and board.
            tribe.subtractStars(nextType.getCost(nextType.getKey(), gs.getTribesConfig()));

            Types.TURN_STATUS turn_status = unit.getStatus();
            //We first remove the unit, so there's space for the new one to take its place.
            board.removeUnitFromBoard(unit);
            board.removeUnitFromCity(unit, city, tribe);
            board.addUnit(city, newUnit);
            newUnit.setStatus(turn_status);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        return new Upgrade(this.unitId);
    }

    public String toString() {
        return "UPGRADE by unit " + this.unitId;
    }
}

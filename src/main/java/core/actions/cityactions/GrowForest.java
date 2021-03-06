package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import utils.Vector2d;

import java.util.LinkedList;

public class GrowForest extends CityAction
{

    public GrowForest(int cityId)
    {
        super.cityId = cityId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        TribesConfig tc = gs.getTribesConfig();
        City city = (City) gs.getActor(this.cityId);
        Board b = gs.getBoard();
        if(b.getTerrainAt(targetPos.x, targetPos.y) != Types.TERRAIN.PLAIN) return false;
        if(b.getCityIdAt(targetPos.x, targetPos.y) != city.getActorId()) return false;

        Tribe t = gs.getTribe(city.getTribeId());
        if(t.getStars() < tc.GROW_FOREST_COST) return false;
        return t.getTechTree().isResearched(Types.TECHNOLOGY.SPIRITUALISM);
    }

    @Override
    public boolean execute(GameState gs) {
        if (isFeasible(gs)){
            TribesConfig tc = gs.getTribesConfig();
            Board b = gs.getBoard();
            City city = (City) gs.getActor(this.cityId);
            b.setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.FOREST);
            b.setResourceAt(targetPos.x, targetPos.y, null);
            gs.getTribe(city.getTribeId()).subtractStars(tc.GROW_FOREST_COST);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        GrowForest grow = new GrowForest(this.cityId);
        grow.setTargetPos(this.targetPos.copy());
        return grow;
    }

    public String toString()
    {
        return "GROW_FOREST by city " + this.cityId+ " at " + targetPos;
    }
}

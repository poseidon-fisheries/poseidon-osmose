/*
 *     POSEIDON, an agent-based model of fisheries
 *     Copyright (C) 2017  CoHESyS Lab cohesys.lab@gmail.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package uk.ac.ox.poseidon.osmose;

import uk.ac.ox.oxfish.biology.LocalBiology;
import uk.ac.ox.oxfish.biology.Species;
import uk.ac.ox.oxfish.fisher.equipment.Catch;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.event.AbstractYearlyTargetExogenousCatches;
import uk.ac.ox.oxfish.utility.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Used to simulate shrimp-related snapper mortality (which is concentrated in 0-1 juveniles)
 * Created by carrknight on 6/2/17.
 */
public class OsmoseBoundedExogenousCatches extends AbstractYearlyTargetExogenousCatches {


    private final Map<Species, Pair<Integer, Integer>> ageBounds;

    public OsmoseBoundedExogenousCatches(
        LinkedHashMap<Species, Double> exogenousYearlyCatchesInKg,
        Map<Species, Pair<Integer, Integer>> ageBounds, final String dataColumnName
    ) {
        super(exogenousYearlyCatchesInKg, dataColumnName);
        this.ageBounds = ageBounds;
    }

    /**
     * simulate exogenous catch
     *
     * @param simState the model
     * @param target   species to kill
     * @param tile     where to kill it
     * @param step     how much at most to kill
     * @return
     */
    @Override
    protected Catch mortalityEvent(
        FishState simState, Species target, LocalBiology tile, double step
    ) {
        LocalOsmoseWithoutRecruitmentBiology biology = (LocalOsmoseWithoutRecruitmentBiology) tile;
        System.out.println(step + " ---- " + getFishableBiomass(target, tile));
        double toKill = Math.min(step, getFishableBiomass(target, tile));
        Catch caught = new Catch(target, toKill, simState.getBiology());
        biology.catchThisSpeciesByThisAmountWithinTheseAgeBounds(
            caught,
            caught,
            target.getIndex(),
            ageBounds.get(target).getFirst(),
            ageBounds.get(target).getSecond()
        );
        return caught;

    }


    @Override
    protected Double getFishableBiomass(Species target, LocalBiology seaTile) {
        return ((LocalOsmoseWithoutRecruitmentBiology) seaTile).getBiomass(
            target.getIndex(),
            ageBounds.get(target).getFirst(),
            ageBounds.get(target).getSecond()
        );
    }
}

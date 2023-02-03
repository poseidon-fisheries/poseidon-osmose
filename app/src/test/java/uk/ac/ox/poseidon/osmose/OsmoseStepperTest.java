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

import ec.util.MersenneTwisterFast;
import fr.ird.osmose.OsmoseSimulation;
import org.junit.Test;
import org.mockito.Mockito;
import uk.ac.ox.oxfish.model.FishState;


public class OsmoseStepperTest {

    @Test
    public void stepsCorrectly() {

        OsmoseSimulation osmose = Mockito.mock(OsmoseSimulation.class);
        Mockito.when(osmose.stepsPerYear()).thenReturn(365);
        OsmoseStepper stepper = new OsmoseStepper(365 * 2, osmose, new MersenneTwisterFast());
        final LocalOsmoseWithoutRecruitmentBiology localBiology = Mockito.mock(LocalOsmoseWithoutRecruitmentBiology.class);
        stepper.getToReset().add(localBiology);
        stepper.start(Mockito.mock(FishState.class));


        for (int i = 0; i < 10; i++)
            stepper.step(Mockito.mock(FishState.class));

        Mockito.verify(osmose, Mockito.times(5)).oneStep();
        Mockito.verify(localBiology, Mockito.times(5)).osmoseStep();


    }
}
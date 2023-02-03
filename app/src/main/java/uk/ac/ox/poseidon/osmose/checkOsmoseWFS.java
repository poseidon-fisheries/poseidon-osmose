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

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.nio.file.Paths;
import uk.ac.ox.oxfish.model.FishState;
import uk.ac.ox.oxfish.model.data.collectors.DataColumn;
import uk.ac.ox.oxfish.model.network.EmptyNetworkBuilder;
import uk.ac.ox.oxfish.model.scenario.PrototypeScenario;
import uk.ac.ox.oxfish.utility.FishStateUtilities;

/**
 * Run simulation with fishing mortality set by OSMOSE and make sure it matches what Arnaud has
 * Created by carrknight on 10/26/16.
 */
public class checkOsmoseWFS {

    public static final Path OUTPUT_FOLDER = Paths.get("temp_wfs", "output");
    public static final Path INPUT_FOLDER = Paths.get("temp_wfs", "wfs");

    /**
     * histogram of 100 observations
     */
    public static void main(String[] args) {
        // mortalityHistogram("nogrouper", "2");
        // mortalityHistogram("no_intervention", "");
        mortalityHistogram("grouper_poseidon", "2");
    }

    public static void mortalityHistogram(final String simulationName, final String speciesToManage) {
        // because i am planning on running this once I am just going to hard-code this in.
        String configurationLocation =
                INPUT_FOLDER.resolve("osm_all-parameters.csv").toAbsolutePath().toString();

        DataColumn earlyInitialization = new DataColumn("after_initialization");
        DataColumn twenty = new DataColumn("20 years");

        for (int i = 0; i < 100; i++) {
            PrototypeScenario scenario = new PrototypeScenario();
            scenario.setMapInitializer(new OsmoseMapInitializerFactory());
            OsmoseBiologyFactory biologyFactory = new OsmoseBiologyFactory();
            biologyFactory.setIndexOfSpeciesToBeManagedByThisModel(speciesToManage);
            scenario.setBiologyInitializer(biologyFactory);
            scenario.setFishers(0); // no fishers
            scenario.setNetworkBuilder(new EmptyNetworkBuilder()); // no social network
            biologyFactory.setNumberOfOsmoseStepsToPulseBeforeSimulationStart(114 * 12);
            biologyFactory.setOsmoseConfigurationFile(configurationLocation);
            biologyFactory.setPreInitializedConfiguration(false);
            FishState state = new FishState(i);
            state.setScenario(scenario);
            state.start();

            Preconditions.checkState(state.getSpecies().get(2).getName().trim().equalsIgnoreCase("redgrouper"));
            earlyInitialization.add(state.getTotalBiomass(state.getSpecies().get(2)));

            while (state.getYear() <= 20) state.schedule.step(state);

            twenty.add(state.getTotalBiomass(state.getSpecies().get(2)));
        }

        OUTPUT_FOLDER.toFile().mkdir();
        FishStateUtilities.printCSVColumnsToFile(
                OUTPUT_FOLDER.resolve(simulationName + "_docking.csv").toFile(), earlyInitialization, twenty);
    }
}

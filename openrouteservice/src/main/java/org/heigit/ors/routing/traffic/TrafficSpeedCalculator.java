package org.heigit.ors.routing.traffic;

import com.graphhopper.routing.EdgeKeys;
import com.graphhopper.routing.profiles.DecimalEncodedValue;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.SpeedCalculator;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.util.EdgeIteratorState;
import org.heigit.ors.routing.graphhopper.extensions.storages.GraphStorageUtils;
import org.heigit.ors.routing.graphhopper.extensions.storages.TrafficGraphStorage;

import java.util.HashMap;
import java.util.Map;

public class TrafficSpeedCalculator implements SpeedCalculator {
    protected final DecimalEncodedValue avSpeedEnc;
    // time-dependent stuff
    private final TrafficGraphStorage trafficGraphStorage;
    public Map<Double, Double> changedSpeedCount = new HashMap();
    public Map<Double, Double> changedSpeed = new HashMap();
    private int i = 0;


    public TrafficSpeedCalculator(GraphHopperStorage graph, FlagEncoder encoder) {
        avSpeedEnc = encoder.getAverageSpeedEnc();

        // time-dependent stuff
        EncodingManager encodingManager = graph.getEncodingManager();
        trafficGraphStorage = GraphStorageUtils.getGraphExtension(graph, TrafficGraphStorage.class);
    }

    public double getSpeed(EdgeIteratorState edge, boolean reverse, long time) {
        double speed = reverse ? edge.getReverse(avSpeedEnc) : edge.get(avSpeedEnc);

//         retrieve time-dependent maxspeed here
        if (time != -1) {
            int edgeId = EdgeKeys.getOriginalEdge(edge);
            double trafficSpeed = trafficGraphStorage.getSpeedValue(edgeId, edge.getBaseNode(), edge.getAdjNode(), time);
            if (trafficSpeed != -1) {
                if (speed >= 45.0 && !(trafficSpeed > 1.1 * speed)
                        || trafficSpeed < speed) {
                    changedSpeed.put(speed, changedSpeed.getOrDefault(speed, 0.0) + trafficSpeed*edge.getDistance());
                    changedSpeedCount.put(speed, changedSpeedCount.getOrDefault(speed, 0.0) + edge.getDistance());
                    speed = trafficSpeed;
                }
            }
        }
        return speed;
    }
}

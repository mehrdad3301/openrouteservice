/*  This file is part of Openrouteservice.
 *
 *  Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.

 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License along with this library;
 *  if not, see <https://www.gnu.org/licenses/>.
 */
package org.heigit.ors.routing.graphhopper.extensions;

import com.graphhopper.routing.*;
import com.graphhopper.util.StopWatch;
import org.heigit.ors.routing.graphhopper.extensions.core.AbstractCoreRoutingAlgorithm;
import org.heigit.ors.routing.graphhopper.extensions.core.CoreRoutingAlgorithmFactory;

import java.util.List;

public class CorePathCalculator implements PathCalculator {
    private final CoreRoutingAlgorithmFactory algoFactory;
    private final AlgorithmOptions algoOpts;
    private String debug;
    private int visitedNodes;

    public CorePathCalculator(CoreRoutingAlgorithmFactory algoFactory, AlgorithmOptions algoOpts) {
        this.algoFactory = algoFactory;
        this.algoOpts = algoOpts;
    }

    @Override
    public List<Path> calcPaths(int from, int to, EdgeRestrictions edgeRestrictions) {
        if (!edgeRestrictions.getUnfavoredEdges().isEmpty())
            throw new IllegalArgumentException("Using unfavored edges is currently not supported for CH");
        AbstractCoreRoutingAlgorithm algo = createAlgo();
        return calcPaths(from, to, edgeRestrictions, algo);
    }

    private AbstractCoreRoutingAlgorithm createAlgo() {
        StopWatch sw = new StopWatch().start();
        AbstractCoreRoutingAlgorithm algo = algoFactory.createAlgo(algoOpts);
        debug = ", algoInit:" + (sw.stop().getNanos() / 1000) + " μs";
        return algo;
    }

    private List<Path> calcPaths(int from, int to, EdgeRestrictions edgeRestrictions, AbstractCoreRoutingAlgorithm algo) {
        StopWatch sw = new StopWatch().start();
        List<Path> paths;
        /* FIXME
        if (edgeRestrictions.getSourceOutEdge() != ANY_EDGE || edgeRestrictions.getTargetInEdge() != ANY_EDGE) {
            paths = Collections.singletonList(algo.calcPath(from, to,
                    edgeRestrictions.getSourceOutEdge(),
                    edgeRestrictions.getTargetInEdge()));
        } else {
            paths = algo.calcPaths(from, to);
        }
        */
        paths = algo.calcPaths(from, to);
        if (paths.isEmpty())
            throw new IllegalStateException("Path list was empty for " + from + " -> " + to);
        if (algo.getVisitedNodes() >= algoOpts.getMaxVisitedNodes())
            throw new IllegalArgumentException("No path found due to maximum nodes exceeded " + algoOpts.getMaxVisitedNodes());
        visitedNodes = algo.getVisitedNodes();
        debug += ", " + algo.getName() + "-routing:" + sw.stop().getMillis() + " ms";
        return paths;
    }

    @Override
    public String getDebugString() {
        return debug;
    }

    @Override
    public int getVisitedNodes() {
        return visitedNodes;
    }

}
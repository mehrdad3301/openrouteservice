/*
 * This file is part of Openrouteservice.
 *
 * Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, see <https://www.gnu.org/licenses/>.
 */

package org.heigit.ors.api.responses.routing.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.heigit.ors.api.requests.routing.RouteRequest;
import org.heigit.ors.api.responses.routing.json.JSONBasedIndividualRouteResponse;
import org.heigit.ors.api.responses.routing.json.JSONExtra;
import org.heigit.ors.api.responses.routing.json.JSONSegment;
import org.heigit.ors.exceptions.StatusCodeException;
import org.heigit.ors.geojson.GeometryJSON;
import org.heigit.ors.routing.RouteResult;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class GeoJSONIndividualRouteResponse extends JSONBasedIndividualRouteResponse {
    @JsonProperty("type")
    public final String type = "Feature";

    @JsonProperty("properties")
    private final GeoJSONSummary properties;

    public GeoJSONIndividualRouteResponse(RouteResult routeResult, RouteRequest request) throws StatusCodeException {
        super(routeResult, request);
        List<JSONSegment> segments = constructSegments(routeResult, request);

        Map<String, JSONExtra> extras = constructExtras(request, routeResult);

        properties = new GeoJSONSummary(routeResult, segments, extras, this.includeElevation, this.isPtRequest, constructLegs(routeResult));
    }

    @Schema(implementation = JSONObject.class, description = "The geometry of the route. For GeoJSON route responses this is a JSON LineString.")
    @JsonProperty("geometry")
    public JSONObject getGeometry() {
        JSONObject geoJson = new JSONObject();
        geoJson.put("type", "LineString");
        geoJson.put("coordinates", GeometryJSON.toJSON(this.routeCoordinates, includeElevation));

        return geoJson;
    }

    public GeoJSONSummary getProperties() {
        return properties;
    }

    @JsonProperty("bbox")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public double[] getBBox() {
        return bbox.getAsArray();
    }
}

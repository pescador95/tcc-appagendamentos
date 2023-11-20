package app.core.resources.profile;

import app.core.model.DTO.Responses;
import app.core.model.profile.TimeZone;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Path("/timezones")
public class TimeZoneResources {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response list() {
        List<TimeZone> allTimeZones = ZoneId.getAvailableZoneIds().stream()
                .sorted()
                .map(zoneId -> {
                    ZoneId zone = ZoneId.of(zoneId);
                    String offset = zone.getRules().getOffset(Instant.now()).toString();
                    return new TimeZone(zoneId, offset);
                })
                .toList();
        Responses responses = new Responses();
        responses.setDatas(Collections.singletonList(allTimeZones));
        return Response.ok(responses) .status(200).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response get(@PathParam("id") Long id) {
        TimeZone timeZone = TimeZone.findById(id);
        Responses responses = new Responses();
        responses.setData(timeZone);
        return Response.ok(responses).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"admin"})
    @Transactional
    public Response create(TimeZone pTimeZone) {
        Responses responses = new Responses();
        TimeZone timeZone = TimeZone.find("timeZoneOffset = ?1", pTimeZone.getTimeZoneOffset()).firstResult();
        if (timeZone != null){
            pTimeZone.persist();
            responses.setData(pTimeZone);
        }
        return Response.ok(responses).status(200).build();
    }

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"admin"})
    @Transactional
    public Response createByList(List<TimeZone> timeZones) {
        List<TimeZone> allTimeZones = TimeZone.listAll();
        timeZones.forEach(timezone -> {
            if(!allTimeZones.contains(timezone)){
                timezone.persist();
            }
        });

        Responses responses = new Responses();
        responses.getDatas().addAll(timeZones);
        return Response.ok(responses).status(200).build();
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"admin"})
    @Transactional
    public Response update(TimeZone timeZone) {
        timeZone.persist();
        Responses responses = new Responses();
        responses.setData(timeZone);
        return Response.ok(responses).status(200).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"admin"})
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        TimeZone timeZone = TimeZone.findById(id);
        timeZone.delete();
        Responses responses = new Responses();
        responses.setData(timeZone);
        return Response.ok(responses).status(200).build();
    }


}
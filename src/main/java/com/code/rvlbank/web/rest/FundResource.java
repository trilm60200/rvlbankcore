package com.code.rvlbank.web.rest;


import com.code.rvlbank.injection.InjectorProvider;
import com.code.rvlbank.models.Fund;
import com.code.rvlbank.services.IFundService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
public class FundResource {
    private final IFundService fundService;

    public FundResource() {
        fundService = InjectorProvider.provide().getInstance(IFundService.class);
    }

    @POST
    @Path("{accountRef}/deposit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(@PathParam("accountRef") String accoutRef, Fund fund) {
        fundService.deposit(accoutRef, fund);
        return Response.ok().status(200, "SUCCESS").build();
    }

    @POST
    @Path("{accountRef}/withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdraw(@PathParam("accountRef") String accoutRef, Fund fund) {
        fundService.withdraw(accoutRef, fund);
        return Response.ok().status(200, "SUCCESS").build();
    }

    @POST
    @Path("{fromAccount}/transfer/{toAccount}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(
            @PathParam("fromAccount") String fromId,
            @PathParam("toAccount") String toId,
            Fund fund) {
        fundService.transfer(fromId, toId, fund);
        return Response.ok().status(200, "SUCCESS").build();
    }


}
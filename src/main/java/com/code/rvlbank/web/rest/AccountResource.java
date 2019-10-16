package com.code.rvlbank.web.rest;

import com.code.rvlbank.injection.InjectorProvider;
import com.code.rvlbank.models.Account;
import com.code.rvlbank.services.IAccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
public class AccountResource {
    private final IAccountService accountService;

    public AccountResource() {
        accountService = InjectorProvider.provide().getInstance(IAccountService.class);
    }

    @GET
    @Path("{accountRef}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountRef") String accountRef) {
        return accountService.getAccount(accountRef);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(@QueryParam("accountRef") String accountRef, @QueryParam("currency") String currency) {
        if (currency != null) {
            return accountService.createAccount(accountRef, currency);
        } else
            return accountService.createAccount(accountRef);
    }

    @POST
    @Path("{accountRef}/reject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lockAccount(@PathParam("accountRef") String accountRef) {
        accountService.lockAccount(accountRef);
        return Response.ok().status(200, "SUCCESS").build();
    }

    @POST
    @Path("{accountRef}/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlockAccount(@PathParam("accountRef") String accountRef) {
        accountService.unlockAccount(accountRef);
        return Response.ok().status(200, "SUCCESS").build();
    }

    @DELETE
    @Path("{accountRef}/unbind")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unbindAccount(@PathParam("accountRef") String accountRef) {
        accountService.unbindAccount(accountRef);
        return Response.ok().status(200, "SUCCESS").build();
    }


}

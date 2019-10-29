package ke.co.narwassco.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import ke.co.narwassco.common.ServletListener;

/**
 * Login
 * @version 1.00
 * @author Igarashi
 */
@Path("/Login")
public class Login {
	private final Logger logger = Logger.getLogger(Login.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<Boolean> get(@QueryParam("Password") String password) {

		logger.info("get start.");
		try{
			Boolean res = false;
			if (ServletListener.adminpassword.equals(password)){
				res = true;
			}
			return new RestResult<Boolean>(res);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

}

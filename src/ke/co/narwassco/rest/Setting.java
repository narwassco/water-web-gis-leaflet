package ke.co.narwassco.rest;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import ke.co.narwassco.common.ServletListener;

/**
 * Setting
 * @version 1.00
 * @author Igarashi
 */
@Path("/Setting")
public class Setting {
	private final Logger logger = Logger.getLogger(Setting.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<HashMap<String, String>> get() {

		logger.info("get start.");
		try{
			HashMap<String, String> res = new HashMap<String, String>();
			res.put("MapServerUrl",ServletListener.MapServerUrl);
			res.put("bounds",ServletListener.bounds);
			return new RestResult<HashMap<String, String>>(res);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

}

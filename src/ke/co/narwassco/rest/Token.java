package ke.co.narwassco.rest;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ke.co.narwassco.rest.Accounts.Account;
import ke.co.narwassco.common.ServletListener;

/**
 * Login
 * @version 1.00
 * @author Igarashi
 */
@Path("/Token")
public class Token {
	private Logger logger = LogManager.getLogger(Token.class);

	@Path("/Login")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<Account> login(
			@QueryParam("id") String id,
			@QueryParam("password") String password) {

		logger.info("login start.");
		try{
			Accounts accounts = new Accounts();
			ArrayList<Account> list = accounts.get(id, password).getValue();
			if (list.size() == 0){
				return new RestResult<Account>(new Exception("Login ID or Password is not correct."));
			}
			Account account = list.get(0);
			String token = this.registerToken(account.accountid);
			account.setToken(token);

			return new RestResult<Account>(account);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return new RestResult<Account>(e);
		}
	}

	@Path("/Logout")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<Boolean> logout(
			@QueryParam("token") String token) {

		logger.info("logout start.");
		try{
			Boolean isDeleted = this.deleteToken(token);
			return new RestResult<Boolean>(isDeleted);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return new RestResult<Boolean>(e);
		}
	}

	private Boolean deleteToken(String token) throws SQLException, ClassNotFoundException{
		logger.info("deleteToken start.");
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE FROM token WHERE token = ?");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, token);
			return pstmt.execute();
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	private String registerToken(Integer accountid) throws SQLException, ClassNotFoundException{
		logger.info("registerToken start.");
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO token(accountid, token, expireddate) VALUES (?, ?, ?) ");
			sql.append(" ON CONFLICT ON CONSTRAINT token_pkey ");
			sql.append(" DO UPDATE SET token = ?, expireddate = ? ");

			//トークンの有効期限を設定
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, 1);
			Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
			String token = this.getCsrfToken();

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, accountid);
			pstmt.setString(2, token);
			pstmt.setTimestamp(3, timestamp);
			pstmt.setString(4, token);
			pstmt.setTimestamp(5, timestamp);
			pstmt.execute();

			return token;
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	private String getCsrfToken() {
	    byte token[] = new byte[16];
	    StringBuffer buf = new StringBuffer();
	    SecureRandom random = null;

	    try {
	      random = SecureRandom.getInstance("SHA1PRNG");
	      random.nextBytes(token);

	      for (int i = 0; i < token.length; i++) {
	        buf.append(String.format("%02x", token[i]));
	      }

	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }

	    return buf.toString();
	  }

}

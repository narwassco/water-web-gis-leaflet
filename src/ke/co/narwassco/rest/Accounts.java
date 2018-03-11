package ke.co.narwassco.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ke.co.narwassco.common.ServletListener;

@Path("/Accounts")
public class Accounts {
	private Logger logger = LogManager.getLogger(Accounts.class);

	public class Account{

		@XmlAttribute(name="accountid")
		public Integer accountid;

		@XmlAttribute(name="firstname")
		public String firstname;

		@XmlAttribute(name="lastname")
		public String lastname;

		@XmlAttribute(name="disignation")
		public String disignation;

		@XmlAttribute(name="loginid")
		public String loginid;

		@XmlAttribute(name="token")
		public String token = "";

		public Account(Integer accountid, String firstname, String lastname, String disignation, String loginid){
			this.accountid = accountid;
			this.firstname = firstname;
			this.lastname = lastname;
			this.disignation = disignation;
			this.loginid = loginid;
		}

		public void setToken(String token){
			this.token = token;
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<ArrayList<Account>> get(
			@QueryParam("loginid") String loginid,
			@QueryParam("password") String password) throws SQLException {

		logger.info("get start.");
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT accountid, firstname, lastname, disignation, loginid FROM account ");
			sql.append(" WHERE loginid=? and password=? ");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, loginid);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<Account> res = new ArrayList<Account>();
			while(rs.next()){
				Account account = new Account(rs.getInt("accountid"),rs.getString("firstname"),rs.getString("lastname"),rs.getString("disignation"), rs.getString("loginid"));
				res.add(account);
			}
			return new RestResult<ArrayList<Account>>(res);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return new RestResult<ArrayList<Account>>(e);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}
}

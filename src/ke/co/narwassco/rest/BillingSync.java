package ke.co.narwassco.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import ke.co.narwassco.common.ServletListener;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.handlers.StringArrayListHandler;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


/**
 * BillingSync
 * @version 1.00
 * @author Igarashi
 */
@Path("/BillingSync")
public class BillingSync {
	private final Logger logger = Logger.getLogger(BillingSync.class);

	/**
	 * Upload billing system csvfile to postgresql database
	 * @param file csv file
	 * @param fileDisposition
	 * @param yearmonth "yyyyMM" format
	 * @return Number of inserting records
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<Integer> upload(
			@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition fileDisposition,
			@FormDataParam("yearmonth") String yearmonth
			) {

		logger.info("upload start.");
		logger.debug("filename:" + fileDisposition.getName());
		logger.debug("yearmonth:" + yearmonth);

		try{
			Integer res = this.uploadBillingData(file, yearmonth);

			String latestYM = this.getLatestYearMonth();
			this.updateLatestCustomerData(latestYM);
			this.updateLatestMeterData(latestYM);

			return new RestResult<Integer>(res);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Path("/uploadAll")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<HashMap<String,Integer>> uploadAll() {

		logger.info("uploadAll start.");

		try{
			String directorypath = "D:\\documents\\05_Billing Data(Backup)\\for Jin\\";
			String[] filenames= {
					"2008-12",
					"2009-01","2009-02","2009-03","2009-04","2009-05","2009-06",
					"2009-07","2009-08","2009-09","2009-10","2009-11","2009-12",
					"2010-01","2010-02","2010-03","2010-04","2010-05","2010-06",
					"2010-07","2010-08","2010-09","2010-10","2010-11","2010-12",
					"2011-01","2011-02","2011-03","2011-04","2011-05","2011-06",
					"2011-07","2011-08","2011-09","2011-10","2011-11","2011-12",
					"2012-01","2012-02","2012-03","2012-04","2012-05","2012-06",
					"2012-07","2012-08","2012-09","2012-10","2012-11","2012-12",
					"2013-01","2013-02","2013-03","2013-04","2013-05","2013-06",
					"2013-07","2013-08","2013-09","2013-10","2013-11","2013-12",
					"2014-01","2014-02","2014-03","2014-04","2014-05","2014-06",
					"2014-07","2014-08","2014-09","2014-10","2014-11","2014-12",
					"2015-01","2015-02","2015-03","2015-04","2015-05","2015-06",
					"2015-07","2015-08"};

			ArrayList<HashMap<String,Integer>> res = new ArrayList<HashMap<String,Integer>>();
			for (String name : filenames){
				File file = new File(directorypath + name + ".csv");
				Integer iRes = this.uploadBillingData(new FileInputStream(file), name.replace("-", ""));
				HashMap<String,Integer> obj = new HashMap<String,Integer>();
				obj.put(file.getName(), iRes);
				res.add(obj);
			}

			return new ArrayList<HashMap<String,Integer>>(res);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	private Integer uploadBillingData(
			InputStream file,
			String yearmonth) throws SQLException{
		Connection conn = null;

		logger.debug("uploadBillingData start.");
		logger.debug("yearmonth:" + yearmonth);
		try{
			CsvConfig ccg = new CsvConfig();
			ccg.setQuoteDisabled(false);
			List<String[]> list = Csv.load(file, ccg, new StringArrayListHandler());

			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("INSERT INTO billing_bkup(");
			sql.append("yearmonth, ");
			sql.append("cat, ");
			sql.append("sno, ");
			sql.append("zone, ");
			sql.append("con, ");
			sql.append("names, ");
			sql.append("add, ");
			sql.append("town, ");
			sql.append("v, ");
			sql.append("vno, ");
			sql.append("met_no, ");
			sql.append("met_size, ");
			sql.append("mread1, ");
			sql.append("mread2, ");
			sql.append("status, ");
			sql.append("arr1, ");
			sql.append("cons, ");
			sql.append("cac, ");
			sql.append("wb, ");
			sql.append("rent, ");
			sql.append("tbill, ");
			sql.append("coll, ");
			sql.append("adj, ");
			sql.append("surcharge, ");
			sql.append("arr2, ");
			sql.append("date_inst, ");
			sql.append("inst_by, ");
			sql.append("mtrread, ");
			sql.append("comment, ");
			sql.append("lastadj, ");
			sql.append("lastrec, ");
			sql.append("lastscharge, ");
			sql.append("lastmread, ");
			sql.append("lastco, ");
			sql.append("not_due, ");
			sql.append("current, ");
			sql.append("month_1, ");
			sql.append("month_2, ");
			sql.append("month_3, ");
			sql.append("month_4, ");
			sql.append("month_5, ");
			sql.append("month_6, ");
			sql.append("lastedit, ");
			sql.append("edit_by, ");
			sql.append("edit_time, ");
			sql.append("allocate, ");
			sql.append("deposit, ");
			sql.append("labour, ");
			sql.append("reccon_fee, ");
			sql.append("misc_coll, ");
			sql.append("sewer, ");
			sql.append("sewer_bf, ");
			sql.append("sewer_fee, ");
			sql.append("sewer_col, ");
			sql.append("plot_no");
			sql.append(")VALUES (");
			sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			sql.append(")");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			conn.setAutoCommit(false);

			//Delete data of target Year/Month
			PreparedStatement pstmtDel = conn.prepareStatement("DELETE FROM billing_bkup WHERE yearmonth = ?");
			pstmtDel.setString(1, yearmonth);
			pstmtDel.execute();

			//Start inserting data of target Year/Month
			for (int i = 1; i < list.size();i++){
				String[] row = list.get(i);
				if (row.length == 1){
					continue;
				}
				pstmt.setString(1, yearmonth);
				for (int j = 0; j < row.length; j++){
					String value = row[j];
					if (value.isEmpty()){
						value = null;
					}
					pstmt.setString(j+2, value);
				}
				int res = pstmt.executeUpdate();
				if (res == 0){
					logger.debug(((DelegatingPreparedStatement)pstmt).getDelegate().toString());
					conn.rollback();
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
				}
			}
			conn.commit();
			return list.size();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			if (conn != null){
				conn.rollback();
			}
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	private String getLatestYearMonth() throws SQLException{
		logger.debug("getLatestYearMonth start.");
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql
				= new StringBuffer("SELECT yearmonth FROM billing_bkup GROUP BY yearmonth ORDER BY yearmonth DESC");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String> res = new ArrayList<String>();
			while(rs.next()){
				res.add(rs.getString(1));
			}
			return res.get(0);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	private Integer updateLatestCustomerData(String yearmonth) throws SQLException{
		logger.debug("updateLatestCustomerData start.");
		logger.debug("yearmonth:" + yearmonth);
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO customer ");
			sql.append(" SELECT ");
			sql.append(" nextval('customer_customerid_seq'),");
			sql.append(" zone,");
			sql.append(" CAST(con as Integer),");
			sql.append(" names,");
			sql.append(" CAST(vno as Integer),");
			sql.append(" current_date,");
			sql.append(" current_date,");
			sql.append(" status, ");
			sql.append(" sno, ");
			sql.append(" met_no ");
			sql.append(" FROM billing_bkup ");
			sql.append(" WHERE yearmonth = ?");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, yearmonth);

			conn.setAutoCommit(false);

			Statement stmt = conn.createStatement();
			stmt.execute("truncate table customer");
			stmt.execute("SELECT setval('customer_customerid_seq',1)");
			Integer res = pstmt.executeUpdate();
			conn.commit();
			return res;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			if (conn != null){
				conn.rollback();
			}
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	private Integer updateLatestMeterData(String yearmonth) throws SQLException{
		logger.debug("updateLatestMeterData start.");
		logger.debug("yearmonth:" + yearmonth);
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE meter m ");
			sql.append(" SET serialno = b.met_no ");
			sql.append(" ,installationdate = cast(b.date_inst as date) ");
			sql.append(" FROM billing_bkup b ");
			sql.append(" WHERE ");
			sql.append(" b.zone = m.zonecd ");
			sql.append(" AND cast(b.con as int) = m.connno");
			sql.append(" AND b.yearmonth = ? ");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, yearmonth);

			conn.setAutoCommit(false);
			Integer res = pstmt.executeUpdate();
			conn.commit();
			return res;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			if (conn != null){
				conn.rollback();
			}
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

}

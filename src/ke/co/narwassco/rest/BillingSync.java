package ke.co.narwassco.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import ke.co.narwassco.common.ServletListener;
import ke.co.narwassco.pdf.MyFooter;
import net.arnx.jsonic.JSON;

import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
			return new RestResult<Integer>(RestResult.error,e.getMessage());
		}
	}

	@Path("/uploadAll")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<HashMap<String,Integer>> uploadAll() {

		logger.info("uploadAll start.");

		try{
			String directorypath = "D:\\documents\\05_Billing Data\\02_CSV Data\\Consumption Data";
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
					"2015-07","2015-08","2015-09","2015-10","2015-11","2015-12",
					"2016-01","2016-02","2016-03","2016-04","2016-05","2016-06",
					"2016-07"};

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
	
	@SuppressWarnings("unchecked")
	@Path("/ConsumptionReport")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> downloadConsumptionReport(
			@QueryParam("yearmonth") String yearmonth
			) throws SQLException  {
		logger.info("downloadConsumptionReport start.");
		logger.debug("yearmonth:" + yearmonth);
		
		Connection conn = null;
		Document document = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT ");
			sql.append("   cast(b.vno as integer) as villageid, ");
			sql.append("   v.name as villagename, ");
			sql.append("   v.area, ");
			sql.append("   b.status, ");
			sql.append("   CASE WHEN b.status = 'ON' THEN '1:ON' WHEN b.status = 'AVG' THEN '2:AVG' WHEN b.status = 'CO' THEN '3:CO' ELSE '' END as statusfororder, ");
			sql.append("   count(b.*) as NoOfConn,");
			sql.append("   sum(cast(b.cons as integer)) as consumption");
			sql.append(" FROM billing_bkup b");
			sql.append(" INNER JOIN village v");
			sql.append(" ON cast(b.vno as integer) = v.villageid");
			sql.append(" WHERE b.yearmonth=?");
			sql.append(" GROUP BY b.vno, v.name, v.area, b.status,statusfororder");
			sql.append(" ORDER BY b.VNO, statusfororder");
			
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, yearmonth);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd= rs.getMetaData();
			ArrayList<HashMap<String,Object>> resData = new ArrayList<HashMap<String,Object>>();
			while(rs.next()){
				HashMap<String,Object> data = new HashMap<String,Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colname = rsmd.getColumnName(i);
					data.put(colname, rs.getObject(colname));
				}
				resData.add(data);
			}
			
			HashMap<String, ArrayList<HashMap<String,HashMap<String, Object>>>> dataByTown = new HashMap<String, ArrayList<HashMap<String,HashMap<String, Object>>>>();
			for (int i = 0; i < resData.size(); i++){
				HashMap<String,Object> data = resData.get(i);
				if (java.util.Objects.isNull(data.get("status"))){
					continue;
				}
				String area = data.get("area").toString();
				ArrayList<HashMap<String,HashMap<String, Object>>> dataList = new ArrayList<HashMap<String,HashMap<String, Object>>>();
				if (dataByTown.containsKey(area)){
					dataList = dataByTown.get(area);
				}
				HashMap<String, Object> villagedata = null;
				for (HashMap<String,HashMap<String, Object>> _villageList : dataList){
					String _villageid = data.get("villageid").toString();
					if (!java.util.Objects.isNull(_villageList.get(_villageid))){
						villagedata = _villageList.get(_villageid);
					}
				}
				HashMap<String,HashMap<String, Object>> villageList = new HashMap<String,HashMap<String, Object>>();
				if (villagedata == null){
					villagedata = new HashMap<String, Object>();
					villagedata.put("villageid", data.get("villageid").toString());
					villagedata.put("villagename", data.get("villagename").toString());
					villageList.put(data.get("villageid").toString(), villagedata);
					dataList.add(villageList);
				}
				HashMap<String,String> statusdata = new HashMap<String,String>();
				String currentStatus = data.get("status").toString();
				
				statusdata.put("noofconn", data.get("noofconn").toString());
				statusdata.put("consumption", data.get("consumption").toString());
				villagedata.put(currentStatus, statusdata);
				dataByTown.put(area, dataList);
			}
			
			// (3)文書の出力を開始
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf_filename = new SimpleDateFormat("yyyy-MM-dd");
            String pdf_name = sdf_filename.format(cal.getTime()) ;

			String filename = pdf_name + "_MonthlyConsumptionReport.pdf";
			document = new Document(PageSize.A4, 0,0 , 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ServletListener.downloadexportpath + "\\" + filename));

			document.open();
            BaseFont bf = BaseFont.createFont();
            Font fData = new Font(bf, 10);
            
            SimpleDateFormat sdf_normaldate = new SimpleDateFormat("dd/MM/yyyy");
            String pdf_date = sdf_normaldate.format(cal.getTime()) ;

            MyFooter event = new MyFooter("Printed:" + pdf_date,"Monthly Consumption Report by Villages (" + yearmonth + ")","",
            		"","","(C) 2016 Narok Water and Sewerage Services Co., Ltd.",
            		true);
            writer.setPageEvent(event);
			
            int widths[] = {60,200,60,150,150};
			
            String[] towns = {"Narok","Ololulunga","Kilgoris"};
            Integer iRowCount = 0;
            for (String town : towns){
            	PdfPTable t = new PdfPTable(5);
                t.setHorizontalAlignment(Element.ALIGN_CENTER);
                t.setWidths(widths);
                
            	PdfPCell cHeaderTown = new PdfPCell(new Paragraph(town,fData));
            	cHeaderTown.setColspan(5);
                t.addCell(cHeaderTown);
                
                if (iRowCount == 0){
                	PdfPCell cHeader1 = new PdfPCell(new Paragraph("NO",fData));
                    PdfPCell cHeader2 = new PdfPCell(new Paragraph("VILLAGE",fData));
                    PdfPCell cHeader3 = new PdfPCell(new Paragraph("STATUS",fData));
                    PdfPCell cHeader4 = new PdfPCell(new Paragraph("NO OF METERS",fData));
                    PdfPCell cHeader5 = new PdfPCell(new Paragraph("CONSUMPTION(m3)",fData));
                    cHeader1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cHeader2.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cHeader3.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cHeader4.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cHeader5.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    t.addCell(cHeader1);
                    t.addCell(cHeader2);
                    t.addCell(cHeader3);
                    t.addCell(cHeader4);
                    t.addCell(cHeader5);
                }
                
                ArrayList<HashMap<String,HashMap<String,Object>>> dataList = dataByTown.get(town);
                HashMap<String,HashMap<String,Integer>> global_status_data = new HashMap<String,HashMap<String,Integer>>();
                for (HashMap<String,HashMap<String,Object>> data : dataList){
                	if (iRowCount == 12){
                    	iRowCount = 0;
                    	document.newPage();
                        document.add(t);
                        t = new PdfPTable(5);
                        t.setHorizontalAlignment(Element.ALIGN_CENTER);
                        t.setWidths(widths);
                    }
                	
                	HashMap<String,Object> village = (HashMap<String, Object>) data.values().toArray()[0];
                	
                	PdfPCell cData1 = new PdfPCell(new Paragraph(village.get("villageid").toString(),fData));
                    PdfPCell cData2 = new PdfPCell(new Paragraph(village.get("villagename").toString(),fData));
                    cData1.setRowspan(4);
                    cData2.setRowspan(4);
                    cData1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    t.addCell(cData1);
                    t.addCell(cData2);
                    
                    String[] statusList = {"ON","AVG","CO","TOTAL"};
                    Integer sum_nometers = 0;
                    Integer sum_cons = 0;
                    for (String status : statusList){
                    	HashMap<String,String> statusdata = null;
                    	if (village.containsKey(status)){
                    		statusdata = (HashMap<String, String>) village.get(status);
                    	}else{
                    		statusdata = new HashMap<String,String>();
                    		statusdata.put("status", status);
                    		statusdata.put("noofconn", "0");
                    		statusdata.put("consumption", "0");
                    	}
                    	String noofconn = "";
                    	String cons = "";
                    	if (!status.equals("TOTAL")){
                    		noofconn = statusdata.get("noofconn");
                        	cons = statusdata.get("consumption");
                        	sum_nometers += Integer.valueOf(noofconn);
                        	sum_cons += Integer.valueOf(cons);
                    	}else{
                    		noofconn = String.valueOf(sum_nometers);
                        	cons = String.valueOf(sum_cons);
                    	}
                    	HashMap<String, Integer> global_sum = null;
                    	if (global_status_data.containsKey(status)){
                    		global_sum = global_status_data.get(status);
                    	}else{
                    		global_sum = new HashMap<String, Integer>();
                    		global_sum.put("noofconn", 0);
                        	global_sum.put("cons", 0);
                    		global_status_data.put(status, global_sum);
                    	}
                    	global_sum.put("noofconn", global_sum.get("noofconn") + Integer.valueOf(noofconn));
                    	global_sum.put("cons", global_sum.get("cons") + Integer.valueOf(cons));
                    	
                    	PdfPCell cData3 = new PdfPCell(new Paragraph(status,fData));
                        PdfPCell cData4 = new PdfPCell(new Paragraph(noofconn,fData));
                        PdfPCell cData5 = new PdfPCell(new Paragraph(cons,fData));
                        cData3.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                        cData4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cData5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        t.addCell(cData3);
                        t.addCell(cData4);
                        t.addCell(cData5);
                    }
                    
                    if (dataList.get(dataList.size() - 1).equals(data)){
                    	PdfPCell cTotalData1 = new PdfPCell(new Paragraph(town + "Global",fData));
                    	cTotalData1.setRowspan(4);
                    	cTotalData1.setColspan(2);
                        t.addCell(cTotalData1);
                        for (String status : statusList){
                        	HashMap<String,Integer> global_sum_status = global_status_data.get(status);
                        	PdfPCell cTotalData3 = new PdfPCell(new Paragraph(status,fData));
                            PdfPCell cTotalData4 = new PdfPCell(new Paragraph(global_sum_status.get("noofconn").toString(),fData));
                            PdfPCell cTotalData5 = new PdfPCell(new Paragraph(global_sum_status.get("cons").toString(),fData));
                            cTotalData3.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                            cTotalData4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cTotalData5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            t.addCell(cTotalData3);
                            t.addCell(cTotalData4);
                            t.addCell(cTotalData5);
                        }
                        iRowCount++;
                    }
                    iRowCount++;
                }
                
                iRowCount = 0;
                document.newPage();
                document.add(t);
            }
            document.close();

			String url = "." + ServletListener.downloadurlpath + "/" + filename;
			return new RestResult<String>(url);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return new RestResult<String>(RestResult.error,e.getMessage());
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	@Path("/Statement")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> downloadStatement(
			@QueryParam("zone") String zone,
			@QueryParam("connectionno") String connectionno
			) throws SQLException{
		logger.info("downloadStatement start.");
		logger.debug("zone:" + zone);
		logger.debug("connectionno:" + connectionno);
		
		Connection conn = null;
		Document document = null;
		
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT");
			sql.append(" b.sno");
			sql.append(",b.zone");
			sql.append(",b.con");
			sql.append(",b.names");
			sql.append(",b.add");
			sql.append(",b.town");
			sql.append(",b.met_no");
			sql.append(",b.met_size");
			sql.append(",b.yearmonth");
			sql.append(",b.status");
			sql.append(",b.mread2");
			sql.append(",b.cons");
			sql.append(",b.wb");
			sql.append(",b.coll");
			sql.append(",b.adj");
			sql.append(",b.arr2");
			sql.append(" FROM billing_bkup b");
			sql.append(" WHERE b.zone= ? and b.con= ?");
			sql.append(" ORDER BY b.yearmonth DESC");
			//sql.append(" LIMIT 12 OFFSET 0");
			
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, zone.trim());
			pstmt.setString(2, connectionno.trim());
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd= rs.getMetaData();
			ArrayList<HashMap<String,Object>> resData = new ArrayList<HashMap<String,Object>>();
			while(rs.next()){
				HashMap<String,Object> data = new HashMap<String,Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colname = rsmd.getColumnName(i);
					data.put(colname, rs.getObject(colname));
				}
				resData.add(data);
			}
			
			// (3)文書の出力を開始
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf_filename = new SimpleDateFormat("yyyy-MM-dd");
            String pdf_name = sdf_filename.format(cal.getTime()) ;
            
			String filename = pdf_name + "_Statement.pdf";
			document = new Document(PageSize.A4, 0,0 , 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ServletListener.downloadexportpath + "\\" + filename));

			document.open();
            BaseFont bf = BaseFont.createFont();
            Font fData = new Font(bf, 10);
            
            SimpleDateFormat sdf_normaldate = new SimpleDateFormat("dd/MM/yyyy");
            String pdf_date = sdf_normaldate.format(cal.getTime()) ;

            MyFooter event = new MyFooter("Printed:" + pdf_date,"Ledger Reports","",
            		"","","(C) 2016 Narok Water and Sewerage Services Co., Ltd.",
            		true);
            writer.setPageEvent(event);
						
			PdfPTable t = new PdfPTable(8);
            t.setHorizontalAlignment(Element.ALIGN_CENTER);
            int widths[] = {150,80,150,150,150,150,150,150};
            t.setWidths(widths);
            
            Integer iRowCount = 0;
            for (int i = resData.size() - 1; i >= 0;i--){
            	if (iRowCount == 0){
            		HashMap<String,Object> latestdata = resData.get(0);
                    PdfPCell cTitle1 = new PdfPCell(new Paragraph("Serial No.",fData));
                    PdfPCell cTitle2 = new PdfPCell(new Paragraph(latestdata.get("sno").toString(),fData));
                    PdfPCell cTitle3 = new PdfPCell(new Paragraph("Name",fData));
                    PdfPCell cTitle4 = new PdfPCell(new Paragraph(latestdata.get("names").toString(),fData));
                    cTitle4.setColspan(5);
                    t.addCell(cTitle1);
                    t.addCell(cTitle2);
                    t.addCell(cTitle3);
                    t.addCell(cTitle4);
                    
                    PdfPCell cTitle5 = new PdfPCell(new Paragraph("Zone",fData));
                    PdfPCell cTitle6 = new PdfPCell(new Paragraph(latestdata.get("zone").toString(),fData));
                    PdfPCell cTitle7 = new PdfPCell(new Paragraph("Connection No.",fData));
                    PdfPCell cTitle8 = new PdfPCell(new Paragraph(latestdata.get("con").toString(),fData));
                    cTitle6.setColspan(3);
                    cTitle7.setColspan(2);
                    cTitle8.setColspan(2);
                    t.addCell(cTitle5);
                    t.addCell(cTitle6);
                    t.addCell(cTitle7);
                    t.addCell(cTitle8);
                    
                    PdfPCell cTitle9 = new PdfPCell(new Paragraph("Address",fData));
                    PdfPCell cTitle10 = new PdfPCell(new Paragraph(java.util.Objects.toString(latestdata.get("add"), "") + " " + java.util.Objects.toString(latestdata.get("town"), ""),fData));
                    PdfPCell cTitle11 = new PdfPCell(new Paragraph("Meter No.",fData));
                    PdfPCell cTitle12 = new PdfPCell(new Paragraph(java.util.Objects.toString(latestdata.get("met_no"), ""),fData));
                    PdfPCell cTitle13 = new PdfPCell(new Paragraph("Size",fData));
                    PdfPCell cTitle14 = new PdfPCell(new Paragraph(java.util.Objects.toString(latestdata.get("met_size"), ""),fData));
                    cTitle10.setColspan(2);
                    cTitle12.setColspan(2);
                    t.addCell(cTitle9);
                    t.addCell(cTitle10);
                    t.addCell(cTitle11);
                    t.addCell(cTitle12);
                    t.addCell(cTitle13);
                    t.addCell(cTitle14);
                    
                    PdfPCell cHeader1 = new PdfPCell(new Paragraph("Month",fData));
                    PdfPCell cHeader2 = new PdfPCell(new Paragraph("Stat",fData));
                    PdfPCell cHeader3 = new PdfPCell(new Paragraph("Reading",fData));
                    PdfPCell cHeader4 = new PdfPCell(new Paragraph("Cons",fData));
                    PdfPCell cHeader5 = new PdfPCell(new Paragraph("Billed",fData));
                    PdfPCell cHeader6 = new PdfPCell(new Paragraph("Receipts",fData));
                    PdfPCell cHeader7 = new PdfPCell(new Paragraph("Adj.",fData));
                    PdfPCell cHeader8 = new PdfPCell(new Paragraph("Arrears",fData));
                    t.addCell(cHeader1);
                    t.addCell(cHeader2);
                    t.addCell(cHeader3);
                    t.addCell(cHeader4);
                    t.addCell(cHeader5);
                    t.addCell(cHeader6);
                    t.addCell(cHeader7);
                    t.addCell(cHeader8);
            	}
            	HashMap<String,Object> data = resData.get(i);
            	SimpleDateFormat sdf_ym = new SimpleDateFormat("yyyyMM");
            	Date ym = sdf_ym.parse(data.get("yearmonth").toString());
            	SimpleDateFormat sdf_ymafter = new SimpleDateFormat("MMM yyyy");
            	
            	PdfPCell cData1 = new PdfPCell(new Paragraph(sdf_ymafter.format(ym),fData));
                PdfPCell cData2 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("status"),""),fData));
                PdfPCell cData3 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("mread2"),""),fData));
                PdfPCell cData4 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("cons"),""),fData));
                PdfPCell cData5 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("wb"),""),fData));
                PdfPCell cData6 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("coll"),""),fData));
                PdfPCell cData7 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("adj"),""),fData));
                PdfPCell cData8 = new PdfPCell(new Paragraph(java.util.Objects.toString(data.get("arr2"),""),fData));
                cData3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cData4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cData5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cData6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cData7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cData8.setHorizontalAlignment(Element.ALIGN_RIGHT);
                t.addCell(cData1);
                t.addCell(cData2);
                t.addCell(cData3);
                t.addCell(cData4);
                t.addCell(cData5);
                t.addCell(cData6);
                t.addCell(cData7);
                t.addCell(cData8);

                iRowCount++;
                if (iRowCount==49){
                	iRowCount = 0;
                }
            }
            document.add(t);
            document.close();

			String url = "." + ServletListener.downloadurlpath + "/" + filename;
			return new RestResult<String>(url);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return new RestResult<String>(RestResult.error,e.getMessage());
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	@Path("/AdjustmentReport")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> downloadAdjustmentReport(
			@QueryParam("fromym") String fromym,
			@QueryParam("toym") String toym
			) throws SQLException  {
		logger.info("downloadAdjustmentReport start.");
		logger.debug("fromym:" + fromym);
		logger.debug("toym:" + toym);
		
		Connection conn = null;
		Document document = null;
		
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT ");
			sql.append(" b.yearmonth ");
			sql.append(" ,b.zone ");
			sql.append(" ,b.con ");
			sql.append(" ,b.names ");
			sql.append(" ,v.name as villagename");
			sql.append(" ,b.adj ");
			sql.append(" ,b.lastadj ");
			sql.append(" FROM billing_bkup b ");
			sql.append(" INNER JOIN village v ");
			sql.append(" ON cast(vno as integer) = v.villageid ");
			sql.append(" WHERE ");
			sql.append(" b.yearmonth>=? and b.yearmonth<=? ");
			sql.append(" and ");
			sql.append(" cast(b.adj as float)>0 ");
			sql.append(" ORDER BY b.yearmonth,b.zone,b.con ");
			
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, fromym.trim());
			pstmt.setString(2, toym.trim());
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd= rs.getMetaData();
			ArrayList<HashMap<String,Object>> resData = new ArrayList<HashMap<String,Object>>();
			while(rs.next()){
				HashMap<String,Object> data = new HashMap<String,Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colname = rsmd.getColumnName(i);
					data.put(colname, rs.getObject(colname));
				}
				resData.add(data);
			}
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf_filename = new SimpleDateFormat("yyyy-MM-dd");
            String pdf_name = sdf_filename.format(cal.getTime()) ;

			String filename = pdf_name + "_AdjustmentReport.pdf";
			document = new Document(PageSize.A4, 0,0 , 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ServletListener.downloadexportpath + "\\" + filename));

			// (3)文書の出力を開始
            document.open();
            BaseFont bf = BaseFont.createFont();
            Font fData = new Font(bf, 10);
            Font fName = new Font(bf,7);

            SimpleDateFormat sdf_normaldate = new SimpleDateFormat("dd/MM/yyyy");
            String pdf_date = sdf_normaldate.format(cal.getTime()) ;

            MyFooter event = new MyFooter("Printed:" + pdf_date,"ADJUSTMENT REPORT(" + fromym + " - " + toym + ")","",
            		"","","(C) 2016 Narok Water and Sewerage Services Co., Ltd.",
            		true);
            writer.setPageEvent(event);

            PdfPTable t = new PdfPTable(6);
            t.setHorizontalAlignment(Element.ALIGN_CENTER);
            int widths[] = {100,50,50,230,90,80};
            t.setWidths(widths);
			
            Integer iRowCount = 0;
            for (int i = 0; i < resData.size(); i++){
            	HashMap<String,Object> obj = resData.get(i);
                if (iRowCount == 0){
                    PdfPCell cHeader1 = new PdfPCell(new Paragraph("VILLAGE",fData));
                    PdfPCell cHeader2 = new PdfPCell(new Paragraph("ZONE",fData));
                    PdfPCell cHeader3 = new PdfPCell(new Paragraph("CON",fData));
                    PdfPCell cHeader4 = new PdfPCell(new Paragraph("NAMES",fData));
                    PdfPCell cHeader5 = new PdfPCell(new Paragraph("ADJUSTMENT",fData));
                    PdfPCell cHeader6 = new PdfPCell(new Paragraph("DATE",fData));
                    t.addCell(cHeader1);
                    t.addCell(cHeader2);
                    t.addCell(cHeader3);
                    t.addCell(cHeader4);
                    t.addCell(cHeader5);
                    t.addCell(cHeader6);
            	}

                PdfPCell cData1 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("villagename"),""),fName));
                PdfPCell cData2 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("zone"),""),fData));
                PdfPCell cData3 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("con"),""),fData));
                PdfPCell cData4 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("names"),""),fName));
                PdfPCell cData5 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("adj"),""),fData));
                PdfPCell cData6 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("lastadj"),""),fData));
                t.addCell(cData1);
                t.addCell(cData2);
                t.addCell(cData3);
                t.addCell(cData4);
                t.addCell(cData5);
                t.addCell(cData6);

                if (iRowCount == 50){
                	iRowCount = 0;

                }else{
                	iRowCount++;
                }
            }
            document.add(t);
            document.close();
			
			String url = "." + ServletListener.downloadurlpath + "/" + filename;
			return new RestResult<String>(url);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return new RestResult<String>(RestResult.error,e.getMessage());
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}
	
}

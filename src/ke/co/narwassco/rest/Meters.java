package ke.co.narwassco.rest;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.arnx.jsonic.JSON;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ke.co.narwassco.common.ServletListener;
import ke.co.narwassco.pdf.MyFooter;

/**
 * <pre>
 *  クラス名  ：Meters
 *  クラス説明：
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
@Path("/Meters")
public class Meters {
	private final Logger logger = Logger.getLogger(Meters.class);

	@Path("/VillageChange")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> downloadDifferentVillage(
			@QueryParam("zonecd") String zonecd
			) throws SQLException {

		logger.info("downloadDifferentVillage start.");
		logger.debug("zonecd:" + zonecd);
		Connection conn = null;
		Document document = null;
		try{
			ArrayList<String> zonecdarray = JSON.decode(zonecd);
			String zonecdparam = "";
			for (int i = 0; i < zonecdarray.size(); i++){
				if (i > 0){
					zonecdparam += ",";
				}
				String cd = zonecdarray.get(i);
				zonecdparam += "'" + cd + "'";
			}

			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT ");
			sql.append(" m.zonecd ");
			sql.append(" ,to_char(m.connno, '0000') as connno ");
			sql.append(" ,c.name ");
			sql.append(" ,to_char(c.villageid,'00') as beforevno ");
			sql.append(" ,to_char(v.villageid,'00') as aftervno ");
			sql.append(" ,v.name as villagename ");
			sql.append(" FROM village v,meter m ");
			sql.append(" INNER JOIN customer c ");
			sql.append(" on m.zonecd = c.zonecd ");
			sql.append(" and m.connno = c.connno ");
			sql.append(" WHERE ");
			sql.append(" ST_Intersects(m.geom,v.geom) = true ");
			sql.append(" and c.villageid <> v.villageid ");
			sql.append(" AND m.zonecd in (" + zonecdparam + ") ");
			sql.append(" ORDER BY zonecd,beforevno,connno ");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
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

			String filename = pdf_name + "_DifferentVillageMeterList.pdf";
			document = new Document(PageSize.A4, 0,0 , 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ServletListener.downloadexportpath + "\\" + filename));

			// (3)文書の出力を開始
            document.open();
            BaseFont bf = BaseFont.createFont();
            Font fData = new Font(bf, 10);
            Font fName = new Font(bf,7);

            SimpleDateFormat sdf_normaldate = new SimpleDateFormat("dd/MM/yyyy");
            String pdf_date = sdf_normaldate.format(cal.getTime()) ;

            MyFooter event = new MyFooter("Printed:" + pdf_date,"METER LIST for Changing Villages","",
            		"","","(C) 2016 Narok Water and Sewerage Services Co., Ltd.",
            		true);
            writer.setPageEvent(event);

            PdfPTable t = new PdfPTable(6);
            t.setHorizontalAlignment(Element.ALIGN_CENTER);
            int widths[] = {50,50,210,60,60,150};
            t.setWidths(widths);

            Integer iRowCount = 0;
            for (int i = 0; i < resData.size(); i++){
            	HashMap<String,Object> obj = resData.get(i);
                if (iRowCount == 0){
                    PdfPCell cHeader1 = new PdfPCell(new Paragraph("ZONE",fData));
                    PdfPCell cHeader2 = new PdfPCell(new Paragraph("CON",fData));
                    PdfPCell cHeader3 = new PdfPCell(new Paragraph("NAME",fData));
                    PdfPCell cHeader4 = new PdfPCell(new Paragraph("BEFORE VNO",fData));
                    PdfPCell cHeader5 = new PdfPCell(new Paragraph("AFTER VNO",fData));
                    PdfPCell cHeader6 = new PdfPCell(new Paragraph("VILLAGE",fData));
                    t.addCell(cHeader1);
                    t.addCell(cHeader2);
                    t.addCell(cHeader3);
                    t.addCell(cHeader4);
                    t.addCell(cHeader5);
                    t.addCell(cHeader6);
            	}

                PdfPCell cData1 = new PdfPCell(new Paragraph(obj.get("zonecd").toString(),fData));
                PdfPCell cData2 = new PdfPCell(new Paragraph(obj.get("connno").toString(),fData));
                PdfPCell cData3 = new PdfPCell(new Paragraph(obj.get("name").toString(),fName));
                PdfPCell cData4 = new PdfPCell(new Paragraph(obj.get("beforevno").toString(),fData));
                PdfPCell cData5 = new PdfPCell(new Paragraph(obj.get("aftervno").toString(),fData));
                PdfPCell cData6 = new PdfPCell(new Paragraph(obj.get("villagename").toString(),fName));
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
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}

	}

	/**
	 * Download meter list par village which are not captured by GPS
	 * @param zonecd put zonecd which you want as JSON String Array format. ex)['A','B']
	 * @return url of csvfile
	 * @throws SQLException
	 */
	@Path("/Uncaptured")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> downloadUncaptured(
			@QueryParam("zonecd") String zonecd
			) throws SQLException {

		logger.info("downloadUncaptured start.");
		logger.debug("zonecd:" + zonecd);
		Connection conn = null;
		Document document = null;
		try{
			ArrayList<String> zonecdarray = JSON.decode(zonecd);
			String zonecdparam = "";
			for (int i = 0; i < zonecdarray.size(); i++){
				if (i > 0){
					zonecdparam += ",";
				}
				String cd = zonecdarray.get(i);
				zonecdparam += "'" + cd + "'";
			}

			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ");
			sql.append("  to_char(x.villageid,'00') as VillageID, ");
			sql.append("  z.name as VillageName,");
			sql.append("  x.zonecd as Zone, ");
			sql.append("  to_char(x.connno,'0000') as Con, ");
			sql.append("  x.name as Name,  ");
			sql.append("  x.serialno as SerialNo,  ");
			sql.append("  x.status as Status  ");
			sql.append("FROM  customer x ");
			sql.append("LEFT JOIN meter y ");
			sql.append("ON x.zonecd = y.zonecd ");
			sql.append("AND x.connno = y.connno ");
			sql.append("INNER JOIN village z ");
			sql.append("ON x.villageid = z.villageid ");
			sql.append("WHERE y.geom is null ");
			sql.append("AND x.zonecd in (" + zonecdparam + ") ");
			sql.append("ORDER BY ");
			sql.append("   x.villageid, ");
			sql.append("   x.zonecd, ");
			sql.append("   x.connno ");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
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

			String filename = pdf_name + "_UncapturedMeterList.pdf";
			document = new Document(PageSize.A4, 0,0 , 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ServletListener.downloadexportpath + "\\" + filename));

			// (3)文書の出力を開始
            document.open();
            BaseFont bf = BaseFont.createFont();
            Font fData = new Font(bf, 10);
            Font fName = new Font(bf,7);

            SimpleDateFormat sdf_normaldate = new SimpleDateFormat("dd/MM/yyyy");
            String pdf_date = sdf_normaldate.format(cal.getTime()) ;

            MyFooter event = new MyFooter("Printed:" + pdf_date,"GPS UNCAPTURED METER LIST","",
            		"","","(C) 2016 Narok Water and Sewerage Services Co., Ltd.",
            		true);
            writer.setPageEvent(event);

            PdfPTable t = new PdfPTable(6);
            t.setHorizontalAlignment(Element.ALIGN_CENTER);
            int widths[] = {150,50,50,210,70,70};
            t.setWidths(widths);

            Integer iRowCount = 0;
            for (int i = 0; i < resData.size(); i++){
            	HashMap<String,Object> obj = resData.get(i);
                if (iRowCount == 0){
                    PdfPCell cHeader1 = new PdfPCell(new Paragraph("VILLAGE",fData));
                    PdfPCell cHeader2 = new PdfPCell(new Paragraph("ZONE",fData));
                    PdfPCell cHeader3 = new PdfPCell(new Paragraph("CON",fData));
                    PdfPCell cHeader4 = new PdfPCell(new Paragraph("NAMES",fData));
                    PdfPCell cHeader5 = new PdfPCell(new Paragraph("S/N",fData));
                    PdfPCell cHeader6 = new PdfPCell(new Paragraph("STATUS",fData));
                    t.addCell(cHeader1);
                    t.addCell(cHeader2);
                    t.addCell(cHeader3);
                    t.addCell(cHeader4);
                    t.addCell(cHeader5);
                    t.addCell(cHeader6);
            	}

                String _village = obj.get("villageid").toString() + ":" + obj.get("villagename").toString();

                PdfPCell cData1 = new PdfPCell(new Paragraph(_village,fName));
                PdfPCell cData2 = new PdfPCell(new Paragraph(obj.get("zone").toString(),fData));
                PdfPCell cData3 = new PdfPCell(new Paragraph(obj.get("con").toString(),fData));
                PdfPCell cData4 = new PdfPCell(new Paragraph(obj.get("name").toString(),fName));
                PdfPCell cData5 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("serialno"),""),fName));
                PdfPCell cData6 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("status"), ""),fData));
                t.addCell(cData1);
                t.addCell(cData2);
                t.addCell(cData3);
                t.addCell(cData4);
                t.addCell(cData5);
                t.addCell(cData6);

                if (iRowCount == 51){
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
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	/**
	 * Download meter reading sheets par village
	 * @param villageid put villageid which you want as JSON String Array format. ex)['1','2']
	 * @return url of csvfile
	 * @throws SQLException
	 */
	@Path("/MReading")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> downloadMeterReading(
			@QueryParam("villageid") String villageid
			) throws SQLException {

		logger.info("downloadMeterReading start.");
		logger.debug("villageid:" + villageid);
		Connection conn = null;

		Document document = null;

		try{
			ArrayList<String> villageidarray = JSON.decode(villageid);
			String villageidparam = "";
			for (int i = 0; i < villageidarray.size(); i++){
				if (i > 0){
					villageidparam += ",";
				}
				String cd = villageidarray.get(i);
				villageidparam += "'" + cd + "'";
			}

			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ");
			sql.append("  to_char(x.villageid,'00') as VillageID, ");
			sql.append("  z.name as VillageName,");
			sql.append("  x.sno as SNO, ");
			sql.append("  x.zonecd as Zone, ");
			sql.append("  to_char(x.connno,'0000') as Con, ");
			sql.append("  x.name as Name,  ");
			sql.append("  x.serialno as SerialNo,  ");
			sql.append("  x.status as Status  ");
			sql.append("FROM  customer x ");
			sql.append("LEFT JOIN meter y ");
			sql.append("ON x.zonecd = y.zonecd ");
			sql.append("AND x.connno = y.connno ");
			sql.append("INNER JOIN village z ");
			sql.append("ON x.villageid = z.villageid ");
			sql.append("WHERE ");
			sql.append(" x.villageid in (" + villageidparam + ") ");
			sql.append("ORDER BY ");
			sql.append("   x.villageid, ");
			sql.append("   x.zonecd, ");
			sql.append("   x.connno ");

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd= rs.getMetaData();
			ArrayList<ArrayList<HashMap<String,Object>>> resDataList = new ArrayList<ArrayList<HashMap<String,Object>>>();
			ArrayList<HashMap<String,Object>> resData = null;
			String lastVillageId = "";
			while(rs.next()){
				HashMap<String,Object> data = new HashMap<String,Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colname = rsmd.getColumnName(i);
					data.put(colname, rs.getObject(colname));
				}

				if (rs.getObject("VillageId").equals("") || rs.getObject("VillageId").equals(lastVillageId)){
					if (resData == null){
						resData = new ArrayList<HashMap<String,Object>>();
					}
					resData.add(data);
				}else{
					resData = new ArrayList<HashMap<String,Object>>();
					resData.add(data);
					resDataList.add(resData);
				}
				lastVillageId = rs.getObject("VillageId").toString();
			}

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf_filename = new SimpleDateFormat("yyyy-MM-dd");
            String pdf_name = sdf_filename.format(cal.getTime()) ;

			String filename = pdf_name + "_Meter Reading Sheet.pdf";
			document = new Document(PageSize.A4, 0,0 , 50, 50);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(ServletListener.downloadexportpath + "\\" + filename));

            // (3)文書の出力を開始
            document.open();
            BaseFont bf = BaseFont.createFont();
            Font fData = new Font(bf, 10);
            Font fName = new Font(bf,7);

            SimpleDateFormat sdf_normaldate = new SimpleDateFormat("dd/MM/yyyy");
            String pdf_date = sdf_normaldate.format(cal.getTime()) ;

            MyFooter event = new MyFooter("Printed:" + pdf_date,"METER READING SHEET","METER READER:_______________",
            		"","","(C) 2016 Narok Water and Sewerage Services Co., Ltd.",
            		true);
            writer.setPageEvent(event);

            for (int i = 0; i < resDataList.size(); i++){
            	PdfPTable t = new PdfPTable(7);
                t.setHorizontalAlignment(Element.ALIGN_CENTER);
                int widths[] = {50,40,40,250,70,80,70};
                t.setWidths(widths);

                ArrayList<HashMap<String,Object>> array = resDataList.get(i);
                Integer iRowCount = 0;
                for (int j = 0; j < array.size(); j++){
                	HashMap<String,Object> obj = array.get(j);
                	if (iRowCount == 0){
                		PdfPCell cVillage1 = new PdfPCell(new Paragraph("Village:",fData));
                        PdfPCell cVillage2 = new PdfPCell(new Paragraph(obj.get("villageid").toString(),fData));
                        PdfPCell cVillage3 = new PdfPCell(new Paragraph(obj.get("villagename").toString(),fData));
                        cVillage3.setColspan(5);
                        t.addCell(cVillage1);
                        t.addCell(cVillage2);
                        t.addCell(cVillage3);

                        PdfPCell cHeader1 = new PdfPCell(new Paragraph("SNO",fName));
                        PdfPCell cHeader2 = new PdfPCell(new Paragraph("ZONE",fName));
                        PdfPCell cHeader3 = new PdfPCell(new Paragraph("CON",fName));
                        PdfPCell cHeader4 = new PdfPCell(new Paragraph("NAMES",fName));
                        PdfPCell cHeader5 = new PdfPCell(new Paragraph("S/N",fName));
                        PdfPCell cHeader6 = new PdfPCell(new Paragraph("READINGS",fName));
                        PdfPCell cHeader7 = new PdfPCell(new Paragraph("COMMENTS",fName));
                        t.addCell(cHeader1);
                        t.addCell(cHeader2);
                        t.addCell(cHeader3);
                        t.addCell(cHeader4);
                        t.addCell(cHeader5);
                        t.addCell(cHeader6);
                        t.addCell(cHeader7);
                	}

                	PdfPCell cData1 = new PdfPCell(new Paragraph(obj.get("sno").toString(),fData));
                    PdfPCell cData2 = new PdfPCell(new Paragraph(obj.get("zone").toString(),fData));
                    PdfPCell cData3 = new PdfPCell(new Paragraph(obj.get("con").toString(),fData));
                    PdfPCell cData4 = new PdfPCell(new Paragraph(obj.get("name").toString(),fName));
                    PdfPCell cData5 = new PdfPCell(new Paragraph(java.util.Objects.toString(obj.get("serialno"), ""),fName));
                    PdfPCell cData6 = new PdfPCell(new Paragraph(""));
                    PdfPCell cData7 = new PdfPCell(new Paragraph(""));
                    t.addCell(cData1);
                    t.addCell(cData2);
                    t.addCell(cData3);
                    t.addCell(cData4);
                    t.addCell(cData5);
                    t.addCell(cData6);
                    t.addCell(cData7);

                    if (iRowCount == 50){
                    	iRowCount = 0;
                    }else{
                    	iRowCount++;
                    }
                }

                document.newPage();
        		document.add(t);
            }

            // (5)文書の出力を終了
            document.close();

			String url = "." + ServletListener.downloadurlpath + "/" + filename;
			return new RestResult<String>(url);
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

}

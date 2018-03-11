package ke.co.narwassco.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.arnx.jsonic.JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ke.co.narwassco.common.ServletListener;

/**
 * Worksheets
 * @version 1.00
 * @author Igarashi
 */
@Path("/Worksheets")
public class Worksheets {
	private final Logger logger = LogManager.getLogger(Worksheets.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<HashMap<String, Object>> post(
			@FormParam("workno") Integer workno,
			@FormParam("worktypeid") Integer worktypeid,
			@FormParam("otherworkname") String otherworkname,
			@FormParam("officerid") Integer officerid,
			@FormParam("inputdate") String inputdate,
			@FormParam("roadname") String roadname,
			@FormParam("worklocation") String worklocation,
			@FormParam("lekagescale") Integer lekagescale,
			@FormParam("dateofwork") String dateofwork,
			@FormParam("workersno") Integer workersno,
			@FormParam("timetaken") Integer timetaken,
			@FormParam("usedmaterial") String usedMaterial,
			@FormParam("pipe_material") String pipe_material,
			@FormParam("pipe_diameter") Integer pipe_diameter,
			@FormParam("pipe_depth") Integer pipe_depth,
			@FormParam("land_class") String land_class,
			@FormParam("pipe_class") String pipe_class,
			@FormParam("surface") String surface,
			@FormParam("work_point") String work_point,
			@FormParam("comments") String comments,
			@FormParam("geom") String geom
			) throws SQLException{
		logger.info("post start.");
		logger.debug("workno:" + workno);
		logger.debug("worktypeid:" + worktypeid);
		logger.debug("otherworkname:" + otherworkname);
		logger.debug("officerid:" + officerid);
		logger.debug("inputdate:" + inputdate);
		logger.debug("roadname:" + roadname);
		logger.debug("worklocation:" + worklocation);
		logger.debug("lekagescale:" + lekagescale);
		logger.debug("dateofwork:" + dateofwork);
		logger.debug("workersno:" + workersno);
		logger.debug("timetaken:" + timetaken);
		logger.debug("usedMaterial:" + usedMaterial);
		logger.debug("pipe_material:" + pipe_material);
		logger.debug("pipe_diameter:" + pipe_diameter);
		logger.debug("pipe_depth:" + pipe_depth);
		logger.debug("land_class:" + land_class);
		logger.debug("pipe_class:" + pipe_class);
		logger.debug("surface:" + surface);
		logger.debug("work_point:" + work_point);
		logger.debug("comments:" + comments);
		logger.debug("geom:" + geom);

		Connection conn = null;
		RestResult<HashMap<String, Object>> res = null;
		try{
			if (existWorkNo(workno)){
				res = new RestResult<HashMap<String, Object>>(RestResult.error,"This Work No. already exists in database.");
				return res;
			}

			Date _inputdate = new SimpleDateFormat("dd/MM/yyyy").parse(inputdate);
			Date _dateofwork = new SimpleDateFormat("dd/MM/yyyy").parse(dateofwork);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			logger.debug(sdf.format(_inputdate));
			logger.debug(sdf.format(_dateofwork));

			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			conn.setAutoCommit(false);

			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO worksheets ");
			sql.append(" (workno ");
			sql.append(" ,typeid ");
			sql.append(" ,typename ");
			sql.append(" ,inputdate ");
			sql.append(" ,officerid ");
			sql.append(" ,roadname ");
			sql.append(" ,worklocation ");
			sql.append(" ,leakagescale ");
			sql.append(" ,dateofwork ");
			sql.append(" ,workersno ");
			sql.append(" ,timetaken ");
			sql.append(" ,pipe_material ");
			sql.append(" ,pipe_diameter ");
			sql.append(" ,pipe_depth ");
			sql.append(" ,land_class ");
			sql.append(" ,pipe_class ");
			sql.append(" ,surface ");
			sql.append(" ,work_point ");
			sql.append(" ,comments ");
			sql.append(" ,geom)  ");
			sql.append(" VALUES (?, ?, ?");
			sql.append(" , Date'" + sdf.format(_inputdate) + "' ");
			sql.append(" , ?, ?, ?, ? ");
			sql.append(" , Date'" + sdf.format(_dateofwork) + "' ");
			sql.append(" , ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
			sql.append(" ,st_transform(st_geomfromtext('" + geom + "'," + ServletListener.epsg + ")," + ServletListener.epsgproject + ") )");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, workno);
			pstmt.setInt(2, worktypeid);
			pstmt.setString(3, otherworkname);
			pstmt.setInt(4, officerid);
			pstmt.setString(5, roadname);
			pstmt.setString(6, worklocation);
			pstmt.setInt(7, lekagescale);
			pstmt.setInt(8, workersno);
			pstmt.setInt(9, timetaken);
			pstmt.setString(10, pipe_material);
			pstmt.setInt(11, pipe_diameter);
			pstmt.setInt(12, pipe_depth);
			pstmt.setString(13, land_class);
			pstmt.setString(14, pipe_class);
			pstmt.setString(15, surface);
			pstmt.setString(16, work_point);
			pstmt.setString(17, comments);

			int result = pstmt.executeUpdate();
			if (result == 0){
				return new RestResult<HashMap<String, Object>>(RestResult.error,"It failed to insert data.");
			}
			ArrayList<HashMap<String,String>> materials = JSON.decode(usedMaterial);
			Boolean resMaterial = this.insertUsedMaterial(workno,materials,conn);
			if (resMaterial == false){
				conn.rollback();
				return new RestResult<HashMap<String, Object>>(RestResult.error,"It failed to insert data of used materials.");
			}
			conn.commit();
			return this.get(workno);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			conn.rollback();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
	}

	private boolean insertUsedMaterial(Integer workno,ArrayList<HashMap<String,String>> materials, Connection conn) throws SQLException{
		logger.debug("insertUsedMaterial start.");

		try{
			if (materials.size() == 0){
				return true;
			}

			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO usedmaterials ");
			sql.append(" (workno ");
			sql.append(" , seqno ");
			sql.append(" , description ");
			sql.append(" , unit ");
			sql.append(" , quantity ");
			sql.append(" , remarks)  ");
			sql.append(" VALUES (?, ?, ?, ?, ?, ? )");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			for (HashMap<String,String> values : materials){
				Integer seqno = Integer.parseInt(values.get("seqno"));
				String description = (String) values.get("description");
				String unit = (String) values.get("unit");
				Integer quantity = Integer.parseInt(values.get("quantity"));
				String remarks = (String) values.get("remarks");
				pstmt.setInt(1, workno);
				pstmt.setInt(2, seqno);
				pstmt.setString(3, description);
				pstmt.setString(4, unit);
				pstmt.setInt(5, quantity);
				pstmt.setString(6, remarks);

				int result = pstmt.executeUpdate();
				if (result == 0){
					return false;
				}
			}

			return true;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean existWorkNo(Integer workno) throws SQLException{
		Connection conn = null;
		Boolean res = false;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ");
			sql.append("  COUNT(*) count");
			sql.append(" FROM worksheets ");
			sql.append(" WHERE workno = ? ");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, workno);
			ResultSet rs = pstmt.executeQuery();
			Integer numrec = 0;
			while(rs.next()){
				numrec = rs.getInt("count");
			}
			if (numrec > 0){
				res = true;
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}finally{
			if (conn != null){
				conn.close();
				conn = null;
			}
		}
		return res;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<HashMap<String,Object>> get(@QueryParam("workno") Integer workno) throws SQLException {

		logger.info("get start.");
		logger.debug("workno:" + workno);
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ");
			sql.append("  ws.workno, ");
			sql.append("  ws.typeid, ");
			sql.append("  wt.name as worktypename, ");
			sql.append("  ws.typename as othersname,  ");
			sql.append("  ws.officerid,  ");
			sql.append("  o.name,  ");
			sql.append("  o.designation,  ");
			sql.append("  ws.inputdate,  ");
			sql.append("  ws.roadname,  ");
			sql.append("  ws.worklocation,  ");
			sql.append("  CASE ws.leakagescale WHEN 1 THEN 'Large' WHEN 2 THEN 'Middle' WHEN 3 THEN 'Small' ELSE '' END AS lekagescale,  ");
			sql.append("  ws.dateofwork,  ");
			sql.append("  ws.workersno,  ");
			sql.append("  ws.timetaken,  ");
			sql.append("  ws.pipe_material,  ");
			sql.append("  ws.pipe_diameter,  ");
			sql.append("  ws.pipe_depth,  ");
			sql.append("  ws.land_class,  ");
			sql.append("  ws.pipe_class,  ");
			sql.append("  ws.surface,  ");
			sql.append("  ws.work_point,  ");
			sql.append("  ws.comments,  ");
			sql.append("  ST_AsText(ws.geom) AS WKT ");
			sql.append("FROM  worksheets ws ");
			sql.append("INNER JOIN officers o ");
			sql.append("ON o.officerid = ws.officerid ");
			sql.append("INNER JOIN worktype wt ");
			sql.append("ON wt.worktypeid = ws.typeid ");
			sql.append("WHERE ws.workno = ?");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, workno);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd= rs.getMetaData();
			HashMap<String,Object> res = new HashMap<String,Object>();
			while(rs.next()){
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colname = rsmd.getColumnName(i);
					res.put(colname, rs.getObject(colname));
				}
			}

			ArrayList<HashMap<String,Object>> resMaterials = getMaterials(workno);
			if (resMaterials.size() > 0){
				res.put("UsedMaterials", resMaterials);
			}

			return new RestResult<HashMap<String,Object>>(res);
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

	private ArrayList<HashMap<String,Object>> getMaterials(int workno) throws SQLException{
		logger.info("get start.");
		Connection conn = null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(ServletListener.dburl, ServletListener.dbuser,ServletListener.dbpassword);
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ");
			sql.append("  u.workno, ");
			sql.append("  u.seqno, ");
			sql.append("  u.description, ");
			sql.append("  u.unit, ");
			sql.append("  u.quantity, ");
			sql.append("  u.remarks ");
			sql.append("FROM ");
			sql.append("  usedmaterials u ");
			sql.append("WHERE u.workno = ?");
			sql.append("ORDER BY u.workno, u.seqno ");
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, workno);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd= rs.getMetaData();
			ArrayList<HashMap<String,Object>> resMaterials = new ArrayList<HashMap<String,Object>>();
			while(rs.next()){
				HashMap<String,Object> material = new HashMap<String,Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colname = rsmd.getColumnName(i);
					material.put(colname, rs.getObject(colname));
				}
				resMaterials.add(material);
			}

			return resMaterials;
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

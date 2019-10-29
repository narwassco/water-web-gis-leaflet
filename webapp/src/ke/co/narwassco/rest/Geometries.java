package ke.co.narwassco.rest;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import ke.co.narwassco.geometryOp.PolygonOp;
import ke.co.narwassco.geometryOp.Util;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * geometries
 * @version 1.00
 * @author Igarashi
 */
@Path("/geometries")
public class Geometries {

	private final Logger logger = Logger.getLogger(Geometries.class);

	/**
	 * 2つのジオメトリのintersection部分を返却する
	 * @param wkt1 well-known-text1
	 * @param wkt2 well-known-text2
	 * @return intersection結果のWKT配列
	 */
	@Path("/intersection")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<ArrayList<String>> intersection(
			@QueryParam("geom1") String wkt1,
			@QueryParam("geom2") String wkt2){
		try{
			Geometry geom1 = Util.createGeometryFromWkt(wkt1);
			Geometry geom2 = Util.createGeometryFromWkt(wkt2);
			Geometry geometories = geom1.intersection(geom2);
			ArrayList<String> geomList = new ArrayList<String>();
			for (int i = 0;i < geometories.getNumGeometries();i++){
				Geometry geom = geometories.getGeometryN(i);
				geomList.add(geom.toText());
			}
			return new RestResult<ArrayList<String>>(geomList);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 2つのジオメトリのdifference部分を返却する
	 * @param wkt1 well-known-text1
	 * @param wkt2 well-known-text2
	 * @return difference結果のWKT配列
	 */
	@Path("/difference")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<ArrayList<String>> difference(
			@QueryParam("geom1") String wkt1,
			@QueryParam("geom2") String wkt2){
		try{
			Geometry geom1 = Util.createGeometryFromWkt(wkt1);
			Geometry geom2 = Util.createGeometryFromWkt(wkt2);
			Geometry geometories = geom1.difference(geom2);
			ArrayList<String> geomList = new ArrayList<String>();
			for (int i = 0;i < geometories.getNumGeometries();i++){
				Geometry geom = geometories.getGeometryN(i);
				geomList.add(geom.toText());
			}
			return new RestResult<ArrayList<String>>(geomList);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 2つのジオメトリのsymdifference部分を返却する
	 * @param wkt1 well-known-text1
	 * @param wkt2 well-known-text2
	 * @return symdifference結果のWKT配列
	 */
	@Path("/symdifference")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<ArrayList<String>> symdifference(
			@QueryParam("geom1") String wkt1,
			@QueryParam("geom2") String wkt2){
		try{
			Geometry geom1 = Util.createGeometryFromWkt(wkt1);
			Geometry geom2 = Util.createGeometryFromWkt(wkt2);
			Geometry geometories = geom1.symDifference(geom2);
			ArrayList<String> geomList = new ArrayList<String>();
			for (int i = 0;i < geometories.getNumGeometries();i++){
				Geometry geom = geometories.getGeometryN(i);
				geomList.add(geom.toText());
			}
			return new RestResult<ArrayList<String>>(geomList);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * バッファを作成して返す
	 * @param wkt Well-Known-Text
	 * @param distance バッファ距離
	 * @return バッファ作成後のWKT配列
	 */
	@Path("/buffer")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<ArrayList<String>> buffer(
			@QueryParam("geometries") String wktGeometryies,
			@QueryParam("distance") Double distance){
		try{
			ArrayList<String> geomList = new ArrayList<String>();
			Geometry geometories = Util.createGeometryFromWkt(wktGeometryies);
			for (int i = 0;i < geometories.getNumGeometries();i++){
				Geometry geom = geometories.getGeometryN(i);
				geomList.add(geom.buffer(distance).toText());
			}
			return new RestResult<ArrayList<String>>(geomList);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * ポリゴンを結合する
	 * @param wktPolygon マルチポリゴンのWKT
	 * @return 結合後のポリゴンのWKT。結合できなかった場合は空文字列
	 */
	@Path("/union")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<String> union(@QueryParam("polygon") String wktPolygon){
		try{
			Geometry multiPolygon = Util.createGeometryFromWkt(wktPolygon);
			PolygonOp polygonOp = new PolygonOp(multiPolygon);
			Geometry union = polygonOp.concatenate();
			String res = "";
			if (union != null){
				res = union.toString();
			}
			return new RestResult<String>(res);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * トポロジー編集をする
	 * @param before 編集前のポリゴン
	 * @param after 編集後のポリゴン
	 * @return
	 */
	@Path("/topology")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<HashMap<String,String>> topology(
			@QueryParam("before") String before,
			@QueryParam("after") String after,
			@QueryParam("touchedPolygon") String touchedPolygon){
		try{
			//logger.debug("before:" + before);
			//logger.debug("after:" + after);
			//logger.debug("touchedPolygon:" + touchedPolygon);

			Geometry beforePolygon = Util.createGeometryFromWkt(before);
			Geometry afterPolygon = Util.createGeometryFromWkt(after);
			GeometryFactory fact = new GeometryFactory(beforePolygon.getPrecisionModel());

			//トポロジー編集で移動前後のポイントの割り出し
			MultiPoint beforeMultiPt = fact.createMultiPoint(beforePolygon.getCoordinates());
			MultiPoint afterMultiPt = fact.createMultiPoint(afterPolygon.getCoordinates());
			Geometry beforePoint = beforeMultiPt.difference(afterMultiPt);
			Geometry afterPoint = afterMultiPt.difference(beforeMultiPt);
			//logger.debug("beforePoint:" + beforePoint);
			//logger.debug("afterPoint:" + afterPoint);

			Geometry beforeLine = null;
			if (beforePoint.isEmpty()){
				//辺の途中にノードを追加しようとした場合beforePointを取得できない
				//afterPointの前後のノードから変更前ポリゴンの対象の辺を見つける。
				PolygonOp afterOp = new PolygonOp(afterPolygon);
				MultiPoint points = afterOp.findBeforeAfterPoint(afterPoint);
				beforeLine = fact.createLineString(points.getCoordinates());
				//logger.debug("beforeLine:" + beforeLine.toString());
			}

			//移動前のポイントとタッチする隣接ポリゴンを割出し
			Geometry touchedMulti = Util.createGeometryFromWkt(touchedPolygon);
			ArrayList<Geometry> newPolygonList = new ArrayList<Geometry>();
			for (int i = 0; i < touchedMulti.getNumGeometries(); i++){
				Geometry temp = touchedMulti.getGeometryN(i);
				PolygonOp polygonOp = new PolygonOp(temp);
				if (temp.touches(beforePoint)){
					//ノードの移動
					Polygon newPolygon = polygonOp.movePoint((Point) beforePoint,(Point) afterPoint);
					newPolygonList.add(newPolygon);
				}else if (temp.contains(afterPoint)){
					//ノードの追加
					Polygon newPolygon = polygonOp.addPointInPolygon((Point) afterPoint);
					newPolygonList.add(newPolygon);
				}else if (beforePoint.isEmpty() && beforeLine.isEmpty() == false && temp.covers(beforeLine)){
					Polygon newPolygon = polygonOp.addPointInPolygon((Point) afterPoint);
					newPolygonList.add(newPolygon);
				}else{
					//何もしない
					newPolygonList.add(temp);
				}
			}
			Geometry touchedNewGeom = fact.createMultiPolygon(newPolygonList.toArray(new Polygon[0]));
			//logger.debug("touchedNewGeom:" + touchedNewGeom.toString());

			HashMap<String,String> res = new HashMap<String,String>();
			res.put("target",after);
			res.put("touches", touchedNewGeom.toString());

			return new RestResult<HashMap<String,String>>(res);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * ポリゴンを分割する
	 * @param wktPolygon PolygonのWKT
	 * @param wktSplitLine 分割ラインのWKT
	 * @return
	 */
	@Path("/split")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResult<ArrayList<String>> split(
			@QueryParam("polygon") String wktPolygon,
			@QueryParam("line") String wktSplitLine) {

		try {
			Geometry splitLine = Util.createGeometryFromWkt(wktSplitLine);
			Geometry multiPolygon = Util.createGeometryFromWkt(wktPolygon);
			GeometryFactory fact = new GeometryFactory(multiPolygon.getPrecisionModel());
			ArrayList<Geometry> geomList = new ArrayList<Geometry>();
			for (int i = 0; i < multiPolygon.getNumGeometries();i++){
				Polygon polygons = (Polygon) multiPolygon.getGeometryN(i);
				Geometry exteriorRing =fact.createPolygon(polygons.getExteriorRing().getCoordinates());
				PolygonOp exteriorOp = new PolygonOp(exteriorRing);
				Geometry exteriorOpRes = exteriorOp.split((LineString) splitLine);
				if (exteriorOpRes == null) {
					continue;
				}

				for (int j = 0;j < exteriorOpRes.getNumGeometries();j++){
					geomList.add(exteriorOpRes.getGeometryN(j));
				}

				if (polygons.getNumInteriorRing() > 0){
					//穴あきポリゴンの時
					for (int j = 0; j < polygons.getNumInteriorRing();j++){
						Geometry interiorRing = fact.createPolygon(polygons.getInteriorRingN(j).getCoordinates());
						for (int k = 0; k < geomList.size(); k++){
							Geometry tempPolygon = geomList.get(k);
							if (tempPolygon.contains(interiorRing)){
								//穴あきリングが外郭線の完全に内側にある
								Polygon tempextPoly = (Polygon) tempPolygon;
								LinearRing extRing = fact.createLinearRing(tempextPoly.getExteriorRing().getCoordinates());
								ArrayList<LinearRing> intRings = new ArrayList<LinearRing>();
								for (int l = 0;l < tempextPoly.getNumInteriorRing();l++){
									intRings.add(fact.createLinearRing(tempextPoly.getInteriorRingN(l).getCoordinates()));
								}
								intRings.add(fact.createLinearRing(interiorRing.getCoordinates()));

								tempPolygon = fact.createPolygon(extRing, intRings.toArray(new LinearRing[0]));
								geomList = setGeomList(geomList,tempPolygon,k);
								//geomList.set(k, tempPolygon);

							}else if (tempPolygon.intersects(interiorRing)){
								//穴あきリングが外郭線と共有部分がある
								tempPolygon = tempPolygon.difference(interiorRing);
								geomList = setGeomList(geomList,tempPolygon,k);
								//geomList.set(k, tempPolygon);

							}else{
								//交わっていない
								continue;
							}
						}
					}
				}
			}
			ArrayList<String> res = new ArrayList<String>();
			for (Geometry geom : geomList) {
				res.add(geom.toString());
			}
			return new RestResult<ArrayList<String>>(res);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * ジオメトリリストに追加して返す
	 * @param geomList ジオメトリリスト
	 * @param geom 追加対象のジオメトリ
	 * @param index 追加対象のジオメトリの位置
	 * @return 追加後のジオメトリリスト
	 */
	private ArrayList<Geometry> setGeomList(ArrayList<Geometry> geomList,Geometry geom,int index){
		if (geom.getNumGeometries() == 1){
			geomList.set(index, geom);
		}else{
			//マルチジオメトリの場合はばらして追加する
			geomList.set(index, geom.getGeometryN(0));
			for (int i = 1;i < geom.getNumGeometries();i++){
				geomList.add(geom.getGeometryN(i));
			}
		}
		return geomList;
	}

}

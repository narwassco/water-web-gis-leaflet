package ke.co.narwassco.geometryOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.operation.distance.DistanceOp;

public class PolygonOp {
	//private final Logger logger = Logger.getLogger(PolygonOp.class);
	
	/**
	 * 対象となるPolygonまたはMultiPolygonジオメトリ
	 */
	private Geometry targetPolygon = null;
	
	/**
	 * コンストラクタ（Geometry版）
	 * @param polygon PolygonまたはMultiPolygonジオメトリ
	 */
	public PolygonOp(Geometry polygon){
		targetPolygon = polygon;
	}
	
	/**
	 * コンストラクタ（WKT版）
	 * @param wkt Well-Known-Text
	 * @throws ParseException
	 */
	public PolygonOp(String wkt) throws ParseException{
		targetPolygon = Util.createGeometryFromWkt(wkt);
	}
	
	/**
	 * ポリゴンの辺にノードを追加して新しいポリゴンを返す
	 * @param point 追加するポイント
	 * @return  ノード追加後の新しいポリゴン
	 * @throws ParseException 
	 */
	public Polygon addPointInPolygon(
			Point point) throws ParseException{
		//ポイントと一番近いラインを取得
		LineString nearestLine = getCloestLine(point);
		//logger.debug("nearestLine:" + nearestLine.toString());
		//ポリゴンのマルチポイントをループさせてマルチラインを作成する
		ArrayList<LineString> lineList = new ArrayList<LineString>();
		GeometryFactory fact = new GeometryFactory(point.getPrecisionModel());
		Coordinate[] nodes = targetPolygon.getCoordinates();
		for (int i = 0,j = 1; i < nodes.length -1;i++,j = i + 1){
			Coordinate frmP = nodes[i];
			Coordinate toP = nodes[j];
			LineString line = fact.createLineString(new Coordinate[]{frmP,toP});
			//logger.debug("targetline:" + line.toString());
			//指定ポイントと直近のラインと同じ場合は、当該ラインの中間にポイントを差し込む
			if (line.equals(nearestLine)){
				//但しfromまたはtoのいずれかが追加ポイントと同じ場合は除外
				if (!(frmP.equals2D(point.getCoordinate()) 
						|| toP.equals2D(point.getCoordinate()))){
					//logger.debug("line equal");
					line = fact.createLineString(new Coordinate[]{frmP,point.getCoordinate(),toP});
					//logger.debug("addedLine:" + line.toString());
				}
			}
			//ラインリストに追加
			lineList.add(line);
		}
		
		//マルチラインをポリゴンに変換する
		MultiLineString multiLine = fact.createMultiLineString(lineList.toArray(new LineString[0]));
		LineStringOp lineOp = new LineStringOp(multiLine);
		Polygon newpolygon = lineOp.lineToPolygon();
		//logger.debug("newpolygon:" + newpolygon.toString());
		return newpolygon;
	}
	
	/**
	 * ポリゴン内のポイントを指定ポイントに移動する
	 * @param before 移動前のポイント
	 * @param after 移動後のポイント
	 * @return 編集後のポリゴン
	 */
	public Polygon movePoint(Point before,Point after){
		ArrayList<LineString> lineList = new ArrayList<LineString>();
		GeometryFactory fact = new GeometryFactory(before.getPrecisionModel());
		Coordinate[] nodes = targetPolygon.getCoordinates();
		for (int i = 0,j = 1; i < nodes.length -1;i++,j = i + 1){
			Coordinate frmP = nodes[i];
			Coordinate toP = nodes[j];
			
			if (frmP.equals(before.getCoordinate())){
				frmP = after.getCoordinate();
			}else if (toP.equals(before.getCoordinate())){
				toP = after.getCoordinate();
			}
			
			LineString line = fact.createLineString(new Coordinate[]{frmP,toP});
			//ラインリストに追加
			lineList.add(line);
		}
		//マルチラインをポリゴンに変換する
		MultiLineString multiLine = fact.createMultiLineString(lineList.toArray(new LineString[0]));
		LineStringOp lineOp = new LineStringOp(multiLine);
		Polygon newpolygon = lineOp.lineToPolygon();
		return newpolygon;
	}
	
	/**
	 * 指定ポイントの前後のノードを見つけて返す
	 * @param point 指定ポイント
	 * @return 前後のマルチポイント
	 */
	public MultiPoint findBeforeAfterPoint(Geometry point){
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		Coordinate[] nodes = targetPolygon.getCoordinates();
		ArrayList<Coordinate> points = new ArrayList<Coordinate>();
		for (int i = 0,j = 1; i < nodes.length -1;i++,j = i + 1){
			Coordinate frmP = nodes[i];
			Coordinate toP = nodes[j];
			
			if (frmP.equals(point.getCoordinate())){
				points.add(toP);
			}else if (toP.equals(point.getCoordinate())){
				points.add(frmP);
			}
		}
		return fact.createMultiPoint(points.toArray(new Coordinate[0]));
	}
	
	/**
	 * 指定ポリゴンを構成するラインの内、指定ポイントと一番近いラインを返す
	 * @param point 指定ポイント
	 * @return 一番近いラインジオメトリ
	 * @throws ParseException 
	 */
	public LineString getCloestLine(Geometry point) throws ParseException{
		
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		
		double minDist = -1;
		LineString nearestLine = null;
		Coordinate[] nodes = targetPolygon.getCoordinates();
		for (int i = 0,j = 1; i < nodes.length -1;i++,j = i + 1){
			Coordinate frmP = nodes[i];
			Coordinate toP = nodes[j];
			LineString line = fact.createLineString(new Coordinate[]{frmP,toP});
			
			DistanceOp distOp = new DistanceOp(line,point);
			////取得後のマルチポイントの1点目にはポリゴン上の最近点、パラメータで渡したポイントが格納
			double tempDist = distOp.distance();
			if (minDist == -1){
				minDist = tempDist;
			}else if (minDist > tempDist){
				minDist = tempDist;
			}else{
				continue;
			}
			nearestLine = line;
		}
		return nearestLine;
	}
	
	/**
	 * 指定ポイントと一番近いポリゴンの筆界線上の点と距離を取得して返す
	 * @param point 指定ポイントジオメトリ
	 * @return 結果が格納されたハッシュマップ。
	 * （１）distance：距離（単位はcm）。
	 * （２）closetPoint1：ポリゴン上の最も近いポイントのジオメトリ
	 * （３）closetPoint2：指定ポイントのWKT
	 */
	public HashMap<String,String> getClosestPoint(Geometry point){
		//ポリゴンをラインに変換
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		LineString polyline = fact.createLineString(targetPolygon.getCoordinates());
		
		//一番近い点と距離を計算
		DistanceOp distOp = new DistanceOp(polyline,point);
		Coordinate[] nearestPoints = distOp.nearestPoints();
		double distance = fact.createLineString(nearestPoints).getLength();
		//JTSの長さの単位は度分秒形式。
		//単位をcmに変換。cmに変換するには10の-7乗したものを使用する
		distance = distance / Math.pow(10, -7);
		
		//結果を格納
		HashMap<String,String> res = new HashMap<String,String>();
		res.put("Distance", String.valueOf(distance));
		res.put("ClosetPoint1", fact.createPoint(nearestPoints[0]).toString());
		res.put("ClosetPoint2", fact.createPoint(nearestPoints[nearestPoints.length -1]).toString());
		return res;
	}
	
	/**
	 * ラインとポリゴンで位相的に差（MINUS演算）となるジオメトリを返す
	 * @param line ラインジオメトリ
	 * @return ラインとポリゴンの重なっていない部分のラインジオメトリ
	 * @throws ParseException 
	 */
	public LineString getDifferenceLine(
			LineString line
			){

		//ポリゴンをラインに変換
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		Geometry polyline = fact.createLinearRing(targetPolygon.getCoordinates());
		//ラインとポリゴンラインの差分ラインを取得
		Geometry differenceLine = line.difference(polyline);
		if (differenceLine instanceof LineString){
			return (LineString) differenceLine;
		}else{
			return null;
		}
		
	}
	
	/**
	 * パラメータのポリゴンとラインの交点のジオメトリを取得する
	 * @param polygon ポリゴンのジオメトリ
	 * @param splitLine 分割ラインのジオメトリ
	 * @return 結果が格納されたハッシュマップ。
	 * キーはString値で、値はObject型なので使用する場合は型変換すること。
	 * （１）RetVal：RetCodeの定数を指定。
	 * （２）Geometry：交点のジオメトリ。MULTIPOINTで返却
	 * @throws Exception 
	 */
	public Geometry getIntersectPoints(
			LineString splitLine
			){
		
		//int retVal = RetCode.ERROR;
		
		//指定ポリゴンを線分図形に変換する
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		Geometry polyline = fact.createLineString(targetPolygon.getCoordinates());
		
		//交点のポイントジオメトリを取得する
		Geometry intersect = polyline.intersection(splitLine);
		
		return intersect;
	}
	
	/**
	 * ポリゴン内の分割ラインを取得する
	 * @param polygon ポリゴンのジオメトリ
	 * @param splitLine 分割ラインのジオメトリ
	 * @return ポリゴン内部の分割ライン
	 * @throws Exception 
	 */
	public MultiLineString getSplitLine(
			LineString splitLine
			) throws Exception{
		
		//ポリゴンとラインの差分ジオメトリを取得する
		LineString difference = getDifferenceLine(splitLine);
		if (difference == null){
			difference = splitLine;
		}
		
		//交点のポイントジオメトリを取得する
		MultiPoint intersect = (MultiPoint) getIntersectPoints(splitLine);
		if (intersect.isEmpty()){
			return null;
		}
		
		//ポリゴン内部の分割ラインを取得する
		Geometry inline = targetPolygon.intersection(splitLine);
		ArrayList<LineString> inlineList = new ArrayList<LineString>();
		for (int i = 0; i < inline.getNumGeometries();i++){
			LineString line = (LineString) inline.getGeometryN(i);
			//内部のラインの始点と終点が交点と一致しているかチェック
			//ポリゴン内部の分割ラインの始点、終点が交点と一致しているかチェック
			Coordinate firstP = line.getCoordinates()[0];
			Coordinate lastP = line.getCoordinates()[line.getNumPoints() - 1];
			int intersect_count = 0;
			for (Coordinate tempP : intersect.getCoordinates()){
				if (firstP.equals2D(tempP) || lastP.equals2D(tempP)){
					intersect_count++;
				}
			}
			if (intersect_count != 2){
				//交点が2つでない場合はポリゴンに内接する分割ラインとみなさない
				continue;
			}
			inlineList.add(line);
		}
		
		if (inlineList.size() == 0){
			//分割ラインが指定筆の内部にありません
			return null;
		}
		
		GeometryFactory fact = new GeometryFactory(splitLine.getPrecisionModel());
		return fact.createMultiLineString(inlineList.toArray(new LineString[0]));
	}
	
	/**
	 * 図形２が、図形１の内部バッファに内包されるか判定する
	 * @param insidedist 図形１の内部バッファを作成するときの距離値（単位：ｍ）。0のときは、内部バッファは作成しない。
	 * @param geom2 図形2
	 * @return TRUE：図形２は図形１の内部にある
	 */
	public Boolean isInsideGeometry(
			Double insidedist,
			Geometry geom2
			){
		
		Geometry geom1 = targetPolygon;
		Geometry buffer = geom1;
		if (insidedist != 0 ){
			//メートルを度分秒に変換する
			insidedist = insidedist * Math.pow(10, -5);
			
			//図形1の内部バッファを作成する
			buffer = geom1.buffer(-insidedist);
		}
		//図形2が図形1又はその内部バッファの内部にある、または内部でその境界と接する場合、trueを返す。
		//それ以外はfalseを返す。
		return (geom2.within(buffer) || geom2.coveredBy(buffer));
	}
	
	/**
	 * 指定図形の最小境界矩形の対角線の長さを取得する
	 * @param geom 図形
	 * @return 指定図形の最小境界矩形の対角線の長さ
	 */
	public Double getLengthMbrDiagonal(){
		
		//最小境界矩形を取得
		//ジオメトリには以下の順でポイントが格納。
		//(minx, miny), (maxx, miny), (maxx, maxy), (minx, maxy), (minx, miny)
		Geometry bounds = targetPolygon.getBoundary();
		
		//最小緯度経度を取得
		Coordinate minP = bounds.getCoordinates()[0];
		
		//最大緯度経度を取得
		Coordinate maxP = bounds.getCoordinates()[2];
		
		//最小境界矩形の左下から右上までの距離を算出する
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		DistanceOp distOp = new DistanceOp(fact.createPoint(minP),fact.createPoint(maxP));
		return distOp.distance();
	}
	
	/**
	 * マルチポリゴンを結合して一つのポリゴンにする。
	 * @return 結合後のポリゴン。ポリゴン同士が非接触で結合できない場合はNULLが返る
	 */
	public Polygon concatenate(){
		
		//マルチポリゴンから一つずつ取出し結合していく
		Geometry unionGeom = null;
		for (int i = 0;i< targetPolygon.getNumGeometries();i++){
			Geometry tempPolygon = targetPolygon.getGeometryN(i);
			if (unionGeom == null){
				unionGeom = tempPolygon;
			}else{
				unionGeom = unionGeom.union(tempPolygon);
			}
		}
		
		if (!(unionGeom instanceof Polygon)){
			//ポリゴン以外の時
			return null;
		}
		
		//結合図形が有効な図形かチェックする
		if (Util.validatePlace(unionGeom) != RetCode.SUCCESS){
			return null;
		}
		return (Polygon) unionGeom;
	}
	
	/**
	 * 指定ポリゴンをラインで複数のポリゴンの分割して返す。
	 * @param originalSplitLine オリジナル分割ライン
	 * @return {Geometry} 分割後のマルチポリゴン
	 * @throws Exception 
	 */
	public Geometry split(
			LineString originalSplitLine
			) throws Exception{
		
		//ポリゴン内部の分割ラインを取得する
		MultiLineString geom_inlines = getSplitLine(originalSplitLine);
		if (geom_inlines == null){
			return null;
		}
		GeometryFactory fact = new GeometryFactory(targetPolygon.getPrecisionModel());
		ArrayList<Polygon> polygonList = new ArrayList<Polygon>();
		for (int cntLine = 0; cntLine < geom_inlines.getNumGeometries();cntLine++){
			LineString geom_inline = (LineString) geom_inlines.getGeometryN(cntLine);
			
			//分割対象のポリゴンを設定
			if (polygonList.size() > 0){
				//編集中ポリゴンがある場合、geom_inlineと2点で交差するものをtargetとする。
				for (Polygon temp_Polygon : polygonList){
					if (temp_Polygon.intersects(geom_inline)){
						targetPolygon = temp_Polygon;
					}
				}
			}
			
			//-----------------------------
			//分割処理
			//-----------------------------
			//(1)ポリゴンと線分で、排他的論理和を取る（SDO_GEOM.SDO_XOR）
			//ポリゴンに分割ラインとの交点を追加する
			//※Oracle版ではXOR関数を使用しているがJTS版では判定精度が厳しすぎうまくいかないため、
			//自作したaddPointInPolygon関数で交点を差し込むようにしました。
			Polygon wk_polygon = addPointInPolygon(geom_inline.getPointN(0));
			PolygonOp wk_polygonOp = new PolygonOp(wk_polygon);
			wk_polygon = wk_polygonOp.addPointInPolygon(geom_inline.getPointN(geom_inline.getNumPoints() -1));
			
			//(2)分割後ポリゴン１を生成する
			//(1)のポリゴンを線分図形に変換する
			LineString wk_polyline = fact.createLineString(wk_polygon.getCoordinates());
			
			//ポリゴン図形における交点の座標格納位置(ID)を取得する(ポリゴンの開始座標と同じ点を交点１とする）
			int pointIdx1 = -1; 
			int pointIdx2 = -1;
			int iCnt = 0;
			for (Coordinate polyCoord :wk_polyline.getCoordinates()){
				for (Coordinate ptCoord : geom_inline.getCoordinates()){
					if (polyCoord.equals2D(ptCoord)){
						if (pointIdx1 < 0){
							pointIdx1 = iCnt;
							
						}else{
							pointIdx2 = iCnt;
						}
					}
				}
				iCnt++;
			}
			
			//ID1～ID2までの点列 ＋ ポリゴン内部の分割ラインで、ポリゴン図形のWKTを作成する
			//ID1～ID2までの点列の部分
			ArrayList<Coordinate> poly1_points = new ArrayList<Coordinate>();
			for (int i = pointIdx1; i <= pointIdx2;i++){
				Coordinate wk_coord = wk_polyline.getCoordinateN(i);
				poly1_points.add(wk_coord);
			}
			//ポリゴン内部の分割ラインの部分
			//交点２と分割ラインの始点が異なる場合は、分割ラインの点列の順序を逆にする
			Point inline_firstP = geom_inline.getPointN(0);
			Point inline_lastP = wk_polyline.getPointN(pointIdx2);
			if (!inline_firstP.equals(inline_lastP)){
				geom_inline = (LineString) geom_inline.reverse();
			}
			
			//分割ラインの最終座標の位置（＝交点１）を取得する
			int inline_lastidx = geom_inline.getNumPoints() -1;
			
			//分割ラインの2番目の座標から最後の座標まで、ポリゴンに追加する。
			for (int i = 1;i <= inline_lastidx;i++){
				Point wk_coord = geom_inline.getPointN(i);
				poly1_points.add(wk_coord.getCoordinate());
			}
			
			//座標値リストからジオメトリを生成する
			Polygon geom_poly1 = fact.createPolygon(poly1_points.toArray(new Coordinate[0]));
			//logger.debug("geom_poly1：" + geom_poly1.toString());
			
			//(4)分割後ポリゴン２を生成する
			//ID2～最後までの点列の部分
			ArrayList<Coordinate> poly2_points = new ArrayList<Coordinate>();
			for (int i = pointIdx2; i < wk_polyline.getNumPoints() -1;i++){
				Point wk_coord = wk_polyline.getPointN(i);
				//logger.debug(wk_coord.toString());
				poly2_points.add(wk_coord.getCoordinate());
			}
			//ポリゴンの最初からID1までの点列の部分
			for (int i = 0; i < pointIdx1;i++){
				Point wk_coord = wk_polyline.getPointN(i);
				//logger.debug(wk_coord.toString());
				poly2_points.add(wk_coord.getCoordinate());
			}
			//分割ラインの2番目の座標から最後の座標まで、ポリゴンに追加する。
			LineString geom_reverseLine = (LineString) geom_inline.reverse();
			for (int i = 0;i < geom_reverseLine.getNumPoints();i++){
				Point wk_coord = geom_reverseLine.getPointN(i);
				//logger.debug(wk_coord.toString());
				poly2_points.add(wk_coord.getCoordinate());
			}
			Polygon geom_poly2 = fact.createPolygon(poly2_points.toArray(new Coordinate[0]));
			//Geometry geom_poly2 = targetPolygon.symDifference(geom_poly1);
			//logger.debug("geom_poly2：" + geom_poly2.toString());
			
			int retVal = RetCode.SUCCESS;
			//分割した図形が、ポリゴンのみの図形になっているかチェックする
			if (Util.validatePlace(geom_poly1) != RetCode.SUCCESS
					|| Util.validatePlace(geom_poly2) != RetCode.SUCCESS){
				//分割線が筆の頂点の近傍と交差しているため、正しく分割できませんでした。
				retVal = RetCode.SPLIT_LINE_OPE_ERROR;
			}
			
			if (retVal != RetCode.SUCCESS){
				continue;
			}
			
			//編集対象のポリゴンをリストから一旦削除
			Iterator<Polygon> itr = polygonList.iterator();
			while(itr.hasNext()){
				Polygon pg = itr.next();
				if (pg.equals(targetPolygon)){
					itr.remove();
				}
			}
			
			polygonList.add(geom_poly1);
			polygonList.add(geom_poly2);
		}
		
		//マルチポリゴン生成
		MultiPolygon geom_multi = fact.createMultiPolygon(polygonList.toArray(new Polygon[0]));
		return geom_multi;
		
		
	}
	
}

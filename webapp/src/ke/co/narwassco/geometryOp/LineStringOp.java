package ke.co.narwassco.geometryOp;

import java.util.ArrayList;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

/**
 * Operation for LineString object
 * @author Jin Igarashi
 * @version 1.0
 */
public class LineStringOp {
	//private final Logger logger = Logger.getLogger(LineStringOp.class);
	
	/**
	 * 対象となるラインジオメトリ
	 */
	private Geometry targetLine = null;
	
	/**
	 * コンストラクタ（Geometry版）
	 * @param line LineStringまたはMultiLineStringジオメトリ
	 */
	public LineStringOp(Geometry line){
		targetLine = line;
	}
	
	/**
	 * コンストラクタ（WKT版）
	 * @param wkt Well-Known-Text
	 * @throws ParseException
	 */
	public LineStringOp(String wkt) throws ParseException{
		targetLine = Util.createGeometryFromWkt(wkt);
	}
	
	/**
	 * 指定ラインの先端に矢印を作成して返却する
	 * @return 矢印を付与したポリゴンジオメトリ
	 */
	public Polygon createArrow(){
		
		if (targetLine == null){
			return null;
		}
		
		//初期設定
		//定数を指定
		final double from_cross_lenrate = 0.3;		//開始点と交差点の距離の割合
		final double cross_triangle_lenrate = 0.1;	//交差点と三角形の2点との距離の割合
		
		//開始点と終了点を取得
		Coordinate from_point = ((LineString) targetLine).getCoordinateN(0);
		Coordinate to_point = ((LineString) targetLine).getCoordinateN(targetLine.getNumPoints() -1);
		/*logger.debug("from_point:" + from_point.toString());
		logger.debug("to_point:" + to_point.toString());*/
		
		GeometryFactory fact = new GeometryFactory(targetLine.getPrecisionModel());
		
		////ラインの傾きを計算
		//double line_bearing = Util.getLineAngle(fact.createPoint(from_point), fact.createPoint(to_point), 2);
		
		//ラインの開始点から指定距離にある点を計算
		double cross_pntX = (to_point.x - from_point.x) * from_cross_lenrate + from_point.x;
		double cross_pntY = (to_point.y - from_point.y) * from_cross_lenrate + from_point.y;
		Point cross_point = fact.createPoint(new Coordinate(cross_pntX,cross_pntY));
		//logger.debug("cross_point:" + cross_point.toText());
		
		//矢印の三角形の2点を求めるための基準点を計算
		double triangle_length = Math.sqrt(Math.pow(from_cross_lenrate, 2) + Math.pow(cross_triangle_lenrate, 2));
		//logger.debug("triangle_length:" + triangle_length);
		double crossRotate_pntX = (to_point.x - from_point.x) * triangle_length + from_point.x;
		double crossRotate_pntY = (to_point.y - from_point.y) * triangle_length + from_point.y;
		Point crossRotatePoint = fact.createPoint(new Coordinate(crossRotate_pntX,crossRotate_pntY));
		//logger.debug("crossRotatePoint:" + crossRotatePoint.toText());
		//矢印の傾きを求める
		//double atanRegian = Math.atan(cross_triangle_lenrate / from_cross_lenrate);
	
		double atanRegian = Math.atan2(cross_triangle_lenrate,from_cross_lenrate);
		//logger.debug("atanRegian:" + atanRegian);
		
		//矢印の三角形の二点を求める
		PointOp ptOp = new PointOp(fact.createPoint(from_point));
		Point triangle_point1 = ptOp.rotatePoint(crossRotatePoint,-atanRegian);
		Point triangle_point2 = ptOp.rotatePoint(crossRotatePoint,atanRegian);
		
		//矢印を作成
		return fact.createPolygon(new Coordinate[]{
				from_point,
				triangle_point1.getCoordinate(),
				cross_point.getCoordinate(),
				to_point,
				cross_point.getCoordinate(),
				triangle_point2.getCoordinate(),
				from_point
		});
	}
	
	/**
	 * 点1,点2を通る直線に点Pからの垂線の足の座標を求める
	 * @param pointP 点P
	 * @return 結果が格納されたハッシュマップ。
	 * キーはString値で、値はObject型なので使用する場合は型変換すること。
	 * （１）bonline：(boolean)点(x,y）が線分点１-点2上の点か？
	 * （２）cloestPoint：(string)線分上の最近点のWKT
	 * （３）bearing：(double)点１、点２を通る線分の角度（-89度～90度）
	 */
	public HashMap<String,Object> getClosestPoint(
			Point pointP
			){
		
		//点1と点2が格納された2点のみのライン
		LineString baseLine = (LineString) targetLine;
		
		//点1
		Point point1 = baseLine.getPointN(0);
		//点2
		Point point2 = baseLine.getPointN(baseLine.getNumPoints() - 1);
		
		//入力ジオメトリから座標値を取得する
		double x1 = point1.getCoordinate().x;
		double y1 = point1.getCoordinate().y;
		double x2 = point2.getCoordinate().x;
		double y2 = point2.getCoordinate().y;
		double xp = pointP.getCoordinate().x;
		double yp = pointP.getCoordinate().y;
		
		//点１、点２を通る直線のパラメータを算出(ax+by+c=0)
		double a = y2 - y1;
		double b = x1 - x2;
		double c = -y1 * b - x1 * a;
		
		//距離を算出 (line = a * xp + b * yp + c 、pita = a^2 + b^2 ⇒ dist = |line|/√pita)
		double line = a * xp + b * yp + c;
		double pita = a * a + b * b;
		
		double x = 0;
		double y = 0;
		Point cloestPoint = null;
		if (pita > 0){
			//直線上で点Pから一番近い点を算出
			double lpp = line / pita;
			x = xp - a * lpp;
			y = yp - b * lpp;
			
			GeometryFactory fact = new GeometryFactory(point1.getPrecisionModel());
			cloestPoint = fact.createPoint(new Coordinate(x,y));
		}else{
			//点1、点2が等しいとき（線にならないとき）
			cloestPoint = point1;
		}
		
		//点１、点２を通る線分の角度（-89度～90度）を算出する
		double bearing = getLineAngle(0);
		
		//点(x,y）が線分点１-点2上の点か？
		boolean bonline = true;
		if (x1 < x2){
			if (x < x1 || x > x2){
				bonline = false;
			}
		}else{
			if (x < x2 || x > x1){
				bonline = false;
			}
		}
		if (bonline){
			if (y1 < y2){
				if (y < y1 || y > y2){
					bonline = false;
				}
			}else{
				if (y < y2 || y > y1){
					bonline = false;
				}
			}
		}
		
		HashMap<String,Object> res = new HashMap<String,Object>();
		res.put("bonline", bonline);
		res.put("cloestPoint", cloestPoint.toText());
		res.put("bearing", bearing);
		return res;
		
	}
	
	/**
	 * 点1,点2を通る線分の角度（-89度～90度）を求める
	 * @param kind 返却値種別
	 * 0:東を基準に、反時計回り；-89～90度
	 * 1:東を基準に、反時計回り；-π～πラジアン
	 * 2:北を基準に、時計回り  ；0～360度
	 * 3:北を基準に、時計回り  ；0～2πラジアン
	 * @return 点１、点２を通る線分の角度（-89度～90度）
	 */
	public Double getLineAngle(
			Integer kind
			){
		
		//点1と点2が格納された2点のみのライン
		LineString baseLine = (LineString) targetLine;
				
		//点1
		Point point1 = baseLine.getPointN(0);
		//点2
		Point point2 = baseLine.getPointN(baseLine.getNumPoints() - 1);
		
		//入力ジオメトリから座標値を取得する
		//点1
		double x1 = point1.getCoordinate().x;
		double y1 = point1.getCoordinate().y;
		//点2
		double x2 = point2.getCoordinate().x;
		double y2 = point2.getCoordinate().y;
		
		//点１、点２を通る直線のパラメータを算出(ax+by+c=0)
		double a = y2 - y1;
		double b = x1 - x2;
		//double c = -y1 * b - x1 * a;
		
		double bearing;
		double tilt;
		if (kind == 0){
			if (b >= -0.00000005 && b <= 0.00000005){
				bearing = 90;
			}else{
				tilt = -1 * a / b;
				bearing = Math.atan(tilt);
				bearing = Math.round(Util.radsToDegrees(bearing));
				if (bearing == -90){
					bearing = 90;
				}
			}
		}else{
			if (b >= -0.00000005 && b <= -0.00000005){
				if (a > 0){
					bearing = Util.degreesToRads(-90);
				}else{
					bearing = Util.degreesToRads(90);
				}
			}else{
				tilt = -1 * a / b;
				bearing = Math.atan(tilt);
				if (x1 > x2){
					bearing = Util.calcBearing(bearing, 180.0, 0);
				}
			}
			if (kind == 2 || kind == 3){
				bearing = Util.convertBearing(bearing,"RADIAN",0);
				if (kind == 2){
					bearing = Math.round(Util.radsToDegrees(bearing));
				}
			}
		}
		return bearing;
	}
	
	/**
	 * 点1,点2を通る直線と点1,点2の中点で垂直に交わる線分を作成する
	 * @param lineLength 線分の長さ
	 * @param direction 線分を作成する方向(0:両方、1:点1から点2に向かって右側、2:点1から点2に向かって左側）
	 * @return 点1,点2を通る直線と点1,点2の中点で垂直に交わる線分
	 */
	public LineString getVerticalMiddlePointLine(
			Double lineLength,
			Integer direction
			){
		
		//点1,点2を通る直線
		LineString baseLine = (LineString) targetLine;
		//点1
		Point point1 = baseLine.getPointN(0);
		//点2
		Point point2 = baseLine.getPointN(baseLine.getNumPoints() - 1);
		
		//入力ジオメトリから座標値を取得する
		//点1
		double x1 = point1.getX();
		double y1 = point1.getY();
		//点2
		double x2 = point2.getX();
		double y2 = point2.getY();
		
		//点1、点2の中点（点C）の座標を算出する
		double xc = (x1 + x2) / 2;
		double yc = (y1 + y2) / 2;
		
		//点1、点2を点Cを中心に90度回転させたときの座標を取得する
		double rad_angle = Util.degreesToRads(90);
		double sinrad = Math.sin(rad_angle);
		double cosrad = Math.cos(rad_angle);
		
		//点１から点２に向かって右側
		double x1d = xc + (x1 - xc) * cosrad - (y1 - yc) * sinrad;;
		double y1d = yc + (x1 - xc) * sinrad + (y1 - yc) * cosrad;
		
		//点１から点２に向かって左側
		double x2d = xc + (x2 - xc) * cosrad - (y2 - yc) * sinrad;
		double y2d = yc + (x2 - xc) * sinrad + (y2 - yc) * cosrad;
		
		GeometryFactory fact = new GeometryFactory(point1.getPrecisionModel());
		
		//点1'から点2'に向かう方位(を北を基準に時計回り)を算出する
		Point point1d = fact.createPoint(new Coordinate(x1d,y1d));
		Point point2d = fact.createPoint(new Coordinate(x2d,y2d));
		LineStringOp wk_lineOp = new LineStringOp(fact.createLineString(new Coordinate[]{
				point1d.getCoordinate(),point2d.getCoordinate()
		}));
		double bearing = wk_lineOp.getLineAngle(3);
		
		//指定の長さの線分を作成する
		//点C（基点）のジオメトリを作成する
		Point pointc = fact.createPoint(new Coordinate(xc,yc));
		
		//点１から点２に向かって右側
		PointOp wk_ptOp = new PointOp(pointc);
		if (direction == 0 || direction == 1){
			//点Cを基準に、指定された距離linelength、方位bearing+πの点を求める
			point1d = wk_ptOp.pointAtBearing(Util.calcBearing(bearing, 180.0, 0), lineLength);
		}
		
		//点１から点２に向かって左側
		if (direction == 0 || direction == 2){
			//点Cを基準に、指定された距離linelength、方位bearingの点を求める
			point2d = wk_ptOp.pointAtBearing(bearing, lineLength);
		}
		
		//点1'から点2'に向かう線分(direction=0)
		LineString return_line = fact.createLineString(new Coordinate[]{
				point1d.getCoordinate(),point2d.getCoordinate()
		});
		
		if (direction == 1 || direction == 2){
			//片側だけに線分を作成する場合、中点の正確な座標を求め直す
			LineString line = fact.createLineString(new Coordinate[]{
					point1.getCoordinate(),point2.getCoordinate()
			});
			Point pointcd = (Point) line.intersection(return_line);
			if (!pointcd.isEmpty()){
				pointc = pointcd;
			}
			if (direction == 1){
				//点Cから点1'に向かう線分
				return_line = fact.createLineString(new Coordinate[]{
						pointc.getCoordinate(),point1d.getCoordinate()
				});
			}else{
				//点Cから点2'に向かう線分
				return_line = fact.createLineString(new Coordinate[]{
						pointc.getCoordinate(),point2d.getCoordinate()
				});
			}
		}
		return return_line;
	}
	
	/**
	 * マルチラインをポリゴンに変換する
	 * @return 変換後のポリゴン
	 */
	@SuppressWarnings("unchecked")
	public Polygon lineToPolygon(){
		ArrayList<LineString> lines = new ArrayList<LineString>();
		for (int i = 0; i < targetLine.getNumGeometries();i++){
			LineString line = (LineString) targetLine.getGeometryN(i);
			lines.add(line);
		}
		//マルチラインを一つにマージ
		LineMerger lineMerger = new LineMerger();
	    lineMerger.add(lines);
	    ArrayList<LineString> mergedLineStrings = (ArrayList<LineString>) lineMerger.getMergedLineStrings();
	    
	    //マージしたLineStringをLinearRingに変換
	    GeometryFactory fact = new GeometryFactory(targetLine.getPrecisionModel());
	    LinearRing shell = fact.createLinearRing(mergedLineStrings.get(0).getCoordinates());
	    //LinearRingからPolygonに変換
	    Polygon polygon = fact.createPolygon(shell);
	    return polygon;
	}
	
}

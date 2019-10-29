package ke.co.narwassco.geometryOp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;

/**
 * Operation for Point object
 * @author Jin Igarashi
 * @version 1.0
 */
public class PointOp {
	//private final Logger logger = Logger.getLogger(PointOp.class);
	
	/**
	 * 対象となるポイントジオメトリ
	 */
	private Geometry targetPoint = null;
	
	/**
	 * コンストラクタ（Geometry版）
	 * @param point PointまたはMultiPointジオメトリ
	 */
	public PointOp(Geometry point){
		targetPoint = point;
	}
	
	/**
	 * コンストラクタ（WKT版）
	 * @param wkt Well-Known-Text
	 * @throws ParseException
	 */
	public PointOp(String wkt) throws ParseException{
		targetPoint = Util.createGeometryFromWkt(wkt);
	}
	
	/**
	 * 文字列枠のポリゴンジオメトリを返却する
	 * @param height 文字列枠の高さ（単位：m）
	 * @param width 文字列枠の幅（単位：m）
	 * @return 文字列枠のポリゴンジオメトリ
	 */
	public Polygon getStringBounds(
			Double height,
			Double width
			){
		
		//文字列枠の中心点
		Point center = (Point) targetPoint;
		
		//πの値
		double pi = Util.degreesToRads(180);
		
		//文字列の幅、高さより角度0のときの文字列を囲む矩形の座標を算出する
		
		//左辺の経度（中心点から西方向に、幅/2の距離にある点のX座標）
		double x1 = pointAtBearing(pi * 3 / 2, width / 2).getX();
		
		//底辺の緯度（中心点から南方向に、高さ/2の距離にある点のX座標）
		double y1 = pointAtBearing(pi, height / 2).getY();
		
		//右辺の経度（中心点から東方向に、幅/2の距離にある点のX座標）
		double x2 = pointAtBearing(pi / 2,width / 2).getX();
		
		//上辺の緯度（中心点から北方向に、高さ/2の距離にある点のX座標）
		double y2 = pointAtBearing(0.0, height / 2).getY();
		
		//算出した緯度経度から矩形のジオメトリを生成する
		GeometryFactory fact = new GeometryFactory(center.getPrecisionModel());
		return fact.createPolygon(new Coordinate[]{
				new Coordinate(x1,y1),
				new Coordinate(x2,y1),
				new Coordinate(x2,y2),
				new Coordinate(x1,y2),
				new Coordinate(x1,y1)
		});
	}
	
	/**
	 * 開始点を基準として指定された距離および方位に存在する点ジオメトリを戻します。
	 * @param bearing 北を基準として時計回りに測定されるラジアンの数値を指定します。-piからpiか、または0から2×piの範囲で指定する必要があります。（いずれの表記でも指定できます。）
	 * @param distance start_pointから初期の方位方向への計算の終了点までの数値（m）を指定します。地球の円周の半分より小さい値を指定する必要があります。
	 * @return 指定距離および方位に基準点をずらした点ジオメトリ
	 */
	public Point pointAtBearing(
			Double bearing,
			Double distance
			){
		
		//必要な点を配置するために、指定された方位の距離計算の開始点となる点ジオメトリ・オブジェクトを指定します。
		Point start_point = (Point) targetPoint;
		
		final double mATN1P45  = Math.atan(1)/45;
		final double ER = 6378.14;
		
		double Topx,Topy;
		
		double Mypx = start_point.getX();
		double Mypy = start_point.getY();
		double distanceKM = distance / 1000;
		
		double cb,sb,clc,c;
		double xx,ido,kdo;
		ido = (90 - Mypy) * (Math.atan(1) * 4) / 180;
		c = distanceKM / ER;
		if (c > (Math.atan(1) * 4) * 2){
			c = c - (Math.atan(1) * 4) * 2;
		}
		cb = Math.cos(c) * Math.cos(ido) + Math.sin(c) * Math.sin(ido) * Math.cos(bearing * mATN1P45);
		if (cb == 1){
			sb = 0;
		}else{
			sb = Math.sqrt(1 - cb * cb);
		}
		if (Math.sin(ido) * sb == 0){
			clc = 0;
		}else{
			clc = (Math.cos(c) - Math.cos(ido) * cb) / (Math.sin(ido) * sb);
			if (clc > 1){
				clc = 1;
			}else if (clc < -1){
				clc = -1;
			}
		}
		xx = cb;
		if (Math.sqrt(-xx * xx + 1) == 0){
			ido = Math.atan(1) * 2;
		}else{
			ido = Math.atan(-xx / Math.sqrt(-xx * xx + 1)) + 2 * Math.atan(1);
		}
		Topy = 90 - ido * 180 / (Math.atan(1) * 4);
		xx = clc;
		if (xx == 1){
			kdo = 0;
		}else if (xx == -1){
			kdo = Math.atan(1) * 4;
		}else{
			if (Math.sin(bearing * mATN1P45) >= 0){
				kdo = Math.atan(-xx / Math.sqrt(-xx * xx + 1)) + 2 * Math.atan(1);
			}else{
				kdo = -(Math.atan(-xx / Math.sqrt(-xx * xx + 1)) + 2 * Math.atan(1));
			}
		}
		if (c > Math.atan(1) * 4 ){
			kdo = -kdo;
		}
		kdo = kdo * 180 / (Math.atan(1) * 4);
		Topx = Mypx + kdo;
		if (Topx > 180){
			Topx = Topx -360;
		}else if (Topx < -180){
			Topx = Topx + 360;
		}
		
		GeometryFactory fact = new GeometryFactory(start_point.getPrecisionModel());
		return fact.createPoint(new Coordinate(Topx,Topy));
	}
	
	/**
	 * 回転基準点からの角度分、回転対象のポイントをずらして座標を求める
	 * @param basePoint 回転基準となるポイント
	 * @param rotatePoint 回転する対象のポイント
	 * @param angle 角度(単位：ラジアン)
	 * @return 回転後のポイント
	 */
	public Point rotatePoint(
			Point rotatePoint,
			Double angle
			){
		
		//回転基準となるポイント
		Point basePoint = (Point) this.targetPoint;
		
		/*logger.debug("rotatePoint start.");
		logger.debug("basePoint:" + basePoint.toText());
		logger.debug("targetPoint:" + targetPoint.toText());
		logger.debug("angle:" + angle);*/
		
		//回転基準点の座標を取得
		double baseX = basePoint.getCoordinate().x;
		double baseY = basePoint.getCoordinate().y;
		
		//回転対象点の座標を取得
		double targetX = rotatePoint.getCoordinate().x;
		double targetY = rotatePoint.getCoordinate().y;
		
		//ラジアンからサインとコサインを計算
		double sinrad = Math.sin(angle);
		double cosrad = Math.cos(angle);
		
		//回転後のXY座標を計算
		double resultX = baseX + (targetX - baseX) * cosrad - (targetY - baseY) * sinrad;
		double resultY = baseY + (targetX - baseX) * sinrad + (targetY - baseY) * cosrad;
		
		GeometryFactory fact = new GeometryFactory(basePoint.getPrecisionModel());
		Point resultP = fact.createPoint(new Coordinate(resultX,resultY));
		/*logger.debug("resultP:" + resultP.toText());*/
		return resultP;
	}
	
	/**
	 * 文字列枠（矩形）を中心点を起点に指定角度だけ回転させた図形を作成する
	 * @param center 中心点
	 * @param rect 文字列枠
	 * @param degree_angle 角度(単位：度)
	 * @return 矩形の４点の座標を中心を起点に指定角度分、回転させたジオメトリ
	 */
	public Polygon rotateRect(
			Polygon rect,
			Double degree_angle
			){
		
		//中心点
		Point center = (Point) targetPoint;
		
		//中心の座標を取得
		double xc = center.getCoordinate().x;
		double yc = center.getCoordinate().y;
		
		//角度をラジアンに変換
		Double rad_angle = Util.degreesToRads(degree_angle);
		
		//ラジアンからサインとコサインを計算
		double sinrad = Math.sin(rad_angle);
		double cosrad = Math.cos(rad_angle);
		
		Coordinate[] newRectCoords = new Coordinate[rect.getNumPoints()];
		//矩形の座標点でループ
		for (int i = 0; i < rect.getNumPoints();i++){
			//矩形座標点を取り出す
			Coordinate wrk_point = rect.getCoordinates()[i];
			double x1 = wrk_point.x;
			double y1 = wrk_point.y;
			
			//中心点とサイン・コサインから回転させた座標を計算
			double x2 = xc + (x1 - xc) * cosrad - (y1 - yc) * sinrad;
			double y2 = yc + (x1 - xc) * sinrad + (y1 - yc) * cosrad;
			
			newRectCoords[i] = new Coordinate(x2,y2);
		}
		
		//算出した座標をもとに、ポリゴンジオメトリを作成し、返却する
		GeometryFactory fact = new GeometryFactory(rect.getPrecisionModel());
		return fact.createPolygon(newRectCoords);
	}
	
}

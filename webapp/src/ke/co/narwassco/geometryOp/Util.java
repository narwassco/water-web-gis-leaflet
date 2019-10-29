package ke.co.narwassco.geometryOp;

import java.math.BigDecimal;

import ke.co.narwassco.common.ServletListener;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Operation for Util function
 * @author Jin Igarashi
 * @version 1.0
 */
public class Util {

	/**
	 * 基準となる方位に指定角度分回転した時の方位（単位：ラジアン）を返却する
	 * @param bearing 基準となる方位(単位：ラジアン)
	 * @param addBearing 加算する方位(単位：度)
	 * @param unitType 結果の単位（0:ラジアン、1:度）
	 * @return 単位：ラジアン
	 */
	public static double calcBearing(
			Double bearing,
			Double addBearing,
			Integer unitType
			){
		
		//2πの値
		double pi2 = degreesToRads(360);
		
		//加算する方位をラジアンに変換する
		double addValue = degreesToRads(addBearing);
		
		//方位を加算する
		double result = bearing + addValue;
		
		//0～2πになるように調整する
		while(true){
			if (result < 0){
				result = result + pi2;
			}else if (result > pi2){
				result = result - pi2;
			}else{
				break;
			}
		}
		
		if (unitType == 1){
			result = Math.round(radsToDegrees(result));
		}
		return result;
	}
	
	/**
	 * 東を基準として反時計回りに測定される度の数値を北を基準として時計回りに測定されるラジアンの数値に変換する
	 * オプション：逆の変換で、-89～90度に丸めた値を返却する
	 * @param bearing 変換元の方位
	 * @param unit 変換元の方位の単位('DEGREE' or 'RADIAN')
	 * @param kind 種別（0:東→北、1:北→東）
	 * @return
	 */
	public static double convertBearing(
			Double bearing,
			String unit,
			Integer kind
			){

		//単位が度の場合、ラジアンに変換する
		double b = bearing;
		if (unit == "DEGREE"){
			b = degreesToRads(bearing);
		}
		
		//2πの値
		double pi2 = degreesToRads(360.0);
		
		//方位を変換する
		double result;
		if (kind == 0){
			//北を基準として時計回りに測定されるラジアンの数値に変換する
			result = pi2 / 4 - b;
			//0～2πになるように調整する
			while(true){
				if (result < 0 ){
					result += pi2;
				}else if (result > pi2){
					result -= pi2;
				}else{
					break;
				}
			}
		}else{
			//ラジアン→度
			result = Math.round(radsToDegrees(b));
			//東を基準として反時計回りに測定される度の数値に変換する
			result = 450 - result;
			
			//-89～90になるように調整する
			while(true){
				if (result > 90){
					result -= 180;
				}else if (result < -89){
					result += 180;
				}else{
					break;
				}
			}
		}
		return result;
	}
	
	/**
     * ラジアンから度に変換します。
     * 
     * @param radian
     * @return
     */
    public static double radsToDegrees(double radian) {
        return radian * (180f / Math.PI);
    }

    /**
     * 度からラジアンに変換します。
     * 
     * @param degrees
     * @return
     */
    public static double degreesToRads(double degrees) {
        return degrees * (Math.PI / 180f);
    }
	
    /**
	 * 精度（小数点第8位）を設定したジオメトリをWKTから生成して返却
	 * @param wkt Well-Known-Text
	 * @return ジオメトリ
	 * @throws ParseException
	 */
	public static Geometry createGeometryFromWkt(
			String wkt) throws ParseException{
		
		//double scale = 8;
		//分割時に小数点が丸められることで不具合が出たため精度は指定しない
		//PrecisionModel pm = new PrecisionModel(Math.pow(10.0, scale));
		PrecisionModel pm = new PrecisionModel();
		
		GeometryFactory fact = new GeometryFactory(pm);
		WKTReader wktRdr = new WKTReader(fact);
		Geometry geom = wktRdr.read(wkt);
		geom.setSRID(Integer.valueOf(ServletListener.epsg));
		
		return geom;
	}
    
    /**
	 * ジオメトリの座標点の小数点以下を指定精度で切り捨てる
	 * @param targetGeom 対象ジオメトリ
	 * @param digit 指定精度
	 * @return 切り捨て後のジオメトリ
	 */
	public static Geometry roundGeometryCoordinates(
			Geometry targetGeom,
			Integer digit
			){
		for (Coordinate coord : targetGeom.getCoordinates()){
			BigDecimal x = new BigDecimal(coord.x);
			BigDecimal y = new BigDecimal(coord.y);
			
			coord.x = x.setScale(digit, BigDecimal.ROUND_DOWN).doubleValue();
			coord.y = y.setScale(digit, BigDecimal.ROUND_DOWN).doubleValue();
		}
		return targetGeom;
	}
	
	/**
	 * 指定図形が有効な図形かチェックする
	 * @param p_fig 対象のGeometryオブジェクト
	 * @return RetCodeクラスの定数(0:正常終了、2:筆図形が複合図形で構成されている、3:筆図形がポリゴンでない)
	 */
	public static int validatePlace(Geometry p_fig){
		
		int num_element = p_fig.getNumGeometries();
		//ポリゴン図形１個以外はエラーにする
		if (num_element == 1){
			if (!(p_fig instanceof Polygon)){
				//図形が単純ポリゴンでない場合
				return RetCode.PLACE_NOTPOLYGON;
			}
		}else{
			//ジオメトリが複数図形で構成されている場合
			return RetCode.PLACE_MULTIPOLYGON;
		}
		return RetCode.SUCCESS;
	}
}

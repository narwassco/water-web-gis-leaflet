package ke.co.narwassco.geometryOp;

import java.util.HashMap;

/**
 * Manage for Code Setting
 * @author Jin Igarashi
 * @version 1.0
 */
public class RetCode {

	/**
	 * 正常終了
	 */
	public static final int SUCCESS = 0;
	
	/**
	 * システムエラー
	 */
	public static final int ERROR = 99;
	
	/**
	 * 筆図形が複合図形で構成されている
	 */
	public static final int PLACE_MULTIPOLYGON = 2;
	
	/**
	 * 筆図形がポリゴンでない
	 */
	public static final int PLACE_NOTPOLYGON = 3;
	
	/**
	 * 筆情報に格納されている図形と分割ラインが２点で交差していません。
	 */
	public static final int PLACE_INTERSECTERROR = 4;
	
	/**
	 * 分割ラインが指定筆の内部にありません。
	 */
	public static final int SPLIT_LINE_INTERSECTION_ERROR = 5;
	
	/**
	 * 分割ラインの指定に誤りがあります。
	 */
	public static final int SPLIT_LINE_SET_ERROR = 6;
	
	/**
	 * 分割線が筆の頂点の近傍と交差しているため、正しく分割できませんでした。
	 */
	public static final int SPLIT_LINE_OPE_ERROR = 7;
	
	/**
	 * 新点補正候補の筆界線が補正範囲内に存在しません。
	 */
	public static final int CORRECT_TARGET_PLACE_NOTEXISTS_ERROR = 8;
	
	/**
	 * 筆界線が筆図形と交差していません。
	 */
	public static final int SPLIT_LINE_NOT_INTERSECTS_POLYGON = 9;
	
	/**
	 * 余剰な筆界線を検知しました。
	 */
	public static final int EXISTS_EXCESS_LINE_ERROR = 10;
	
	/**
	 * 指定コードのメッセージを返却する。
	 * @param code リターンコード
	 * @return メッセージ定義がある場合は返却。ない場合は空文字列を返却。
	 */
	public static String getMessage(int code){
		HashMap<Integer,String> messageMap = new HashMap<Integer,String>();
		messageMap.put(RetCode.SUCCESS, "");
		messageMap.put(RetCode.ERROR, "システムエラー");
		messageMap.put(RetCode.PLACE_MULTIPOLYGON, "筆図形が複合図形で構成されています。");
		messageMap.put(RetCode.PLACE_NOTPOLYGON, "筆図形がポリゴンではありません。");
		messageMap.put(RetCode.PLACE_INTERSECTERROR, "筆情報に格納されている図形と分割ラインが２点で交差していません。");
		messageMap.put(RetCode.SPLIT_LINE_INTERSECTION_ERROR, "分割ラインが指定筆の内部にありません。");
		messageMap.put(RetCode.SPLIT_LINE_SET_ERROR, "分割ラインの指定に誤りがあります。");
		messageMap.put(RetCode.SPLIT_LINE_OPE_ERROR, "分割線が筆の頂点の近傍と交差しているため、正しく分割できませんでした。");
		messageMap.put(RetCode.CORRECT_TARGET_PLACE_NOTEXISTS_ERROR, "新点補正候補の筆界線が補正範囲内に存在しません。");
		messageMap.put(RetCode.SPLIT_LINE_NOT_INTERSECTS_POLYGON, "筆界線が筆図形と交差していません。");
		messageMap.put(RetCode.EXISTS_EXCESS_LINE_ERROR, "余剰な筆界線を検知しました。");
		
		if (messageMap.containsKey(code)){
			return messageMap.get(code);
		}else{
			return "";
		}
	}
}

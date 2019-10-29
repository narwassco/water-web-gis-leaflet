package ke.co.narwassco.pdf;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContext;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

import net.arnx.jsonic.JSON;

/**
 * <pre>
 *  クラス名  ：PdfSetting
 *  クラス説明：PDFの設定情報を管理するクラス
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfSetting {

	private String exportPath = "";

	/**
	 * 出力先フォルダを取得
	 * @return
	 */
	public String getExportPath(){
		return exportPath;
	}

	private String exportRealPath = "";

	/**
	 * 出力先フォルダの実際のパスを取得
	 * @return
	 */
	public String getExportRealPath(){
		return exportRealPath;
	}


	private String jsonLayoutPath ="";

	/**
	 * PDFのJSONレイアウト定義ファイルパスを取得
	 * @return
	 */
	public String getJsonLayoutPath(){
		return jsonLayoutPath;
	}

	private String mapfile = "";

	/**
	 * Mapfileパスを取得
	 * @return
	 */
	public String getMapfile(){
		return mapfile;
	}

	private String layers = "";

	/**
	 * 表示するレイヤ名を取得
	 * @return
	 */
	public String getLayers(){
		return layers;
	}

	private Rectangle pageSize = PageSize.A4;
	public Rectangle getPageSize(){
		return pageSize;
	}

	private Integer mapWidth = 0;
	public Integer getMapWidth(){
		return this.mapWidth;
	}

	private Integer mapHeight = 0;
	public Integer getMapHeight(){
		return  this.mapHeight;
	}

	/**
	 * コンストラクタ
	 * @param config JSON設定定義
	 */
	public PdfSetting(String config,ServletContext sc){
		HashMap<String,String> map = JSON.decode(config);

		exportPath = map.get("exportPath");
		exportRealPath = sc.getRealPath(exportPath);
		//地図出力用のフォルダがなかったら作成する
		File exportFile = new File(exportRealPath);
		if (!exportFile.exists()){
			exportFile.mkdir();
		}

		mapfile = map.get("mapfile");
		layers = map.get("layers");
		jsonLayoutPath = sc.getRealPath(map.get("jsonLayoutPath"));

		/*String size = map.get("pageSize");
		if (size.equals("A4_LANDSCAPE")){
			pageSize = PageSize.A4_LANDSCAPE;
		}else if(size.equals("A3")){
			pageSize = PageSize.A3;
		}*/

		this.mapWidth =Integer.valueOf(map.get("mapWidth"));
		this.mapHeight = Integer.valueOf(map.get("mapHeight"));

	}

}

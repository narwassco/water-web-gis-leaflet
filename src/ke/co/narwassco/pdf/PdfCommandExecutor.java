package ke.co.narwassco.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import net.arnx.jsonic.JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * <pre>
 *  クラス名  ：PdfCommandExecutor
 *  クラス説明：コマンド定義ファイルを実行しPDFを作成する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfCommandExecutor {
	private final Logger logger = LogManager.getLogger(PdfCommandExecutor.class);

	private PdfContentByte cb;
	private String FileName = "";
	private String LayoutFilePath = "";
	private Rectangle PageSize = null;
	private HashMap<String,String> MapParams = null;

	private PdfCmdBase[] CommandObjArray = null;

	/**
	 * コンストラクタ
	 * @param filename 出力先ファイル名
	 * @param layoutfilepath レイアウト定義ファイルパス
	 * @param params 実行時に置き換えるパラメータ
	 */
	public PdfCommandExecutor(String filename,PdfSetting setting,HashMap<String,String> params){
		this.FileName = filename;
		this.LayoutFilePath = setting.getJsonLayoutPath();
		this.PageSize = setting.getPageSize();
		this.MapParams = params;

		//使用するコマンドオブジェクトを設定
		CommandObjArray = new PdfCmdBase[]{
				new PdfBeginText(),
				new PdfSetFontAndSize(),
				new PdfMoveText(),
				new PdfShowText(),
				new PdfShowTextAligned(),
				new PdfEndText(),
				new PdfAddImage(),
				new PdfMoveTo(),
				new PdfLineTo(),
				new PdfSetLineWidth(),
				new PdfStroke()};
	}

	/**
	 * PDF作成
	 * @throws Exception
	 */
	public void create() throws Exception{
		Document document = new Document(this.PageSize,0,0,0,0);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FileName));
			document.open();
			cb = writer.getDirectContent();

			String layouttext = readTextFile(LayoutFilePath);
			layouttext = replaceParam(layouttext);

			ArrayList<Object> params = JSON.decode(layouttext);
			for (Object prm:params){
				String jsonprm = JSON.encode(prm);
				draw(jsonprm);
			}

		}finally{
			if (document != null && document.isOpen()){
				document.close();
				document = null;
			}
		}
	}

	/**
	 * レイアウト定義ファイルの%で囲った文字列を置き換えて返す
	 * @param layouttext レイアウト定義
	 * @return 置き換え後の定義
	 */
	private String replaceParam(String layouttext){
		if (MapParams != null){
			//パラメータでhtml内の指定文字列を置き換え
			for (String key:MapParams.keySet()){
				layouttext = layouttext.replaceAll("%" + key + "%", MapParams.get(key));
			}
		}
		return layouttext;
	}

	/**
	 * 指定位置にPDFを描画する
	 * @param param JSON形式のパラメータ。実行可能コマンドについて以下に示す
	 * @throws Exception
	 */
	private void draw(String param) throws Exception{
		logger.debug("draw param:" + param);
		HashMap<String,String> map = JSON.decode(param);
		String method = map.get("method");

		PdfCmdBase execcmd = null;

		for (PdfCmdBase cmd:CommandObjArray){
			if (cmd.getMethodName().equals(method)){
				execcmd = cmd;
			}
		}

		execcmd.setCmdParams(map);
		execcmd.execute(cb);

	}

	/**
	 * テキストファイルを読み込む
	 * @param filename ファイル名
	 * @return 読みこんだ結果のテキスト
	 * @throws IOException
	 */
	private String readTextFile(String filename) throws IOException {
		logger.info("readTextFile start.");
		logger.debug("filename:" + filename);

		BufferedReader br = null;
		String resultString = "";
        try {
            // 入力元ファイル
            File file = new File(filename);

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            // １行づつ読み込みます。
            while ((line = br.readLine()) != null) {
            	resultString += line;
            }
        } finally {
        	// ストリームは必ず finally で close します。
        	if (br != null ){
        		br.close();
        	}
        }
        return resultString;
	}
}

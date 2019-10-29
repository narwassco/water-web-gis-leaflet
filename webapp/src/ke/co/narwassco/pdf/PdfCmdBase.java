package ke.co.narwassco.pdf;

import java.util.HashMap;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfCmdBase
 *  クラス説明：iTextのコマンド実行クラスのスーパークラス
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public abstract class PdfCmdBase {

	/**
	 * メソッド名
	 */
	private String MethodName = "";

	/**
	 * コマンドのパラメータ
	 */
	protected HashMap<String,String> CmdParams = null;

	/**
	 * メソッド名の取得
	 * @return メソッド名
	 */
	public String getMethodName(){
		return this.MethodName;
	}

	/**
	 * パラメーターの設定
	 * @param params コマンド名とパラメータが格納されたマップ
	 * <br>コマンド用のテキストファイルにはJSONのマップ形式で1コマンド1マップで記述すること。
	 * <br>各コマンドの詳細な設定の仕方はPdfCmdBaseを継承するサブクラスを参照すること。
	 * <br>設定例）
	 * <br>座標(288,51)までラインを引きたい場合は下記のように指定する。
	 * <br>{"method":"lineTo","x":"288","y":"51"}
	 */
	public void setCmdParams(HashMap<String,String> params){
		this.CmdParams = params;
	}

	/**
	 * コンストラクタ
	 * @param method メソッド名
	 */
	public PdfCmdBase(String method){
		this.MethodName = method;
	}

	/**
	 * コマンド実行
	 * @param cb 実行対象となるPdfContentByteオブジェクト
	 */
	public abstract void execute(PdfContentByte cb) throws Exception;

	/**
	 * ミリメートルをピクセルに変換
	 * @param mm ミリメートル
	 * @return ピクセル
	 */
	protected float mm2pixel(final float mm){
        //1インチ = 25.4 ミリメートル
        return (mm / 25.4f ) * 72.0f;
    }

	/**
	 * ピクセルをミリメートルに変換
	 * @param pixel ピクセル
	 * @return ミリメートル
	 */
	protected float pixel2mm(final float pixel){
        //1インチ = 25.4 ミリメートル
        return (pixel / 72.0f) * 25.4f;
    }

}

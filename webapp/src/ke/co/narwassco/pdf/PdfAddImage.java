package ke.co.narwassco.pdf;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfAddImage
 *  クラス説明：イメージを追加するコマンドを実行する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfAddImage extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfAddImage() {
		super("addImage");
	}

	/**
	 * イメージを追加する。
	 * コマンドの指定方法は以下の通り。
	 * <br>method:addImage
	 * <br>path:イメージファイルのパス
	 * <br>x:左下のX座標(ミリ)
	 * <br>y:左下のY座標（ミリ）
	 * <br>width:画像の幅（ミリ）
	 * <br>height:画像の高さ（ミリ）
	 * <br>scaleAbsoluteWidth:解像度の幅（ピクセル）
	 * <br>scaleAbsoluteHeight:解像度の高さ（ピクセル）
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		String path = CmdParams.get("path");
		Image img = Image.getInstance(path);
		/*Float scalepercent = Float.valueOf(CmdParams.get("scalepercent"));
		img.scalePercent(scalepercent);*/
		Float scaleAbsoluteWidth = Float.valueOf(CmdParams.get("scaleAbsoluteWidth"));
		Float scaleAbsoluteHeight = Float.valueOf(CmdParams.get("scaleAbsoluteHeight"));
		img.scaleAbsolute(scaleAbsoluteWidth, scaleAbsoluteHeight);

		Float x = Float.valueOf(CmdParams.get("x"));
		Float y = Float.valueOf(CmdParams.get("y"));
		Integer width = Integer.valueOf(CmdParams.get("width"));
		Integer height = Integer.valueOf(CmdParams.get("height"));
		cb.addImage(img, mm2pixel(width), 0, 0, mm2pixel(height), mm2pixel(x), mm2pixel(y));
	}

}

package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfLineTo
 *  クラス説明：指定位置までラインを引くコマンドを実行する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfLineTo extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfLineTo() {
		super("lineTo");
	}

	/**
	 * 指定座標まで線を引く。
	 * コマンドの指定方法は以下の通り。
	 * <br>method:lineTo
	 * <br>x:X座標(ミリ)
	 * <br>y:Y座標（ミリ）
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		Float x = Float.valueOf(CmdParams.get("x"));
		Float y = Float.valueOf(CmdParams.get("y"));
		cb.lineTo(mm2pixel(x), mm2pixel(y));
	}

}

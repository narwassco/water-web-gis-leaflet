package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfMoveTo
 *  クラス説明：ラインを引きたい開始位置を指定する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfMoveTo extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfMoveTo() {
		super("moveTo");
	}

	/**
	 * ラインを引きたい開始位置を指定する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:moveTo
	 * <br>x:X座標(ミリ)
	 * <br>y:Y座標（ミリ）
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		Float x = Float.valueOf(CmdParams.get("x"));
		Float y = Float.valueOf(CmdParams.get("y"));
		cb.moveTo(mm2pixel(x), mm2pixel(y));
	}

}

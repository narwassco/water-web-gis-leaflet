package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfMoveText
 *  クラス説明：テキストを入力したい座標位置を指定する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfMoveText extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfMoveText() {
		super("moveText");
	}

	/**
	 * テキストの描画位置を指定する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:moveText
	 * <br>x:左下のX座標(ミリ)
	 * <br>y:左下のY座標（ミリ）
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		Float x = Float.valueOf(CmdParams.get("x"));
		Float y = Float.valueOf(CmdParams.get("y"));
		cb.moveText(mm2pixel(x), mm2pixel(y));
	}

}

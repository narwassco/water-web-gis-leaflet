package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfShowTextAligned
 *  クラス説明：指定テキストを指定位置、指定スタイルで描画する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfShowTextAligned extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfShowTextAligned() {
		super("showTextAligned");
	}

	/**
	 * 指定テキストを指定位置、指定スタイルで描画する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:showTextAligned
	 * <br>text:表示テキスト
	 * <br>x:X座標(ミリ)
	 * <br>y:Y座標（ミリ）
	 * <br>rotation:角度
	 * <br>align:表示位置。Center:中央、Right:右、Left及びそれ以外：左
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		String text = CmdParams.get("text");
		Float x = Float.valueOf(CmdParams.get("x"));
		Float y = Float.valueOf(CmdParams.get("y"));
		Float rotation = Float.valueOf(CmdParams.get("rotation"));
		String align = CmdParams.get("align");
		Integer iTextAlign = PdfContentByte.ALIGN_LEFT;
		if (align.equals("Center")){
			iTextAlign = PdfContentByte.ALIGN_CENTER;
		}else if (align.equals("Right")){
			iTextAlign = PdfContentByte.ALIGN_RIGHT;
		}
		cb.showTextAligned(iTextAlign, text, mm2pixel(x), mm2pixel(y), rotation);
	}

}

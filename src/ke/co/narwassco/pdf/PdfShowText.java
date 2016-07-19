package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfShowText
 *  クラス説明：指定テキストを描画する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfShowText extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfShowText() {
		super("showText");
	}

	/**
	 * 指定テキストを描画する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:showText
	 * <br>text:表示テキスト
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		String text = CmdParams.get("text");
		cb.showText(text);
	}

}

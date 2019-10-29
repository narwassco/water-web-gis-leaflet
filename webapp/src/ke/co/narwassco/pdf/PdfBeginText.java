/**
 *
 */
package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfBeginText
 *  クラス説明：テキストの入力開始コマンドを実行する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfBeginText extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfBeginText() {
		super("beginText");
	}

	/**
	 * テキストの描画を開始する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:beginText
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		cb.beginText();
	}

}

package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfEndText
 *  クラス説明：テキスト入力を終了するコマンドを実行する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfEndText extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfEndText() {
		super("endText");
	}

	/**
	 * テキストの描画を終了する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:endText
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		cb.endText();
	}

}

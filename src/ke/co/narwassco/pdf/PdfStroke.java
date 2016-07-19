package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfStroke
 *  クラス説明：PDF上に描くように指定した図形をまとめて描画する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfStroke extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfStroke() {
		super("stroke");
	}

	/**
	 * PDF上に描くように指定した図形をまとめて描画する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:stroke
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		cb.stroke();
	}

}

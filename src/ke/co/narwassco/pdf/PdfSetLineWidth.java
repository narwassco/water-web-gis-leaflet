package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfSetLineWidth
 *  クラス説明：ラインの幅を指定する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfSetLineWidth extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfSetLineWidth() {
		super("setLineWidth");
	}

	/**
	 * ラインの幅を指定する
	 * コマンドの指定方法は以下の通り。
	 * <br>method:setLineWidth
	 * <br>linewidth:ライン幅
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		Float linewidth = Float.valueOf(CmdParams.get("linewidth"));
		cb.setLineWidth(linewidth);
	}

}

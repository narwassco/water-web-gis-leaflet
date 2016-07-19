package ke.co.narwassco.pdf;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

/**
 * <pre>
 *  クラス名  ：PdfSetFontAndSize
 *  クラス説明：iTextの使用フォント及びサイズを指定する
 * </pre>
 *
 * @version 1.00
 * @author Igarashi
 *
 */
public class PdfSetFontAndSize extends PdfCmdBase {

	/**
	 * コンストラクタ
	 */
	public PdfSetFontAndSize() {
		super("setFontAndSize");
	}

	/**
	 * iTextの使用フォント及びサイズを指定する。
	 * コマンドの指定方法は以下の通り。
	 * <br>method:setFontAndSize
	 * <br>fontname:フォント名
	 * <br>   HeiseiKakuGo-W5；ゴシック体
	 * <br>   HeiseiMin-W3:明朝体
	 * <br>fontencoding:文字コードの種別。
	 * <br>   UniJIS-UCS2-H:Adobe日本語文字のUniCode用エンコーディング
	 * <br>   UniJIS-UCS2-V:UniJIS-UCS2-Hの縦書きエンコーディング
	 * <br>   UniJIS-UCS2-HW-H:UniJIS-UCS2-Hのうち、プロポーショナル文字のみ半角文字に変更したエンコーディング
	 * <br>   UniJIS-UCS2-HW-V:UniJIS-UCS2-HW-Hの縦書きエンコーディング
	 * <br>fontsize:フォントサイズ(pt)
	 */
	@Override
	public void execute(PdfContentByte cb) throws Exception{
		String fontname = CmdParams.get("fontname");
		String fontencoding = CmdParams.get("fontencoding");
		Integer fontsize =Integer.valueOf(CmdParams.get("fontsize"));
		BaseFont bf = BaseFont.createFont();
		if (! fontname.equals("") == true && fontencoding.equals("") == true){
			bf = BaseFont.createFont(fontname, fontencoding, BaseFont.NOT_EMBEDDED);
		}
		cb.setFontAndSize(bf, fontsize);
	}

}

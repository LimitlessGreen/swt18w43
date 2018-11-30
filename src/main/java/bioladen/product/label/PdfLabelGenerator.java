package bioladen.product.label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

import bioladen.product.InventoryProduct;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;

public class PdfLabelGenerator {
	private static final float MM_TO_PT = 2.834645669291f;

	private static final float LABEL_WIDTH  = 70 * MM_TO_PT;
	private static final float LABEL_HEIGHT = 50 * MM_TO_PT;

	private static final float PRODUCT_NAME_POS_X = 95 * MM_TO_PT;
	private static final float PRODUCT_NAME_POS_Y = 43 * MM_TO_PT;


	public void generate(InventoryProduct inventoryProduct) {
		try {
			PDDocument document = new PDDocument();

			PDFont Roboto100  = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Thin.ttf"));
			PDFont Roboto100i = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-ThinItalic.ttf"));
			PDFont Roboto300  = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Light.ttf"));
			PDFont Roboto300i = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-LightItalic.ttf"));
			PDFont Roboto400  = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Regular.ttf"));
			PDFont Roboto400i = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Italic.ttf"));
			PDFont Roboto500  = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Medium.ttf"));
			PDFont Roboto500i = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-MediumItalic.ttf"));
			PDFont Roboto700  = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Bold.ttf"));
			PDFont Roboto700i = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-BoldItalic.ttf"));
			PDFont Roboto900  = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-Black.ttf"));
			PDFont Roboto900i = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto/Roboto-BlackItalic.ttf"));

			PDFont RobotoSlab100 = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto_Slab/RobotoSlab-Thin.ttf"));
			PDFont RobotoSlab300 = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto_Slab/RobotoSlab-Light.ttf"));
			PDFont RobotoSlab400 = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto_Slab/RobotoSlab-Regular.ttf"));
			PDFont RobotoSlab700 = PDType0Font.load(document,
					new File("/src/main/resources/static/pdffonts/Roboto_Slab/RobotoSlab-Bold.ttf"));

			PDPage labelPage = new PDPage(new PDRectangle(LABEL_WIDTH, LABEL_HEIGHT));
			document.addPage(labelPage);

			PDPageContentStream contentStream;

			contentStream = new PDPageContentStream(document, labelPage);

			//contentStream.beginText();

			//contentStream.setFont(Roboto100, 17);
			//contentStream.setLeading(23 * -4);
			//contentStream.showText("Rechnung");
			//contentStream.newLine();

			contentStream.close();

			//document.save("src/main/resources/static/bills/bill" + ".pdf");
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

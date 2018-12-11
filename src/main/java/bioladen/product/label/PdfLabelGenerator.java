package bioladen.product.label;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@RequiredArgsConstructor
public class PdfLabelGenerator {
	private final InventoryProductCatalog inventoryProductCatalog;

	private static final String BASE_PATH = "src/main/resources/generated/";

	private static final float MM_TO_PT = 2.834645669291f;

	private static final float LABEL_WIDTH  = 70 * MM_TO_PT;
	private static final float LABEL_HEIGHT = 50 * MM_TO_PT;

	private static final float LABEL_MARGIN = 2.5f * MM_TO_PT;

	private static final float PRODUCT_NAME_FONT_SIZE = 18;
	private static final float PRODUCT_NAME_POS_X     = LABEL_MARGIN; // 13 * MM_TO_PT;
	private static final float PRODUCT_NAME_POS_Y     = 43 * MM_TO_PT;

	private static final float PRODUCT_DESCRIPTION_FONT_SIZE     = 12;
	private static final float PRODUCT_DESCRIPTION_POS_X         = PRODUCT_NAME_POS_X + 1 * MM_TO_PT;
	private static final float PRODUCT_DESCRIPTION_POS_Y         = 38 * MM_TO_PT;
	private static final float PRODUCT_DESCRIPTION_MARGIN_BOTTOM =  2 * MM_TO_PT;

	private static final float PRODUCT_PRICE_POS_Y = 27 * MM_TO_PT;

	private static final float QR_CODE_SIZE = 20 * MM_TO_PT;

	private static final float BARCODE_FONT_SIZE        = 12;
	private static final float BARCODE_WIDTH            = 40 * MM_TO_PT;
	private static final float BARCODE_HEIGHT           = 20 * MM_TO_PT - (BARCODE_FONT_SIZE / 2);
	private static final float BARCODE_POS_X            = LABEL_WIDTH - LABEL_MARGIN - BARCODE_WIDTH;
	private static final float BARCODE_POS_Y            = LABEL_MARGIN + (BARCODE_FONT_SIZE / 2);
	private static final float BARCODE_LINE_WIDTH       = BARCODE_WIDTH / 97;
	private static final float BARCODE_WHITESPACE0_LEFT = 4.5f * BARCODE_LINE_WIDTH;
	private static final float BARCODE_WHITESPACE_WIDTH = 42 * BARCODE_LINE_WIDTH;
	private static final float BARCODE_WHITESPACE1_LEFT = BARCODE_WHITESPACE0_LEFT + BARCODE_WHITESPACE_WIDTH + 4 * BARCODE_LINE_WIDTH;

	private static final float ORGANIZATION_LOGO_WIDTH = 8.75f * MM_TO_PT;

	private PDDocument document = new PDDocument();

	private PDFont roboto400i, roboto500, robotoSlab700, robotoMono400;

	{
		try {
			roboto400i = PDType0Font.load(document,
					new File("src/main/resources/static/pdffonts/Roboto/Roboto-Italic.ttf"));
			roboto500  = PDType0Font.load(document,
					new File("src/main/resources/static/pdffonts/Roboto/Roboto-Medium.ttf"));
			robotoSlab700 = PDType0Font.load(document,
					new File("src/main/resources/static/pdffonts/Roboto_Slab/RobotoSlab-Bold.ttf"));
			robotoMono400 = PDType0Font.load(document,
					new File("src/main/resources/static/pdffonts/Roboto_Mono/RobotoMono-Regular.ttf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void addLabel(InventoryProduct inventoryProduct) {
		try {
			PDPage labelPage = new PDPage(new PDRectangle(LABEL_WIDTH, LABEL_HEIGHT));
			document.addPage(labelPage);
			PDPageContentStream cs = new PDPageContentStream(document, labelPage);

			cs.beginText();
				cs.setFont(this.robotoSlab700, PRODUCT_NAME_FONT_SIZE);
				cs.newLineAtOffset(PRODUCT_NAME_POS_X, PRODUCT_NAME_POS_Y);
				cs.showText(inventoryProduct.getName());
			cs.endText();

			cs.beginText();
				cs.setFont(roboto500, PRODUCT_DESCRIPTION_FONT_SIZE);
				cs.setLeading(PRODUCT_DESCRIPTION_FONT_SIZE + PRODUCT_DESCRIPTION_MARGIN_BOTTOM);
				cs.newLineAtOffset(PRODUCT_DESCRIPTION_POS_X, PRODUCT_DESCRIPTION_POS_Y);
				cs.showText("Beschreibung");

				cs.newLine();

				cs.setFont(roboto400i, PRODUCT_DESCRIPTION_FONT_SIZE);
				cs.setLeading(PRODUCT_DESCRIPTION_FONT_SIZE);
				cs.showText(inventoryProduct.getUnit() + " Metrik");
				cs.newLine();
				cs.showText("(Grundpreis)");
			cs.endText();

			cs.beginText();
				cs.setFont(robotoSlab700, 32);
				cs.newLineAtOffset(
						LABEL_WIDTH - LABEL_MARGIN - (robotoSlab700.getStringWidth(inventoryProduct.getPrice().toString()) / 1000.0f) * 32,
						PRODUCT_PRICE_POS_Y);
				cs.showText(inventoryProduct.getPrice().toString());
			cs.endText();

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.MARGIN, 0);
			BitMatrix bitMatrix = qrCodeWriter.encode(
					"http://" + InetAddress.getLocalHost().getHostName() + ":"
							+ "8080" // TODO: Dynamic Port Fill-In
							+ "/product?id=" + inventoryProduct.getId(),
					BarcodeFormat.QR_CODE, 1024, 1024, hints);
			Path path = FileSystems.getDefault().getPath(BASE_PATH + "qrc" + inventoryProduct.getId() + ".png");
			MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

			PDImageXObject qrCode = PDImageXObject.createFromFile(
					BASE_PATH + "qrc" + inventoryProduct.getId() + ".png",
					document);
			cs.drawImage(qrCode, LABEL_MARGIN, LABEL_MARGIN, QR_CODE_SIZE, QR_CODE_SIZE);

			MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
			hints.clear();
			hints.put(EncodeHintType.MARGIN, 0);
			bitMatrix = multiFormatWriter.encode(Long.toString(inventoryProduct.toEan13(inventoryProduct.getId())),
					BarcodeFormat.EAN_13, 2048, 1024, hints);
			path = FileSystems.getDefault().getPath(BASE_PATH + "brc" + inventoryProduct.getId() + ".png");
			MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

			PDImageXObject barCode = PDImageXObject.createFromFile(
					BASE_PATH + "brc" + inventoryProduct.getId() + ".png",
					document);
			cs.drawImage(barCode, BARCODE_POS_X, BARCODE_POS_Y, BARCODE_WIDTH, BARCODE_HEIGHT);

			cs.setNonStrokingColor(Color.WHITE);
			cs.addRect(BARCODE_POS_X + BARCODE_WHITESPACE0_LEFT, LABEL_MARGIN, BARCODE_WHITESPACE_WIDTH, BARCODE_FONT_SIZE);
			cs.fill();
			cs.addRect(BARCODE_POS_X + BARCODE_WHITESPACE1_LEFT, LABEL_MARGIN, BARCODE_WHITESPACE_WIDTH, BARCODE_FONT_SIZE);
			cs.fill();

			cs.setNonStrokingColor(Color.BLACK);
			cs.beginText();
			cs.setFont(robotoMono400, BARCODE_FONT_SIZE);
			cs.newLineAtOffset(BARCODE_POS_X + BARCODE_WHITESPACE0_LEFT + 0.5f * BARCODE_WHITESPACE_WIDTH
					- 0.5f * (robotoMono400.getStringWidth(Long.toString(inventoryProduct.toEan13(inventoryProduct.getId())).substring(1, 7)) / 1000.0f) * BARCODE_FONT_SIZE, LABEL_MARGIN);
			cs.showText(Long.toString(inventoryProduct.toEan13(inventoryProduct.getId())).substring(1, 7));
			cs.endText();

			cs.beginText();
			cs.setFont(robotoMono400, BARCODE_FONT_SIZE);
			cs.newLineAtOffset(BARCODE_POS_X + BARCODE_WHITESPACE1_LEFT + 0.5f * BARCODE_WHITESPACE_WIDTH
					- 0.5f * (robotoMono400.getStringWidth(Long.toString(inventoryProduct.toEan13(inventoryProduct.getId())).substring(7)) / 1000.0f) * BARCODE_FONT_SIZE, LABEL_MARGIN);
			cs.showText(Long.toString(inventoryProduct.toEan13(inventoryProduct.getId())).substring(7));
			cs.endText();

			cs.close();
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
	}

	public void generate(long id) {
		InventoryProduct inventoryProduct = inventoryProductCatalog.findById(id).orElse(null);

		try {
			if (inventoryProduct != null) {
				addLabel(inventoryProduct);
			}
			document.save(BASE_PATH + "p" + id + ".pdf");
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateAll(Iterable<InventoryProduct> inventoryProducts) {
		try {
			for (InventoryProduct inventoryProduct : inventoryProducts) {
				addLabel(inventoryProduct);
			}
			document.save(BASE_PATH + "pAll.pdf");
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

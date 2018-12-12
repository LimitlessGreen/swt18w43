package bioladen.communication;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ScanController {

	private final InventoryProductCatalog inventoryProductCatalog;

	@MessageMapping("/sendId/{sc_id}")
	@SendTo("/topic/receiveId/{sc_id}")
	public String scan(@DestinationVariable String sc_id, String message) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode codeResultNode = objectMapper.readTree(message).path("message").path("codeResult");
		if (codeResultNode.path("format").asText().equals("ean_13")) {
			long code = codeResultNode.path("code").asLong();
			long id = InventoryProduct.fromEan13(code);
			if (inventoryProductCatalog.findById(id).isPresent()) {
				return Long.toString(code);
			}
		}

		return null;
	}

	@RequestMapping("/shoppingCartScanner")
	String shoppingCartScanner() {
		return "shoppingCartScanner";
	}
}

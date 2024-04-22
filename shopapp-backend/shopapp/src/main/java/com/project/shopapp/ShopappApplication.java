package com.project.shopapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ShopappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopappApplication.class, args);
	}
	// Định nghĩa mảng ban đầu của các sản phẩm
	List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(Map.of("id", 1, "name", "Product A", "price", 24000, "comb", 3));
        rows.add(Map.of("id", 2, "name", "Product B", "price", 26000, "comb", 8));
        rows.add(Map.of("id", 3, "name", "Product C", "price", 21000, "comb", 2));
        rows.add(Map.of("id", 4, "name", "Product D", "price", 25000, "comb", 7));

	// Tạo một bản đồ để lưu trữ chi tiết sản phẩm theo ID
	Map<Integer, Map<String, Object>> productMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
		int id = (int) row.get("id");
		productMap.put(id, row);
	}

	// Tạo một danh sách mới để lưu trữ dữ liệu sản phẩm đã được cập nhật
	List<Map<String, Object>> updatedRows = new ArrayList<>();

	// Xử lý từng sản phẩm để giải quyết các tham chiếu "comb"
        for (Map<String, Object> row : rows) {
		int combId = (int) row.get("comb");

		// Kiểm tra nếu combId tồn tại trong productMap
		if (productMap.containsKey(combId)) {
			Map<String, Object> combProduct = productMap.get(combId);
			String combName = (String) combProduct.get("name");
			int combPrice = (int) combProduct.get("price");

			// Cập nhật sản phẩm hiện tại với thông tin của sản phẩm kết hợp
			Map<String, Object> updatedRow = new HashMap<>(row);
			updatedRow.put("comb_name", combName);
			updatedRow.put("price", (int) updatedRow.get("price") + combPrice);

			// Thêm sản phẩm đã cập nhật vào danh sách
			updatedRows.add(updatedRow);
		}
	}

	// Hiển thị dữ liệu sản phẩm đã cập nhật
        for (Map<String, Object> updatedRow : updatedRows) {
		System.out.println(updatedRow);
	}
}

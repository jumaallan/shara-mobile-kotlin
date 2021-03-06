package co.shara.data.retrofit

data class OrderResponse(
    var id: Int,
    var user_id: Int,
    var order_number: String,
    var created_at: String,
    var updated_at: String,
    var product: List<ProductsResponse>
)

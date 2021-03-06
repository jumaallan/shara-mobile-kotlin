package co.shara.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import co.shara.data.api.OrderAPI
import co.shara.data.dao.OrderDao
import co.shara.data.dao.ProductDao
import co.shara.data.model.Order
import co.shara.data.model.Product
import co.shara.data.retrofit.CreateOrder
import co.shara.data.retrofit.CreateOrderProduct
import co.shara.network.NetworkResult
import co.shara.network.safeApiCall
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class OrderRepository(
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val orderAPI: OrderAPI
) {

    suspend fun createOrder(createOrder: CreateOrder): NetworkResult<Order> {
        return safeApiCall(Dispatchers.IO) {
            val response = orderAPI.createOrder(createOrder)
            val order = Order(
                0,
                response!!.id,
                response.user_id,
                response.order_number,
                response.created_at,
                response.updated_at
            )
            order.copy()
        }
    }

    suspend fun createOrderProduct(createOrderProduct: CreateOrderProduct): NetworkResult<Product> {
        return safeApiCall(Dispatchers.IO) {
            val response = orderAPI.createOrderProduct(createOrderProduct)
            val product = Product(
                0,
                response!!.id,
                response.order_id,
                response.name,
                response.price,
                response.uom,
                response.created_at,
                response.updated_at
            )
            product.copy()
        }
    }

    suspend fun fetchMyOrders() {
        try {
            val response =
                orderAPI.fetchMyOrders()
            if (response.isSuccessful) {
                val result = response.body()
                orderDao.insert(
                    result?.map {
                        Order(
                            0,
                            it.id,
                            it.user_id,
                            it.order_number,
                            it.created_at,
                            it.updated_at
                        )
                    }.orEmpty()
                )
                result?.map { it ->
                    it.product.forEach {
                        productDao.insert(
                            Product(
                                0,
                                it.id,
                                it.order_id,
                                it.name,
                                it.price,
                                it.uom,
                                it.created_at,
                                it.updated_at
                            )
                        )
                    }
                }
            } else {
                Timber.e("Failed to download orders")
            }
        } catch (t: Throwable) {
            Timber.e(t)
        }
    }

    fun getOrders(): LiveData<List<Order>> {
        return orderDao.getOrders().asLiveData()
    }

    fun getOrderProducts(orderId: Int): LiveData<List<Product>> {
        return productDao.getOrderProducts(orderId).asLiveData()
    }
}
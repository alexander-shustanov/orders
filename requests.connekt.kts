import org.assertj.core.api.Assertions

val host: String by env

val productIds: List<Long> by GET("$host/products") then {
    data class Product(val id: Long)

    jsonPath().readList("$.content", Product::class.java)
        .map { it.id }
}

GET("$host/customers")

GET("$host/customers/{id}") {
    pathParam("id", 1)
}

val customerIds by GET("$host/customers") then {
    data class Customer(val id: Long)

    jsonPath().readList("$.content", Customer::class.java)
        .map { it.id }
}

val firstCity by GET("$host/cities") then {
    jsonPath().readLong("$[0].id")
}


// CREATE ORDER
POST("$host/orders") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "customerId" : ${customerIds[1]},
            "cityId": $firstCity
        }
    """.trimIndent()
    )
}

val activeOrderId by GET("$host/orders/active?customerId=${customerIds[1]}") then {
    Assertions.assertThat(code).isEqualTo(200)

    jsonPath().readLong("id")
}

DELETE("$host/orders/{orderId}") {
    pathParam("orderId", activeOrderId)
}

GET("$host/orders/{orderId}") {
    pathParam("orderId", activeOrderId)
}

POST("$host/orders/{orderId}/lines") {
    pathParam("orderId", activeOrderId)
    header("Content-Type", "application/json")
    body(
        """
        {
            "productId": ${productIds[0]},
            "amount": 6
        }
        """.trimIndent()
    )

}

POST("$host/orders/{orderId}/pay") {
    pathParam("orderId", activeOrderId)
}

POST("$host/inventory/supply") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "productId": ${productIds[0]},
            "cityId": $firstCity,
            "amount": 500
        }
        """.trimIndent()
    )
}

GET("$host/inventory/for-product/{productId}") {
    pathParam("productId", productIds[0])
}

flow("Do supply") {
    data class City(val id: Long)

    val cityIds by GET("$host/cities") then {
        jsonPath().readList("$", City::class.java).map { it.id }
    }

    for (cityId in cityIds) {
        for (productId in productIds) {
            POST("$host/inventory/supply") {
                header("Content-Type", "application/json")
                body(
                    """
        {
            "productId": $productId,
            "cityId": $cityId,
            "amount": ${Random.nextInt(100, 1000)}
        }
        """.trimIndent()
                )
            }
        }
    }
}

GET("$host/orders?customerId=${customerIds[1]}") {
}

